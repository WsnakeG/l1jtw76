package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ClanTable;
import com.lineage.server.datatables.storage.ClanStorage;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 血盟資料
 * 
 * @author dexc
 */
public class ClanReading {

	private final Lock _lock;

	private final ClanStorage _storage;

	private static ClanReading _instance;

	private ClanReading() {
		_lock = new ReentrantLock(true);
		_storage = new ClanTable();
	}

	public static ClanReading get() {
		if (_instance == null) {
			_instance = new ClanReading();
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
	 * 加入虛擬血盟
	 * 
	 * @param integer
	 * @param l1Clan
	 */
	public void addDeClan(final Integer integer, final L1Clan clan) {
		_lock.lock();
		try {
			_storage.addDeClan(integer, clan);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 建立血盟資料
	 * 
	 * @param player
	 * @param clan_name
	 * @return
	 */
	public L1Clan createClan(final L1PcInstance player, final String clan_name) {
		_lock.lock();
		L1Clan tmp = null;
		try {
			tmp = _storage.createClan(player, clan_name);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新血盟資料
	 * 
	 * @param clan
	 */
	public void updateClan(final L1Clan clan) {
		_lock.lock();
		try {
			_storage.updateClan(clan);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除血盟資料
	 * 
	 * @param clan_name
	 */
	public void deleteClan(final String clan_name) {
		_lock.lock();
		try {
			_storage.deleteClan(clan_name);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 指定血盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	public L1Clan getTemplate(final int clan_id) {
		_lock.lock();
		L1Clan tmp = null;
		try {
			tmp = _storage.getTemplate(clan_id);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 全部血盟資料
	 * 
	 * @return
	 */
	public Map<Integer, L1Clan> get_clans() {
		_lock.lock();
		Map<Integer, L1Clan> tmp = null;
		try {
			tmp = _storage.get_clans();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
