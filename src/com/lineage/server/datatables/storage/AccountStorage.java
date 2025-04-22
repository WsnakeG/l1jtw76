package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Account;

/**
 * 人物帳戶資料
 */
public interface AccountStorage {

	/**
	 * 預先加載帳戶名稱
	 */
	public void load();

	/**
	 * 傳回指定帳戶名稱資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 * @return true:有該帳號 false:沒有該帳號
	 */
	public boolean isAccountUT(final String loginName);

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
			final String spwd);

	/**
	 * 傳回指定帳戶資料是否存在
	 * 
	 * @param loginName 帳號名稱
	 */
	public boolean isAccount(final String loginName);

	/**
	 * 傳回指定帳戶資料
	 * 
	 * @param loginName 帳號
	 */
	public L1Account getAccount(final String loginName);

	/**
	 * 更新倉庫密碼
	 * 
	 * @param loginName 帳號
	 * @param pwd 密碼
	 */
	public void updateWarehouse(final String loginName, final int pwd);

	/**
	 * 更新上線時間/IP/MAC
	 * 
	 * @param account 帳戶
	 */
	public void updateLastActive(final L1Account account);

	/**
	 * 更新帳號可用人物數量
	 * 
	 * @param loginName 帳號
	 * @param count 擴充數量
	 */
	public void updateCharacterSlot(final String loginName, final int count);

	/**
	 * 更新帳號密碼
	 * 
	 * @param loginName 帳號
	 * @param newpwd 新密碼
	 */
	public void updatePwd(final String loginName, final String newpwd);

	/**
	 * 更新帳號連線狀態
	 * 
	 * @param loginName 帳號
	 * @param islan 是否連線
	 */
	public void updateLan(final String loginName, final boolean islan);

	/**
	 * 更新帳號連線狀態(全部離線)
	 */
	public void updateLan();

	public int getPoint(String loginName);

	public void setPoint(String loginName, int point);

}
