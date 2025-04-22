package com.lineage.server.timecontroller.npc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldMob;

/**
 * Npc MP自然回復時間軸(對怪物)
 * 
 * @author dexc
 */
public class NpcMprTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcMprTimer.class);

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

			final Collection<L1MonsterInstance> allMob = WorldMob.get().all();
			// 不包含元素
			if (allMob.isEmpty()) {
				return;
			}

			for (final Iterator<L1MonsterInstance> iter = allMob.iterator(); iter.hasNext();) {
				final L1MonsterInstance mob = iter.next();
				// HP是否具備回復條件
				if (mob.isMpR()) {
					mpUpdate(mob);
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("Npc MP自然回復時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcMprTimer npcMprTimer = new NpcMprTimer();
			npcMprTimer.start();
		}
	}

	/**
	 * 判斷是否執行回復
	 * 
	 * @param mob
	 */
	private static void mpUpdate(final L1MonsterInstance mob) {
		int mprInterval = mob.getNpcTemplate().get_mprinterval();
		// 無特別指定時間 每30秒回復一次
		if (mprInterval <= 0) {
			mprInterval = 30;
		}

		if ((_time % mprInterval) == 0) {
			// 無特別指定回復量 每次回復1
			int mpr = mob.getNpcTemplate().get_mpr();
			if (mpr <= 0) {
				mpr = 1;
			}

			mprInterval(mob, mpr);
		}
	}

	/**
	 * 執行回復MP
	 * 
	 * @param mob
	 * @param mpr
	 */
	private static void mprInterval(final L1MonsterInstance mob, final int mpr) {
		try {
			if (mob.isMpRegenerationX()) {
				mob.setCurrentMp(mob.getCurrentMp() + mpr);
			}

		} catch (final Exception e) {
			_log.error("Npc 執行回復MP發生異常", e);
			mob.deleteMe();
		}
	}
}
