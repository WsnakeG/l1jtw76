package com.lineage.data.item_etcitem.shop;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public class Marbin_Package extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(Marbin_Package.class);

	private Marbin_Package() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Marbin_Package();
	}

	/**
	 * 寶箱福袋(保底) Roy 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	// @Override
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		try {
			// 例外狀況:物件為空
			if (item == null) {
				return;
			}

			// 例外狀況:人物為空
			if (pc == null) {
				return;
			}

			final int charge = item.getChargeCount() - 1;
			if (charge <= 0) {
				// 移除袋子

				final int i = (int) (Math.random() * 5);// 隨機數字範圍0~9

				int item_id = 0;

				switch (i) {
				case 0:
					item_id = 44070;// 魔法娃娃：肥肥
					break;

				case 1:
					item_id = 44070;// 魔法娃娃：小思克巴
					break;

				case 2:
					item_id = 44070;// 魔法娃娃：野狼寶寶
					break;

				case 3:
					item_id = 44070;// 魔法娃娃：長者
					break;

				case 4:
					item_id = 44070;// 魔法娃娃：奎斯坦修
					break;

				}
				CreateNewItem.createNewItem(pc, item_id, 1);
				pc.getInventory().removeItem(item);

			} else {
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

			if (item != null) {
				item.setChargeCount(charge);
				// 設置延遲使用機制
				final Timestamp ts = new Timestamp(System.currentTimeMillis());
				item.setLastUsed(ts);
				pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
				pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}