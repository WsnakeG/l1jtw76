package com.lineage.server.templates;

/**
 * 回收物品資料
 * 
 * @author admin
 */
public class L1PrivateShopBuyList {

	public L1PrivateShopBuyList() {
	}

	private int _itemObjectId;

	/**
	 * 設置要購買的物品OBJID
	 * 
	 * @param i
	 */
	public void setItemObjectId(final int i) {
		_itemObjectId = i;
	}

	/**
	 * 傳回要購買的物品OBJID
	 * 
	 * @return
	 */
	public int getItemObjectId() {
		return _itemObjectId;
	}

	private int _buyTotalCount; // 預計買入數量

	/**
	 * 設置預計買入數量
	 * 
	 * @param i
	 */
	public void setBuyTotalCount(final int i) {
		_buyTotalCount = i;
	}

	/**
	 * 傳回預計買入數量
	 * 
	 * @return
	 */
	public int getBuyTotalCount() {
		return _buyTotalCount;
	}

	private int _buyPrice; // 預計價格

	/**
	 * 設置回收價格
	 * 
	 * @param i
	 */
	public void setBuyPrice(final int i) {
		_buyPrice = i;
	}

	/**
	 * 回收價格
	 * 
	 * @return
	 */
	public int getBuyPrice() {
		return _buyPrice;
	}

	private int _buyCount; // 買入數量累計

	/**
	 * 設置買入數量累計
	 * 
	 * @param i
	 */
	public void setBuyCount(final int i) {
		_buyCount = i;
	}

	/**
	 * 傳回買入數量累計
	 * 
	 * @return
	 */
	public int getBuyCount() {
		return _buyCount;
	}
}
