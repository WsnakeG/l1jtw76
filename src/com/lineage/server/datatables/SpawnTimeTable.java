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
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1SpawnTime;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 召喚時間資料(指定時間召喚NPC) 固定時間之間執行召喚
 * 
 * @author dexc
 */
public class SpawnTimeTable {

	private static final Log _log = LogFactory.getLog(SpawnTimeTable.class);

	private static SpawnTimeTable _instance;

	private static final Map<Integer, L1SpawnTime> _times = new HashMap<Integer, L1SpawnTime>();

	public static SpawnTimeTable getInstance() {
		if (_instance == null) {
			_instance = new SpawnTimeTable();
		}
		return _instance;
	}

	private SpawnTimeTable() {
		final PerformanceTimer timer = new PerformanceTimer();
		load();
		_log.info("載入召喚時間資料數量: " + _times.size() + "(" + timer.get() + "ms)");
	}

	public L1SpawnTime get(final int id) {
		return _times.get(id);
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `spawnlist_time`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int npc_id = rs.getInt("npc_id");
				final L1Npc l1npc = NpcTable.get().getTemplate(npc_id);
				if (l1npc == null) {
					_log.error("召喚NPC編號: " + npc_id + " 不存在資料庫中!(spawnlist_time)");
					delete(npc_id);
					continue;
				}

				final L1SpawnTime.L1SpawnTimeBuilder builder = new L1SpawnTime.L1SpawnTimeBuilder(npc_id);
				builder.setTimeStart(rs.getTime("time_start"));
				builder.setTimeEnd(rs.getTime("time_end"));
				builder.setDeleteAtEndTime(rs.getBoolean("delete_at_endtime"));
				// 怪物出生訊息 (可使用%s對應怪物名稱) by terry0412
				builder.setSpawnMsg(rs.getString("spawn_msg"));
				// 每週出生日期 (使用西曆格式, 1=星期日, 空值則為全星期都套用) by terry0412
				builder.setWeekDays(rs.getString("week_days"));

				_times.put(npc_id, builder.build());
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 刪除錯誤資料
	 * 
	 * @param clan_id
	 */
	public static void delete(final int npc_id) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `spawnlist_time` WHERE `npc_id`=?");
			ps.setInt(1, npc_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
