package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.OtherUserSellTable;
import com.lineage.server.datatables.storage.OtherUserSellStorage;

/**
 * 賣出物品給個人商店紀錄
 * 
 * @author dexc
 */
public class OtherUserSellReading {

	private final Lock _lock;

	private final OtherUserSellStorage _storage;

	private static OtherUserSellReading _instance;

	private OtherUserSellReading() {
		_lock = new ReentrantLock(true);
		_storage = new OtherUserSellTable();
	}

	public static OtherUserSellReading get() {
		if (_instance == null) {
			_instance = new OtherUserSellReading();
		}
		return _instance;
	}

	/**
	 * 增加紀錄
	 * 
	 * @param itemname 回收物品名稱
	 * @param itemobjid 回收物品OBJID
	 * @param itemadena 單件物品回收金額
	 * @param itemcount 回收數量
	 * @param pcobjid 賣出者OBJID
	 * @param pcname 賣出者名稱
	 * @param srcpcobjid 買入者OBJID(個人商店)
	 * @param srcpcname 買入者名稱(個人商店)
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
