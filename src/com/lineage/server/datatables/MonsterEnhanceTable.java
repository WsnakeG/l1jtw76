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
import com.lineage.server.model.Instance.L1MonsterEnhanceInstance;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 怪物強化系統
 * 
 * @author by erics4179
 */
public class MonsterEnhanceTable {

	private static final Log _log = LogFactory.getLog(MonsterEnhanceTable.class);

	private static final Map<Integer, L1MonsterEnhanceInstance> _meis = new HashMap<Integer, L1MonsterEnhanceInstance>();

	private final boolean _initialized;

	private static MonsterEnhanceTable _instance;

	public static MonsterEnhanceTable getInstance() {
		if (_instance == null) {
			_instance = new MonsterEnhanceTable();
		}
		return _instance;
	}

	public boolean isInitialized() {
		return _initialized;
	}

	private MonsterEnhanceTable() {
		load();
		_initialized = true;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM extra_monster_enhance");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1MonsterEnhanceInstance mei = new L1MonsterEnhanceInstance();
				int npcid = rs.getInt("npcid");

				mei.setNpcId(npcid);
				mei.setCurrentDc(rs.getInt("current_dc")); // 怪物死亡-設定每(X)次後則強化
				// mei.setCurrentDc(0); //把註解拿掉即代表功能為當伺服器重啟則強化歸零
				// 就可以在伺服器重開時重新計算死亡次數而達成伺服器重開強化重啟
				mei.setDcEnhance(rs.getInt("dc_enhance")); // 怪物死亡-設定(X)後強化一次
				mei.setLevel(rs.getInt("level"));// 增加等級
				mei.setHp(rs.getInt("hp"));// 增加血量
				mei.setMp(rs.getInt("mp"));// 增加魔量
				mei.setAc(rs.getInt("ac"));// 增加防禦
				mei.setStr(rs.getInt("str"));// 增加力量
				mei.setDex(rs.getInt("dex"));// 增加敏捷
				mei.setCon(rs.getInt("con"));// 增加體質
				mei.setWis(rs.getInt("wis"));// 增加精神
				mei.setInt(rs.getInt("int"));// 增加智力
				mei.setMr(rs.getInt("mr"));// 增加魔防
				mei.setHpr(rs.getInt("hpr"));// 增加回血

				_meis.put(npcid, mei);

			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入怪物強化資料數量: " + _meis.size() + "(" + timer.get() + "ms)");

	}

	public void save(L1MonsterEnhanceInstance mei) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("UPDATE extra_monster_enhance SET current_dc= ? WHERE npcid=?");
			pstm.setInt(1, mei.getCurrentDc());
			pstm.setInt(2, mei.getNpcId());
			pstm.execute();
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1MonsterEnhanceInstance getTemplate(int npcId) {
		return _meis.get(npcId);
	}
}
