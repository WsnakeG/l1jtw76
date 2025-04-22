package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * NPC積分設置資料 2014/06/27 by Roy更新積分狩獵改變(MIN~MAX)
 * 
 * @author dexc
 */
public class NpcScoreTable {

	private static final Log _log = LogFactory.getLog(NpcScoreTable.class);

	private static NpcScoreTable _instance;

	private static Random _random = new Random();

	private static final Map<Integer, Integer> _scoremaxList = new TreeMap<Integer, Integer>();
	private static final Map<Integer, Integer> _scoreminList = new TreeMap<Integer, Integer>();
	private static final ArrayList<Integer> _scorenpcList = new ArrayList<Integer>();

	public static NpcScoreTable get() {
		if (_instance == null) {
			_instance = new NpcScoreTable();
		}
		return _instance;
	}

	public void reload() {
		_scoremaxList.clear();
		_scoreminList.clear();
		_scorenpcList.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `npcscore`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int npcId = rs.getInt("npcid");
				final int score_min = rs.getInt("score_min");
				final int score_max = rs.getInt("score_max");

				_scoremaxList.put(npcId, score_max);
				_scoreminList.put(npcId, score_min);
				_scorenpcList.add(npcId);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入NPC積分設置資料數量: " + _scoremaxList.size() + "(" + timer.get() + "ms)");
	}

	public ArrayList<Integer> get_scoreList() {
		return _scorenpcList;
	}

	public int get_score(final int npcid) {
		if (_scoreminList.containsKey(npcid)) {
			// System.out.println("number1");
			final int max = _scoremaxList.get(npcid);
			// System.out.println("number2");
			final int min = _scoreminList.get(npcid);
			// System.out.println("number3");
			final int sum = min + _random.nextInt(max);
			// System.out.println("number4");
			// System.out.println("sum = "+ sum);
			return sum;
		}
		// System.out.println("no npcid:"+npcid);
		return 0;
	}

}
