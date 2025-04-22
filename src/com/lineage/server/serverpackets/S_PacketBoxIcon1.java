package com.lineage.server.serverpackets;

public class S_PacketBoxIcon1 extends ServerBasePacket {

	/** 技能圖示 */
	private static final int _icons_1 = 0x14;// 20;//0x14

	private static final int _dodge_up = 0x58;// 88 增加閃避率

	private static final int _dodge_down = 0x65;// 101 減少閃避率

	private byte[] _byte = null;

	public S_PacketBoxIcon1(final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(_icons_1);
		writeH(time);
		writeC(type);
	}

	/**
	 * 技能 - 閃避率
	 * 
	 * @param type true:增加閃避率 false:減少閃避率
	 * @param i 增減質
	 */
	public S_PacketBoxIcon1(final boolean type, final int i) {
		writeC(S_OPCODE_PACKETBOX);
		if (type) {// 增加閃避率
			writeC(_dodge_up);
			writeC(i);

		} else {// 減少閃避率
			writeC(_dodge_down);
			writeC(i);
		}
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
