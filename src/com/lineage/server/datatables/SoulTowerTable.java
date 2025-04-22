package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lineage.DatabaseFactory;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.SQLUtil;

/**
 * 屍魂塔排行
 */
public class SoulTowerTable {

	private static Logger _log = Logger.getLogger(SoulTowerTable.class.getName());

	private static SoulTowerTable _instance;

	public static SoulTowerTable getInstance() {
		if (_instance == null) {
			_instance = new SoulTowerTable();
		}
		return _instance;
	}

	private final ArrayList<SoulTowerRank> list = new ArrayList<>();

	private SoulTowerTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT *FROM soul_tower_rank");
			rs = pstm.executeQuery();
			while (rs.next()) {

				final SoulTowerRank rank = new SoulTowerRank();
				rank.name = rs.getString("name");
				rank.classType = rs.getInt("class");
				rank.time = rs.getInt("time");
				rank.date = rs.getDate("date").getTime();

				list.add(rank);
			}
		} catch (final SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}

	}

	public void updateRank(final L1PcInstance pc, final int time) {

		boolean isBestter = false;

		for (final SoulTowerRank rank : list) {

			if (time < rank.time || list.size() < 10) {
				isBestter = true;
				break;
			}
		}

		if (isBestter) {

			final SoulTowerRank rank = new SoulTowerRank();
			rank.name = pc.getName();
			rank.classType = pc.getType();
			rank.time = time;

			final java.util.Date now = new java.util.Date();
			final java.sql.Date sqlDate = new java.sql.Date(now.getTime());

			rank.date = sqlDate.getTime();

			// System.out.println("now=" + rank.date);

			list.add(rank);

			Collections.sort(list, new Comparator<SoulTowerRank>() {
				@Override
				public int compare(final SoulTowerRank r1, final SoulTowerRank r2) {
					return r1.time - r2.time;
				}
			});

			Connection con = null;
			PreparedStatement pstm = null;
			try {
				con = DatabaseFactory.get().getConnection();
				pstm = con.prepareStatement("DELETE FROM soul_tower_rank WHERE rank >0");
				pstm.execute();

				for (int i = 0; i < list.size() && i < 10; i++) {
					final SoulTowerRank str = list.get(i);
					pstm = con.prepareStatement("INSERT INTO soul_tower_rank  SET rank=?,name=?,class=?,time=?,date=?");

					pstm.setInt(1, i + 1);
					pstm.setString(2, str.name);
					pstm.setInt(3, str.classType);
					pstm.setInt(4, str.time);

					final java.util.Date utilDate = new java.util.Date();
					utilDate.setTime(str.date);

					pstm.setDate(5, new java.sql.Date(utilDate.getTime()));

					pstm.execute();
				}

			} catch (final SQLException e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}

		}
	}

	public class SoulTowerRank {

		public String name;
		public int classType;
		public int time;
		public long date;

	}
}