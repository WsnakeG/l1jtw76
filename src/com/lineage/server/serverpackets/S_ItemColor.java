package com.lineage.server.serverpackets;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 物品色彩狀態
 * 
 * @author dexc
 */
public class S_ItemColor extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品色彩狀態
	 */
	public S_ItemColor(final L1ItemInstance item) {
		if (item == null) {
			return;
		}
		buildPacket(item);
	}

	private void buildPacket(final L1ItemInstance item) {
		writeC(S_OPCODE_ITEMCOLOR);
		writeD(item.getId());
		// 0:祝福 1:通常 2:呪い 3:未鑑定
		// 128:祝福&封印 129:&封印 130:呪い&封印 131:未鑑定&封印
		writeC(item.getBless());
	}

	/**
	 * 物品色彩狀態 - 測試用
	 * 
	 * @param item
	 * @param id
	 */
	/*
	 * public S_ItemColor(L1PcInstance _pc, final L1ItemInstance item) {
	 * this.writeC(S_OPCODE_ITEMCOLOR); this.writeD(item.getId()); // 0:祝福 1:通常
	 * 2:呪い 3:未鑑定 // 128:祝福&封印 129:&封印 130:呪い&封印 131:未鑑定&封印 this.writeC(id); }
	 */

	/**
	 * 物品色彩狀態 - 測試用
	 * 
	 * @param item
	 * @param id
	 */
	public S_ItemColor(final L1ItemInstance item, final int id) {
		writeC(S_OPCODE_ITEMCOLOR);
		writeD(item.getId());
		// 0:祝福 1:通常 2:呪い 3:未鑑定
		// 128:祝福&封印 129:&封印 130:呪い&封印 131:未鑑定&封印
		writeC(id);
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
