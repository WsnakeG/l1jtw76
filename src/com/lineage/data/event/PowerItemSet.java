package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.templates.L1Event;

/**
 * 古文字系統 DELETE FROM `server_event` WHERE `id`='39'; INSERT INTO `server_event`
 * VALUES ('39', '古文字系統', 'PowerItemSet', '1', '0', '說明:啟動古文字系統');
 * 
 * @author dexc
 */
public class PowerItemSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(PowerItemSet.class);

	// 古文字系統
	public static boolean START = false;

	/**
	 *
	 */
	private PowerItemSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new PowerItemSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			START = true;

			ItemPowerTable.get().load();// 古文字資料

			// 人物古文字物品資料
			// CharItemPowerReading.get().load();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}