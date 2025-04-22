package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 潘朵拉抽獎訊息
 * 
 * @author simlin
 */
public class S_LotteryMessage extends ServerBasePacket {

	private byte[] _byte = null;

	public S_LotteryMessage(final int type, final String msg1, final String msg2, final int gfxid) {
		writeC(S_OPCODE_SERVERMSG);
		writeH(type);
		writeC(2);
		writeS(msg1);
		writeD(gfxid);
		writeS(msg2);
	}

	public S_LotteryMessage(final int type, final int count) {
		writeC(S_OPCODE_SERVERMSG);
		writeH(type);
		writeD(count);
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
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
