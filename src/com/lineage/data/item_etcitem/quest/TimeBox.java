package com.lineage.data.item_etcitem.quest;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 49312 時空之甕
 */
public class TimeBox extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(TimeBox.class);

	/**
	 *
	 */
	private TimeBox() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TimeBox();
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
		try {
			// 取得道具(時空之玉 * 2)
			// CreateNewItem.createNewItem(pc, 49313, 2);
			// 取得道具 (哈汀的時空之玉 x1, 歐林的時空之玉 x1)
			CreateNewItem.createNewItem(pc, 56214, 1);
			CreateNewItem.createNewItem(pc, 56215, 1);
			// 設置延遲使用機制
			final Timestamp ts = new Timestamp(System.currentTimeMillis());
			item.setLastUsed(ts);
			pc.getInventory().updateItem(item, L1PcInventory.COL_DELAY_EFFECT);
			pc.getInventory().saveItem(item, L1PcInventory.COL_DELAY_EFFECT);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
