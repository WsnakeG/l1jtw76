package com.lineage.data.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.lock.CharShiftingReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_ItemCount;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.utils.DigitalUtil;
import com.lineage.server.world.World;

/**
 * 給予物件的處理
 * 
 * @author dexc
 */
public class CreateNewItem {

	private static final Log _log = LogFactory.getLog(CreateNewItem.class);

	/**
	 * 刪除需要的材料(僅於刪除/不會給與)
	 * 
	 * @param pc
	 * @param srcItemIds 刪除物品群
	 * @param counts 刪除物品數量群
	 * @param amount 數量
	 * @return 是否刪除完成 true可以執行 false:有錯誤
	 */
	public static boolean delItems(final L1PcInstance pc, final int[] srcItemIds, final int[] counts,
			final long amount) {
		try {
			if (pc == null) {
				return false;
			}
			if (amount <= 0) {
				return false;
			}
			if (srcItemIds.length <= 0) {
				return false;
			}
			if (counts.length <= 0) {
				return false;
			}
			if (srcItemIds.length != counts.length) {
				_log.error("道具交換物品與數量陣列設置異常!");
				return false;
			}

			// 需要物件數量確認
			for (int i = 0; i < srcItemIds.length; i++) {
				final long itemCount = counts[i] * amount;// 需要的物件數量
				final L1ItemInstance item = pc.getInventory().checkItemX(srcItemIds[i], itemCount);// 需要的物件確認
				if (item == null) {
					return false;
				}
			}
			// 刪除需要物件(材料)
			for (int i = 0; i < srcItemIds.length; i++) {
				final long itemCount1 = counts[i] * amount;// 需要的物件數量 * 換取數量
				// 需要的物件確認
				final L1ItemInstance item = pc.getInventory().checkItemX(srcItemIds[i], itemCount1);
				if (item != null) {
					pc.getInventory().removeItem(item, itemCount1);// 刪除道具

				} else {
					// 刪除失敗
					return false;
				}
			}
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 交換物品<BR>
	 * amount(指定交換數量)為0時 傳送出false以及數量選取網頁<BR>
	 * amount(指定交換數量)不為0時 傳送出true執行物件交換<BR>
	 * 這個方法的缺點為選取數量網業輸出為原始HTML命令<BR>
	 * 如果文字串較長 將會導致封包變大<BR>
	 * 
	 * @param pc 執行者
	 * @param pc 對話NPC
	 * @param items 需要物件組
	 * @param counts 需要物件數量組
	 * @param gitems 給予物件組
	 * @param gcounts 給予物件組數量組
	 * @param amount 指定交換數量
	 * @return
	 */
	public static boolean getItem(final L1PcInstance pc, final L1NpcInstance npc, final String cmd,
			final int[] items, final int[] counts, final int[] gitems, final int[] gcounts,
			final long amount) {
		// 可製作數量
		final long xcount = checkNewItem(pc, items, counts);
		if (xcount > 0) {
			if (amount == 0) {
				pc.sendPackets(new S_ItemCount(npc.getId(), (int) xcount, cmd));
				return false;

			} else {
				if (xcount >= amount) {
					// 收回需要物件 給予完成物件
					createNewItem(pc, items, counts, // 需要
							gitems, amount, gcounts);// 給予
				}
				return true;
			}
		}
		return true;
	}

	// TODO 換取物品數量預先處理(單一需求物品)

	/**
	 * 換取物品數量預先處理(單一需求物品)<BR>
	 * 數量不足傳回訊息
	 * 
	 * @param pc 人物
	 * @param srcItemId 需要的物件
	 * @param count 需要的數量
	 * @return 可換取的數量
	 */
	public static long checkNewItem(final L1PcInstance pc, final int srcItemId, final int count) {
		try {
			if (pc == null) {
				return -1;
			}

			final L1ItemInstance item = pc.getInventory().findItemIdNoEq(srcItemId);// 需要的物件

			long itemCount = -1;

			if (item != null) {
				itemCount = item.getCount() / count;
			}

			if (itemCount < 1) {
				// 原始物件資料
				final L1Item tgItem = ItemTable.get().getTemplate(srcItemId);
				// 337 \f1%0不足%s。
				pc.sendPackets(new S_ServerMessage(337,
						tgItem.getNameId() + "(" + (count - (item == null ? 0 : item.getCount())) + ")"));
				return -1;
			}
			return itemCount;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return -1;
	}

	// TODO 換取物品數量處理(需要複數物件)

	/**
	 * 換取物品數量預先處理(需要複數物件)<BR>
	 * 檢查人物可換取數量<BR>
	 * 數量不足傳回訊息
	 * 
	 * @param pc 人物
	 * @param srcItemIds 需要的物件(複數)
	 * @param counts 需要的數量(複數)
	 * @return 可換取的數量
	 */
	public static long checkNewItem(final L1PcInstance pc, final int[] srcItemIds, final int[] counts) {
		try {
			if (pc == null) {
				return -1;
			}

			if (srcItemIds.length <= 0) {
				return -1;
			}
			if (counts.length <= 0) {
				return -1;
			}
			if (srcItemIds.length != counts.length) {
				_log.error("道具交換物品與數量陣列設置異常!");
				return -1;
			}

			// 即將輸出檢查可交換數量的數據
			final long[] checkCount = new long[srcItemIds.length];

			boolean error = false;

			// 需要物件數量確認
			for (int i = 0; i < srcItemIds.length; i++) {
				final int itemid = srcItemIds[i];// 需要的物品
				final int count = counts[i];// 需要的數量

				// 人物背包該物件數據
				final L1ItemInstance item = pc.getInventory().findItemIdNoEq(itemid);

				if (item != null) {
					final long itemCount = item.getCount() / count;

					// 建立該編號物件可交換數量
					checkCount[i] = itemCount;
					if (itemCount < 1) {
						// 337 \f1%0不足%s。
						pc.sendPackets(new S_ServerMessage(337,
								item.getName() + "(" + (count - item.getCount()) + ")"));
						error = true;
					}

				} else {
					// 原始物件資料
					final L1Item tgItem = ItemTable.get().getTemplate(itemid);
					// 337 \f1%0不足%s。
					pc.sendPackets(new S_ServerMessage(337, tgItem.getNameId() + "(" + (count) + ")"));
					error = true;
				}
			}

			// 道具足
			if (!error) {
				final long count = DigitalUtil.returnMin(checkCount);
				return count;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return -1;
	}

	// TODO 換取物品數量處理(需要複數物件/給予複數物件 且 數量大於等於1)

	/**
	 * 換取物品數量處理(需要複數物件/給予複數物件 且 數量大於等於1)<BR>
	 * 超過容量/重量不予換取<BR>
	 * 
	 * @param pc 人物
	 * @param srcItemIds 需要的物件(複數)
	 * @param counts 需要的數量(複數)
	 * @param getItemIds 給予的物件編號(複數)
	 * @param amount 交換的數量
	 * @param getCounts 給予的物件數量(複數)
	 */
	public static void createNewItem(final L1PcInstance pc, final int[] srcItemIds, final int[] counts,
			final int[] getItemIds, final long amount, final int[] getCounts) {
		try {
			if (pc == null) {
				return;
			}
			if (amount <= 0) {
				return;
			}
			if (srcItemIds.length <= 0) {
				return;
			}
			if (counts.length <= 0) {
				return;
			}
			if (srcItemIds.length != counts.length) {
				_log.error("道具交換物品與數量陣列設置異常!");
				return;
			}
			if (getItemIds.length <= 0) {
				return;
			}
			if (getCounts.length <= 0) {
				return;
			}
			if (getItemIds.length != getCounts.length) {
				_log.error("道具交換物品與數量陣列設置異常!");
				return;
			}
			boolean error = false;

			// 需要物件數量確認
			for (int i = 0; i < srcItemIds.length; i++) {
				final long itemCount = counts[i] * amount;// 需要的物件數量
				final L1ItemInstance item = pc.getInventory().checkItemX(srcItemIds[i], itemCount);// 需要的物件確認
				if (item == null) {
					error = true;
				}
			}

			if (!error) {
				for (int i = 0; i < getItemIds.length; i++) {
					if (!getItemIsOk(pc, getItemIds[i], amount, getCounts[i])) {
						error = true;
					}
				}
			}

			if (!error) {
				// 刪除需要物件(材料)
				for (int i = 0; i < srcItemIds.length; i++) {
					final long itemCount1 = counts[i] * amount;// 需要的物件數量 * 換取數量
					// 需要的物件確認
					final L1ItemInstance item = pc.getInventory().checkItemX(srcItemIds[i], itemCount1);
					if (item != null) {
						pc.getInventory().removeItem(item, itemCount1);// 刪除道具

					} else {
						// 刪除失敗
						error = true;
					}
				}
			}

			if (!error) {
				// 給予物品
				for (int i = 0; i < getItemIds.length; i++) {
					getItemIs(pc, getItemIds[i], amount, getCounts[i]);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 換取物品數量後處理(需要複數物件/給予的物件數量大於1)<BR>
	 * 檢查 人物 容量/重量<BR>
	 * 
	 * @param pc 人物
	 * @param getItemId 給予的物件編號
	 * @param amount 交換的數量
	 * @param getCount 給予的物件數量
	 * @return true:允許加入物品 false:不允許加入物品
	 */
	private static boolean getItemIsOk(final L1PcInstance pc, final int getItemId, final long amount,
			final int getCount) {
		try {
			if (pc == null) {
				return false;
			}
			// 產容量/重量比對物件
			final L1Item tgItem = ItemTable.get().getTemplate(getItemId);

			// 增加物品是否成功
			if (pc.getInventory().checkAddItem(tgItem, amount * getCount) != L1Inventory.OK) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return true;
	}

	/**
	 * 換取物品數量後處理(需要複數物件/給予的物件數量大於1)<BR>
	 * 給予物品
	 * 
	 * @param pc 人物
	 * @param getItemId 給予的物件編號
	 * @param amount 交換的數量
	 * @param getCount 給予的物件數量
	 */
	private static void getItemIs(final L1PcInstance pc, final int getItemId, final long amount,
			final int getCount) {
		try {
			if (pc == null) {
				return;
			}
			// 原始物件資料
			final L1Item tgItem = ItemTable.get().getTemplate(getItemId);
			// 給予物品可以堆疊
			if (tgItem.isStackable()) {
				final L1ItemInstance tgItemX = ItemTable.get().createItem(getItemId);
				tgItemX.setCount(amount * getCount);// 設置給予物品數量
				createNewItem(pc, tgItemX);// 給予物品

			} else {
				for (int get = 0; get < (amount * getCount); get++) {
					final L1ItemInstance tgItemX = ItemTable.get().createItem(getItemId);
					tgItemX.setCount(1);// 設置給予物品數量
					createNewItem(pc, tgItemX);// 給予物品
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 換取物品後處理(給予的物件數量大於1)<BR>
	 * 超過容量/重量不予換取<BR>
	 * 
	 * @param pc 人物
	 * @param srcItemId 需要的物件
	 * @param count 需要的數量
	 * @param getItemId 給予的物件
	 * @param getCount 給予的物件數量
	 */
	public static void createNewItem(final L1PcInstance pc, final int srcItemId, final int count,
			final int getItemId, final int getCount) {
		createNewItem(pc, srcItemId, count, getItemId, 1, getCount);
	}

	/**
	 * 換取物品後處理(給予的物件數量大於1)<BR>
	 * 超過容量/重量不予換取<BR>
	 * 
	 * @param pc 人物
	 * @param srcItemId 需要的物件
	 * @param count 需要的數量
	 * @param getItemId 給予的物件
	 * @param amount 交換的數量
	 * @param getCount 給予的物件數量
	 */
	public static void createNewItem(final L1PcInstance pc, final int srcItemId, final int count,
			final int getItemId, final long amount, final int getCount) {
		final long itemCount1 = count * amount;// 需要的物件數量
		final L1ItemInstance item1 = pc.getInventory().checkItemX(srcItemId, itemCount1);// 需要的物件確認
		if (item1 != null) {
			// 產生新物件
			final L1ItemInstance tgItem = ItemTable.get().createItem(getItemId);

			// 增加物品是否成功
			if (pc.getInventory().checkAddItem(tgItem, amount * getCount) == L1Inventory.OK) {
				pc.getInventory().removeItem(item1, itemCount1);// 刪除道具
				// 物品可以堆疊
				if (tgItem.isStackable()) {
					tgItem.setCount(amount * getCount);// 設置給予物品數量
					createNewItem(pc, tgItem);// 給予物品

				} else {
					for (int get = 0; get < (amount * getCount); get++) {
						final L1ItemInstance tgItemX = ItemTable.get().createItem(getItemId);
						tgItemX.setCount(1);// 設置給予物品數量
						createNewItem(pc, tgItemX);// 給予物品
					}
				}

			} else {
				// 移出世界
				World.get().removeObject(tgItem);
			}
		}
	}

	/**
	 * 換取物品數量後處理(需要數量為1, 給予數量為1)<BR>
	 * 超過容量/重量不予換取
	 * 
	 * @param pc 人物
	 * @param srcItemId 需要的物件
	 * @param count 需要的數量
	 * @param getItemId 給予的物件
	 * @param amount 交換的數量
	 */
	public static void createNewItem(final L1PcInstance pc, final int srcItemId, final int count,
			final int getItemId, final long amount) {
		final long itemCount1 = count * amount;// 需要的物件數量
		final L1ItemInstance item1 = pc.getInventory().checkItemX(srcItemId, itemCount1);// 需要的物件確認
		if (item1 != null) {
			// 產生新物件
			final L1ItemInstance tgItem = ItemTable.get().createItem(getItemId);

			// 增加物品是否成功
			if (pc.getInventory().checkAddItem(tgItem, amount) == L1Inventory.OK) {
				pc.getInventory().removeItem(item1, itemCount1);// 刪除道具
				// 物品可以堆疊
				if (tgItem.isStackable()) {
					tgItem.setCount(amount);// 設置給予物品數量
					createNewItem(pc, tgItem);// 給予物品

				} else {
					for (int get = 0; get < amount; get++) {
						final L1ItemInstance tgItemX = ItemTable.get().createItem(getItemId);
						tgItemX.setCount(1);// 設置給予物品數量
						createNewItem(pc, tgItemX);// 給予物品
					}
				}
			}
		}
	}

	/**
	 * 給予物件的處理(物件已加入世界)
	 * 
	 * @param pc 執行人物
	 * @param item 物件
	 * @return
	 */
	public static void createNewItem(final L1PcInstance pc, final L1ItemInstance item) {
		try {
			if (pc == null) {
				return;
			}
			if (item == null) {
				return;
			}
			pc.getInventory().storeItem(item);
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 給予物件的處理(物件已加入世界)<BR>
	 * 超出數量掉落地面
	 * 
	 * @param pc
	 * @param item
	 * @param count
	 */
	public static void createNewItem(final L1PcInstance pc, final L1ItemInstance item, final long count) {
		try {
			if (pc == null) {
				return;
			}
			if (item == null) {
				return;
			}
			item.setCount(count);

			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);

			} else {
				item.set_showId(pc.get_showId());
				// 掉落地面
				World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}

			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 給予物件的處理(超出容量到落地面)<BR>
	 * 只能給予可堆疊物品<BR>
	 * 不能堆疊限定只能給一個<BR>
	 * 
	 * @param pc 執行人物
	 * @param item_id 物件編號
	 * @param count 數量
	 * @return
	 */
	public static boolean createNewItem(final L1PcInstance pc, final int item_id, final long count) {
		try {
			if (pc == null) {
				return false;
			}
			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(item_id);
			if (item != null) {
				item.setCount(count);
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					pc.getInventory().storeItem(item);

				} else {
					item.set_showId(pc.get_showId());
					// 掉落地面
					World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
				}
				pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // 获得0%。
				return true;

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 給予任務物件的處理(超出容量到落地面)<BR>
	 * 只能給予可堆疊物品<BR>
	 * 不能堆疊限定只能給一個<BR>
	 * 
	 * @param atk 給予的對象
	 * @param npc 給予物品的NPC
	 * @param item_id 物件編號
	 * @param count 數量
	 */
	public static void getQuestItem(final L1Character atk, final L1NpcInstance npc, final int item_id,
			final long count) {
		try {
			if (atk == null) {
				return;
			}
			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(item_id);
			if (item != null) {
				item.setCount(count);
				if (atk.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					atk.getInventory().storeItem(item);

				} else {
					item.set_showId(atk.get_showId());
					// 掉落地面
					World.get().getInventory(atk.getX(), atk.getY(), atk.getMapId()).storeItem(item);
				}

				if (atk instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) atk;
					if (npc != null) {
						// \f1%0%s 給你 %1%o 。
						pc.sendPackets(
								new S_HelpMessage("\\fW" + npc.getNameId() + "給你" + item.getLogName()));
					} else {
						// \f1%0%s 給你 %1%o 。
						pc.sendPackets(new S_HelpMessage("\\fW" + "給你", item.getLogName()));
					}
				}

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 防具武器 升級/交換 (保留原始效果)

	/**
	 * 防具武器 升級/交換 (保留原始效果)
	 * 
	 * @param pc 執行人物
	 * @param srcItem 原始物件
	 * @param newItem 新物件
	 * @param enchant 強化質大於設定降低強化質
	 * @param down 降低的數量
	 * @param mode 模式 0: 交換裝備 1: 裝備升級 2: 轉移裝備
	 */
	public static void updateA(final L1PcInstance pc, final L1ItemInstance srcItem,
			final L1ItemInstance newItem, final int enchant, final int down, final int mode) {
		try {
			if (pc == null) {
				return;
			}
			if (srcItem == null) {
				return;
			}
			if (newItem == null) {
				return;
			}
			newItem.setCount(1);
			// 強化質大於0
			if (srcItem.getEnchantLevel() > enchant) {
				newItem.setEnchantLevel(srcItem.getEnchantLevel() - down);

			} else {
				newItem.setEnchantLevel(srcItem.getEnchantLevel());
			}

			newItem.setAttrEnchantKind(srcItem.getAttrEnchantKind());
			newItem.setAttrEnchantLevel(srcItem.getAttrEnchantLevel());
			newItem.setIdentified(true);

			final int srcObjid = srcItem.getId();
			final L1Item srcItemX = srcItem.getItem();
			// 刪除原始物件
			if (pc.getInventory().removeItem(srcItem) == 1) {
				// 給予物品
				pc.getInventory().storeItem(newItem);

				pc.sendPackets(new S_ServerMessage(403, newItem.getLogName())); // 获得0%。

				// 建立紀錄
				CharShiftingReading.get().newShifting(pc, 0, null, srcObjid, srcItemX, newItem, mode);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// TODO 防具武器 升級/交換 (不保留原始效果)

	/**
	 * 防具武器升級/交換 (不保留原始效果)
	 * 
	 * @param pc 執行人物
	 * @param srcItem 原始物件
	 * @param newid 新物件編號
	 */
	public static void updateB(final L1PcInstance pc, final L1ItemInstance srcItem, final int newid) {
		try {
			if (pc == null) {
				return;
			}
			if (srcItem == null) {
				return;
			}

			final L1ItemInstance newItem = ItemTable.get().createItem(newid);
			if (newItem != null) {
				// 刪除原始物件
				if (pc.getInventory().removeItem(srcItem) == 1) {
					// 給予物品
					pc.getInventory().storeItem(newItem);
				}

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + newid + ")");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 給予物件的處理(超出容量掉落地面)<BR>
	 * 只能給予可堆疊物品<BR>
	 * 不能堆疊限定只能給一個<BR>
	 * [創造出的物品為未鑑定狀態]<BR>
	 * (不允許掉落地面, 不顯示獲得訊息)
	 * 
	 * @param pc 執行人物
	 * @param item_id 物件編號
	 * @param count 數量
	 * @return
	 * @author terry0412
	 */
	public static boolean createNewItemW(final L1PcInstance pc, final int item_id, final long count) {
		try {
			if (pc == null) {
				return false;
			}
			// 產生新物件
			final L1ItemInstance item = ItemTable.get().createItem(item_id);
			if (item != null) {
				item.setCount(count);
				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					pc.getInventory().storeItem(item);
					return true;
				}

			} else {
				_log.error("給予物件失敗 原因: 指定編號物品不存在(" + item_id + ")");
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	/**
	 * NPC給予道具(身上空間不足將掉落地面)
	 * @param pc
	 * @param npcName
	 * @param item_id
	 * @param count
	 */
	public static void createNewItem_NPC(L1PcInstance pc, String npcName,
			int item_id, int count) {
		L1ItemInstance item = ItemTable.get().createItem(item_id);
		item.setCount(count);
		if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
			pc.getInventory().storeItem(item);
		} else {
			World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
		}
		pc.sendPackets(new S_ServerMessage(143, npcName, item.getLogName()));
	}
}
