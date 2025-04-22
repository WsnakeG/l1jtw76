package com.lineage.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import com.lineage.server.model.Instance.L1PcInstance;

class L1ShopSellOrder {

	private final L1AssessedItem _item;

	private final int _count;

	public L1ShopSellOrder(final L1AssessedItem item, final int count) {
		_item = item;
		_count = Math.max(0, count);
	}

	public L1AssessedItem getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}

}

public class L1ShopSellOrderList {
	private final L1Shop _shop;
	private final L1PcInstance _pc;
	private final List<L1ShopSellOrder> _list = new ArrayList<L1ShopSellOrder>();

	L1ShopSellOrderList(final L1Shop shop, final L1PcInstance pc) {
		_shop = shop;
		_pc = pc;
	}

	public void add(final int itemObjectId, final int count) {
		final L1AssessedItem assessedItem = _shop.assessItem(_pc.getInventory().getItem(itemObjectId));
		if (assessedItem == null) {
			/*
			 * 買取リストに無いアイテムが指定された。 不正パケの可能性。
			 */
			throw new IllegalArgumentException();
		}

		_list.add(new L1ShopSellOrder(assessedItem, count));
	}

	L1PcInstance getPc() {
		return _pc;
	}

	List<L1ShopSellOrder> getList() {
		return _list;
	}
}
