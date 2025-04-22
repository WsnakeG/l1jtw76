package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.FurnitureSpawnTable;
import com.lineage.server.datatables.storage.FurnitureSpawnStorage;
import com.lineage.server.model.Instance.L1FurnitureInstance;

/**
 * 家具資料
 * 
 * @author dexc
 */
public class FurnitureSpawnReading {

	private final Lock _lock;

	private final FurnitureSpawnStorage _storage;

	private static FurnitureSpawnReading _instance;

	private FurnitureSpawnReading() {
		_lock = new ReentrantLock(true);
		_storage = new FurnitureSpawnTable();
	}

	public static FurnitureSpawnReading get() {
		if (_instance == null) {
			_instance = new FurnitureSpawnReading();
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
	 * 增加家具
	 * 
	 * @param furniture
	 */
	public void insertFurniture(final L1FurnitureInstance furniture) {
		_lock.lock();
		try {
			_storage.insertFurniture(furniture);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除家具
	 * 
	 * @param furniture
	 */
	public void deleteFurniture(final L1FurnitureInstance furniture) {
		_lock.lock();
		try {
			_storage.deleteFurniture(furniture);
		} finally {
			_lock.unlock();
		}
	}

}
