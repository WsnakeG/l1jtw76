package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 凹槽擴建 by Roy 可針對道具擴充一個指定的欄位
 */
public class Create_Hole extends ItemExecutor {

	/**
	 *
	 */
	private Create_Hole() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Create_Hole();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 對象OBJID
		final int targObjId = data[0];

		// 目標物品
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}

		if (tgItem.isEquipped()) {
			pc.sendPackets(new S_ServerMessage("\\aD你必須先解除物品裝備!"));
			return;
		}

		pc.getInventory().removeItem(item, 1);
		L1ItemPowerHole_name power = null;
		boolean update = false;

		// 凹槽誕生
		switch (tgItem.getItem().getUseType()) {
		case 1:// 武器
			if (tgItem.get_power_name_hole() != null) {
				power = tgItem.get_power_name_hole();
				update = true;
			} else {
				power = new L1ItemPowerHole_name();
			}
			break;
		case 2:// 盔甲
		case 18:// T恤
		case 19:// 斗篷
		case 20:// 手套
		case 21:// 靴
		case 22:// 頭盔
		case 25:// 盾牌
			if (tgItem.get_power_name_hole() != null) {
				power = tgItem.get_power_name_hole();
				update = true;
			} else {
				power = new L1ItemPowerHole_name();
			}
			break;
		}
		if (power != null) {
			power.set_item_obj_id(tgItem.getId());
			power.set_hole_count(_Power_count);
			tgItem.set_power_name_hole(power);

			if (update) {
				CharItemPowerHoleReading.get().updateItem(tgItem.getId(), tgItem.get_power_name_hole());

			} else {
				CharItemPowerHoleReading.get().storeItem(tgItem.getId(), tgItem.get_power_name_hole());
			}
			pc.sendPackets(new S_ItemName(tgItem));
			pc.sendPackets(new S_ItemStatus(tgItem));
		}
	}

	private int _Power_count;

	@Override
	public void set_set(final String[] set) {
		try {
			_Power_count = Integer.parseInt(set[1]);
		} catch (final Exception e) {
		}
	}
}
