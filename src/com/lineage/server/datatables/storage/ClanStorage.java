package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 血盟資料
 * 
 * @author dexc
 */
public interface ClanStorage {

	/**
	 * 預先加載血盟資料
	 */
	public void load();

	/**
	 * 加入虛擬血盟
	 * 
	 * @param integer
	 * @param l1Clan
	 */
	public void addDeClan(Integer integer, L1Clan l1Clan);

	/**
	 * 建立血盟資料
	 * 
	 * @param player
	 * @param clan_name
	 * @return
	 */
	public L1Clan createClan(final L1PcInstance player, final String clan_name);

	/**
	 * 更新血盟資料
	 * 
	 * @param clan
	 */
	public void updateClan(final L1Clan clan);

	/**
	 * 刪除血盟資料
	 * 
	 * @param clan_name
	 */
	public void deleteClan(final String clan_name);

	/**
	 * 指定血盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	public L1Clan getTemplate(final int clan_id);

	/**
	 * 全部血盟資料
	 * 
	 * @return
	 */
	public Map<Integer, L1Clan> get_clans();

}
