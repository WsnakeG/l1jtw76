package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 武器魔法DIY系統
 * 
 * @author terry0412
 */
public final class ExtraMagicWeaponTable {

	private static final Log _log = LogFactory.getLog(ExtraMagicWeaponTable.class);

	private static final Map<Integer, L1MagicWeapon> _magicList = new HashMap<Integer, L1MagicWeapon>();

	private static ExtraMagicWeaponTable _instance;

	public static ExtraMagicWeaponTable getInstance() {
		if (_instance == null) {
			_instance = new ExtraMagicWeaponTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM extra_weapon_skill ORDER BY item_id");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int item_id = rs.getInt("item_id");
				final String skill_name = rs.getString("skill_name");
				final int success_random = rs.getInt("success_random");
				final int max_use_time = rs.getInt("max_use_time");
				final String success_msg = rs.getString("success_msg");
				final String failure_msg = rs.getString("failure_msg");
				final int probability = rs.getInt("probability");
				final boolean isLongRange = rs.getBoolean("isLongRange");
				final int fixDamage = rs.getInt("fixDamage");
				final int randomDamage = rs.getInt("randomDamage");
				final double doubleDmgValue = rs.getInt("doubleDmgValue");
				final int gfxId = rs.getInt("gfxId");
				final boolean gfxIdTarget = rs.getBoolean("gfxIdTarget");
				String gfxIdOtherLocStr = rs.getString("gfxIdOtherLoc");
				List<int[]> gfxIdOtherLoc = null;

				if ((gfxIdOtherLocStr != null) && !gfxIdOtherLocStr.isEmpty()) {
					gfxIdOtherLoc = new ArrayList<int[]>();
					gfxIdOtherLocStr = gfxIdOtherLocStr.replace(" ", "");

					for (final String value : gfxIdOtherLocStr.split(",")) {
						final String[] value2 = value.split("/");
						gfxIdOtherLoc
								.add(new int[] { Integer.parseInt(value2[0]), Integer.parseInt(value2[1]) });
					}
				}

				final int area = rs.getInt("area");
				final boolean arrowType = rs.getBoolean("arrowType");
				final int effectId = rs.getInt("effectId");
				final int effectTime = rs.getInt("effectTime");
				final int attr = rs.getInt("attr");
				final int hpAbsorb = rs.getInt("hpAbsorb");
				final int mpAbsorb = rs.getInt("mpAbsorb");
				final int steps = rs.getInt("steps");
				final L1MagicWeapon magicStone = new L1MagicWeapon(item_id, skill_name, success_random,
						max_use_time, success_msg, failure_msg, probability, isLongRange, fixDamage,
						randomDamage, doubleDmgValue, gfxId, gfxIdTarget, gfxIdOtherLoc, area, arrowType,
						effectId, effectTime, attr, hpAbsorb, mpAbsorb,steps);
				final int index = item_id * 100 + steps;
				_magicList.put(index, magicStone);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入武器魔法DIY資料數量: " + _magicList.size() + "(" + timer.get() + "ms)");
	}

	public final L1MagicWeapon get(final int id,final int steps) {
		final int index = id * 100 + steps;
		return _magicList.get(index);
	}
}
