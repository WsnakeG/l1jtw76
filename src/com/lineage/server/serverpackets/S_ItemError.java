package com.lineage.server.serverpackets;

/**
 * 學習魔法材料不足
 * 
 * @author dexc
 */
public class S_ItemError extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 學習魔法材料不足
	 * 
	 * @param skillid
	 */
	public S_ItemError(final int skillid) {
		buildPacket(skillid);
	}

	private void buildPacket(final int skillid) {
		// 0000: 6f 00 00 00 00 10 4f e9 o.....O.
		this.writeC(S_OPCODE_MATERIAL); // XXX S_NOT_ENOUGH_FOR_SPELL 修改為 S_OPCODE_MATERIAL
		writeD(skillid);
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