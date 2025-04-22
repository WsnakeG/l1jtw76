/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */

package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1WilliamGfxIdOrginal;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public class GfxIdOrginal {

	private static final Log _log = LogFactory.getLog(GfxIdOrginal.class);

	private static GfxIdOrginal _instance;

	private final static HashMap<Integer, L1WilliamGfxIdOrginal> _gfxIdIndex = new HashMap<Integer, L1WilliamGfxIdOrginal>();

	public static GfxIdOrginal get() {
		if (_instance == null) {
			_instance = new GfxIdOrginal();
		}
		return _instance;
	}

	private GfxIdOrginal() {
		loadGfxIdOrginal();
	}

	public void loadGfxIdOrginal() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_gfxid_orginal");
			rs = pstm.executeQuery();
			fillWeaponSkill(rs);
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		_log.info("載入變身外觀特化設定: " + _gfxIdIndex.size() + "(" + timer.get() + "ms)");
	}

	private void fillWeaponSkill(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			final int gfxId = rs.getInt("gfxId"); // 變身編號
			final boolean deadExp = rs.getInt("deadExp") == 0 ? false : true; // 死亡是否不掉落經驗值
			final boolean cancellation = rs.getInt("cancellation") == 0 ? false : true; // 是否相消不會移除變身圖檔
			final byte addStr = rs.getByte("addStr");
			final byte addDex = rs.getByte("addDex");
			final byte addCon = rs.getByte("addCon");
			final byte addInt = rs.getByte("addInt");
			final byte addWis = rs.getByte("addWis");
			final byte addCha = rs.getByte("addCha");
			final int addAc = rs.getInt("addAc");
			final int addMaxHp = rs.getInt("addMaxHp");
			final int addMaxMp = rs.getInt("addMaxMp");
			final int addHpr = rs.getInt("addHpr");
			final int addMpr = rs.getInt("addMpr");
			final int addDmg = rs.getInt("addDmg");
			final int addBowDmg = rs.getInt("addBowDmg");
			final int addHit = rs.getInt("addHit");
			final int addBowHit = rs.getInt("addBowHit");
			final int reduction_dmg = rs.getInt("reduction_dmg");
			final int reduction_magic_dmg = rs.getInt("reduction_magic_dmg");
			final int addMr = rs.getInt("addMr");
			final int addSp = rs.getInt("addSp");
			final int addFire = rs.getInt("addFire");
			final int addWind = rs.getInt("addWind");
			final int addEarth = rs.getInt("addEarth");
			final int addWater = rs.getInt("addWater");

			final L1WilliamGfxIdOrginal gfxIdOrginal = new L1WilliamGfxIdOrginal(gfxId, deadExp, cancellation,
					addStr, addDex, addCon, addInt, addWis, addCha, addAc, addMaxHp, addMaxMp, addHpr, addMpr,
					addDmg, addBowDmg, addHit, addBowHit, reduction_dmg, reduction_magic_dmg, addMr, addSp,
					addFire, addWind, addEarth, addWater);
			_gfxIdIndex.put(gfxId, gfxIdOrginal);
		}
	}

	public L1WilliamGfxIdOrginal getTemplate(final int gfxId) {
		return _gfxIdIndex.get(gfxId);
	}

	public L1WilliamGfxIdOrginal[] getGfxIdList() {
		return _gfxIdIndex.values().toArray(new L1WilliamGfxIdOrginal[_gfxIdIndex.size()]);
	}
}
