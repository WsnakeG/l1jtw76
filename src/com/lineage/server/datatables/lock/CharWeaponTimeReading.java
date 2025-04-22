package com.lineage.server.datatables.lock;

import java.sql.Timestamp;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharWeaponTimeTable;
import com.lineage.server.datatables.storage.CharWeaponStorage;

/**
 * 人物背包物品使用期限資料
 * 
 * @author dexc
 */
public class CharWeaponTimeReading {

	private final Lock _lock;

	private final CharWeaponStorage _storage;

	private static CharWeaponTimeReading _instance;

	private CharWeaponTimeReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharWeaponTimeTable();
	}

	public static CharWeaponTimeReading get() {
		if (_instance == null) {
			_instance = new CharWeaponTimeReading();
		}
		return _instance;
	}

	/**
	 * 資料預先載入
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
	 * 增加物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	public void addTime(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon) {
		_lock.lock();
		try {
			_storage.addTime(itemr_obj_id, usertime, magic_weapon);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	public void updateTime(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon, final int steps, final int same) {
		_lock.lock();
		try {
			_storage.updateTime(itemr_obj_id, usertime, magic_weapon, steps, same);

		} finally {
			_lock.unlock();
		}
	}
}
