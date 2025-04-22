package com.lineage.server.templates;

/**
 * 虛擬商店買賣物品
 * 
 * @author dexc
 */
public class DeShopItem {

	private int _id;

	private int _item_id;

	private int _price;

	private int _sellcount;

	private int _buycount;

	private int _enchantlvl;

	/**
	 * @return 傳出 _id
	 */
	public int get_id() {
		return _id;
	}

	/**
	 * @param _id 對 _id 進行設置
	 */
	public void set_id(final int id) {
		_id = id;
	}

	/**
	 * @return 傳出 _item_id
	 */
	public int get_item_id() {
		return _item_id;
	}

	/**
	 * @param _item_id 對 _item_id 進行設置
	 */
	public void set_item_id(final int item_id) {
		_item_id = item_id;
	}

	/**
	 * @return 傳出 _price
	 */
	public int get_price() {
		return _price;
	}

	/**
	 * @param _price 對 _price 進行設置
	 */
	public void set_price(final int price) {
		_price = price;
	}

	/**
	 * @return 傳出 _sellcount
	 */
	public int get_sellcount() {
		return _sellcount;
	}

	/**
	 * @param _sellcount 對 _sellcount 進行設置
	 */
	public void set_sellcount(final int sellcount) {
		_sellcount = sellcount;
	}

	/**
	 * @return 傳出 _buycount
	 */
	public int get_buycount() {
		return _buycount;
	}

	/**
	 * @param _buycount 對 _buycount 進行設置
	 */
	public void set_buycount(final int buycount) {
		_buycount = buycount;
	}

	/**
	 * @return 傳出 _enchantlvl
	 */
	public int get_enchantlvl() {
		return _enchantlvl;
	}

	/**
	 * @param _enchantlvl 對 _enchantlvl 進行設置
	 */
	public void set_enchantlvl(final int enchantlvl) {
		_enchantlvl = enchantlvl;
	}

}
