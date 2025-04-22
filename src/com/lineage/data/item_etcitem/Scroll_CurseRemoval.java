package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;

/**
 * 象牙塔解咒卷軸40097<br>
 * 解除咀咒的卷軸40119<br>
 * 解除咀咒的卷軸(祝福)140119<br>
 * 原住民圖騰 (祝福)140329<br>
 */
public class Scroll_CurseRemoval extends ItemExecutor {

	/**
	 *
	 */
	private Scroll_CurseRemoval() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Scroll_CurseRemoval();
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
		for (final L1ItemInstance tgItem : pc.getInventory().getItems()) {
			// 非詛咒物品
			if (tgItem.getBless() != 2) {
				continue;
			}

			// 非封印装備
			if (tgItem.getBless() >= 128) {
				continue;
			}

			// 對盔甲施法的卷軸
			if (tgItem.getItemId() == 240074) {
				if (item.getBless() != 0) {
					continue;
				}
			}

			// 對武器施法的卷軸
			if (tgItem.getItemId() == 240087) {
				if (item.getBless() != 0) {
					continue;
				}
			}

			// 受詛咒的 幻化石
			if (tgItem.getItemId() == 41216) {
				continue;
			}

			final int id_normal = tgItem.getItemId() - 200000;
			final L1Item template = ItemTable.get().getTemplate(id_normal);

			if (template == null) {
				continue;
			}

			boolean isEun = false;
			// 身上具有該解除詛咒後物品
			if (pc.getInventory().checkItem(id_normal)) {
				// 物品可以堆疊
				if (template.isStackable()) {
					// 刪除身上原有物件
					pc.getInventory().removeItem(tgItem, tgItem.getCount());
					// 給予新物件
					pc.getInventory().storeItem(id_normal, tgItem.getCount());

				} else {
					isEun = true;
				}

				// 身上不具備該物品
			} else {
				isEun = true;
			}

			if (isEun) {
				// System.out.println("isEun");
				tgItem.setBless(1);
				tgItem.setItem(template);
				pc.getInventory().updateItem(tgItem, L1PcInventory.COL_ITEMID + L1PcInventory.COL_BLESS);
				pc.getInventory().saveItem(tgItem, L1PcInventory.COL_ITEMID + L1PcInventory.COL_BLESS);
			}
		}
		pc.getInventory().removeItem(item, 1);
		pc.sendPackets(new S_ServerMessage(155)); // \f1你感觉到似乎有人正在帮助你。
	}
}
