package com.lineage.server.serverpackets;

/**
 * 魔法效果:加速纇
 * 
 * @author dexc
 */
public class S_SkillHaste extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:加速纇
	 * 
	 * @param objid 對象objid
	 * @param mode 效果 <br>
	 *            0:正常<br>
	 *            1:加速<br>
	 *            2:減速<br>
	 * @param time 時間
	 */
	public S_SkillHaste(final int objid, final int mode, final int time) {
		// 0000: 0b 9d dc ad 01 01 b0 04 ........
		writeC(S_OPCODE_SKILLHASTE);
		writeD(objid);
		writeC(mode);
		writeH(time);
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
