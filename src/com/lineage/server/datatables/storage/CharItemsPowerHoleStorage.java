package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 物品凹槽資料
 * 
 * @author dexc
 */
public interface CharItemsPowerHoleStorage {

	/**
	 * 資料預先載入
	 */
	public void load();

	/**
	 * 增加物品凹槽資料
	 * 
	 * @param objid
	 * @param power
	 * @return
	 */
	public void storeItem(final int objId, final L1ItemPowerHole_name power) throws Exception;

	/**
	 * 更新凹槽資料
	 * 
	 * @param item_obj_id
	 * @param power
	 */
	public void updateItem(final int item_obj_id, final L1ItemPowerHole_name power);

	public void delItem(int objId);
}
