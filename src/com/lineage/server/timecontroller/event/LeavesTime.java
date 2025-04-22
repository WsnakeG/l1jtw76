package com.lineage.server.timecontroller.event;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.LeavesSet;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 殷海薩的祝福-休息系統<BR>
 * 
 * @author dexc
 */
public class LeavesTime extends TimerTask {

	private static final Log _log = LogFactory.getLog(LeavesTime.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;// 間隔時間(分鐘)
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
					final int time = tgpc.get_other().get_teaves_time();// 時間
					tgpc.get_other().set_teaves_time(time + 1);// 時間

					if (tgpc.get_other().get_teaves_time() >= LeavesSet.TIME) {// 達到增加時間設置
						tgpc.get_other().set_teaves_time(0);// 時間歸0

						final int exp = tgpc.get_other().get_teaves_time_exp();// 計算前經驗質
						final int addexp = exp + LeavesSet.EXP;
						tgpc.get_other().set_teaves_time_exp(addexp);

						// 經驗質有異動 送出百分比
						tgpc.sendPackets(
								new S_PacketBoxExp(tgpc.get_other().get_teaves_time_exp() / LeavesSet.EXP));

					}
					Thread.sleep(100);
				}
			}

		} catch (final Exception e) {
			_log.error("殷海薩的祝福-休息系統時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final LeavesTime leavesTime = new LeavesTime();
			leavesTime.start();
		}
	}

	/**
	 * PC HP回復執行 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	private static boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}
			// 死亡
			if (tgpc.isDead()) {
				return false;
			}
			// 非安全區
			if (!tgpc.isSafetyZone()) {
				return false;
			}
			// 傳送狀態
			if (tgpc.isTeleport()) {
				return false;
			}
			// 可增加質已滿
			if (tgpc.get_other().get_teaves_time_exp() >= LeavesSet.MAXEXP) {
				return false;
			}
			// 攻擊狀態
			if (tgpc.getHpRegenState() == L1PcInstance.REGENSTATE_ATTACK) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
}
