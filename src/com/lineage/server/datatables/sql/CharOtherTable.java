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
import com.lineage.data.event.LeavesSet;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.storage.CharOtherStorage;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 額外紀錄資料
 * 
 * @author dexc
 */
public class CharOtherTable implements CharOtherStorage {

	private static final Log _log = LogFactory.getLog(CharOtherTable.class);

	private static final Map<Integer, L1PcOther> _otherMap = new HashMap<Integer, L1PcOther>();

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_other`");
			rs = ps.executeQuery();
			while (rs.next()) {
				final int char_obj_id = rs.getInt("char_obj_id");
				// 檢查該資料所屬是否遺失
				if (CharObjidTable.get().isChar(char_obj_id) != null) {
					final int logintime = rs.getInt("logintime");// TIME
					final int hpup = rs.getInt("hpup");// HP人参，玩家已增加的HP值
					final int mpup = rs.getInt("mpup");// MP人参，玩家已增加的MP值
					final int score = rs.getInt("score");// 設置積分
					// final int title = rs.getInt("title");// 頭銜
					final int color = rs.getInt("color");// 名稱色彩
					final int usemap = rs.getInt("usemap");// 計時地圖編號
					final int usemaptime = rs.getInt("usemaptime");// 計時地圖可用時間
					final int clanskill = rs.getInt("clanskill");// 設置血盟技能
					final int killCount = rs.getInt("killCount");// 設置殺人次數
					final int deathCount = rs.getInt("deathCount");// 設置被殺次數

					final L1PcOther other = new L1PcOther();
					other.set_objid(char_obj_id);
					other.set_login_time(logintime);
					other.set_addhp(hpup);// HP人参，玩家已增加的HP值
					other.set_addmp(mpup);// MP人参，玩家已增加的MP值
					other.set_score(score);// 設置積分
					// other.set_title(title);// 頭銜
					other.set_color(color);// 名稱色彩

					if (usemaptime <= 0) {
						other.set_usemap(-1);// 計時地圖編號
						other.set_usemapTime(0);// 計時地圖可用時間

					} else {
						other.set_usemap(usemap);// 計時地圖編號
						other.set_usemapTime(usemaptime);// 計時地圖可用時間
					}

					other.set_clanskill(clanskill);// 設置血盟技能
					other.set_killCount(killCount);// 設置殺人次數
					other.set_deathCount(deathCount);// 設置被殺次數

					addMap(char_obj_id, other);

				} else {
					delete(char_obj_id);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入額外紀錄資料數量: " + _otherMap.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 刪除遺失額外紀錄資料
	 * 
	 * @param objid
	 */
	private static void delete(final int objid) {
		// 清空資料庫紀錄
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_other` WHERE `char_obj_id`=?");
			ps.setInt(1, objid);
			ps.execute();

			_otherMap.remove(objid);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 加入額外紀錄清單
	 * 
	 * @param objId
	 * @param buffTmp
	 */
	private static void addMap(final int objId, final L1PcOther other) {
		final L1PcOther otherTmp = _otherMap.get(objId);
		if (otherTmp == null) {
			_otherMap.put(objId, other);
		}
	}

	/**
	 * 取回保留額外紀錄
	 * 
	 * @param pc
	 */
	@Override
	public L1PcOther getOther(final L1PcInstance pc) {
		final L1PcOther otherTmp = _otherMap.get(pc.getId());
		return otherTmp;
	}

	/**
	 * 增加/更新 保留額外紀錄
	 * 
	 * @param objId
	 * @param other
	 */
	@Override
	public void storeOther(final int objId, final L1PcOther other) {
		final L1PcOther otherTmp = _otherMap.get(objId);
		if (otherTmp == null) {
			addMap(objId, other);
			addNewOther(other);

		} else {
			updateOther(other);
		}
	}

	/**
	 * 更新紀錄
	 * 
	 * @param other
	 */
	private void updateOther(final L1PcOther other) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			final int hpup = other.get_addhp();
			final int mpup = other.get_addmp();
			final int score = other.get_score();
			// final int title = other.get_title();
			final int color = other.get_color();
			final int usemap = other.get_usemap();
			final int usemapTime = other.get_usemapTime();
			final int clanskill = other.get_clanskill();
			final int killCount = other.get_killCount();
			final int deathCount = other.get_deathCount();

			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("UPDATE `character_other` SET " + "`logintime`=?,`hpup`=?,`mpup`=?,`"
					+ "score`=?,`color`=?,`usemap`=?,`usemaptime`=?,"
					+ "`clanskill`=?,`killCount`=?,`deathCount`=?" + " WHERE `char_obj_id`=?");

			int i = 0;
			int logintime = (int) (System.currentTimeMillis() / 60 / 1000);// 分鐘
			if (LeavesSet.START) {
				if ((other.get_teaves_time_exp() / LeavesSet.EXP) > 0) {
					logintime -= (other.get_teaves_time_exp() / LeavesSet.EXP) * LeavesSet.TIME;
				}
			}
			other.set_login_time(logintime);
			ps.setInt(++i, logintime);
			ps.setInt(++i, hpup);
			ps.setInt(++i, mpup);
			ps.setInt(++i, score);
			ps.setInt(++i, color);
			ps.setInt(++i, usemap);
			ps.setInt(++i, usemapTime);
			ps.setInt(++i, clanskill);
			ps.setInt(++i, killCount);
			ps.setInt(++i, deathCount);

			ps.setInt(++i, other.get_objid());

			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 增加紀錄
	 * 
	 * @param other
	 */
	private void addNewOther(final L1PcOther other) {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			final int oid = other.get_objid();
			final int hpup = other.get_addhp();
			final int mpup = other.get_addmp();
			final int score = other.get_score();
			final int color = other.get_color();
			final int usemap = other.get_usemap();
			final int usemapTime = other.get_usemapTime();
			final int clanskill = other.get_clanskill();
			final int killCount = other.get_killCount();
			final int deathCount = other.get_deathCount();

			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement(
					"INSERT INTO `character_other` SET `char_obj_id`=?,`logintime`=?,`hpup`=?,"
							+ "`mpup`=?,`score`=?,`color`=?,`usemap`=?,`usemaptime`=?,"
							+ "`clanskill`=?,`killCount`=?,`deathCount`=?");

			int i = 0;
			ps.setInt(++i, oid);
			int logintime = (int) (System.currentTimeMillis() / 60 / 1000);// 分鐘
			if (LeavesSet.START) {
				if ((other.get_teaves_time_exp() / LeavesSet.EXP) > 0) {
					logintime -= (other.get_teaves_time_exp() / LeavesSet.EXP) * LeavesSet.TIME;
				}
			}
			other.set_login_time(logintime);
			ps.setInt(++i, logintime);
			ps.setInt(++i, hpup);
			ps.setInt(++i, mpup);
			ps.setInt(++i, score);
			ps.setInt(++i, color);
			ps.setInt(++i, usemap);
			ps.setInt(++i, usemapTime);
			ps.setInt(++i, clanskill);
			ps.setInt(++i, killCount);
			ps.setInt(++i, deathCount);

			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 歸0殺人次數(全部玩家)
	 */
	@Override
	public void tam() {
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			for (final L1PcOther other : _otherMap.values()) {
				other.set_killCount(0);
				other.set_deathCount(0);
			}
			cn = DatabaseFactory.get().getConnection();
			ps = cn.prepareStatement("UPDATE `character_other` SET `killCount`='0' AND `deathCount`='0'");
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
