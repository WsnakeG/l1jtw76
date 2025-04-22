package com.lineage.server.serverpackets;

/**
 * 你覺得舒服多了訊息
 */
public class S_PacketBoxHpMsg extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * <font color=#00800>(77) \f1你覺得舒服多了。</font>
	 */
	private static final int MSG_FEEL_GOOD = 31;

	public S_PacketBoxHpMsg() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(MSG_FEEL_GOOD); // 31

		// this.writeC(S_OPCODE_SERVERMSG);
		// this.writeH(MSG_FEEL_GOOD); // 77
		// this.writeC(0x00);
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
