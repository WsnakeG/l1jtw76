package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MobSkillGroup;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * @author XXX
 */
public class MobSkillGroupTable {

	private static final Log _log = LogFactory.getLog(MobSkillGroupTable.class);

	private static MobSkillGroupTable _instance;

	private static final Map<Integer, L1MobSkillGroup> _mobskills = new HashMap<Integer, L1MobSkillGroup>();

	public static MobSkillGroupTable get() {
		if (_instance == null) {
			_instance = new MobSkillGroupTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm1 = null;
		PreparedStatement pstm2 = null;
		ResultSet rs1 = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm1 = con.prepareStatement("SELECT mobid, count(*) as cnt FROM mobskill_group group by mobid");

			int count = 0;
			int mobid = 0;
			int actNo = 0;

			pstm2 = con
					.prepareStatement("SELECT * FROM mobskill_group where mobid = ? order by mobid, actNo");

			for (rs1 = pstm1.executeQuery(); rs1.next();) {
				mobid = rs1.getInt("mobid");
				count = rs1.getInt("cnt");

				ResultSet rs2 = null;
				try {
					pstm2.setInt(1, mobid);
					rs2 = pstm2.executeQuery();

					final L1MobSkillGroup mobskill = new L1MobSkillGroup(count);
					mobskill.set_mobid(mobid);

					while (rs2.next()) {
						actNo = rs2.getInt("actNo");
						mobskill.setChatId(actNo, rs2.getString("ChatId"));
						mobskill.setInterval(actNo, rs2.getInt("Interval"));
						mobskill.setChance(actNo, rs2.getInt("Chance"));
						mobskill.setRange(actNo, rs2.getInt("Range"));
						mobskill.setLeverage(actNo, rs2.getInt("Leverage"));
						mobskill.setActNoList(actNo, rs2.getString("ActNoList"));
					}
					_mobskills.put(new Integer(mobid), mobskill);

				} catch (final SQLException e1) {
					_log.error(e1.getLocalizedMessage(), e1);

				} finally {
					SQLUtil.close(rs2);
				}
			}
			_log.info("載入MOB技能組資料數量: " + _mobskills.size() + "(" + timer.get() + "ms)");

		} catch (final SQLException e2) {
			_log.error(e2.getLocalizedMessage(), e2);

		} finally {
			SQLUtil.close(rs1);
			SQLUtil.close(pstm1);
			SQLUtil.close(pstm2);
			SQLUtil.close(con);
		}
	}

	public L1MobSkillGroup getTemplate(final int id) {
		return _mobskills.get(id);
	}
}
