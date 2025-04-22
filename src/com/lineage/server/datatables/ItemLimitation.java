package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 伺服器掉落物總量管制
 * 
 * @author simlin
 */
public class ItemLimitation {

	private static final Log _log = LogFactory.getLog(ItemLimitation.class);

	public final static Map<Integer, Integer> _limitation = new HashMap<Integer, Integer>();

	private static ItemLimitation _instance;

	public static ItemLimitation get() {
		if (_instance == null) {
			_instance = new ItemLimitation();
		}
		return _instance;
	}

	public void reload() {
		_limitation.clear();
		load();
	}

	private ItemLimitation() {
		load();
	}

	private void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int size = 0;
		try {
			con = DatabaseFactoryLogin.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `droplist_limitation`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int itemId = rs.getInt("item_id");
				final int limit_count = rs.getInt("limit_count");
				_limitation.put(itemId, limit_count);
				size++;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入道具掉落限制數量: " + size + "(" + timer.get() + "ms)");
	}

	/**
	 * 檢查掉落物總量
	 * 
	 * @param itemId 目標流水號
	 * @return
	 */
	public boolean checkLimitation(final int itemId) {
		if (_limitation.get(itemId) != null) {
			// 可掉落數量為0
			if (_limitation.get(itemId) == 0) {
				return false;
			}
			// 更新可掉落數量
			final int now_count = _limitation.get(itemId) - 1;
			Math.max(now_count, 0);
			_limitation.put(itemId, now_count);
		}
		return true;
	}

}
