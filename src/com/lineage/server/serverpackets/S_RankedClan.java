package com.lineage.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.templates.T_ClanRankModel;

public class S_RankedClan extends ServerBasePacket {

	private byte[] _byte = null;

	public S_RankedClan(final ArrayList<T_ClanRankModel> models) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(166);
		writeC(2);
		writeD(T_RankTable._basedTime);
		writeD(models.size());

		for (int i = 0; i < models.size(); i++) {
			final T_ClanRankModel model = models.get(i);
			writeS(model.getClanName());
			writeS(model.getLeaderName());
			writeH(model.getMemberCount());
			writeD(model.getClanFraction());
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