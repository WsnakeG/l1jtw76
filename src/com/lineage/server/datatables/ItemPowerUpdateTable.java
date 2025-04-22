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
import com.lineage.server.templates.L1ItemPowerUpdate;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 特殊物品升級資料
 * 
 * @author loli
 */
public class ItemPowerUpdateTable {

	private static final Log _log = LogFactory.getLog(ItemPowerUpdateTable.class);

	private static Map<String, L1ItemPowerUpdate> _updateMap = new HashMap<String, L1ItemPowerUpdate>();

	private static ItemPowerUpdateTable _instance;

	public static ItemPowerUpdateTable get() {
		if (_instance == null) {
			_instance = new ItemPowerUpdateTable();
		}
		return _instance;
	}

	public void reload() {
		_updateMap.clear();
		load();
	}

	/**
	 * 初始化載入
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_item_power_update`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemid = rs.getInt("itemid");
				if (ItemTable.get().getTemplate(itemid) == null) {
					_log.error("特殊物品升級資料錯誤: 沒有這個編號的道具:" + itemid);
					delete(itemid);
					continue;
				}
				final int nedid = rs.getInt("nedid");
				final int type_id = rs.getInt("type_id");
				final int order_id = rs.getInt("order_id");
				final int mode = rs.getInt("mode");
				final int random = rs.getInt("random");

				final String key = itemid + "/" + nedid;
				L1ItemPowerUpdate value = _updateMap.get(key);
				if (value == null) {
					value = new L1ItemPowerUpdate();
					value.set_itemid(itemid);
					value.set_nedid(nedid);
					value.set_type_id(type_id);
					value.set_order_id(order_id);
					value.set_mode(mode);
					value.set_random(random);
				} else {
					_log.error("特殊物品升級資料錯誤: 轉換重複:" + itemid);
				}

				_updateMap.put(key, value);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入物品升級資料數量: " + _updateMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除錯誤資料
	 * 
	 * @param itemid
	 */
	public static void delete(final int itemid) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `server_item_power_update` WHERE `itemid`=?");
			ps.setInt(1, itemid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 資訊
	 * 
	 * @param key
	 * @return
	 */
	public Map<Integer, L1ItemPowerUpdate> get_type_id(final String key) {
		final Map<Integer, L1ItemPowerUpdate> updateMap = new HashMap<Integer, L1ItemPowerUpdate>();
		final L1ItemPowerUpdate tmp = _updateMap.get(key);
		if (tmp != null) {
			final int type_id = tmp.get_type_id();
			for (final L1ItemPowerUpdate value : _updateMap.values()) {
				if (value.get_type_id() == type_id) {
					updateMap.put(value.get_order_id(), value);
				}
			}
		}
		return updateMap;
	}

	/**
	 * L1ItemPowerUpdate 資訊
	 * 
	 * @param key
	 * @return
	 */
	public L1ItemPowerUpdate get(final String key) {
		return _updateMap.get(key);
	}

	/**
	 * Map<Integer, L1ItemPowerUpdate> 資訊
	 * 
	 * @return
	 */
	public Map<String, L1ItemPowerUpdate> map() {
		return _updateMap;
	}
}
