package com.lineage.server.datatables.storage;

/**
 * 服務器資料
 */
public interface ServerStorage {

	/**
	 * 預先加載服務器存檔資料
	 */
	public void load();

	/**
	 * 傳回服務器最小編號設置
	 */
	public int minId();

	/**
	 * 傳回服務器最大編號設置
	 */
	public int maxId();

	/**
	 * 設定服務器關機<BR>
	 * 同時記錄已用最大編號
	 */
	public void isStop();

}
