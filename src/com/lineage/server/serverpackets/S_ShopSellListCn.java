package com.lineage.server.serverpackets;

import java.util.ArrayList;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.model.Instance.L1ItemStatus;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopItem;

/**
 * 特殊商店(貨幣)
 * 
 * @author dexc
 */
public class S_ShopSellListCn extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopSellListCn(final L1PcInstance pc, final L1NpcInstance npc) {
		buildPacket(pc, npc.getId(), npc.getNpcId());
	}

	private void buildPacket(final L1PcInstance pc, final int tgObjid, final int npcid) {
		writeC(S_OPCODE_SHOWSHOPBUYLIST);

		writeD(tgObjid);

		final ArrayList<L1ShopItem> shopItems = ShopCnTable.get().get(npcid);

		if (shopItems.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(shopItems.size()); // a

		int i = 0;
		for (final L1ShopItem shopItem : shopItems) {
			i++;
			pc.get_otherList().add_cnList(shopItem, i);

			final L1Item item = shopItem.getItem();

			writeD(i);
			writeH(item.getGfxId());
			writeD(shopItem.getPrice());
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
			
			final L1ItemStatus itemInfo = new L1ItemStatus(item, enchantLevel);
			// 取回物品資訊
			final byte[] status = itemInfo.getStatusBytes().getBytes();
			writeC(status.length);
			for (final byte b : status) {
				writeC(b);
			}
			// 降低封包量 不傳送詳細資訊
			// this.writeC(0x00);
		}

		writeH(0x17d4); // 0x0000:無顯示 0x0001:珍珠 0x0007:金幣 0x17d4:貨幣
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
