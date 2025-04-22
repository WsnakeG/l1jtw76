package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther;

/**
 * 額外紀錄資料
 * 
 * @author dexc
 */
public interface CharOtherStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 取回保留額外紀錄
	 * 
	 * @param pc
	 */
	public L1PcOther getOther(final L1PcInstance pc);

	/**
	 * 增加保留額外紀錄
	 * 
	 * @param objId
	 * @param other
	 */
	public void storeOther(final int objId, final L1PcOther other);

	/**
	 * 歸0殺人次數
	 */
	public void tam();

}
