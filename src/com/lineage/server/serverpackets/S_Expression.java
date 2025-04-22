package com.lineage.server.serverpackets;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 
 * @author kyo
 *
 */
public class S_Expression extends ServerBasePacket {
	
	private byte[] _byte = null;
	
	/**
	 * 
	 * @param id
	 * @param value
	 */
	public S_Expression(final int id, final int value) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x0140);
		this.writeInt32(1, id);
		this.writeInt32(2, 0x02);
		this.writeInt32(3, value);
		this.randomShort();
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return "[S] " + this.getClass().getSimpleName();
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
