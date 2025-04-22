package com.lineage.server.timecontroller.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.data.event.LimitTimeMerchantSet;
import com.lineage.server.Shutdown;
import com.lineage.server.datatables.ExtraQuiz1SetTable;
import com.lineage.server.datatables.ExtraQuizSetTable;
import com.lineage.server.datatables.lock.CharMapsTimeReading;
import com.lineage.server.model.TimeLimit.GetNowTime;
import com.lineage.server.model.TimeLimit.TimeLimitCharTable;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 自動重啟
 * 
 * @author dexc
 */
public class ServerRestartTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerRestartTimer.class);

	private ScheduledFuture<?> _timer;

	private static final ArrayList<Calendar> _restartList = new ArrayList<Calendar>();

	private static Calendar _restart = null;

	private static String _string = "yyyy/MM/dd HH:mm:ss";

	private static String _startTime = null;

	private static String _restartTime = null;

	/**
	 * 重新啟動時間
	 * 
	 * @return
	 */
	public static String get_restartTime() {
		return _restartTime;
	}

	/**
	 * 啟動時間
	 * 
	 * @return
	 */
	public static String get_startTime() {
		return _startTime;
	}

	/**
	 * 距離關機小逾10分鐘
	 * 
	 * @return
	 */
	public static boolean isRtartTime() {
		if (_restart == null) {
			return false;
		}
		return (_restart.getTimeInMillis() - System.currentTimeMillis()) <= (10 * 60 * 1000);
	}

	private static Calendar timestampToCalendar() {
		final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(_tz);

		return cal;
	}

	public void start() {
		if (Config.AUTORESTART == null) {
			return;
		}

		final Calendar cals = timestampToCalendar();

		if (_startTime == null) {
			final String nowDate = new SimpleDateFormat(_string).format(cals.getTime());
			_startTime = nowDate;
		}

		if (Config.AUTORESTART != null) {
			final String HH = new SimpleDateFormat("HH").format(cals.getTime());
			final int HHi = Integer.parseInt(HH);
			final String mm = new SimpleDateFormat("mm").format(cals.getTime());
			final int mmi = Integer.parseInt(mm);

			for (final String hm : Config.AUTORESTART) {
				final String[] hm_b = hm.split(":");
				final String hh_b = hm_b[0];
				final String mm_b = hm_b[1];

				final int newHH = Integer.parseInt(hh_b);
				final int newMM = Integer.parseInt(mm_b);

				final Calendar cal = timestampToCalendar();

				int xh = -1;
				final int xhh = newHH - HHi;
				if (xhh > 0) {
					xh = xhh;

				} else {
					xh = (24 - HHi) + newHH;
				}

				final int xm = newMM - mmi;

				cal.add(Calendar.HOUR, xh);
				cal.add(Calendar.MINUTE, xm);

				_restartList.add(cal);
			}

			for (final Calendar tmpCal : _restartList) {
				if (_restart == null) {
					_restart = tmpCal;

				} else {
					final boolean re = tmpCal.before(_restart);
					if (re) {
						_restart = tmpCal;
					}
				}
			}

		}

		final String restartTime = new SimpleDateFormat(_string).format(_restart.getTime());
		_restartTime = restartTime;

		_log.warn("\n\r--------------------------------------------------" + "\n\r       開機完成時間為:"
				+ _startTime + "\n\r       設置關機時間為:" + _restartTime
				+ "\n\r--------------------------------------------------");

		final int timeMillis = 60 * 1000;// 1分鐘
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			startCommand();
			
			if(LimitTimeMerchantSet.ISOPEN){
				// 限時商人更新銷售數量
				if(( GetNowTime.GetNowHour() == LimitTimeMerchantSet.TimeLimit_H ) && GetNowTime.GetNowMinute() == LimitTimeMerchantSet.TimeLimit_M){
					for (int i = 10; i > 0; i--) {
						World.get().broadcastServerMessage("\\fV請注意!限購商將在" + i + "\\fV秒後更新刷新購買數量!");
						Thread.sleep(1000);
					}
					/*
					final L1TimeLimit shop = ShopTimeLimitTable.getInstance().get(91002);
			    	List<L1ShopTimeLimit> shopItems = shop.getSellingItems();
			    	for (int i = 0; i < shopItems.size(); i++) {
			    		L1ShopTimeLimit shopItem = shopItems.get(i);
			    		if (shopItem.get_isall() == 0) {// 全服限購物品更新
				    		ShopTimeLimitTable Limit = new ShopTimeLimitTable();
				    		Limit.updateendcount(shopItem.getItemId(), Limit.getNewcount(shopItem.getItemId()));
			    		}
			    	}
			    	*/
			    	TimeLimitCharTable ttt = new TimeLimitCharTable();
			    	ttt.deleteall();
					World.getInstance().broadcastServerMessage("\\fV限購商 已重置完畢!!!");
					World.getInstance().broadcastServerMessage("\\fV限購商 已重置完畢!!");
					World.getInstance().broadcastServerMessage("\\fV限購商 已重置完畢!");
				}
			}
			
			
			

		} catch (final Exception e) {
			_log.error("自動重啟時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ServerRestartTimer restartTimer = new ServerRestartTimer();
			restartTimer.start();
		}
	}

	// 每日重置紀錄處理 (設置重置時間) by terry0412
	private static Calendar RESET_TIMER;

	static {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 55);
		cal.set(Calendar.SECOND, 0);
		RESET_TIMER = cal;
	}

	private void startCommand() {
		if (Config.AUTORESTART != null) {
			final Calendar cals = Calendar.getInstance();

			// 每日重置紀錄處理 by terry0412
			if ((RESET_TIMER != null) && RESET_TIMER.before(cals)) {
				// 清除全部地圖入場時間紀錄
				CharMapsTimeReading.get().clearAllTime();

				// 清除完畢，回收物件
				RESET_TIMER = null;
			}

			// 每日重置紀錄處理 by terry0412
			if ((ConfigAlt.QUIZ_SET_RESET_TIME != null) && ConfigAlt.QUIZ_SET_RESET_TIME.before(cals)) {
				/** 清除 每日一題 紀錄 */
				if (ConfigAlt.QUIZ_SET_SWITCH) {
					// 取消目前題目使用狀態 並更換到下一題
					ExtraQuizSetTable.getInstance().updateQuizToNext();

					// 更新當前題目的所有內容
					ExtraQuizSetTable.getInstance().updateQuizInfo();

					// 重置全體玩家的答題狀態
					ExtraQuizSetTable.getInstance().updateAllPcQuizSet();

					// 發送系統提示訊息
					World.get().broadcastServerMessage("\\fX【每日一題】今日題目已自動更新完成，快來賺獎品喔！");
				}

				// 清除完畢，回收物件
				ConfigAlt.QUIZ_SET_RESET_TIME = null;
			}

			// 每日重置紀錄處理 by terry0412
			if ((ConfigAlt.QUIZ_SET_RESET_TIME1 != null) && ConfigAlt.QUIZ_SET_RESET_TIME1.before(cals)) {
				/** 清除 每日一題 紀錄 */
				if (ConfigAlt.QUIZ_SET_SWITCH1) {
					// 取消目前題目使用狀態 並更換到下一題
					ExtraQuiz1SetTable.getInstance().updateQuizToNext();

					// 更新當前題目的所有內容
					ExtraQuiz1SetTable.getInstance().updateQuizInfo();

					// 重置全體玩家的答題狀態
					ExtraQuiz1SetTable.getInstance().updateAllPcQuizSet();

					// 發送系統提示訊息
					World.get().broadcastServerMessage("\\fX【每日任務系統】已重置完畢，快來查看進行任務吧！");
				}

				// 清除完畢，回收物件
				ConfigAlt.QUIZ_SET_RESET_TIME1 = null;
			}

			if (_restart.before(cals)) {
				Shutdown.getInstance().startShutdown(null, 300, true);
			}
		}
	}
}
