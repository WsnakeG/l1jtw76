package com.lineage.server.timecontroller.server;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.serverpackets.S_Teleport;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 坐船系統時間軸
 * 
 * @author terry0412
 */
public class ServerShipTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerShipTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1PcInstance, Long> MAP = new ConcurrentHashMap<L1PcInstance, Long>();

	public void start() {
		final int timeMillis = 5000; // 5秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			checkShipTime();

		} catch (final Exception e) {
			_log.error("坐船系統時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerShipTimer shipTimer = new ServerShipTimer();
			shipTimer.start();
		}
	}

	/**
	 * 檢查PC是否在船上準備下船
	 */
	private final void checkShipTime() {
		final int servertime = L1GameTimeClock.getInstance().currentTime().getSeconds();
		final int nowtime = servertime % 86400;
		if (((nowtime >= ((25 * 360) + 1800)) && (nowtime < ((25 * 360) + 3600)))
				|| ((nowtime >= ((55 * 360) + 1800)) && (nowtime < ((55 * 360) + 3600)))
				|| ((nowtime >= ((85 * 360) + 1800)) && (nowtime < ((85 * 360) + 3600)))
				|| ((nowtime >= ((115 * 360) + 1800)) && (nowtime < ((115 * 360) + 3600)))
				|| ((nowtime >= ((145 * 360) + 1800)) && (nowtime < ((145 * 360) + 3600)))
				|| ((nowtime >= ((175 * 360) + 1800)) && (nowtime < ((175 * 360) + 3600)))
				|| ((nowtime >= ((205 * 360) + 1800)) && (nowtime < ((205 * 360) + 3600)))
				|| ((nowtime >= ((235 * 360) + 1800)) && (nowtime < ((235 * 360) + 3600)))) {
			for (final L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc.getMapId() == 5) { // 往古魯丁的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40299, 1)) {
						teleport(pc, 32540, 32728, (short) 4);
					}
				} else if (pc.getMapId() == 84) { // 往海音的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40301, 1)) {
						teleport(pc, 33426, 33501, (short) 4);
					}
				} else if (pc.getMapId() == 447) { // 往海賊島的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40302, 1)) {
						teleport(pc, 32297, 33086, (short) 440);
					}
				}
			}
		} else if (((nowtime >= ((10 * 360) + 1800)) && (nowtime < ((10 * 360) + 3600)))
				|| ((nowtime >= ((40 * 360) + 1800)) && (nowtime < ((40 * 360) + 3600)))
				|| ((nowtime >= ((70 * 360) + 1800)) && (nowtime < ((70 * 360) + 3600)))
				|| ((nowtime >= ((100 * 360) + 1800)) && (nowtime < ((100 * 360) + 3600)))
				|| ((nowtime >= ((130 * 360) + 1800)) && (nowtime < ((130 * 360) + 3600)))
				|| ((nowtime >= ((160 * 360) + 1800)) && (nowtime < ((160 * 360) + 3600)))
				|| ((nowtime >= ((190 * 360) + 1800)) && (nowtime < ((190 * 360) + 3600)))
				|| ((nowtime >= ((220 * 360) + 1800)) && (nowtime < ((220 * 360) + 3600)))) {
			for (final L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc.getMapId() == 6) { // 往說話之島的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40298, 1)) {
						teleport(pc, 32631, 32982, (short) 0);
					}
				} else if (pc.getMapId() == 83) { // 往遺忘之島的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40300, 1)) {
						teleport(pc, 32936, 33057, (short) 70);
					}
				} else if (pc.getMapId() == 446) { // 往隱藏之港的船
					// 船票檢查並刪除
					if (pc.getInventory().consumeItem(40303, 1)) {
						teleport(pc, 32751, 32873, (short) 445);
					}
				}
			}
		}
	}

	/**
	 * 傳送PC
	 * 
	 * @param tgpc
	 * @param locx
	 * @param locy
	 * @param mapid
	 */
	private final void teleport(final L1PcInstance tgpc, final int locx, final int locy, final short mapid) {
		// 傳送鎖定 (有動畫) by terry0412
		tgpc.setTeleportX(locx);
		tgpc.setTeleportY(locy);
		tgpc.setTeleportMapId(mapid);
		// 送出鎖定封包
		tgpc.sendPackets(new S_Teleport(tgpc));
	}
}
