package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CharTitle;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxPledge;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 要求脫離血盟
 * 
 * @author daien
 */
public class C_LeaveClan extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_LeaveClan.class);

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			// this.read(decrypt);

			final L1PcInstance player = client.getActiveChar();

			if (player == null) { // 角色為空
				return;
			}

			final String player_name = player.getName();
			final String clan_name = player.getClanname();
			final int clan_id = player.getClanid();
			if (clan_id == 0) {// クラン未所属
				return;
			}

			final L1Clan clan = WorldClan.get().getClan(clan_name);
			if (clan != null) {
				final String clan_member_name[] = clan.getAllMembers();
				int i;
				if (player.isCrown() && (player.getId() == clan.getLeaderId())) { // 盟主
					if (!ConfigOther.CLANDEL) {
						// \f1無法解散。
						player.sendPackets(new S_ServerMessage(302));
						player.sendPackets(new S_NPCTalkReturn(player.getId(), "y_clanD"));
						return;
					}

					final int castleId = clan.getCastleId();
					final int houseId = clan.getHouseId();

					if (castleId != 0) {
						// \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
						player.sendPackets(new S_ServerMessage(665));
						return;
					}

					if (houseId != 0) {
						// \f1擁有城堡與血盟小屋的狀態下無法解散血盟。
						player.sendPackets(new S_ServerMessage(665));
						return;
					}

					for (final L1War war : WorldWar.get().getWarList()) {
						// 戰爭中
						if (war.checkClanInWar(clan_name)) {
							// \f1無法解散。
							player.sendPackets(new S_ServerMessage(302));
							return;
						}
					}

					// 同盟系統 by terry0412
					if (ClanAllianceReading.get().getAlliance(player.getClanid()) != null) {
						// 同盟時無法解散血盟。
						player.sendPackets(new S_ServerMessage(3109));
						return;
					}

					for (i = 0; i < clan_member_name.length; i++) { // クラン員のクラン情報をクリア
						final L1PcInstance online_pc = World.get().getPlayer(clan_member_name[i]);
						if (online_pc != null) { // 線上成員
							online_pc.setClanid(0);
							online_pc.setClanname("");
							online_pc.setClanRank(0);

							online_pc.setTitle("");
							online_pc.sendPacketsAll(new S_CharTitle(online_pc.getId()));
							// online_pc.setTitle("");
							// online_pc.sendPacketsAll(new
							// S_CharTitle(online_pc.getId(), ""));
							online_pc.save(); // 資料存檔
							// %1血盟的盟主%0%s解散了血盟。
							online_pc.sendPackets(new S_ServerMessage(269, player_name, clan_name));
							if (online_pc.isCrown()) {
								online_pc.sendPackets(new S_PacketBoxPledge(6, null, player_name, 0));
							} else {
								online_pc.sendPackets(new S_ClanUpdate(online_pc.getId()));

							}
							L1Teleport.teleport(online_pc, online_pc.getX(), online_pc.getY(),
									online_pc.getMapId(), online_pc.getHeading(), false);
						} else { // 離線成員
							try {
								final L1PcInstance offline_pc = CharacterTable.get()
										.restoreCharacter(clan_member_name[i]);
								offline_pc.setClanid(0);
								offline_pc.setClanname("");
								offline_pc.setClanRank(0);
								offline_pc.setTitle("");
								offline_pc.save(); // 資料存檔

							} catch (final Exception e) {
								_log.error(e.getLocalizedMessage(), e);
							}
						}
					}
					// 資料刪除
					ClanEmblemReading.get().deleteIcon(clan_id);
					/*
					 * final String emblem_file = String.valueOf(clan_id); final
					 * File file = new File("emblem/" + emblem_file);
					 * file.delete();
					 */
					ClanReading.get().deleteClan(clan_name);

				} else { // 血盟主以外
					final L1PcInstance clanMember[] = clan.getOnlineClanMember();
					for (final L1PcInstance clanmember : clanMember) {
						if (clanmember.isCrown()) {
							clanmember.sendPackets(new S_PacketBoxPledge(4, null, player_name, 0));
						} else {
							clanmember.sendPackets(new S_ClanUpdate(player.getId()));
						}
						clanmember.sendPackets(new S_ServerMessage(1898, player_name));
					}
					if (clan.getWarehouseUsingChar() == player.getId()) {
						clan.setWarehouseUsingChar(0); // 解除血盟倉庫目前使用者
					}
					player.setClanid(0);
					player.setClanname("");
					player.setClanRank(0);

					player.setTitle("");
					player.sendPacketsAll(new S_CharTitle(player.getId()));
					player.save(); // 資料存檔
					clan.delMemberName(player_name);
				}

			} else {
				player.setClanid(0);
				player.setClanname("");
				player.setClanRank(0);

				player.setTitle("");
				player.sendPacketsAll(new S_CharTitle(player.getId()));
				player.save(); // 資料庫更新
				// \f1%0%s退出 %1 血盟了。
				player.sendPackets(new S_ServerMessage(178, player_name, clan_name));
				player.sendPackets(new S_ClanUpdate(player.getId()));
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
