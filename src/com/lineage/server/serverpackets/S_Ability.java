package com.lineage.server.serverpackets;

/**
 * 戒指
 * 
 * @author dexc
 */
public class S_Ability extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 戒指
	 * 
	 * @param type
	 * @param equipped
	 */
	public S_Ability(final int type, final boolean equipped) {
		buildPacket(type, equipped);
	}

	private void buildPacket(final int type, final boolean equipped) {
		writeC(S_OPCODE_ABILITY);
		writeC(type); // 1:ROTC 5:ROSC
		writeC(equipped ? 0x01 : 0x00);
		// this.writeC(0x02);
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

	@Override
	public byte[] getContent() { //20240901
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() { //20240901
		return this.getClass().getSimpleName();
	}
}
