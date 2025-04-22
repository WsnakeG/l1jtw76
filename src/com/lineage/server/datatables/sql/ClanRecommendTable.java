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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.storage.ClanRecommendStorage;
import com.lineage.server.templates.L1ClanRecommend;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

public final class ClanRecommendTable implements ClanRecommendStorage {

	private static final Log _log = LogFactory.getLog(ClanRecommendTable.class);

	private final Map<Integer, L1ClanRecommend> _recommendsList = new ConcurrentHashMap<Integer, L1ClanRecommend>();

	private final Map<Integer, CopyOnWriteArrayList<Integer>> _applyList = new ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>>();

	@Override
	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		deleteIllegalData();

		Connection con = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM clan_recommend ORDER BY clan_id");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int clan_id = rs.getInt("clan_id");

				if (ClanReading.get().getTemplate(clan_id) == null) {
					deleteRecommend(clan_id);
					continue;
				}

				final int type_id = rs.getInt("type_id");

				final String type_message = rs.getString("type_message");
				if ((type_message == null) || type_message.isEmpty()) {
					deleteRecommend(clan_id);
					continue;
				}

				final L1ClanRecommend recommend = new L1ClanRecommend(clan_id, type_id, type_message);
				_recommendsList.put(clan_id, recommend);
			}

			pstm2 = con.prepareStatement("SELECT * FROM clan_request_list ORDER BY clan_id");
			rs2 = pstm2.executeQuery();

			while (rs2.next()) {
				final int clan_id = rs2.getInt("clan_id");
				final int applicant_id = rs2.getInt("applicant_id");

				if ((ClanReading.get().getTemplate(clan_id) == null)
						|| (CharObjidTable.get().isChar(applicant_id) == null)) {
					deleteRecommendApply(clan_id, applicant_id);
					continue;
				}

				CopyOnWriteArrayList<Integer> total_list = _applyList.get(clan_id);
				if (total_list == null) {
					total_list = new CopyOnWriteArrayList<Integer>();
				}
				total_list.add(applicant_id);

				_applyList.put(clan_id, total_list);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs2);
			SQLUtil.close(rs);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入血盟推薦登錄資料數量: " + _recommendsList.size() + "(" + timer.get() + "ms)");
	}

	@SuppressWarnings("resource")
	private final void deleteIllegalData() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"DELETE FROM clan_recommend WHERE clan_id NOT IN(SELECT clan_id FROM clan_data) || up_time < DATE_SUB(CURDATE(), INTERVAL 3 DAY); ");
			pstm.execute();

			pstm = con.prepareStatement(
					"DELETE FROM clan_request_list WHERE clan_id NOT IN(SELECT clan_id FROM clan_data) || clan_id NOT IN(SELECT clan_id FROM clan_recommend)");
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final void insertRecommend(final int clan_id, final String clan_name, final String leader_name,
			final int type_id, final String type_message) {
		// 已存在列表中
		if (_recommendsList.containsKey(clan_id)) {
			return;
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO clan_recommend SET clan_id=?, clan_name=?, leader_name=?, type_id=?, type_message=?, up_time=SYSDATE()");
			pstm.setInt(1, clan_id);
			pstm.setString(2, clan_name);
			pstm.setString(3, leader_name);
			pstm.setInt(4, type_id);
			pstm.setString(5, type_message);
			pstm.execute();

			final L1ClanRecommend recommend = new L1ClanRecommend(clan_id, type_id, type_message);
			_recommendsList.put(clan_id, recommend);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final void insertRecommendApply(final int clan_id, final String clan_name, final int applicant_id,
			final String applicant_name) {
		CopyOnWriteArrayList<Integer> total_list = _applyList.get(clan_id);
		if (total_list == null) {
			total_list = new CopyOnWriteArrayList<Integer>();
		}
		if (total_list.contains(applicant_id)) {
			return;
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO clan_request_list SET clan_id=?, clan_name=?, applicant_id=?, applicant_name=?");
			pstm.setInt(1, clan_id);
			pstm.setString(2, clan_name);
			pstm.setInt(3, applicant_id);
			pstm.setString(4, applicant_name);
			pstm.execute();

			total_list.add(applicant_id);

			_applyList.put(clan_id, total_list);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final void updateRecommend(final int clan_id, final int type_id, final String type_message) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement(
					"UPDATE clan_recommend SET type_id=?, type_message=?, up_time=SYSDATE() WHERE clan_id=?");
			pstm.setInt(1, type_id);
			pstm.setString(2, type_message);
			pstm.setInt(3, clan_id);
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final void deleteRecommend(final int clan_id) {
		if (!_recommendsList.containsKey(clan_id)) {
			return;
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("DELETE FROM clan_recommend WHERE clan_id=?");
			pstm.setInt(1, clan_id);
			pstm.execute();

			_recommendsList.remove(clan_id);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final void deleteRecommendApply(final int chan_id, final int char_id) {
		if (chan_id != 0) {
			final CopyOnWriteArrayList<Integer> list = _applyList.get(chan_id);
			if ((list == null) || !list.contains(char_id)) {
				return;
			}

			if (list.remove(new Integer(char_id))) {
				if (list.isEmpty()) {
					_applyList.remove(list);
				}
			}

		} else {
			for (final CopyOnWriteArrayList<Integer> list : _applyList.values()) {
				if (list.remove(new Integer(char_id))) {
					if (list.isEmpty()) {
						_applyList.remove(list);
					}
				}
			}
		}

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();

			final StringBuilder sbr = new StringBuilder();
			sbr.append("DELETE FROM clan_request_list WHERE applicant_id=?");
			if (chan_id != 0) {
				sbr.append(" AND clan_id=?");
			}

			pstm = con.prepareStatement(sbr.toString());
			pstm.setInt(1, char_id);

			if (chan_id != 0) {
				pstm.setInt(2, chan_id);
			}
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	@Override
	public final Map<Integer, L1ClanRecommend> getRecommendsList() {
		return _recommendsList;
	}

	@Override
	public final Map<Integer, CopyOnWriteArrayList<Integer>> getApplyList() {
		return _applyList;
	}
}
