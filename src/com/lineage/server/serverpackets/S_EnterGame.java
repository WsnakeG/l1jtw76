package com.lineage.server.serverpackets;

/**
 * 宣告進入遊戲
 * 
 * @author dexc Server OP NO: 54 0000: 36 03 c9 ea a5 c4 f2 1c
 */
public class S_EnterGame extends ServerBasePacket {

	private byte[] _byte = null;

	public S_EnterGame() {
		// 0000: 29 03 6a e0 cf 83 e3 da ).j.....
		writeC(S_OPCODE_LOGINTOGAME);
		writeC(0x03);
		writeC(0x15);
		writeC(0x8b);
		writeC(0x7b);
		writeC(0x94);
		writeC(0xf0);
		writeC(0x2f);

		/*
		 * this.writeC(0x6a); this.writeC(0xe0); this.writeC(0xcf);
		 * this.writeC(0x83); this.writeC(0xe3); this.writeC(0xda);
		 */
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
