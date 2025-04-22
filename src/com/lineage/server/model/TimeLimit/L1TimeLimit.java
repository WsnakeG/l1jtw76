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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;

//import javolution.util.FastTable;

/**
 * 自定義貨幣商城購物 類名稱：L1TimeLimit<br>
 * 修改時間：2018年9月4日 下午7:18:54<br>
 * 修改備註:<br>
 * 
 * @version 2.7c<br>
 */
public class L1TimeLimit {
	private static final Log _log = LogFactory.getLog(L1TimeLimit.class);
	private final int _npcId;
	private final List<L1ShopTimeLimit> _sellingItems;
	private final List<L1ShopTimeLimit> _purchasingItems;

	public L1TimeLimit(int npcId, List<L1ShopTimeLimit> sellingItems, List<L1ShopTimeLimit> purchasingItems) {
		if (sellingItems == null || purchasingItems == null) {
			throw new NullPointerException();
		}

		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;

	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopTimeLimit> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * 傳回指定itemid的售賣信息
	 * 
	 * @param itemId
	 * @return
	 */
	private L1ShopTimeLimit getSellingItem(int itemId) {
		for (L1ShopTimeLimit shopItem : _sellingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	/**
	 * 商店、指定買取可能狀態返。
	 * 
	 * @param item
	 * @return 買取可能true
	 */
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // 裝備中不可
			return false;
		}
		if (item.getEnchantLevel() != 0) { // 強化(or弱化)不可
			return false;
		}

		return true;
	}

	/**
	 * 傳回回收指定itemid信息
	 * 
	 * @param itemId
	 * @return
	 */
	private L1ShopTimeLimit getPurchasingItem(int itemId) {
		for (L1ShopTimeLimit shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1TimeLimitAssessedItem assessItem(L1ItemInstance item) {
		L1ShopTimeLimit shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1TimeLimitAssessedItem(item.getId(), getAssessedPrice(shopItem), shopItem.getCurrency());
	}

	/**
	 * 傳回售賣價格
	 * 
	 * @param item
	 * @return
	 */
	private int getAssessedPrice(L1ShopTimeLimit item) {
		return (int) (item.getPrice());
	}

	/**
	 * 傳回售賣的貨幣ID
	 * 
	 * @param item
	 * @return
	 */
	private int getCurrency(L1ShopTimeLimit item) {
		return item.getCurrency();
	}

	/**
	 * 內買取可能查定。
	 * 
	 * @param inv
	 *            查定對像
	 * @return 查定買取可能
	 */
	public List<L1TimeLimitAssessedItem> assessItems(L1PcInventory inv) {
		List<L1TimeLimitAssessedItem> result = new ArrayList<L1TimeLimitAssessedItem>();
		for (L1ShopTimeLimit item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}

				result.add(new L1TimeLimitAssessedItem(targetItem.getId(), getAssessedPrice(item), getCurrency(item)));
			}
		}
		return result;
	}

	/**
	 * 販賣保證。
	 * 
	 * @return 何理由販賣場合、false
	 */
	private boolean ensureSell(L1PcInstance pc, L1TimeLimitBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		// 檢查販賣物品總價
		if (!IntRange.includes(price, 0, 100000000)) {
			// 904 總共販賣價格無法超過 %d金幣。
			pc.sendPackets(new S_ServerMessage(904, "100,000,000"));
			return false;
		}
		// 取回指定物品ID的屬性
		final L1Item adenaName = ItemTable.get().getTemplate(orderList.getCurrency());
		// 檢查背包金幣數量
		if (!pc.getInventory().checkItem(orderList.getCurrency(), price)) {
			pc.sendPackets(new S_SystemMessage(adenaName.getName() + " 數量不足。"));
			return false;
		}
		// 檢查重量限制
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 82 此物品太重了，所以你無法攜帶。
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 檢查買入數量
		int totalCount = pc.getInventory().getSize();
		for (L1TimeLimitBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// 263 \f1一個角色最多可攜帶180個道具。
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		return true;
	}

	/**
	 * 取回買入物品
	 */
	private void sellItems(L1PcInventory inv, L1TimeLimitBuyOrderList orderList) {
		try {
			for (L1TimeLimitBuyOrder order : orderList.getList()) {
				ShopTimeLimitTable Limit = new ShopTimeLimitTable();
				int itemId = order.getItem().getItemId();
				int amount = order.getCount();
				int level = order.getItem().getEnlvl();
				// int Price = getSellingItem(itemId).getPrice();
				// 取回指定物品ID的屬性
				final L1Item adenaName = ItemTable.get().getTemplate(orderList.getCurrency());
				if (order.getItem().get_isall() == 0) { // 當物品等於0時,為全服限購
					if (Limit.getEndCount(itemId) < amount) {
						inv.getOwner().sendPackets(new S_SystemMessage("當前全服限時物品數量不足:" + amount + "個,剩餘:" + Limit.getEndCount(itemId) + "個。"));
						return;
					}
				} else if (order.getItem().get_isall() == 1) {// 當物品等於1時,為個人限購
					TimeLimitCharTable LimitChar = new TimeLimitCharTable();
					if (LimitChar.getCount(inv.getOwner().getId(), itemId) < amount) {
						inv.getOwner().sendPackets(new S_SystemMessage("當前個人限時物品數量不足:" + amount + "個,剩餘:" + LimitChar.getCount(inv.getOwner().getId(), itemId) + "個。"));
						return;
					}
				}
				L1ItemInstance item = ItemTable.get().createItem(itemId);
				item.setCount(amount);
				item.setIdentified(true);
				item.setEnchantLevel(level);
				// 更新剩餘數量
				if (order.getItem().get_isall() == 0) { // 當物品等於0時,為全服限購
					// 更新全服限購數量
					Limit.updateendcount(itemId, Limit.getEndCount(itemId) - amount);
					/*
					 * WriteLogTxt.Recording("全服限購購買記錄", "玩家" + inv.getOwner().getName() + "花費" + adenaName.getName() + "(" + Price + ")" + "購入物品" + item.getName() + "(" + item.getCount()
					 * + ")" + "。");
					 */
				} else if (order.getItem().get_isall() == 1) {// 當物品等於1時,為個人限購
					// 更新個人限購數量
					TimeLimitCharTable LimitChar = new TimeLimitCharTable();
					LimitChar.upCount(LimitChar.getCount(inv.getOwner().getId(), itemId) - amount, inv.getOwner().getId(), itemId);
					/*
					 * WriteLogTxt.Recording("個人限購購買記錄", "玩家" + inv.getOwner().getName() + "花費" + adenaName.getName() + "(" + Price + ")" + "購入物品" + item.getName() + "(" + item.getCount()
					 * + ")" + "。");
					 */
				}

				if (!inv.consumeItem(orderList.getCurrency(), amount * getSellingItem(itemId).getPrice())) {
					inv.getOwner().sendPackets(new S_SystemMessage(adenaName.getName() + " 不足."));
					return;// 購買物品時金幣不足
				}
				inv.storeItem(item);
				inv.getOwner().sendPackets(new S_SystemMessage("獲得 +" + level + " " + item.getName() + "。"));
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 、L1TimeLimitBuyOrderList記載販賣。
	 * 
	 * @param pc
	 *            販賣
	 * @param orderList
	 *            販賣記載L1TimeLimitBuyOrderList
	 */
	public void sellItems(L1PcInstance pc, L1TimeLimitBuyOrderList orderList) {
		if (!ensureSell(pc, orderList)) {
			return;
		}
		sellItems(pc.getInventory(), orderList);
	}

	public L1TimeLimitBuyOrderList newBuyOrderList() {
		return new L1TimeLimitBuyOrderList(this);
	}

	public L1TimeLimitSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1TimeLimitSellOrderList(this, pc);
	}
}
