package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 物件動作種類(長時間)
 * 
 * @author dexc
 */
public class S_CharVisualUpdate extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件動作種類(長時間)
	 * 
	 * @param objid 物件OBJID
	 * @param weaponType 武器型態代號(TYPE)
	 */
	public S_CharVisualUpdate(final int objid, final int weaponType) {
		writeC(S_OPCODE_CHARVISUALUPDATE);
		writeD(objid);
		writeC(weaponType);
	}

	/**
	 * 物件動作種類(長時間)
	 * 
	 * @param cha
	 */
	public S_CharVisualUpdate(final L1PcInstance cha) {
		writeC(S_OPCODE_CHARVISUALUPDATE);
		writeD(cha.getId());
		writeC(cha.getCurrentWeapon());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
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
