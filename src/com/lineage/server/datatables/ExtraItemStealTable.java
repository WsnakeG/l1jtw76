package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemSteal;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 道具搶奪系統
 * 
 * @author terry0412
 */
public final class ExtraItemStealTable {

	private static final Log _log = LogFactory.getLog(ExtraItemStealTable.class);

	private static ExtraItemStealTable _instance;

	private static final ArrayList<L1ItemSteal> _stealList = new ArrayList<L1ItemSteal>();

	public static ExtraItemStealTable getInstance() {
		if (_instance == null) {
			_instance = new ExtraItemStealTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM extra_item_steal");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int item_id = rs.getInt("item_id");// 設置被搶奪的道具編號
				final int level = rs.getInt("level");// 設置這個欄位等級以上會被搶奪
				final int mete_level = rs.getInt("mete_level");// 轉生次數
				final int steal_chance = rs.getInt("steal_chance");// 被搶奪的機率
				final int min_steal_count = rs.getInt("min_steal_count");// 被搶奪的最低數量
				final int max_steal_count = rs.getInt("max_steal_count");// 被搶奪的最大數量
				final boolean is_broadcast = rs.getBoolean("is_broadcast");// 被搶奪道具是否廣播
				final boolean drop_on_floor = rs.getBoolean("drop_on_floor");// 被奪取道具方式
																				// (1=掉地面,
																				// 0=掉在攻擊者身上)
				final int anti_steal_item_id = rs.getInt("anti_steal_item_id");// 防止被奪取的道具編號

				// 建立儲存資料
				final L1ItemSteal itemSteal = new L1ItemSteal(item_id, level, mete_level, steal_chance,
						min_steal_count, max_steal_count, is_broadcast, drop_on_floor, anti_steal_item_id);

				// 加到清單
				_stealList.add(itemSteal);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入道具奪取資料數量: " + _stealList.size() + "(" + timer.get() + "ms)");
	}

	public final ArrayList<L1ItemSteal> getList() {
		return _stealList;
	}
}
