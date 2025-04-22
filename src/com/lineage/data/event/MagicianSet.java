package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;

/**
 * 魔法師(輔助魔法)系統
 * 
 * @author terry0412
 */
public class MagicianSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(MagicianSet.class);

	public static int ITEM_ID; // 消耗道具編號

	public static int ITEM_COUNT; // 消耗道具數量

	/**
	 *
	 */
	private MagicianSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new MagicianSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {

			final String[] set = event.get_eventother().split(",");

			ITEM_ID = Integer.parseInt(set[0]);

			ITEM_COUNT = Integer.parseInt(set[1]);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
