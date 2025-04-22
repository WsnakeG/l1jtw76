package com.lineage.server.serverpackets;

public class S_WhoAmount extends ServerBasePacket {

	private byte[] _byte = null;

	public S_WhoAmount(final String amount) {
		writeC(S_OPCODE_SERVERMSG);
		writeH(0x0051);
		writeC(0x01);
		writeS(amount);
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
