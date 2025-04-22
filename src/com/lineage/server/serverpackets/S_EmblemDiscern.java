package com.lineage.server.serverpackets;

/**
 * 啟用/關閉 血盟盟徽識別
 * 
 * @author user
 */
public class S_EmblemDiscern extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 啟用/關閉 血盟盟徽識別
	 * 
	 * @param is use true 啟用 false 不啟用
	 */
	public S_EmblemDiscern(final boolean use) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0xad);// type 0xad
		writeC(0x01);
		writeBoolean(use);
	}

	/**
	 * 空血盟使用 啟用/關閉 血盟盟徽識別
	 */
	public S_EmblemDiscern() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0xad);// type 0xad
		writeC(0x81);
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
		return "[S] " + this.getClass().getSimpleName();
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
