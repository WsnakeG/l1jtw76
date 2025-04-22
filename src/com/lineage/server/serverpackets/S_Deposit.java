package com.lineage.server.serverpackets;

/**
 * 城堡寶庫(要求存入資金)
 * 
 * @author dexc
 */
public class S_Deposit extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 城堡寶庫(要求存入資金)
	 * 
	 * @param objecId
	 */
	public S_Deposit(final int objecId) {
		writeC(S_OPCODE_DEPOSIT);
		writeD(objecId);
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
