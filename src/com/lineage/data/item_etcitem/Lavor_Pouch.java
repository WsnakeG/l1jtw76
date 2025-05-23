package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>拉伯勒的袋子41006</font><BR>
 * Lavor's Pouch
 * 
 * @author dexc
 */
public class Lavor_Pouch extends ItemExecutor {

	/**
	 *
	 */
	private Lavor_Pouch() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Lavor_Pouch();
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

		int item_id = 0;

		final int count = 1;

		final int k = (int) (Math.random() * 5);// 隨機數字範圍0~4

		switch (k) {
		case 0:// 空的魔法卷軸(等級1)
			item_id = 40090;
			break;

		case 1:// 空的魔法卷軸(等級2)
			item_id = 40091;
			break;

		case 2:// 空的魔法卷軸(等級3)
			item_id = 40092;
			break;

		case 3:// 空的魔法卷軸(等級4)
			item_id = 40093;
			break;

		default:// 空的魔法卷軸(等級5)
			item_id = 40094;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
