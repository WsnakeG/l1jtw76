package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.GamblingTable;
import com.lineage.server.datatables.storage.GamblingStorage;
import com.lineage.server.templates.L1Gambling;

/**
 * 賭場紀錄
 * 
 * @author dexc
 */
public class GamblingReading {

	private final Lock _lock;

	private final GamblingStorage _storage;

	private static GamblingReading _instance;

	private GamblingReading() {
		_lock = new ReentrantLock(true);
		_storage = new GamblingTable();
	}

	public static GamblingReading get() {
		if (_instance == null) {
			_instance = new GamblingReading();
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
	 * 傳回賭場紀錄(獲勝NPC票號)
	 * 
	 * @return
	 */
	public L1Gambling getGambling(final String key) {
		_lock.lock();
		L1Gambling tmp;
		try {
			tmp = _storage.getGambling(key);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回賭場紀錄(場次編號)
	 * 
	 * @return
	 */
	public L1Gambling getGambling(final int key) {
		_lock.lock();
		L1Gambling tmp;
		try {
			tmp = _storage.getGambling(key);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加賭場紀錄
	 */
	public void add(final L1Gambling gambling) {
		_lock.lock();
		try {
			_storage.add(gambling);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新賭場紀錄
	 */
	public void updateGambling(final int id, final int outcount) {
		_lock.lock();
		try {
			_storage.updateGambling(id, outcount);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 傳回場次數量 與獲勝次數
	 * 
	 * @param npcid
	 * @return
	 */
	public int[] winCount(final int npcid) {
		_lock.lock();
		int[] tmp;
		try {
			tmp = _storage.winCount(npcid);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 已用最大ID
	 * 
	 * @return
	 */
	public int maxId() {
		_lock.lock();
		int tmp;
		try {
			tmp = _storage.maxId();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
