package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.HouseTable;
import com.lineage.server.datatables.storage.HouseStorage;
import com.lineage.server.templates.L1House;

/**
 * 盟屋資料
 * 
 * @author dexc
 */
public class HouseReading {

	private final Lock _lock;

	private final HouseStorage _storage;

	private static HouseReading _instance;

	private HouseReading() {
		_lock = new ReentrantLock(true);
		_storage = new HouseTable();
	}

	public static HouseReading get() {
		if (_instance == null) {
			_instance = new HouseReading();
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
	 * 傳回小屋列表
	 * 
	 * @return
	 */
	public Map<Integer, L1House> getHouseTableList() {
		_lock.lock();
		Map<Integer, L1House> tmp;
		try {
			tmp = _storage.getHouseTableList();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定小屋資料
	 * 
	 * @param houseId
	 * @return
	 */
	public L1House getHouseTable(final int houseId) {
		_lock.lock();
		L1House tmp;
		try {
			tmp = _storage.getHouseTable(houseId);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新小屋資料
	 * 
	 * @param house
	 */
	public void updateHouse(final L1House house) {
		_lock.lock();
		try {
			_storage.updateHouse(house);
		} finally {
			_lock.unlock();
		}
	}
}
