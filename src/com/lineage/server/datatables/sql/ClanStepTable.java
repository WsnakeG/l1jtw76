package com.lineage.server.datatables.sql;

// import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.lineage.DatabaseFactory;

/**
 * 血盟技能階級化系統
 * 
 * @author erics4179
 */

public class ClanStepTable {
	public final static List<Integer> skill1 = new ArrayList<Integer>();
	public final static List<Integer> skill2 = new ArrayList<Integer>();
	public final static List<Integer> skill3 = new ArrayList<Integer>();
	public final static List<Integer> skill4 = new ArrayList<Integer>();

	public static void load() {
		Connection conn = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM `clan_step` WHERE `skillid` = 1 ORDER BY `step`");

			if (rs != null) {
				while (rs.next()) {
					int status = rs.getInt("status");
					skill1.add(status);
				}
			}

			conn = DatabaseFactory.get().getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM `clan_step` WHERE `skillid` = 2 ORDER BY `step`");

			if (rs != null) {
				while (rs.next()) {
					int status = rs.getInt("status");
					skill2.add(status);
				}
			}

			conn = DatabaseFactory.get().getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM `clan_step` WHERE `skillid` = 3 ORDER BY `step`");

			if (rs != null) {
				while (rs.next()) {
					int status = rs.getInt("status");
					skill3.add(status);
				}
			}

			conn = DatabaseFactory.get().getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM `clan_step` WHERE `skillid` = 4 ORDER BY `step`");

			if (rs != null) {
				while (rs.next()) {
					int status = rs.getInt("status");
					skill4.add(status);
				}
			}

			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (Exception ex) {
		}
	}
}
