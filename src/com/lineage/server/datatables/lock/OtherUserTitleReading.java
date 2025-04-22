package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.OtherUserTradeTable;
import com.lineage.server.datatables.storage.OtherUserTradeStorage;

/**
 * 個人交易物品紀錄
 * 
 * @author dexc
 */
public class OtherUserTitleReading {

	private final Lock _lock;

	private final OtherUserTradeStorage _storage;

	private static OtherUserTitleReading _instance;

	private OtherUserTitleReading() {
		_lock = new ReentrantLock(true);
		_storage = new OtherUserTradeTable();
	}

	public static OtherUserTitleReading get() {
		if (_instance == null) {
			_instance = new OtherUserTitleReading();
		}
		return _instance;
	}

	/**
	 * 增加紀錄
	 * 
	 * @param itemname 物品名稱
	 * @param itemobjid 物品OBJID
	 * @param itemadena 0 暫無用途
	 * @param itemcount 數量
	 * @param pcobjid 移入人物OBJID
	 * @param pcname 移入人物名稱
	 * @param srcpcobjid 移出人物者OBJID
	 * @param srcpcname 移出人物名稱
	 */
	public void add(final String itemname, final int itemobjid, final int itemadena, final long itemcount,
			final int pcobjid, final String pcname, final int srcpcobjid, final String srcpcname) {
		_lock.lock();
		try {
			_storage.add(itemname, itemobjid, itemadena, itemcount, pcobjid, pcname, srcpcobjid, srcpcname);

		} finally {
			_lock.unlock();
		}
	}
}
