package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ServerCnInfoTable;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Item;

/**
 * 貨幣購買紀錄
 */
public class ServerCnInfoReading {

	private final Lock _lock;

	private final ServerCnInfoTable _storage;

	private static ServerCnInfoReading _instance;

	private ServerCnInfoReading() {
		_lock = new ReentrantLock(true);
		_storage = new ServerCnInfoTable();
	}

	public static ServerCnInfoReading get() {
		if (_instance == null) {
			_instance = new ServerCnInfoReading();
		}
		return _instance;
	}

	/**
	 * 資料預先載入
	 */
	public void create(final L1PcInstance pc, final L1Item itemtmp, final long count) {
		_lock.lock();
		try {
			_storage.create(pc, itemtmp, count);

		} finally {
			_lock.unlock();
		}
	}
}
