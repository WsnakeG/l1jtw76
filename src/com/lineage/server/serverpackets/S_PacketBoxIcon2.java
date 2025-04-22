package com.lineage.server.serverpackets;

public class S_PacketBoxIcon2 extends ServerBasePacket {

	/** 技能圖示 */
	private static final int ICONS2 = 0x15;// 21;//0x15

	private byte[] _byte = null;

	public S_PacketBoxIcon2(final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(ICONS2);
		writeH(time);
		writeC(type);
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
