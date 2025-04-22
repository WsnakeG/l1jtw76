package com.lineage.server.clientpackets;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigOther;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.ClanRecommendReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxPledgeRecommend;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ClanRecommend;
import com.lineage.server.world.World;

/**
 * @author ibm99nets
 */
public class C_PledgeRecommendation extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_PledgeRecommendation.class);

	/*
	 * public C_PledgeRecommendation() { } public C_PledgeRecommendation(final
	 * byte[] abyte0, final ClientExecutor client) { super(abyte0); try {
	 * this.start(abyte0, client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();
			if (pc == null) {
				return;
			}

			final int data = readC();

			if (data == 0) {
				final L1Clan clan = getClan(pc.getClanid());
				if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
					return;
				}

				final int clanType = readC();
				final String typeMessage = readS();

				if (ClanRecommendReading.get().getRecommendsList().containsKey(clan.getClanId())) {
					ClanRecommendReading.get().updateRecommend(clan.getClanId(), clanType, typeMessage);

				} else {
					ClanRecommendReading.get().insertRecommend(clan.getClanId(), clan.getClanName(),
							clan.getLeaderName(), clanType, typeMessage);
				}

				pc.sendPackets(new S_PacketBoxPledgeRecommend(true, clan.getClanId()));

			} else if (data == 1) {
				final L1Clan clan = getClan(pc.getClanid());
				if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
					return;
				}

				ClanRecommendReading.get().deleteRecommend(pc.getClanid());

				pc.sendPackets(new S_PacketBoxPledgeRecommend(false, clan.getClanId()));

			} else if (data == 2) {
				final Map<Integer, L1ClanRecommend> allRecommend = ClanRecommendReading.get()
						.getRecommendsList();

				final Map<Integer, CopyOnWriteArrayList<Integer>> allApply = ClanRecommendReading.get()
						.getApplyList();

				final ArrayList<L1ClanRecommend> allClanRecommend = new ArrayList<L1ClanRecommend>();

				final int pc_objid = pc.getId();

				for (final L1ClanRecommend recommend : allRecommend.values()) {
					final L1Clan clan = ClanReading.get().getTemplate(recommend.getClanId());
					if ((clan != null) && ((allApply.get(recommend.getClanId()) == null)
							|| !allApply.get(recommend.getClanId()).contains(pc_objid))) {
						allClanRecommend.add(recommend);
					}
				}

				pc.sendPackets(new S_PacketBoxPledgeRecommend(allClanRecommend, allApply));

			} else if (data == 3) {
				final Map<Integer, L1ClanRecommend> allRecommend = ClanRecommendReading.get()
						.getRecommendsList();

				final Map<Integer, CopyOnWriteArrayList<Integer>> allApply = ClanRecommendReading.get()
						.getApplyList();

				final ArrayList<Integer> allClanApply = new ArrayList<Integer>();

				final int pc_objid = pc.getId();

				for (final Entry<Integer, CopyOnWriteArrayList<Integer>> list : allApply.entrySet()) {
					if (list.getValue().contains(pc_objid)) {
						allClanApply.add(list.getKey());
					}
				}

				pc.sendPackets(new S_PacketBoxPledgeRecommend(allRecommend, allClanApply));

			} else if (data == 4) {
				final L1Clan clan = getClan(pc.getClanid());
				if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
					return;
				}

				final L1ClanRecommend recommend = ClanRecommendReading.get().getRecommendsList()
						.get(clan.getClanId());

				final CopyOnWriteArrayList<Integer> apply = ClanRecommendReading.get().getApplyList()
						.get(clan.getClanId());

				pc.sendPackets(new S_PacketBoxPledgeRecommend(recommend, apply));

			} else if (data == 5) {
				if (pc.getClanid() != 0) {
					return;
				}
				final int clan_id = readD();

				final L1Clan clan = getClan(clan_id);
				if (clan == null) {
					return;
				}

				ClanRecommendReading.get().insertRecommendApply(clan_id, clan.getClanName(), pc.getId(),
						pc.getName());

				pc.sendPackets(new S_PacketBoxPledgeRecommend(data, clan_id, 0, 0));

			} else if (data == 6) {
				final L1Clan clan = getClan(pc.getClanid());
				if (clan == null) {
					return;
				}

				final int objid = readD();
				final int type = readC();

				if (type == 1) {
					final String name = CharObjidTable.get().isChar(objid);
					if (name == null) {
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
						return;
					}

					final L1PcInstance joinPc = World.get().getPlayer(name);
					if (joinPc == null) {
						pc.sendPackets(new S_ServerMessage(73, name));
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
						return;
					}

					if (joinPc.getClanid() != 0) {
						pc.sendPackets(new S_ServerMessage(2739));

						ClanRecommendReading.get().deleteRecommendApply(clan.getClanId(), objid);
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
						return;
					}

					boolean isAllianceClan = false;

					int maxMember = 0;

					if (ConfigOther.CLANCOUNT != 0) {
						maxMember = ConfigOther.CLANCOUNT;

					} else {
						L1PcInstance clanLeader = World.get().getPlayer(clan.getLeaderName());
						if (clanLeader == null) {
							try {
								clanLeader = CharacterTable.get().restoreCharacter(clan.getLeaderName());
							} catch (final Exception e) {
								_log.error(e.getLocalizedMessage(), e);
							}

							if (clanLeader == null) {
								pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
								return;
							}

							clanLeader.getQuest().load();
						}

						final int charisma = clanLeader.getCha();

						if (clanLeader.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
							isAllianceClan = true;
						}

						if (pc.getLevel() >= 50) { // Lv50以上
							if (isAllianceClan == true) { // Lv45クエストクリア済み
								maxMember = charisma * 9;
							} else {
								maxMember = charisma * 3;
							}

						} else { // Lv50未満
							if (isAllianceClan == true) { // Lv45クエストクリア済み
								maxMember = charisma * 6;
							} else {
								maxMember = charisma * 2;
							}
						}
					}
					final String clanMembersName[] = clan.getAllMembers();
					if (maxMember <= clanMembersName.length) {
						joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
						return;
					}

					for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
						clanMembers.sendPackets(new S_ServerMessage(94, joinPc.getName()));
					}

					joinPc.setClanid(clan.getClanId());
					joinPc.setClanname(clan.getClanName());
					if (isAllianceClan) {
						joinPc.setClanRank(L1Clan.ALLIANCE_CLAN_RANK_ATTEND);

						joinPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, joinPc.getClanRank(),
								joinPc.getName()));

					} else {
						joinPc.setClanRank(L1Clan.NORMAL_CLAN_RANK_ATTEND);

						joinPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, joinPc.getClanRank(),
								joinPc.getName()));
					}
					joinPc.save();

					joinPc.sendPackets(
							new S_ClanUpdate(joinPc.getId(), joinPc.getClanname(), joinPc.getClanRank()));
					clan.addMemberName(joinPc.getName());

					for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
						clanMembers.sendPackets(
								new S_ClanUpdate(joinPc.getId(), joinPc.getClanname(), joinPc.getClanRank()));
					}

					joinPc.sendPackets(new S_ServerMessage(95, clan.getClanName()));

					ClanRecommendReading.get().deleteRecommendApply(0, objid);

					pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 0));

				} else if ((type == 2) || (type == 3)) {
					final CopyOnWriteArrayList<Integer> apply = ClanRecommendReading.get().getApplyList()
							.get(clan.getClanId());

					if ((apply != null) && apply.contains(objid)) {
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 0));

					} else {
						pc.sendPackets(new S_PacketBoxPledgeRecommend(data, objid, type, 131));
					}

					ClanRecommendReading.get().deleteRecommendApply(clan.getClanId(), objid);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private final L1Clan getClan(final int clan_id) {
		if (clan_id == 0) {
			return null;
		}

		return ClanReading.get().getTemplate(clan_id);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
