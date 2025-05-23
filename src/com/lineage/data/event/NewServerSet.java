package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.NewServerTime;

/**
 * 服務器介紹與教學<BR>
 * 
 * @author dexc
 */
public class NewServerSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(NewServerSet.class);

	/**
	 *
	 */
	private NewServerSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new NewServerSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final int time = Integer.parseInt(event.get_eventother());

			final NewServerTime chatTime = new NewServerTime();
			chatTime.start(time);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
