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
import com.lineage.server.model.L1Spawn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * EVENT NPC召喚資料
 * 
 * @author dexc
 */
public class EventSpawnTable {

	private static final Log _log = LogFactory.getLog(EventSpawnTable.class);

	private static EventSpawnTable _instance;

	private static final Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

	public static EventSpawnTable get() {
		if (_instance == null) {
			_instance = new EventSpawnTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int spawnCount = 0;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_event_spawn`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final int eventid = rs.getInt("eventid");

				// 檢查活動編號 是否啟用
				if (EventTable.get().getTemplate(eventid) != null) {

					final int npcTemplateId = rs.getInt("npc_templateid");
					final L1Npc template1 = NpcTable.get().getTemplate(npcTemplateId);

					if (template1 == null) {
						_log.error("召喚NPC編號: " + npcTemplateId + " 不存在資料庫中!(server_event_spawn)");
						delete(npcTemplateId);
						continue;
					}

					final int count = rs.getInt("count");

					if (count == 0) {
						continue;
					}

					final int group_id = rs.getInt("group_id");

					final int locx1 = rs.getInt("locx1");
					final int locx2 = rs.getInt("locx2");
					final int locy1 = rs.getInt("locy1");
					final int locy2 = rs.getInt("locy2");
					final int mapid = rs.getInt("mapid");
					final int heading = rs.getInt("heading");
					final int respawn_delay = rs.getInt("respawn_delay");
					final int movement_distance = rs.getInt("movement_distance");
					final int near_spawn = rs.getInt("near_spawn");

					final L1Spawn spawnDat = new L1Spawn(template1);
					spawnDat.setId(id);
					spawnDat.setAmount(count);
					spawnDat.setGroupId(group_id);

					if ((locx2 == 0) && (locy2 == 0)) {
						spawnDat.setLocX(locx1);
						spawnDat.setLocY(locy1);
						spawnDat.setLocX1(0);
						spawnDat.setLocY1(0);
						spawnDat.setLocX2(0);
						spawnDat.setLocY2(0);

					} else {
						spawnDat.setLocX(locx1);
						spawnDat.setLocY(locy1);
						spawnDat.setLocX1(locx1);
						spawnDat.setLocY1(locy1);
						spawnDat.setLocX2(locx2);
						spawnDat.setLocY2(locy2);
					}

					if ((count > 1) && (spawnDat.getLocX1() == 0)) {
						final int range = Math.min(count * 6, 30);
						spawnDat.setLocX1(spawnDat.getLocX() - range);
						spawnDat.setLocY1(spawnDat.getLocY() - range);
						spawnDat.setLocX2(spawnDat.getLocX() + range);
						spawnDat.setLocY2(spawnDat.getLocY() + range);
					}

					if ((locx2 < locx1) && (locx2 != 0)) {
						_log.error("server_event_spawn : locx2 < locx1: " + id);
						continue;
					}

					if ((locy2 < locy1) && (locy2 != 0)) {
						_log.error("server_event_spawn : locy2 < locy1: " + id);
						continue;
					}

					spawnDat.setRandomx(0);
					spawnDat.setRandomy(0);

					spawnDat.setMinRespawnDelay(respawn_delay);
					spawnDat.setMovementDistance(movement_distance);

					spawnDat.setHeading(heading);
					spawnDat.setMapId((short) mapid);
					spawnDat.setSpawnType(near_spawn);// 是否閃避玩家召喚

					spawnDat.setTime(SpawnTimeTable.getInstance().get(spawnDat.getId()));

					spawnDat.setName(template1.get_name());

					spawnDat.init();
					spawnCount += spawnDat.getAmount();

					_spawntable.put(new Integer(spawnDat.getId()), spawnDat);
				}

			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入召喚EVENT NPC資料數量: " + spawnCount + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除錯誤資料
	 * 
	 * @param npc_id
	 */
	public static void delete(final int npc_id) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `server_event_spawn` WHERE `npc_templateid`=?");
			ps.setInt(1, npc_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * EVENT NPC/MOB召喚列表中物件
	 * 
	 * @param Id
	 * @return
	 */
	public L1Spawn getTemplate(final int Id) {
		return _spawntable.get(new Integer(Id));
	}
}
