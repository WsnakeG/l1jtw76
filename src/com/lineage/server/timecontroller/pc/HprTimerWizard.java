package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldWizard;

/**
 * PC HP回復時間軸(法師)
 * 
 * @author dexc
 */
public class HprTimerWizard extends TimerTask {

	private static final Log _log = LogFactory.getLog(HprTimerWizard.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = WorldWizard.get().all();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				final HprExecutor hpr = HprExecutor.get();
				if (hpr.check(tgpc)) {
					hpr.checkRegenHp(tgpc);
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("Pc(法師) HP自然回復時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final HprTimerWizard hprWizard = new HprTimerWizard();
			hprWizard.start();
		}
	}
}
