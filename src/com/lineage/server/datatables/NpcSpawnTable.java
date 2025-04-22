package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Spawn;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 召喚NPC資料
 * 
 * @author dexc
 */
public class NpcSpawnTable {

	private static final Log _log = LogFactory.getLog(NpcSpawnTable.class);

	private static NpcSpawnTable _instance;

	private static final Map<Integer, L1Spawn> _spawntable = new HashMap<Integer, L1Spawn>();

	public static NpcSpawnTable get() {
		if (_instance == null) {
			_instance = new NpcSpawnTable();
		}
		return _instance;
	}

	public void reload() {
		_spawntable.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();

		int spawnCount = 0;

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `spawnlist_npc`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int npcTemplateid = rs.getInt("npc_templateid");
				final L1Npc l1npc = NpcTable.get().getTemplate(npcTemplateid);
				if (l1npc == null) {
					_log.error("召喚NPC編號: " + npcTemplateid + " 不存在資料庫中!(spawnlist_npc)");
					delete(npcTemplateid);
					continue;
				}

				final int count = rs.getInt("count");
				if (count == 0) {
					continue;
				}

				final L1Spawn l1spawn = new L1Spawn(l1npc);
				l1spawn.setId(rs.getInt("id"));
				l1spawn.setAmount(count);
				l1spawn.setLocX(rs.getInt("locx"));
				l1spawn.setLocY(rs.getInt("locy"));
				l1spawn.setRandomx(rs.getInt("randomx"));
				l1spawn.setRandomy(rs.getInt("randomy"));
				l1spawn.setLocX1(0);
				l1spawn.setLocY1(0);
				l1spawn.setLocX2(0);
				l1spawn.setLocY2(0);
				l1spawn.setHeading(rs.getInt("heading"));
				l1spawn.setMinRespawnDelay(rs.getInt("respawn_delay"));
				l1spawn.setMapId(rs.getShort("mapid"));
				l1spawn.setMovementDistance(rs.getInt("movement_distance"));
				// NPC允許存在時間 by terry0412
				l1spawn.setDeleteTime(
						rs.getTimestamp("deleteTime") == null ? 0 : rs.getTimestamp("deleteTime").getTime());

				l1spawn.setName(l1npc.get_name());
				l1spawn.init();
				spawnCount += l1spawn.getAmount();

				_spawntable.put(new Integer(l1spawn.getId()), l1spawn);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		_log.info("載入召喚NPC資料數量: " + _spawntable.size() + "/" + spawnCount + "(" + timer.get() + "ms)");
	}

	/**
	 * 手動增加SPAWN物件
	 * 
	 * @param pc
	 * @param npc
	 */
	public void storeSpawn(final L1PcInstance pc, final L1Npc npc) {
		Connection con = null;
		PreparedStatement pstm = null;

		try {
			final int count = 1;
			final String note = npc.get_name();

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO `spawnlist_npc` SET `location`=?,`count`=?,"
					+ "`npc_templateid`=?,`locx`=?,`locy`=?,`heading`=?,`mapid`=?");
			pstm.setString(1, note);
			pstm.setInt(2, count);
			pstm.setInt(3, npc.get_npcId());
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.setInt(6, pc.getHeading());
			pstm.setInt(7, pc.getMapId());
			pstm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
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
			ps = cn.prepareStatement("DELETE FROM `spawnlist_npc` WHERE `npc_templateid`=?");
			ps.setInt(1, npc_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	public final void storeSpawn(final int npcId, final int locx, final int locy, final int mapId,
			final int time) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO `spawnlist_npc` SET `location`=?,"
					+ "`count`=1,`npc_templateid`=?,`locx`=?,`locy`=?,`heading`=0,"
					+ "`mapid`=?,`deleteTime`=?");
			pstm.setString(1, "限時NPC物件");
			pstm.setInt(2, npcId);
			pstm.setInt(3, locx);
			pstm.setInt(4, locy);
			pstm.setInt(5, mapId);
			pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis() + time));
			pstm.execute();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public final void checkMaps() {
		final long timeMillis = System.currentTimeMillis();
		for (final L1Spawn spawn : _spawntable.values()) {
			// 存在限制時間
			if (spawn.getDeleteTime() > 0) {
				final L1NpcInstance npc = spawn.getNpcTemp();
				if (npc != null) {
					final long checkTime = spawn.getDeleteTime() - timeMillis;
					if ((checkTime > 0) && (checkTime <= 600000)) {
						if (npc.getNpcId() == 70936) {
							for (final L1PcInstance pc : World.get().getAllPlayers()) {
								if (pc.getMapId() == 1002) {
									pc.sendPackets(new S_ServerMessage(1584));
								}
							}
						}
					} else if (checkTime <= 0) {
						if (npc.getNpcId() == 70936) {
							npc.broadcastPacketAll(new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Die));
						}
						npc.setCurrentHpDirect(0);
						npc.setDead(true);
						npc.setStatus(ActionCodes.ACTION_Die);
						npc.setreSpawn(false);
						npc.deleteMe();
						deleteId(spawn.getId());
						break;
					}
				}
			}
		}
	}

	private final void deleteId(final int id) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `spawnlist_npc` WHERE `id`=?");
			ps.setInt(1, id);
			ps.execute();
			_spawntable.remove(id);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	public L1Spawn getTemplate(final int i) {
		return _spawntable.get(i);
	}
}
