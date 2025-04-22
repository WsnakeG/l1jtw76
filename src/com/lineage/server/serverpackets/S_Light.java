package com.lineage.server.serverpackets;

/**
 * 更新物件亮度
 * 
 * @author dexc
 */
public class S_Light extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物件亮度
	 * 
	 * @param objid
	 * @param type
	 */
	public S_Light(final int objid, final int type) {
		buildPacket(objid, type);
	}

	private void buildPacket(final int objid, final int type) {
		// 0000: 66 a5 ef 8a 01 00 e0 58 f......X
		writeC(S_OPCODE_LIGHT);
		writeD(objid);
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
