package com.lineage.server.serverpackets;

/**
 * 水之元氣 OVER
 * 
 * @author daien
 */
public class S_PacketBoxWaterLife extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxWaterLife() {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x3b);
		writeH(0x0000);
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