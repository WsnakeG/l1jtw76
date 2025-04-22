package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.server.templates.L1ArmorSets;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 套裝設置
 * 
 * @author dexc
 */
public class ArmorSetTable {

	private static final Log _log = LogFactory.getLog(ArmorSetTable.class);

	private static ArmorSetTable _instance;

	private static final HashMap<Integer, L1ArmorSets> _armorSetList = new HashMap<Integer, L1ArmorSets>();
	
	private static HashMap<Integer, Integer> _armor_id_list = new HashMap<Integer, Integer>();

	public static ArmorSetTable get() {
		if (_instance == null) {
			_instance = new ArmorSetTable();
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
			pstm = con.prepareStatement("SELECT * FROM `armor_set`");
			rs = pstm.executeQuery();
			fillTable(rs);

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		_log.info("載入套裝設置數量: " + _armorSetList.size() + "(" + timer.get() + "ms)");

		// 套裝設置加載
		ArmorSet.load();
	}

	public final L1ArmorSets[] getAllList() {
		return _armorSetList.values().toArray(new L1ArmorSets[_armorSetList.size()]);
	}

	private void fillTable(final ResultSet rs) throws SQLException {
		while (rs.next()) {
			final L1ArmorSets as = new L1ArmorSets();
//			as.setId(rs.getInt("id"));// 套裝編號
//			as.setSets(rs.getString("sets"));// 套裝組件編號
			final int id = rs.getInt("id");// 套裝編號
			as.setId(id);
			String sets = rs.getString("sets");
			as.setSets(sets);
		    for (int key : getArray(sets)) {
		         _armor_id_list.put(key, id);
		    }
			
			
			
			as.setPolyId(rs.getInt("polyid"));// 變身代號
			as.setAc(rs.getInt("ac"));// 防禦力增加
			as.setHp(rs.getInt("hp"));// HP增加
			as.setMp(rs.getInt("mp"));// MP增加
			as.setHpr(rs.getInt("hpr"));// HP回復增加
			as.setMpr(rs.getInt("mpr"));// MP回復增加
			as.setMr(rs.getInt("mr"));// 抗魔增加

			as.setStr(rs.getInt("str"));// 力量增加
			as.setDex(rs.getInt("dex"));// 敏捷增加
			as.setCon(rs.getInt("con"));// 體質增加
			as.setWis(rs.getInt("wis"));// 精神增加
			as.setCha(rs.getInt("cha"));// 魅力增加
			as.setIntl(rs.getInt("intl"));// 智力增加

			as.setDefenseWater(rs.getInt("defense_water"));// 水屬性增加
			as.setDefenseWind(rs.getInt("defense_wind"));// 風屬性增加
			as.setDefenseFire(rs.getInt("defense_fire"));// 火屬性增加
			as.setDefenseEarth(rs.getInt("defense_earth"));// 地屬性增加

			as.set_regist_stun(rs.getInt("regist_stun"));// 暈眩耐性增加
			as.set_regist_stone(rs.getInt("regist_stone"));// 石化耐性增加
			as.set_regist_sleep(rs.getInt("regist_sleep"));// 睡眠耐性增加
			as.set_regist_freeze(rs.getInt("regist_freeze"));// 寒冰耐性增加
			as.set_regist_sustain(rs.getInt("regist_sustain"));// 支撐耐性增加
			as.set_regist_blind(rs.getInt("regist_blind"));// 暗闇耐性增加

			as.set_modifier_dmg(rs.getInt("modifier_dmg"));// 套裝增加物理傷害
			as.set_reduction_dmg(rs.getInt("reduction_dmg"));// 套裝減免物理傷害
			as.set_magic_modifier_dmg(rs.getInt("magic_modifier_dmg"));// 套裝增加魔法傷害
			as.set_magic_reduction_dmg(rs.getInt("magic_reduction_dmg"));// 套裝減免魔法傷害
			as.set_bow_modifier_dmg(rs.getInt("bow_modifier_dmg"));// 套裝增加弓的物理傷害
			
			// 161228 新增8特殊能力為套裝設置
			as.set_physicsDmgUp(rs.getInt("physicsDmgUp"));// 套裝增加物理傷害 %
			as.set_magicDmgUp(rs.getInt("magicDmgUp"));// 套裝增加魔法傷害 %
			as.set_physicsDmgDown(rs.getInt("physicsDmgDown"));// 套裝增加物理傷害減免 %
			as.set_magicDmgDown(rs.getInt("magicDmgDown"));// 套裝增加魔法傷害減免 %
			as.set_magicHitUp(rs.getInt("magicHitUp"));// 套裝增加有害魔法命中 %
			as.set_magicHitDown(rs.getInt("magicHitDown"));// 套裝增加有害魔法抵抗 %
			as.set_physicsDoubleHit(rs.getInt("physicsDoubleHit"));// 套裝增加物理爆擊傷害(1.5倍) %
			as.set_magicDoubleHit(rs.getInt("magicDoubleHit"));// 套裝增加魔法爆擊傷害(1.5倍) %

			final String gfx = rs.getString("gfx");
			if ((gfx != null) && !gfx.equals("")) {
				final String[] gfxs = gfx.replaceAll(" ", "").split(",");
				final int[] out = new int[gfxs.length];
				for (int i = 0; i < gfxs.length; i++) {
					out[i] = Integer.parseInt(gfxs[i]);
				}
				as.set_gfxs(out);// 套裝效果動畫
			}

			// 套裝特效持續顯示的間隔秒數 (需先設定gfx) by terry0412
			as.set_gfx_timesecs(rs.getInt("gfx_timesecs"));

			_armorSetList.put(as.getId(), as);
		}
	}
	
	/** 套裝組 */
	public boolean checkArmorSet(int item_id) {
	    return _armor_id_list.containsKey(item_id);
	}
	
	private static int[] getArray(String s) {
	    StringTokenizer st = new StringTokenizer(s, ",");
	    int iSize = st.countTokens();
	    String sTemp = null;
	    int[] iReturn = new int[iSize];
	    for (int i = 0; i < iSize; i++) {
	      sTemp = st.nextToken();
	      iReturn[i] = Integer.parseInt(sTemp);
	    }
	    return iReturn;
	}
}
