package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1MagicStone;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 寶石鑲嵌系統(DB自製)
 * 
 * @author terry0412
 */
public final class ExtraMagicStoneTable {

	private static final Log _log = LogFactory.getLog(ExtraMagicStoneTable.class);

	private static final Map<Integer, L1MagicStone> _stoneList = new HashMap<>();

	private static ExtraMagicStoneTable _instance;

	public static ExtraMagicStoneTable getInstance() {
		if (_instance == null) {
			_instance = new ExtraMagicStoneTable();
		}
		return _instance;
	}

	public final void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM extra_magic_stone");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int item_id = rs.getInt("item_id");
				final String name = rs.getString("name");
				final int stage = rs.getInt("stage");
				final int use_type = rs.getInt("use_type");
				final int chance = rs.getInt("chance");
				final int need_hole = rs.getInt("need_hole");
				final boolean delete_hole = rs.getBoolean("delete_hole");
				final int add_str = rs.getInt("add_str");
				final int add_con = rs.getInt("add_con");
				final int add_dex = rs.getInt("add_dex");
				final int add_int = rs.getInt("add_int");
				final int add_wis = rs.getInt("add_wis");
				final int add_cha = rs.getInt("add_cha");
				final int add_hp = rs.getInt("add_hp");
				final int add_mp = rs.getInt("add_mp");
				final int hit_modifier = rs.getInt("hit_modifier");
				final int dmg_modifier = rs.getInt("dmg_modifier");
				final int bow_hit_modifier = rs.getInt("bow_hit_modifier");
				final int bow_dmg_modifier = rs.getInt("bow_dmg_modifier");
				final int add_ac = rs.getInt("add_ac");
				final int m_def = rs.getInt("m_def");
				final int add_sp = rs.getInt("add_sp");
				final int defense_water = rs.getInt("defense_water");
				final int defense_wind = rs.getInt("defense_wind");
				final int defense_fire = rs.getInt("defense_fire");
				final int defense_earth = rs.getInt("defense_earth");
				final int regist_stun = rs.getInt("regist_stun");
				final int regist_stone = rs.getInt("regist_stone");
				final int regist_sleep = rs.getInt("regist_sleep");
				final int regist_freeze = rs.getInt("regist_freeze");
				final int regist_sustain = rs.getInt("regist_sustain");
				final int regist_blind = rs.getInt("regist_blind");

				final int physicsDmgUp = rs.getInt("physicsDmgUp");
				final int magicDmgUp = rs.getInt("magicDmgUp");
				final int physicsDmgDown = rs.getInt("physicsDmgDown");
				final int magicDmgDown = rs.getInt("magicDmgDown");
				final int magicHitUp = rs.getInt("magicHitUp");
				final int magicHitDown = rs.getInt("magicHitDown");
				final int physicsDoubleHit = rs.getInt("physicsDoubleHit");
				final int magicDoubleHit = rs.getInt("magicDoubleHit");

				// 建立儲存資料
				final L1MagicStone magicStone = new L1MagicStone(item_id, name, stage, use_type, chance,
						need_hole, delete_hole, add_str, add_con, add_dex, add_int, add_wis, add_cha, add_hp,
						add_mp, hit_modifier, dmg_modifier, bow_hit_modifier, bow_dmg_modifier, add_ac, m_def,
						add_sp, defense_water, defense_wind, defense_fire, defense_earth, regist_stun,
						regist_stone, regist_sleep, regist_freeze, regist_sustain, regist_blind, physicsDmgUp,
						magicDmgUp, physicsDmgDown, magicDmgDown, magicHitUp, magicHitDown, physicsDoubleHit,
						magicDoubleHit);
				// 加到清單
				_stoneList.put(item_id, magicStone);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入寶石鑲嵌資料數量: " + _stoneList.size() + "(" + timer.get() + "ms)");
	}

	public final L1MagicStone findStone(final int id) {
		return _stoneList.get(id);
	}

	public final ArrayList<L1MagicStone> nextStageStone(final int stageId) {
		final ArrayList<L1MagicStone> nextList = new ArrayList<>();
		for (L1MagicStone magicStone : _stoneList.values()) {
			if (magicStone.getStage() == stageId) {
				nextList.add(magicStone);
			}
		}
		return nextList;
	}
}
