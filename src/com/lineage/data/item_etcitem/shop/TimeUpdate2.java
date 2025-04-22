package com.lineage.data.item_etcitem.shop;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1ItemTime;

/**
 * shop.TimeUpdate2 時效復原(使用時間限制類型 - 使用時間限制 設置在server_item_time裡面的)<BR>
 * 設置本CLASS的物品 use_type 建議指定為 choice
 * 
 * @author dexc
 */
public class TimeUpdate2 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(TimeUpdate2.class);

	/**
	 *
	 */
	private TimeUpdate2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TimeUpdate2();
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
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}

		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		try {
			// 對象OBJID
			final int targObjId = data[0];

			final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
			if (tgItem == null) {
				return;
			}

			// 武器/防具 具有使用時間
			if (tgItem.get_time() == null) {
				// 沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));

			} else {
				final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
				if (itemTime != null) {
					// 刪除物件
					pc.getInventory().removeItem(item, 1);

					// 目前時間 加上指定天數耗用秒數
					final long upTime = System.currentTimeMillis() + (itemTime.get_remain_time() * 60 * 1000);
					// 時間數據
					final Timestamp ts = new Timestamp(upTime);
					item.set_time(ts);

					// 人物背包物品使用期限資料
					CharItemsTimeReading.get().updateTime(tgItem.getId(), ts);
					pc.sendPackets(new S_ItemName(tgItem));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
