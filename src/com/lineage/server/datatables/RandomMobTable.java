/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.TimeInfo;

/**
 * 隨機生怪資料
 * @author admin
 *
 */
public final class RandomMobTable {
	
	private class Data {
		public int id = 0;
		public String note = "";
		public int mobId = 0;
		public int cont = 0;
		public short mapId[] = {};
		public int timeSecondToDelete = -1;
		public boolean isActive = false;
		public boolean isbroad = false;
		public int time[] = {};
	}

	private static Logger _log = Logger.getLogger(RandomMobTable.class
			.getName());

	private static RandomMobTable _instance;

	private final Map<Integer, Data> _mobs = new HashMap<Integer, Data>();

	private RandomMobTable() {
		loadRandomMobFromDatabase();
	}

	private void loadRandomMobFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_random_mob");
			rs = pstm.executeQuery();
			while (rs.next()) {
				Data data = new Data();
				int id = rs.getInt("id");
				data.id = id;
				data.note = rs.getString("note");
				String temp[] = rs.getString("mapId").split(",");
				short i[] = new short[temp.length];
				int loop = 0;
				for (String s : temp) {
					i[loop] = (short) Integer.parseInt(s);
					loop++;
				}
				data.mapId = i;
				data.mobId = rs.getInt("mobId");
				data.cont = rs.getInt("cont");
				data.timeSecondToDelete = rs.getInt("timeSecondToKill");
				data.isActive = rs.getBoolean("isActive");
				data.isbroad = rs.getBoolean("broad");
				
				String temp1[] = rs.getString("time").split(",");
				int i1[] = new int[temp1.length];
				int loop1 = 0;
				for (String s : temp1) {
					i1[loop1] = (int) Integer.parseInt(s);
					loop1++;
				}
				data.time = i1;
				
				_mobs.put(new Integer(id), data);
			}
			_log.config("RandomMob " + _mobs.size());
		} catch (SQLException e) {
			e.printStackTrace();
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public void startRandomMob() {
		// 招換
		for (final Data data : _mobs.values()) {
			if (data.isActive) {// 假如有啟動
				String mTime = TimeInfo.time().getNow_YMDHMS(3);
				int mm = Integer.parseInt(mTime);
				boolean _isRandomMob = false;
				for (int time : data.time) {
					if (time == mm) {
						_isRandomMob = true;
					}
				}
				if (_isRandomMob) {
					L1SpawnUtil.spawn(data.id);
				}
			}
		}
	}

	public static RandomMobTable getInstance() {
		if (_instance == null) {
			_instance = new RandomMobTable();
		}
		return _instance;
	}

	public short getRandomMapId(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return 0;
		}
		final int length = _mobs.get(RandomMobId).mapId.length;
		final int rand = RandomArrayList.getInt(length);
		return _mobs.get(RandomMobId).mapId[rand];
	}

	public int getRandomTime(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return 0;
		}
		final int length = _mobs.get(RandomMobId).time.length;
		final int rand = RandomArrayList.getInt(length);
		return _mobs.get(RandomMobId).time[rand];
	}
	
	public int getRandomMapX(final int mapId) {
		final int startX = MapsTable.get().getStartX(mapId);
		final int endX = MapsTable.get().getEndX(mapId);
		final int rand = RandomArrayList.getInt(endX - startX);

		return startX + rand;
	}

	public int getRandomMapY(final int mapId) {
		final int startY = MapsTable.get().getStartY(mapId);
		final int endY = MapsTable.get().getEndY(mapId);
		final int rand = RandomArrayList.getInt(endY - startY);

		return startY + rand;
	}

	public String getName(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return "";
		}
		return _mobs.get(RandomMobId).note;
	}

	public int getMobId(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return 0;
		}

		return _mobs.get(RandomMobId).mobId;
	}

	public int getCont(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return 0;
		}
		return _mobs.get(RandomMobId).cont;
	}

	public int getTimeSecondToDelete(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data == null) {
			return 0;
		}

		return _mobs.get(RandomMobId).timeSecondToDelete;
	}
	
	public boolean isBroad(final int RandomMobId) {
		final Data data = _mobs.get(RandomMobId);
		if (data.isbroad) {
			return true;
		}
		return false;
	}
}
