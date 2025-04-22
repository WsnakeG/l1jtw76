package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1AttrWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 屬性武器系統(DB自製)
 * 
 * @author terry0412
 */
public final class ExtraAttrWeaponTable {

	private static final Log _log = LogFactory.getLog(ExtraAttrWeaponTable.class);

	private static ExtraAttrWeaponTable _instance;

	private static final Map<Integer, L1AttrWeapon> _attrList = new LinkedHashMap<Integer, L1AttrWeapon>();

	public static ExtraAttrWeaponTable getInstance() {
		if (_instance == null) {
			_instance = new ExtraAttrWeaponTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_attr_weapon ORDER BY id");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int id = rs.getInt("id");
				final String name = rs.getString("name");
				final int stage = rs.getInt("stage");
				final int chance = rs.getInt("chance");
				final int probability = rs.getInt("probability");

				final double type_bind = rs.getDouble("type_bind");
				final double type_drain_hp = rs.getDouble("type_drain_hp");
				final int type_drain_mp = rs.getInt("type_drain_mp");
				final double type_dmgup = rs.getDouble("type_dmgup");
				final int type_range = rs.getInt("type_range");
				final int type_range_dmg = rs.getInt("type_range_dmg");
				final int type_light_dmg = rs.getInt("type_light_dmg");
				final int type_ice_dmg = rs.getInt("type_ice_dmg");
				final boolean type_skill_1 = rs.getBoolean("type_skill_1");
				final boolean type_skill_2 = rs.getBoolean("type_skill_2");
				final boolean type_skill_3 = rs.getBoolean("type_skill_3");
				final boolean type_skill_4 = rs.getBoolean("type_skill_4");
				final double type_skill_time = rs.getDouble("type_skill_time");
				// 被變身後的polyid (設置多樣隨機一種)
				final String temp_poly_list = rs.getString("type_poly_list");
				String[] type_poly_list = null;
				if (temp_poly_list != null && !temp_poly_list.isEmpty()) {
					// 把空白符號去除 並 用逗號分割出來
					type_poly_list = temp_poly_list.replace(" ", "").split(",");
				}
				final boolean type_remove_weapon = rs.getBoolean("type_remove_weapon");
				final boolean type_remove_doll = rs.getBoolean("type_remove_doll");
				final int type_remove_armor = rs.getInt("type_remove_armor");

				final L1AttrWeapon attrWeapon = new L1AttrWeapon(name, stage, chance, probability, type_bind,
						type_drain_hp, type_drain_mp, type_dmgup, type_range, type_range_dmg, type_light_dmg,
						type_ice_dmg, type_skill_1, type_skill_2, type_skill_3, type_skill_4, type_skill_time,
						type_poly_list, type_remove_weapon, type_remove_doll, type_remove_armor);
				final int index = id * 100 + stage;
				_attrList.put(index, attrWeapon);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		_log.info("載入屬性武器資料數量: " + _attrList.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 取出指定索引碼的L1AttrWeapon
	 * 
	 * @param id
	 * @param stage
	 * @return
	 */
	public final L1AttrWeapon get(final int id, final int stage) {
		final int index = id * 100 + stage;
		return _attrList.get(index);
	}
}
