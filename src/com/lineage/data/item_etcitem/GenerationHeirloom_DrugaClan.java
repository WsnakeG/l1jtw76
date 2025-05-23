package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>多魯嘉1世傳家之寶40623</font><BR>
 * 1st Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉2世傳家之寶40624</font><BR>
 * 2nd Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉3世傳家之寶40625</font><BR>
 * 3rd Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉4世傳家之寶40626</font><BR>
 * 4th Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉5世傳家之寶40627</font><BR>
 * 5th Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉6世傳家之寶40628</font><BR>
 * 6th Generation Heirloom of the Druga Clan<BR>
 * <font color=#00800>多魯嘉7世傳家之寶40629</font><BR>
 * 7th Generation Heirloom of the Druga Clan<BR>
 * 
 * @author dexc
 */
public class GenerationHeirloom_DrugaClan extends ItemExecutor {

	/**
	 *
	 */
	private GenerationHeirloom_DrugaClan() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GenerationHeirloom_DrugaClan();
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
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:// 金幣
		case 53:
		case 54:
		case 62:
		case 63:
		case 26:
		case 27:
		case 89:
		case 31:
		case 80:
		case 81:
		case 36:
		case 37:
			item_id = 40308;
			count = 1000000;
			break;

		case 52:
		case 55:
		case 56:
		case 57:
		case 17:
		case 18:
		case 19:
		case 20:// 古代水龍鱗盔甲
			item_id = 20153;
			break;

		case 60:
		case 61:
		case 64:
		case 65:
		case 21:
		case 23:
		case 24:
		case 25:// 古代地龍鱗盔甲
			item_id = 20130;
			break;

		case 68:
		case 69:
		case 70:
		case 71:
		case 72:
		case 28:
		case 29:
		case 30:// 古代火龍鱗盔甲
			item_id = 20119;
			break;

		case 84:
		case 85:
		case 86:
		case 87:
		case 88:
		case 32:
		case 34:
		case 35:// 古代風龍鱗盔甲
			item_id = 20108;
			break;

		case 76:
		case 77:
		case 78:
		case 79:
		case 38:
		case 39:// 屠龍劍
			item_id = 66;
			break;

		case 92:
		case 93:
		case 94:
		case 95:
		case 96:
		case 97:
		case 40:
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:// 無法得知的傳家之寶
			item_id = 40666;
			break;

		case 46:
		case 47:
		case 48:
		case 73:
		case 16:
		case 49:
		case 22:
		case 33:
		case 50:
		case 51:// 古代的卷軸
			item_id = 40076;
			break;

		case 58:
		case 59:// 萬能藥(力量)
			item_id = 40033;
			break;

		case 66:
		case 67:// 萬能藥(敏捷)
			item_id = 40035;
			break;

		case 74:
		case 75:// 萬能藥(體質)
			item_id = 40034;
			break;

		case 82:
		case 83:// 萬能藥(智慧)
			item_id = 40036;
			break;

		case 90:
		case 91:// 萬能藥(精神)
			item_id = 40037;
			break;

		case 98:
		case 99:// 萬能藥(魅力)
			item_id = 40038;
			break;
		}

		// 刪除道具
		pc.getInventory().removeItem(item, 1);

		// 取得道具
		CreateNewItem.createNewItem(pc, item_id, count);
	}
}
