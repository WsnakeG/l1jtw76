package com.lineage.server.timecontroller.server;

import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1TrapInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldTrap;

/**
 * 陷阱召喚處理
 * 
 * @author dexc
 */
public class ServerTrapTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerTrapTimer.class);

	private ScheduledFuture<?> _timer;

	/**
	 * 啟動控管
	 */
	public void start() {
		final int timeMillis = 5000;// 5秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final HashMap<Integer, L1TrapInstance> allTrap = WorldTrap.get().map();
			// 不包含元素
			if (allTrap.isEmpty()) {
				return;
			}

			for (final Object iter : allTrap.values().toArray()) {
				final L1TrapInstance trap = (L1TrapInstance) iter;
				// 陷阱停止作用
				if (!trap.isEnable()) {
					trap.set_stop(trap.get_stop() + 1);
					if (trap.get_stop() >= trap.getSpan()) {
						// 重新設置陷阱啟動與位置
						trap.resetLocation();
						Thread.sleep(50);
					}
				}
			}

		} catch (final Exception io) {
			_log.error("陷阱召喚處理時間軸異常重啟", io);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerTrapTimer trapTimer = new ServerTrapTimer();
			trapTimer.start();
		}
	}
}
