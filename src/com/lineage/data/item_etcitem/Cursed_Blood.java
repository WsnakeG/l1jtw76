package com.lineage.data.item_etcitem;

import java.sql.Timestamp;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>詛咒之血40416</font><BR>
 * Cursed Blood
 * 
 * @see 24小時後，方可再度使用
 * @author dexc
 */
public class Cursed_Blood extends ItemExecutor {

	/**
	 *
	 */
	private Cursed_Blood() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Cursed_Blood();
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

		final int count = 1;// 預設給予數量1

		final short k = (short) (Math.random() * 6);// 隨機數字範圍0~5

		switch (k) {
		case 1:// 惡魔之血
			item_id = 40031;
			break;

		case 2:// 創造怪物魔杖
			item_id = 40006;
			break;

		case 3:// 變身魔杖
			item_id = 40008;
			break;

		case 4:// 驅逐魔杖
			item_id = 40009;
			break;

		case 5:// 黑色血痕
			item_id = 40524;
			break;

		default:// 閃電魔杖
			item_id = 40007;
			break;
		}

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
		// 設置延遲使用機制
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
	}
}
