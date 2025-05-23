package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SpawnTimeTable;
import com.lineage.server.datatables.storage.SpawnBossStorage;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * BOSS召喚資料
 * 
 * @author dexc
 */
public class SpawnBossTable implements SpawnBossStorage {

	private static final Log _log = LogFactory.getLog(SpawnBossTable.class);

	// 召喚表清單(召喚ID / 召喚執行項)
	private static final Map<Integer, L1Spawn> _bossSpawnTable = new HashMap<Integer, L1Spawn>();

	// BOSS ID
	private final List<Integer> _bossId = new ArrayList<Integer>();

	private Calendar timestampToCalendar(final Timestamp ts) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `spawnlist_boss`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final int npcTemplateId = rs.getInt("npc_templateid");
				final L1Npc temp1 = NpcTable.get().getTemplate(npcTemplateId);

				if (temp1 == null) {
					_log.error("BOSS召喚MOB編號: " + npcTemplateId + " 不存在資料庫中!");

				} else {
					// 加入BOSS ID 清單
					_bossId.add(new Integer(npcTemplateId));
					temp1.set_boss(true);

					// BOSS 具備變身
					int tmp_id = temp1.getTransformId();
					while (tmp_id > 0) {
						// 加入BOSS ID 清單
						_bossId.add(new Integer(tmp_id));
						final L1Npc temp2 = NpcTable.get().getTemplate(tmp_id);
						temp2.set_boss(true);
						tmp_id = temp2.getTransformId();
					}

					/*
					 * if (temp1.getTransformId() != -1) { // 加入BOSS ID 清單
					 * _bossId.add(new Integer(temp1.getTransformId())); final
					 * L1Npc temp2 =
					 * NpcTable.get().getTemplate(temp1.getTransformId());
					 * temp2.set_boss(true); if (temp2.getTransformId() != -1) {
					 * // 加入BOSS ID 清單 _bossId.add(new
					 * Integer(temp2.getTransformId())); final L1Npc temp3 =
					 * NpcTable.get().getTemplate(temp2.getTransformId());
					 * temp3.set_boss(true); } }
					 */
					final int count = rs.getInt("count");
					if (count <= 0) {
						continue;
					}

					final int group_id = rs.getInt("group_id");
					final int locx1 = rs.getInt("locx1");
					final int locy1 = rs.getInt("locy1");
					final int locx2 = rs.getInt("locx2");
					final int locy2 = rs.getInt("locy2");
					final int heading = rs.getInt("heading");
					final int mapid = rs.getShort("mapid");

					final Timestamp time = rs.getTimestamp("next_spawn_time");
					// 下次召喚時間
					Calendar next_spawn_time = null;
					if (time != null) {
						// 下次召喚時間
						next_spawn_time = timestampToCalendar(rs.getTimestamp("next_spawn_time"));
					}

					final int spawn_interval = rs.getInt("spawn_interval");// 差異時間(單位:分鐘)
					final int exist_time = rs.getInt("exist_time");// 存在時間(單位:分鐘)

					final L1Spawn spawnDat = new L1Spawn(temp1);
					spawnDat.setId(id);
					spawnDat.setAmount(count);
					spawnDat.setGroupId(group_id);
					spawnDat.setNpcid(npcTemplateId);

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

					if ((locx2 < locx1) && (locx2 != 0)) {
						_log.error("spawnlist_boss : locx2 < locx1:" + id);
						continue;
					}

					if ((locy2 < locy1) && (locy2 != 0)) {
						_log.error("spawnlist_boss : locy2 < locy1:" + id);
						continue;
					}
					spawnDat.setHeading(heading);
					spawnDat.setMapId((short) mapid);

					spawnDat.setMinRespawnDelay(10);
					spawnDat.setMovementDistance(100);

					spawnDat.setName(temp1.get_name());

					spawnDat.set_nextSpawnTime(next_spawn_time);
					spawnDat.set_spawnInterval(spawn_interval);
					spawnDat.set_existTime(exist_time);
					spawnDat.setSpawnType(0);
					spawnDat.setTime(SpawnTimeTable.getInstance().get(npcTemplateId));
					// added by terry0412
					spawnDat.setBroadcast(rs.getBoolean("isBroadcast"));
					spawnDat.setBroadcastInfo(rs.getString("broadcastInfo"));

					if ((count > 1) && (spawnDat.getLocX1() == 0)) {
						final int range = Math.min(count * 6, 30);
						spawnDat.setLocX1(spawnDat.getLocX() - range);
						spawnDat.setLocY1(spawnDat.getLocY() - range);
						spawnDat.setLocX2(spawnDat.getLocX() + range);
						spawnDat.setLocY2(spawnDat.getLocY() + range);
					}
					spawnDat.init();
					_bossSpawnTable.put(new Integer(spawnDat.getId()), spawnDat);
					com.lineage.server.datatables.lock.SpawnBossReading.get().bossreid().add(spawnDat.getId());
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入BOSS召喚資料數量: " + _bossSpawnTable.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 更新資料庫 下次召喚時間紀錄
	 * 
	 * @param id
	 * @param spawnTime
	 */
	@Override
	public void upDateNextSpawnTime(final int id, final Calendar spawnTime) {
		// _log.info("更新BOSS召喚時間!"+id);
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `spawnlist_boss` SET `next_spawn_time`=? WHERE `id`=?");

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			final String fm = sdf.format(spawnTime.getTime());

			int i = 0;
			pstm.setString(++i, fm);
			pstm.setInt(++i, id);
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public static Map<Integer, L1Spawn> get_bossSpawnTable() {
		return _bossSpawnTable;
	}

	/**
	 * BOSS召喚列表中物件
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public L1Spawn getTemplate(final int key) {
		return _bossSpawnTable.get(key);
	}

	/**
	 * BOSS召喚列表中物件(NPCID)
	 * 
	 * @return _bossId
	 */
	@Override
	public List<Integer> bossIds() {
		return _bossId;
	}
}
