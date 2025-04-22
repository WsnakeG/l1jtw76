package com.lineage.server.serverpackets;

public class S_TaxRate extends ServerBasePacket {

	private byte[] _byte = null;

	public S_TaxRate(final int objecId) {
		writeC(S_OPCODE_TAXRATE);
		writeD(objecId);
		writeC(0x0a); // 10 10%~50%
		writeC(0x32); // 50
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
