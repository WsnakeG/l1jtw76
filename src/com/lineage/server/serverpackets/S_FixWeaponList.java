package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.List;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 損壞武器名單
 * 
 * @author dexc
 */
public class S_FixWeaponList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * 損壞武器名單
	 * 
	 * @param weaponList 清單
	 */
	public S_FixWeaponList(final List<L1ItemInstance> weaponList) {
		writeC(S_OPCODE_SELECTLIST);
		writeD(0x000000c8); // Price

		writeH(weaponList.size()); // Weapon Amount

		for (final L1ItemInstance weapon : weaponList) {
			writeD(weapon.getId()); // Item ID
			writeC(weapon.get_durability()); // Fix Level
		}
	}

	/**
	 * 損壞武器名單
	 * 
	 * @param pc 執行人物
	 */
	public S_FixWeaponList(final L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(final L1PcInstance pc) {
		writeC(S_OPCODE_SELECTLIST);
		writeD(0x000000c8); // Price

		final List<L1ItemInstance> weaponList = new ArrayList<L1ItemInstance>();
		final List<L1ItemInstance> itemList = pc.getInventory().getItems();
		for (final L1ItemInstance item : itemList) {

			// Find Weapon
			switch (item.getItem().getType2()) {
			case 1:
				if (item.get_durability() > 0) {
					weaponList.add(item);
				}
				break;
			}
		}

		writeH(weaponList.size()); // Weapon Amount

		for (final L1ItemInstance weapon : weaponList) {

			writeD(weapon.getId()); // Item ID
			writeC(weapon.get_durability()); // Fix Level
		}
	}

	/**
	 * 損壞武器名單 - 測試
	 */
	public S_FixWeaponList(final L1ItemInstance weapon) {
		writeC(S_OPCODE_SELECTLIST);
		writeD(0x000000c8); // Price

		writeH(1); // Weapon Amount

		writeD(weapon.getId()); // Item ID
		writeC(weapon.get_durability()); // Fix Level
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