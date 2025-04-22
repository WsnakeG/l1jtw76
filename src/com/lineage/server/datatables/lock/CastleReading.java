package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CastleTable;
import com.lineage.server.datatables.storage.CastleStorage;
import com.lineage.server.templates.L1Castle;

/**
 * 城堡資料
 * 
 * @author dexc
 */
public class CastleReading {

	private final Lock _lock;

	private final CastleStorage _storage;

	private static CastleReading _instance;

	private CastleReading() {
		_lock = new ReentrantLock(true);
		_storage = new CastleTable();
	}

	public static CastleReading get() {
		if (_instance == null) {
			_instance = new CastleReading();
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
	 * 城堡MAP
	 * 
	 * @return
	 */
	public Map<Integer, L1Castle> getCastleMap() {
		_lock.lock();
		Map<Integer, L1Castle> tmp;
		try {
			tmp = _storage.getCastleMap();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 城堡陣列
	 * 
	 * @return
	 */
	public L1Castle[] getCastleTableList() {
		_lock.lock();
		L1Castle[] tmp;
		try {
			tmp = _storage.getCastleTableList();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 指定城堡資料
	 * 
	 * @param id
	 * @return
	 */
	public L1Castle getCastleTable(final int id) {
		_lock.lock();
		L1Castle tmp;
		try {
			tmp = _storage.getCastleTable(id);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新城堡指定資料
	 * 
	 * @param castle
	 */
	public void updateCastle(final L1Castle castle) {
		_lock.lock();
		try {
			_storage.updateCastle(castle);
		} finally {
			_lock.unlock();
		}
	}
}
