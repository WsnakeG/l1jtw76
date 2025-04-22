package com.lineage.server.serverpackets;

import java.io.IOException;

import com.lineage.server.datatables.T_RankTable;

public class S_RankedMine extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedMine(final int level, final int clna, final int weapon, final int gold,
			final int consumption, final int kill) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(0);
		writeD(T_RankTable._basedTime);
		writeD(level);
		writeD(clna);
		writeD(weapon);
		writeD(gold);
		writeD(consumption);
		writeD(kill);
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