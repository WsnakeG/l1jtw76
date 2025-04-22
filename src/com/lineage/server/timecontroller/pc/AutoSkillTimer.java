package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * PC 內掛計時更新處理 時間軸 類名稱：ExpTimer<br>
 * 創建人:Nick<br>
 * 修改時間：2024年6月15日<br>
 * 修改備註:<br>
 * 
 * @version 7.6c<br>
 */
public class AutoSkillTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(AutoSkillTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000;
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
				if (check(tgpc)) {

					tgpc.upAtktimer(1); // 被攻擊時間軸刷新

					if (tgpc.getOpenskill_id() != 0) { // 開怪
						tgpc.up_openskill_timer(1);
					}
					if (tgpc.getAtkskill_id() != 0) { // 攻擊
						tgpc.up_atkskill_timer(1);
					}
					if (tgpc.getRngskill_id() != 0) { // 範圍
						tgpc.up_Rngskill_timer(1);
					}
					Thread.sleep(1);
				}
			}

		} catch (final Exception e) {
			_log.error("內掛計時時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final AutoSkillTimer ReseAutoTimer = new AutoSkillTimer();
			ReseAutoTimer.start();
		}
	}

	/**
	 * 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	private static boolean check(L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}

			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}

			if (tgpc.getNetConnection() == null) {
				return false;
			}

			if (tgpc.isTeleport()) {
				return false;
			}

			if (!tgpc.IsAuto()) {
				return false;
			}

		} catch (final Exception e) {
			return false;
		}
		return true;
	}
}
