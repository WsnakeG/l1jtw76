package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ServerEtcItem;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;
import com.lineage.server.utils.collections.Maps;

/**
 * 能力道具系統 ServerEtcItemTable <BR>
 * 
 * @author Roy
 */

public class ServerEtcItemTable {
	private static final Log _log = LogFactory.getLog(ServerEtcItemTable.class);

	private static final ArrayList<L1ServerEtcItem> etcitem = new ArrayList<L1ServerEtcItem>();

	private static final Map<Integer, L1ServerEtcItem> mapetcitem = Maps.newHashMap();

	private static ServerEtcItemTable _instance;

	public static ServerEtcItemTable get() {
		if (_instance == null) {
			_instance = new ServerEtcItemTable();
		}
		return _instance;
	}

	public void reload() {
		etcitem.clear();
		getList();
	}

	private ServerEtcItemTable() {
		getList();
	}

	public L1ServerEtcItem getItem(final int itemid) {
		return mapetcitem.get(Integer.valueOf(itemid));
	}

	public ArrayList<L1ServerEtcItem> getAllList() {
		return etcitem;
	}

	private void getList() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = DatabaseFactory.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM extra_etcitem");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final L1ServerEtcItem value = new L1ServerEtcItem();
				value.itemid = rs.getInt("itemid");
				value.itemname = rs.getString("itemname");
				value.addMaxHP = rs.getInt("addMaxHP");
				value.addMaxMP = rs.getInt("addMaxMP");
				value.add_str = rs.getInt("add_str");
				value.add_con = rs.getInt("add_con");
				value.add_dex = rs.getInt("add_dex");
				value.add_int = rs.getInt("add_int");
				value.add_wis = rs.getInt("add_wis");
				value.add_cha = rs.getInt("add_cha");
				value.add_hpr = rs.getInt("add_hpr");
				value.add_mpr = rs.getInt("add_mpr");
				value.add_sp = rs.getInt("add_sp");
				value.dmg_modifier = rs.getInt("dmg_modifier");
				value.bow_dmg_modifier = rs.getInt("bow_dmg_modifier");
				value.add_ac = rs.getInt("add_ac");
				value.m_def = rs.getInt("m_def");
				value.double_dmg_chance = rs.getInt("double_dmg_chance");
				value.itemtime = rs.getInt("itemtime");
				value.deleteafteruse = rs.getBoolean("deleteafteruse");
				value.magic_reduction_dmg = rs.getInt("magic_reduction_dmg");
				value.reduction_dmg = rs.getInt("reduction_dmg");
				value.gif = rs.getInt("gif");
				
				// 新增能力道具系統 八種特殊能力 Erics4179 160829
				value.physicsDmgUp = rs.getInt("physicsDmgUp");
				value.magicDmgUp = rs.getInt("magicDmgUp");
				value.physicsDmgDown = rs.getInt("physicsDmgDown");
				value.magicDmgDown = rs.getInt("magicDmgDown");
				value.magicHitUp = rs.getInt("magicHitUp");
				value.magicHitDown = rs.getInt("magicHitDown");
				value.physicsDoubleHit = rs.getInt("physicsDoubleHit");
				value.magicDoubleHit = rs.getInt("magicDoubleHit");
				
				// 新增幸運度 Erics4179 160901
				value.InfluenceLuck = rs.getInt("influence_luck");
				
				etcitem.add(value);
				mapetcitem.put(Integer.valueOf(value.itemid), value);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入道具強化系統: " + etcitem.size() + "(" + timer.get() + "ms)");
	}
}
