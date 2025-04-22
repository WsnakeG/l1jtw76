package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.ClanRecommendTable;
import com.lineage.server.datatables.storage.ClanRecommendStorage;
import com.lineage.server.templates.L1ClanRecommend;

public class ClanRecommendReading {

	private final Lock _lock;

	private final ClanRecommendStorage _storage;

	private static ClanRecommendReading _instance;

	private ClanRecommendReading() {
		_lock = new ReentrantLock(true);
		_storage = new ClanRecommendTable();
	}

	public static ClanRecommendReading get() {
		if (_instance == null) {
			_instance = new ClanRecommendReading();
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

	public void insertRecommend(final int clan_id, final String clan_name, final String leader_name,
			final int type_id, final String type_message) {
		_lock.lock();
		try {
			_storage.insertRecommend(clan_id, clan_name, leader_name, type_id, type_message);

		} finally {
			_lock.unlock();
		}
	}

	public void insertRecommendApply(final int clan_id, final String clan_name, final int applicant_id,
			final String applicant_name) {
		_lock.lock();
		try {
			_storage.insertRecommendApply(clan_id, clan_name, applicant_id, applicant_name);

		} finally {
			_lock.unlock();
		}
	}

	public void updateRecommend(final int clan_id, final int type_id, final String type_message) {
		_lock.lock();
		try {
			_storage.updateRecommend(clan_id, type_id, type_message);

		} finally {
			_lock.unlock();
		}
	}

	public void deleteRecommend(final int clan_id) {
		_lock.lock();
		try {
			_storage.deleteRecommend(clan_id);

		} finally {
			_lock.unlock();
		}
	}

	public void deleteRecommendApply(final int chan_id, final int char_id) {
		_lock.lock();
		try {
			_storage.deleteRecommendApply(chan_id, char_id);

		} finally {
			_lock.unlock();
		}
	}

	public Map<Integer, L1ClanRecommend> getRecommendsList() {
		_lock.lock();
		Map<Integer, L1ClanRecommend> tmp;
		try {
			tmp = _storage.getRecommendsList();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public Map<Integer, CopyOnWriteArrayList<Integer>> getApplyList() {
		_lock.lock();
		Map<Integer, CopyOnWriteArrayList<Integer>> tmp;
		try {
			tmp = _storage.getApplyList();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}
}
