package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Town;

/**
 * 村莊資料
 * 
 * @author dexc
 */
public interface TownStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回村莊陣列資料
	 * 
	 * @return
	 */
	public L1Town[] getTownTableList();

	/**
	 * 傳回指定村莊資料
	 * 
	 * @param id
	 * @return
	 */
	public L1Town getTownTable(int id);

	/**
	 * 檢查是否為村長
	 * 
	 * @param pc
	 * @param town_id
	 * @return
	 */
	public boolean isLeader(L1PcInstance pc, int town_id);

	/**
	 * 更新村莊收入
	 * 
	 * @param town_id
	 * @param salesMoney
	 */
	public void addSalesMoney(int town_id, int salesMoney);

	/**
	 * 更新村莊稅率
	 */
	public void updateTaxRate();

	/**
	 * 更新收入資訊
	 */
	public void updateSalesMoneyYesterday();

	/**
	 * @param townId
	 * @return
	 */
	public String totalContribution(int townId);

	/**
	 *
	 */
	public void clearHomeTownID();

	/**
	 * 領取報酬
	 * 
	 * @return 報酬
	 */
	public int getPay(int objid);
}
