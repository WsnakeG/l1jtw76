package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ClanEmblemTable;
import com.lineage.server.datatables.storage.ClanEmblemStorage;
import com.lineage.server.templates.L1EmblemIcon;

/**
 * 盟輝圖檔紀錄
 * 
 * @author dexc
 */
public class ClanEmblemReading {

	private final Lock _lock;

	private final ClanEmblemStorage _storage;

	private static ClanEmblemReading _instance;

	private ClanEmblemReading() {
		_lock = new ReentrantLock(true);
		_storage = new ClanEmblemTable();
	}

	public static ClanEmblemReading get() {
		if (_instance == null) {
			_instance = new ClanEmblemReading();
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
	 * 傳回 Clan Icon
	 */
	public L1EmblemIcon get(final int clan_id) {
		_lock.lock();
		L1EmblemIcon tmp;
		try {
			tmp = _storage.get(clan_id);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加虛擬血盟盟輝
	 * 
	 * @param clan_id
	 * @param icon
	 */
	public void add(final int clan_id, final byte[] icon) {
		_lock.lock();
		try {
			_storage.add(clan_id, icon);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除盟輝資料
	 * 
	 * @param clan_id
	 */
	public void deleteIcon(final int clan_id) {
		_lock.lock();
		try {
			_storage.deleteIcon(clan_id);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 新建 ICON
	 */
	public L1EmblemIcon storeClanIcon(final int clan_id, final byte[] emblemicon) {
		_lock.lock();
		L1EmblemIcon tmp;
		try {
			tmp = _storage.storeClanIcon(clan_id, emblemicon);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新 ICON
	 */
	public void updateClanIcon(final L1EmblemIcon emblemIcon) {
		_lock.lock();
		try {
			_storage.updateClanIcon(emblemIcon);

		} finally {
			_lock.unlock();
		}
	}
}
