package com.lineage.server.datatables.storage;

import java.sql.Timestamp;
import java.util.Map;

import com.lineage.server.model.Instance.L1PcInstance;

/**
 * VIP紀錄資料
 * 
 * @author dexc
 */
public interface VIPStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 全部VIP紀錄
	 * 
	 * @return
	 */
	public Map<Integer, Timestamp> map();

	/**
	 * 取回VIP系統紀錄
	 * 
	 * @param pc
	 */
	public Timestamp getOther(final L1PcInstance pc);

	/**
	 * 增加/更新 VIP系統紀錄
	 * 
	 * @param key
	 * @param value
	 */
	public void storeOther(final int key, final Timestamp value);

	/**
	 * 刪除VIP系統紀錄
	 * 
	 * @param key PC OBJID
	 */
	public void delOther(final int key);

}
