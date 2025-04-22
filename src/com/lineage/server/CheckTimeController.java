/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server;

import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * PC檢查時間軸
 * 
 * @author terry0412
 */
public final class CheckTimeController implements Runnable {

	private static final Log _log = LogFactory.getLog(CheckTimeController.class);

	private static CheckTimeController _instance;

	public static CheckTimeController getInstance() {
		if (_instance == null) {
			_instance = new CheckTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			// 執行全PC檢查程序
			for (final L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc != null) {
					// 地圖群組設置資料 (入場時間限制) by terry0412
					final L1MapsLimitTime mapsLimitTime = MapsGroupTable.get().findGroupMap(pc.getMapId());
					if (mapsLimitTime != null) {
						// 入場地圖索引碼
						final int order_id = mapsLimitTime.getOrderId();
						// 已使用時間 (單位:秒)
						final int used_time = pc.getMapsTime(order_id);
						// 最大時間
						final int limit_time = mapsLimitTime.getLimitTime();

						// 允許時間已到
						if (used_time > limit_time) {
							L1Teleport.teleport(pc, mapsLimitTime.getOutMapX(), mapsLimitTime.getOutMapY(),
									mapsLimitTime.getOutMapId(), pc.getHeading(), true);

						} else {
							checkTime(pc, used_time, limit_time);
							// 入場時間+1
							pc.putMapsTime(order_id, used_time + 1);
						}
					}

					// TODO 其他程序判斷

				}
				Thread.sleep(1);
			}

		} catch (final Exception e) {
			_log.error("PC檢查時間軸異常重啟");
			GeneralThreadPool.get().cancel(_timer, false);
			_instance = new CheckTimeController();
			_instance.start();
		}
	}

	/**
	 * 檢查時間並傳回訊息<BR>
	 * [地監可停留時間：%0 小時，都已使用完畢了] 1522<BR>
	 * [地監可停留時間 %0 分都使用完畢了] 1523<BR>
	 * [地監可停留時間 %0 小時 %1分都使用完畢了] 1524<BR>
	 * [地監可停留時間剩餘 %0小時 %1分了] 1525<BR>
	 * [地監可停留時間剩餘 %0小時了] 1526<BR>
	 * [地監可停留時間剩餘 %0分了] 1527<BR>
	 * [地監可停留時間剩餘 %0秒了] 1528<BR>
	 * 
	 * @param pc
	 * @param usedTime
	 * @param limitTime
	 */
	private final void checkTime(final L1PcInstance pc, final int usedTime, final int limitTime) {
		// 已超過可停留時間
		if (usedTime >= limitTime) {
			if (limitTime < 3600) {
				// 地監可停留時間 %0 分都使用完畢了
				pc.sendPackets(new S_ServerMessage(1523, String.valueOf(limitTime / 60)));
			} else if ((limitTime % 3600) == 0) {
				// 地監可停留時間：%0 小時，都已使用完畢了
				pc.sendPackets(new S_ServerMessage(1522, String.valueOf(limitTime / 3600)));
			} else {
				// 地監可停留時間 %0 小時 %1分都使用完畢了
				pc.sendPackets(new S_ServerMessage(1524, String.valueOf(limitTime / 3600),
						String.valueOf((limitTime % 3600) / 60)));
			}

		} else {
			// 計算差距時間
			final int remainTime = limitTime - usedTime;
			if ((remainTime % 60) == 0) { // 每60秒
				if (remainTime < 3600) {
					// 地監可停留時間剩餘 %0分了
					pc.sendPackets(new S_ServerMessage(1527, String.valueOf(remainTime / 60)));

				} else if ((remainTime % 3600) == 0) {
					// 地監可停留時間剩餘 %0小時了
					pc.sendPackets(new S_ServerMessage(1526, String.valueOf(remainTime / 3600)));

				} else {
					// 地監可停留時間剩餘 %0小時 %1分了
					pc.sendPackets(new S_ServerMessage(1525, String.valueOf(remainTime / 3600),
							String.valueOf((remainTime % 3600) / 60)));
				}

			} else if (remainTime < 60) { // 最後一分鐘提示訊息
				// 每5秒提示一次 或是 倒數時間剩餘 5秒
				if (((remainTime % 5) == 0) || (remainTime < 5)) {
					// 地監可停留時間剩餘 %0秒了
					pc.sendPackets(new S_ServerMessage(1528, String.valueOf(remainTime)));
				}
			}
		}
	}

	private ScheduledFuture<?> _timer;

	public final void start() {
		// 啟動執行緒
		final int timeMillis = 1000; // 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
}
