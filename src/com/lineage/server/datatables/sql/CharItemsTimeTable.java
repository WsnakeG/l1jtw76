package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.storage.CharItemsTimeStorage;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.World;

/**
 * 物品使用期限
 * 
 * @author dexc
 */
public class CharItemsTimeTable implements CharItemsTimeStorage {

	private static final Log _log = LogFactory.getLog(CharBookTable.class);

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
			ps = cn.prepareStatement("SELECT * FROM `character_items_time`");
			rs = ps.executeQuery();

			// 目前時間 by terry0412
			final Timestamp ts = new Timestamp(System.currentTimeMillis());

			while (rs.next()) {
				final int itemr_obj_id = rs.getInt("itemr_obj_id");
				final Timestamp usertime = rs.getTimestamp("usertime");

				// 已經超過有效期限 by terry0412
				if (usertime.before(ts)) {
					final L1ItemInstance item = CharItemsReading.get().getUserItem(itemr_obj_id);
					if (item != null) {
						try {
							CharItemsReading.get().deleteItem(item.get_char_objid(), item);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					} else {
						delete(itemr_obj_id);
					}

				} else {
					addValue(itemr_obj_id, usertime);
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
		_log.info("載入物品使用期限資料數量: " + size + "(" + timer.get() + "ms)");
	}

	/**
	 * 初始化建立資料
	 * 
	 * @param itemr_obj_id
	 * @param usertime
	 * @param magic_weapon
	 */
	private static void addValue(final int itemr_obj_id, final Timestamp usertime) {
		final L1Object obj = World.get().findObject(itemr_obj_id);
		boolean isError = true;
		if (obj != null) {
			if (obj instanceof L1ItemInstance) {
				final L1ItemInstance item = (L1ItemInstance) obj;
				item.set_time(usertime);
				if (item.getItem().getType2() == 0) {
					// 目前時間
					final Timestamp ts = new Timestamp(System.currentTimeMillis());
					// 指示此 time 对象是否早于给定的 ts 对象。
					if (usertime.before(ts)) {
						item.set_card_use(2);// 到期

					} else {
						item.set_card_use(1);// 使用中
					}
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
			ps = cn.prepareStatement("DELETE FROM `character_items_time` WHERE `itemr_obj_id`=?");
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
	public void addTime(final int itemr_obj_id, final Timestamp usertime) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("INSERT INTO `character_items_time` SET `itemr_obj_id`=?,`usertime`=?");

			int i = 0;
			ps.setInt(++i, itemr_obj_id);
			ps.setTimestamp(++i, usertime);
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
	public void updateTime(final int itemr_obj_id, final Timestamp usertime) {
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = DatabaseFactory.get().getConnection();
			ps = co.prepareStatement("UPDATE `character_items_time` SET `usertime`=? WHERE `itemr_obj_id`=?");
			int i = 0;
			ps.setTimestamp(++i, usertime);
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
