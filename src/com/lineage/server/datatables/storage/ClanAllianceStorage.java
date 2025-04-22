package com.lineage.server.datatables.storage;

import java.util.ArrayList;

import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Clan;

/**
 * 血盟同盟紀錄
 * 
 * @author terry0412
 */
public interface ClanAllianceStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 建立資料
	 * 
	 * @param alliance
	 */
	public void insertAlliance(final L1Alliance alliance);

	/**
	 * 更新資料
	 * 
	 * @param order_id
	 * @param totalList
	 */
	public void updateAlliance(final int order_id, final ArrayList<L1Clan> totalList);

	/**
	 * 刪除資料
	 * 
	 * @param order_id
	 */
	public void deleteAlliance(final int order_id);

	/**
	 * 取得指定同盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	public L1Alliance getAlliance(final int clan_id);

}
