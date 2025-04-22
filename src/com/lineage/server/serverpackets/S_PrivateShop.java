package com.lineage.server.serverpackets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PrivateShopBuyList;
import com.lineage.server.templates.L1PrivateShopSellList;
import com.lineage.server.world.World;

/**
 * 交易個人商店清單(購買)
 * 
 * @author dexc
 */
public class S_PrivateShop extends ServerBasePacket {
	
	private byte[] _byte = null;

	public S_PrivateShop(final L1PcInstance pc, final int objectId, final int type) {
		final L1Object shopObj = World.get().findObject(objectId);
		if (shopObj instanceof L1PcInstance) {
			isPc(pc, objectId, type);

		} else if (shopObj instanceof L1DeInstance) {
			isDe(pc, objectId, type);
		}
	}

	/**
	 * 對象是NPC
	 * 
	 * @param pc
	 * @param objectId
	 * @param type
	 */
	private void isDe(L1PcInstance pc, int objectId, int type) {
		final L1DeInstance shopDe = (L1DeInstance) World.get().findObject(objectId);
		if (shopDe == null) {
			return;
		}

		this.writeC(S_OPCODE_PRIVATESHOPLIST);
		this.writeC(type);
		this.writeD(objectId);

		if (type == 0) {// 賣出物品
			Map<Integer, L1ItemInstance> sellList = new HashMap<Integer, L1ItemInstance>();
			Map<L1ItemInstance, Integer> map = shopDe.get_sellList();

			if (map.isEmpty()) {
				this.writeH(0x0000);
				return;
			}

			if (map.size() <= 0) {
				this.writeH(0x0000);
				return;
			}

			this.writeH(map.size());

			int i = 0;
			for (L1ItemInstance item : map.keySet()) {
				this.writeC(i);
				this.writeC(item.getEnchantLevel());
				this.writeH(item.getItem().getGfxId());
				this.writeD((int) item.getCount());
				int price = map.get(item);
				this.writeD(price);
				this.writeC(item.isIdentified() ? 1 : 0);
				this.writeC(item.getBless());

				String name = item.getNumberedViewName(item.getCount());

				this.writeS(name);
				if (item.isIdentified()) {
					int oldCount = (int) item.getCount();
					item.setCount(1);
					byte[] status = item.getStatusBytes();
					item.setCount(oldCount);
					writeC(status.length);
					for (byte b : status) {
						this.writeC(b);
					}
				} else
					this.writeC(0);
				sellList.put(i, item);
				i++;
			}
			pc.get_otherList().DELIST.clear();
			pc.get_otherList().DELIST.putAll(sellList);
			this.writeH(0x00);
		} else if (type == 1) {// 回收物品
			Map<Integer, int[]> list = shopDe.get_buyList();

			if (list.isEmpty()) {
				this.writeH(0x0000);
				return;
			}

			if (list.size() <= 0) {
				this.writeH(0x0000);
				return;
			}

			this.writeH(list.size());

			for (int key : list.keySet()) {
				int[] buyitem = list.get(key);
				final int count = buyitem[2];
				final int level = buyitem[1];
				final int price = buyitem[0];
				int i = 0;
				for (final L1ItemInstance pcItem : pc.getInventory().getItems()) {
					if (pcItem.getItemId() == key && pcItem.getEnchantLevel() == level) {
						this.writeC(i);
						this.writeD(pcItem.getId());
						this.writeD(count);
						this.writeD(price);
						i++;
					}
				}
			}
		}
	}

	/**
	 * 對象是PC
	 * 
	 * @param pc
	 * @param objectId
	 * @param type
	 */
	private void isPc(final L1PcInstance pc, final int objectId, final int type) {
		final L1PcInstance shopPc = (L1PcInstance) World.get().findObject(objectId);

		if (shopPc == null) {
			return;
		}

		this.writeC(S_OPCODE_PRIVATESHOPLIST);
		this.writeC(type);
		this.writeD(objectId);

		if (type == 0) {// 賣出物品
			final ArrayList<?> list = shopPc.getSellList();

			if (list.isEmpty()) {
				this.writeH(0x0000);
				return;
			}

			final int size = list.size();

			if (size <= 0) {
				this.writeH(0x0000);
				return;
			}

			pc.setPartnersPrivateShopItemCount(size);

			this.writeH(size);
			for (int i = 0; i < size; i++) {
				final L1PrivateShopSellList pssl = (L1PrivateShopSellList) list.get(i);
				final int itemObjectId = pssl.getItemObjectId();
				final int count = pssl.getSellTotalCount() - pssl.getSellCount();
				final int price = pssl.getSellPrice();
				final L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
				if (item != null) {
					this.writeC(i);
					this.writeD(count);
					this.writeD(price);
					this.writeH(item.getItem().getGfxId());
					this.writeC(item.getEnchantLevel());
					this.writeC(item.isIdentified() ? 1 : 0);
					this.writeC(item.getBless());

					String name = item.getNumberedViewName(count);

					this.writeS(name);
					if (item.isIdentified()) {
						int oldCount = (int) item.getCount();
						item.setCount(1);
						byte[] status = item.getStatusBytes();
						item.setCount(oldCount);
						this.writeC(status.length);
						for (byte b : status) {
							this.writeC(b);
						}
					} else
						this.writeC(0);
				}
			}
		} else if (type == 1) {// 回收物品
			final ArrayList<?> list = shopPc.getBuyList();
			int size = list.size();
			int itemCount = 0; // 因以預設寫入數量 所以將數量預設為0
			for (int i = 0; i < size; i++) {
				final L1PrivateShopBuyList psbl = (L1PrivateShopBuyList) list.get(i);
				final int itemObjectId = psbl.getItemObjectId();
				final int count = psbl.getBuyTotalCount();

				final L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
				int showCount = 0;

				for (final L1ItemInstance pcItem : pc.getInventory().getItems()) {
					if ((showCount < count) && (item.getItemId() == pcItem.getItemId())
							&& (item.getEnchantLevel() == pcItem.getEnchantLevel())) {
						showCount++;
						itemCount++;
					}
				}
			}
			this.writeH(itemCount); // 將原值取size 改為預設值取 0
			for (int i = 0; i < size; i++) {
				final L1PrivateShopBuyList psbl = (L1PrivateShopBuyList) list.get(i);
				final int itemObjectId = psbl.getItemObjectId();
				final int count = psbl.getBuyTotalCount();
				final int price = psbl.getBuyPrice();
				final L1ItemInstance item = shopPc.getInventory().getItem(itemObjectId);
				int showCount = 0;

				for (final L1ItemInstance pcItem : pc.getInventory().getItems()) {
					if ((showCount < count) && (item.getItemId() == pcItem.getItemId())
							&& (item.getEnchantLevel() == pcItem.getEnchantLevel())) {
						this.writeC(i);
						this.writeD(count);
						this.writeD(price);
						this.writeD(pcItem.getId());
						this.writeC(0);
						showCount++;
					}
				}
			}
		}
	}

	@Override
	public byte[] getContent() {
		return this.getBytes();
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
