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
 * NPC工作時間軸
 * 
 * @author dexc
 */
public class NpcWorkTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcWorkTimer.class);

	private ScheduledFuture<?> _timer;

	public static void put(final L1NpcInstance npc, final Integer time) {
		npc.set_work_time(time);
	}

	public void start() {
		// NPC工作設置資料
		final int timeMillis = 2500;
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
				if (npc.get_work_time() > 0) {
					final int time = npc.get_work_time() - 2;
					if (time <= 0) {
						startWork(npc);
					} else {
						npc.set_work_time(time);
					}
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("NPC工作時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcWorkTimer workTimer = new NpcWorkTimer();
			workTimer.start();
		}
	}

	private static void startWork(final L1NpcInstance npc) {
		try {
			if (npc != null) {
				final int time = npc.WORK.workTime();// 重新取回工作間格時間

				if (time != 0) {
					npc.WORK.work(npc);// 執行動作
					npc.set_work_time(time);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
