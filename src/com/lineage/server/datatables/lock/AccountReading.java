package com.lineage.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.AccountTable;
import com.lineage.server.datatables.storage.AccountStorage;
import com.lineage.server.templates.L1Account;

/**
 * 人物帳戶資料
 * 
 * @author dexc
 */
public class AccountReading {

	private final Lock _lock;

	private final AccountStorage _storage;

	private static AccountReading _instance;

	private AccountReading() {
		_lock = new ReentrantLock(true);
		_storage = new AccountTable();
	}

	public static AccountReading get() {
		if (_instance == null) {
			_instance = new AccountReading();
		}
		return _instance;
	}

	/**
	 * 預先加載帳戶名稱
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
	 * 傳回指定帳戶名稱資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 * @return true:有該帳號 false:沒有該帳號
	 */
	public boolean isAccountUT(final String loginName) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.isAccountUT(loginName);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 建立帳號資料
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 * @param ip IP位置
	 * @param host MAC位置
	 * @param spwd 超級密碼
	 * @return L1Account
	 */
	public L1Account create(final String loginName, final String pwd, final String ip, final String host,
			final String spwd) {
		_lock.lock();
		L1Account tmp = null;
		try {
			tmp = _storage.create(loginName, pwd, ip, host, spwd);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定帳戶資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 * @return true:有該帳號 false:沒有該帳號
	 */
	public boolean isAccount(final String loginName) {
		_lock.lock();
		boolean tmp = false;
		try {
			tmp = _storage.isAccount(loginName);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定帳戶資料
	 * 
	 * @param loginName 帳號
	 * @return L1Account
	 */
	public L1Account getAccount(final String loginName) {
		_lock.lock();
		L1Account tmp = null;
		try {
			tmp = _storage.getAccount(loginName);

		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 更新倉庫密碼
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	public void updateWarehouse(final String loginName, final int pwd) {
		_lock.lock();
		try {
			_storage.updateWarehouse(loginName, pwd);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新上線時間/IP/MAC
	 * 
	 * @param account 帳戶
	 */
	public void updateLastActive(final L1Account account) {
		_lock.lock();
		try {
			_storage.updateLastActive(account);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新帳號可用人物數量
	 * 
	 * @param loginName 帳號
	 * @param count 擴充數量
	 */
	public void updateCharacterSlot(final String loginName, final int count) {
		_lock.lock();
		try {
			_storage.updateCharacterSlot(loginName, count);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新帳號密碼
	 * 
	 * @param loginName 帳號
	 * @param newpwd 新密碼
	 */
	public void updatePwd(final String loginName, final String newpwd) {
		_lock.lock();
		try {
			_storage.updatePwd(loginName, newpwd);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新帳號連線狀態
	 * 
	 * @param loginName 帳號
	 * @param islan 是否連線
	 */
	public void updateLan(final String loginName, final boolean islan) {
		_lock.lock();
		try {
			_storage.updateLan(loginName, islan);

		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新帳號連線狀態(全部離線)
	 */
	public void updateLan() {
		_lock.lock();
		try {
			_storage.updateLan();

		} finally {
			_lock.unlock();
		}
	}

	public int getPoints(final String loginName) {
		_lock.lock();
		int points = 0;
		try {
			points = _storage.getPoint(loginName);

		} finally {
			_lock.unlock();
		}
		return points;
	}

	public void setPoints(final String loginName, final int point) {
		_lock.lock();
		try {
			_storage.setPoint(loginName, point);

		} finally {
			_lock.unlock();
		}
	}
}
