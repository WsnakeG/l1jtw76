/********************************************************************
 *  GetNowTime.java
 *  by mca
 *  2008/4/1
 */

package com.lineage.server.model.TimeLimit;

import java.util.Calendar;

/**
 * 取得現實時間
 */
public class GetNowTime {

	/** 傳回取得今日之值 */
	public static int GetNowDay() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowDay;
		nowDay = rightNow.get(Calendar.DATE); // 取得今日之值
		return nowDay; // 傳回取得今日之值
	}

	/** 傳回取得此時之值 */
	public static int GetNowHour() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowHour;
		nowHour = rightNow.get(Calendar.HOUR_OF_DAY); // 取得此時之值
		return nowHour; // 傳回取得此時之值
	}

	/** 傳回取得此分之值 */
	public static int GetNowMinute() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowMinute;
		nowMinute = rightNow.get(Calendar.MINUTE); // 取得此分之值
		return nowMinute; // 傳回取得此分之值
	}

	/** 傳回取得現月之值 */
	public static int GetNowMonth() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowMonth;
		nowMonth = rightNow.get(Calendar.MONTH); // 取得現月之值
		return nowMonth; // 傳回取得現月之值
	}

	/** 傳回取得此秒之值 */
	public static int GetNowSecond() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowSecond;
		nowSecond = rightNow.get(Calendar.SECOND); // 取得此秒之值
		return nowSecond; // 傳回取得此秒之值
	}

	/** 傳回取得現年之值 */
	public static int GetNowYear() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowYear;
		nowYear = rightNow.get(Calendar.YEAR); // 取得現年之值
		return nowYear; // 傳回取得現年之值
	}

	/** 傳回取得星期之值 */
	public static int GetNowDayWeek() {
		final Calendar rightNow = Calendar.getInstance(); // 取得預設月曆物件
		int nowDayWeek;
		nowDayWeek = rightNow.get(Calendar.DAY_OF_WEEK);// 取得星期之值
		return nowDayWeek;// 傳回取得星期之值
	}
}
