package com.lineage.server.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.ProtectorSet;
import com.lineage.server.IdFactory;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.datatables.sql.LetterTable;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.world.World;

/**
 * 背包
 * 
 * @author dexc
 */
public class L1Inventory extends L1Object {

	private static final Log _log = LogFactory.getLog(L1Inventory.class);

	private static final long serialVersionUID = 1L;

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

	public static final int MAX_WEIGHT = 1500;

	public L1Inventory() {
		//
	}

	/**
	 * 背包內全部數量
	 * 
	 * @return
	 */
	public int getSize() {
		if (_items.isEmpty()) {
			return 0;
		}
		return _items.size();
	}

	/**
	 * 背包內全物件清單
	 * 
	 * @return
	 */
	public List<L1ItemInstance> getItems() {
		return _items;
	}

	/**
	 * 背包內全部重量
	 * 
	 * @return
	 */
	public int getWeight() {
		int weight = 0;

		for (final L1ItemInstance item : _items) {
			weight += item.getWeight();
		}

		return weight;
	}

	public static final int OK = 0;// 成功

	public static final int SIZE_OVER = 1;// 超過數量

	public static final int WEIGHT_OVER = 2;// 超過可攜帶重量

	public static final int AMOUNT_OVER = 3;// 超過LONG最大質

	public int checkAddItem(final int item, final long count) {
		return -1;
	}

