package com.lineage.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactory;
import com.lineage.server.templates.L1ItemPower_name;
import com.lineage.server.templates.L1ItemPower_text;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 文字組合能力資料
 * 
 * @author dexc
 */
public class ItemPowerTable {

	private static final Log _log = LogFactory.getLog(ItemPowerTable.class);

	private static ItemPowerTable _instance;

	// 全部文字組合能力設置
	public static final HashMap<Integer, L1ItemPower_text> POWER_TEXT = new HashMap<Integer, L1ItemPower_text>();

	// 能力名稱清單
	public static final HashMap<Integer, L1ItemPower_name> POWER_NAME = new HashMap<Integer, L1ItemPower_name>();

	public static ItemPowerTable get() {
		if (_instance == null) {
			_instance = new ItemPowerTable();
		}
		return _instance;
	}

	private static int _max = 0;
	private static int _min = 1000;

	public int get_int() {
		return (_max - _min) / 2;
	}
	
	public void reload() {
		POWER_NAME.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection co = null;
		PreparedStatement pm = null;
		ResultSet rs = null;

		// 加載古文字內容
		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement("SELECT * FROM `server_item_power_name`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int power_id = rs.getInt("power_id");
				final String power_name = rs.getString("power_name");
				final int dice = rs.getInt("dice");

				if (POWER_NAME.get(power_id) == null) {
					final L1ItemPower_name name = new L1ItemPower_name();
					name.set_power_id(power_id);
					name.set_power_name(power_name);
					name.set_dice(dice);
					if (dice > _max) {
						_max = dice;
					}
					if (dice < _min) {
						_min = dice;
					}
					POWER_NAME.put(power_id, name);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}

		// 加載能力設置
		try {
			co = DatabaseFactory.get().getConnection();
			pm = co.prepareStatement("SELECT * FROM `server_item_power`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int key = rs.getInt("id");
				final String power_id = rs.getString("power_id");
				final int ac = rs.getInt("ac");
				final int hp = rs.getInt("hp");
				final int mp = rs.getInt("mp");
				final int hpr = rs.getInt("hpr");
				final int mpr = rs.getInt("mpr");
				final int mr = rs.getInt("mr");
				final int sp = rs.getInt("sp");
				final int hit = rs.getInt("hit");
				final int str = rs.getInt("str");
				final int dex = rs.getInt("dex");
				final int con = rs.getInt("con");
				final int inter = rs.getInt("inter");
				final int wis = rs.getInt("wis");
				final int cha = rs.getInt("cha");
				final int dmgup = rs.getInt("dmgup");
				final int bowhit = rs.getInt("bowhit");
				final int bowdmgup = rs.getInt("bowdmgup");
				final int dice_dmg = rs.getInt("dice_dmg");
				final int dmg = rs.getInt("dmg");
				final int dodge = rs.getInt("dodge");
				final int dice_hp = rs.getInt("dice_hp");
				final int sucking_hp = rs.getInt("sucking_hp");
				final int dice_mp = rs.getInt("dice_mp");
				final int sucking_mp = rs.getInt("sucking_mp");
				final int double_dmg = rs.getInt("double_dmg");
				final int lift = rs.getInt("lift");
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
				final String gfx = rs.getString("gfx");
				final String msg = rs.getString("msg");
				// Erics4179 160602 古文字系統新增八種能力
				final int physicsDmgUp = rs.getInt("physicsDmgUp"); // 物理傷害增加+%
				final int magicDmgUp = rs.getInt("magicDmgUp"); // 魔法傷害增加+%
				final int physicsDmgDown = rs.getInt("physicsDmgDown"); // 物理傷害減免+%
				final int magicDmgDown = rs.getInt("magicDmgDown"); // 魔法傷害減免+%
				final int magicHitUp = rs.getInt("magicHitUp"); // 有害魔法成功率+%
				final int magicHitDown = rs.getInt("magicHitDown"); // 抵抗有害魔法成功率+%
				final int physicsDoubleHit = rs.getInt("physicsDoubleHit"); // 物理暴擊發動機率+%
																			// (發動後普攻傷害*1.5倍)
				final int magicDoubleHit = rs.getInt("magicDoubleHit"); // 魔法暴擊發動機率+%
																		// (發動後技能傷害*1.5倍)

				final L1ItemPower_text text = new L1ItemPower_text();
				text.set_id(key);
				if ((power_id != null) && !power_id.equals("")) {
					final String[] power_ids = power_id.replaceAll(" ", "").split(",");
					final int[] out = new int[power_ids.length];
					for (int i = 0; i < power_ids.length; i++) {
						out[i] = Integer.parseInt(power_ids[i]);
					}
					text.setPower_id(out);// 套裝
				}

				text.setAc(ac);
				text.setHp(hp);
				text.setMp(mp);
				text.setHpr(hpr);
				text.setMpr(mpr);
				text.setMr(mr);
				text.setStr(str);
				text.setDex(dex);
				text.setCon(con);
				text.setInt(inter);
				text.setWis(wis);
				text.setCha(cha);
				text.setSp(sp);
				text.setHit(hit);
				text.setDmgup(dmgup);
				text.setBowhit(bowhit);
				text.setBowdmgup(bowdmgup);
				text.setDice_dmg(dice_dmg);
				text.setDmg(dmg);
				text.setDodge(dodge);
				text.setDice_hp(dice_hp);
				text.setSucking_hp(sucking_hp);
				text.setDice_mp(dice_mp);
				text.setSucking_mp(sucking_mp);
				text.setDouble_dmg(double_dmg);
				text.setLift(lift);
				text.setDefense_water(defense_water);
				text.setDefense_wind(defense_wind);
				text.setDefense_fire(defense_fire);
				text.setDefense_earth(defense_earth);
				text.setRegist_stun(regist_stun);
				text.setRegist_stone(regist_stone);
				text.setRegist_sleep(regist_sleep);
				text.setRegist_freeze(regist_freeze);
				text.setRegist_sustain(regist_sustain);
				text.setRegist_blind(regist_blind);
				// Erics4179 160602 古文字系統新增八種能力
				text.setphysicsDmgUp(physicsDmgUp);
				text.setmagicDmgUp(magicDmgUp);
				text.setphysicsDmgDown(physicsDmgDown);
				text.setmagicDmgDown(magicDmgDown);
				text.setmagicHitUp(magicHitUp);
				text.setmagicHitDown(magicHitDown);
				text.setphysicsDoubleHit(physicsDoubleHit);
				text.setmagicDoubleHit(magicDoubleHit);

				if ((gfx != null) && !gfx.equals("")) {
					final String[] gfxs = gfx.replaceAll(" ", "").split(",");
					final int[] out = new int[gfxs.length];
					for (int i = 0; i < gfxs.length; i++) {
						out[i] = Integer.parseInt(gfxs[i]);
					}
					text.setGfx(out);// 套裝效果動畫
				}
				text.setMsg(msg);

				POWER_TEXT.put(key, text);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
		_log.info("載入文字組合能力資料數量: " + POWER_TEXT.size() + " 文字:" + POWER_NAME.size() + "(" + timer.get()
				+ "ms)");
	}
}
