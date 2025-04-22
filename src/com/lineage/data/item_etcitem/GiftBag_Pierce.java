package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>皮爾斯的禮物40715</font><BR>
 * Gift Bag from Pierce<BR>
 * 
 * @author dexc
 */
public class GiftBag_Pierce extends ItemExecutor {

	/**
	 *
	 */
	private GiftBag_Pierce() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GiftBag_Pierce();
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
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:// 青銅 鋼爪 1 個
			item_id = 152;
			break;

		case 6:
		case 7:
		case 8:
		case 9:
		case 10:// 青銅 雙刀 1 個
			item_id = 69;
			break;

		case 11:
		case 12:
		case 13:
		case 14:
		case 15:// 鋼鐵 鋼爪 1 個
			item_id = 153;
			break;

		case 16:
		case 17:
		case 18:
		case 19:// 鋼鐵 雙刀 1 個
			item_id = 71;
			break;

		case 21:
		case 22:
		case 23:
		case 24:// 暗影 鋼爪 1 個
			item_id = 154;
			break;

		case 26:
		case 27:
		case 28:
		case 29:// 暗影 雙刀 1 個
			item_id = 72;
			break;

		case 31:
		case 32:
		case 33:
		case 34:// 短 鋼爪 1 個
			item_id = 159;
			break;

		case 36:
		case 37:
		case 38:
		case 39:// 短 雙刀 1 個
			item_id = 77;
			break;

		case 41:
		case 42:
		case 43:
		case 44:// 大馬士革 鋼爪 1 個
			item_id = 161;
			break;

		case 46:
		case 47:
		case 48:
		case 49:// 大馬士革 雙刀 1 個
			item_id = 80;
			break;

		case 51:
		case 52:
		case 53:
		case 54:// 黑暗 鋼爪 1 個
			item_id = 158;
			break;

		case 56:
		case 57:
		case 58:
		case 59:// 黑暗 雙刀 1 個
			item_id = 75;
			break;

		case 61:
		case 62:
		case 63:
		case 64:// 黑暗 十字弓 1 個
			item_id = 168;
			break;

		case 86:
		case 87:
		case 88:
		case 89:// 幽暗 鋼爪 1 個
			item_id = 162;
			break;

		case 66:
		case 67:
		case 68:
		case 69:// 幽暗 雙刀 1 個
			item_id = 81;
			break;

		case 91:
		case 92:
		case 93:
		case 94:// 幽暗 十字弓 1 個
			item_id = 177;
			break;

		case 71:
		case 72:
		case 73:
		case 74:// 飛刀 1000 個
			item_id = 40739;
			count = 1000;
			break;

		case 76:
		case 77:
		case 78:
		case 79:// 銀飛刀 1000 個
			item_id = 40738;
			count = 1000;
			break;

		case 81:
		case 82:
		case 83:
		case 84:// 重飛刀 500 個
			item_id = 40740;
			count = 500;
			break;

		case 96:
		case 97:
		case 98:
		case 99:// 黑暗 頭飾 1 個
			item_id = 20032;
			break;

		case 85:
		case 80:
		case 75:
		case 95:// 黑暗 斗篷 1 個
			item_id = 20070;
			break;

		case 70:
		case 90:
		case 65:
		case 60:// 黑暗 手套 1 個
			item_id = 20180;
			break;

		case 55:
		case 50:
		case 45:
		case 40:// 黑暗 披肩 1 個
			item_id = 20132;
			break;

		default:// 黑暗長靴 1 個 20210
			item_id = 20210;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
