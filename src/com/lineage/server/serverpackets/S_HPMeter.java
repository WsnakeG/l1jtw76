package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物見血條
 * @author admin
 *
 */
public class S_HPMeter extends ServerBasePacket {

	private byte[] _byte = null;
	
	/**
	 * 物見血條
	 * @param objId 物件OBJID
	 * @param hpRatio 血條百分比
	 */
	public S_HPMeter(int objId, int hpRatio, int mpRatio) {
		buildPacket(objId, hpRatio, mpRatio);
	}
	
	/**
	 * 物件血條
	 * @param cha
	 */
	public S_HPMeter(L1Character cha) {
		final int objId = cha.getId();
		int hpRatio = 100;
		if (0 < cha.getMaxHp()) {
			hpRatio = (100 * cha.getCurrentHp()) / cha.getMaxHp();
		}
		
		int mpRatio = cha.getMaxMp() == 0 ? 0xff : 100 * cha.getCurrentMp() / cha.getMaxMp();

		if (!(cha instanceof L1PcInstance)) {
			mpRatio = 0xff;
		}

		buildPacket(objId, hpRatio, mpRatio);
	}

	private void buildPacket(int objId, int hpRatio, int mpRatio) {
		writeC(S_OPCODE_HPMETER);
		writeD(objId);
		writeC(hpRatio);
		writeC(mpRatio);
		writeH(0x00);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}

		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName() + " [S->C 發送物件血條封包]";
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
