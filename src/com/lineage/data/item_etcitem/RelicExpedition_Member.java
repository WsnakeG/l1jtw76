package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>遠征隊的遺物(1F)(2F)40696</font><BR>
 * Relic Expedition Member
 * 
 * @author dexc
 */
public class RelicExpedition_Member extends ItemExecutor {

	/**
	 *
	 */
	private RelicExpedition_Member() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new RelicExpedition_Member();
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

		final int k = (int) (Math.random() * 6);// 隨機數字範圍0~5

		switch (k) {
		// 1F
		case 0:
			item_id = 40682;// 污濁的腕甲
			break;

		case 1:
			item_id = 40681;// 污濁的鋼靴
			break;

		case 2:
			item_id = 40680;// 污濁斗篷
			break;

		// 2F
		case 3:
			item_id = 40684;// 污濁的弓
			break;

		case 4:
			item_id = 40679;// 污濁的金甲
			break;

		case 5:
			item_id = 40683;// 污濁的頭盔
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
