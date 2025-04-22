package com.lineage.server.templates;

import com.lineage.server.model.c1.C1Executor;

/**
 * 陣營階級能力資料
 * 
 * @author daien
 */
public class L1Name_Power {

	private int _c1_id;// 階級

	private int _c1_type;// 陣營

	private String _c1_name_type;// 階級名稱

	private C1Executor _c1_classname;// CLASS位置

	private int _set;// 需要積分

	private int _down;// 死亡積分減少

	private int _gift_box; // 禮物寶箱 by terry0412

	/**
	 * 階級
	 * 
	 * @return
	 */
	public int get_c1_id() {
		return _c1_id;
	}

	/**
	 * 階級
	 * 
	 * @param _c1_id
	 */
	public void set_c1_id(final int _c1_id) {
		this._c1_id = _c1_id;
	}

	/**
	 * 陣營
	 * 
	 * @return
	 */
	public int get_c1_type() {
		return _c1_type;
	}

	/**
	 * 陣營
	 * 
	 * @param _c1_type
	 */
	public void set_c1_type(final int _c1_type) {
		this._c1_type = _c1_type;
	}

	/**
	 * 階級名稱
	 * 
	 * @return
	 */
	public String get_c1_name_type() {
		return _c1_name_type;
	}

	/**
	 * 階級名稱
	 * 
	 * @param _c1_name_type
	 */
	public void set_c1_name_type(final String _c1_name_type) {
		this._c1_name_type = _c1_name_type;
	}

	/**
	 * CLASS
	 * 
	 * @return
	 */
	public C1Executor get_c1_classname() {
		return _c1_classname;
	}

	/**
	 * CLASS
	 * 
	 * @param _c1_classname
	 */
	public void set_c1_classname(final C1Executor _c1_classname) {
		this._c1_classname = _c1_classname;
	}

	/**
	 * 需要積分
	 * 
	 * @return
	 */
	public int get_set() {
		return _set;
	}

	/**
	 * 需要積分
	 * 
	 * @param _set
	 */
	public void set_set(final int _set) {
		this._set = _set;
	}

	/**
	 * 死亡積分減少
	 * 
	 * @return
	 */
	public int get_down() {
		return _down;
	}

	/**
	 * 死亡積分減少
	 * 
	 * @param _set
	 */
	public void set_down(final int _down) {
		this._down = _down;
	}

	/**
	 * 禮物寶箱 by terry0412
	 * 
	 * @return
	 */
	public int get_gift_box() {
		return _gift_box;
	}

	/**
	 * 禮物寶箱 by terry0412
	 * 
	 * @param _set
	 */
	public void set_gift_box(final int _gift_box) {
		this._gift_box = _gift_box;
	}
}
