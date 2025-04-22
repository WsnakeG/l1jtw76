package com.lineage.server.serverpackets;

/**
 * 給GM的訊息
 * 
 * @author dexc
 */
public class S_ToGmMessage extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 給GM的訊息
	 * 
	 * @param mode
	 */
	public S_ToGmMessage(final String info) {
		writeC(S_OPCODE_NPCSHOUT);
		writeC(0x00);// 一般頻道
		writeD(0x00000000);
		writeS("\\fY" + info);
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
