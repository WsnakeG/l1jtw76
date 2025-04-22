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
 * 陣營名稱記錄
 * 
 * @author daien
 */
public class C1_Name_Table {

	private static final Log _log = LogFactory.getLog(C1_Name_Table.class);

	private static final Map<Integer, String> _names = new HashMap<Integer, String>();

	private static C1_Name_Table _instance;

	public static C1_Name_Table get() {
		if (_instance == null) {
			_instance = new C1_Name_Table();
		}
		return _instance;
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_c1_name`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int c1_id = rs.getInt("c1_id");
				final String c1_name = rs.getString("c1_name");
				_names.put(c1_id, c1_name);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入陣營名稱記錄數量: " + _names.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 編號對應的陣營名稱
	 * 
	 * @param key
	 * @return
	 */
	public String get(final int key) {
		return _names.get(key);
	}

	/**
	 * 取得陣營列表 by terry0412
	 * 
	 * @return
	 */
	public Map<Integer, String> getMapList() {
		return _names;
	}

	/**
	 * 名稱對應的陣營編號
	 * 
	 * @param v
	 * @return
	 */
	public Integer getv(final String v) {
		for (final Integer key : _names.keySet()) {
			final String value = _names.get(key);
			if (value.equals(v)) {
				return key;
			}
		}
		return -1;
	}
}
