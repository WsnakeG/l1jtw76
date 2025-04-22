package com.lineage.server.clientpackets;

import java.util.Calendar;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxMapTimer;
import com.lineage.server.serverpackets.S_PacketBoxPledge;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.utils.FaceToFace;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 要求給予角色血盟階級
 * 
 * @author daien
 */
public class C_Rank extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Rank.class);

	/*
	 * public C_Rank() { } public C_Rank(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);
			int data = 0;
			int rank = 0;
			try {
				data = readC();
				rank = readC();

			} catch (final Exception e) {
				return;
			}

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}
			final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
			switch (data) {
			
			case 0: // 查询血盟成員清單
				if (clan == null) {
					return;
				}
				final S_PacketBoxPledge Pledge2 = new S_PacketBoxPledge(2, pc, null, 0);
				pc.sendPackets(Pledge2);
				break;
				
			case 1:// 階級
				String name = readS();
				rank(pc, rank, name);
				break;

			case 2:// 觀看同盟目錄(XXX 7.6取消)
				/*final L1Clan clan1 = pc.getClan();
				if (clan1 == null) {
					pc.sendPackets(new S_ServerMessage(1064));
					return;
				}

				final L1Alliance alliance1 = ClanAllianceReading.get().getAlliance(clan1.getClanId());
				if (alliance1 != null) {
					final StringBuffer sbr = new StringBuffer();
					for (final L1Clan l1clan : alliance1.getTotalList()) {
						if (clan1.getClanId() != l1clan.getClanId()) {
							sbr.append(l1clan.getClanName()).append(" ");
						}
					}
					pc.sendPackets(new S_PacketBox(S_PacketBox.ALLIANCE_LIST, sbr.toString()));

				} else {
					pc.sendPackets(new S_ServerMessage(1233));
				}
				break;*/

			case 3:
				/*final L1Clan clan2 = pc.getClan();
				if ((clan2 == null) || (pc.getId() != clan2.getLeaderId())) {
					pc.sendPackets(new S_ServerMessage(518));
					return;
				}

				if ((pc.getLevel() < 25) || !pc.isCrown()) {
					pc.sendPackets(new S_ServerMessage(1206));
					return;
				}

				if (ClanAllianceReading.get().getAlliance(clan2.getClanId()) != null) {
					pc.sendPackets(new S_ServerMessage(1202));
					return;
				}

				for (final L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(clan2.getClanName())) {
						pc.sendPackets(new S_ServerMessage(1234));
						return;
					}
				}

				final L1PcInstance alliancePc = FaceToFace.faceToFace(pc);
				if (alliancePc != null) {
					if (!alliancePc.isCrown()) {
						pc.sendPackets(new S_ServerMessage(92, alliancePc.getName()));
						return;
					}
					if ((alliancePc.getClanid() == 0) || (alliancePc.getClan() == null)) {
						pc.sendPackets(new S_ServerMessage(90, alliancePc.getName()));
						return;
					}

					alliancePc.setTempID(pc.getId());
					alliancePc.sendPackets(new S_Message_YN(223, pc.getName()));
				}*/
				break;

			case 4:
				final L1Clan clan3 = pc.getClan();
				if ((clan3 == null) || (pc.getId() != clan3.getLeaderId())) {
					pc.sendPackets(new S_ServerMessage(518));
					return;
				}

				for (final L1War war : WorldWar.get().getWarList()) {
					if (war.checkClanInWar(clan3.getClanName())) {
						pc.sendPackets(new S_ServerMessage(1203));
						return;
					}
				}

				final L1Alliance alliance3 = ClanAllianceReading.get().getAlliance(clan3.getClanId());
				if (alliance3 != null) {
					pc.sendPackets(new S_Message_YN(1210));

				} else {
					pc.sendPackets(new S_ServerMessage(1233));
				}
				break;

			case 5:// 生存吶喊(CTRL+E)
				if (pc.get_food() >= 225) {
					if (pc.getWeapon() != null) {
						final Random random = new Random();
						final long time = pc.get_h_time();
						final Calendar cal = Calendar.getInstance();
						final long h_time = cal.getTimeInMillis() / 1000;// 換算為秒
						final int n = (int) ((h_time - time) / 60);// 換算為分

						int addhp = 0;

						if (n <= 0) {
							// 1974：還無法使用生存的吶喊。
							pc.sendPackets(new S_ServerMessage(1974));

						} else if ((n >= 1) && (n <= 29)) {
							addhp = (int) (pc.getMaxHp() * (n / 100.0D));

						} else if (n >= 30) {
							final int lv = pc.getWeapon().getEnchantLevel();
							switch (lv) {
							case 0:
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8907));
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8684));
								addhp = (int) (pc.getMaxHp() * ((random.nextInt(20) + 20) / 100.0D));
								break;

							case 7:
							case 8:
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8909));
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8685));
								addhp = (int) (pc.getMaxHp() * ((random.nextInt(20) + 30) / 100.0D));
								break;

							case 9:
							case 10:
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8910));
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8773));
								addhp = (int) (pc.getMaxHp() * ((random.nextInt(10) + 50) / 100.0D));
								break;

							case 11:
							default:
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8908));
								pc.sendPacketsX8(new S_SkillSound(pc.getId(), 8686));
								addhp = (int) (pc.getMaxHp() * (0.7));
								break;
							}
						}

						if (addhp != 0) {
							pc.set_food((short) 0);
							pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, (short) 0));
							pc.setCurrentHp(pc.getCurrentHp() + addhp);
						}

					} else {
						// 1973：必須裝備上武器才可使用。
						pc.sendPackets(new S_ServerMessage(1973));
					}
				}
				break;

			case 6:// 生存吶喊(Alt+0)
				if (pc.getWeapon() != null) {
					final int lv = pc.getWeapon().getEnchantLevel();
					int gfx = 8684;
					switch (lv) {
					case 0:
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
						gfx = 8684;
						break;

					case 7:
					case 8:
						gfx = 8685;
						break;

					case 9:
					case 10:
						gfx = 8773;
						break;

					case 11:
					default:
						gfx = 8686;
						break;
					}

					pc.sendPacketsX8(new S_SkillSound(pc.getId(), gfx));

				} else {
					// 1973：必須裝備上武器才可使用。
					pc.sendPackets(new S_ServerMessage(1973));
				}
				break;

			case 8:
				final Collection<L1MapsLimitTime> mapLimitList = MapsGroupTable.get().getGroupMaps().values();
				for (final L1MapsLimitTime mapLimit : mapLimitList) {
					final int used_time = pc.getMapsTime(mapLimit.getOrderId());
					final int time_str = (mapLimit.getLimitTime() - used_time) / 60;
					pc.sendPackets(
							new S_ServerMessage(2535, mapLimit.getMapName(), String.valueOf(time_str)));
				}
				break;

			case 9:
				pc.sendPackets(new S_PacketBoxMapTimer(pc));
				break;
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private void rank(final L1PcInstance pc, final int rank, final String name) {
		final L1PcInstance targetPc = World.get().getPlayer(name);
		final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
		if (clan == null) {
			return;
		}
		boolean isOK = false;
		// rank 2:一般 3:副君主 4:聯盟君主 5:修習騎士 6:守護騎士 7:一般 8:修習騎士 9:守護騎士 10:聯盟君主
		// 12精銳騎士 13 精銳騎士
		if ((rank >= 2) && (rank <= 13)) {
			isOK = true;
		}

		if (!isOK) {
			// \f1請輸入以下內容: "/階級 \f0角色名稱 階級[守護騎士, 修習騎士, 一般]\f1"
			pc.sendPackets(new S_ServerMessage(2149));
			return;
		}

		if (pc.isCrown()) { // 君主
			if (pc.getId() != clan.getLeaderId()) { // 血盟主
				// 785 你不再是君主了
				pc.sendPackets(new S_ServerMessage(785));
				return;
			}

		} else {
			// 518 血盟君主才可使用此命令。
			pc.sendPackets(new S_ServerMessage(518));
			return;
		}

		if (targetPc != null) {
			try {
				if (pc.getId() == targetPc.getId()) {
					return;
				}

				if (pc.getClanid() == targetPc.getClanid()) {
					targetPc.setClanRank(rank);
					targetPc.save();

					// 你的階級變更為%s
					targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, targetPc.getClanRank(),
							targetPc.getName()));

				} else {
					// 201：\f1%0%d不是你的血盟成員。
					pc.sendPackets(new S_ServerMessage(201, name));
					return;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}

		} else { // 線上無此人物
			try {
				final L1PcInstance restorePc = CharacterTable.get().restoreCharacter(name);
				if (pc.getId() == restorePc.getId()) {
					return;
				}

				if ((restorePc != null) && (restorePc.getClanid() == pc.getClanid())) { // 相同血盟
					restorePc.setClanRank(rank);
					restorePc.save();

				} else {
					// 109 沒有叫%0的人。
					pc.sendPackets(new S_ServerMessage(109, name));
					return;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
