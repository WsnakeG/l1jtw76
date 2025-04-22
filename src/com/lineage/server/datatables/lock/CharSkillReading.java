package com.lineage.server.datatables.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.CharSkillTable;
import com.lineage.server.datatables.storage.CharSkillStorage;
import com.lineage.server.templates.L1UserSkillTmp;

/**
 * 人物技能紀錄
 * 
 * @author dexc
 */
public class CharSkillReading {

	private final Lock _lock;

	private final CharSkillStorage _storage;

	private static CharSkillReading _instance;

	private CharSkillReading() {
		_lock = new ReentrantLock(true);
		_storage = new CharSkillTable();
	}

	public static CharSkillReading get() {
		if (_instance == null) {
			_instance = new CharSkillReading();
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
	 * 取回該人物技能列表
	 * 
	 * @param pc
	 * @return
	 */
	public ArrayList<L1UserSkillTmp> skills(final int playerobjid) {
		_lock.lock();
		ArrayList<L1UserSkillTmp> tmp;
		try {
			tmp = _storage.skills(playerobjid);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加技能
	 */
	public void spellMastery(final int playerobjid, final int skillid, final String skillname,
			final int active, final int time) {
		_lock.lock();
		try {
			if (skillname.equals("none")) {
				return;
			}
			_storage.spellMastery(playerobjid, skillid, skillname, active, time);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除技能
	 */
	public void spellLost(final int playerobjid, final int skillid) {
		_lock.lock();
		try {
			_storage.spellLost(playerobjid, skillid);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 檢查技能是否重複
	 */
	public boolean spellCheck(final int playerobjid, final int skillid) {
		_lock.lock();
		boolean tmp;
		try {
			tmp = _storage.spellCheck(playerobjid, skillid);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 設置自動技能狀態
	 */
	public void setAuto(final int mode, final int objid, final int skillid) {
		_lock.lock();
		try {
			_storage.setAuto(mode, objid, skillid);
		} finally {
			_lock.unlock();
		}
	}
}
