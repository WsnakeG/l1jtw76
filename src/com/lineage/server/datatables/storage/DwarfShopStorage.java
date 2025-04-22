package com.lineage.server.datatables.storage;

import java.util.HashMap;
import java.util.Map;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ShopS;

/**
 * 託售物件清單
 * 
 * @author dexc
 */
public interface DwarfShopStorage {

	/**
	 * 預先加載
	 */
	public void load();

	public int get_id();

	public void set_id(int id);

	/**
	 * 傳回全部出售中物件資料數據
	 * 
	 * @return
	 */
	public HashMap<Integer, L1ShopS> allShopS();

	/**
	 * 傳回全部託售物件數據
	 * 
	 * @return
	 */
	public Map<Integer, L1ItemInstance> allItems();

	/**
	 * 傳回指定託售物件數據
	 * 
	 * @return
	 */
	public L1ShopS getShopS(int objid);

	/**
	 * 指定人物託售紀錄
	 * 
	 * @param pcobjid
	 * @return
	 */
	public HashMap<Integer, L1ShopS> getShopSMap(int pcobjid);

	/**
	 * 加入託售物件數據
	 * 
	 * @param key
	 * @param item
	 * @param shopS
	 */
	public void insertItem(final int key, final L1ItemInstance item, final L1ShopS shopS);

	/**
	 * 資料更新(託售狀態)
	 * 
	 * @param item
	 */
	public void updateShopS(final L1ShopS shopS);

	/**
	 * 託售物件資料刪除
	 * 
	 * @param key
	 */
	public void deleteItem(final int key);

}
