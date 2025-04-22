package com.lineage.server.serverpackets;

/**
 * 慎重藥水
 * 
 * @author dexc
 */
public class S_PacketBoxWisdomPotion extends ServerBasePacket {

	private byte[] _byte = null;

	/** 慎重藥水 */
	public static final int WISDOM_POTION = 0x39;// 57;

	public S_PacketBoxWisdomPotion(final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(WISDOM_POTION);// 57
		writeC(0x2c);// 44
		writeH(time);
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
