package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharMapsTimeStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * @author terry0412
 */
public class CharMapsTimeTable implements CharMapsTimeStorage {

	private static final Log _log = LogFactory.getLog(CharMapsTimeTable.class);

	private static final Map<Integer, HashMap<Integer, Integer>> _timeMap = new ConcurrentHashMap<Integer, HashMap<Integer, Integer>>();

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_maps_time` ORDER BY `char_obj_id`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int char_obj_id = rs.getInt("char_obj_id");

				if (CharObjidTable.get().isChar(char_obj_id) != null) {
					final int order_id = rs.getInt("order_id");
					final int used_time = rs.getInt("used_time");

					// 加入清單
					addTime(char_obj_id, order_id, used_time);

				} else {
					deleteTime(char_obj_id);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入地圖入場時間紀錄資料數量: " + _timeMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 新增地圖入場時間紀錄
	 * 
	 * @param objId
	 * @param order_id
	 * @param used_time
	 * @return
	 */
	@Override
	public Map<Integer, Integer> addTime(final int objId, final int order_id, final int used_time) {
		final HashMap<Integer, Integer> list = _timeMap.get(objId);
		if (list == null) {
			final HashMap<Integer, Integer> newlist = new HashMap<Integer, Integer>();
			newlist.put(order_id, used_time);
			// put
			_timeMap.put(objId, newlist);

			return newlist;

		} else {
			list.put(order_id, used_time);
		}
		return list;
	}

	/**
	 * 取回地圖入場時間紀錄
	 * 
	 * @param pc
	 */
	@Override
	public void getTime(final L1PcInstance pc) {
		final HashMap<Integer, Integer> list = _timeMap.get(pc.getId());
		if (list != null) {
			pc.setMapsList(list);
		}
	}

	/**
	 * 刪除地圖入場時間紀錄
	 * 
	 * @param objid
	 */
	@Override
	public void deleteTime(final int objid) {
		final HashMap<Integer, Integer> list = _timeMap.get(objid);
		if (list != null) {
			list.clear();
		}
		_timeMap.remove(objid);

		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_maps_time` WHERE `char_obj_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 刪除並儲存全部地圖入場時間紀錄
	 */
	@Override
	public void saveAllTime() {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			cn.setAutoCommit(false);
			ps = cn.prepareStatement("DELETE FROM `character_maps_time`");
			ps.execute();

			final String sql = "INSERT INTO `character_maps_time`"
					+ "SET `char_obj_id`=?,`order_id`=?,`used_time`=?";

			for (final Entry<Integer, HashMap<Integer, Integer>> entryList : _timeMap.entrySet()) {
				for (final Entry<Integer, Integer> entryList2 : entryList.getValue().entrySet()) {
					try (PreparedStatement ps_each = cn.prepareStatement(sql)) {
						ps_each.setInt(1, entryList.getKey());
						ps_each.setInt(2, entryList2.getKey());
						ps_each.setInt(3, entryList2.getValue());
						ps_each.execute();
					}
				}
			}

			cn.commit();
			cn.setAutoCommit(true); // reset

		} catch (final SQLException e) {
			try {
				cn.rollback();
			} catch (final SQLException ignore) {
				// ignore
			}
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 清除全部地圖入場時間紀錄 (重置用)
	 */
	@Override
	public void clearAllTime() {
		for (final HashMap<Integer, Integer> list : _timeMap.values()) {
			if (list != null) {
				list.clear();
			}
		}
		_timeMap.clear();

		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_maps_time`");
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
