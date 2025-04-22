package com.lineage.server.datatables.lock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.SpawnBossTable;
import com.lineage.server.datatables.storage.SpawnBossStorage;
import com.lineage.server.model.L1Spawn;

/**
 * BOSS召喚資料
 * 
 * @author dexc
 */
public class SpawnBossReading {

	private final Lock _lock;

	private final SpawnBossStorage _storage;
	
	private static SpawnBossReading _instance;
	
	private final List<Integer> _bossreid = new ArrayList<Integer>();
	
	private SpawnBossReading() {
		_lock = new ReentrantLock(true);
		_storage = new SpawnBossTable();
	}

	public static SpawnBossReading get() {
		if (_instance == null) {
			_instance = new SpawnBossReading();
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
	 * 更新資料庫 下次召喚時間紀錄
	 * 
	 * @param id
	 * @param spawnTime
	 */
	public void upDateNextSpawnTime(final int id, final Calendar spawnTime) {
		_lock.lock();
		try {
			_storage.upDateNextSpawnTime(id, spawnTime);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * BOSS召喚列表中物件
	 * 
	 * @param key
	 * @return
	 */
	public L1Spawn getTemplate(final int key) {
		_lock.lock();
		L1Spawn tmp = null;
		try {
			tmp = _storage.getTemplate(key);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * BOSS召喚列表中物件(NPCID)
	 * 
	 * @return _bossId
	 */
	public List<Integer> bossIds() {
		_lock.lock();
		List<Integer> tmp = null;
		try {
			tmp = _storage.bossIds();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
	

	public List<Integer> bossreid() {
	    return _bossreid;
	}
	
}
