package com.lineage.server.datatables;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 地圖切換點設置
 * 
 * @author dexc
 */
public class DungeonTable {

	private static final Log _log = LogFactory.getLog(DungeonTable.class);

	private static DungeonTable _instance = null;

	private static Map<String, NewDungeon> _dungeonMap = new HashMap<String, NewDungeon>();

	private enum DungeonType {
		NONE, SHIP_FOR_FI, SHIP_FOR_HEINE, SHIP_FOR_PI, SHIP_FOR_HIDDENDOCK, SHIP_FOR_GLUDIN, SHIP_FOR_TI
	};

	public static DungeonTable get() {
		if (_instance == null) {
			_instance = new DungeonTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			cn = DatabaseFactory.get().getConnection();

			ps = cn.prepareStatement("SELECT * FROM `dungeon`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int srcMapId = rs.getInt("src_mapid");
				final int srcX = rs.getInt("src_x");
				final int srcY = rs.getInt("src_y");
				final String key = new StringBuilder().append(srcMapId).append(srcX).append(srcY).toString();
				final int newX = rs.getInt("new_x");
				final int newY = rs.getInt("new_y");
				final int newMapId = rs.getInt("new_mapid");
				final int heading = rs.getInt("new_heading");
				DungeonType dungeonType = DungeonType.NONE;
				if ((((srcX == 33423) || (srcX == 33424) || (srcX == 33425) || (srcX == 33426))
						&& (srcY == 33502) && (srcMapId == 4 // ハイネ船着場->FI行きの船
						)) || (((srcX == 32733) || (srcX == 32734) || (srcX == 32735) || (srcX == 32736)) && (srcY == 32794) && (srcMapId == 83))) { // FI行きの船->ハイネ船着場
					dungeonType = DungeonType.SHIP_FOR_FI;
				} else if ((((srcX == 32935) || (srcX == 32936) || (srcX == 32937)) && (srcY == 33058)
						&& (srcMapId == 70 // FI船着場->ハイネ行きの船
						)) || (((srcX == 32732) || (srcX == 32733) || (srcX == 32734) || (srcX == 32735)) && (srcY == 32796) && (srcMapId == 84))) { // ハイネ行きの船->FI船着場
					dungeonType = DungeonType.SHIP_FOR_HEINE;
				} else if ((((srcX == 32750) || (srcX == 32751) || (srcX == 32752)) && (srcY == 32874)
						&& (srcMapId == 445 // 隠された船着場->海賊島行きの船
						)) || (((srcX == 32731) || (srcX == 32732) || (srcX == 32733)) && (srcY == 32796) && (srcMapId == 447))) { // 海賊島行きの船->隠された船着場
					dungeonType = DungeonType.SHIP_FOR_PI;
				} else if ((((srcX == 32296) || (srcX == 32297) || (srcX == 32298)) && (srcY == 33087)
						&& (srcMapId == 440 // 海賊島船着場->隠された船着場行きの船
						)) || (((srcX == 32735) || (srcX == 32736) || (srcX == 32737)) && (srcY == 32794) && (srcMapId == 446))) { // 隠された船着場行きの船->海賊島船着場
					dungeonType = DungeonType.SHIP_FOR_HIDDENDOCK;
				} else if ((((srcX == 32630) || (srcX == 32631) || (srcX == 32632)) && (srcY == 32983)
						&& (srcMapId == 0 // TalkingIsland->TalkingIslandShiptoAdenMainland
						)) || (((srcX == 32733) || (srcX == 32734) || (srcX == 32735)) && (srcY == 32796) && (srcMapId == 5))) { // TalkingIslandShiptoAdenMainland->TalkingIsland
					dungeonType = DungeonType.SHIP_FOR_GLUDIN;
				} else if ((((srcX == 32540) || (srcX == 32542) || (srcX == 32543) || (srcX == 32544)
						|| (srcX == 32545)) && (srcY == 32728) && (srcMapId == 4 // AdenMainland->AdenMainlandShiptoTalkingIsland
				)) || (((srcX == 32734) || (srcX == 32735) || (srcX == 32736) || (srcX == 32737))
						&& (srcY == 32794) && (srcMapId == 6))) { // AdenMainlandShiptoTalkingIsland->AdenMainland
					dungeonType = DungeonType.SHIP_FOR_TI;
				}
				final NewDungeon newDungeon = new NewDungeon(newX, newY, (short) newMapId, heading,
						dungeonType);
				if (_dungeonMap.containsKey(key)) {
					_log.error("相同SRC傳送座標(" + key + ")");
				}
				_dungeonMap.put(key, newDungeon);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入地圖切換點設置數量: " + _dungeonMap.size() + "(" + timer.get() + "ms)");
	}

	private static AtomicInteger _nextId = new AtomicInteger(50000);

	private static class NewDungeon {
		int _id;
		int _newX;
		int _newY;
		short _newMapId;
		int _heading;
		DungeonType _dungeonType;

		private NewDungeon(final int newX, final int newY, final short newMapId, final int heading,
				final DungeonType dungeonType) {
			_id = _nextId.incrementAndGet();
			_newX = newX;
			_newY = newY;
			_newMapId = newMapId;
			_heading = heading;
			_dungeonType = dungeonType;

		}
	}

	/**
	 * 執行座標移動
	 * 
	 * @param locX
	 * @param locY
	 * @param mapId
	 * @param pc
	 * @return
	 */
	public boolean dg(final int locX, final int locY, final int mapId, final L1PcInstance pc) {
		final int servertime = L1GameTimeClock.getInstance().currentTime().getSeconds();
		final int nowtime = servertime % 86400;
		final String key = new StringBuilder().append(mapId).append(locX).append(locY).toString();

		if (_dungeonMap.containsKey(key)) {
			final NewDungeon newDungeon = _dungeonMap.get(key);
			final DungeonType dungeonType = newDungeon._dungeonType;
			boolean teleportable = false;

			if (dungeonType == DungeonType.NONE) {
				teleportable = true;

			} else {
				if (((nowtime >= (15 * 360)) && (nowtime < (25 * 360 // 01:30~02:30
				))) || ((nowtime >= (45 * 360)) && (nowtime < (55 * 360 // 04:30~05:30
				))) || ((nowtime >= (75 * 360)) && (nowtime < (85 * 360 // 07:30~08:30
				))) || ((nowtime >= (105 * 360)) && (nowtime < (115 * 360 // 10:30~11:30
				))) || ((nowtime >= (135 * 360)) && (nowtime < (145 * 360 // 13:30~14:30
				))) || ((nowtime >= (165 * 360)) && (nowtime < (175 * 360 // 16:30~17:30
				))) || ((nowtime >= (195 * 360)) && (nowtime < (205 * 360 // 19:30~20:30
				))) || ((nowtime >= (225 * 360)) && (nowtime < (235 * 360)))) { // 22:30~23:30
					if ((pc.getInventory().checkItem(40299, 1) // 往古魯丁的船票
							&& (dungeonType == DungeonType.SHIP_FOR_GLUDIN)) // TalkingIslandShiptoAdenMainland
							|| (pc.getInventory().checkItem(40301, 1) // 海音港口船票
									&& (dungeonType == DungeonType.SHIP_FOR_HEINE)) // AdenMainlandShiptoForgottenIsland
							|| (pc.getInventory().checkItem(40302, 1) // 海賊島船票
									&& (dungeonType == DungeonType.SHIP_FOR_PI))) { // ShipPirateislandtoHiddendock
						teleportable = true;
					}
				} else if (((nowtime >= 0) && (nowtime < (10 * 360 // 00:00~01:00
				))) || ((nowtime >= (30 * 360)) && (nowtime < (40 * 360 // 03:00~04:00
				))) || ((nowtime >= (60 * 360)) && (nowtime < (70 * 360 // 06:00~07:00
				))) || ((nowtime >= (90 * 360)) && (nowtime < (100 * 360 // 09:00~10:00
				))) || ((nowtime >= (120 * 360)) && (nowtime < (130 * 360 // 12:00~13:00
				))) || ((nowtime >= (150 * 360)) && (nowtime < (160 * 360 // 15:00~16:00
				))) || ((nowtime >= (180 * 360)) && (nowtime < (190 * 360 // 18:00~19:00
				))) || ((nowtime >= (210 * 360)) && (nowtime < (220 * 360)))) { // 21:00~22:00
					if ((pc.getInventory().checkItem(40298, 1) // 往說話之島的船票
							&& (dungeonType == DungeonType.SHIP_FOR_TI)) // AdenMainlandShiptoTalkingIsland
							|| (pc.getInventory().checkItem(40300, 1) // 遺忘之島船票
									&& (dungeonType == DungeonType.SHIP_FOR_FI)) // ForgottenIslandShiptoAdenMainland
							|| (pc.getInventory().checkItem(40303, 1) // 隱藏港口船票
									&& (dungeonType == DungeonType.SHIP_FOR_HIDDENDOCK))) { // ShipHiddendocktoPirateisland
						teleportable = true;
					}
				}
			}

			if (teleportable) {
				final int id = newDungeon._id;
				final short newMap = newDungeon._newMapId;
				final int newX = newDungeon._newX;
				final int newY = newDungeon._newY;
				final int heading = newDungeon._heading;

				// 2秒間は無敵（アブソルートバリア状態）にする。
				pc.setSkillEffect(ABSOLUTE_BARRIER, 2000);
				pc.stopHpRegeneration();
				pc.stopMpRegeneration();

				teleport(pc, id, newX, newY, newMap, heading, false);
				return true;
			}
		}
		return false;
	}

	/**
	 * 執行傳送
	 * 
	 * @param pc
	 * @param newX
	 * @param newY
	 * @param newMap
	 * @param heading
	 * @param b
	 */
	private void teleport(final L1PcInstance pc, final int id, final int newX, final int newY,
			final short newMap, final int heading, final boolean b) {
		L1Teleport.teleport(pc, newX, newY, newMap, heading, b);
	}
}
