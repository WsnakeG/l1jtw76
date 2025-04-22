package com.lineage.server.serverpackets;

/**
 * 角色封號
 * 
 * @author dexc
 */
public class S_CharTitle extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 角色封號
	 * 
	 * @param objid
	 * @param title
	 */
	/*
	 * public S_CharTitle(final int objid, final String title) {
	 * this.writeC(S_OPCODE_CHARTITLE); this.writeD(objid); this.writeS(title);
	 * }
	 */

	/**
	 * 角色封號
	 * 
	 * @param objid
	 * @param title
	 */
	public S_CharTitle(final int objid, final StringBuilder title) {
		writeC(S_OPCODE_CHARTITLE);
		writeD(objid);
		writeS(title.toString());
	}

	/**
	 * 消除角色封號
	 * 
	 * @param objid
	 * @param title
	 */
	public S_CharTitle(final int objid) {
		writeC(S_OPCODE_CHARTITLE);
		writeD(objid);
		writeS("");
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
