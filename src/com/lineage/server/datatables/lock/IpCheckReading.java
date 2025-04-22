package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.IpCheckTable;
import com.lineage.server.datatables.storage.IpCheckStorage;

/**
 * IP驗證庫
 * 
 * @author dexc
 */
public class IpCheckReading {

	private final Lock _lock;

	private final IpCheckStorage _storage;

	private static IpCheckReading _instance;

	private IpCheckReading() {
		_lock = new ReentrantLock(true);
		_storage = new IpCheckTable();
	}

	public static IpCheckReading get() {
		if (_instance == null) {
			_instance = new IpCheckReading();
		}
		return _instance;
	}

	public boolean check(final String ipaddr) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.check(ipaddr);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
