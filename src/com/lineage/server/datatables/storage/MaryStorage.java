package com.lineage.server.datatables.storage;

/**
 * 小瑪莉資料紀錄
 * 
 * @author dexc
 */
public interface MaryStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 更新紀錄
	 * 
	 * @param all_stake
	 * @param all_user_prize
	 * @param count
	 */
	public void update(final long all_stake, final long all_user_prize, final int count);

}
