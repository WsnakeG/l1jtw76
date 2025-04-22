package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1Vip;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * VIP能力資料
 * 
 * @author simlin
 */
public class VipSetsTable {

	private static final Log _log = LogFactory.getLog(VipSetsTable.class);

	private static VipSetsTable _instance;

	public static final HashMap<Integer, L1Vip> _list_vip = new HashMap<Integer, L1Vip>();

	public static VipSetsTable get() {
		if (_instance == null) {
			_instance = new VipSetsTable();
		}
		return _instance;
	}

	private VipSetsTable() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement pm = null;
		ResultSet rs = null;

		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement("SELECT * FROM `server_vip_sets`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int viplevel = rs.getInt("vip_level");

				if (_list_vip.get(viplevel) == null) {
					final L1Vip vip = new L1Vip();
					vip.set_add_hp(rs.getInt("add_hp"));
					vip.set_add_mp(rs.getInt("add_mp"));
					vip.set_add_hpr(rs.getInt("add_hpr"));
					vip.set_add_mpr(rs.getInt("add_mpr"));
					vip.set_add_dmg(rs.getInt("add_dmg"));
					vip.set_add_bowdmg(rs.getInt("add_bow_dmg"));
					vip.set_add_hit(rs.getInt("add_hit"));
					vip.set_add_bowhit(rs.getInt("add_bow_hit"));
					vip.set_add_sp(rs.getInt("add_sp"));
					vip.set_add_mr(rs.getInt("add_mr"));
					_list_vip.put(viplevel, vip);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
		_log.info("載入VIP能力資料數量: " + _list_vip.size() + "(" + timer.get() + "ms)");
	}
}
