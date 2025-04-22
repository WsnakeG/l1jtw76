package com.lineage.server.templates;

import java.sql.Timestamp;

/**
 * 潘朵拉幸運抽獎
 * 
 * @author simlin
 */
public class L1LotteryWarehouse {

	private int _orderId;

	private int _charId;

	private int _itemId;

	private int _itemCount;

	private int _enchantLevel;

	private String _itemName;

	private Timestamp _time;

	public Timestamp getTime() {
		return _time;
	}

	public void setTime(final Timestamp time) {
		_time = time;
	}

	public String getItemName() {
		return _itemName;
	}

	public void setItemName(final String itemName) {
		_itemName = itemName;
	}

	public int getOrderId() {
		return _orderId;
	}

	public void setOrderId(final int orderId) {
		_orderId = orderId;
	}

	public int getCharId() {
		return _charId;
	}

	public void setCharId(final int charId) {
		_charId = charId;
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(final int itemId) {
		_itemId = itemId;
	}

	public int getItemCount() {
		return _itemCount;
	}

	public void setItemCount(final int itemCount) {
		_itemCount = itemCount;
	}

	public int getEnchantLevel() {
		return _enchantLevel;
	}

	public void setEnchantLevel(final int enchantLevel) {
		_enchantLevel = enchantLevel;
	}
}
