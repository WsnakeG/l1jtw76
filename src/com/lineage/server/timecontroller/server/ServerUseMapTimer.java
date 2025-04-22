package com.lineage.server.timecontroller.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PackBoxMaptime;
import com.lineage.server.serverpackets.S_PacketBoxGame;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 計時地圖時間軸
 * 
 * @author dexc
 */
public class ServerUseMapTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerUseMapTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1PcInstance, Integer> MAP = new ConcurrentHashMap<L1PcInstance, Integer>();

	public void start() {
		final int timeMillis = 1000;// 1.1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	/**
	 * 加入計時地圖物件清單<BR>
	 * 同時更新地圖使用時間<BR>
	 * 送出時間封包
	 * 
	 * @param pc
	 * @param time
	 *            秒
	 */
	public static void put(final L1PcInstance pc, final int time) {
		pc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIME, time));
		pc.sendPackets(new S_PackBoxMaptime((time)));
		pc.get_other().set_usemapTime(time);
		MAP.put(pc, new Integer(time));
	}

	@Override
	public void run() {
		try {
			// 包含元素
			if (!MAP.isEmpty()) {
				for (final Iterator<Entry<L1PcInstance, Integer>> iter = MAP
						.entrySet().iterator(); iter.hasNext();) {
					final Entry<L1PcInstance, Integer> info = iter.next();
					final L1PcInstance key = info.getKey();
					if (check(key)) {
						// 取回剩餘時間
						int value = info.getValue();

						// for (final L1PcInstance key : MAP.keySet()) {
						// 取回剩餘時間
						// Integer value = MAP.get(key);
						value--;

						if (value <= 0) {
							teleport(key);

						} else {
							// 更新可用時間
							key.get_other().set_usemapTime(value);
							MAP.put(key, value);
						}
						Thread.sleep(5);
					}
				}
			}

		} catch (final Exception e) {
			_log.error("計時地圖時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerUseMapTimer useMapTimer = new ServerUseMapTimer();
			useMapTimer.start();
		}
	}

	/**
	 * PC 執行 判斷
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

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * 傳出PC
	 * 
	 * @param item
	 */
	public static void teleport(final L1PcInstance tgpc) {

		try {
			MAP.remove(tgpc);
			if (World.get().getPlayer(tgpc.getName()) == null) {
				// 人物離開世界
				return;
			}

			tgpc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));// 凍結
			Thread.sleep(2000);// 倒數兩秒

			if (tgpc.getMapId() == tgpc.get_other().get_usemap()) {
				L1Teleport.teleport(tgpc, 32703, 32829, (short) 350, 5, true);
				tgpc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));// 解凍
				tgpc.sendPackets(new S_ServerMessage("您可以動了....."));
			}
			tgpc.get_other().set_usemapTime(0);
			tgpc.get_other().set_usemap(-1);
			tgpc.sendPackets(new S_PacketBoxGame(S_PacketBoxGame.STARTTIMECLEAR));

		} catch (InterruptedException e) {
		}
	}
}
