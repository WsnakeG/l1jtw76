package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ShopXTable;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.ShopXTime;

/**
 * 託售管理員
 * 
 * @author dexc
 */
public class ShopXSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(ShopXSet.class);

	public static int ADENA;// 手續費

	public static int DATE;// 寄售時間(天)

	public static int MIN;// 最低出售價

	public static int MAX;// 最高出售價

	/**
	 *
	 */
	private ShopXSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new ShopXSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			ADENA = Integer.parseInt(set[0]);

			DATE = Integer.parseInt(set[1]);

			MIN = Integer.parseInt(set[2]);

			MAX = Integer.parseInt(set[3]);

			DwarfShopReading.get().load();

			// 載入禁止拍賣物品資料
			ShopXTable.get().load();

			final ShopXTime timer = new ShopXTime();
			timer.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
