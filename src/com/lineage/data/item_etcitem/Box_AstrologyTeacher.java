package com.lineage.data.item_etcitem;

import java.sql.Timestamp;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>占星術師的甕</font><BR>
 * 
 * @author dexc
 */
public class Box_AstrologyTeacher extends ItemExecutor {

	/**
	 *
	 */
	private Box_AstrologyTeacher() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Box_AstrologyTeacher();
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
		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得 占星術師的靈魂球
		CreateNewItem.createNewItem(pc, 41313, 1);
		// 設置延遲使用機制
		final Timestamp ts = new Timestamp(System.currentTimeMillis());
		item.setLastUsed(ts);
		pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
		pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);
	}
}
