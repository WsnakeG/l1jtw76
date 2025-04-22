package com.lineage.server.templates;

/**
 * 佈告欄資料
 * 
 * @author dexc
 */
public class L1Board {

	private int _id;

	private String _name;

	private String _date;

	private String _title;

	private String _content;

	/**
	 * 傳回佈告欄ID
	 * 
	 * @return
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * 設置佈告欄ID
	 * 
	 * @param id
	 */
	public void set_id(final int id) {
		_id = id;
	}

	/**
	 * 傳回佈告欄公佈者
	 * 
	 * @return
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * 設置佈告欄公佈者
	 * 
	 * @param name
	 */
	public void set_name(final String name) {
		_name = name;
	}

	/**
	 * 傳回佈告欄公佈日期
	 * 
	 * @return
	 */
	public String get_date() {
		return _date;
	}

	/**
	 * 設置佈告欄公佈日期
	 * 
	 * @param date
	 */
	public void set_date(final String date) {
		_date = date;
	}

	/**
	 * 傳回佈告欄標題
	 * 
	 * @return
	 */
	public String get_title() {
		return _title;
	}

	/**
	 * 設置佈告欄標題
	 * 
	 * @param title
	 */
	public void set_title(final String title) {
		_title = title;
	}

	/**
	 * 傳回佈告欄內容
	 * 
	 * @return
	 */
	public String get_content() {
		return _content;
	}

	/**
	 * 設置佈告欄內容
	 * 
	 * @param content
	 */
	public void set_content(final String content) {
		_content = content;
	}
}