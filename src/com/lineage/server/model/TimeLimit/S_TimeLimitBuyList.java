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

import java.util.List;
import java.util.Map;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

/**
 * 購買限時商人物品 類名稱：S_TimeLimitBuyList<br>
 * 修改備註:<br>
 * 
 * @version 2.7c<br>
 */
public class S_TimeLimitBuyList extends ServerBasePacket {
	private byte[] _byte = null;

	/**
	 * 店品物表示。BUY押時送。
	 */
	public S_TimeLimitBuyList(int npcId, L1PcInstance pc) {
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(npcId);

		L1Object npcObj = World.get().findObject(npcId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcIdd = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		L1TimeLimit shop = ShopTimeLimitTable.getInstance().get(npcIdd);
		List<L1ShopTimeLimit> shopItems = shop.getSellingItems();

		writeH(shopItems.size());
		L1ItemInstance dummy = new L1ItemInstance();

		ShopTimeLimitTable sss = new ShopTimeLimitTable();
		TimeLimitCharTable LimitChar = new TimeLimitCharTable();
		
		if (LimitChar.loadItems(pc.getId()) == null) {// 如果人物限購數據不存在,則加入限購數據
			
			
			//------------------------------------------------------------------
			Map<Integer, L1TimeLimit> shopmap = ShopTimeLimitTable.getInstance().getall();
			for (Map.Entry<Integer, L1TimeLimit> entry : shopmap.entrySet()) {
				Integer key = entry.getKey();

				L1TimeLimit npcshop = ShopTimeLimitTable.getInstance().get(key);
				List<L1ShopTimeLimit> itemlist = npcshop.getSellingItems();
				for (L1ShopTimeLimit shopitem : itemlist) {
//					if (shopitem.get_isall() == 0) {// 過濾全服限購物品
//						continue;
//					}
					L1TimeLimitChar limititem = new L1TimeLimitChar();
					limititem.set_charobjid(pc.getId());
					limititem.set_itemid(shopitem.getItemId());
					LimitChar.storitem(pc.getId(), limititem);
				}
			}
			//------------------------------------------------------------------
			
			
			
			
			/*
			 * for (int i = 0; i < shopItems.size(); i++) { L1ShopTimeLimit shopItem = shopItems.get(i); if (shopItem.get_isall() == 0) {// 過濾全服限購物品 continue; } L1TimeLimitChar limititem =
			 * new L1TimeLimitChar(); limititem.set_charobjid(pc.getId()); limititem.set_itemid(shopItem.getItemId()); LimitChar.storitem(pc.getId(), limititem); }
			 */
		}

		for (int i = 0; i < shopItems.size(); i++) {
			L1ShopTimeLimit shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			int price = (shopItem.getPrice());
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
			writeD(price);
//			if (shopItem.get_isall() == 0) { // 當物品等於0時,為全服限購
//				if (sss.getEndCount(shopItem.getItemId()) > 0) {
//					if (shopItem.getEnlvl() > 0) {
//						writeS("+" + shopItem.getEnlvl() + " " + item.getName() + " 剩餘數量:" + sss.getEndCount(item.getItemId()));
//					} else {
//						writeS(item.getNameId() + " 剩餘數量:" + sss.getEndCount(item.getItemId()));
//					}
//				} else {
//					this.writeS(sss.getEndName(item.getItemId()));
//				}
//			} else if (shopItem.get_isall() == 1) { // 當物品等於1時,為個人限購
				if (LimitChar.getUserItems(pc.getId(), shopItem.getItemId())) {// 還有可購買次數
					if (shopItem.getEnlvl() > 0) {
						writeS("+" + shopItem.getEnlvl() + " " + item.getName() + " 限購:" + LimitChar.getCount(pc.getId(), shopItem.getItemId()));
					} else {
						writeS(item.getNameId() + " 限購:" + LimitChar.getCount(pc.getId(), shopItem.getItemId()));
					}
				} else {
					this.writeS(sss.getEndName(item.getItemId()));
				}
//			}

			L1Item template = ItemTable.get().getTemplate(item.getItemId());
			this.writeD(template.getUseType());// XXX 7.6新增商品分類
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
//		writeH(2336);
		writeH(0x17d4);
	}

	// //@Override
	public byte[] getContent() {
		return getBytes();
	}
	
	@Override
	public byte[] getContentBIG5() { //20240901
		if (_byte == null) {
			_byte = _bao3.toByteArray();
		}
		return _byte;
	}
	
	@Override
	public byte[] getContentGBK() { //20240901
		if (_byte == null) {
			_byte = _bao5.toByteArray();
		}
		return _byte;
	}
}