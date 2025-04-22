package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * VIP ICON 顯示
 * 
 * @author simlin
 */
public class S_VipShow extends ServerBasePacket {

	private byte[] _byte = null;

	public S_VipShow(final int objId, final int vipLevel) {
		writeC(S_OPCODE_CHARRESET);
		writeC(73);
		writeD(objId);
		writeD(vipLevel);
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