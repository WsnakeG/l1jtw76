package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 測試釣魚 時間bar
 */
public class S_FishTime extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_FishTime(final int time) {
		writeC(S_OPCODE_CRAFTSYSTEM); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為 S_OPCODE_CRAFTSYSTEM
		writeC(0x3f);
		writeC(0x00);
		writeC(0x08);
		writeC(0x01);
		writeC(0x10);
		writeC(time);
		writeC(0x01);
		writeC(0x18);
		writeC(0x01);
		writeC(0x4d);
		writeC(0x4c);
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
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

