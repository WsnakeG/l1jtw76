package com.lineage.server.datatables.storage;

import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 精靈倉庫物件
 * 
 * @author dexc
 */
public interface DwarfForElfStorage {

	/**
	 * 預先加載
	 */
	public void load();

	/**
	 * 傳回精靈倉庫數據
	 * 
	 * @return
	 */
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String account_name);

	/**
	 * 刪除精靈倉庫資料(完整)
	 * 
	 * @param account_name
	 */
	public void delUserItems(final String account_name);

	/**
	 * 該精靈倉庫是否有指定數據
	 * 
	 * @param account_name
	 * @param objid
	 * @param count
	 * @return
	 */
	public boolean getUserItems(final String account_name, final int objid, int count);

	/**
	 * 加入精靈倉庫數據
	 */
	public void insertItem(final String account_name, final L1ItemInstance item);

	/**
	 * 精靈倉庫資料更新(物品數量)
	 * 
	 * @param item
	 */
	public void updateItem(final L1ItemInstance item);

	/**
	 * 精靈倉庫物品資料刪除
	 * 
	 * @param account_name
	 * @param item
	 */
	public void deleteItem(final String account_name, final L1ItemInstance item);

}
