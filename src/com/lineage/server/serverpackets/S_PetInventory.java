package com.lineage.server.serverpackets;

import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PetInstance;

/**
 * 物品名單(寵物背包)
 * 
 * @author dexc
 */
public class S_PetInventory extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 物品名單(寵物背包)
	 * 
	 * @param pet
	 */
	/*
	 * public S_PetInventory(final L1PetInstance pet) { final
	 * List<L1ItemInstance> itemList = pet.getInventory().getItems();
	 * this.writeC(S_OPCODE_SHOWRETRIEVELIST); this.writeD(pet.getId());
	 * this.writeH(itemList.size()); this.writeC(0x0b); for (final
	 * L1ItemInstance item : itemList) { if (item != null) {
	 * this.writeD(item.getId()); this.writeC(0x13);
	 * this.writeH(item.get_gfxid()); this.writeC(item.getBless());
	 * this.writeD((int) Math.min(item.getCount(), 2000000000));
	 * this.writeC(item.isIdentified() ? 1 : 0);
	 * this.writeS(item.getViewName()); } } this.writeC(0x0a); }
	 */

	/**
	 * 物品名單(寵物背包)
	 * 
	 * @param pet
	 * @param b 寵物是否剛進入
	 */
	public S_PetInventory(final L1PetInstance pet, final boolean b) {
		isTrue(pet);
	}

	private void isTrue(final L1PetInstance pet) {
		final List<L1ItemInstance> itemList = pet.getInventory().getItems();

		writeC(S_OPCODE_SHOWRETRIEVELIST);
		writeD(pet.getId());
		writeH(itemList.size());
		writeC(0x0b);
		for (final L1ItemInstance item : itemList) {
			if (item != null) {
				writeD(item.getId());
				writeC(0x16);
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD((int) Math.min(item.getCount(), 2000000000));
				writeC(item.isIdentified() ? 1 : 0);
				writeS(item.getViewName());
			}
		}
		writeC(0x0a);
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
