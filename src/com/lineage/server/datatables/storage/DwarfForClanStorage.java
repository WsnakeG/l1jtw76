package com.lineage.server.datatables.storage;

import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 血盟倉庫物件清單
 * 
 * @author dexc
 */
public interface DwarfForClanStorage {

	/**
	 * 預先加載
	 */
	public void load();

	/**
	 * 傳回血盟倉庫數據
	 * 
	 * @return
	 */
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String clan_name);

	/**
	 * 刪除血盟倉庫資料(完整)
	 * 
	 * @param account_name
	 */
	public void delUserItems(final String clan_name);

	/**
	 * 該血盟倉庫是否有指定數據
	 * 
	 * @param clan_name
	 * @param objid
	 * @param count
	 * @return
	 */
	public boolean getUserItems(final String clan_name, final int objid, int count);

	/**
	 * 加入血盟倉庫數據
	 */
	public void insertItem(final String clan_name, final L1ItemInstance item);

	/**
	 * 血盟倉庫資料更新(物品數量)
	 * 
	 * @param item
	 */
	public void updateItem(final L1ItemInstance item);

	/**
	 * 血盟倉庫物品資料刪除
	 * 
	 * @param account_name
	 * @param item
	 */
	public void deleteItem(final String clan_name, final L1ItemInstance item);
}
