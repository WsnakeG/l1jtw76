package com.lineage.data.event;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.EventClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.EventExecutor;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Event;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 隨機送禮系統
 * 
 * @author terry0412
 */
public class RandomGiftSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(RandomGiftSet.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static int TIME_DELAY; // 每隔幾小時贈送一次

	public static int BROADCAST_TIME_DELAY;

	public static int TOTAL_COUNT; // 隨機送禮給幾個玩家

	public static int GIFT_ID; // 禮物編號

	public static int GIFT_COUNT; // 禮物數量

	/**
	 *
	 */
	private RandomGiftSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new RandomGiftSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			final String[] set = event.get_eventother().split(",");

			TIME_DELAY = Integer.parseInt(set[0]);

			BROADCAST_TIME_DELAY = Integer.parseInt(set[1]);

			TOTAL_COUNT = Integer.parseInt(set[2]);

			GIFT_ID = Integer.parseInt(set[3]);

			GIFT_COUNT = Integer.parseInt(set[4]);

			// 下一個判斷時間 by terry0412
			final long now_time = System.currentTimeMillis();
			if (event.get_next_time() == null) {
				event.set_next_time(new Timestamp(now_time + (TIME_DELAY * 3600000)));
				// 更新`下一次判斷時間`
				EventClass.get().updateEventNextTime(event.get_eventid(), event.get_next_time());

			} else {
				// 持續判斷
				while (event.get_next_time().getTime() < now_time) {
					event.set_next_time(
							new Timestamp(event.get_next_time().getTime() + (TIME_DELAY * 3600000)));
				}
				// 更新`下一次判斷時間`
				EventClass.get().updateEventNextTime(event.get_eventid(), event.get_next_time());
			}

			// 啟動時間軸
			final GetItemTimer getItemTimer = new GetItemTimer(event);
			getItemTimer.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private class GetItemTimer extends TimerTask {

		private ScheduledFuture<?> _timer;

		private final L1Event _event;

		private List<L1PcInstance> _allPc; // 隨機抽取玩家列表

		private GetItemTimer(final L1Event event) {
			_event = event;
		}

		private GetItemTimer(final L1Event event, final List<L1PcInstance> allPc) {
			_event = event;
			_allPc = allPc;
		}

		private void start() {
			final int timeMillis = 1000 * 60; // 1分鐘
			_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
		}

		@Override
		public void run() {
			try {
				// 還有抽取次數
				if (_allPc != null) {
					for (final L1PcInstance pc : _allPc) {
						// 先進入延遲
						Thread.sleep(5000);

						// 全玩家廣播
						World.get()
								.broadcastPacketToAll(
										new S_SystemMessage(
												new StringBuilder().append("恭喜伺服器幸運的玩家[").append(pc.getName())
														.append("]獲得")
														.append(ItemTable.get().getTemplate(GIFT_ID)
																.getNameId())
														.append("x").append(GIFT_COUNT).toString()));

						// 獲得獎勵
						CreateNewItem.createNewItem(pc, GIFT_ID, GIFT_COUNT);
					}

					// 釋放列表
					_allPc = null;
				}

				// 目前時間毫秒數
				final long now_time = System.currentTimeMillis();

				// 判斷時間是否已到
				if (_event.get_next_time().getTime() < now_time) {
					// 取回隨機抽取玩家列表
					_allPc = World.get().getRandomPlayers(TOTAL_COUNT);
					// `不包含元素` 或是 `抽取數量小於要求數量`
					if ((_allPc == null) || (_allPc.size() < TOTAL_COUNT)) {
						return;
					}

					_event.set_next_time(
							new Timestamp(_event.get_next_time().getTime() + (TIME_DELAY * 3600000)));
					// 更新`下一次判斷時間`
					EventClass.get().updateEventNextTime(_event.get_eventid(), _event.get_next_time());

					// 全玩家廣播
					World.get().broadcastPacketToAll(
							new S_SystemMessage(new StringBuilder().append("現在開始進行抽獎送禮，稍後將抽出[")
									.append(TOTAL_COUNT).append("]個玩家獲得獎勵!!").toString()));

				} else {
					if (((_event.get_next_time().getTime() - now_time) / 3600000) <= BROADCAST_TIME_DELAY) {
						if (((_event.get_next_time().getTime() - now_time) % 3600000) == 0) {
							// 全玩家廣播
							World.get()
									.broadcastPacketToAll(new S_SystemMessage(new StringBuilder()
											.append("下次送禮抽獎時間為[").append(sdf.format(_event.get_next_time()))
											.append("]\n預計抽出[").append(TOTAL_COUNT).append("]個玩家獲得獎勵")
											.toString()));
						}
					}
				}

			} catch (final Exception e) {
				_log.error("隨機送禮系統時間軸異常重啟", e);
				GeneralThreadPool.get().cancel(_timer, false);
				// 保留抽取列表
				final GetItemTimer getItemTimer = new GetItemTimer(_event, _allPc);
				getItemTimer.start();
			}
		}
	}
}
