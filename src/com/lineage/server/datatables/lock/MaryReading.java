package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.MaryTable;
import com.lineage.server.datatables.storage.MaryStorage;

/**
 * 小瑪莉資料紀錄
 * 
 * @author dexc
 */
public class MaryReading {

	private final Lock _lock;

	private final MaryStorage _storage;

	private static MaryReading _instance;

	private MaryReading() {
		_lock = new ReentrantLock(true);
		_storage = new MaryTable();
	}

	public static MaryReading get() {
		if (_instance == null) {
			_instance = new MaryReading();
		}
		return _instance;
	}

	/**
	 * 初始化載入
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
	 * 更新紀錄
	 * 
	 * @param all_stake
	 * @param all_user_prize
	 * @param count
	 */
	public void update(final long all_stake, final long all_user_prize, final int count) {
		_lock.lock();
		try {
			_storage.update(all_stake, all_user_prize, count);

		} finally {
			_lock.unlock();
		}
	}
}
