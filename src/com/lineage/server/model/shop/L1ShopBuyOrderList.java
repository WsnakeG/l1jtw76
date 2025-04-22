package com.lineage.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import com.lineage.config.ConfigRate;
import com.lineage.server.model.L1TaxCalculator;
import com.lineage.server.templates.L1ShopItem;

/**
 * @author dexc
 */
class L1ShopBuyOrder {

	private final L1ShopItem _item;

	private final int _count;

	public L1ShopBuyOrder(final L1ShopItem item, final int count) {
		_item = item;
		_count = Math.max(0, count);
	}

	public L1ShopItem getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}
}

/**
 * @author dexc
 */
public class L1ShopBuyOrderList {

	private final L1Shop _shop;

	private final List<L1ShopBuyOrder> _list = new ArrayList<L1ShopBuyOrder>();

	private final L1TaxCalculator _taxCalc;

	private int _totalWeight = 0;

	private int _totalPrice = 0;

	private int _totalPriceTaxIncluded = 0;

	public L1ShopBuyOrderList(final L1Shop shop) {
		_shop = shop;
		_taxCalc = new L1TaxCalculator(shop.getNpcId());
	}

	/**
	 * 如果列表不包含元素，则返回 true。
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return _list.isEmpty();
	}

	public void add(final int orderNumber, final int count) {
		if (_shop.getSellingItems().size() < orderNumber) {
			return;
		}
		int ch_count = Math.max(0, count);
		if (ch_count <= 0) {
			return;
		}

		final L1ShopItem shopItem = _shop.getSellingItems().get(orderNumber);

		final int price = (int) (shopItem.getPrice() * ConfigRate.RATE_SHOP_SELLING_PRICE);// 物品單價
		if (price < 1000) {
			ch_count = Math.min(count, 9999);
		} else if (price < 10000) {
			ch_count = Math.min(count, 999);
		} else if (price < 100000) {
			ch_count = Math.min(count, 99);
		} else if (price < 3000000) {
			ch_count = Math.min(count, 20);
		} else {
			ch_count = Math.min(count, 1);
		}

		if ((price * ch_count) < 0) {
			return;
		}

		if (_totalPrice < 0) {
			return;
		}
		_totalPrice += price * ch_count;
		_totalPriceTaxIncluded += _taxCalc.layTax(price) * ch_count;
		_totalWeight += shopItem.getItem().getWeight() * ch_count * shopItem.getPackCount();

		if (shopItem.getItem().isStackable()) {
			_list.add(new L1ShopBuyOrder(shopItem, ch_count * shopItem.getPackCount()));
			return;
		}

		for (int i = 0; i < (ch_count * shopItem.getPackCount()); i++) {
			_list.add(new L1ShopBuyOrder(shopItem, 1));
		}
	}

	List<L1ShopBuyOrder> getList() {
		return _list;
	}

	public int getTotalWeight() {
		return _totalWeight;
	}

	public int getTotalPrice() {
		return _totalPrice;
	}

	public int getTotalPriceTaxIncluded() {
		return _totalPriceTaxIncluded;
	}

	L1TaxCalculator getTaxCalculator() {
		return _taxCalc;
	}
}
