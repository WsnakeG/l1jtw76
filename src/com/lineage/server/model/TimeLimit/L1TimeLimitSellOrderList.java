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

import com.lineage.server.model.Instance.L1PcInstance;

//import javolution.util.FastTable;

class L1TimeLimitSellOrder {
	private final L1TimeLimitAssessedItem _item;
	private final int _count;

	public L1TimeLimitSellOrder(L1TimeLimitAssessedItem item, int count) {
		_item = item;
		_count = Math.max(1, count);
	}

	public L1TimeLimitAssessedItem getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}

}

public class L1TimeLimitSellOrderList {
	private final L1TimeLimit _shop;
	private final L1PcInstance _pc;
	private final List<L1TimeLimitSellOrder> _list = new ArrayList<L1TimeLimitSellOrder>();

	L1TimeLimitSellOrderList(L1TimeLimit shop, L1PcInstance pc) {
		_shop = shop;
		_pc = pc;
	}

	public void add(int itemObjectId, int count) {
		L1TimeLimitAssessedItem assessedItem = _shop.assessItem(_pc.getInventory().getItem(itemObjectId));
		if (assessedItem == null) {
			/*
			 * 買取無指定。 不正可能性。
			 */
			throw new IllegalArgumentException();
		}

		_list.add(new L1TimeLimitSellOrder(assessedItem, count));
	}

	L1PcInstance getPc() {
		return _pc;
	}

	List<L1TimeLimitSellOrder> getList() {
		return _list;
	}
}
