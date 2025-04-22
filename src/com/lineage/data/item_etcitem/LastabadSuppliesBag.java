package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>拉斯塔巴德補給袋41243</font><BR>
 * Lastabad Supplies Bag
 * 
 * @author dexc
 */
public class LastabadSuppliesBag extends ItemExecutor {

	/**
	 *
	 */
	private LastabadSuppliesBag() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new LastabadSuppliesBag();
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
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 82:
		case 83:
		case 84:
		case 85:
		case 86:
			item_id = 40308;// 金幣
			count = 300;
			break;

		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 87:
			item_id = 40308;// 金幣
			count = 1000;
			break;

		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
		case 35:
			item_id = 40308;// 金幣
			count = 10000;
			break;

		case 36:
			item_id = 40006;// 松木魔杖(創造怪物魔杖)
			break;

		case 41:
			item_id = 40008;// 楓木魔杖(變身魔杖)
			break;

		case 46:
		case 47:
		case 48:
		case 49:
		case 50:
			item_id = 40044;// 鑽石
			break;

		case 51:
		case 52:
		case 53:
		case 54:
		case 55:
			item_id = 40045;// 紅寶石
			break;

		case 56:
		case 57:
		case 58:
		case 37:
		case 38:
			item_id = 40046;// 藍寶石
			break;

		case 59:
		case 60:
		case 61:
		case 42:
		case 43:
			item_id = 40047;// 綠寶石
			break;

		case 62:
		case 63:
		case 64:
			item_id = 40048;// 品質鑽石
			break;

		case 65:
		case 66:
		case 67:
			item_id = 40049;// 品質紅寶石
			break;

		case 68:
		case 39:
		case 40:
			item_id = 40050;// 品質藍寶石
			break;

		case 69:
		case 44:
		case 45:
			item_id = 40051;// 品質綠寶石
			break;

		case 70:
			item_id = 40052;// 高品質鑽石
			break;

		case 71:
			item_id = 40053;// 高品質紅寶石
			break;

		case 91:
			item_id = 40054;// 高品質藍寶石
			break;

		case 90:
			item_id = 40055;// 高品質綠寶石
			break;

		case 72:
		case 73:
		case 74:
		case 75:
		case 76:
		case 77:
		case 78:
		case 79:
		case 80:
		case 81:
			item_id = 40429;// 大洞穴卷軸碎片
			break;

		case 92:
			item_id = 40441;// 白金原石
			break;

		case 93:
			item_id = 40444;// 黑色米索莉原石
			break;

		case 94:
			item_id = 40468;// 銀原石
			break;

		case 95:
			item_id = 40489;// 黃金原石
			break;

		case 96:
			item_id = 40508;// 奧里哈魯根
			break;

		case 97:
			item_id = 40524;// 黑色血痕
			break;

		case 98:
			item_id = 140074;// 對盔甲施法的卷軸(祝福)
			break;

		case 99:
			item_id = 140087;// 對武器施法的卷軸(祝福)
			break;

		default:
			item_id = 140100;// 瞬間移動卷軸(祝福)
			count = 3;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
