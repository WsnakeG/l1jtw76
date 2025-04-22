package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Fishing;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 漁獲資料暫存
 * 
 * @author dexc
 */
public class FishingTable {

	public static final Log _log = LogFactory.getLog(FishingTable.class);

	private final HashMap<Integer, ArrayList<L1Fishing>> _fishingMap = new HashMap<Integer, ArrayList<L1Fishing>>();

	private static Random _random = new Random();

	private static FishingTable _instance;

	public static FishingTable get() {
		if (_instance == null) {
			_instance = new FishingTable();
		}
		return _instance;
	}

	private FishingTable() {
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `server_fishing`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int pole = rs.getInt("pole");
				final int key = rs.getInt("itemid");
				final int randomint = rs.getInt("randomint");
				final int random = rs.getInt("random");
				final int count = rs.getInt("count");

				if (ItemTable.get().getTemplate(key) == null) {
					_log.error("漁獲資料錯誤: 沒有這個編號的道具:" + key);
					delete(key);
					continue;
				}
				if (count > 0) {
					final L1Fishing value = new L1Fishing();
					value.set_itemid(key);
					value.set_randomint(randomint);
					value.set_random(random);
					value.set_count(count);

					ArrayList<L1Fishing> fish = _fishingMap.get(pole);
					if (fish == null) {
						fish = new ArrayList<L1Fishing>();
						fish.add(value);
						_fishingMap.put(pole, fish);
					} else {
						fish.add(value);
					}
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入漁獲資料數量: " + _fishingMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除錯誤資料
	 * 
	 * @param clan_id
	 */
	private static void delete(final int item_id) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `server_fishing` WHERE `itemid`=?");
			ps.setInt(1, item_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	public L1Fishing get_item(final int pole) {
		try {
			final Object[] objs = _fishingMap.get(pole).toArray();
			final Object obj = objs[_random.nextInt(objs.length)];
			final L1Fishing fishing = (L1Fishing) obj;
			return fishing;

		} catch (final Exception e) {
		}
		return null;
	}
}