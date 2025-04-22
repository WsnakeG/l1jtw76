package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharOtherTable;
import com.lineage.server.datatables.storage.CharOtherStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther;

/**
 * 額外紀錄資料
 * 
 * @author dexc
 */
public class CharOtherReading {

	private final Lock _lock;

	private final CharOtherStorage _storage;

	private static CharOtherReading _instance;

	private CharOtherReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharOtherTable();
	}

	public static CharOtherReading get() {
		if (_instance == null) {
			_instance = new CharOtherReading();
		}
		return _instance;
	}

	public void load() {
		_lock.lock();
		try {
			_storage.load();

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 取回保留額外紀錄
	 * 
	 * @param pc
	 */
	public L1PcOther getOther(final L1PcInstance pc) {
		_lock.lock();
		L1PcOther tmp;
		try {
			tmp = _storage.getOther(pc);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加 或更新保留額外紀錄
	 * 
	 * @param objId
	 * @param other
	 */
	public void storeOther(final int objId, final L1PcOther other) {
		_lock.lock();
		try {
			_storage.storeOther(objId, other);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 歸0殺人次數
	 */
	public void tam() {
		_lock.lock();
		try {
			_storage.tam();

		} finally {
			_lock.unlock();
		}
	}
}
