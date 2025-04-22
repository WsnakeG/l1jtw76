package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MapsLimitTime;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 地圖群組設置資料 (入場時間限制)
 * 
 * @author terry0412
 */
public final class MapsGroupTable {

	private static final Log _log = LogFactory.getLog(MapsGroupTable.class);

	// 全入場地圖設定 暫存組
	private static final Map<Integer, L1MapsLimitTime> _mapsGroup = new HashMap<Integer, L1MapsLimitTime>();

	private static MapsGroupTable _instance;

	public static MapsGroupTable get() {
		if (_instance == null) {
			_instance = new MapsGroupTable();
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
			// 降冪排序
			pstm = con.prepareStatement("SELECT * FROM `mapids_group` ORDER BY order_id DESC");

			for (rs = pstm.executeQuery(); rs.next();) {
				final int order_id = rs.getInt("order_id");
				final String mapIds = rs.getString("mapids");
				final String mapName = rs.getString("name");
				final int limitTime = rs.getInt("time");
				final int outMapX = rs.getInt("out_x");
				final int outMapY = rs.getInt("out_y");
				final short outMapId = rs.getShort("out_map");

				// 建立入場地圖列表
				final List<Integer> mapList = new ArrayList<Integer>();
				for (final String map_str : mapIds.split(",")) {
					mapList.add(Integer.parseInt(map_str));
				}

				// 建立暫存組
				final L1MapsLimitTime mapsLimitTime = new L1MapsLimitTime(order_id, mapList, mapName,
						limitTime, outMapX, outMapY, outMapId);

				_mapsGroup.put(order_id, mapsLimitTime);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入地圖群組設置資料 (入場時間限制) 數量: " + _mapsGroup.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 傳回全部入場地圖資料
	 * 
	 * @return
	 */
	public final Map<Integer, L1MapsLimitTime> getGroupMaps() {
		return _mapsGroup;
	}

	/**
	 * 檢查人物所在地圖編號是否符合
	 * 
	 * @param mapId
	 * @return
	 */
	public final L1MapsLimitTime findGroupMap(final int mapId) {
		for (final L1MapsLimitTime mapsLimitTime : _mapsGroup.values()) {
			if (mapsLimitTime.getMapList().contains(mapId)) {
				return mapsLimitTime;
			}
		}
		return null;
	}
}
