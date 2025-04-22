package com.lineage.server.timecontroller.event;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.lock.VIPReading;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * VIP計時時間軸
 * 
 * @author dexc
 */
public class VIPTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(VIPTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;// 1分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Map<Integer, Timestamp> map = VIPReading.get().map();
			// 不包含元素
			if (map.isEmpty()) {
				return;
			}

			// 目前時間
			final Timestamp ts = new Timestamp(System.currentTimeMillis());

			for (final Integer objid : map.keySet()) {
				final Timestamp time = map.get(objid);
				// 指示此 time 对象是否早于给定的 ts 对象。
				if (time.before(ts)) {
					// 移出清單
					VIPReading.get().delOther(objid);
					// 檢查對象
					checkVIP(objid);
				}
				Thread.sleep(5);
			}

		} catch (final Exception e) {
			_log.error("VIP計時時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final VIPTimer timer = new VIPTimer();
			timer.start();
		}
	}

	/**
	 * 移出VIP清單 檢查是否傳送PC
	 * 
	 * @param objid
	 */
	private static void checkVIP(final Integer objid) {
		try {
			// 取回對象(接收指令者)
			final L1Object target = World.get().findObject(objid);
			if (target != null) {
				final boolean isOut = false;
				/*
				 * if (target.getMapId() == 200) {// 傲慢塔100樓 isOut = true; } if
				 * (target.getMapId() == 1002) {// 蒼空之谷 isOut = true; }
				 */
				if (!isOut) {
					return;
				}
				if (target instanceof L1PcInstance) {
					final L1PcInstance tgpc = (L1PcInstance) target;
					L1Teleport.teleport(tgpc, 33080, 33392, (short) 4, 5, true);
					tgpc.sendPackets(new S_ServerMessage("VIP時間終止"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
