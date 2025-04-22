package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1ItemPower_name;

/**
 * 人物古文字物品資料
 * 
 * @author dexc
 */
public interface CharItemsPowerStorage {

	/**
	 * 資料預先載入
	 */
	public void load();

	/**
	 * 增加古文字物品資料
	 * 
	 * @param objid
	 * @param power
	 * @return
	 */
	public void storeItem(final int objId, final L1ItemPower_name power) throws Exception;

	public void delItem(int objId);

	/**
	 * 更新凹槽資料
	 * 
	 * @param item_obj_id
	 * @param power
	 */
	public void updateItem(final int item_obj_id, final L1ItemPower_name power);
}
