package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.QuestClass;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Quest;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * Quest(任務)設置資料
 * 
 * @author dexc
 */
public class QuestTable {

	private static final Log _log = LogFactory.getLog(QuestTable.class);

	private static QuestTable _instance;

	/**
	 * 最小任務編號
	 */
	public static int MINQID = 0;

	/**
	 * 最大任務編號
	 */
	public static int MAXQID = 0;

	private static final HashMap<Integer, L1Quest> _questList = new HashMap<Integer, L1Quest>();

	public static QuestTable get() {
		if (_instance == null) {
			_instance = new QuestTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_quest` ORDER BY `id`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final String questname = rs.getString("questname");
				final String questclass = rs.getString("questclass");
				if (questclass.equals("0")) {
					continue;
				}
				final boolean queststart = rs.getBoolean("queststart");
				if (!queststart) {
					continue;
				}
				if (id > MAXQID) {
					MAXQID = id;
				}
				final boolean del = rs.getBoolean("del");
				final int questuser = rs.getInt("questuser");
				final int questlevel = rs.getInt("questlevel");
				final int difficulty = rs.getInt("difficulty");
				final String note = rs.getString("note");

				if (queststart) {
					final L1Quest quest = new L1Quest();
					quest.set_id(id);
					quest.set_questname(questname);
					quest.set_questclass(questclass);
					quest.set_queststart(queststart);
					quest.set_del(del);
					quest.set_questuser(questuser);
					quest.set_questlevel(questlevel);
					quest.set_difficulty(difficulty);
					quest.set_note(note);

					QuestClass.get().addList(id, questclass);
					_questList.put(new Integer(id), quest);
				}
			}

			_log.info("載入Quest(任務)設置資料數量: " + _questList.size() + "(" + timer.get() + "ms)");

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);

			start();
		}
	}

	/**
	 * 任務啟用設置
	 */
	private void start() {
		try {
			for (final L1Quest quest : _questList.values()) {
				QuestClass.get().execute(quest);
				Thread.sleep(20);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 傳回任務內容
	 * 
	 * @param id
	 * @return
	 */
	public L1Quest getTemplate(final int id) {
		return _questList.get(new Integer(id));
	}

	/**
	 * 傳回任務清單
	 * 
	 * @return
	 */
	public HashMap<Integer, L1Quest> getList() {
		return _questList;
	}

	/**
	 * 傳回任務數量
	 * 
	 * @return
	 */
	public int size() {
		return _questList.size();
	}

	/**
	 * 傳回該等級可執行未完成任務數量
	 * 
	 * @return
	 */
	public int levelQuest(final L1PcInstance pc, final int level) {
		int i = 0;
		for (final Integer key : _questList.keySet()) {
			final L1Quest value = _questList.get(key);

			// 等於可執行等級
			if (level >= value.get_questlevel()) {
				// 該任務已經結束
				if (pc.getQuest().isEnd(key)) {
					continue;
				}
				// 可執行職業判斷
				if (value.check(pc)) {
					i++;
				}
			}
		}
		return i;
	}

	/**
	 * 傳回該職業目前等級可執行任務<BR>
	 * 由大編號至小編號排列
	 * 
	 * @param pc
	 * @return
	 */
	/*
	 * public HashMap<Integer, L1Quest> get_level_quest(final L1PcInstance pc) {
	 * final HashMap<Integer, L1Quest> questList = new HashMap<Integer,
	 * L1Quest>(); for (Integer key : _questList.keySet()) { final L1Quest value
	 * = _questList.get(key); // 大於可執行等級 if (pc.getLevel() >=
	 * value.get_questlevel()) { // 該任務已經結束 if (pc.getQuest().isEnd(key)) {
	 * continue; } // 該任務已經開始 if (pc.getQuest().isStart(value.get_id())) {
	 * continue; } // 可執行職業判斷 if (value.check(pc)) { questList.put(key, value);
	 * } } } return questList; }
	 */

	/**
	 * 傳回該職業全部任務<BR>
	 * 
	 * @param pc
	 * @return
	 */
	/*
	 * public HashMap<Integer, L1Quest> get_all_quest(final L1PcInstance pc) {
	 * final HashMap<Integer, L1Quest> questList = new HashMap<Integer,
	 * L1Quest>(); for (Integer key : _questList.keySet()) { final L1Quest value
	 * = _questList.get(key); // 可執行職業判斷 if (value.check(pc)) {
	 * questList.put(key, value); } } return questList; }
	 */
}
