package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ItemPowerTable;
import com.lineage.server.datatables.storage.CharItemsPowerStorage;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldItem;

/**
 * 人物古文字物品資料
 * 
 * @author dexc
 */
public class CharItemsPowerTable implements CharItemsPowerStorage {

	private static final Log _log = LogFactory.getLog(CharItemsPowerTable.class);

	private static final CopyOnWriteArrayList<Integer> _objList = new CopyOnWriteArrayList<Integer>();

	/**
	 * 資料預先載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		int i = 0;
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_item_power`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int item_obj_id = rs.getInt("item_obj_id");
				final int item_power = rs.getInt("item_power");
				final Timestamp date_time = rs.getTimestamp("date_time");

				L1ItemPower_name power = ItemPowerTable.POWER_NAME.get(item_power);
				if (power == null) {
					power = new L1ItemPower_name();
				}
				power.set_date_time(date_time);

				addValue(item_obj_id, power);
				i++;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入人物古文字物件清單資料數量: " + i + "(" + timer.get() + "ms)");
	}

	/**
	 * 初始化建立資料
	 * 
	 * @param item_obj_id
	 * @param value
	 */
	private static void addValue(final int item_obj_id, final L1ItemPower_name power) {
		final L1ItemInstance item = WorldItem.get().getItem(item_obj_id);
		boolean isError = true;
		if (item != null) {
			if (item.get_power_name() == null) {
				_objList.add(new Integer(item_obj_id));
				item.set_power_name(power);
			}
			isError = false;
		}

		if (isError) {
			errorItem(item_obj_id);
		}
	}

	/**
	 * 刪除 錯誤/遺失 物品資料
	 * 
	 * @param objid
	 */
	private static void errorItem(final int item_obj_id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `character_item_power` WHERE `item_obj_id`=?");
			pstm.setInt(1, item_obj_id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 增加古文字物品資料
	 * 
	 * @param item_obj_id
	 * @param power
	 * @throws Exception
	 */
	@Override
	public void storeItem(final int item_obj_id, final L1ItemPower_name power) throws Exception {
		if (_objList.contains(new Integer(item_obj_id))) {
			return;
		}
		_objList.add(new Integer(item_obj_id));
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO `character_item_power` SET `item_obj_id`=?,"
					+ "`item_power`=?,`note`=?,`date_time`=?");
			int i = 0;
			pstm.setInt(++i, item_obj_id);
			pstm.setInt(++i, power.get_power_id());
			pstm.setString(++i, power.get_power_name());
			pstm.setTimestamp(++i, power.get_date_time());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public void delItem(final int item_obj_id) {
		if (_objList.contains(new Integer(item_obj_id))) {
			_objList.remove(new Integer(item_obj_id));
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM `character_item_power`" + " WHERE `item_obj_id`=?");
			pstm.setInt(1, item_obj_id);
			pstm.execute();
		} catch (final java.lang.ArrayIndexOutOfBoundsException e) {
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新凹槽資料
	 * 
	 * @param item_obj_id
	 * @param power
	 */
	@Override
	public void updateItem(final int item_obj_id, final L1ItemPower_name power) {
		Connection co = null;
		PreparedStatement pm = null;
		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement("UPDATE `character_item_power` SET `date_time`=? WHERE `item_obj_id`=?");

			int i = 0;
			pm.setTimestamp(++i, power.get_date_time());
			pm.setInt(++i, item_obj_id);
			pm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
	}
}
