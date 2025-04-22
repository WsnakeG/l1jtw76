package com.lineage.server.serverpackets;

import java.io.IOException;

/**
 * 戰士技能
 * 
 * @author simlin
 */
public class S_WarriorSkill extends ServerBasePacket {
	
	private byte[] _byte = null;

	public static final int LOGIN = 0x91;

	public static final int ADD = 0x92;

	public S_WarriorSkill(final int subcode, final int type) {
		writeC(S_OPCODE_CRAFTSYSTEM); // XXX S_OPCODE_EXTENDED_PROTOBUF 修改為 S_OPCODE_CRAFTSYSTEM
		writeC(subcode);
		switch (subcode) {
		case LOGIN:
			writeC(0x01);
			writeC(0x0a);
			writeC(0x02);
			writeC(0x08);
			writeC(type);
			writeC(0x82);
			writeC(0x0b);
			break;
		case ADD:
			writeC(0x01);
			writeC(0x08);
			writeC(type);
			writeC(0x10);
			writeC(0x0a);
			writeH(0x33c2);
			break;
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
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
