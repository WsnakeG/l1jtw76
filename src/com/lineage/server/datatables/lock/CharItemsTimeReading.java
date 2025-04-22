package com.lineage.server.datatables.lock;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharItemsTimeTable;
import com.lineage.server.datatables.storage.CharItemsTimeStorage;

/**
 * 人物背包物品使用期限資料
 * 
 * @author dexc
 */
public class CharItemsTimeReading {

	private final Lock _lock;

	private final CharItemsTimeStorage _storage;

	private static CharItemsTimeReading _instance;

	private CharItemsTimeReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharItemsTimeTable();
	}

	public static CharItemsTimeReading get() {
		if (_instance == null) {
			_instance = new CharItemsTimeReading();
		}
		return _instance;
	}

	/**
	 * 資料預先載入
	 */
	public void load() {
		_lock.lock();
		try {
			_storage.load();

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 增加物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	public void addTime(final int itemr_obj_id, final Timestamp usertime) {
		_lock.lock();
		try {
			_storage.addTime(itemr_obj_id, usertime);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	public void updateTime(final int itemr_obj_id, final Timestamp usertime) {
		_lock.lock();
		try {
			_storage.updateTime(itemr_obj_id, usertime);

		} finally {
			_lock.unlock();
		}
	}
}
