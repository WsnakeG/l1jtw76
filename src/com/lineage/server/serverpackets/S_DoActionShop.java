package com.lineage.server.serverpackets;

import com.lineage.server.ActionCodes;

/**
 * 物件動作種類(短時間)-個人商店
 * 
 * @author dexc
 */
public class S_DoActionShop extends ServerBasePacket {
	
	private byte[] _byte = null;

	/**
	 * PC使用
	 * 
	 * @param object
	 * @param message
	 */
	public S_DoActionShop(final int object, final byte[] message) {
		writeC(S_OPCODE_DOACTIONGFX);
		writeD(object);
		writeC(ActionCodes.ACTION_Shop);// 動作編號
		writeByte(message);// 文字內容
	}

	/**
	 * 虛擬人物使用
	 * 
	 * @param object
	 * @param message
	 */
	public S_DoActionShop(final int object, final String message) {
		writeC(S_OPCODE_DOACTIONGFX);
		writeD(object);
		writeC(ActionCodes.ACTION_Shop);// 動作編號
		writeS(message);// 文字內容
	}

	@Override
	public byte[] getContent() {
		return getBytes();
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
