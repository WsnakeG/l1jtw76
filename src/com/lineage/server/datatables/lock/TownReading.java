package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.TownTable;
import com.lineage.server.datatables.storage.TownStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Town;

/**
 * 村莊資料
 * 
 * @author dexc
 */
public class TownReading {

	private final Lock _lock;

	private final TownStorage _storage;

	private static TownReading _instance;

	private TownReading() {
		_lock = new ReentrantLock(true);
		_storage = new TownTable();
	}

	public static TownReading get() {
		if (_instance == null) {
			_instance = new TownReading();
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
	 * 傳回村莊陣列資料
	 * 
	 * @return
	 */
	public L1Town[] getTownTableList() {
		_lock.lock();
		L1Town[] tmp;
		try {
			tmp = _storage.getTownTableList();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定村莊資料
	 * 
	 * @param id
	 * @return
	 */
	public L1Town getTownTable(final int id) {
		_lock.lock();
		L1Town tmp;
		try {
			tmp = _storage.getTownTable(id);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 檢查是否為村長
	 * 
	 * @param pc
	 * @param town_id
	 * @return
	 */
	public boolean isLeader(final L1PcInstance pc, final int town_id) {
		_lock.lock();
		boolean tmp;
		try {
			tmp = _storage.isLeader(pc, town_id);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新村莊收入
	 * 
	 * @param town_id
	 * @param salesMoney
	 */
	public void addSalesMoney(final int town_id, final int salesMoney) {
		_lock.lock();
		try {
			_storage.addSalesMoney(town_id, salesMoney);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新村莊稅率
	 */
	public void updateTaxRate() {
		_lock.lock();
		try {
			_storage.updateTaxRate();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新收入資訊
	 */
	public void updateSalesMoneyYesterday() {
		_lock.lock();
		try {
			_storage.updateSalesMoneyYesterday();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * @param townId
	 * @return
	 */
	public String totalContribution(final int townId) {
		_lock.lock();
		String tmp;
		try {
			tmp = _storage.totalContribution(townId);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 *
	 */
	public void clearHomeTownID() {
		_lock.lock();
		try {
			_storage.clearHomeTownID();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 領取報酬
	 * 
	 * @return 報酬
	 */
	public int getPay(final int objid) {
		_lock.lock();
		final int tmp = 0;
		try {
			_storage.getPay(objid);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
