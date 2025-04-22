package com.lineage.server.serverpackets;

import java.util.Random;

import com.lineage.config.ConfigKill;

/**
 * 殺人公告
 * 
 * @author dexc
 */
public class S_KillMessage extends ServerBasePacket {

	private byte[] _byte = null;

	private static final Random _random = new Random();

	/**
	 * 殺人公告
	 * 
	 * @param winName
	 * @param deathName
	 */
	public S_KillMessage(final String winName, final String deathName) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		final String x1 = ConfigKill.KILL_TEXT_LIST
				.get(_random.nextInt(ConfigKill.KILL_TEXT_LIST.size()) + 1);
		writeS(String.format(x1, winName, deathName));
	}

	/**
	 * 賭場NPC對話
	 * 
	 * @param winName
	 * @param deathName
	 */
	public S_KillMessage(final String name, final String msg, final int i) {
		writeC(S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(" \\fY[" + name + "] " + msg);
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
