package com.lineage.server.serverpackets;

/**
 * 交易封包
 * 
 * @author dexc
 */
public class S_Trade extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 交易封包
	 * 
	 * @param name
	 */
	public S_Trade(final String name) {
		writeC(S_OPCODE_TRADE);
		writeS(name);
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
