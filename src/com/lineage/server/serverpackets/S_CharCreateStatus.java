package com.lineage.server.serverpackets;

/**
 * 角色創造結果
 * 
 * @author DaiEn
 */
public class S_CharCreateStatus extends ServerBasePacket {

	private byte[] _byte = null;

	public static final int REASON_OK = 0x02;

	public static final int REASON_ALREADY_EXSISTS = 0x06;

	public static final int REASON_INVALID_NAME = 0x09;

	public static final int REASON_WRONG_AMOUNT = 0x15;

	/**
	 * 角色創造結果
	 * 
	 * @param reason
	 */
	public S_CharCreateStatus(final int reason) {
		// 0000: 1d 02 3c 6c 12 a1 43 46 ..<l..CF
		writeC(S_OPCODE_NEWCHARWRONG);
		writeC(reason);
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
