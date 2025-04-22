package com.lineage.server.serverpackets;

/**
 * 角色選擇視窗
 */
public class S_PacketBoxSelect extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * <font color=#00800>角色選擇視窗</font> > 0000 : 39 2a e1 88 08 12 48 fa
	 * 9*....H.
	 */
	public static final int LOGOUT = 0x2a;// 42

	/**
	 * 角色選擇視窗
	 * 
	 * @param subCode
	 */
	public S_PacketBoxSelect() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(LOGOUT);// 42
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
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
