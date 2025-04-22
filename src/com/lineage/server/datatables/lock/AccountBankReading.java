package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.AccountBankTable;
import com.lineage.server.datatables.storage.AccountBankStorage;
import com.lineage.server.templates.L1Bank;

/**
 * 銀行帳戶資料
 * 
 * @author loli
 */
public class AccountBankReading {

	private final Lock _lock;

	private final AccountBankStorage _storage;

	private static AccountBankReading _instance;

	private AccountBankReading() {
		_lock = new ReentrantLock(true);
		_storage = new AccountBankTable();
	}

	public static AccountBankReading get() {
		if (_instance == null) {
			_instance = new AccountBankReading();
		}
		return _instance;
	}

	/**
	 * 預先加載
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
	 * 該帳戶資料
	 * 
	 * @param account_name
	 * @return
	 */
	public L1Bank get(final String account_name) {
		_lock.lock();
		L1Bank tmp = null;
		try {
			tmp = _storage.get(account_name);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	public Map<String, L1Bank> map() {
		_lock.lock();
		Map<String, L1Bank> tmp = null;
		try {
			tmp = _storage.map();

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 建立帳號資料
	 * 
	 * @param loginName
	 * @param bank
	 */
	public void create(final String loginName, final L1Bank bank) {
		_lock.lock();
		try {
			_storage.create(loginName, bank);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新密碼
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	public void updatePass(final String loginName, final String pwd) {
		_lock.lock();
		try {
			_storage.updatePass(loginName, pwd);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新存款
	 * 
	 * @param loginName 帳號
	 * @param adena 金額
	 */
	public void updateAdena(final String loginName, final long adena) {
		_lock.lock();
		try {
			_storage.updateAdena(loginName, adena);

		} finally {
			_lock.unlock();
		}
	}
}
