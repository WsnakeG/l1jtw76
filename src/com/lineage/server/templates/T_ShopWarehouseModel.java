package com.lineage.server.templates;

/**
 * 商城倉庫暫存
 * 
 * @author simlin
 */
public class T_ShopWarehouseModel {

	private int _id;

	private String _accountName;

	private int _objId;

	private int _itemId;

	private String _itemName;

	private int _itemCount;

	private int _itemBless;

	private int _itemEnchantLevel;

	public T_ShopWarehouseModel(final int id, final String accountName, final int objId, final int itemId,
			final String itemName, final int itemCount, final int itemBless, final int itemEnchantLevel) {
		_id = id;
		_accountName = accountName;
		_objId = objId;
		_itemId = itemId;
		_itemName = itemName;
		_itemCount = itemCount;
		_itemBless = itemBless;
		_itemEnchantLevel = itemEnchantLevel;
	}

	public int getId() {
		return _id;
	}

	public void setId(final int id) {
		_id = id;
	}

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(final String accountName) {
		_accountName = accountName;
	}

	public int getObjId() {
		return _objId;
	}

	public void setObjId(final int objId) {
		_objId = objId;
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	public String getItemName() {
		return _itemName;
	}

	public void setItemName(final String itemName) {
		_itemName = itemName;
	}

	public int getItemCount() {
		return _itemCount;
	}

	public void setItemCount(final int itemCount) {
		_itemCount = itemCount;
	}

	public int getItemBless() {
		return _itemBless;
	}

	public void setItemBless(final int itemBless) {
		_itemBless = itemBless;
	}

	public int getEnchantLevel() {
		return _itemEnchantLevel;
	}

	public void setEnchantLevel(final int itemEnchantLevel) {
		_itemEnchantLevel = itemEnchantLevel;
	}
}