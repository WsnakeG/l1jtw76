package com.lineage.server.serverpackets.ability;

import com.lineage.server.serverpackets.ServerBasePacket;

/**
 * 萬能藥使用數量
 * @author kyo
 *
 */
public class S_ElixirCount extends ServerBasePacket {
	
	private byte[] _byte = null;

	/**
	 *  萬能藥使用數量
	 * @param stage
	 * @param invWeight
	 * @param maxWeight
	 */
	public S_ElixirCount(final int count) {
		this.writeC(S_OPCODE_CRAFTSYSTEM);
		this.writeH(0x01E9);
		this.writeInt32(1, count);// 萬能藥使用數量
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
