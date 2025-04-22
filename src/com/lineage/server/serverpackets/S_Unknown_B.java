package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 未知 B 人物列表之前
 * 
 * @author dexc
 */
public class S_Unknown_B extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 未知 B 人物列表之前
	 * 
	 * @param i
	 */
	public S_Unknown_B() {
		/*
		 * Server op: 43 0000: 2b 0a 02 00 00 00 2b 7f +.....+
		 */
		writeC(S_OPCODE_CHARRESET);

		writeC(0x0a);
		writeC(0x02);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x2b);
		writeC(0x7f);
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
