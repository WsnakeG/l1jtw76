package com.lineage.server.timecontroller.npc;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * NPC(BOSS)召喚時間時間軸
 * 
 * @author dexc
 */
public class NpcSpawnBossTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcSpawnBossTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1NpcInstance, Long> MAP = new ConcurrentHashMap<L1NpcInstance, Long>();

	/*
	 * private static final ArrayList<L1NpcInstance> REMOVE = new
	 * ArrayList<L1NpcInstance>();
	 */

	public void start() {
		final int timeMillis = 60 * 1000;// 1分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 不包含元素
			if (MAP.isEmpty()) {
				return;
			}
			for (final L1NpcInstance npc : MAP.keySet()) {
				final Long time = MAP.get(npc);
				final long t = time - 60;

				if (time > 0) {
					// 更新時間
					MAP.put(npc, t);

				} else {
					// 召喚
					spawn(npc);
					MAP.remove(npc);
				}
				Thread.sleep(1);
			}

			// 移出清單不包含元素
			/*
			 * if (REMOVE.isEmpty()) { return; } for (final L1NpcInstance npc :
			 * REMOVE) { // 召喚 spawn(npc); MAP.remove(npc); Thread.sleep(1); }
			 */

		} catch (final Exception e) {
			_log.error("NPC(BOSS)召喚時間時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcSpawnBossTimer bossTimer = new NpcSpawnBossTimer();
			bossTimer.start();

		} finally {
			// ListMapUtil.clear(REMOVE);
		}
	}

	/**
	 * 召喚BOSS
	 * 
	 * @param npc
	 */
	private static void spawn(final L1NpcInstance npc) {
		try {
			npc.getSpawn().executeSpawnTask(npc.getSpawnNumber(), npc.getId());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
