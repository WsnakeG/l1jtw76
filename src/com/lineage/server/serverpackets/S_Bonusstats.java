package com.lineage.server.serverpackets;

/**
 * 能力質選取資料
 * 
 * @author dexc
 */
public class S_Bonusstats extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 能力質選取資料
	 * 
	 * @param objid
	 */
	public S_Bonusstats(final int objid) {
		buildPacket(objid);
	}

	private void buildPacket(final int objid) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(objid);
		writeS("RaiseAttr");
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
