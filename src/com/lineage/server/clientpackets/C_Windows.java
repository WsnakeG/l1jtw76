package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxLoc;
import com.lineage.server.serverpackets.S_PacketBoxPoly;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 視窗失焦
 * 
 * @author dexc
 */
public class C_Windows extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Windows.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			final int type = readC();

			switch (type) {
			case 0x00:
				// 300秒內無法重複檢舉對方
				if (pc.hasSkillEffect(L1SkillId.AICHECK_PN) && pc.getAccessLevel() < 200) {
					pc.sendPackets(new S_SystemMessage("\\aE需間隔時間後才可再申訴，請勿濫用AI驗證系統！"));
					return;
				}
				final int objid = readD();
				final L1Object obj = World.get().findObject(objid);
				if (obj instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) obj;

					// AI 懲罰金額設定
					if (Config.AICHECK) {
						if ((ConfigOther.ai_count <= 0)
								|| pc.getInventory().consumeItem(L1ItemId.ADENA, ConfigOther.ai_count)) {
							tgpc.setCheckAI(pc.getName());
							WriteLogTxt.Recording("玩家申訴清單",
									"玩家:" + pc.getName() + " 申訴:(" + objid + ")" + tgpc.getName());
							pc.sendPackets(
									new S_SystemMessage("為了防止功能濫用扣除金幣(" + ConfigOther.ai_count + ")！"));
							pc.sendPackets(new S_SystemMessage("舉報玩家" + tgpc.getName() + "成功！"));

							// 300秒內無法重複檢舉對方
							pc.setSkillEffect(L1SkillId.AICHECK_PN, 300 * 1000);

						} else {
							pc.sendPackets(new S_SystemMessage(
									"為了防止功能濫用，我們會收取一定的金幣費用(" + ConfigOther.ai_count + ")！"));
						}
					}
					/*
					 * _log.warn("玩家:" + pc.getName() + " 申訴:(" + objid + ")" +
					 * tgpc.getName());
					 */
				} else {
					_log.warn("玩家:" + pc.getName() + " 申訴:NPC(" + objid + ")");
				}
				break;

			case 0x06:
				final int itemobjid = readD();
				final int selectdoor = readC();
				final L1ItemInstance item = pc.getInventory().getItem(itemobjid);
				if (item == null) {
					return;
				}
				if (item.getItemId() != 47010) {
					return;
				}
				switch (selectdoor) {
				case 0:
					pc.getInventory().removeItem(item, 1);
					L1SpawnUtil.spawn(pc, 70932, 0, 7200);
					break;
				case 1:
					pc.getInventory().removeItem(item, 1);
					L1SpawnUtil.spawn(pc, 70937, 0, 7200);
					break;
				case 2:
					pc.getInventory().removeItem(item, 1);
					L1SpawnUtil.spawn(pc, 70934, 0, 7200);
					break;
				default:
					break;
				}
				break;

			case 0x0b:
				final String name = readS();
				final int mapid = readH();
				final int x = readH();
				final int y = readH();
				final int zone = readD();
				final L1PcInstance target = World.get().getPlayer(name);
				if (target != null) {
					target.sendPackets(new S_PacketBoxLoc(pc.getName(), mapid, x, y, zone));
					pc.sendPackets(new S_ServerMessage(1783, name));// 已發送座標位置給%0。

				} else {
					pc.sendPackets(new S_ServerMessage(1782));// 無法找到該角色或角色不在線上。
				}
				break;

			case 0x36: // 特化死亡騎士變身
				final int itemobjids = readD();
				final L1ItemInstance items = pc.getInventory().getItem(itemobjids);
				if (items == null) {
					return;
				}
				if (items.getItemId() != 156115) {
					return;
				}
				pc.sendPackets(new S_PacketBoxPoly(itemobjids));// 無法找到該角色或角色不在線上。
				break;

			case 0x2c: // ','
				// pc.sendPackets(new S_ServerMessage("\\fV暫沒實裝！"));
				pc.setKillCount(0);
				pc.sendPackets(new S_OwnCharStatus(pc));
				break;

			case 0x2e:
				// 如果不是君主或聯盟王
				if ((pc.getClanRank() != L1Clan.NORMAL_CLAN_RANK_PRINCE)
						&& (pc.getClanRank() != L1Clan.CLAN_RANK_PRINCE)) {
					return;
				}
				final int emblemStatus = readC(); // 0: 關閉 1:開啟
				final L1Clan clan = pc.getClan();
				clan.sendPacketsAll(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, emblemStatus));
				break;
			}

			// if (pc != null) {
			// // 額外
			// if (pc.get_mazu_time() != 0) {
			// if (pc.is_mazu()) {
			// final Calendar cal = Calendar.getInstance();
			// long h_time = cal.getTimeInMillis() / 1000;// 換算為秒
			// if (h_time - pc.get_mazu_time() >= 2400) {// 2400秒 =
			// // 40分鐘
			// pc.set_mazu_time(0);
			// pc.set_mazu(false);
			// }
			// }
			// }
			// }

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
