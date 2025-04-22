package com.lineage.server.datatables.storage;

import java.sql.Timestamp;

/**
 * 物品使用期限記錄
 * 
 * @author dexc
 */
public interface CharWeaponStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 增加物品使用期限記錄
	 * 
	 * @param number
	 */
	public void addTime(final int itemr_obj_id, final Timestamp usertime, int magic_weapon);

	/**
	 * 更新物品使用期限記錄
	 * 
	 * @param itemr_obj_id
	 * @param usertime
	 */
	public void updateTime(final int itemr_obj_id, final Timestamp usertime, int magic_weapon, int steps, int same);

}
