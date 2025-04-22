package com.lineage.server.serverpackets;

/**
 * 受到殷海薩的祝福，增加了些許的狩獵經驗值
 * 
 * @author DaiEn
 */
public class S_PacketBoxExp extends ServerBasePacket {

	private byte[] _byte = null;

	/** 1,550：受到殷海薩的祝福，增加了些許的狩獵經驗值。 */
	public static final int LEAVES = 0x52;

	/**
	 * 受到殷海薩的祝福，增加了些許的狩獵經驗值
	 * 
	 * @param exp 經驗值增加率
	 */
	public S_PacketBoxExp(final int exp) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(LEAVES);
		writeD(exp);
		writeD(7700);
		writeD(0);
	}

	/**
	 * 解除 受到殷海薩的祝福，增加了些許的狩獵經驗值
	 */
	public S_PacketBoxExp() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(LEAVES);
		writeD(0);
		writeD(7700);
		writeD(0);
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
