package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.RangeInt;

/**
 * HP更新顯示
 * 
 * @author dexc
 */
public class S_HPUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	private static final RangeInt _hpRange = new RangeInt(1, 32767);

	/**
	 * HP更新顯示
	 * 
	 * @param currentHp
	 * @param maxHp
	 */
	public S_HPUpdate(final int currentHp, final int maxHp) {
		buildPacket(currentHp, maxHp);
	}

	/**
	 * HP更新顯示
	 * 
	 * @param pc
	 */
	public S_HPUpdate(final L1PcInstance pc) {
		buildPacket(pc.getCurrentHp(), pc.getMaxHp());
	}

	public void buildPacket(final int currentHp, final int maxHp) {
		// 0000: 26 d8 01 fd 01 bd 53 a9 &.....S.
		writeC(S_OPCODE_HPUPDATE);
		writeH(_hpRange.ensure(currentHp));
		writeH(_hpRange.ensure(maxHp));

		/*
		 * this.writeC(0x00); this.writeC(0x00); this.writeC(0x00);
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
