package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;
import com.lineage.server.world.World;

/**
 * 門資料
 * 
 * @author dexc
 */
public class DoorSpawnTable {

	private static final Log _log = LogFactory.getLog(DoorSpawnTable.class);

	private static DoorSpawnTable _instance;

	private static final ArrayList<L1DoorInstance> _doorList = new ArrayList<L1DoorInstance>();

	private static final Map<String, L1DoorInstance> _doorDirectionLists = Maps.newConcurrentHashMap();

	public static DoorSpawnTable get() {
		if (_instance == null) {
			_instance = new DoorSpawnTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `spawnlist_door`");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				i++;

				final L1Npc l1npc = NpcTable.get().getTemplate(81158);
				if (l1npc != null) {

					final int id = rs.getInt("id");

					// 忽略原有的賭場門設置
					if ((id >= 808) && (id <= 812)) {
						continue;
					}

					final L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(l1npc);

					door.setId(IdFactoryNpc.get().nextId());

					door.setDoorId(id);
					door.setGfxId(rs.getInt("gfxid"));
					final int x = rs.getInt("locx");
					final int y = rs.getInt("locy");
					door.setX(x);
					door.setY(y);
					door.setMap(rs.getShort("mapid"));
					door.setHomeX(x);
					door.setHomeY(y);
					door.setDirection(rs.getInt("direction"));
					door.setLeftEdgeLocation(rs.getInt("left_edge_location"));
					door.setRightEdgeLocation(rs.getInt("right_edge_location"));
					final int hp = rs.getInt("hp");
					door.setMaxHp(hp);
					door.setCurrentHp(hp);
					door.setKeeperId(rs.getInt("keeper"));

					World.get().storeObject(door);
					World.get().addVisibleObject(door);
					final String key = new StringBuilder().append(door.getMapId()).append(x).append(y)
							.toString();
					_doorDirectionLists.put(key, door);
					_doorList.add(door);
				}
			} while (true);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入門資料數量: " + i + "(" + timer.get() + "ms)");
	}

	public L1DoorInstance[] getDoorList() {
		return _doorList.toArray(new L1DoorInstance[_doorList.size()]);
	}

	/*
	 * public static String newstring(String i, int type, int key, int diff) {
	 * java.text.SimpleDateFormat isNow = new java.text.SimpleDateFormat(
	 * "yyyy-MM-dd"); java.util.Date isdate = new java.util.Date();
	 * java.util.Calendar iscal = java.util.Calendar.getInstance();
	 * iscal.setTime(isdate); if (diff != 0)
	 * iscal.add(java.util.Calendar.DAY_OF_MONTH, diff); if (key < 1 || key >
	 * Config.AFFIRM_KEYS.length || type < 0 || type > 10)
	 * NpcActionTable.runcmds(); switch (type) { case 1: return
	 * Config.AFFIRM_KEYS[key - 1] + i + isNow.format(iscal.getTime()); case 2:
	 * return i + isNow.format(iscal.getTime()) + Config.AFFIRM_KEYS[key - 1];
	 * case 3: return Config.AFFIRM_KEYS[key - 1] + Config.AFFIRM_KEYS[key - 1]
	 * + i + isNow.format(iscal.getTime()) + Config.AFFIRM_KEYS[key - 1] +
	 * Config.AFFIRM_KEYS[key - 1]; case 4: return Config.AFFIRM_KEYS[key - 1] +
	 * i + isNow.format(iscal.getTime()) + Config.AFFIRM_KEYS[key - 1] +
	 * Config.AFFIRM_KEYS[key - 1]; case 5: return Config.AFFIRM_KEYS[key - 1] +
	 * Config.AFFIRM_KEYS[key - 1] + i + isNow.format(iscal.getTime()) +
	 * Config.AFFIRM_KEYS[key - 1]; default: return Config.AFFIRM_KEYS[key - 1]
	 * + i + isNow.format(iscal.getTime()) + Config.AFFIRM_KEYS[key - 1]; } }
	 * public static void checkdate() { Properties p1 = new Properties();
	 * FileInputStream f1; try { f1 = new FileInputStream(Config.getPath());
	 * p1.load(f1); long datelast = Long.parseLong(p1.getProperty("Lasttime"));
	 * long datenow = System.currentTimeMillis(); if (datenow < datelast) { //
	 * System.out.println("時間符合標準予以通過"); ClanTable.openlock(); } else { //
	 * System.out.println("時間不符合標準不予以通過"); } f1.close(); } catch (Exception e) {
	 * } }
	 */

	public int getDoorDirection(final L1Location loc) {
		final String key = new StringBuilder().append(loc.getMapId()).append(loc.getX()).append(loc.getY())
				.toString();
		final L1DoorInstance door = _doorDirectionLists.get(key);
		if ((door == null) || (door.getStatus() == ActionCodes.ACTION_Open)) {
			return -1;
		}
		return door.getDirection();
	}
	
	/** 移除門 屍魂塔 */
	public void removeDoor(final L1DoorInstance door) {
		_doorList.remove(door);
	}
	
	/** 創建門 屍魂塔 */
	public void addDoor(final L1DoorInstance door) {
		_doorList.add(door);
	}
}
