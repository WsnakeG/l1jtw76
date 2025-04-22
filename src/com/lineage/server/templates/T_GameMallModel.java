package com.lineage.server.templates;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 商城清單暫存
 * 
 * @author simlin
 */
public class T_GameMallModel {

	private int _id;

	private L1ItemInstance _item;

	private int _shopItemDesc;

	private int _price;

	private int _sort;

	private boolean _newItem;

	private int _vipLevelItem;

	private boolean _hotItem;

	public T_GameMallModel(final int id, final L1ItemInstance item, final int shopItemDesc, final int price,
			final int sort, final boolean newItem, final int vipLevelItem, final boolean hotItem) {
		_id = id;
		_item = item;
		_shopItemDesc = shopItemDesc;
		_price = price;
		_sort = sort;
		_newItem = newItem;
		_vipLevelItem = vipLevelItem;
		_hotItem = hotItem;
	}

	public L1ItemInstance getMallItem() {
		return _item;
	}

	public void setMallItem(final L1ItemInstance item) {
		_item = item;
	}

	public int getShopItemDesc() {
		return _shopItemDesc;
	}

	public void setShopItemDesc(final int shopItemDesc) {
		_shopItemDesc = shopItemDesc;
	}

	public int getPrice() {
		return _price;
	}

	public void setPrice(final int price) {
		_price = price;
	}

	public int getSort() {
		return _sort;
	}

	public void setSort(final int sort) {
		_sort = sort;
	}

	public boolean isNewItem() {
		return _newItem;
	}

	public void setNewItem(final boolean newItem) {
		_newItem = newItem;
	}

	public int getVipLevel() {
		return _vipLevelItem;
	}

	public void setVipLevel(final int vipLevelItem) {
		_vipLevelItem = vipLevelItem;
	}

	public boolean isHotItem() {
		return _hotItem;
	}

	public void setHotItem(final boolean hotItem) {
		_hotItem = hotItem;
	}

	public int getMallId() {
		return _id;
	}

	public void setMallId(final int id) {
		_id = id;
	}
}