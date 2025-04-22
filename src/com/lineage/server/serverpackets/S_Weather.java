package com.lineage.server.serverpackets;

/**
 * 遊戲天氣 1~3雨 17~19雪
 * 
 * @author dexc
 */
public class S_Weather extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 遊戲天氣
	 * 
	 * @param weather
	 */
	public S_Weather(final int weather) {
		buildPacket(weather);
	}

	private void buildPacket(final int weather) {
		writeC(S_OPCODE_WEATHER);
		writeC(weather);
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
