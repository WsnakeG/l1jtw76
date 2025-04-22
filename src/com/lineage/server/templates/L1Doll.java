package com.lineage.server.templates;

import java.util.ArrayList;

import com.lineage.server.model.doll.L1DollExecutor;

/**
 * 娃娃能力資料
 * 
 * @author daien
 */
public class L1Doll {

	private int _itemid;

	private ArrayList<L1DollExecutor> powerList = null;

	private int[] _need;

	private int[] _counts;

	private int _time;

	private int _gfxid;

	private String _nameid;

	private int _level;
	
	public int get_itemid() {
		return _itemid;
	}

	public void set_itemid(final int _itemid) {
		this._itemid = _itemid;
	}

	public ArrayList<L1DollExecutor> getPowerList() {
		return powerList;
	}

	public void setPowerList(final ArrayList<L1DollExecutor> powerList) {
		this.powerList = powerList;
	}

	public int[] get_need() {
		return _need;
	}

	public void set_need(final int[] _need) {
		this._need = _need;
	}

	public int[] get_counts() {
		return _counts;
	}

	public void set_counts(final int[] _counts) {
		this._counts = _counts;
	}

	public int get_gfxid() {
		return _gfxid;
	}

	public void set_gfxid(final int _gfxid) {
		this._gfxid = _gfxid;
	}

	public String get_nameid() {
		return _nameid;
	}

	public void set_nameid(final String _nameid) {
		this._nameid = _nameid;
	}

	public int get_time() {
		return _time;
	}

	public void set_time(final int _time) {
		this._time = _time;
	}
	
	public int get_level() {
		return _level;
	}

	public void set_level(final int _level) {
		this._level = _level;
	}
}
