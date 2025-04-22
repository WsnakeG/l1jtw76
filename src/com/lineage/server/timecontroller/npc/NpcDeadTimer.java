package com.lineage.server.timecontroller.npc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldNpc;

/**
 * NPC死亡時間軸
 * 
 * @author dexc
 */
public class NpcDeadTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcDeadTimer.class);

	public void start() {
		final int timeMillis = 1000;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	private static NpcDeadTimer _instance;

	private ScheduledFuture<?> _timer;

	public static NpcDeadTimer get() {
		if (_instance == null) {
			_instance = new NpcDeadTimer();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			final Collection<L1NpcInstance> allMob = WorldNpc.get().all();
			// 不包含元素
			if (allMob.isEmpty()) {
				return;
			}

			for (final Iterator<L1NpcInstance> iter = allMob.iterator(); iter.hasNext();) {
				final L1NpcInstance npc = iter.next();
				if (npc == null) {
					continue;
				}
				if (npc.getMaxHp() <= 0) {
					continue;
				}
				// 未死亡(復活可能)
				if (!npc.isDead()) {
					continue;
				}
				if (npc.get_deadTimerTemp() == -1) {
					continue;
				}
				final int time = npc.get_deadTimerTemp();
				npc.set_deadTimerTemp(time - 1);

				if (npc.get_deadTimerTemp() <= 0) {
					npc.deleteMe();
				}
				Thread.sleep(10);
			}

		} catch (final Exception e) {
			_log.error("NPC死亡時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcDeadTimer npcDeadTimer = new NpcDeadTimer();
			npcDeadTimer.start();
		}
	}
}
