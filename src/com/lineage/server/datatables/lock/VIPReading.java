package com.lineage.server.datatables.lock;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.VIPTable;
import com.lineage.server.datatables.storage.VIPStorage;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * VIP系統紀錄資料
 * 
 * @author dexc
 */
public class VIPReading {

	private final Lock _lock;

	private final VIPStorage _storage;

	private static VIPReading _instance;

	private VIPReading() {
		_lock = new ReentrantLock(true);
		_storage = new VIPTable();
	}

	public static VIPReading get() {
		if (_instance == null) {
			_instance = new VIPReading();
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
	 * 全部VIP紀錄
	 * 
	 * @return
	 */
	public Map<Integer, Timestamp> map() {
		_lock.lock();
		Map<Integer, Timestamp> tmp;
		try {
			tmp = _storage.map();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * VIP系統紀錄
	 * 
	 * @param pc
	 */
	public Timestamp getOther(final L1PcInstance pc) {
		_lock.lock();
		Timestamp tmp;
		try {
			tmp = _storage.getOther(pc);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加/更新 VIP系統紀錄
	 * 
	 * @param key
	 * @param value
	 */
	public void storeOther(final int key, final Timestamp value) {
		_lock.lock();
		try {
			_storage.storeOther(key, value);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除VIP系統紀錄
	 * 
	 * @param key PC OBJID
	 */
	public void delOther(final int key) {
		_lock.lock();
		try {
			_storage.delOther(key);

		} finally {
			_lock.unlock();
		}
	}
}
