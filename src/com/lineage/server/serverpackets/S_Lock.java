package com.lineage.server.serverpackets;

/**
 * 座標異常重整
 * 
 * @author dexc
 */
public class S_Lock extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 座標異常重整
	 * 
	 * @param type
	 * @param equipped
	 */
	public S_Lock() {
		buildPacket();
	}

	private void buildPacket() {
		writeC(S_OPCODE_TELEPORTLOCK);
		writeC(0x00);
		/*
		 * this.writeC(0xf1); this.writeC(0x2d); this.writeC(0x7d);
		 * this.writeC(0x02); this.writeC(0xf9);
		 */
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
