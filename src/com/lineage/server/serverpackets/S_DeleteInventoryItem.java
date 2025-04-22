package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品刪除
 * 
 * @author dexc
 */
public class S_DeleteInventoryItem extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品刪除
	 * 
	 * @param item
	 */
	public S_DeleteInventoryItem(final L1ItemInstance item) {
		writeC(S_OPCODE_DELETEINVENTORYITEM);
		writeD(item.getId());
	}

	public S_DeleteInventoryItem(final int objid) {
		writeC(S_OPCODE_DELETEINVENTORYITEM);
		writeD(objid);
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
