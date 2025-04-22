package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemStatus;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 凹槽歸零卡 (更改效果)
 * 
 * @author terry0412
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

		if (tgItem.isEquipped()) {
			pc.sendPackets(new S_SystemMessage("你必須先解除物品裝備!"));
			return;
		}

		// 取得凹槽紀錄
		final L1ItemPowerHole_name power = tgItem.get_power_name_hole();
		if (power == null || power.get_hole_count() <= 0) {
			pc.sendPackets(new S_SystemMessage("沒有經過開孔的武器或防具，所以無法使用。"));
			return;
		}
		pc.getInventory().removeItem(item, 1);

		// 歸零凹槽
		power.set_hole_1(0);
		power.set_hole_2(0);
		power.set_hole_3(0);
		power.set_hole_4(0);
		power.set_hole_5(0);

		// 更新紀錄
		CharItemPowerHoleReading.get().storeItem(tgItem.getId(), tgItem.get_power_name_hole());

		pc.sendPackets(new S_ItemStatus(tgItem));
		pc.sendPackets(new S_SystemMessage("\\aD你已經將所有凹槽的能力清除，可重新賦予！"));
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), _gfxid_s));
	}

	private int _gfxid_s; // 成功特效編號

	@Override
	public void set_set(String[] set) {
		try {
			_gfxid_s = Integer.parseInt(set[1]);

		} catch (Exception e) {
		}
	}
}
