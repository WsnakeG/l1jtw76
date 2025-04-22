package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.RangeInt;

/**
 * MP更新顯示
 * 
 * @author dexc
 */
public class S_MPUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	private static final RangeInt _mpRangeA = new RangeInt(0, 32767);

	private static final RangeInt _mpRangeX = new RangeInt(1, 32767);

	/**
	 * MP更新顯示
	 * 
	 * @param currentmp
	 * @param maxmp
	 */
	public S_MPUpdate(final int currentmp, final int maxmp) {
		buildPacket(currentmp, maxmp);
	}

	/**
	 * MP更新顯示
	 * 
	 * @param pc
	 */
	public S_MPUpdate(final L1PcInstance pc) {
		buildPacket(pc.getCurrentMp(), pc.getMaxMp());
	}

	/**
	 * MP更新顯示
	 * 
	 * @param currentmp
	 * @param maxmp
	 * @return
	 */
	private void buildPacket(final int currentmp, final int maxmp) {
		// 0000: 0f bb 00 ce 00 52 9b 97 .....R..
		writeC(S_OPCODE_MPUPDATE);
		writeH(_mpRangeA.ensure(currentmp));
		writeH(_mpRangeX.ensure(maxmp));

		/*
		 * this.writeC(0x00); this.writeC(0x00); this.writeC(0x00);
		 */

		/*
		 * if (currentmp < 0) { writeH(0); } else if (currentmp > 32767) {
		 * writeH(32767); } else { writeH(currentmp); } if (maxmp < 1) {
		 * writeH(1); } else if (maxmp > 32767) { writeH(32767); } else {
		 * writeH(maxmp); }
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
