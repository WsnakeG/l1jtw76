package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>卡立普的高級袋子(品鑽)49009</font><BR>
 * High Quality Pouch of Karif (HQD)
 * 
 * @author dexc
 */
public class Caliph_Pouch_HQD extends ItemExecutor {

	/**
	 *
	 */
	private Caliph_Pouch_HQD() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Caliph_Pouch_HQD();
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

		final int k = (int) (Math.random() * 100);// 隨機數字範圍0~99

		switch (k) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:// 治癒藥水
			item_id = 40010;
			count = 20;
			break;

		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:// 濃縮終極體力恢復劑
			item_id = 40021;
			count = 2;
			break;

		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:// 強化自我加速藥水
			item_id = 40018;
			count = 2;
			break;

		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
		case 38:
		case 39:
		case 40:// 勇敢藥水
			item_id = 40014;
			count = 2;
			break;

		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 91:
		case 92:
		case 93:
		case 94:
		case 95:// 精靈玉
			item_id = 40319;
			count = 3;
			break;

		case 46:
		case 47:
		case 48:
		case 49:
		case 50:// 魔法寶石
			item_id = 40318;
			count = 2;
			break;

		case 51:
		case 52:
		case 53:
		case 54:
		case 55:// 金屬塊
			item_id = 40408;
			count = 3;
			break;

		case 56:
		case 57:
		case 58:
		case 59:
		case 60:// 鋼鐵塊
			item_id = 40779;
			count = 2;
			break;

		case 61:
		case 62:
		case 63:
		case 64:
		case 65:// 月光之淚
			item_id = 40428;
			break;

		case 66:
		case 67:
		case 68:
		case 69:
		case 70:// 黃金原石
			item_id = 40489;
			count = 2;
			break;

		case 71:
		case 72:
		case 73:
		case 74:
		case 75:// 純粹的米索莉塊
			item_id = 40494;
			count = 30;
			break;

		case 76:
		case 77:
		case 78:
		case 79:
		case 80:// 馬普勒之石
			item_id = 40304;
			break;

		case 81:
		case 82:
		case 83:
		case 84:
		case 85:// 奇美拉之皮(蛇)
			item_id = 40400;
			break;

		case 86:
		case 87:
		case 88:
		case 89:
		case 90:// 阿西塔基奧的灰燼
			item_id = 40460;
			break;

		case 96:
		case 97:
		case 98:// 食人巨魔的血
			item_id = 40513;
			break;

		default:// 地龍鱗
			item_id = 40396;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
