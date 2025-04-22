package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魔法效果:敏捷提升
 * 
 * @author dexc
 */
public class S_Dexup extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:敏捷提升
	 * 
	 * @param pc 原始值
	 * @param type 增加值
	 * @param time 時間
	 */
	public S_Dexup(final L1PcInstance pc, final int type, final int time) {
		// 0000: 65 b0 04 13 05 21 3e d8 e....!>.
		writeC(S_OPCODE_DEXUP);
		writeH(time);
		writeC(pc.getDex());
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
