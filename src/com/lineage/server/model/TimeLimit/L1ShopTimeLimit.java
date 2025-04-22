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

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.templates.L1Item;

/**
 * 限時商人
 * 類名稱：L1CenterItem<br>
 * 修改備註:<br>
 * @version 2.7c<br>
 */
public class L1ShopTimeLimit {

	private final int _itemId;

	private final L1Item _item;

	private final int _price;

	private final int _enlvl;
	
	private final int _Currency;
	
	private final int _isall;

	public L1ShopTimeLimit(int itemId, int price, int enlvl, int Currency, int isall) {
		_itemId = itemId;
		_item = ItemTable.get().getTemplate(itemId);
		_price = price;
		_enlvl = enlvl;
		_Currency = Currency;
		_isall = isall;
	}

	/**
	 * 傳回物品ID
	 * @return
	 */
	public int getItemId() {
		return _itemId;
	}

	public L1Item getItem() {
		return _item;
	}

	/**
	 * 傳回售價
	 * @return
	 */
	public int getPrice() {
		return _price;
	}

	/**
	 * 傳回售賣強化等級
	 * @return
	 */
	public int getEnlvl() {
		return _enlvl;
	}
	
	/**
	 * 傳回售賣貨幣ID
	 * @return
	 */
	public int getCurrency() {
		return _Currency;
	}
	
	/**
	 * 傳回物品是全服限購還是個人限購
	 * @return 0:全服  1:個人
	 */
	public int get_isall() {
		return _isall;
	}
}
