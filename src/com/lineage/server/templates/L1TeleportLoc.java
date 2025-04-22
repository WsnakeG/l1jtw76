package com.lineage.server.templates;

/**
 * 傳送點資料
 * 
 * @author daien
 */
public class L1TeleportLoc {

	private int _id;

	private String _name;

	private String _orderid;

	private int _locx;

	private int _locy;

	private int _mapid;

	private int _itemid;

	private int _price;

	private int _time;

	private int _user;

	private int _min;

	private int _max;

	/**
	 * @return 傳出 _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * @param _id 對 _id 進行設置
	 */
	public void set_id(final int _id) {
		this._id = _id;
	}

	/**
	 * @return 傳出 _name
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * @param _name 對 _name 進行設置
	 */
	public void set_name(final String _name) {
		this._name = _name;
	}

	/**
	 * @return 傳出 _orderid
	 */
	public String get_orderid() {
		return _orderid;
	}

	/**
	 * @param _orderid 對 _orderid 進行設置
	 */
	public void set_orderid(final String _orderid) {
		this._orderid = _orderid;
	}

	/**
	 * @return 傳出 _locx
	 */
	public int get_locx() {
		return _locx;
	}

	/**
	 * @param _locx 對 _locx 進行設置
	 */
	public void set_locx(final int _locx) {
		this._locx = _locx;
	}

	/**
	 * @return 傳出 _locy
	 */
	public int get_locy() {
		return _locy;
	}

	/**
	 * @param _locy 對 _locy 進行設置
	 */
	public void set_locy(final int _locy) {
		this._locy = _locy;
	}

	/**
	 * @return 傳出 _mapid
	 */
	public int get_mapid() {
		return _mapid;
	}

	/**
	 * @param _mapid 對 _mapid 進行設置
	 */
	public void set_mapid(final int _mapid) {
		this._mapid = _mapid;
	}

	/**
	 * @return 傳出 _itemid
	 */
	public int get_itemid() {
		return _itemid;
	}

	/**
	 * @param _itemid 對 _itemid 進行設置
	 */
	public void set_itemid(final int _itemid) {
		this._itemid = _itemid;
	}

	/**
	 * @return 傳出 _price
	 */
	public int get_price() {
		return _price;
	}

	/**
	 * @param _price 對 _price 進行設置
	 */
	public void set_price(final int _price) {
		this._price = _price;
	}

	/**
	 * @return 傳出 _time
	 */
	public int get_time() {
		return _time;
	}

	/**
	 * @param _time 對 _time 進行設置
	 */
	public void set_time(final int _time) {
		this._time = _time;
	}

	/**
	 * @return 傳出 _user
	 */
	public int get_user() {
		return _user;
	}

	/**
	 * @param user 對 _user 進行設置
	 */
	public void set_user(final int user) {
		_user = user;
	}

	public int get_min() {
		return _min;
	}

	public void set_min(final int _min) {
		this._min = _min;
	}

	public int get_max() {
		return _max;
	}

	public void set_max(final int _max) {
		this._max = _max;
	}
	
	private String _showMapNane;

	public String get_showMapNane() {
		return _showMapNane;
	}

	public void set_showMapNane(final String aaa) {
		this._showMapNane = aaa;
	}
	
	private int _count;

	public int get_count() {
		return _count;
	}

	public void set_count(final int aaa) {
		this._count = aaa;
	}
}
