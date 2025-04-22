package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 更新物品顯示名稱(背包)
 * 
 * @author admin
 */
public class S_ItemName extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 更新物品顯示名稱(背包)
	 */
	public S_ItemName(final L1ItemInstance item) {
		if (item == null) {
			return;
		}
		writeC(S_OPCODE_ITEMNAME);
		writeD(item.getId());
		writeS(item.getViewName());
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
