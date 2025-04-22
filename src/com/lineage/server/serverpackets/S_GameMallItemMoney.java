package com.lineage.server.serverpackets;

import java.io.IOException;

public class S_GameMallItemMoney extends ServerBasePacket {

	private byte[] _byte = null;

	public S_GameMallItemMoney(final long count) {
		writeC(S_OPCODE_CHARRESET);
		writeC(37);
		writeD((int) count);
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