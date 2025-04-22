package com.lineage.server.serverpackets;

import java.io.IOException;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 全頻廣播封包[尬廣跟上]
 * 
 * @author Roy
 */
public class S_Suona extends ServerBasePacket {

	private byte[] _byte = null;

	public S_Suona(final L1PcInstance pc, final byte[] chat) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x12);
		final String s = "[" + pc.getName() + "] ";
		final byte[] date = s.getBytes();
		for (int i = 0; i < date.length; i++) {
			writeC(date[i]);
		}
		writeByte(chat);
	}

	@Override
	public byte[] getContent() throws IOException {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
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
