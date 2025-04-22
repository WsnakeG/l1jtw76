package com.lineage.data.item_etcitem.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * shop.TimeUpdate1 時效復原(使用時 具有時間類型 設置在armor / weapon 裡面的 max_use_time欄位)<BR>
 * 設置本CLASS的物品 use_type 建議指定為 choice
 * 
 * @author dexc
 */
public class TimeUpdate1 extends ItemExecutor {

	private static final Log _log = LogFactory.getLog(TimeUpdate1.class);

	/**
	 *
	 */
	private TimeUpdate1() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TimeUpdate1();
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
			if (tgItem.getItem().getMaxUseTime() <= 0) {
				// 沒有任何事情發生。
				pc.sendPackets(new S_ServerMessage(79));

			} else {
				// 刪除物件
				pc.getInventory().removeItem(item, 1);

				final int time = tgItem.getItem().getMaxUseTime();

				tgItem.setRemainingTime(time);
				pc.getInventory().updateItem(tgItem, L1PcInventory.COL_REMAINING_TIME);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
