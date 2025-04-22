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

import java.util.Random;

public class ServerAIEffectTable {
	private static final Log _log = LogFactory
			.getLog(ServerAIEffectTable.class);
	private static final ArrayList<Integer> _effectlist = new ArrayList<Integer>();
	private static ServerAIEffectTable _instance;

	public static ServerAIEffectTable get() {
		if (_instance == null) {
			_instance = new ServerAIEffectTable();
		}
		return _instance;
	}

	private ServerAIEffectTable() {
		load();
	}

	public static void reload() {
		_effectlist.clear();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_ai_effect`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int effect_id = rs.getInt("effectid");
				_effectlist.add(Integer.valueOf(effect_id));
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_ai_effect`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int effect_id = rs.getInt("effectid");
				_effectlist.add(Integer.valueOf(effect_id));
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入AI特效編號設置:" + _effectlist.size());
	}

	public static int getEffectId() {
		Random rnd = new Random();
		if (_effectlist.size() != 0) {
			return _effectlist.get(rnd.nextInt(_effectlist.size()));
		} else
			return 1765;
	}
}