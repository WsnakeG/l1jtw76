package com.lineage.server.datatables.storage;

import java.util.ArrayList;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1BookMark;

/**
 * 記憶座標紀錄資料
 * 
 * @author dexc
 */
public interface CharBookStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 取回保留記憶座標紀錄群
	 * 
	 * @param pc
	 */
	public ArrayList<L1BookMark> getBookMarks(final L1PcInstance pc);

	/**
	 * 取回保留記憶座標紀錄
	 * 
	 * @param pc
	 */
	public L1BookMark getBookMark(final L1PcInstance pc, final int i);

	/**
	 * 刪除記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	public void deleteBookmark(final L1PcInstance pc, final String s);

	/**
	 * 增加記憶座標
	 * 
	 * @param pc
	 * @param s
	 */
	public void addBookmark(final L1PcInstance pc, final String s);
}
