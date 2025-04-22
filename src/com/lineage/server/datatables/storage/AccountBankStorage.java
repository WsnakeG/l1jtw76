package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.templates.L1Bank;

/**
 * 銀行帳戶資料
 */
public interface AccountBankStorage {

	/**
	 * 預先加載
	 */
	public void load();

	/**
	 * 該帳戶資料
	 * 
	 * @param account_name
	 * @return
	 */
	public L1Bank get(String account_name);

	public Map<String, L1Bank> map();

	/**
	 * 建立帳號資料
	 * 
	 * @param loginName
	 * @param bank
	 */
	public void create(final String loginName, final L1Bank bank);

	/**
	 * 更新密碼
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	public void updatePass(final String loginName, final String pwd);

	/**
	 * 更新存款
	 * 
	 * @param loginName 帳號
	 * @param adena 金額
	 */
	public void updateAdena(final String loginName, final long adena);

}
