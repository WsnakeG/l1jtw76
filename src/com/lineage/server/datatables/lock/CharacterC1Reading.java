package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharacterC1Table;
import com.lineage.server.datatables.storage.CharacterC1Storage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1User_Power;

/**
 * 人物陣營紀錄
 * 
 * @author dexc
 */
public class CharacterC1Reading {

	private final Lock _lock;

	private final CharacterC1Storage _storage;

	private static CharacterC1Reading _instance;

	private CharacterC1Reading() {
		_lock = new ReentrantLock(true);
		_storage = new CharacterC1Table();
	}

	public static CharacterC1Reading get() {
		if (_instance == null) {
			_instance = new CharacterC1Reading();
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
	 * 傳回 L1User_Power
	 */
	public L1User_Power get(final int objectId) {
		_lock.lock();
		L1User_Power tmp;
		try {
			tmp = _storage.get(objectId);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 新建 L1User_Power
	 */
	public void storeCharacterC1(final L1PcInstance pc) {
		_lock.lock();
		try {
			_storage.storeCharacterC1(pc);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新 L1User_Power
	 */
	public void updateCharacterC1(final int object_id, final int c1_type, final String note) {
		_lock.lock();
		try {
			_storage.updateCharacterC1(object_id, c1_type, note);

		} finally {
			_lock.unlock();
		}
	}
}
