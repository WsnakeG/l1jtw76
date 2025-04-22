package com.lineage.server.templates;

/**
 * 物品升級資料暫存
 * 
 * @author DaiEn
 */
public class L1ItemUpdate {

	public static final String _html_01 = "y_up_i0";

	public static final String _html_02 = "y_up_i1";

	public static final String _html_03 = "y_up_i2";

	private int _item_id;

	private int _toid;

	private int[] _needids = null;

	private int[] _needcounts = null;

	private int _chance; // 合成機率 by terry0412

	public int get_item_id() {
		return _item_id;
	}

	public void set_item_id(final int _item_id) {
		this._item_id = _item_id;
	}

	public int get_toid() {
		return _toid;
	}

	public void set_toid(final int _toid) {
		this._toid = _toid;
	}

	public int[] get_needids() {
		return _needids;
	}

	public void set_needids(final int[] _needids) {
		this._needids = _needids;
	}

	public int[] get_needcounts() {
		return _needcounts;
	}

	public void set_needcounts(final int[] _needcounts) {
		this._needcounts = _needcounts;
	}

	// 合成機率 by terry0412
	public int get_chance() {
		return _chance;
	}

	public void set_chance(final int _chance) {
		this._chance = _chance;
	}
}
