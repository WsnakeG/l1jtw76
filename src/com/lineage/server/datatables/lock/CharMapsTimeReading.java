package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharMapsTimeTable;
import com.lineage.server.datatables.storage.CharMapsTimeStorage;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 保留地圖入場時間紀錄
 * 
 * @author terry0412
 */
public class CharMapsTimeReading {

	private final Lock _lock;

	private final CharMapsTimeStorage _storage;

	private static CharMapsTimeReading _instance;

	private CharMapsTimeReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharMapsTimeTable();
	}

	public static CharMapsTimeReading get() {
		if (_instance == null) {
			_instance = new CharMapsTimeReading();
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
	 * 新增地圖入場時間紀錄
	 * 
	 * @param objId
	 * @param order_id
	 * @param used_time
	 * @return
	 */
	public Map<Integer, Integer> addTime(final int objId, final int order_id, final int used_time) {
		_lock.lock();
		Map<Integer, Integer> tmp;
		try {
			tmp = _storage.addTime(objId, order_id, used_time);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 取回地圖入場時間紀錄
	 * 
	 * @param pc
	 */
	public void getTime(final L1PcInstance pc) {
		_lock.lock();
		try {
			_storage.getTime(pc);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除地圖入場時間紀錄
	 * 
	 * @param objid
	 */
	public void deleteTime(final int objid) {
		_lock.lock();
		try {
			_storage.deleteTime(objid);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除並儲存全部地圖入場時間紀錄
	 */
	public void saveAllTime() {
		_lock.lock();
		try {
			_storage.saveAllTime();

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 清除全部地圖入場時間紀錄 (重置用)
	 */
	public void clearAllTime() {
		_lock.lock();
		try {
			_storage.clearAllTime();

		} finally {
			_lock.unlock();
		}
	}
}
