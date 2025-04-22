package com.lineage.server.serverpackets;

/**
 * 選擇一個目標
 * 
 * @author DaiEn
 */
public class S_SelectTarget extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 選擇一個目標
	 * 
	 * @param ObjectId
	 */
	public S_SelectTarget(final int ObjectId) {
		writeC(S_OPCODE_SELECTTARGET);
		writeD(ObjectId);
		writeC(0x00);// TYPE 未知用途
		writeC(0x00);
		writeC(0x02);
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
