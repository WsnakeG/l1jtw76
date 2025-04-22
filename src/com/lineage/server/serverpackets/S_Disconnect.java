package com.lineage.server.serverpackets;

/**
 * 立即中斷連線
 * 
 * @author dexc
 */
public class S_Disconnect extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 立即中斷連線
	 */
	public S_Disconnect() {
		// final int content = 500;

		writeC(S_OPCODE_DISCONNECT);
		writeH(0x01f4);// 500
		writeD(0x00000000);
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
