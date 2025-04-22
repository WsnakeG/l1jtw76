package com.lineage.server.datatables.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ClanAllianceTable;
import com.lineage.server.datatables.storage.ClanAllianceStorage;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Clan;

/**
 * 血盟同盟紀錄
 * 
 * @author terry0412
 */
public class ClanAllianceReading {

	private final Lock _lock;

	private final ClanAllianceStorage _storage;

	private static ClanAllianceReading _instance;

	private ClanAllianceReading() {
		_lock = new ReentrantLock(true);
		_storage = new ClanAllianceTable();
	}

	public static ClanAllianceReading get() {
		if (_instance == null) {
			_instance = new ClanAllianceReading();
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
	 * 建立資料
	 * 
	 * @param alliance
	 */
	public void insertAlliance(final L1Alliance alliance) {
		_lock.lock();
		try {
			_storage.insertAlliance(alliance);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新資料
	 * 
	 * @param order_id
	 * @param totalList
	 */
	public void updateAlliance(final int order_id, final ArrayList<L1Clan> totalList) {
		_lock.lock();
		try {
			_storage.updateAlliance(order_id, totalList);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除資料
	 * 
	 * @param order_id
	 */
	public void deleteAlliance(final int order_id) {
		_lock.lock();
		try {
			_storage.deleteAlliance(order_id);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 取得指定同盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	public L1Alliance getAlliance(final int clan_id) {
		_lock.lock();
		L1Alliance tmp;
		try {
			tmp = _storage.getAlliance(clan_id);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
