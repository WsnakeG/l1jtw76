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
package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.storage.ClanAllianceStorage;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Clan;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.world.WorldClan;

/**
 * 血盟同盟紀錄
 * 
 * @author terry0412
 */
public final class ClanAllianceTable implements ClanAllianceStorage {

	private static final Log _log = LogFactory.getLog(ClanAllianceTable.class);

	// 全部同盟資料
	private final HashMap<Integer, L1Alliance> _allianceList = new HashMap<Integer, L1Alliance>();

	/**
	 * 初始化載入
	 */
	@Override
	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_alliance ORDER BY order_id");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int order_id = rs.getInt("order_id");
				final int alliance_id1 = rs.getInt("alliance_id1");
				final int alliance_id2 = rs.getInt("alliance_id2");
				final int alliance_id3 = rs.getInt("alliance_id3");
				final int alliance_id4 = rs.getInt("alliance_id4");

				// 搜索同盟資料 (資料只儲存四個)
				final ArrayList<L1Clan> totalList = new ArrayList<L1Clan>(4);

				// 搜尋迴圈
				for (final L1Clan clan : WorldClan.get().getAllClans()) {
					if ((clan.getClanId() == alliance_id1) || (clan.getClanId() == alliance_id2)
							|| (clan.getClanId() == alliance_id3) || (clan.getClanId() == alliance_id4)) {
						totalList.add(clan);
					}
				}

				// 締結數量小於2
				if (totalList.size() < 2) {
					deleteAlliance(order_id);
					continue;
				}

				final L1Alliance l1alliance = new L1Alliance(order_id,
						totalList.toArray(new L1Clan[totalList.size()]));
				_allianceList.put(order_id, l1alliance);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入師徒系統資料數量: " + _allianceList.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 建立資料
	 * 
	 * @param alliance
	 */
	@Override
	public final void insertAlliance(final L1Alliance alliance) {
		// 同盟締結數量小於最低數量2
		if (alliance.getTotalList().size() < 2) {
			return;
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO character_alliance SET order_id=?, alliance_id1=?, alliance_id2=?");
			pstm.setInt(1, alliance.getOrderId());
			pstm.setInt(2, alliance.getTotalList().get(0).getClanId());
			pstm.setInt(3, alliance.getTotalList().get(1).getClanId());
			pstm.execute();
			// 加入列表
			_allianceList.put(alliance.getOrderId(), alliance);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新資料
	 * 
	 * @param order_id
	 * @param totalList
	 */
	@Override
	public final void updateAlliance(final int order_id, final ArrayList<L1Clan> totalList) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE character_alliance SET alliance_id1=?, alliance_id2=?, alliance_id3=?, alliance_id4=? WHERE order_id=?");
			pstm.setInt(1, totalList.size() > 0 ? totalList.get(0).getClanId() : 0);
			pstm.setInt(2, totalList.size() > 1 ? totalList.get(1).getClanId() : 0);
			pstm.setInt(3, totalList.size() > 2 ? totalList.get(2).getClanId() : 0);
			pstm.setInt(4, totalList.size() > 3 ? totalList.get(3).getClanId() : 0);
			pstm.setInt(5, order_id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 刪除資料
	 * 
	 * @param order_id
	 */
	@Override
	public final void deleteAlliance(final int order_id) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM character_alliance WHERE order_id=?");
			pstm.setInt(1, order_id);
			pstm.execute();
			// 移除列表
			_allianceList.remove(order_id);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 取得指定同盟資料
	 * 
	 * @param clan_id
	 * @return
	 */
	@Override
	public final L1Alliance getAlliance(final int clan_id) {
		// 巢狀迴圈...
		for (final L1Alliance alliance : _allianceList.values()) {
			for (final L1Clan clan : alliance.getTotalList()) {
				if (clan.getClanId() == clan_id) {
					return alliance;
				}
			}
		}
		return null;
	}
}
