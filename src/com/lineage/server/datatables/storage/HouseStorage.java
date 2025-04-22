package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.templates.L1House;

/**
 * 盟屋資料
 * 
 * @author dexc
 */
public interface HouseStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回小屋列表
	 * 
	 * @return
	 */
	public Map<Integer, L1House> getHouseTableList();

	/**
	 * 傳回指定小屋資料
	 * 
	 * @param houseId
	 * @return
	 */
	public L1House getHouseTable(int houseId);

	/**
	 * 更新小屋資料
	 * 
	 * @param house
	 */
	public void updateHouse(L1House house);
}
