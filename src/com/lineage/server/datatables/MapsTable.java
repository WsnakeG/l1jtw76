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
import com.lineage.server.templates.MapData;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 地圖設置資料
 * 
 * @author dexc
 */
public final class MapsTable {

	private static final Log _log = LogFactory.getLog(MapsTable.class);

	/*
	 * private class MapData { public int startX = 0; public int endX = 0;
	 * public int startY = 0; public int endY = 0; public double monster_amount
	 * = 1; public double dropRate = 1; public boolean isUnderwater = false;
	 * public boolean markable = false; public boolean teleportable = false;
	 * public boolean escapable = false; public boolean isUseResurrection =
	 * false; public boolean isUsePainwand = false; public boolean
	 * isEnabledDeathPenalty = false; public boolean isTakePets = false; public
	 * boolean isRecallPets = false; public boolean isUsableItem = false; public
	 * boolean isUsableSkill = false; }
	 */

	private static MapsTable _instance;

	/**
	 * Key(mapId) Value(MapData)
	 */
	private static final Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * マップのテレポート可否フラグをデータベースから読み込み、HashMap _mapsに格納する。
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `mapids`");

			for (rs = pstm.executeQuery(); rs.next();) {
				final MapData data = new MapData();
				final int mapId = rs.getInt("mapid");
				data.location = rs.getString("location_name");
				data.mapId = mapId;
				data.startX = rs.getInt("startX");
				data.endX = rs.getInt("endX");
				data.startY = rs.getInt("startY");
				data.endY = rs.getInt("endY");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");
				// 該地圖是否可擺設商店 by terry0412
				data.isUsableShop = rs.getInt("usable_shop");
				data.isArrows = rs.getBoolean("isArrows");
				data.isAutoBot = rs.getBoolean("isAutoBot");

				_maps.put(new Integer(mapId), data);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		_log.info("載入地圖設置資料數量: " + _maps.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * MapsTableのインスタンスを返す。
	 * 
	 * @return MapsTableのインスタンス
	 */
	public static MapsTable get() {
		if (_instance == null) {
			_instance = new MapsTable();
		}
		return _instance;
	}

	/**
	 * 傳回全部地圖資料
	 * 
	 * @return
	 */
	public Map<Integer, MapData> getMaps() {
		return _maps;
	}
	
	/**
	 * 屍魂塔
	 * 
	 * @return
	 */
	public MapData getMapData(final int mapId) {
		final MapData map = _maps.get(mapId);
		return map;
	}
	
	/**
	 * 屍魂塔
	 * 
	 * @return
	 */
	public void setMaps(final int mapId, final MapData mapdata) {
		_maps.put(mapId, mapdata);
	}

	/**
	 * 隨機創怪地圖位置資訊
	 * @param mapId
	 * @return
	 */
	public String getLoaction(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return "";
		}
		return _maps.get(mapId).location;
	}
	
	/**
	 * X開始座標
	 * 
	 * @param mapId 地圖編號
	 * @return X開始座標
	 */
	public int getStartX(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * マップがのX終了座標を返す。
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return X終了座標
	 */
	public int getEndX(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * マップがのY開始座標を返す。
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return Y開始座標
	 */
	public int getStartY(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * マップがのY終了座標を返す。
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return Y終了座標
	 */
	public int getEndY(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * NPC數量
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return モンスター量の倍率
	 */
	public double getMonsterAmount(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * 掉落倍率
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return ドロップ倍率
	 */
	public double getDropRate(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * 水中
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return 水中であればtrue
	 */
	public boolean isUnderwater(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUnderwater;
	}

	/**
	 * 記憶座標
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return ブックマーク可能であればtrue
	 */
	public boolean isMarkable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * 使用傳送
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return 可能であればtrue
	 */
	public boolean isTeleportable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * 使用回捲
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return 可能であればtrue
	 */
	public boolean isEscapable(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * 復活
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return 復活可能であればtrue
	 */
	public boolean isUseResurrection(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * 使用魔杖
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return パインワンド使用可能であればtrue
	 */
	public boolean isUsePainwand(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * 死亡逞罰
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return デスペナルティであればtrue
	 */
	public boolean isEnabledDeathPenalty(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * 攜帶寵物
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return ペット・サモンを連れて行けるならばtrue
	 */
	public boolean isTakePets(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * 召喚寵物
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return ペット・サモンを呼び出せるならばtrue
	 */
	public boolean isRecallPets(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * 使用物品
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return アイテムを使用できるならばtrue
	 */
	public boolean isUsableItem(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * 使用技能
	 * 
	 * @param mapId 調べるマップのマップID
	 * @return スキルを使用できるならばtrue
	 */
	public boolean isUsableSkill(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}

	/**
	 * 該地圖是否可擺設商店 by terry0412
	 * 
	 * @param mapId
	 * @return
	 */
	public int isUsableShop(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).isUsableShop;
	}
	
	/**
	 * 該地圖是否可以用血盟支援令
	 * 
	 * @param mapId
	 * @return
	 */
	public boolean isArrows(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isArrows;
	}
	
	/**
	 * 該地圖是否可以用掛機
	 * 
	 * @param mapId
	 * @return
	 */
	public boolean isAutoBot(final int mapId) {
		final MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isAutoBot;
	}
	
}
