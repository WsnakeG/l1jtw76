package com.lineage.server.serverpackets;

/**
 * 撥放音效
 * 
 * @author dexc
 */
public class S_Sound extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 撥放音效
	 * 
	 * @param sound 音效編號
	 */
	public S_Sound(final int sound) {
		buildPacket(sound, 0);
	}

	/**
	 * 撥放音效
	 * 
	 * @param sound 音效編號
	 * @param repeat 重複
	 */
	public S_Sound(final int sound, final int repeat) {
		buildPacket(sound, repeat);
	}

	private void buildPacket(final int sound, final int repeat) {
		// 0000: 68 00 b5 01 d2 af 45 10 h.....E.
		writeC(S_OPCODE_SOUND);
		writeC(repeat); // 重複
		writeH(sound);// 音效編號
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
