package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.BroadcastController;
import com.lineage.server.templates.L1Event;

/**
 * 廣播系統
 * 
 * @author terry0412
 */
public class BroadcastSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(BroadcastSet.class);

	public static int ITEM_ID; // 廣播卡道具編號

	/**
	 *
	 */
	private BroadcastSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new BroadcastSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			ITEM_ID = Integer.parseInt(set[0]);

			// 廣播系統時間軸 by terry0412
			BroadcastController.getInstance().start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
