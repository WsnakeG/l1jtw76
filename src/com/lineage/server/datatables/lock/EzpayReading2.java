package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.EzpayTable2;
import com.lineage.server.datatables.storage.EzpayStorage;

/**
 * 網站購物資料
 * 
 * @author dexc
 */
public class EzpayReading2 {

	private final Lock _lock;

	private final EzpayStorage _storage;

	private static EzpayReading2 _instance;

	private EzpayReading2() {
		_lock = new ReentrantLock(true);
		_storage = new EzpayTable2();
	}

	public static EzpayReading2 get() {
		if (_instance == null) {
			_instance = new EzpayReading2();
		}
		return _instance;
	}

	/**
	 * 傳回指定帳戶匯款資料
	 * 
	 * @param loginName 帳號名稱
	 * @return
	 */
	public Map<Integer, int[]> ezpayInfo(final String loginName) {
		_lock.lock();
		Map<Integer, int[]> tmp = null;
		try {
			tmp = _storage.ezpayInfo(loginName);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定帳戶匯款資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id 流水號
	 * @return
	 */
	public int[] ezpayInfo(final String loginName, final int id) {
		_lock.lock();
		int[] tmp = null;
		try {
			tmp = _storage.ezpayInfo(loginName, id);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新資料
	 * 
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 */
	public boolean update(final String loginName, final int id, final String pcname, final String ip) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.update(loginName, id, pcname, ip);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
