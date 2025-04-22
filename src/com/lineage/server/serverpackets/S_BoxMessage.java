package com.lineage.server.serverpackets;

/**
 * 寶物公告
 * 
 * @author loli
 */
public class S_BoxMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 寶物公告
	 * 
	 * @param msg
	 */
	public S_BoxMessage(final String msg) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(msg);
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
