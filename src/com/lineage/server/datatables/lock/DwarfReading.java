package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.DwarfTable;
import com.lineage.server.datatables.storage.DwarfStorage;
import com.lineage.server.model.Instance.L1ItemInstance;

/**
 * 倉庫數據
 * 
 * @author dexc
 */
public class DwarfReading {

	private final Lock _lock;

	private final DwarfStorage _storage;

	private static DwarfReading _instance;

	private DwarfReading() {
		_lock = new ReentrantLock(true);
		_storage = new DwarfTable();
	}

	public static DwarfReading get() {
		if (_instance == null) {
			_instance = new DwarfReading();
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
	 * 傳回全部倉庫數據
	 * 
	 * @return
	 */
	public Map<String, CopyOnWriteArrayList<L1ItemInstance>> allItems() {
		_lock.lock();
		Map<String, CopyOnWriteArrayList<L1ItemInstance>> tmp = null;
		try {
			tmp = _storage.allItems();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回倉庫數據
	 * 
	 * @return
	 */
	public CopyOnWriteArrayList<L1ItemInstance> loadItems(final String account_name) {
		_lock.lock();
		CopyOnWriteArrayList<L1ItemInstance> tmp = null;
		try {
			tmp = _storage.loadItems(account_name);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 刪除倉庫資料(完整)
	 * 
	 * @param account_name
	 */
	public void delUserItems(final String account_name) {
		_lock.lock();
		try {
			_storage.delUserItems(account_name);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 加入倉庫數據
	 */
	public void insertItem(final String account_name, final L1ItemInstance item) {
		_lock.lock();
		try {
			_storage.insertItem(account_name, item);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 倉庫資料更新(物品數量)
	 * 
	 * @param item
	 */
	public void updateItem(final L1ItemInstance item) {
		_lock.lock();
		try {
			_storage.updateItem(item);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 倉庫物品資料刪除
	 * 
	 * @param account_name
	 * @param item
	 */
	public void deleteItem(final String account_name, final L1ItemInstance item) {
		_lock.lock();
		try {
			_storage.deleteItem(account_name, item);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 該倉庫是否有指定數據
	 * 
	 * @param account_name
	 * @param objid
	 * @param count
	 * @return
	 */
	public boolean getUserItems(final String account_name, final int objid, final int count) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.getUserItems(account_name, objid, count);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
