package com.lineage.server.timecontroller.pet;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1HierarchInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldHierarch;

/**
 * 祭司召喚處理時間軸
 * 
 * @author KZK
 */
public class HierarchTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(HierarchTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;// 60秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1HierarchInstance> allHierarch = WorldHierarch.get().all();
			// 不包含元素
			if (allHierarch.isEmpty()) {
				return;
			}

			for (final Iterator<L1HierarchInstance> iter = allHierarch.iterator(); iter.hasNext();) {
				final L1HierarchInstance hierarch = iter.next();
				final int time = hierarch.get_time() - 60;
				if (time <= 0) {
					outHierarch(hierarch);
				} else {
					if (hierarch.isDead()) {
						continue;
					}
					hierarch.set_time(time);
				}
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.error("祭司處理時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final HierarchTimer hierarch_Timer = new HierarchTimer();
			hierarch_Timer.start();
		}
	}

	/**
	 * 移除祭司
	 * 
	 * @param hierarch
	 */
	private static void outHierarch(final L1HierarchInstance hierarch) {
		try {
			if (hierarch != null) {
				if (hierarch._destroyed) {
					return;
				}
				hierarch.deleteMe();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
