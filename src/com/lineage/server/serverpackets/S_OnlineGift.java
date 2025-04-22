package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 線上獎勵
 * 
 * @author simlin
 */
public class S_OnlineGift extends ServerBasePacket {

	private byte[] _byte = null;

	public S_OnlineGift(final int giftIndex, final int time, final int type) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(150);
		writeC(type);
		writeC(giftIndex);
		writeD(time);
	}

	public S_OnlineGift(final int giftIndex) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(151);
		writeC(giftIndex);
		writeH(5555);
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