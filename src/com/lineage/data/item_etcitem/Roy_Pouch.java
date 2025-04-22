package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>羅伊的袋子41003</font><BR>
 * Rabla's Pouch
 * 
 * @author dexc
 */
public class Roy_Pouch extends ItemExecutor {

	/**
	 *
	 */
	private Roy_Pouch() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Roy_Pouch();
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

		int count = 1;// 預設給予數量1

		final int k = (int) (Math.random() * 56);// 隨機數字範圍0~54

		switch (k) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			item_id = 40308;// 金幣(1000)
			count = 1000;
			break;

		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
			item_id = 40016;// 慎重藥水(1)
			break;

		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
			item_id = 40015;// 加速魔力恢復藥水(1)
			break;

		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
		case 38:
		case 39:
			item_id = 40089;// 復活卷軸(1)
			break;

		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
			item_id = 40088;// 變形卷軸(1)
			break;

		case 48:
			item_id = 26000;// 精靈銀盔甲
			break;

		case 49:
			item_id = 120317;// 歐吉皮帶(祝福)
			break;

		default:
			item_id = 40074;// 對盔甲施法的卷軸(1)
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
