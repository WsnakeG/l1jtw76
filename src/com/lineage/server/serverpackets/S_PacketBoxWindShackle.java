package com.lineage.server.serverpackets;

/**
 * 風之枷鎖(S_OPCODE_PACKETBOX)
 * 
 * @author dexc
 */
public class S_PacketBoxWindShackle extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * <font color=#00800>風之枷鎖</font>
	 */
	public static final int WIND_SHACKLE = 0x2c;// 44;

	/**
	 * 風之枷鎖
	 * 
	 * @param objectId
	 * @param time
	 */
	public S_PacketBoxWindShackle(final int objectId, final int time) {
		final int buffTime = time >> 2; // なぜか4倍されるため4で割っておく
		writeC(S_OPCODE_PACKETBOX);
		writeC(WIND_SHACKLE);// 44
		writeD(objectId);
		writeH(buffTime);
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
