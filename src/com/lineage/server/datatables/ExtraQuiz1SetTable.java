package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.config.ConfigAlt;
import com.lineage.server.utils.SQLUtil;

/**
 * 每日任務系統
 * 
 * @author erics4179
 */
public final class ExtraQuiz1SetTable {

	private static final Log _log = LogFactory.getLog(ExtraQuiz1SetTable.class);

	public static int GS_quizId1;

	public static int GS_itemid;

	public static int GS_count;
	
	public static int GS_level;

	public static String GS_showQuiz1;

	public static String[] GS_option1 = new String[4];

	public static byte GS_answer1;

	private static ExtraQuiz1SetTable _instance;

	public static ExtraQuiz1SetTable getInstance() {
		if (_instance == null) {
			_instance = new ExtraQuiz1SetTable();
		}
		return _instance;
	}

	public final void load() {
		updateQuizInfo();
		_log.info("載入每日任務資料 (quiz_id: " + GS_quizId1 + ")");
	}

	public final void updateQuizInfo() {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_quiz1_set WHERE is_active=1");
			rs = pstm.executeQuery();

			if (rs.next()) {
				GS_quizId1 = rs.getInt("quiz_id");
				GS_showQuiz1 = rs.getString("show_quiz");
				GS_option1[0] = rs.getString("optionA");
				GS_option1[1] = rs.getString("optionB");
				GS_option1[2] = rs.getString("optionC");
				GS_option1[3] = rs.getString("optionD");
				GS_answer1 = rs.getByte("answer");
				GS_itemid = rs.getInt("itemid");
				GS_count = rs.getInt("count");
				GS_level = rs.getInt("level");
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	public final void updateQuizToNext() {
		int quiz_id = 0;

		switch (ConfigAlt.QUIZ_SET_TYPE1) {
		case 0:
			return;

		case 1:
			if (checkQuizExist(GS_quizId1 + 1)) {
				quiz_id = GS_quizId1 + 1;
			}
			break;

		case 2:
			quiz_id = new Random().nextInt(getQuizSize());
			break;
		}

		GS_showQuiz1 = null;
		GS_option1[0] = null;
		GS_option1[1] = null;
		GS_option1[2] = null;
		GS_option1[3] = null;
		GS_answer1 = 0;
		GS_itemid = 0;
		GS_count = 0;
		GS_level = 0;

		Connection conn = null;
		PreparedStatement pstm = null;
		PreparedStatement pstm2 = null;
		try {
			conn = DatabaseFactory.get().getConnection();

			pstm = conn.prepareStatement("UPDATE extra_quiz1_set SET is_active=0");
			pstm.execute();

			pstm2 = conn.prepareStatement("UPDATE extra_quiz1_set SET is_active=1 WHERE quiz_id=?");
			pstm2.setInt(1, quiz_id);
			pstm2.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}

	private final boolean checkQuizExist(final int quiz_id) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT * FROM extra_quiz1_set WHERE quiz_id=?");
			pstm.setInt(1, quiz_id);
			rs = pstm.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		return false;
	}

	private final int getQuizSize() {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement("SELECT count(*) FROM extra_quiz1_set");
			rs = pstm.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
		return 0;
	}

	public final void updateAllPcQuizSet() {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DatabaseFactory.get().getConnection();
			pstm = conn.prepareStatement(
					"UPDATE character_quests SET quest_step=0 WHERE quest_id=81250 AND quest_step=1");
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(conn);
		}
	}
}
