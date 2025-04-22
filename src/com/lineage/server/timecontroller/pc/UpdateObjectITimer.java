package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldIllusionist;

/**
 * PC 可見物更新處理 時間軸(幻術)
 * 
 * @author dexc
 */
public class UpdateObjectITimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(UpdateObjectITimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 350;// 0.35秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = WorldIllusionist.get().all();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (UpdateObjectCheck.check(tgpc)) {
					tgpc.updateObject();
				}
			}

			/*
			 * for (final L1PcInstance iter : allPc) { if
			 * (UpdateObjectCheck.check(iter)) { iter.updateObject(); } }
			 */

		} catch (final Exception e) {
			_log.error("Pc 可見物更新處理時間軸(幻術)異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final UpdateObjectITimer objectITimer = new UpdateObjectITimer();
			objectITimer.start();
		}
	}
}
