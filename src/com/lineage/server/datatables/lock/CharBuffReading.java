package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharBuffTable;
import com.lineage.server.datatables.storage.CharBuffStorage;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 保留技能紀錄
 * 
 * @author dexc
 */
public class CharBuffReading {

	private final Lock _lock;

	private final CharBuffStorage _storage;

	private static CharBuffReading _instance;

	private CharBuffReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharBuffTable();
	}

	public static CharBuffReading get() {
		if (_instance == null) {
			_instance = new CharBuffReading();
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
	 * 增加保留技能紀錄
	 * 
	 * @param pc
	 */
	public void saveBuff(final L1PcInstance pc) {
		_lock.lock();
		try {
			_storage.saveBuff(pc);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 取回保留技能紀錄
	 * 
	 * @param pc
	 */
	public void buff(final L1PcInstance pc) {
		_lock.lock();
		try {
			_storage.buff(pc);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除全部保留技能紀錄
	 * 
	 * @param pc
	 */
	public void deleteBuff(final L1PcInstance pc) {
		_lock.lock();
		try {
			_storage.deleteBuff(pc);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除全部保留技能紀錄
	 * 
	 * @param objid
	 */
	public void deleteBuff(final int objid) {
		_lock.lock();
		try {
			_storage.deleteBuff(objid);
		} finally {
			_lock.unlock();
		}
	}
}
