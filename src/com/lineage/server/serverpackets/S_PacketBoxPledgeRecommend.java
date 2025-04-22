package com.lineage.server.serverpackets;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ClanRecommend;
import com.lineage.server.world.World;

public class S_PacketBoxPledgeRecommend extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxPledgeRecommend(final boolean postStatus, final int clan_id) {
		writeC(S_OPCODE_MATCH_MAKING); // XXX S_OPCODE_CLAN_RECOMMEND 修改為 S_OPCODE_MATCH_MAKING
		writeC(postStatus ? 0x00 : 0x01); // type
		writeC(0x00);
	}

	public S_PacketBoxPledgeRecommend(final List<L1ClanRecommend> allClanRecommend,
			final Map<Integer, CopyOnWriteArrayList<Integer>> allApply) {
		writeC(S_OPCODE_MATCH_MAKING); // XXX S_OPCODE_CLAN_RECOMMEND 修改為 S_OPCODE_MATCH_MAKING
		writeC(0x02); // type
		writeC(0x00); // ???

		writeC(allClanRecommend.size());

		for (final L1ClanRecommend recommend : allClanRecommend) {
			final L1Clan clan = ClanReading.get().getTemplate(recommend.getClanId());

			writeD(clan.getClanId());
			writeS(clan.getClanName());
			writeS(clan.getLeaderName());
			writeD(clan.getLoginLevel());
			writeC(recommend.getTypeId());
			writeC(clan.getHouseId() > 0 ? 1 : 0);
			writeC(clan.getCastleId() > 0 ? 1 : 0);
			writeC(0);
			writeS(recommend.getTypeMessage());
			writeD(clan.getEmblemId());
		}
	}

	public S_PacketBoxPledgeRecommend(final Map<Integer, L1ClanRecommend> allRecommend,
			final List<Integer> allClanApply) {
		writeC(S_OPCODE_MATCH_MAKING); // XXX S_OPCODE_CLAN_RECOMMEND 修改為 S_OPCODE_MATCH_MAKING
		writeC(0x03); // type
		writeC(0x00); // ???

		writeC(allClanApply.size());

		for (final int clan_id : allClanApply) {
			final L1ClanRecommend recommend = allRecommend.get(clan_id);

			final L1Clan clan = ClanReading.get().getTemplate(clan_id);

			writeD(clan_id);
			writeC(0x00);
			writeD(clan_id);
			writeS(clan.getClanName());
			writeS(clan.getLeaderName());
			writeD(clan.getLoginLevel());
			writeC(recommend.getTypeId());
			writeC(clan.getHouseId() > 0 ? 1 : 0);
			writeC(clan.getCastleId() > 0 ? 1 : 0);
			writeC(0);
			writeS(recommend.getTypeMessage());
			writeD(clan.getEmblemId());
		}
	}

	public S_PacketBoxPledgeRecommend(final L1ClanRecommend recommend, final List<Integer> apply) {
		writeC(S_OPCODE_MATCH_MAKING); // XXX S_OPCODE_CLAN_RECOMMEND 修改為 S_OPCODE_MATCH_MAKING
		writeC(0x04); // type

		if (recommend == null) {
			writeC(0x82);

		} else {
			writeC(0x00);
			writeC(recommend.getTypeId());
			writeS(recommend.getTypeMessage());

			if (apply == null) {
				writeC(0x00);
				return;
			}

			writeC(apply.size());

			for (final int objid : apply) {
				writeD(objid);
				writeC(0x00); // ???

				L1PcInstance pc = null;
				final String name = CharObjidTable.get().isChar(objid);
				if (name != null) {
					pc = World.get().getPlayer(name);
					if (pc == null) {
						try {
							pc = CharacterTable.get().restoreCharacter(name);
						} catch (final Exception e) {
							e.printStackTrace();
						}
						writeC(0);

					} else {
						writeC(1);
					}

				} else {
					writeC(0);
				}

				writeS(name);
				writeC(pc == null ? 0 : pc.getType());
				writeH(pc == null ? 0 : pc.getLawful());
				writeH(pc == null ? 0 : pc.getLevel());
			}
		}
	}

	public S_PacketBoxPledgeRecommend(final int type, final int record_id, final int acceptType,
			final int actionType) {
		writeC(S_OPCODE_MATCH_MAKING); // XXX S_OPCODE_CLAN_RECOMMEND 修改為 S_OPCODE_MATCH_MAKING
		writeC(type); // type
		writeC(actionType);
		writeD(record_id);
		writeC(acceptType);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}
