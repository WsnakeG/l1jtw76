package com.lineage.server.timecontroller.npc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1BoxInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldNpc;

/**
 * @author terry0412
 */
public class NpcBoxRepairTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcBoxRepairTimer.class);

	/** 間隔幾秒判斷一次 */
	private final int SCHEDULE_TIME = 30;

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = SCHEDULE_TIME * 1000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1NpcInstance> allNpc = WorldNpc.get().all();
			// 不包含元素
			if (allNpc.isEmpty()) {
				return;
			}

			for (final Iterator<L1NpcInstance> iter = allNpc.iterator(); iter.hasNext();) {
				final L1NpcInstance npc = iter.next();

				if (!(npc instanceof L1BoxInstance)) {
					continue;
				}

				final L1BoxInstance box = (L1BoxInstance) npc;

				if (box.getOpenStatus() != ActionCodes.ACTION_Open) {
					continue;
				}

				final int remaining_time = box.get_repair_time();

				if (remaining_time <= 0) {
					continue;
				}

				final int time = Math.max(remaining_time - SCHEDULE_TIME, 0);
				box.set_repair_time(time);

				if (time <= 0) {
					box.close();
				}

				Thread.sleep(1);
			}

		} catch (final Exception e) {
			_log.error("NPC寶箱重新復原時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcBoxRepairTimer npcBoxRepairTimer = new NpcBoxRepairTimer();
			npcBoxRepairTimer.start();
		}
	}
}
