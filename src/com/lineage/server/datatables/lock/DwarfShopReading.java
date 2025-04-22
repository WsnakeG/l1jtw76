package com.lineage.server.datatables.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.DwarfShopTable;
import com.lineage.server.datatables.storage.DwarfShopStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ShopS;

/**
 * 託售物件數據
 * 
 * @author dexc
 */
public class DwarfShopReading {

	private final Lock _lock;

	private final DwarfShopStorage _storage;

	private static DwarfShopReading _instance;

	private DwarfShopReading() {
		_lock = new ReentrantLock(true);
		_storage = new DwarfShopTable();
	}

	public static DwarfShopReading get() {
		if (_instance == null) {
			_instance = new DwarfShopReading();
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

	public int nextId() {
		_lock.lock();
		int tmp = 1;
		try {
			tmp += _storage.get_id();
			_storage.set_id(tmp);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回全部出售中物件資料數據
	 * 
	 * @return
	 */
	public HashMap<Integer, L1ShopS> allShopS() {
		_lock.lock();
		HashMap<Integer, L1ShopS> tmp = null;
		try {
			tmp = _storage.allShopS();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回全部託售物件數據
	 * 
	 * @return
	 */
	public Map<Integer, L1ItemInstance> allItems() {
		_lock.lock();
		Map<Integer, L1ItemInstance> tmp = null;
		try {
			tmp = _storage.allItems();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定託售物件數據
	 * 
	 * @param objid 物品OBJID
	 * @return
	 */
	public L1ShopS getShopS(final int objid) {
		_lock.lock();
		L1ShopS tmp = null;
		try {
			tmp = _storage.getShopS(objid);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 指定人物託售紀錄
	 * 
	 * @param pcobjid
	 * @return
	 */
	public HashMap<Integer, L1ShopS> getShopSMap(final int pcobjid) {
		_lock.lock();
		HashMap<Integer, L1ShopS> tmp = null;
		try {
			tmp = _storage.getShopSMap(pcobjid);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 加入託售物件數據
	 * 
	 * @param key
	 * @param item
	 * @param shopS
	 */
	public void insertItem(final int key, final L1ItemInstance item, final L1ShopS shopS) {
		_lock.lock();
		try {
			_storage.insertItem(key, item, shopS);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 資料更新(託售狀態)
	 * 
	 * @param item
	 */
	public void updateShopS(final L1ShopS shopS) {
		_lock.lock();
		try {
			_storage.updateShopS(shopS);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 託售物件資料刪除
	 * 
	 * @param key
	 */
	public void deleteItem(final int key) {
		_lock.lock();
		try {
			_storage.deleteItem(key);

		} finally {
			_lock.unlock();
		}
	}
}
