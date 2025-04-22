package com.lineage.server.serverpackets;

import java.util.HashMap;
import java.util.Map;

import com.lineage.server.datatables.ShopTable;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 賣(回收商)
 * 
 * @author dexc
 */
public class S_ShopBuyListAll extends ServerBasePacket {

	private byte[] _byte = null;

	public S_ShopBuyListAll(final L1PcInstance pc, final L1NpcInstance npc) {
		final Map<L1ItemInstance, Integer> assessedItems = assessItems(pc.getInventory());

		if (assessedItems.isEmpty()) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		if (assessedItems.size() <= 0) {
			// 你並沒有我需要的東西
			pc.sendPackets(new S_NoSell(npc));
			return;
		}

		writeC(S_OPCODE_SHOWSHOPSELLLIST);
		writeD(npc.getId());

		writeH(assessedItems.size());

		for (final L1ItemInstance key : assessedItems.keySet()) {
			writeD(key.getId());
			writeD(assessedItems.get(key));
		}
	}

	/**
	 * 傳回物品價格
	 * 
	 * @param inv
	 * @return
	 */
	private Map<L1ItemInstance, Integer> assessItems(final L1PcInventory inv) {
		final Map<L1ItemInstance, Integer> result = new HashMap<L1ItemInstance, Integer>();
		for (final L1ItemInstance item : inv.getItems()) {
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
			final int key = item.getItemId();
			final int price = ShopTable.get().getPrice(key);
			if (price > 0) {
				result.put(item, price);
			}

		}
		return result;
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