package com.lineage.server.templates;

import java.sql.Timestamp;

public class L1Account {

	private String _login;// 帳號

	private String _password;// 密碼

	private Timestamp _lastactive;// 最後登入時間

	private int _access_level;// 帳戶等級

	private String _ip;// 登入IP

	private String _mac;// 登入MAC

	private int _character_slot;// 人物擴充

	private String _spw;// 超級密碼

	private int _warehouse;// 倉庫密碼

	private int _countCharacters;// 已創人物數量

	private boolean _isLoad;// 帳戶已登入

	private int _server_no;// 帳戶登入服務器編號

	private int _point;// 商城點數

	/**
	 * 帳號
	 * 
	 * @return 傳出 _login
	 */
	public String get_login() {
		return _login;
	}

	/**
	 * 帳號
	 * 
	 * @param login 對 _login 進行設置
	 */
	public void set_login(final String login) {
		_login = login;
	}

	/**
	 * 密碼
	 * 
	 * @return 傳出 _password
	 */
	public String get_password() {
		return _password;
	}

	/**
	 * 密碼
	 * 
	 * @param password 對 _password 進行設置
	 */
	public void set_password(final String password) {
		_password = password;
	}

	/**
	 * 最後登入時間
	 * 
	 * @return 傳出 _lastactive
	 */
	public Timestamp get_lastactive() {
		return _lastactive;
	}

	/**
	 * 最後登入時間
	 * 
	 * @param lastactive 對 _lastactive 進行設置
	 */
	public void set_lastactive(final Timestamp lastactive) {
		_lastactive = lastactive;
	}

	/**
	 * 帳戶等級
	 * 
	 * @return 傳出 _access_level
	 */
	public int get_access_level() {
		return _access_level;
	}

	/**
	 * 帳戶等級
	 * 
	 * @param access_level 對 _access_level 進行設置
	 */
	public void set_access_level(final int access_level) {
		_access_level = access_level;
	}

	/**
	 * 登入IP
	 * 
	 * @return 傳出 _ip
	 */
	public String get_ip() {
		return _ip;
	}

	/**
	 * 登入IP
	 * 
	 * @param ip 對 _ip 進行設置
	 */
	public void set_ip(final String ip) {
		_ip = ip;
	}

	/**
	 * 登入MAC
	 * 
	 * @return 傳出 _mac
	 */
	public String get_mac() {
		return _mac;
	}

	/**
	 * 登入MAC
	 * 
	 * @param mac 對 _mac 進行設置
	 */
	public void set_mac(final String mac) {
		_mac = mac;
	}

	/**
	 * 人物擴充
	 * 
	 * @return 傳出 _character_slot
	 */
	public int get_character_slot() {
		return _character_slot;
	}

	/**
	 * 人物擴充
	 * 
	 * @param character_slot 對 _character_slot 進行設置
	 */
	public void set_character_slot(final int character_slot) {
		_character_slot = character_slot;
	}

	/**
	 * 超級密碼
	 * 
	 * @return 傳出 _spw
	 */
	public String get_spw() {
		return _spw;
	}

	/**
	 * 超級密碼
	 * 
	 * @param spw 對 _spw 進行設置
	 */
	public void set_spw(final String spw) {
		_spw = spw;
	}

	/**
	 * 倉庫密碼
	 * 
	 * @return 傳出 _warehouse
	 */
	public int get_warehouse() {
		return _warehouse;
	}

	/**
	 * 倉庫密碼
	 * 
	 * @param warehouse 對 _warehouse 進行設置
	 */
	public void set_warehouse(final int warehouse) {
		_warehouse = warehouse;
	}

	/**
	 * 已創人物數量
	 * 
	 * @return 傳出 _countCharacters
	 */
	public int get_countCharacters() {
		return _countCharacters;
	}

	/**
	 * 已創人物數量
	 * 
	 * @param characters 對 _countCharacters 進行設置
	 */
	public void set_countCharacters(final int characters) {
		_countCharacters = characters;
	}

	/**
	 * 帳戶已登入
	 * 
	 * @return 傳出 _isLoad true:登入 false:未登入
	 */
	public boolean is_isLoad() {
		return _isLoad;
	}

	/**
	 * @param load 對 _isLoad 進行設置
	 */
	public void set_isLoad(final boolean load) {
		_isLoad = load;
	}

	/**
	 * 帳戶登入服務器編號
	 * 
	 * @param server_no
	 */
	public void set_server_no(final int server_no) {
		_server_no = server_no;
	}

	/**
	 * 帳戶登入服務器編號
	 * 
	 * @return
	 */
	public int get_server_no() {
		return _server_no;
	}

	/**
	 * 金流串接
	 * 
	 * @return
	 */
	public void set_point(int point) {
		_point = point;
	}

	/**
	 * 金流串接
	 * 
	 * @return
	 */
	public int get_point() {
		return _point;
	}
}