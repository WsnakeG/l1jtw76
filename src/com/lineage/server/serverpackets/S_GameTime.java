package com.lineage.server.serverpackets;

import com.lineage.server.model.gametime.L1GameTimeClock;

/**
 * @author dexc
 */
public class S_GameTime extends ServerBasePacket {
	private byte[] _byte = null;
	public S_GameTime(final int time) {
		buildPacket(time);
	}

	public S_GameTime() {
		final int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		buildPacket(time);
	}

	private void buildPacket(final int time) {
		// 0000: 30 84 15 37 20 04 08 00 0..7 ...
		writeC(S_OPCODE_GAMETIME);
		writeD(time);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
