package com.lineage.server.serverpackets;

public class S_SystemMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * クライアントにデータの存在しないオリジナルのメッセージを表示する。
	 * メッセージにnameid($xxx)が含まれている場合はオーバーロードされたもう一方を使用する。
	 * 
	 * @param msg - 表示する文字列
	 */
	public S_SystemMessage(final String msg) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(msg);
	}

	/**
	 * クライアントにデータの存在しないオリジナルのメッセージを表示する。
	 * 
	 * @param msg - 表示する文字列
	 * @param nameid - 文字列にnameid($xxx)が含まれている場合trueにする。
	 */
	public S_SystemMessage(final String msg, final boolean nameid) {
		writeC(S_OPCODE_NPCSHOUT);
		writeC(2);
		writeD(0);
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
