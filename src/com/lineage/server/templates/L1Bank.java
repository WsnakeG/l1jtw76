package com.lineage.server.templates;

import com.lineage.list.OnlineUser;

/**
 * 銀行帳戶資料
 * 
 * @author loli
 */
public class L1Bank {

	private String _account_name = null;

	private long _adena_count = 0;

	private String _pass = null;

	public String get_account_name() {
		return _account_name;
	}

	public void set_account_name(final String _account_name) {
		this._account_name = _account_name;
	}

	public long get_adena_count() {
		return _adena_count;
	}

	public void set_adena_count(final long _adena_count) {
		this._adena_count = _adena_count;
	}

	/**
	 * 取款密碼(0~9) 6位數
	 * 
	 * @return
	 */
	public String get_pass() {
		return _pass;
	}

	public void set_pass(final String _pass) {
		this._pass = _pass;
	}

	/**
	 * 該帳號是否連線
	 * 
	 * @return
	 */
	public boolean isLan() {
		return OnlineUser.get().isLan(_account_name);
	}

	/**
	 * 該帳號無存款
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return _adena_count <= 0;
	}
}