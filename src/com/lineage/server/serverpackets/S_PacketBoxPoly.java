package com.lineage.server.serverpackets;

public class S_PacketBoxPoly extends ServerBasePacket {

	private byte[] _byte = null;

	public S_PacketBoxPoly(final int id) {
		writeC(S_OPCODE_PACKETBOX);
		writeC(0xb9);
		writeD(id);
		writeH(4);
		writeS("gold deathknight");
		writeS("lightning deathknight");
		writeS("fire deathknight");
		writeS("dark deathknight");
		writeD(0);
		writeH(0);
		writeC(0);
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
