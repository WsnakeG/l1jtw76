package com.lineage.server.datatables.storage;

import java.sql.Timestamp;

/**
 * 物品使用期限記錄
 * 
 * @author dexc
 */
public interface CharItemsTimeStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 增加物品使用期限記錄
	 * 
	 * @param number
	 */
	public void addTime(final int itemr_obj_id, final Timestamp usertime);

	/**
	 * 更新物品使用期限記錄
	 * 
	 * @param itemr_obj_id
	 * @param usertime
	 */
	public void updateTime(final int itemr_obj_id, final Timestamp usertime);

}
