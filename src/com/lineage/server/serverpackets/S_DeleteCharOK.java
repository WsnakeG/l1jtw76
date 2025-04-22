package com.lineage.server.serverpackets;

/**
 * 角色移除(立即/非立即)
 * 
 * @author dexc
 */
public class S_DeleteCharOK extends ServerBasePacket {

	private byte[] _byte = null;

	public static final int DELETE_CHAR_NOW = 0x05;// 立即刪除

	public static final int DELETE_CHAR_AFTER_7DAYS = 0x51;// 7日刪除

	/**
	 * 角色移除(立即/非立即)
	 * 
	 * @param type
	 */
	public S_DeleteCharOK(final int type) {
		// 0000: 04 05 34 12 59 00 00 32 ..4.Y..2
		writeC(S_OPCODE_DETELECHAROK);
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
