package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * Quest(副本)地圖設置資料
 * 
 * @author dexc
 */
public class QuestMapTable {

	private static final Log _log = LogFactory.getLog(QuestMapTable.class);

	private static QuestMapTable _instance;

	// 地圖資訊
	private static final Map<Integer, Integer> _mapList = new HashMap<Integer, Integer>();

	// 地圖時間限制(單位:秒)
	private static final Map<Integer, Integer> _timeList = new HashMap<Integer, Integer>();

	public static QuestMapTable get() {
		if (_instance == null) {
			_instance = new QuestMapTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_quest_maps`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int mapid = rs.getInt("mapid");// 地圖編號
				final int time = rs.getInt("time");// 該地圖可進入時間限制 單位:秒(-1無限制)
				final int users = rs.getInt("users");// 允許進入人數(<= 0 不限制)

				/*
				 * 假設該副本對應多張地圖 則對應任務編號僅設定在任務開始進入的地一張地圖 其餘地圖均設定-1
				 */

				_mapList.put(new Integer(mapid), new Integer(users));

				if (time > 0) {
					_timeList.put(new Integer(mapid), new Integer(time));
				}
			}

			_log.info("載入Quest(副本)地圖設置資料數量: " + _mapList.size() + "(" + timer.get() + "ms)");

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 是Quest(副本)地圖設置
	 * 
	 * @param mapid
	 * @return true:是 false:不是
	 */
	public boolean isQuestMap(final int mapid) {
		return _mapList.get(new Integer(mapid)) != null;
	}

	/**
	 * 傳回Quest(副本)地圖設置內容
	 * 
	 * @param mapid MAP編號
	 * @return 可進入人數限制
	 */
	public int getTemplate(final int mapid) {
		if (_mapList.get(new Integer(mapid)) != null) {
			return _mapList.get(new Integer(mapid));
		}
		return -1;
	}

	/**
	 * 傳回Quest(副本)地圖時間內容
	 * 
	 * @param mapid MAP編號
	 * @return 可進入時間限制(單位:秒)
	 */
	public Integer getTime(final int mapid) {
		return _timeList.get(new Integer(mapid));
	}

	/**
	 * 傳回Quest(副本)地圖設置清單
	 * 
	 * @return
	 */
	public Map<Integer, Integer> getList() {
		return _mapList;
	}
}
