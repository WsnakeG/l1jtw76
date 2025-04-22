package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharItemsPowerHoleTable;
import com.lineage.server.datatables.storage.CharItemsPowerHoleStorage;
import com.lineage.server.templates.L1ItemPowerHole_name;

/**
 * 人物物品凹槽資料
 * 
 * @author dexc
 */
public class CharItemPowerHoleReading {

	private final Lock _lock;

	private final CharItemsPowerHoleStorage _storage;

	private static CharItemPowerHoleReading _instance;

	private CharItemPowerHoleReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharItemsPowerHoleTable();
	}

	public static CharItemPowerHoleReading get() {
		if (_instance == null) {
			_instance = new CharItemPowerHoleReading();
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
	 * 增加物品凹槽資料
	 * 
	 * @param objId
	 * @param power
	 */
	public void storeItem(final int objId, final L1ItemPowerHole_name power) {
		_lock.lock();
		try {
			_storage.storeItem(objId, power);

		} catch (final Exception e) {
			e.printStackTrace();

		} finally {
			_lock.unlock();
		}
	}

	public void delItem(final int objId) {
		_lock.lock();
		try {
			_storage.delItem(objId);

		} catch (final Exception e) {
			e.printStackTrace();

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新凹槽資料
	 * 
	 * @param item_obj_id
	 * @param power
	 */
	public void updateItem(final int item_obj_id, final L1ItemPowerHole_name power) {
		_lock.lock();
		try {
			_storage.updateItem(item_obj_id, power);

		} finally {
			_lock.unlock();
		}
	}
}
