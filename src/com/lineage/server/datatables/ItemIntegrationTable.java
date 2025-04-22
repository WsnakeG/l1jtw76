package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 物品融合系統(DB自製)
 * 
 * @author Roy
 */
public final class ItemIntegrationTable {

	private static final Log _log = LogFactory.getLog(ItemIntegrationTable.class);

	public static ArrayList<ArrayList<Object>> aData20 = new ArrayList<ArrayList<Object>>();

	private static ItemIntegrationTable _instance;

	public static final String TOKEN = ",";

	public static ItemIntegrationTable get() {
		if (_instance == null) {
			_instance = new ItemIntegrationTable();
		}
		return _instance;
	}

	public void reload() {
		aData20.clear();
		load();
	}

	public final static void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_item_integration");
			rs = pstm.executeQuery();
			ArrayList<Object> aReturn = null;
			if (rs != null) {
				while (rs.next()) {
					aReturn = new ArrayList<Object>();
					aReturn.add(0, new Integer(rs.getInt("item_id")));
					aReturn.add(1, new Integer(rs.getInt("checkClass")));
					aReturn.add(2, new Integer(rs.getInt("level")));
					aReturn.add(3, new Integer(rs.getInt("needCount")));
					aReturn.add(4, new Integer(rs.getInt("Integration_ID")));
					aReturn.add(5, new Integer(rs.getInt("Integration_count")));
					aReturn.add(6, new Integer(rs.getInt("random")));

					// 確認道具
					if ((rs.getString("materials") != null) && !rs.getString("materials").equals("")
							&& !rs.getString("materials").equals("0")) {
						aReturn.add(7, getArray(rs.getString("materials"), TOKEN, 1));
					} else {
						aReturn.add(7, null);
					}
					// 確認道具數量
					if ((rs.getString("counts") != null) && !rs.getString("counts").equals("")
							&& !rs.getString("counts").equals("0")) {
						aReturn.add(8, getArray(rs.getString("counts"), TOKEN, 1));
					} else {
						aReturn.add(8, null);
					}

					// 給予道具
					if ((rs.getString("new_item") != null) && !rs.getString("new_item").equals("")
							&& !rs.getString("new_item").equals("0")) {
						aReturn.add(9, getArray(rs.getString("new_item"), TOKEN, 1));
					} else {
						aReturn.add(9, null);
					}

					// 給予道具數量
					if ((rs.getString("new_item_counts") != null)
							&& !rs.getString("new_item_counts").equals("")
							&& !rs.getString("new_item_counts").equals("0")) {
						aReturn.add(10, getArray(rs.getString("new_item_counts"), TOKEN, 1));
					} else {
						aReturn.add(10, null);
					}
					aReturn.add(11, rs.getString("msg"));
					aReturn.add(12, new Integer(rs.getInt("gfxId")));
					aData20.add(aReturn);

				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		_log.info("載入裝備融合系統: " + aData20.size() + "(" + timer.get() + "ms)");
	}

	// 複數文字
	private static Object getArray(final String s, final String sToken, final int iType) {
		final StringTokenizer st = new StringTokenizer(s, sToken);
		final int iSize = st.countTokens();
		String sTemp = null;
		if (iType == 1) {
			final int[] iReturn = new int[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				iReturn[i] = Integer.parseInt(sTemp);
			}
			return iReturn;
		}
		if (iType == 2) { // String
			final String[] sReturn = new String[iSize];
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn[i] = sTemp;
			}
			return sReturn;
		}
		if (iType == 3) { // String
			String sReturn = null;
			for (int i = 0; i < iSize; i++) {
				sTemp = st.nextToken();
				sReturn = sTemp;
			}
			return sReturn;
		}
		return null;
	}

	// 複數文字 end
}
