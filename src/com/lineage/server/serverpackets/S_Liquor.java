package com.lineage.server.serverpackets;

/**
 * 魔法效果:操作混亂
 * 
 * @author dexc
 */
public class S_Liquor extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 魔法效果:操作混亂
	 * 
	 * @param objecId
	 */
	public S_Liquor(final int objecId) {
		writeC(S_OPCODE_LIQUOR);
		writeD(objecId);
		writeC(0x01);
	}

	/**
	 * 混亂武器(失心)
	 * 
	 * @param objecId
	 * @param type 0:無 1:2:3:效果強度
	 */
	public S_Liquor(final int objecId, final int type) {
		writeC(S_OPCODE_LIQUOR);
		writeD(objecId);
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
