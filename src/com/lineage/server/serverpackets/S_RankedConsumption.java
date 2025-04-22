package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import com.lineage.server.datatables.T_RankTable;

public class S_RankedConsumption extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedConsumption(final ArrayList<String> wealthConsumptionRanked) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(5);
		writeD(T_RankTable._basedTime);
		writeD(wealthConsumptionRanked.size());

		for (int i = 0; i < wealthConsumptionRanked.size(); i++) {
			writeS(wealthConsumptionRanked.get(i));
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