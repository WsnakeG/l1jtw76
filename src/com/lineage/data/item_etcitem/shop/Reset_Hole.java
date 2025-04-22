package com.lineage.data.item_etcitem.shop;

import com.lineage.data.event.PowerItemHoleSet;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 56056
 */
public class Reset_Hole extends ItemExecutor {

	/**
	 *
	 */
	private Reset_Hole() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Reset_Hole();
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

		L1ItemPowerHole_name power = null;
		boolean update = false;
		int count = 0;
		// 凹槽誕生
		switch (tgItem.getItem().getUseType()) {
		case 1:// 武器
			count = PowerItemHoleSet.WEAPONHOLE;
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
			count = PowerItemHoleSet.ARMORHOLE;
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
			power.set_hole_count(count);
			power.set_hole_1(0);
			power.set_hole_2(0);
			power.set_hole_3(0);
			power.set_hole_4(0);
			power.set_hole_5(0);
			tgItem.set_power_name_hole(power);

			if (update) {
				CharItemPowerHoleReading.get().updateItem(tgItem.getId(), tgItem.get_power_name_hole());

			} else {
				CharItemPowerHoleReading.get().storeItem(tgItem.getId(), tgItem.get_power_name_hole());
			}
			pc.getInventory().removeItem(item, 1);
			pc.sendPackets(new S_ItemName(tgItem));
			pc.sendPackets(new S_ItemStatus(tgItem));
		}
	}
}
