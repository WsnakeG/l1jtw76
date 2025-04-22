package com.lineage.server.clientpackets;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharApprenticeTable;
import com.lineage.server.datatables.T_OnlineGiftTable;
import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_RankedClan;
import com.lineage.server.serverpackets.S_RankedConsumption;
import com.lineage.server.serverpackets.S_RankedKill;
import com.lineage.server.serverpackets.S_RankedLevel;
import com.lineage.server.serverpackets.S_RankedMine;
import com.lineage.server.serverpackets.S_RankedWealth;
import com.lineage.server.serverpackets.S_RankedWeapon;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 自動登入
 * 
 * @author dexc
 */
public class C_AutoLogin extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AutoLogin.class);

	// 自動登錄伺服器 (省略輸入帳密的步驟)
	private static final int AUTO_LOGIN = 0x00;

	private static final int MENTOR_SYSTEM = 0x0d;

	/*
	 * public C_5M() { } public C_5M(final byte[] abyte0, final ClientExecutor
	 * client) { super(abyte0); try { this.start(abyte0, client); } catch (final
	 * Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final int mode = readC();

			// 請求使用樂豆自動登錄伺服器 (尚未實裝)
			if (mode == AUTO_LOGIN) {
				// 讀取帳號
				final String loginName = readS().toLowerCase();
				// 讀取密碼
				final String password = readS();
				final C_AuthLogin authLogin = new C_AuthLogin();
				authLogin.checkLogin(client, loginName, password, true);
				return;
			}
			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			if (mode == MENTOR_SYSTEM) {
				if (!ConfigAlt.APPRENTICE_SWITCH) {
					pc.sendPackets(new S_ServerMessage("目前不開放師徒系統。"));
					return;
				}
				// 區分類型
				final int type = readC();
				if (type == 0) {
					final String name = readS();
					final L1PcInstance target = World.get().getPlayer(name);
					if (target != null) {
						final long timeMillis = System.currentTimeMillis();
						if (((pc.getPunishTime() != null) && (pc.getPunishTime().getTime() >= timeMillis))
								|| ((target.getPunishTime() != null)
										&& (target.getPunishTime().getTime() >= timeMillis))) {
							pc.sendPackets(new S_ServerMessage(2988));
							return;
						}
						final L1Apprentice apprentice = pc.getApprentice();
						if (apprentice != null) {
							if (pc.getId() == apprentice.getMaster().getId()) {
								if (pc.getLevel() < ConfigAlt.APPRENTICE_LEVEL) {
									pc.sendPackets(new S_ServerMessage(2973));
								} else if (target.getLevel() >= ConfigAlt.APPRENTICE_LEVEL) {
									pc.sendPackets(new S_ServerMessage(2976));
								} else if (!pc.getApprentice().checkSize()) {
									pc.sendPackets(new S_ServerMessage(2971));
								} else if (target.getApprentice() != null) {
									pc.sendPackets(new S_ServerMessage(2970));
								} else {
									// 要接受 %0 為弟子嗎？
									pc.setTempID(target.getId());
									pc.sendPackets(new S_Message_YN(2968, target.getName()));
								}
							} else if ((target.getApprentice() != null)
									&& (target.getApprentice().getMaster() == apprentice.getMaster())) {
								pc.sendPackets(new S_ServerMessage(2966));
							} else {
								pc.sendPackets(new S_ServerMessage(2969));
							}
							return;
						}
						if (target.getLevel() < ConfigAlt.APPRENTICE_LEVEL) {
							pc.sendPackets(new S_ServerMessage(2974));
						} else if (pc.getLevel() >= ConfigAlt.APPRENTICE_LEVEL) {
							pc.sendPackets(new S_ServerMessage(2975));
						} else if ((target.getApprentice() != null) && !target.getApprentice().checkSize()) {
							pc.sendPackets(new S_ServerMessage(2972));
						} else {
							// 要將 %0 奉為師父嗎？
							pc.setTempID(target.getId());
							pc.sendPackets(new S_Message_YN(2967, target.getName()));
						}
					} else {
						pc.sendPackets(new S_ServerMessage(1782));
					}
				} else if (type == 1) {
					final String name = readS();
					// _log.info("解除師徒關係 = " + name);

					// 取得指定師徒資料
					final L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc);
					if (apprentice != null) {
						// 尋找指定角色名稱 (使用getViewName()避免搜尋不到target)
						L1PcInstance target = null;
						if (apprentice.getMaster().getViewName().equalsIgnoreCase(name)) {
							target = apprentice.getMaster();

						} else {
							for (final L1PcInstance l1char : apprentice.getTotalList()) {
								if (l1char.getViewName().equalsIgnoreCase(name)) {
									target = l1char;
									break;
								}
							}
						}

						if (target == null) {
							// 無法找到該角色或角色不在線上。
							pc.sendPackets(new S_ServerMessage(1782));
							return;
						}

						// 該對象為師父
						if ((pc.getId() == apprentice.getMaster().getId() // 自身是師父
						) && ((pc.getId() == target.getId())
								// 或是弟子數量只有一個 (解散師徒關係)
								|| ((apprentice.getTotalList().size() <= 1)
										&& apprentice.isApprentice(target.getId())))) {
							// 移除全部徒弟資料
							for (final L1PcInstance l1char : apprentice.getTotalList()) {
								// 對方目前正在線上
								final L1PcInstance find_pc = World.get().getPlayer(l1char.getName());
								if (find_pc != null) {
									// 更新師徒狀態
									find_pc.setApprentice(null);

									// 師徒關係已刪除。
									find_pc.sendPackets(new S_ServerMessage(2977));
								}
							}

							// 刪除締結資料
							CharApprenticeTable.getInstance().deleteApprentice(pc.getId());

							// 清除師父和徒弟列表
							apprentice.clear();

							// 更新師徒狀態
							pc.setApprentice(null);

							// 師父自行解除師徒關係時，於7日內無法再收其他徒弟。
							pc.setPunishTime(new Timestamp(System.currentTimeMillis() + 604800000));

							// 師徒關係已刪除。
							pc.sendPackets(new S_ServerMessage(2977));

							try {
								pc.save();
							} catch (final Exception e) {
							}
						}
						// 該對象為徒弟
						else {
							// 移除徒弟資料
							for (final L1PcInstance l1char : apprentice.getTotalList()) {
								if (l1char.getId() == target.getId()) {
									apprentice.getTotalList().remove(l1char);
									break;
								}
							}

							// 更新締結資料
							CharApprenticeTable.getInstance().updateApprentice(apprentice.getMaster().getId(),
									apprentice.getTotalList());

							// 對方目前正在線上
							if (World.get().findObject(target.getId()) != null) {
								// 更新師徒狀態
								target.setApprentice(null);

								// 師徒關係已刪除。
								target.sendPackets(new S_ServerMessage(2977));
							}

							// 該對象是自身 且 自身不是師父
							if ((target.getId() == pc.getId())
									&& (pc.getId() != apprentice.getMaster().getId())) {
								// 徒弟自行解除師徒關係時，於1日內無法再拜其他師父。
								pc.setPunishTime(new Timestamp(System.currentTimeMillis() + 86400000));

								try {
									pc.save();
								} catch (final Exception e) {
								}
							}
						}

					} else {
						// 師徒關係刪除失敗。
						pc.sendPackets(new S_ServerMessage(2978));
					}

				} else if (type == 2) {
					final L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc);
					if (apprentice != null) {
						pc.sendPackets(new S_PacketBox(apprentice.getMaster(), apprentice.getTotalList()));
					} else {
						pc.sendPackets(new S_ServerMessage(2979));
					}
				}

			} else if (mode == 14) {
				T_OnlineGiftTable.get().receive(pc);

			} else if (mode == 15) {
				final int type3 = readC();
				final T_RankTable rankTable = T_RankTable.get();
				if (type3 == 0) {
					final String name = pc.getName();
					pc.sendPackets(new S_RankedMine(rankTable.getLevelName().indexOf(name) + 1,
							rankTable.getClanName().indexOf(pc.getClanname()) + 1,
							rankTable.getWeaponName().indexOf(name) + 1,
							rankTable.getWealthName().indexOf(name) + 1,
							rankTable.getConsumeName().indexOf(name) + 1,
							rankTable.getKillName().indexOf(name) + 1));
				} else if (type3 == 1) {
					pc.sendPackets(new S_RankedLevel(rankTable.getLevelList()));
				} else if (type3 == 2) {
					pc.sendPackets(new S_RankedClan(rankTable.getClanNameList()));
				} else if (type3 == 3) {
					pc.sendPackets(new S_RankedWeapon(rankTable.getWeaponNameList()));
				} else if (type3 == 4) {
					pc.sendPackets(new S_RankedWealth(rankTable.getWealthName()));
					pc.sendPackets(new S_RankedConsumption(rankTable.getConsumeName()));
				} else if (type3 == 6) {
					pc.sendPackets(new S_RankedKill(rankTable.getKillNameList()));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
