/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.model.TimeLimit;

import java.util.ArrayList;
import java.util.List;

//import javolution.util.FastTable;

class L1TimeLimitBuyOrder {
	private final L1ShopTimeLimit _item;
	private final int _count;

	public L1TimeLimitBuyOrder(L1ShopTimeLimit item, int count) {
		_item = item;
		_count = Math.max(1, count);
	}

	public L1ShopTimeLimit getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}
}

public class L1TimeLimitBuyOrderList {
	//private static final Log _log = LogFactory .getLog(L1TimeLimitBuyOrderList.class);

	private final L1TimeLimit _shop;
	private final List<L1TimeLimitBuyOrder> _list = new ArrayList<L1TimeLimitBuyOrder>();

	private int _totalWeight = 0;
	private int _totalPrice = 0;
	private int _Currency = 0;

	L1TimeLimitBuyOrderList(L1TimeLimit shop) {
		_shop = shop;
	}

	public void add(int orderNumber, int count) {
		if (_shop.getSellingItems().size() < orderNumber) {
			return;
		}
		if (count <= 0) {
			return;
		}
		if (count > 1000) {
			return;
		}
		L1ShopTimeLimit shopItem = _shop.getSellingItems().get(orderNumber);
		int price = (int) (shopItem.getPrice());
		for (int j = 0; j < count; j++) {
			if (price * j < 0) {
				return;
			}
		}

		if (_totalPrice < 0) {
			return;
		}

		_totalPrice += price * count;
		_Currency = shopItem.getCurrency();
		_totalWeight += shopItem.getItem().getWeight() * count;

		if (shopItem.getItem().isStackable()) {
			_list.add(new L1TimeLimitBuyOrder(shopItem, count));
			return;
		}

		for (int i = 0; i < (count); i++) {
			_list.add(new L1TimeLimitBuyOrder(shopItem, 1));
		}
	}

	List<L1TimeLimitBuyOrder> getList() {
		return _list;
	}

	/**
	 * 傳回重量
	 * @return
	 */
	public int getTotalWeight() {
		return _totalWeight;
	}

	/**
	 * 傳回價格
	 * @return
	 */
	public int getTotalPrice() {
		return _totalPrice;
	}
	
	/**
	 * 傳回貨幣ID
	 * @return
	 */
	public int getCurrency() {
		return _Currency;
	}
}
