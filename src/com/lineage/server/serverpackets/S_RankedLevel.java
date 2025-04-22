package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.T_LevelRankModel;
import com.lineage.server.world.World;

public class S_RankedLevel extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedLevel(final ArrayList<T_LevelRankModel> models) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(1);
		writeD(T_RankTable._basedTime);
		writeD(models.size());
		L1PcInstance pc = null;

		for (int i = 0; i < models.size(); i++) {
			final T_LevelRankModel model = models.get(i);
			final String name = model.getName();
			long exp = model.getExp();
			final int turnLife = model.getTurnLife();
			int level = model.getLevel();
			pc = World.get().getPlayer(name);
			if (pc == null) {
				try {
					pc = CharacterTable.get().restoreCharacter(name);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
			if (pc != null) {
				exp = pc.getExp();
				level = pc.getLevel();
				pc = null;
			}
			writeS(name);
			writeC(level > 99 ? 99 : level);
			writeC(ExpTable.getExpPercentage(level, exp));
			writeC(model.getType());
			if ((exp > model.getExp()) || (turnLife > model.getTurnLife())) {
				writeC(0);
			} else if (exp < model.getExp()) {
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