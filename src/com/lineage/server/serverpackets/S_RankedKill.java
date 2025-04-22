package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.T_KillPlayCountRankModel;
import com.lineage.server.world.World;

public class S_RankedKill extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedKill(final ArrayList<T_KillPlayCountRankModel> killRanked) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(6);
		writeD(T_RankTable._basedTime);
		writeD(killRanked.size());
		L1PcInstance pc = null;
		T_KillPlayCountRankModel model = null;
		for (int i = 0; i < killRanked.size(); i++) {
			model = killRanked.get(i);
			final String name = model.getName();
			int killCount = model.getCount();
			writeS(name);
			writeC(model.getType());
			writeD(killCount);

			pc = World.get().getPlayer(name);
			if (pc == null) {
				try {
					pc = CharacterTable.get().restoreCharacter(name);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if (pc != null) {
				killCount = pc.get_PKcount();
				pc = null;
			}
			if (killCount > model.getCount()) {
				writeC(0);
			} else if (killCount < model.getCount()) {
				writeC(1);
			} else {
				writeC(2);
			}
		}
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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