package com.lineage.server.serverpackets;

/**
 * 畫面中紅色訊息(登入來源)
 * 
 * @author dexc
 */
public class S_RedMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 畫面中紅色訊息(登入來源)
	 * 
	 * @param acc 帳號名稱
	 * @param msg1 IP OR MAC訊息(不支援$)
	 */
	public S_RedMessage(final String acc, final String msg1) {
		buildPacket(acc, new String[] { msg1 });
	}

	private void buildPacket(final String acc, final String[] info) {
		writeC(S_OPCODE_BLUEMESSAGE);
		writeS(acc);
		if (info == null) {
			writeC(0x00);

		} else {
			writeC(info.length);
			for (int i = 0; i < info.length; i++) {
				writeS(info[i]);
			}
		}
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
