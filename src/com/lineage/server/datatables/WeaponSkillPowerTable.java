package com.lineage.server.datatables;

import java.lang.reflect.InvocationTargetException;
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
import com.lineage.server.model.weaponskill.L1WeaponSkillType;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 技能武器資料 127.0.0.1 7f000001 2130706433
 * 
 * @author dexc
 */
public class WeaponSkillPowerTable {

	private static final Log _log = LogFactory.getLog(WeaponSkillPowerTable.class);

	private static WeaponSkillPowerTable _instance;

	private static final Map<Integer, L1WeaponSkillType> _weaponIdIndex = new HashMap<Integer, L1WeaponSkillType>();

	private static final Map<Integer, ArrayList<L1WeaponSkillType>> _weaponSkill = new HashMap<Integer, ArrayList<L1WeaponSkillType>>();

	public static WeaponSkillPowerTable get() {
		if (_instance == null) {
			_instance = new WeaponSkillPowerTable();
		}
		return _instance;
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		load1();
		load2();
		_log.info("載入技能武器能力資料數量: " + _weaponIdIndex.size() + "(" + timer.get() + "ms)");
		_log.info("載入技能武器設置資料數量: " + _weaponSkill.size() + "(" + timer.get() + "ms)");
	}

	private void load1() {
		Connection cn = null;
		PreparedStatement pm = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			pm = cn.prepareStatement("SELECT * FROM `weapon_skill_power`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final String classname = rs.getString("classname");
				final int level = rs.getInt("level");
				final int type1 = rs.getInt("type1");
				final int type2 = rs.getInt("type2");
				final int type3 = rs.getInt("type3");
				final int attr = rs.getInt("attr");
				final int ac_mr = rs.getInt("ac_mr");
				final int gfxid1 = rs.getInt("gfxid1");
				final int gfxid2 = rs.getInt("gfxid2");
				final int gfxid3 = rs.getInt("gfxid3");
				final int gfxid4 = rs.getInt("gfxid4");
				final int gfxid4_count = rs.getInt("gfxid4_count");
				final int power = rs.getInt("power");
				final int srcdmg = rs.getInt("srcdmg");
				final int addsrcdmg = rs.getInt("addsrcdmg");
				final int random1 = rs.getInt("random1");
				final int random2 = rs.getInt("random2");
				final boolean boss_holdout = rs.getBoolean("boss_holdout");

				final L1WeaponSkillType class_name = get_class(id, classname);
				if (class_name != null) {
					class_name.set_level(level);
					class_name.set_type1(type1);
					class_name.set_type2(type2);
					class_name.set_type3(type3);
					class_name.set_attr(attr);
					class_name.set_ac_mr(ac_mr);
					class_name.set_gfxid1(gfxid1);
					class_name.set_gfxid2(gfxid2);
					class_name.set_gfxid3(gfxid3);
					class_name.set_gfxid4(gfxid4);
					class_name.set_gfxid4_count(gfxid4_count);
					class_name.set_power(power);
					class_name.set_srcdmg(srcdmg);
					class_name.set_addsrcdmg(addsrcdmg);
					class_name.set_random1(random1);
					class_name.set_random2(random2);
					class_name.set_boss_holdout(boss_holdout);
				}

				_weaponIdIndex.put(id, class_name);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(cn);
		}
	}

	private void load2() {
		Connection cn = null;
		PreparedStatement pm = null;
		ResultSet rs = null;
		try {
			cn = DatabaseFactory.get().getConnection();
			pm = cn.prepareStatement("SELECT * FROM `weapon_skill_in`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int item_id = rs.getInt("item_id");
				if (ItemTable.get().getTemplate(item_id) == null) {
					_log.error("技能武器設置資料錯誤: 沒有這個編號的武器:" + item_id);
					continue;
				}
				final String powers = rs.getString("powers").replaceAll(" ", "");// 取代空白;

				ArrayList<L1WeaponSkillType> list = _weaponSkill.get(item_id);
				if (list == null) {
					list = new ArrayList<L1WeaponSkillType>();
				}

				if (!powers.equals("")) {
					final String[] set = powers.split(",");
					for (int i = 0; i < set.length; i++) {
						final int itemid = Integer.parseInt(set[i]);
						final L1WeaponSkillType class_name = _weaponIdIndex.get(itemid);
						list.add(class_name);
					}
				}

				_weaponSkill.put(item_id, list);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(cn);
		}
	}

	/**
	 * 加入CLASS清單
	 * 
	 * @param powerid
	 * @param className
	 * @return
	 */
	private L1WeaponSkillType get_class(final int id, final String className) {
		if (className.equals("0")) {
			return null;
		}
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("com.lineage.server.model.weaponskill.");
			stringBuilder.append(className);

			final Class<?> cls = Class.forName(stringBuilder.toString());
			final L1WeaponSkillType exe = (L1WeaponSkillType) cls.getMethod("get").invoke(null);
			return exe;

		} catch (final ClassNotFoundException e) {
			final String error = "發生[技能武器檔案]錯誤, 檢查檔案是否存在:" + className + " 技能武器編號:" + id;
			_log.error(error);

		} catch (final IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final IllegalAccessException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final InvocationTargetException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final NoSuchMethodException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/*
	 * public L1WeaponSkillType getTemp(final int id) { return
	 * _weaponIdIndex.get(id); }
	 */

	public ArrayList<L1WeaponSkillType> getTemplate(final int itemid) {
		return _weaponSkill.get(itemid);
	}
}
