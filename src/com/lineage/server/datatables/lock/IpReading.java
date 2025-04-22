package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.IpTable;
import com.lineage.server.datatables.storage.IpStorage;

/**
 * 禁用ip資料
 * 
 * @author dexc
 */
public class IpReading {

	private final Lock _lock;

	private final IpStorage _storage;

	private static IpReading _instance;

	private IpReading() {
		_lock = new ReentrantLock(true);
		_storage = new IpTable();
	}

	public static IpReading get() {
		if (_instance == null) {
			_instance = new IpReading();
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
	 * 加入禁止位置
	 * 
	 * @param ip
	 * @param info 原因
	 */
	public void add(final String ip, final String info) {
		_lock.lock();
		try {
			_storage.add(ip, info);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 移出禁止位置
	 * 
	 * @param ip
	 * @return
	 */
	public void remove(final String ip) {
		_lock.lock();
		try {
			_storage.remove(ip);
		} finally {
			_lock.unlock();
		}
	}
}
