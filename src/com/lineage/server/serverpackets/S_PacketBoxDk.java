package com.lineage.server.serverpackets;

/**
 * 龍騎士 弱點曝光
 * 
 * @author daien
 */
public class S_PacketBoxDk extends ServerBasePacket {

	/** 弱点曝光1阶段 */
	public static final int LV1 = 0x01;// 攻擊 +3 / +20

	/** 弱点曝光2阶段 */
	public static final int LV2 = 0x02;// 攻擊 +6 / +40

	/** 弱点曝光3阶段 */
	public static final int LV3 = 0x03;// 攻擊 +9 / +60

	private byte[] _byte = null;

	public S_PacketBoxDk(final int lv) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x4b);// 弱點曝光
		writeC(lv);// LV
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
