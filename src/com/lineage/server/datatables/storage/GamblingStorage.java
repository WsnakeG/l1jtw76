package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1Gambling;

/**
 * 賭場紀錄
 * 
 * @author dexc
 */
public interface GamblingStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回賭場紀錄(獲勝NPC票號)
	 * 
	 * @param key
	 * @return
	 */
	public L1Gambling getGambling(String key);

	/**
	 * 傳回賭場紀錄(場次編號)
	 * 
	 * @param key
	 * @return
	 */
	public L1Gambling getGambling(final int key);

	/**
	 * 增加賭場紀錄
	 */
	public void add(L1Gambling gambling);

	/**
	 * 更新賭場紀錄
	 */
	public void updateGambling(final int id, final int outcount);

	/**
	 * 傳回場次數量 與獲勝次數
	 * 
	 * @param npcid
	 * @return
	 */
	public int[] winCount(int npcid);

	/**
	 * 已用最大ID
	 * 
	 * @return
	 */
	public int maxId();
}
