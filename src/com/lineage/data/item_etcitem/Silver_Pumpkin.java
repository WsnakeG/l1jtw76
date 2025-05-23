package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>銀南瓜40723</font><BR>
 * Silver Pumpkin
 * 
 * @author dexc
 */
public class Silver_Pumpkin extends ItemExecutor {

	/**
	 *
	 */
	private Silver_Pumpkin() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Silver_Pumpkin();
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
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:// 濃縮終極體力恢復劑
			item_id = 40021;
			count = 9;
			break;

		case 12:
		case 13:
		case 14:
		case 15:
		case 16:// 古代終極體力恢復劑
			item_id = 40024;
			count = 20;
			break;

		case 17:
		case 18:
		case 19:
		case 20:
		case 21:// 加速魔力恢復藥水
			item_id = 40015;
			count = 20;
			break;

		case 22:
		case 23:
		case 24:
		case 25:
		case 26:// 精靈餅乾
			item_id = 40068;
			count = 5;
			break;

		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
		case 36:// 空的魔法卷軸(等級4)
			item_id = 40093;
			count = 8;
			break;

		case 37:
		case 38:
		case 39:
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:// 空的魔法卷軸(等級5)
			item_id = 40094;
			count = 8;
			break;

		case 47:
		case 48:
		case 49:
		case 50:
		case 51:// 黑色血痕
			item_id = 40524;
			break;

		case 52:
		case 53:
		case 54:
		case 55:
		case 56:
		case 57:
		case 58:
		case 59:// 對盔甲施法的卷軸
			item_id = 40074;
			break;

		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 65:
		case 66:// 對武器施法的卷軸
			item_id = 40087;
			break;

		case 67:
		case 68:
		case 69:
		case 70:
		case 71:// 魔法書(變形術)
			item_id = 40212;
			break;

		case 72:
		case 73:
		case 74:
		case 75:
		case 76:// 魔法書 (創造魔法武器)
			item_id = 40218;
			break;

		case 77:
		case 78:
		case 79:
		case 80:
		case 81:// 精靈餅乾(祝福)
			item_id = 140068;
			break;

		case 82:
		case 83:// 匕首(祝福)
			item_id = 100004;
			break;

		case 84:
		case 85:// 銀劍(祝福)
			item_id = 100025;
			break;

		case 86:
		case 87:// 武官雙手劍(祝福)
			item_id = 100062;
			break;

		case 88:
		case 89:// 精靈之矛(祝福)
			item_id = 100099;
			break;

		case 90:
		case 91:// 神官魔杖(祝福)
			item_id = 100132;
			break;

		case 92:
		case 93:// 獵人之弓(祝福)
			item_id = 100169;
			break;

		case 94:
		case 95:// 抗魔法頭盔(祝福)
			item_id = 120011;
			break;

		case 96:
		case 97:// T恤(祝福)
			item_id = 120085;
			break;

		default:// 精靈鏈甲(祝福)
			item_id = 120137;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
