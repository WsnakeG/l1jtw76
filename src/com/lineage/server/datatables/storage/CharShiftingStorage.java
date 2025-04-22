package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

/**
 * 裝備移轉紀錄資料
 * 
 * @author dexc
 */
public interface CharShiftingStorage {

	/**
	 * 增加裝備移轉紀錄
	 * 
	 * @param pc 執行人物
	 * @param tgId 目標objid
	 * @param tgName 目標名稱
	 * @param srcObjid 原始objid
	 * @param srcItem 原始物件
	 * @param newItem 新物件
	 * @param mode 模式<BR>
	 *            0: 交換裝備<BR>
	 *            1: 裝備升級<BR>
	 *            2: 轉移裝備<BR>
	 */
	public void newShifting(final L1PcInstance pc, int tgId, final String tgName, final int srcObjid,
			final L1Item srcItem, final L1ItemInstance newItem, final int mode);

}
