package com.lineage.server.datatables.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharBookTable;
import com.lineage.server.datatables.storage.CharBookStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1BookMark;

/**
 * 記憶座標紀錄資料
 * 
 * @author dexc
 */
public class CharBookReading {

	private final Lock _lock;

	private final CharBookStorage _storage;

	private static CharBookReading _instance;

	private CharBookReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharBookTable();
	}

	public static CharBookReading get() {
		if (_instance == null) {
			_instance = new CharBookReading();
		}
		return _instance;
	}

	public void load() {
		_lock.lock();
		try {
			_storage.load();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 取回保留記憶座標紀錄群
	 * 
	 * @param pc
	 */
	public ArrayList<L1BookMark> getBookMarks(final L1PcInstance pc) {
		_lock.lock();
		ArrayList<L1BookMark> tmp;
		try {
			tmp = _storage.getBookMarks(pc);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 取回保留記憶座標紀錄
	 * 
	 * @param pc
	 */
	public L1BookMark getBookMark(final L1PcInstance pc, final int i) {
		_lock.lock();
		L1BookMark tmp;
		try {
			tmp = _storage.getBookMark(pc, i);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 刪除記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	public void deleteBookmark(final L1PcInstance pc, final String s) {
		_lock.lock();
		try {
			_storage.deleteBookmark(pc, s);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 增加記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	public void addBookmark(final L1PcInstance pc, final String s) {
		_lock.lock();
		try {
			_storage.addBookmark(pc, s);
		} finally {
			_lock.unlock();
		}
	}
}
