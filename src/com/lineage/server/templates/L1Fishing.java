package com.lineage.server.templates;

/**
 * 漁獲資料暫存
 * 
 * @author daien
 */
public class L1Fishing {

	private int _itemid;// 物品編號

	private int _randomint;// 比對用機率

	private int _random;// 機率

	private int _count;// 給予數量

	private int _bait;// 需要魚餌的數量

	public int get_itemid() {
		return _itemid;
	}

	public void set_itemid(final int _itemid) {
		this._itemid = _itemid;
	}

	public int get_randomint() {
		return _randomint;
	}

	public void set_randomint(final int _randomint) {
		this._randomint = _randomint;
	}

	public int get_random() {
		return _random;
	}

	public void set_random(final int _random) {
		this._random = _random;
	}

	public int get_count() {
		return _count;
	}

	public void set_count(final int _count) {
		this._count = _count;
	}

	public int get_bait() {
		return _bait;
	}

	public void set_bait(final int bait) {
		this._bait = bait;
	}
}