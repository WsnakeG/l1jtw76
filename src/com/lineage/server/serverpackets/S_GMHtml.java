package com.lineage.server.serverpackets;

/**
 * 顯示指定HTML
 * 
 * @author dexc
 */
public class S_GMHtml extends ServerBasePacket {
	private byte[] _byte = null;

	/**
	 * 顯示指定HTML
	 * 
	 * @param _objid
	 * @param html
	 */
	public S_GMHtml(final int _objid, final String html) {
		writeC(S_OPCODE_SHOWHTML);
		writeD(_objid);
		writeS(html);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