	/**
	 * 增加物品是否成功(背包)
	 * 
	 * @param item 物品
	 * @param count 數量
	 * @return 0:成功 1:超過可攜帶數量 2:超過可攜帶重量 3:超過LONG最大質
	 */
	public int checkAddItem(final L1ItemInstance item, final long count) {
		if (item == null) {
			return -1;
		}

		if ((item.getCount() <= 0) || (count <= 0)) {
			return -1;
		}

		if ((getSize() > ConfigAlt.MAX_NPC_ITEM) || ((getSize() == ConfigAlt.MAX_NPC_ITEM)
				&& (!item.isStackable() || !this.checkItem(item.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		final long weight = getWeight() + ((item.getItem().getWeight() * count) / 1000) + 1;
		if ((weight < 0) || (((item.getItem().getWeight() * count) / 1000) < 0)) {
			return WEIGHT_OVER;
		}
		if (weight > (MAX_WEIGHT * ConfigRate.RATE_WEIGHT_LIMIT_PET)) { // 重量確認
			return WEIGHT_OVER;
		}

		final L1ItemInstance itemExist = this.findItemId(item.getItemId());
		if ((itemExist != null) && ((itemExist.getCount() + count) > Long.MAX_VALUE)) {
			return AMOUNT_OVER;
		}

		return OK;
	}

	public static final int WAREHOUSE_TYPE_PERSONAL = 0;// 個人/精靈倉庫

	public static final int WAREHOUSE_TYPE_CLAN = 1;// 血盟倉庫

	/**
	 * 增加物品是否成功(倉庫)
	 * 
	 * @param item 物品
	 * @param count 數量
	 * @param type 模式 0:個人/精靈倉庫 1:血盟倉庫
	 * @return 0:成功 1:超過數量
	 */
	public int checkAddItemToWarehouse(final L1ItemInstance item, final long count, final int type) {
		if (item == null) {
			return -1;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return -1;
		}

		int maxSize = 100;
		if (type == WAREHOUSE_TYPE_PERSONAL) {
			maxSize = ConfigAlt.MAX_PERSONAL_WAREHOUSE_ITEM;

		} else if (type == WAREHOUSE_TYPE_CLAN) {
			maxSize = ConfigAlt.MAX_CLAN_WAREHOUSE_ITEM;
		}
		if ((getSize() > maxSize) || ((getSize() == maxSize)
				&& (!item.isStackable() || !this.checkItem(item.getItem().getItemId())))) { // 容量確認
			return SIZE_OVER;
		}

		return OK;
	}
	
	/**
	 * 全新物件加入背包
	 * 
	 * @param id
	 * @param count
	 * @return
	 */
	public synchronized L1ItemInstance storeItem(final int id, final long count) {
		try {
			if (count <= 0) {
				return null;
			}
			final L1Item temp = ItemTable.get().getTemplate(id);
			if (temp == null) {
				return null;
			}

			if (temp.isStackable()) {
				final L1ItemInstance item = new L1ItemInstance(temp, count);

				if (this.findItemId(id) == null) { // 新しく生成する必要がある場合のみIDの発行とL1Worldへの登録を行う
					item.setId(IdFactory.get().nextId());
					World.get().storeObject(item);
				}

				return this.storeItem(item);
			}

			// スタックできないアイテムの場合
			L1ItemInstance result = null;
			for (int i = 0; i < count; i++) {
				final L1ItemInstance item = new L1ItemInstance(temp, 1);
				item.setId(IdFactory.get().nextId());
				World.get().storeObject(item);
				this.storeItem(item);
				result = item;
			}
			// 最後に作ったアイテムを返す。配列を戻すようにメソッド定義を変更したほうが良いかもしれない。
			return result;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 背包中新物品的增加 (物品購買/道具交換)
	 * 
	 * @param item
	 * @return
	 */
	public synchronized L1ItemInstance storeItem(final L1ItemInstance item) {
		try {
			if (item == null) {
				return null;
			}
			if (item.getCount() <= 0) {
				return null;
			}

			if (item.isStackable()) {
				if (item.getItem().getUseType() == -5) {// 食人妖精競賽票
					final L1ItemInstance[] items = findItemsId(item.getItemId());
					// System.out.println(items);
					for (final L1ItemInstance tgitem : items) {
						final String gamNo = tgitem.getGamNo();
						if (item.getGamNo().equals(gamNo)) {
							tgitem.setCount(tgitem.getCount() + item.getCount());
							this.updateItem(tgitem);
							return tgitem;
						}
					}

				} else {
					final L1ItemInstance findItem = this.findItemId(item.getItem().getItemId());
					if (findItem != null) {
						if (findItem.getId() == item.getId()) {
							if (this instanceof L1PcInventory) {
								final L1PcInventory pc_inv = (L1PcInventory) this;
								final L1PcInstance pc = pc_inv.getOwner();
								if (pc != null) {
									_log.info("增加物品時發生重複ID異常: (人物:" + pc.getName() + ", 道具:"
											+ item.getItem().getName() + ", Objid:" + item.getId() + ")");
								}
							}
							return null;
						}
						findItem.setCount(findItem.getCount() + item.getCount());
						this.updateItem(findItem);
						return findItem;
					}
				}
			}
			item.setX(getX());
			item.setY(getY());
			item.setMap(getMapId());

			// 資料庫最大可用次數
			int chargeCount = item.getItem().getMaxChargeCount();

			// 魔杖類次數給予判斷
			switch (item.getItem().getItemId()) {
			case 20383: // 軍馬頭盔
				chargeCount = 50;
				break;

			case 40006: // 創造怪物魔杖
			case 140006: // 創造怪物魔杖

			case 40008: // 變形魔杖
			case 140008: // 變形魔杖

			case 40007: // 閃電魔杖
			case 40009: // 驅逐魔杖
				final Random random1 = new Random();
				chargeCount -= random1.nextInt(5);
				break;

			default:
				break;
			}

			item.setChargeCount(chargeCount);

			// 物品使用期限 by terry0412
			final L1ItemTime itemTime = ItemTimeTable.TIME.get(item.getItemId());
			if ((itemTime != null) && !itemTime.is_equipped()) {
				// 目前時間 加上指定天數耗用秒數
				final long upTime = System.currentTimeMillis() + (itemTime.get_remain_time() * 60 * 1000);
				// 時間數據
				final Timestamp ts = new Timestamp(upTime);
				item.set_time(ts);
				// 人物背包物品使用期限資料
				CharItemsTimeReading.get().addTime(item.getId(), ts);

				if (this instanceof L1PcInventory) {
					final L1PcInventory pc_inv = (L1PcInventory) this;
					final L1PcInstance pc = pc_inv.getOwner();
					if (pc != null) {
						_log.info("人物: " + pc.getName() + ", 道具: " + item.getName() + ", 到期日: " + ts);
					}
				}
			}

			if (item.getItem().getItemId() == ProtectorSet.ITEM_ID) {
				if (this instanceof L1PcInventory) {
					final L1PcInventory pc_inv = (L1PcInventory) this;
					final L1PcInstance pc = pc_inv.getOwner();
					if ((pc != null) && !pc.isProtector()) {
						pc.setProtector(true);
						// 恭喜XXXXX獲得守護者靈魂成為人人稱羨的守護者
						World.get().broadcastPacketToAll(
								new S_BlueMessage(166, "\\f2[" + pc.getName() + "]獲得守護者靈魂的加護"));
					}
				}
			}

			if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) { // 照明道具時間設置
				item.setRemainingTime(item.getItem().getLightFuel());

			} else {
				item.setRemainingTime(item.getItem().getMaxUseTime());
			}

			item.setBless(item.getItem().getBless());

			_items.add(item);
			insertItem(item);
			return item;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 背包中新物品的增加 (倉庫取回/倉庫存入/丟棄/撿取)
	 * 
	 * @param item
	 * @return
	 */
	public synchronized L1ItemInstance storeTradeItem(final L1ItemInstance item) {
		try {
			if (item == null) {
				return null;
			}
			if (item.getCount() <= 0) {
				return null;
			}

			if (item.isStackable()) {
				if (item.getItem().getUseType() == -5) {// 食人妖精競賽票/死亡競賽票/彩票
					final L1ItemInstance[] items = findItemsId(item.getItemId());
					// System.out.println(items);
					for (final L1ItemInstance tgitem : items) {
						final String gamNo = tgitem.getGamNo();
						if (item.getGamNo().equals(gamNo)) {
							tgitem.setCount(tgitem.getCount() + item.getCount());
							this.updateItem(tgitem);
							return tgitem;
						}
					}

				} else {
					final L1ItemInstance findItem = this.findItemId(item.getItem().getItemId());
					if (findItem != null) {
						if (findItem.getId() == item.getId()) {
							if (this instanceof L1PcInventory) {
								final L1PcInventory pc_inv = (L1PcInventory) this;
								final L1PcInstance pc = pc_inv.getOwner();
								if (pc != null) {
									_log.info("增加物品時發生重複ID異常2: (人物:" + pc.getName() + ", 道具:"
											+ item.getItem().getName() + ", Objid:" + item.getId() + ")");
								}
							}
							return null;
						}
						findItem.setCount(findItem.getCount() + item.getCount());
						this.updateItem(findItem);
						return findItem;
					}

				}
			}
			item.setX(getX());
			item.setY(getY());
			item.setMap(getMapId());
			/*
			 * if (!this._items.contains(item)) { this._items.add(item); }
			 */
			_items.add(item);
			insertItem(item);
			return item;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 刪除指定編號物品及數量
	 * 
	 * @param itemid - 刪除物品的編號
	 * @param count - 刪除的數量
	 * @return true:刪除完成 false:刪除失敗
	 */
	public boolean consumeItem(final int itemid, final long count) {
		if (count <= 0) {
			return false;
		}
		// 物品可以堆疊
		if (ItemTable.get().getTemplate(itemid).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				this.removeItem(item, count);
				return true;
			}

		} else {
			final L1ItemInstance[] itemList = findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					this.removeItem(itemList[i], 1);
				}
				return true;

			} else if (itemList.length > count) {
				// 指定物品具有多個
				final DataComparator dc = new DataComparator();
				Arrays.sort(itemList, dc); // 按照強化質 由低至高排列
				for (int i = 0; i < count; i++) {
					// 先由強化質低的開始移除
					this.removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean consumeItem(final int itemid, final int count) {
		if (count <= 0) {
			return false;
		}
		// 物品可以堆疊
		if (ItemTable.get().getTemplate(itemid).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				this.removeItem(item, count);
				return true;
			}

		} else {
			final L1ItemInstance[] itemList = findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					this.removeItem(itemList[i], 1);
				}
				return true;

			} else if (itemList.length > count) {
				// 指定物品具有多個
				final DataComparator dc = new DataComparator();
				Arrays.sort(itemList, dc); // 按照強化質 由低至高排列
				for (int i = 0; i < count; i++) {
					// 先由強化質低的開始移除
					this.removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 按照強化質 由低至高排列物品
	 * 
	 * @author daien
	 */
	public class DataComparator implements Comparator<Object> {
		@Override
		public int compare(final Object item1, final Object item2) {
			return ((L1ItemInstance) item1).getEnchantLevel() - ((L1ItemInstance) item2).getEnchantLevel();
		}
	}

	/**
	 * 移轉物品
	 * 
	 * @param objectId
	 * @param count
	 * @return
	 */
	public L1ItemInstance shiftingItem(final int objectId, final long count) {
		final L1ItemInstance item = getItem(objectId);
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}
		if (item.getCount() == count) {
			if (!item.isEquipped()) {
				deleteItem(item);
				return item;
			}

		}
		return null;
	}

	/**
	 * 指定OBJID以及數量 刪除物品
	 * 
	 * @param objectId
	 * @param count
	 * @return 實際刪除數量
	 */
	public long removeItem(final int objectId, final long count) {
		final L1ItemInstance item = getItem(objectId);
		return this.removeItem(item, count);
	}

	/**
	 * 指定物品(全部數量) 刪除物品
	 * 
	 * @param item
	 * @return 實際刪除數量
	 */
	public long removeItem(final L1ItemInstance item) {
		return this.removeItem(item, item.getCount());
	}

	/**
	 * 指定物品以及數量 刪除物品
	 * 
	 * @param item
	 * @param count
	 * @return 實際刪除數量
	 */
	public long removeItem(final L1ItemInstance item, long count) {
		if (item == null) {
			return 0;
		}
		if (!_items.contains(item)) {
			return 0;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return 0;
		}
		if (item.getCount() < count) {
			count = item.getCount();
		}
		if (item.getCount() == count) {
			final int itemId = item.getItem().getItemId();
			if ((itemId >= 49016) && (itemId <= 49025)) { // 便箋
				final LetterTable lettertable = new LetterTable();
				lettertable.deleteLetter(item.getId());

			} else if ((itemId >= 41383) && (itemId <= 41400)) { // 家具
				for (final L1Object l1object : World.get().getObject()) {
					if (l1object instanceof L1FurnitureInstance) {
						final L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
						if (furniture.getItemObjId() == item.getId()) { // 既に引き出している家具
							FurnitureSpawnReading.get().deleteFurniture(furniture);
						}
					}
				}
			} else if (itemId == ProtectorSet.ITEM_ID) {
				if (this instanceof L1PcInventory) {
					final L1PcInventory pc_inv = (L1PcInventory) this;
					final L1PcInstance pc = pc_inv.getOwner();
					if ((pc != null) && pc.isProtector()) {
						pc.setProtector(false);
						World.get().broadcastPacketToAll(new S_ServerMessage(2925));
					}
				}
			}
			deleteItem(item);
			World.get().removeObject(item);

		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
		}
		return count;
	}

	/**
	 * 物品資料消除
	 * 
	 * @param item
	 */
	public void deleteItem(final L1ItemInstance item) {
		_items.remove(item);
	}

	// 引数のインベントリにアイテムを移譲
	public synchronized L1ItemInstance tradeItem(final int objectId, final long count,
			final L1Inventory inventory) {
		final L1ItemInstance item = getItem(objectId);
		return this.tradeItem(item, count, inventory);
	}

	/**
	 * 物品轉移
	 * 
	 * @param item 轉移的物品
	 * @param count 移出的數量
	 * @param showId 副本編號
	 * @param inventory 移出對象的背包
	 */
	public synchronized L1ItemInstance tradeItem(final L1ItemInstance item, final int count, final int showId,
			final L1GroundInventory inventory) {
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}

		if ((getItem(item.getId()) == null) || (inventory.getItem(item.getId()) != null)) {
			if (this instanceof L1PcInventory) {
				final L1PcInventory pc_inv = (L1PcInventory) this;
				final L1PcInstance pc = pc_inv.getOwner();
				if (pc != null) {
					_log.info("轉移物品時發生重複ID異常[To地板]: (人物:" + pc.getName() + ", 道具:" + item.getItem().getName()
							+ ", Objid:" + item.getId() + ")");
				}
			}
			return null;
		}

		L1ItemInstance carryItem;
		if (item.getCount() == count) {
			deleteItem(item);
			carryItem = item;
			// 副本編號
			carryItem.set_showId(showId);

		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
			carryItem = ItemTable.get().createItem(item.getItem().getItemId());
			// 副本編號
			carryItem.set_showId(showId);
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getBless());
		}
		return inventory.storeTradeItem(carryItem);
	}

	/**
	 * 物品轉移
	 * 
	 * @param item 轉移的物品
	 * @param count 移出的數量
	 * @param inventory 移出對象的背包
	 * @return
	 */
	public synchronized L1ItemInstance tradeItem(final L1ItemInstance item, final long count,
			final L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
		if (item.getCount() < count) {
			return null;
		}

		if ((getItem(item.getId()) == null) || (inventory.getItem(item.getId()) != null)) {
			if ((this instanceof L1PcInventory) && (inventory instanceof L1PcInventory)) {
				final L1PcInventory pc_inv = (L1PcInventory) this;
				final L1PcInventory pc_inv2 = (L1PcInventory) inventory;
				final L1PcInstance pc = pc_inv.getOwner();
				final L1PcInstance pc2 = pc_inv2.getOwner();
				if ((pc != null) && (pc2 != null)) {
					_log.info("轉移物品時發生重複ID異常[To背包]: (人物:" + pc.getName() + " 將道具:"
							+ item.getNumberedViewName(count) + "轉移給人物:" + pc2.getName() + ", Objid:"
							+ item.getId() + ")");
				}
			}
			return null;
		}

		L1ItemInstance carryItem;
		if (item.getCount() == count) {
			deleteItem(item);
			carryItem = item;

		} else {
			item.setCount(item.getCount() - count);
			this.updateItem(item);
			carryItem = ItemTable.get().createItem(item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getBless());
		}
		return inventory.storeTradeItem(carryItem);
	}

	/**
	 * アイテムを損傷・損耗させる（武器・防具も含む） アイテムの場合、損耗なのでマイナスするが 武器・防具は損傷度を表すのでプラスにする。
	 */
	public L1ItemInstance receiveDamage(final int objectId) {
		final L1ItemInstance item = getItem(objectId);
		return this.receiveDamage(item);
	}

	public L1ItemInstance receiveDamage(final L1ItemInstance item) {
		return this.receiveDamage(item, 1);
	}

	public L1ItemInstance receiveDamage(final L1ItemInstance item, final int count) {
		if (item == null) {
			return null;
		}
		final int itemType = item.getItem().getType2();
		final int currentDurability = item.get_durability();

		if (((currentDurability == 0) && (itemType == 0)) || (currentDurability < 0)) {
			item.set_durability(0);
			return null;
		}

		// 武器・防具のみ損傷度をプラス
		if (itemType == 0) {
			final int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.set_durability(durability);
			}
		} else {
			final int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				item.set_durability(durability);
			}
		}

		this.updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(final L1ItemInstance item) {
		if (item == null) {
			return null;
		}
		final int itemType = item.getItem().getType2();
		final int durability = item.get_durability();

		if (((durability == 0) && (itemType != 0)) || (durability < 0)) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			// 耐久度をプラスしている。
			item.set_durability(durability + 1);
		} else {
			// 損傷度をマイナスしている。
			item.set_durability(durability - 1);
		}

		this.updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	/**
	 * 找尋指定物品(未裝備)
	 * 
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance findItemIdNoEq(final int itemId) {
		for (final L1ItemInstance item : _items) {
			if ((item.getItem().getItemId() == itemId) && !item.isEquipped()) {
				if (item.get_time() == null) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 找尋指定物品<BR>
	 * 不檢查裝備狀態
	 * 
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance findItemId(final int itemId) {
		for (final L1ItemInstance item : _items) {
			if (item.getItem().getItemId() == itemId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 找尋指定物品
	 * 
	 * @param nameid
	 * @return
	 */
	public L1ItemInstance findItemId(final String nameid) {
		for (final L1ItemInstance item : _items) {
			if (item.getName().equals(nameid)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 傳出是否有該編號物品(陣列)
	 * 
	 * @param itemId 物品編號
	 * @return
	 */
	public L1ItemInstance[] findItemsId(final int itemId) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getItemId() == itemId) {// itemid相等
				if (item.get_time() == null) {// 不具備時間限制
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 未裝備物品清單(陣列)
	 * 
	 * @param itemId
	 * @return
	 */
	public L1ItemInstance[] findItemsIdNotEquipped(final int itemId) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getItemId() == itemId) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 未裝備物品清單(陣列)
	 * 
	 * @param nameid
	 * @return
	 */
	public L1ItemInstance[] findItemsIdNotEquipped(final String nameid) {
		final ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (final L1ItemInstance item : _items) {
			if (item.getName().equals(nameid)) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	/**
	 * 檢查是否具有指定OBJID物品
	 * 
	 * @param objectId
	 * @return
	 */
	public L1ItemInstance getItem(final int objectId) {
		for (final Object itemObject : _items) {
			final L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 檢查指定物品是否足夠數量1（矢 魔石的確認）
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkItem(final int id) {
		return this.checkItem(id, 1);
	}

	/**
	 * 檢查指定物品是否足夠數量
	 * 
	 * @param itemId 物品編號
	 * @param count 需要數量
	 * @return
	 */
	public boolean checkItem(final int itemId, final long count) {
		if (count == 0) {
			return true;
		}

		final L1Item tempItem = ItemTable.get().getTemplate(itemId);
		if ((count < 0) || (tempItem == null)) {
			return false;
		}

		// 可堆疊
		if (tempItem.isStackable()) {
			final L1ItemInstance item = this.findItemId(itemId);
			if ((item != null) && (item.getCount() >= count)) {
				return true;
			}

			// 不可堆疊
		} else {
			final Object[] itemList = findItemsId(itemId);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 指定物品編號以及數量<BR>
	 * 該物件未在裝備狀態
	 * 
	 * @param itemid
	 * @param count
	 * @return 足夠傳回物品
	 */
	public L1ItemInstance checkItemX(final int itemid, final long count) {
		if (count <= 0) {
			return null;
		}
		if (ItemTable.get().getTemplate(itemid) != null) {
			final L1ItemInstance item = findItemIdNoEq(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 指定物品編號以及數量(未裝備)
	 * 
	 * @param itemid
	 * @param count
	 * @return 足夠傳回物品
	 */
	public L1ItemInstance checkItemXNoEq(final int itemid, final long count) {
		if (count <= 0) {
			return null;
		}
		if (ItemTable.get().getTemplate(itemid) != null) {
			final L1ItemInstance item = findItemIdNoEq(itemid);
			if ((item != null) && (item.getCount() >= count)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 具有未裝備指定的物品包含強化質
	 * 
	 * @param id 指定物件編號
	 * @param enchant 指定強化質
	 * @param count 數量
	 * @return
	 */
	public boolean checkEnchantItem(final int id, final int enchant, final long count) {
		int num = 0;
		for (final L1ItemInstance item : _items) {
			if (item.isEquipped()) { // 物品裝備狀態
				continue;
			}
			if ((item.getItemId() == id) && (item.getEnchantLevel() == enchant)) {
				num++;
				if (num == count) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 刪除未裝備指定的物品包含強化質
	 * 
	 * @param id 指定物件編號
	 * @param enchant 指定強化質
	 * @param count 數量
	 * @return
	 */
	public boolean consumeEnchantItem(final int id, final int enchant, final long count) {
		for (final L1ItemInstance item : _items) {
			if (item.isEquipped()) { // 不適用於裝備物品 装備しているものは該当しない
				continue;
			}
			if ((item.getItemId() == id) && (item.getEnchantLevel() == enchant)) {
				this.removeItem(item);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 比較未裝備物品數量
	 * 
	 * @param nameid
	 * @param count
	 * @return
	 */
	public boolean checkItemNotEquipped(final String nameid, final long count) {
		if (count == 0) {
			return true;
		}
		return count <= this.countItems(nameid);
	}

	/**
	 * 比較未裝備物品數量
	 * 
	 * @param id
	 * @param count
	 * @return
	 */
	public boolean checkItemNotEquipped(final int id, final long count) {
		if (count == 0) {
			return true;
		}
		return count <= this.countItems(id);
	}

	// 檢查您是否擁有某種物品所需的全部數量（檢查您是否擁有某個活動的多件物品等） 特定のアイテムを全て必要な個数所持しているか確認（イベントとかで複数のアイテムを所持しているか確認するため）
	public boolean checkItem(final int[] ids) {
		final int len = ids.length;
		final int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return this.checkItem(ids, counts);
	}

	public boolean checkItem(final int[] ids, final int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!this.checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找未裝備物品數量
	 * 
	 * @param itemId
	 * @return
	 */
	public long countItems(final int itemId) {
		// 可堆疊
		if (ItemTable.get().getTemplate(itemId).isStackable()) {
			final L1ItemInstance item = this.findItemId(itemId);
			if (item != null) {
				return item.getCount();
			}

			// 不可堆疊
		} else {
			final Object[] itemList = this.findItemsIdNotEquipped(itemId);
			return itemList.length;
		}
		return 0;
	}

	/**
	 * 查找未裝備物品數量
	 * 
	 * @param nameid
	 * @return
	 */
	public long countItems(final String nameid) {
		// 可堆疊
		if (ItemTable.get().getTemplate(nameid).isStackable()) {
			final L1ItemInstance item = this.findItemId(nameid);
			if (item != null) {
				return item.getCount();
			}

			// 不可堆疊
		} else {
			final Object[] itemList = this.findItemsIdNotEquipped(nameid);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(_items);
	}

	/**
	 * 背包內全部物件刪除
	 */
	public void clearItems() {
		for (final Object itemObject : _items) {
			final L1ItemInstance item = (L1ItemInstance) itemObject;
			World.get().removeObject(item);
		}
		_items.clear(); // 不確定是否要清除?
	}

	// オーバーライド用
	public void loadItems() {
	}

	public void insertItem(final L1ItemInstance item) {
	}

	public void updateItem(final L1ItemInstance item) {
	}

	public void updateItem(final L1ItemInstance item, final int colmn) {
	}

}
