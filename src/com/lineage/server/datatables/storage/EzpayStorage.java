package com.lineage.server.datatables.storage;

import java.util.Map;

/**
 * 網站購物資料
 * 
 * @author dexc
 */
public interface EzpayStorage {

	/**
	 * 傳回指定帳戶匯款資料
	 * 
	 * @param loginName 帳號名稱
	 * @return
	 */
	public Map<Integer, int[]> ezpayInfo(final String loginName);

	/**
	 * 傳回指定帳戶匯款資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id 流水號
	 * @return
	 */
	public int[] ezpayInfo(final String loginName, final int id);

	/**
	 * 更新資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 */
	public boolean update(final String loginName, final int id, final String pcname, final String ip);

}
