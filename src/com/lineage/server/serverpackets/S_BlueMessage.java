package com.lineage.server.serverpackets;

/**
 * 畫面中藍色訊息
 * 
 * @author dexc
 */
public class S_BlueMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 畫面中藍色訊息
	 * 
	 * @param type
	 */
	public S_BlueMessage(final int type) {
		buildPacket(type, null);
	}

	/**
	 * 畫面中藍色訊息
	 * 
	 * @param type
	 * @param msg1
	 */
	public S_BlueMessage(final int type, final String msg1) {
		buildPacket(type, new String[] { msg1 });
	}

	/**
	 * 畫面中藍色訊息
	 * 
	 * @param type
	 * @param msg1
	 * @param msg2
	 */
	public S_BlueMessage(final int type, final String msg1, final String msg2) {
		buildPacket(type, new String[] { msg1, msg2 });
	}

	/**
	 * 畫面中藍色訊息
	 * 
	 * @param type
	 * @param info
	 */
	public S_BlueMessage(final int type, final String[] info) {
		buildPacket(type, info);
	}

	private void buildPacket(final int type, final String[] info) {
		writeC(S_OPCODE_BLUEMESSAGE);
		writeH(type);
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
