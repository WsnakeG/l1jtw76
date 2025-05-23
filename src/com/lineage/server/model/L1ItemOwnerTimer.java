package com.lineage.server.model;

import java.util.Timer;
import java.util.TimerTask;

import com.lineage.server.model.Instance.L1ItemInstance;

public class L1ItemOwnerTimer extends TimerTask {
	/*
	 * private static final Log _log = LogFactory.getLog(L1ItemOwnerTimer.class
	 * .getName());
	 */

	public L1ItemOwnerTimer(final L1ItemInstance item, final int timeMillis) {
		_item = item;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		_item.setItemOwnerId(0);
		cancel();
	}

	public void begin() {
		final Timer timer = new Timer();
		timer.schedule(this, _timeMillis);
	}

	private final L1ItemInstance _item;
	private final int _timeMillis;
}
