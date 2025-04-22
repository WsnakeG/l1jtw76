package com.lineage.server.datatables.storage;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 人物背包資料
 * 
 * @author dexc
 */
public interface CharItemsStorage {

	/**
	 * 資料預先載入
	 */
	public void load();

	/**
	 * 傳回該人物背包資料
	 * 
	 * @param objid
	 * @return
	 */
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final Integer objid);

	/**
	 * 刪除人物背包資料
	 * 
	 * @param objid
	 */
	public void delUserItems(final Integer objid);

	/**
	 * 該人物背包是否有指定數據
	 * 
	 * @param pcObjid
	 * @param objid
	 * @param count
	 * @return
	 */
	public boolean getUserItems(final Integer pcObjid, final int objid, final long count);

	/**
	 * 是否有指定數據
	 * 
	 * @param pcObjid
	 * @param objid
	 * @param count
	 * @return
	 */
	public L1ItemInstance getUserItem(final int objid);

	/**
	 * 刪除指定編號全部數據
	 * 
	 * @param itemid
	 */
	public void del_item(final int itemid);

	/**
	 * 增加背包物品
	 * 
	 * @param objId 人物OBJID
	 * @param item 物品
	 * @throws Exception
	 */
	public void storeItem(final int objId, final L1ItemInstance item) throws Exception;

	/**
	 * 刪除背包物品
	 * 
	 * @param objid 人物OBJID
	 * @param item 物品
	 * @throws Exception
	 */
	public void deleteItem(final int objid, final L1ItemInstance item) throws Exception;

	/**
	 * 更新物品ITEMID 與中文名稱
	 * 
	 * @param item
	 */
	public void updateItemId_Name(final L1ItemInstance item) throws Exception;

	/**
	 * 更新ITEMID
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemId(final L1ItemInstance item) throws Exception;

	/**
	 * 更新數量
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemCount(final L1ItemInstance item) throws Exception;

	/**
	 * 更新損壞度
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemDurability(final L1ItemInstance item) throws Exception;

	/**
	 * 更新可用次數
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemChargeCount(final L1ItemInstance item) throws Exception;

	/**
	 * 更新可用時間
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemRemainingTime(final L1ItemInstance item) throws Exception;

	/**
	 * 更新強化度
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemEnchantLevel(final L1ItemInstance item) throws Exception;

	/**
	 * 更新使用狀態
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemEquipped(final L1ItemInstance item) throws Exception;

	/**
	 * 更新鑑定狀態
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemIdentified(final L1ItemInstance item) throws Exception;

	/**
	 * 更新祝福狀態
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemBless(final L1ItemInstance item) throws Exception;

	/**
	 * 更新強化屬性
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemAttrEnchantKind(final L1ItemInstance item) throws Exception;

	/**
	 * 更新強化屬性強化度
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemAttrEnchantLevel(final L1ItemInstance item) throws Exception;

	/**
	 * 更新最後使用時間
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemDelayEffect(final L1ItemInstance item) throws Exception;

	/**
	 * 傳回對應所有物品數量
	 * 
	 * @param objId
	 * @return
	 * @throws Exception
	 */
	public int getItemCount(final int objId) throws Exception;

	/**
	 * 給予金幣(對離線人物)
	 * 
	 * @param objId
	 * @param count
	 * @throws Exception
	 */
	public void getAdenaCount(final int objId, final long count) throws Exception;

	/**
	 * 傳回傭有該物品ID的人物清單<BR>
	 * (適用該物品每人只能傭有一個的狀態)
	 * 
	 * @param itemid
	 * @return
	 */
	public Map<Integer, L1ItemInstance> getUserItems(int itemid);

	/**
	 * 檢查指定道具編號的總世界數量
	 * 
	 * @param pcObjid
	 * @param objid
	 * @param count
	 * @return
	 * @author terry0412
	 */
	public int checkItemId(final int itemId);

	/**
	 * 更新潘朵拉狀態
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateItemPandoraType(final L1ItemInstance item) throws Exception;

	public void updateItemRandom(final L1ItemInstance item) throws Exception;

}
