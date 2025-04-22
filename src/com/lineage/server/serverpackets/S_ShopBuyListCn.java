package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.TimeLimit.L1ShopTimeLimit;
import com.lineage.server.model.TimeLimit.L1TimeLimit;
import com.lineage.server.model.TimeLimit.L1TimeLimitChar;
import com.lineage.server.model.TimeLimit.ShopTimeLimitTable;
import com.lineage.server.model.TimeLimit.TimeLimitCharTable;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.world.World;

/**
 * NPC回收道具
 * 
 * @author terry0412
 */
public class S_ShopBuyListCn extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC回收道具
	 * 
	 * @param pc
	 * @param tgObjid
	 * @param npcid
	 */
	public S_ShopBuyListCn(final L1PcInstance pc, final L1NpcInstance npc) {
		writeC(S_OPCODE_SHOWSHOPSELLLIST);
		writeD(npc.getId());

		final ArrayList<L1ShopItem> shopItems = ShopCnTable.get().get(npc.getNpcId());
		if (shopItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		final Map<Integer, Integer> assessedItems = new HashMap<Integer, Integer>();

		// System.out.println("1.當前販售清單長度為:"+assessedItems.size());
		for (final L1ItemInstance item : pc.getInventory().getItems()) {
			switch (item.getItem().getItemId()) {
			case 40308: // 金幣
			case 44070: // 貨幣
			case 40314: // 項圈
			case 40316: // 高等寵物項圈
				continue;
			}

			if (item.isEquipped()) {// 使用中
				continue;
			}
			if (item.getBless() >= 128) {
				continue;
			}
			if (item.getLastUsed() != null) {
				continue;
			}
			if (item.getItem().cantBeSold()) {
				continue;
			}
			// System.out.println("2.當前販售清單長度為:"+assessedItems.size());
			for (final L1ShopItem shopItem : shopItems) {
				// System.out.println("準備回收道具："+shopItem.getItemId());
				if (shopItem.getItemId() == item.getItemId()) {
					if (shopItem.getPurchasingPrice() < 0) {
						// System.out.println("檢測到當前物品"+item.getItemId()+"賣出價格小於0，略過");
						continue;
					}
					// System.out.println("道具itemid:"+item.getItemId()+"id："+item.getId()+"符合回收需求加入回收清單");
					pc.get_otherList().add_cnList(shopItem, item.getId());

					assessedItems.put(item.getId(), shopItem.getPurchasingPrice());
				}
			}
		}
		// System.out.println("3.當前販售清單長度為:"+assessedItems.size());
		if (assessedItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		writeH(assessedItems.size());

		for (final Entry<Integer, Integer> entrySet : assessedItems.entrySet()) {
			writeD(entrySet.getKey());
			writeD(entrySet.getValue());
		}
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 店品物表示。BUY押時送。
	 * @return 
	 */
	public void S_TimeLimitBuyList(int objId, L1PcInstance pc) {
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		L1Object npcObj = World.get().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		L1TimeLimit shop = ShopTimeLimitTable.getInstance().get(npcId);
		List<L1ShopTimeLimit> shopItems = shop.getSellingItems();

		writeH(shopItems.size());
		L1ItemInstance dummy = new L1ItemInstance();
		
		ShopTimeLimitTable sss = new ShopTimeLimitTable();
		TimeLimitCharTable LimitChar = new  TimeLimitCharTable();
		if (LimitChar.loadItems(pc.getId()) == null) {// 如果人物限購數據不存在,則加入限購數據
			Map<Integer, L1TimeLimit> shopmap = ShopTimeLimitTable.getInstance().getall();
			for (Map.Entry<Integer, L1TimeLimit> entry : shopmap.entrySet()) {
			    Integer key = entry.getKey();
			    
			    L1TimeLimit npcshop = ShopTimeLimitTable.getInstance().get(key);
			    List<L1ShopTimeLimit> itemlist = npcshop.getSellingItems();
			    for(L1ShopTimeLimit shopitem : itemlist) {
			    	if (shopitem.get_isall() == 0) {// 過濾全服限購物品
						continue;
					}
			    	L1TimeLimitChar limititem = new L1TimeLimitChar();
					limititem.set_charobjid(pc.getId());
					limititem.set_itemid(shopitem.getItemId());
					LimitChar.storitem(pc.getId(), limititem);
			    }
			}
			
			/*
			for (int i = 0; i < shopItems.size(); i++) {
				L1ShopTimeLimit shopItem = shopItems.get(i);
				if (shopItem.get_isall() == 0) {// 過濾全服限購物品
					continue;
				}
				L1TimeLimitChar limititem = new L1TimeLimitChar();
				limititem.set_charobjid(pc.getId());
				limititem.set_itemid(shopItem.getItemId());
				LimitChar.storitem(pc.getId(), limititem);
			}
			*/
		}

		for (int i = 0; i < shopItems.size(); i++) {
			L1ShopTimeLimit shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			int price = (shopItem.getPrice());
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
			writeD(price);
			if (shopItem.get_isall() == 0) { // 當物品等於0時,為全服限購
				if (sss.getEndCount(shopItem.getItemId()) > 0) {
					if (shopItem.getEnlvl() > 0) {
						writeS("+" + shopItem.getEnlvl() + " " + item.getName() + " 剩餘數量:" + sss.getEndCount(item.getItemId()));
					} else {
						writeS(item.getNameId() + " 剩餘數量:" + sss.getEndCount(item.getItemId()));
					}
				} else {
					this.writeS(sss.getEndName(item.getItemId()));
				}
			} else if (shopItem.get_isall() == 1) { // 當物品等於1時,為個人限購
				if (LimitChar.getUserItems(pc.getId(), shopItem.getItemId())) {// 還有可購買次數
					if (shopItem.getEnlvl() > 0) {
						writeS("+" + shopItem.getEnlvl() + " " + item.getName() + " 剩餘數量:" + LimitChar.getCount(pc.getId(), shopItem.getItemId()));
					} else {
						writeS(item.getNameId() + " 剩餘數量:" + LimitChar.getCount(pc.getId(), shopItem.getItemId()));
					}
				} else {
					this.writeS(sss.getEndName(item.getItemId()));
				}
			}

			L1Item template = ItemTable.get().getTemplate(item.getItemId());
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
		writeH(2336);
	}

//	// //@Override
//	public byte[] getContent() {
//		return getBytes();
//	}
	
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