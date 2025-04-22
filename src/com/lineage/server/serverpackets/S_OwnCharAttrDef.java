package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 更新角色防禦屬性
 * 
 * @author dexc
 */
public class S_OwnCharAttrDef extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新角色防禦屬性
	 * 
	 * @param pc
	 */
	public S_OwnCharAttrDef(final L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		writeC(S_OPCODE_OWNCHARATTRDEF);
		writeD(pc.getAc());// value C => D by 7.6tw
		writeH(pc.getFire());
		writeH(pc.getWater());
		writeH(pc.getWind());
		writeH(pc.getEarth());
	}

	/**
	 * 更新角色防禦屬性-測試
	 * 
	 * @param pc
	 */
	public S_OwnCharAttrDef() {
		writeC(S_OPCODE_OWNCHARATTRDEF);
		writeC(-99);
		writeH(90);
		writeH(85);
		writeH(80);
		writeH(75);
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
