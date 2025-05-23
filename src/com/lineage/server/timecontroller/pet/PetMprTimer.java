package com.lineage.server.timecontroller.pet;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldPet;

/**
 * Pet MP自然回復時間軸
 * 
 * @author dexc
 */
public class PetMprTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PetMprTimer.class);

	private ScheduledFuture<?> _timer;

	private static int _time = 0;

	public void start() {
		_time = 0;
		final int timeMillis = 1000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			_time++;

			final Collection<L1PetInstance> allPet = WorldPet.get().all();
			// 不包含元素
			if (allPet.isEmpty()) {
				return;
			}

			for (final Iterator<L1PetInstance> iter = allPet.iterator(); iter.hasNext();) {
				final L1PetInstance pet = iter.next();
				if (MprPet.mpUpdate(pet, _time)) {
					Thread.sleep(5);
				}
			}

		} catch (final Exception e) {
			_log.error("Pet MP自然回復時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PetMprTimer petMprTimer = new PetMprTimer();
			petMprTimer.start();
		}
	}
}
