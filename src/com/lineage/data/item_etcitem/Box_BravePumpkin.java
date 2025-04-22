package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>勇者的南瓜袋子41308</font><BR>
 * 
 * @author dexc
 */
public class Box_BravePumpkin extends ItemExecutor {

	/**
	 *
	 */
	private Box_BravePumpkin() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Box_BravePumpkin();
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

		final int k = (int) (Math.random() * 10);// 隨機數字範圍0~9

		int item_id = 0;
		int count = 1;

		switch (k) {
		case 0:
			item_id = 41251;// 骷髏聖盃
			break;

		case 1:
			item_id = 40010;// 紅色藥水(治癒藥水)
			count = 30;
			break;

		case 2:
		case 3:
			item_id = 40010;// 紅色藥水(治癒藥水)
			count = 15;
			break;

		case 4:
		case 5:
			item_id = 40011;// 橙色藥水(強力治癒藥水)
			count = 15;
			break;

		case 6:
		case 7:
			item_id = 40012;// 白色藥水(終極治癒藥水)
			count = 15;
			break;

		case 8:
		case 9:
			item_id = 40088;// 變形卷軸
			count = 6;
			break;
		}

		if (item_id != 0) {
			// 取得道具
			CreateNewItem.createNewItem(pc, item_id, count);
		}
	}
}
