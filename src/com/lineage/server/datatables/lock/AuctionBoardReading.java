package com.lineage.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lineage.server.datatables.sql.AuctionBoardTable;
import com.lineage.server.datatables.storage.AuctionBoardStorage;
import com.lineage.server.templates.L1AuctionBoardTmp;

/**
 * 拍賣公告欄資料
 * 
 * @author dexc
 */
public class AuctionBoardReading {

	private final Lock _lock;

	private final AuctionBoardStorage _storage;

	private static AuctionBoardReading _instance;

	private AuctionBoardReading() {
		_lock = new ReentrantLock(true);
		_storage = new AuctionBoardTable();
	}

	public static AuctionBoardReading get() {
		if (_instance == null) {
			_instance = new AuctionBoardReading();
		}
		return _instance;
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		_lock.lock();
		try {
			_storage.load();
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 傳回公告陣列
	 */
	public Map<Integer, L1AuctionBoardTmp> getAuctionBoardTableList() {
		_lock.lock();
		Map<Integer, L1AuctionBoardTmp> tmp;
		try {
			tmp = _storage.getAuctionBoardTableList();
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 傳回指定公告
	 */
	public L1AuctionBoardTmp getAuctionBoardTable(final int houseId) {
		_lock.lock();
		L1AuctionBoardTmp tmp;
		try {
			tmp = _storage.getAuctionBoardTable(houseId);
		} finally {
			_lock.unlock();
		}
		return tmp;
	}

	/**
	 * 增加公告
	 */
	public void insertAuctionBoard(final L1AuctionBoardTmp board) {
		_lock.lock();
		try {
			_storage.insertAuctionBoard(board);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 更新公告
	 */
	public void updateAuctionBoard(final L1AuctionBoardTmp board) {
		_lock.lock();
		try {
			_storage.updateAuctionBoard(board);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 刪除公告
	 */
	public void deleteAuctionBoard(final int houseId) {
		_lock.lock();
		try {
			_storage.deleteAuctionBoard(houseId);
		} finally {
			_lock.unlock();
		}
	}

}
