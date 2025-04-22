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
 * Npc HP自然回復時間軸(對怪物)
 * 
 * @author dexc
 */
public class NpcHprTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcHprTimer.class);

	private ScheduledFuture<?> _timer;

	private static int _time = 0;

	public void start() {
		_time = 0;
		final int timeMillis = 1000;// 1.2秒
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
				if (mob.isHpR()) {
					hpUpdate(mob);
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("Npc HP自然回復時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcHprTimer npcHprTimer = new NpcHprTimer();
			npcHprTimer.start();
		}
	}

	/**
	 * 判斷是否執行回復
	 * 
	 * @param mob
	 */
	private static void hpUpdate(final L1MonsterInstance mob) {
		int hprInterval = mob.getNpcTemplate().get_hprinterval();
		// 無特別指定時間 每10秒回復一次
		if (hprInterval <= 0) {
			hprInterval = 10;
		}

		if ((_time % hprInterval) == 0) {
			// 無特別指定回復量 每次回復2
			int hpr = mob.getNpcTemplate().get_hpr();
			if (hpr <= 0) {
				hpr = 2;
			}

			hprInterval(mob, hpr);
		}
	}

	/**
	 * 執行回復HP
	 * 
	 * @param mob
	 * @param hpr
	 */
	private static void hprInterval(final L1MonsterInstance mob, final int hpr) {
		try {
			if (mob.isHpRegenerationX()) {
				mob.setCurrentHp(mob.getCurrentHp() + hpr);
			}

		} catch (final Exception e) {
			_log.error("Npc 執行回復HP發生異常", e);
			mob.deleteMe();
		}
	}
}
