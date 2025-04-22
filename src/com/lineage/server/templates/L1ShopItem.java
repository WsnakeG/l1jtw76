package com.lineage.server.templates;

import com.lineage.server.datatables.ItemTable;

/**
 * 貨幣商品
 * 
 * @author daien
 */
public class L1ShopItem {

	private final int _itemId;// item id

	private final L1Item _item;// item

	private final int _price;// 售價

	private final int _packCount;// 數量

	private final int _enchantLevel; // 強化等級 by terry0412

	private int _purchasing_price;// NPC回收該道具所需商幣數量 by terry0412
	
	private final int _checkclass;

	public L1ShopItem(final int itemId, final int price, final int packCount, final int enchantLevel,final int checkclass) {
		_itemId = itemId;
		_item = ItemTable.get().getTemplate(itemId);
		_price = price;
		_packCount = packCount;
		_enchantLevel = enchantLevel;
//		_purchasing_price = purchasing_price;
		_checkclass = checkclass;

	}

//	public L1ShopItem(final int itemId, final int price, final int packCount, final int enchantLevel, final int checkclass) {
//		_itemId = itemId;
//		_item = ItemTable.get().getTemplate(itemId);
//		_price = price;
//		_packCount = packCount;
//		_enchantLevel = enchantLevel;
//		this._checkclass = checkclass;
//	}

	public int getItemId() {
		return _itemId;
	}

	public L1Item getItem() {
		return _item;
	}

	public int getPrice() {
		return _price;
	}

	public int getPackCount() {
		return _packCount;
	}

	public int getEnchantLevel() { // 強化等級 by terry0412
		return _enchantLevel;
	}

	public int getPurchasingPrice() { // NPC回收該道具所需商幣數量
		return _purchasing_price;
	}
	
	public int getCheckClass() {
		return this._checkclass;
	}
}
