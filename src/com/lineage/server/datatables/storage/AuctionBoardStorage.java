package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 拍賣公告欄資料
 * 
 * @author dexc
 */
public interface AuctionBoardStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回公告陣列
	 */
	public Map<Integer, L1AuctionBoardTmp> getAuctionBoardTableList();

	/**
	 * 傳回指定公告
	 */
	public L1AuctionBoardTmp getAuctionBoardTable(int houseId);

	/**
	 * 增加公告
	 */
	public void insertAuctionBoard(L1AuctionBoardTmp board);

	/**
	 * 更新公告
	 */
	public void updateAuctionBoard(L1AuctionBoardTmp board);

	/**
	 * 刪除公告
	 */
	public void deleteAuctionBoard(int houseId);

}
