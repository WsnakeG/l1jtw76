package com.lineage.server.serverpackets;

import com.lineage.server.model.L1Object;

/**
 * 物件刪除
 * 
 * @author dexc
 */
public class S_RemoveObject extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物件刪除
	 * 
	 * @param obj
	 */
	public S_RemoveObject(final L1Object obj) {
		// 0000: 7c 2c b6 00 00 55 b1 ac |,...U..
		writeC(S_OPCODE_REMOVE_OBJECT);
		writeD(obj.getId());

		/*
		 * this.writeC(0x55); this.writeC(0xb1); this.writeC(0xac);
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

