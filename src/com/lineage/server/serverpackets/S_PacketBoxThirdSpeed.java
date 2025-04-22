package com.lineage.server.serverpackets;

/**
 * 巧克力蛋糕
 * 
 * @author LoLi
 */
public class S_PacketBoxThirdSpeed extends ServerBasePacket {

	private byte[] _byte = null;

	public static final int CAKE = 0x3c;

	/**
	 * 巧克力蛋糕
	 * 
	 * @param time
	 */
	public S_PacketBoxThirdSpeed(final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(CAKE);
		writeC(time >> 2); // time / 4
		writeC(0x8);
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
		return getClass().getSimpleName();
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
