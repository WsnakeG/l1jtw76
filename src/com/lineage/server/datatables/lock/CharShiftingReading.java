package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharShiftingTable;
import com.lineage.server.datatables.storage.CharShiftingStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

/**
 * 裝備移轉紀錄資料
 * 
 * @author dexc
 */
public class CharShiftingReading {

	private final Lock _lock;

	private final CharShiftingStorage _storage;

	private static CharShiftingReading _instance;

	private CharShiftingReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharShiftingTable();
	}

	public static CharShiftingReading get() {
		if (_instance == null) {
			_instance = new CharShiftingReading();
		}
		return _instance;
	}

	/**
	 * 增加裝備移轉紀錄資料
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
	public void newShifting(final L1PcInstance pc, final int tgId, final String tgName, final int srcObjid,
			final L1Item srcItem, final L1ItemInstance newItem, final int mode) {
		_lock.lock();
		try {
			_storage.newShifting(pc, tgId, tgName, srcObjid, srcItem, newItem, mode);

		} finally {
			_lock.unlock();
		}
	}

}
