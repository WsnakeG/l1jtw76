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
import com.lineage.server.templates.L1ItemTime;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 物品可用時間
 * 
 * @author terry0412
 */
public class ItemTimeTable {

	private static final Log _log = LogFactory.getLog(ItemTimeTable.class);

	// 物品編號 / 可用時間
	public static final Map<Integer, L1ItemTime> TIME = new HashMap<Integer, L1ItemTime>();

	private static ItemTimeTable _instance;

	public static ItemTimeTable get() {
		if (_instance == null) {
			_instance = new ItemTimeTable();
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
			pstm = con.prepareStatement("SELECT * FROM `server_item_time`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int key = rs.getInt("itemid");
				final int remain_time = rs.getInt("remain_time");
				final boolean equip = rs.getBoolean("equip");
				// 建立儲存物件
				final L1ItemTime itemTime = new L1ItemTime(remain_time, equip);
				TIME.put(key, itemTime);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入物品可用時間限制: " + TIME.size() + "(" + timer.get() + "ms)");
	}
}
