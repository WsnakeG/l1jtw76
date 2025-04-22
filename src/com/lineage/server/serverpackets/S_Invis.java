package com.lineage.server.serverpackets;

/**
 * 物件隱形
 * 
 * @author dexc
 */
public class S_Invis extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件隱形
	 * 
	 * @param objid
	 * @param type 0:無 1:隱形
	 */
	public S_Invis(final int objid, final int type) {
		buildPacket(objid, type);
	}

	private void buildPacket(final int objid, final int type) {
		// 0000: 2a c5 8c b7 01 01 c1 99 *.......
		writeC(S_OPCODE_INVIS);
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
