package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharacterConfigTable;
import com.lineage.server.datatables.storage.CharacterConfigStorage;
import com.lineage.server.templates.L1Config;

/**
 * 快速鍵紀錄
 * 
 * @author dexc
 */
public class CharacterConfigReading {

	private final Lock _lock;

	private final CharacterConfigStorage _storage;

	private static CharacterConfigReading _instance;

	private CharacterConfigReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharacterConfigTable();
	}

	public static CharacterConfigReading get() {
		if (_instance == null) {
			_instance = new CharacterConfigReading();
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
	 * 傳回 L1Config
	 */
	public L1Config get(final int objectId) {
		_lock.lock();
		L1Config tmp;
		try {
			tmp = _storage.get(objectId);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 新建 L1Config
	 */
	public void storeCharacterConfig(final int objectId, final int length, final byte[] data) {
		_lock.lock();
		try {
			_storage.storeCharacterConfig(objectId, length, data);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新 L1Config
	 */
	public void updateCharacterConfig(final int objectId, final int length, final byte[] data) {
		_lock.lock();
		try {
			_storage.updateCharacterConfig(objectId, length, data);
		} finally {
			_lock.unlock();
		}
	}
}
