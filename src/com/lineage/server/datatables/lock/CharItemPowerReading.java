package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharItemsPowerTable;
import com.lineage.server.datatables.storage.CharItemsPowerStorage;
import com.lineage.server.templates.L1ItemPower_name;

/**
 * 人物古文字物品資料
 * 
 * @author dexc
 */
public class CharItemPowerReading {

	private final Lock _lock;

	private final CharItemsPowerStorage _storage;

	private static CharItemPowerReading _instance;

	private CharItemPowerReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharItemsPowerTable();
	}

	public static CharItemPowerReading get() {
		if (_instance == null) {
			_instance = new CharItemPowerReading();
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
	 * 增加古文字物品資料
	 * 
	 * @param objId
	 * @param power
	 */
	public void storeItem(final int objId, final L1ItemPower_name power) {
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
	public void updateItem(final int item_obj_id, final L1ItemPower_name power) {
		_lock.lock();
		try {
			_storage.updateItem(item_obj_id, power);

		} catch (final Exception e) {
			e.printStackTrace();

		} finally {
			_lock.unlock();
		}
	}
}
