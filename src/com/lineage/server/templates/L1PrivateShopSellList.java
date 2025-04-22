package com.lineage.server.templates;

/**
 * 個人商店出售物品的資料緩存
 * 
 * @author admin
 */
public class L1PrivateShopSellList {

	public L1PrivateShopSellList() {
	}

	private int _itemObjectId;// 物品objid

	/**
	 * 設置物品objid
	 * 
	 * @param i
	 */
	public void setItemObjectId(final int i) {
		_itemObjectId = i;
	}

	/**
	 * 傳回物品objid
	 * 
	 * @return
	 */
	public int getItemObjectId() {
		return _itemObjectId;
	}

	private int _sellTotalCount; // 預計賣出的數量

	/**
	 * 設置預計賣出的數量
	 * 
	 * @param i
	 */
	public void setSellTotalCount(final int i) {
		_sellTotalCount = i;
	}

	/**
	 * 傳回預計賣出的數量
	 * 
	 * @return
	 */
	public int getSellTotalCount() {
		return _sellTotalCount;
	}

	private int _sellPrice;// 售價

	/**
	 * 設置售價
	 * 
	 * @param i
	 */
	public void setSellPrice(final int i) {
		_sellPrice = i;
	}

	/**
	 * 傳回售價
	 * 
	 * @return
	 */
	public int getSellPrice() {
		return _sellPrice;
	}

	private int _sellCount; // 已經賣出數量的累計

	/**
	 * 設置已經賣出數量的累計
	 * 
	 * @param i
	 */
	public void setSellCount(final int i) {
		_sellCount = i;
	}

	/**
	 * 傳回已經賣出數量的累計
	 * 
	 * @return
	 */
	public int getSellCount() {
		return _sellCount;
	}
}
