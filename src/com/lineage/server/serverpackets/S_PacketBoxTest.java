package com.lineage.server.serverpackets;

/**
 * 測試
 * 
 * @author dexc
 */
public class S_PacketBoxTest extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxTest() {
		// {0x52,0x52,(byte)0xc8,0x00,0x00,0x00,(byte)0xeb,0x1f},
		writeC(S_OPCODE_PACKETBOX);
		writeC(0x52);
		writeC(150);// 經驗值提高指數
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}

	public S_PacketBoxTest(final byte ocid, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(ocid);
		writeC(time);// 經驗值提高指數
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);

	}

	public S_PacketBoxTest(final int type, final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(type);
		writeH(time);
		writeH(0x00);
	}

	public S_PacketBoxTest(final int value, final String[] clanName) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(value);
		for (int i = 0; i < value; i++) {
			writeS(clanName[i]);
		}
	}

	public S_PacketBoxTest(final int time) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(79);
		writeC(2);
		writeS("TEMP");
		writeS("AASS");

	}

	public S_PacketBoxTest(final byte[] bs) {
		for (final byte outbs : bs) {
			writeC(outbs);
		}
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
