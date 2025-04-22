package com.lineage.server.serverpackets;

import java.util.HashMap;
import java.util.Map;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.DwarfShopReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ShopS;

/**
 * 出售管理員商店(貨幣)
 * 
 * @author dexc
 */
public class S_CnSShopSellList extends ServerBasePacket {

	private byte[] _byte = null;

	public S_CnSShopSellList(final L1PcInstance pc, final L1NpcInstance npc) {
		buildPacket(pc, npc.getId());
	}

	private void buildPacket(final L1PcInstance pc, final int tgObjid) {

		final Map<L1ShopS, L1ItemInstance> shopItems = new HashMap<L1ShopS, L1ItemInstance>();

		final Map<Integer, L1ItemInstance> srcMap = DwarfShopReading.get().allItems();

		for (final Integer key : srcMap.keySet()) {
			final L1ShopS info = DwarfShopReading.get().getShopS(key);
			if (info == null) {
				continue;
			}
			if (info.get_end() != 0) {// 物品非出售中
				continue;
			}
			if (info.get_item() == null) {// 物品設置為空
				continue;
			}

			final L1ItemInstance item = srcMap.get(key);
			shopItems.put(info, item);
		}

		writeC(S_OPCODE_SHOWSHOPBUYLIST);

		writeD(tgObjid);

		if (shopItems.size() <= 0) {
			writeH(0x0000);
			return;
		}

		writeH(shopItems.size());

		int i = 0;
		for (final L1ShopS key : shopItems.keySet()) {
			i++;
			final L1ItemInstance item = shopItems.get(key);
			pc.get_otherList().add_cnSList(item, i);

			writeD(i);// 排序編號
			writeH(item.getItem().getGfxId());
			writeD(key.get_adena());

			writeS(item.getViewName());
			L1Item template = ItemTable.get().getTemplate(item.getItemId());
			this.writeD(template.getUseType());// XXX 7.6新增商品分類
			// 取回物品詳細資訊
			/*final byte[] status = item.getStatusBytes();
			writeC(status.length);
			for (final byte b : status) {
				writeC(b);
			}*/
			final byte status[] = item.getStatusBytes();
			writeC(status.length);
			byte abyte0[];
			final int j = (abyte0 = status).length;
			for (int h = 0; h < j; h++) {
				final byte b = abyte0[h];
				writeC(b);
			}
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