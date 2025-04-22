package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.server.datatables.DropMapTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1TaxCalculator;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.shop.L1Shop;
import com.lineage.server.templates.L1Drop;
import com.lineage.server.templates.L1DropMap;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;
import com.lineage.server.world.World;

/**
 * NPC物品販賣
 * 
 * @author dexc
 */
public class S_ShopSellList extends ServerBasePacket {

	private byte[] _byte = null;

	/**
	 * NPC物品販賣
	 */
	public S_ShopSellList(final int objId) {
		// System.out.println("S_ShopSellList 1");
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		final L1Object npcObj = World.get().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0x0000);
			return;
		}
		final int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		final L1TaxCalculator calc = new L1TaxCalculator(npcId);
		final L1Shop shop = ShopTable.get().get(npcId);
		final List<L1ShopItem> shopItems = shop.getSellingItems();

		if (shopItems.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(shopItems.size());

		// L1ItemInstancegetStatusBytes利用
		L1ItemInstance dummy = new L1ItemInstance();

		for (int i = 0; i < shopItems.size(); i++) {
			final L1ShopItem shopItem = shopItems.get(i);
			final L1Item item = shopItem.getItem();
			final int price = calc.layTax((int) (shopItem.getPrice() * ConfigRate.RATE_SHOP_SELLING_PRICE));
			writeD(i);// 排序

			writeH(shopItem.getItem().getGfxId());// 圖形

			writeD(price);// 售價
			final StringBuilder sbr = new StringBuilder();
			// 道具強化值
			final int enchantLevel = shopItem.getEnchantLevel();
			if (enchantLevel > 0) {
				sbr.append("+").append(enchantLevel).append(" ");
			} else if (enchantLevel < 0) {
				sbr.append(enchantLevel).append(" ");
			}
			// 道具數量
			if (shopItem.getPackCount() > 1) {
				sbr.append(item.getNameId()).append(" (").append(shopItem.getPackCount()).append(")");
			} else {
				sbr.append(item.getNameId());
			}
			// 輸出顯示
			writeS(sbr.toString());

			L1Item template = ItemTable.get().getTemplate(item.getItemId());
			this.writeD(template.getUseType());// XXX 7.6新增商品分類

			if (ConfigOther.SHOPINFO) {
				dummy.setItem(template);
				final byte status[] = dummy.getStatusBytes();
				writeC(status.length);
				byte abyte0[];
				final int j = (abyte0 = status).length;
				for (int h = 0; h < j; h++) {
					final byte b = abyte0[h];
					writeC(b);
				}
				/*
				 * final L1ItemStatus itemInfo = new L1ItemStatus(item, enchantLevel); // 取回物品資訊 final byte[] status = itemInfo.getStatusBytes().getBytes(); writeC(status.length); for
				 * (final byte b : status) { writeC(b); }
				 */

			} else {
				// 降低封包量 不傳送詳細資訊
				writeC(0x00);
			}
			/*
			 * final L1Item template = ItemTable.getInstance().getTemplate(item.getItemId()); if (template == null) { this.writeC(0x00); } else { final L1ItemStatus itemInfo = new
			 * L1ItemStatus(template); // 取回物品資訊 final byte[] status = itemInfo.getStatusBytes().getBytes(); this.writeC(status.length); for (final byte b : status) { this.writeC(b); } }
			 */
		}
		if (npcId == 81461) {
			writeH(0x3a49);
		} else {
			writeH(0x0007);
		}
	}

	/**
	 * NPC物品販賣(無稅率顯示)
	 */
	public S_ShopSellList(final L1NpcInstance npc) {
		// System.out.println("S_ShopSellList 2");
		writeC(S_OPCODE_SHOWSHOPBUYLIST);
		writeD(npc.getId());

		final int npcId = npc.getNpcTemplate().get_npcId();

		final L1Shop shop = ShopTable.get().get(npcId);
		final List<L1ShopItem> shopItems = shop.getSellingItems();

		if (shopItems.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(shopItems.size());

		for (int i = 0; i < shopItems.size(); i++) {
			final L1ShopItem shopItem = shopItems.get(i);
			final L1Item item = shopItem.getItem();
			final int price = shopItem.getPrice();
			writeD(i);// 排序
			writeH(shopItem.getItem().getGfxId());// 圖形
			writeD(price);// 售價
			if (shopItem.getPackCount() > 1) {
				writeS(item.getNameId() + " (" + shopItem.getPackCount() + ")");

			} else {
				writeS(item.getNameId());// 名稱
			}

			// 降低封包量 不傳送詳細資訊
			writeC(0x00);
			/*
			 * final L1Item template = ItemTable.getInstance().getTemplate(item.getItemId()); if (template == null) { this.writeC(0x00); } else { final L1ItemStatus itemInfo = new
			 * L1ItemStatus(template); // 取回物品資訊 final byte[] status = itemInfo.getStatusBytes().getBytes(); this.writeC(status.length); for (final byte b : status) { this.writeC(b); } }
			 */
		}
		if (npcId == 81461) {
			writeH(0x3a49);
		} else {
			writeH(0x0007);
		}
	}

	// 怪物掉落資訊
	public S_ShopSellList(int id, int mapid, final L1PcInstance pc) {
		final ArrayList<L1Drop> droplist = DropTable.get().getdropitem(id);
		final HashMap<Integer, ArrayList<L1DropMap>> droplistMap = DropMapTable.get().getdropitem(mapid);

		if (droplist == null && droplistMap == null) {
			pc.sendPackets(new S_SystemMessage("你查詢的目標沒有任何掉落物"));
			return;
		}

		if (droplistMap != null && droplist != null) {
			final ArrayList<L1DropMap> list = droplistMap.get(mapid);

			this.writeC(S_OPCODE_SHOWSHOPBUYLIST);
			this.writeD(id);

			this.writeH(list.size() + droplist.size());
			int i = 0;

			for (L1Drop obj : droplist) {
				final L1Item item = ItemTable.get().getTemplate(obj.getItemid());
				writeD(i);
				writeH(item.getGfxId());
				writeD(0);
				writeS(item.getNameId());
				this.writeD(0);
				this.writeC(0x00);
				i++;
			}
			for (L1DropMap obj : list) {
				final L1Item item = ItemTable.get().getTemplate(obj.getItemid());
				writeD(i);
				writeH(item.getGfxId());
				writeD(0);
				writeS(item.getNameId());
				this.writeD(0);
				this.writeC(0x00);
				i++;
			}
		} else if (droplistMap != null) {
			final ArrayList<L1DropMap> list = droplistMap.get(mapid);
			this.writeC(S_OPCODE_SHOWSHOPBUYLIST);
			this.writeD(id);

			this.writeH(list.size());
			int i = 0;

			for (L1DropMap obj : list) {
				final L1Item item = ItemTable.get().getTemplate(obj.getItemid());
				writeD(i);
				writeH(item.getGfxId());
				writeD(0);
				writeS(item.getNameId());
				this.writeD(0);
				this.writeC(0x00);
				i++;
			}
		} else {
			this.writeC(S_OPCODE_SHOWSHOPBUYLIST);
			this.writeD(id);

			this.writeH(droplist.size());
			int i = 0;

			for (L1Drop obj : droplist) {
				final L1Item item = ItemTable.get().getTemplate(obj.getItemid());
				writeD(i);
				writeH(item.getGfxId());
				writeD(0);
				writeS(item.getNameId());
				this.writeD(0);
				this.writeC(0x00);
				i++;
			}

		}

		this.writeH(0x0000); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:天寶
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
