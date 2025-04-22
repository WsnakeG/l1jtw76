package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.ExtraMagicWeaponTable;
import com.lineage.server.datatables.storage.CharWeaponStorage;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 物品使用期限
 * 
 * @author dexc
 */
public class CharWeaponTimeTable implements CharWeaponStorage {

	private static final Log _log = LogFactory.getLog(CharBookTable.class);

	// private static final Map<Integer, Timestamp> _timeMap = new
	// HashMap<Integer, Timestamp>();

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int size = 0;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_weapon_time`");
			rs = ps.executeQuery();

			// 目前時間 by terry0412
			final Timestamp ts = new Timestamp(System.currentTimeMillis());

			while (rs.next()) {
				final int itemr_obj_id = rs.getInt("itemr_obj_id");
				final Timestamp usertime = rs.getTimestamp("usertime");
				final int magic_weapon = rs.getInt("magic_weapon");
				final int steps = rs.getInt("steps");
				if ((usertime != null) && usertime.before(ts)) {
					// 已經超過有效期限 ,移除魔法效果
					delete(itemr_obj_id);

				} else {
					// 無有效期限或在有效期限內,附加魔法效果
					addValue(itemr_obj_id, usertime, magic_weapon,steps);
					size++;
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入武器魔法使用期限資料數量: " + size + "(" + timer.get() + "ms)");
	}

	public static int get_steps(final int itemobjid) {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int thesteps = -1;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_weapon_time`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int itemr_obj_id = rs.getInt("itemr_obj_id");
				final int steps = rs.getInt("steps");
				if(itemr_obj_id==itemobjid){
					thesteps=steps;
					break;
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		return thesteps;
	}
	
	/**
	 * 初始化建立資料
	 * 
	 * @param itemr_obj_id
	 * @param usertime
	 * @param magic_weapon
	 */
	private static void addValue(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon, final int steps) {
		final L1Object obj = World.get().findObject(itemr_obj_id);
		boolean isError = true;
		if (obj != null) {
			if (obj instanceof L1ItemInstance) {
				final L1ItemInstance item = (L1ItemInstance) obj;
				item.set_time(usertime);
				if (magic_weapon > 0) {
					final L1MagicWeapon magicWeapon = ExtraMagicWeaponTable.getInstance().get(magic_weapon,steps);
					item.set_magic_weapon(magicWeapon);
				}
				isError = false;
			}
		}

		if (isError) {
			delete(itemr_obj_id);
		}
	}

	/**
	 * 刪除遺失物品紀錄資料
	 * 
	 * @param objid
	 */
	public static void delete(final int objid) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_weapon_time` WHERE `itemr_obj_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);

		}
	}

	/**
	 * 增加物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	@Override
	public void addTime(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement(
					"INSERT INTO `character_weapon_time` SET `itemr_obj_id`=?,`usertime`=?,`magic_weapon`=?,`steps`=?");

			int i = 0;
			ps.setInt(++i, itemr_obj_id);
			ps.setTimestamp(++i, usertime);
			ps.setInt(++i, magic_weapon);
			ps.setInt(++i, 0);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
	
	public static void addTimeUP(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon, final int steps) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement(
					"INSERT INTO `character_weapon_time` SET `itemr_obj_id`=?,`usertime`=?,`magic_weapon`=?,`steps`=?");

			int i = 0;
			ps.setInt(++i, itemr_obj_id);
			ps.setTimestamp(++i, usertime);
			ps.setInt(++i, magic_weapon);
			ps.setInt(++i, steps);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}

	/**
	 * 更新物品使用期限記錄
	 * 
	 * @param objid
	 * @return
	 */
	@Override
	public void updateTime(final int itemr_obj_id, final Timestamp usertime, final int magic_weapon, final int steps, final int same) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("UPDATE `character_weapon_time` SET `usertime`=?,`magic_weapon`=?,`steps`=?"
					+ " WHERE `itemr_obj_id`=?");
			int i = 0;
			ps.setTimestamp(++i, usertime);
			ps.setInt(++i, magic_weapon);
			if(same==0)
			ps.setInt(++i, 0);
			if(same==1)
			ps.setInt(++i, steps);
			ps.setInt(++i, itemr_obj_id);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}
}
