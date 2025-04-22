package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法娃娃的袋子41247</font><BR>
 * 
 * @author dexc
 */
public class Box_Doll extends ItemExecutor {

	/**
	 *
	 */
	private Box_Doll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Box_Doll();
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

		switch (k) {
		case 0:
			item_id = 55000;// 魔法娃娃：肥肥
			break;

		case 1:
			item_id = 55001;// 魔法娃娃：小思克巴
			break;

		case 2:
			item_id = 55002;// 魔法娃娃：野狼寶寶
			break;

		case 3:
			item_id = 55010;// 魔法娃娃：長者
			break;

		case 4:
			item_id = 55011;// 魔法娃娃：奎斯坦修
			break;

		case 5:
			item_id = 55012;// 魔法娃娃：石頭高侖
			break;

		case 6:
			item_id = 55006;// 魔法娃娃：雪怪
			break;

		case 7:
			item_id = 55007;// 魔法娃娃：蛇女
			break;

		case 8:
			item_id = 55009;// 魔法娃娃：史巴托
			break;

		case 9:
			item_id = 55013;// 魔法娃娃：亞利安
			break;

		}

		if (item_id != 0) {
			CreateNewItem.createNewItem(pc, item_id, 1);
		}
	}
}
