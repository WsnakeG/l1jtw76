package com.lineage.server.templates;

/**
 * 物品升級資料暫存
 * 
 * @author DaiEn
 */
public class L1ItemPowerUpdate {

	private int _itemid;// 要升級的物品ITEMID

	private int _nedid;// 需要物品

	private int _type_id;// 群組ID

	private int _order_id;// 群組排序

	private int _mode;// 0:無 1:強化失敗會倒退 2:強化失敗會消失 4:無法強化

	private int _random;// 強化機率(1/1000)

	public int get_itemid() {
		return _itemid;
	}

	public void set_itemid(final int itemid) {
		_itemid = itemid;
	}

	public int get_type_id() {
		return _type_id;
	}

	public void set_type_id(final int type_id) {
		_type_id = type_id;
	}

	public int get_order_id() {
		return _order_id;
	}

	public void set_order_id(final int order_id) {
		_order_id = order_id;
	}

	public int get_mode() {
		return _mode;
	}

	public void set_mode(final int mode) {
		_mode = mode;
	}

	public int get_random() {
		return _random;
	}

	public void set_random(final int random) {
		_random = random;
	}

	public int get_nedid() {
		return _nedid;
	}

	public void set_nedid(final int _nedid) {
		this._nedid = _nedid;
	}
}