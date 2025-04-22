package com.lineage.server.serverpackets;

/**
 * 魔法效果:勇敢藥水纇
 * 
 * @author dexc
 */
public class S_SkillBrave extends ServerBasePacket {
	
	private byte[] _byte = null;

	/**
	 * 魔法效果:勇敢藥水纇
	 * 
	 * @param objid
	 * @param mode <br>
	 *            0:你的情緒回復到正常。(解除 )<br>
	 *            1:從身體的深處感到熱血沸騰。(第一階段勇水)<br>
	 *            3:身體內深刻的感覺到充滿了森林的活力。(精靈餅乾)<br>
	 *            4:風之疾走 / 神聖疾走 / 行走加速 / 生命之樹果實效果<br>
	 *            5:從身體的深處感到熱血沸騰。(第二階段勇水)<br>
	 *            6:引發龍之血暴發出來了。<br>
	 * @param time
	 */
	public S_SkillBrave(final int objid, final int mode, final int time) {
		// 0000: 4e 91 46 a9 01 04 c0 03 N.F.....
		writeC(S_OPCODE_SKILLBRAVE);
		writeD(objid);
		writeC(mode);
		writeH(time);
	}

	/**
	 * TEST
	 * 
	 * @param objid
	 * @param opid
	 */
	public S_SkillBrave(final int objid, final int mode) {
		// 0000: 4e 91 46 a9 01 04 c0 03 N.F.....
		writeC(S_OPCODE_SKILLBRAVE);
		writeD(objid);
		writeC(mode);
		writeH(300);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
