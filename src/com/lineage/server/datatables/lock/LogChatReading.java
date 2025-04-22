package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.LogChatTable;
import com.lineage.server.datatables.storage.LogChatStorage;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 對話紀錄
 * 
 * @author dexc
 */
public class LogChatReading {

	private final Lock _lock;

	private final LogChatStorage _storage;

	private static LogChatReading _instance;

	private LogChatReading() {
		_lock = new ReentrantLock(true);
		_storage = new LogChatTable();
	}

	public static LogChatReading get() {
		if (_instance == null) {
			_instance = new LogChatReading();
		}
		return _instance;
	}

	/**
	 * 具有傳送對象
	 * 
	 * @param pc
	 * @param target
	 * @param text
	 * @param type
	 */
	public void isTarget(final L1PcInstance pc, final L1PcInstance target, final String text,
			final int type) {
		_lock.lock();
		try {
			_storage.isTarget(pc, target, text, type);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 無傳送對象
	 * 
	 * @param pc
	 * @param text
	 * @param type
	 */
	public void noTarget(final L1PcInstance pc, final String text, final int type) {
		_lock.lock();
		try {
			_storage.noTarget(pc, text, type);

		} finally {
			_lock.unlock();
		}
	}

}