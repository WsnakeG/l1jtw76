package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.event.CampSet;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharacterC1Storage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1User_Power;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 人物陣營紀錄
 * 
 * @author dexc
 */
public class CharacterC1Table implements CharacterC1Storage {

	private static final Log _log = LogFactory.getLog(CharacterC1Table.class);

	private static final Map<Integer, L1User_Power> _userPowers = new HashMap<Integer, L1User_Power>();

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `character_c1`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int object_id = rs.getInt("object_id");

				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(object_id) != null) {
					final int c1_type = rs.getInt("c1_type");
					final String note = rs.getString("note");

					final L1User_Power power = new L1User_Power();
					power.set_object_id(object_id);
					power.set_c1_type(c1_type);
					power.set_note(note);

					_userPowers.put(object_id, power);

				} else {
					// 資料遺失刪除記錄
					delete(object_id);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入人物陣營紀錄資料數量: " + _userPowers.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除遺失資料
	 * 
	 * @param objid
	 */
	private static void delete(final int objid) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_c1` WHERE `object_id`=?");
			ps.setInt(1, objid);
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 傳回 L1User_Power
	 */
	@Override
	public L1User_Power get(final int objectId) {
		return _userPowers.get(objectId);
	}

	/**
	 * 新建 L1User_Power
	 */
	@Override
	public void storeCharacterC1(final L1PcInstance pc) {
		if (!CampSet.CAMPSTART) {
			_log.error("陣營系統並未啟動!");
			return;
		}
		final L1User_Power power = pc.get_c_power();
		_userPowers.put(pc.getId(), power);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("INSERT INTO `character_c1` SET `object_id`=?,`c1_type`=?,`note`=?");

			int i = 0;
			pstm.setInt(++i, power.get_object_id());
			pstm.setInt(++i, power.get_c1_type());
			pstm.setString(++i, power.get_note());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 更新 L1User_Power
	 */
	@Override
	public void updateCharacterC1(final int object_id, final int c1_type, final String note) {
		final L1User_Power power = _userPowers.get(object_id);
		power.set_c1_type(c1_type);
		power.set_note(note);

		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE `character_c1` SET `c1_type`=?,`note`=? WHERE `object_id`=?");

			int i = 0;
			pstm.setInt(++i, power.get_c1_type());
			pstm.setString(++i, power.get_note());
			pstm.setInt(++i, power.get_object_id());
			pstm.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
