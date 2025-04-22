package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.datatables.lock.UpdateLocReading;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 解除人物卡點計時時間軸
 * 
 * @author dexc
 */
public class UnfreezingTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(UnfreezingTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 2000;// 2秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				// 師徒系統 - 檢查狀態 by terry0412
				if (ConfigAlt.APPRENTICE_SWITCH) {
					tgpc.checkEffect();
				}
				if (tgpc.get_unfreezingTime() == 0) {
					continue;
				}
				// 取回時間
				final int time = tgpc.get_unfreezingTime() - 1;
				tgpc.set_unfreezingTime(time);
				if (time <= 0) {
					UpdateLocReading.get().setPcLoc(tgpc.getAccountName());
					tgpc.sendPackets(new S_ServerMessage("\\fR帳號內其他人物傳送回指定位置！"));

					L1Teleport.teleport(tgpc, 32781, 32856, (short) 340, 5, true);

				} else {
					tgpc.sendPackets(new S_ServerMessage("\\fR" + (time) + "秒後傳送回指定位置！"));
				}
			}

			/*
			 * for (final L1PcInstance tgpc : all) { if
			 * (tgpc.get_unfreezingTime() == 0) { continue; } // 取回時間 final int
			 * time = tgpc.get_unfreezingTime() - 1;
			 * tgpc.set_unfreezingTime(time); if (time <= 0) {
			 * UpdateLocReading.get().setPcLoc(tgpc.getAccountName());
			 * tgpc.sendPackets(new S_ServerMessage("\\fR帳號內其他人物傳送回指定位置！"));
			 * L1Teleport.teleport(tgpc, 32781, 32856, (short) 340, 5, true); }
			 * else { tgpc.sendPackets(new S_ServerMessage("\\fR" + (time) +
			 * "秒後傳送回指定位置！")); } Thread.sleep(5); }
			 */

		} catch (final Exception e) {
			_log.error("解除人物卡點時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final UnfreezingTimer skillTimer = new UnfreezingTimer();
			skillTimer.start();
		}
	}

}
