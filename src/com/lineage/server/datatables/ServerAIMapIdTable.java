package com.lineage.server.datatables;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerAIMapIdTable {
	private static final Log _log = LogFactory.getLog(ServerAIMapIdTable.class);
	private static final ArrayList<Integer> _can_ai = new ArrayList<Integer>();
	private static final ArrayList<Integer> _cant_ai = new ArrayList<Integer>();
	private static ServerAIMapIdTable _instance;

	public static ServerAIMapIdTable get() {
		if (_instance == null) {
			_instance = new ServerAIMapIdTable();
		}
		return _instance;
	}

	private ServerAIMapIdTable() {
		load();
	}

	public static void reload() {
		_can_ai.clear();
		_cant_ai.clear();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_ai_mapid`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int map_id = rs.getInt("mapid");
				boolean can_ai = rs.getBoolean("safezone");
				boolean cant_ai = rs.getBoolean("noai");
				if (can_ai) {
					_can_ai.add(Integer.valueOf(map_id));
				} else if (cant_ai) {
					_cant_ai.add(Integer.valueOf(map_id));
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入AI地圖編號設置->安區偵測:" + _can_ai.size() + " /不偵測:"
				+ _cant_ai.size());
	}

	private void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_ai_mapid`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int map_id = rs.getInt("mapid");
				boolean can_ai = rs.getBoolean("safezone");
				boolean cant_ai = rs.getBoolean("noai");
				if (can_ai) {
					_can_ai.add(Integer.valueOf(map_id));
				} else if (cant_ai) {
					_cant_ai.add(Integer.valueOf(map_id));
				}
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入AI地圖編號設置->安區偵測:" + _can_ai.size() + " /不偵測:"
				+ _cant_ai.size());
	}

	public boolean checkCanAI(int map_id) {
		return _can_ai.contains(Integer.valueOf(map_id));
	}

	public boolean checkCantAI(int map_id) {
		return _cant_ai.contains(Integer.valueOf(map_id));
	}
}