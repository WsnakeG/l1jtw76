package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * VIP時間顯示
 * 
 * @author simlin
 */
public class S_VipTime extends ServerBasePacket {

	private byte[] _byte = null;

	public S_VipTime(final int vipLevel, final long startTime, final long endTime) {
		writeC(S_OPCODE_CHARRESET);
		writeC(72);
		writeD(vipLevel);
		writeExp(startTime / 1000L);
		writeExp(endTime / 1000L);
		writeH((int) ((endTime - startTime) / 1000L));
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