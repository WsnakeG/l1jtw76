package com.lineage.server.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.lineage.config.Config;

/**
 * 系統時間資訊
 * @author dexc
 *
 */
public class TimeInfo {
	
	private static TimeInfo _instance;

	public static TimeInfo time() {
		if (_instance == null) {
			_instance = new TimeInfo();
		}
		return _instance;
	}

	/**
	 * <font color=#00800>時間資料的轉換</font>
	 * @param ts Timestamp
	 * @return Calendar
	 */
	public Calendar timestampToCalendar(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}
	
	/**
	 * <font color=#00800>傳回系統目前時間</font><BR>
	 * 目前應用範圍:<BR>
	 * 人物刪除時間<BR>
	 * 任何裝備計時<BR>
	 * 攻城時間計算<BR>
	 *  
	 * @return Calendar
	 */
	public Calendar getNowTime() {
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(tz);
		return cal;
	}
	
	/**
	 * <font color=#00800>取得系統時間</font>
	 * 
	 * @return 傳出標準時間格式 yyyy/MM/dd HH:mm:ss
	 */
	public String getNow_YMDHMS() {

		String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new Date());

		return nowDate;
	}
	

	/**
	 * <font color=#00800>取得系統時間(條件式規範)</font>
	 * @param i 要輸出的方式<BR>
	 * 			i = 0 年月日<BR>
	 * 			i = 1 時分秒<BR>
	 * 			i = 2 年月日時分秒
	 * 			i = 3 分
	 * 			i = 4 時
	 *  
	 * @return 傳出標準時間格式
	 * 			i = 0 yyyy-MM-dd<BR>
	 * 			i = 1 HH:mm:ss<BR>
	 * 			i = 2 yyyy-MM-dd HH:mm:ss
	 */
	public String getNow_YMDHMS(int i) {

		Calendar c = Calendar.getInstance();

		String nowDate[] = new String[6];

		nowDate[0] = String.valueOf(c.get(Calendar.YEAR));

		nowDate[1] = String.valueOf(c.get(Calendar.MONTH) + 1);
		if (nowDate[1].length() == 1) {
			nowDate[1] = "0" + nowDate[1];
		}

		nowDate[2] = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		if (nowDate[2].length() == 1) {
			nowDate[2] = "0" + nowDate[2];
		}

		nowDate[3] = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		if (nowDate[3].length() == 1) {
			nowDate[3] = "0" + nowDate[3];
		}

		nowDate[4] = String.valueOf(c.get(Calendar.MINUTE));
		if (nowDate[4].length() == 1) {
			nowDate[4] = "0" + nowDate[4];
		}

		nowDate[5] = String.valueOf(c.get(Calendar.SECOND));
		if (nowDate[5].length() == 1) {
			nowDate[5] = "0" + nowDate[5];
		}
		switch (i) {
		case 0:// 年月日
			return nowDate[0] + "-" + nowDate[1] + "-" + nowDate[2];
		case 1:// 時分秒
			return nowDate[3] + ":" + nowDate[4] + ":" + nowDate[5];
		case 2:// 年月日時分秒
			return nowDate[0] + "-" + nowDate[1] + "-" + nowDate[2] + " " + 
			nowDate[3] + ":" + nowDate[4] + ":" + nowDate[5];
		case 3:// 分
			return nowDate[4];
		case 4:// 時
			return nowDate[3];
		case 5:// 日
			return nowDate[2];
		case 6:// 秒
			return nowDate[5];
		}
		return null;
	}
	
	/**
	 * <font color=#00800>計時器啟動時間</font>
	 */
	private long _begin = System.currentTimeMillis();

	/**
	 * <font color=#00800>設置計時器啟動時間</font>
	 */
	public void reset() {
		_begin = System.currentTimeMillis();
	}

	/**
	 * <font color=#00800>傳回計時器已使用時間</font>
	 * @return
	 */
	public long get() {
		return System.currentTimeMillis() - _begin;
	}
}
