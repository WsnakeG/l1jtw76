package com.lineage.server.serverpackets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 選項(Yes/No)
 * @author loli
 *
 */
public class S_Message_YN extends ServerBasePacket {

	private byte[] _byte = null;

	// 訊息編序
	public static AtomicInteger _MessageNumber = new AtomicInteger(1);
	
	// 交易訊息(string-c交易訊息號獨立拉出來判斷)
	private static final int MESSAGE_TRADE = 0x00fc;

	/**
	 * 選項(Yes/No)
	 * @param mode
	 */
	public S_Message_YN(final int mode) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(mode);
	}
	
	/**
	 * 選項(Yes/No)<BR>
	 * 交易
	 * @param name
	 */
	public S_Message_YN(final String name) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(MESSAGE_TRADE);
		writeS(name);
	}

	/**
	 * 選項(Yes/No)
	 * @param mode
	 * @param msg
	 */
	public S_Message_YN(final int mode, final String msg) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(mode);
		writeS(msg);
	}

	/**
	 * 選項(Yes/No)
	 * @param mode
	 * @param msg1
	 * @param msg2
	 */
	public S_Message_YN(final int mode, final String msg1, final String msg2) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(mode);
		writeS(msg1);
		writeS(msg2);
	}
	
	/**
	 * 選項(Yes/No)
	 * @param mode
	 * @param msg1
	 * @param msg2
	 * @param msg3
	 */
	public S_Message_YN(final int mode, final String msg1, final String msg2, final String msg3) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(mode);
		writeS(msg1);
		writeS(msg2);
		writeS(msg3);
	}
	

	public S_Message_YN(final int mode, final int value) {
		writeC(S_OPCODE_YES_NO);
		writeH(0x0000);
		writeD(_MessageNumber.incrementAndGet());
		writeH(mode);
		writeS(value + "");
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
