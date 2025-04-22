package com.lineage.server.serverpackets;

/**
 * 魔法效果:防禦
 * 
 * @author dexc
 */
public class S_SkillIconShield extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:防禦
	 * 
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_SkillIconShield(final int type, final int time) {
		// 0000: 06 08 07 02 50 01 00 29 ....P..)
		writeC(S_OPCODE_SKILLICONSHIELD);
		writeH(time);
		writeC(type);

		/*
		 * this.writeC(0x50); this.writeC(0x01); this.writeC(0x00);
		 * this.writeC(0x29);
		 */
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
