package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.storage.FurnitureSpawnStorage;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.templates.L1Furniture;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 家具資料
 * 
 * @author dexc
 */
public class FurnitureSpawnTable implements FurnitureSpawnStorage {

	private static final Log _log = LogFactory.getLog(FurnitureSpawnTable.class);

	private static final Map<Integer, L1Furniture> _furnitureList = new HashMap<Integer, L1Furniture>();

	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `spawnlist_furniture`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int npcid = rs.getInt("npcid");

				final L1Npc npc = NpcTable.get().getTemplate(npcid);

				if (npc != null) {
					final int item_obj_id = rs.getInt("item_obj_id");
					final int locx = rs.getInt("locx");
					final int locy = rs.getInt("locy");
					final short mapid = rs.getShort("mapid");

					// 搜尋對應道具是否存在
					if (World.get().findObject(item_obj_id) != null) {
						final L1Furniture value = new L1Furniture();

						value.set_npcid(npcid);
						value.set_item_obj_id(item_obj_id);
						value.set_locx(locx);
						value.set_locy(locy);
						value.set_mapid(mapid);

						_furnitureList.put(item_obj_id, value);

					} else {
						delFurniture(item_obj_id);
					}
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);

			spawnFurniture();
		}
		_log.info("載入家具位置資料數量: " + _furnitureList.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 召喚
	 */
	private static void spawnFurniture() {
		for (final Integer key : _furnitureList.keySet()) {
			final L1Furniture value = _furnitureList.get(key);
			L1SpawnUtil.spawn(value);
		}
	}

	/**
	 * 刪除錯誤物品資料
	 * 
	 * @param objid
	 */
	private static void delFurniture(final int objid) {
		Connection cn = null;
		PreparedStatement pm = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			pm = cn.prepareStatement("DELETE FROM `spawnlist_furniture` WHERE `item_obj_id`=?");
			pm.setInt(1, objid);
			pm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 刪除物品資料
	 */
	@Override
	public void deleteFurniture(final L1FurnitureInstance furniture) {
		final int key = furniture.getItemObjId();
		if (_furnitureList.remove(key) != null) {
			delFurniture(key);
		}
	}

	/**
	 * 新建物品資料
	 */
	@Override
	public void insertFurniture(final L1FurnitureInstance furniture) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			final int item_obj_id = furniture.getItemObjId();
			final int npcid = furniture.getNpcTemplate().get_npcId();
			final int locx = furniture.getX();
			final int locy = furniture.getY();
			final short mapid = furniture.getMapId();

			final L1Furniture value = new L1Furniture();

			value.set_npcid(npcid);
			value.set_item_obj_id(item_obj_id);
			value.set_locx(locx);
			value.set_locy(locy);
			value.set_mapid(mapid);

			_furnitureList.put(item_obj_id, value);

			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"INSERT INTO `spawnlist_furniture` SET `item_obj_id`=?,`npcid`=?,`locx`=?,`locy`=?,`mapid`=?");
			ps.setInt(1, item_obj_id);
			ps.setInt(2, npcid);
			ps.setInt(3, locx);
			ps.setInt(4, locy);
			ps.setInt(5, mapid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
