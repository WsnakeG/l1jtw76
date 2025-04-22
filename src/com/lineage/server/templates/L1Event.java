package com.lineage.server.templates;

import java.sql.Timestamp;

/**
 * Event(活動)設置 暫存
 * 
 * @author daien
 */
public class L1Event {

	private int _eventid;// 活動編號

	private String _eventname;// 活動名稱

	private String _eventclass;// 活動CLASS

	private boolean _eventstart;// 活動是否啟用

	private String _eventother;// 活動其他設定

	/**
	 * 活動編號
	 * 
	 * @return the _eventid
	 */
	public int get_eventid() {
		return _eventid;
	}

	/**
	 * 活動編號
	 * 
	 * @param eventid the _eventid to set
	 */
	public void set_eventid(final int eventid) {
		_eventid = eventid;
	}

	/**
	 * 活動名稱
	 * 
	 * @return the _eventname
	 */
	public String get_eventname() {
		return _eventname;
	}

	/**
	 * 活動名稱
	 * 
	 * @param eventname the _eventname to set
	 */
	public void set_eventname(final String eventname) {
		_eventname = eventname;
	}

	/**
	 * 活動CLASS
	 * 
	 * @return the _eventclass
	 */
	public String get_eventclass() {
		return _eventclass;
	}

	/**
	 * 活動CLASS
	 * 
	 * @param eventclass the _eventclass to set
	 */
	public void set_eventclass(final String eventclass) {
		_eventclass = eventclass;
	}

	/**
	 * 活動是否啟用
	 * 
	 * @return the _eventstart
	 */
	public boolean is_eventstart() {
		return _eventstart;
	}

	/**
	 * 活動是否啟用
	 * 
	 * @param eventstart the _eventstart to set
	 */
	public void set_eventstart(final boolean eventstart) {
		_eventstart = eventstart;
	}

	/**
	 * 活動其他設定
	 * 
	 * @return the _eventother
	 */
	public String get_eventother() {
		return _eventother;
	}

	/**
	 * 活動其他設定
	 * 
	 * @param eventother the _eventother to set
	 */
	public void set_eventother(final String eventother) {
		_eventother = eventother;
	}

	private Timestamp _next_time; // 下一個判斷時間 by terry0412

	/**
	 * 下一個判斷時間 by terry0412
	 * 
	 * @return the _next_time
	 */
	public Timestamp get_next_time() {
		return _next_time;
	}

	/**
	 * 下一個判斷時間 by terry0412
	 * 
	 * @param next_time the _next_time to set
	 */
	public void set_next_time(final Timestamp next_time) {
		_next_time = next_time;
	}
}
