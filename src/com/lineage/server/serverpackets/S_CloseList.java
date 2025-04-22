package com.lineage.server.serverpackets;

/**
 * 關閉對話窗
 * 
 * @author dexc
 */
public class S_CloseList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 關閉對話窗
	 * 
	 * @param objid
	 */
	public S_CloseList(final int objid) {
		writeC(S_OPCODE_SHOWHTML);
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
