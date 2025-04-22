package com.lineage.server.datatables.storage;

/**
 * 陷阱數據接口
 * 
 * @author daien
 */
public interface TrapStorage {

	/**
	 * 文字類型欄位
	 * 
	 * @param name 欄位名稱
	 * @return
	 */
	public String getString(String name);

	/**
	 * 數字類型欄位
	 * 
	 * @param name 欄位名稱
	 * @return
	 */
	public int getInt(String name);

	/**
	 * boolean 類型欄位
	 * 
	 * @param name 欄位名稱
	 * @return
	 */
	public boolean getBoolean(String name);
}
