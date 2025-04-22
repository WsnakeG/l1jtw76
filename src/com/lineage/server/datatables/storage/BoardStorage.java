package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Board;

/**
 * 佈告欄資料
 * 
 * @author dexc
 */
public interface BoardStorage {
	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回公告陣列
	 */
	public Map<Integer, L1Board> getBoardMap();

	/**
	 * 傳回公告陣列
	 */
	public L1Board[] getBoardTableList();

	/**
	 * 傳回指定公告
	 */
	public L1Board getBoardTable(int houseId);

	/**
	 * 傳回已用最大公告編號
	 */
	public int getMaxId();

	/**
	 * 增加佈告欄資料
	 * 
	 * @param pc
	 * @param date
	 * @param title
	 * @param content
	 */
	public void writeTopic(L1PcInstance pc, String date, String title, String content);

	/**
	 * 刪除佈告欄資料
	 * 
	 * @param number
	 */
	public void deleteTopic(int number);

}
