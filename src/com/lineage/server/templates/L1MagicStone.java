/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.templates;

/**
 * 寶石鑲嵌系統(DB自製)
 * 
 * @author terry0412
 */
public class L1MagicStone {

	private final int _item_id; // 寶石道具ID

	private final String _name; // 鑲嵌後顯示在裝備後的名稱

	private final int _stage; // 寶石階級 (請從1開始)

	private final int _use_type; // 允許使用對象 (0 = 無限制, 1 = 武器, 2 = 防具)

	private final int _chance; // 鑲嵌成功機率

	private final int _need_hole; // 鑲嵌所需孔數

	private final boolean _delete_hole; // 鑲嵌失敗是否扣除孔數 (1 = 是, 0 = 否)

	private final int _add_str; // 力量

	private final int _add_con; // 體質

	private final int _add_dex; // 敏捷

	private final int _add_int; // 智力

	private final int _add_wis; // 精神

	private final int _add_cha; // 魅力

	private final int _add_hp; // 血量

	private final int _add_mp; // 魔量

	private final int _hit_modifier; // 近戰攻擊命中

	private final int _dmg_modifier; // 近戰攻擊傷害

	private final int _bow_hit_modifier; // 遠距攻擊命中

	private final int _bow_dmg_modifier; // 遠距攻擊傷害

	private final int _add_ac; // 防禦

	private final int _m_def; // 魔防

	private final int _add_sp; // 魔攻

	private final int _defense_water; // 水屬性防禦

	private final int _defense_wind; // 風屬性防禦

	private final int _defense_fire; // 火屬性防禦

	private final int _defense_earth; // 地屬性防禦

	private final int _regist_stun; // 昏迷耐性

	private final int _regist_stone; // 石化耐性

	private final int _regist_sleep; // 睡眠耐性

	private final int _regist_freeze; // 寒冰耐性

	private final int _regist_sustain; // 支撐耐性

	private final int _regist_blind; // 闇黑耐性

	private final int _physicsDmgUp; // 物理傷害增加+%

	private final int _magicDmgUp; // 魔法傷害增加+%

	private final int _physicsDmgDown; // 物理傷害減免+%

	private final int _magicDmgDown; // 魔法傷害減免+%

	private final int _magicHitUp; // 有害魔法成功率+%

	private final int _magicHitDown; // 抵抗有害魔法成功率+%

	private final int _physicsDoubleHit; // 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)

	private final int _magicDoubleHit; // 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)

	public L1MagicStone(final int item_id, final String name, final int stage, final int use_type,
			final int chance, final int need_hole, final boolean delete_hole, final int add_str,
			final int add_con, final int add_dex, final int add_int, final int add_wis, final int add_cha,
			final int add_hp, final int add_mp, final int hit_modifier, final int dmg_modifier,
			final int bow_hit_modifier, final int bow_dmg_modifier, final int add_ac, final int m_def,
			final int add_sp, final int defense_water, final int defense_wind, final int defense_fire,
			final int defense_earth, final int regist_stun, final int regist_stone, final int regist_sleep,
			final int regist_freeze, final int regist_sustain, final int regist_blind, final int physicsDmgUp,
			final int magicDmgUp, final int physicsDmgDown, final int magicDmgDown, final int magicHitUp,
			final int magicHitDown, final int physicsDoubleHit, final int magicDoubleHit) {
		_item_id = item_id;
		_name = name;
		_stage = stage;
		_use_type = use_type;
		_chance = chance;
		_need_hole = need_hole;
		_delete_hole = delete_hole;
		_add_str = add_str;
		_add_con = add_con;
		_add_dex = add_dex;
		_add_int = add_int;
		_add_wis = add_wis;
		_add_cha = add_cha;
		_add_hp = add_hp;
		_add_mp = add_mp;
		_hit_modifier = hit_modifier;
		_dmg_modifier = dmg_modifier;
		_bow_hit_modifier = bow_hit_modifier;
		_bow_dmg_modifier = bow_dmg_modifier;
		_add_ac = add_ac;
		_m_def = m_def;
		_add_sp = add_sp;
		_defense_water = defense_water;
		_defense_wind = defense_wind;
		_defense_fire = defense_fire;
		_defense_earth = defense_earth;
		_regist_stun = regist_stun;
		_regist_stone = regist_stone;
		_regist_sleep = regist_sleep;
		_regist_freeze = regist_freeze;
		_regist_sustain = regist_sustain;
		_regist_blind = regist_blind;
		_physicsDmgUp = physicsDmgUp;
		_magicDmgUp = magicDmgUp;
		_physicsDmgDown = physicsDmgDown;
		_magicDmgDown = magicDmgDown;
		_magicHitUp = magicHitUp;
		_magicHitDown = magicHitDown;
		_physicsDoubleHit = physicsDoubleHit;
		_magicDoubleHit = magicDoubleHit;
	}

	public final int getItemId() {
		return _item_id;
	}

	public final String getName() {
		return _name;
	}

	public final int getStage() {
		return _stage;
	}

	public final int getUseType() {
		return _use_type;
	}

	public final int getChance() {
		return _chance;
	}

	public final int getNeedHole() {
		return _need_hole;
	}

	public final boolean isDeleteHole() {
		return _delete_hole;
	}

	public final int getAddStr() {
		return _add_str;
	}

	public final int getAddCon() {
		return _add_con;
	}

	public final int getAddDex() {
		return _add_dex;
	}

	public final int getAddInt() {
		return _add_int;
	}

	public final int getAddWis() {
		return _add_wis;
	}

	public final int getAddCha() {
		return _add_cha;
	}

	public final int getAddHp() {
		return _add_hp;
	}

	public final int getAddMp() {
		return _add_mp;
	}

	public final int getHitModifier() {
		return _hit_modifier;
	}

	public final int getDmgModifier() {
		return _dmg_modifier;
	}

	public final int getBowHitModifier() {
		return _bow_hit_modifier;
	}

	public final int getBowDmgModifier() {
		return _bow_dmg_modifier;
	}

	public final int getAddAc() {
		return _add_ac;
	}

	public final int getMdef() {
		return _m_def;
	}

	public final int getAddSp() {
		return _add_sp;
	}

	public final int getDefenseWater() {
		return _defense_water;
	}

	public final int getDefenseWind() {
		return _defense_wind;
	}

	public final int getDefenseFire() {
		return _defense_fire;
	}

	public final int getDefenseEarth() {
		return _defense_earth;
	}

	public final int getRegistStun() {
		return _regist_stun;
	}

	public final int getRegistStone() {
		return _regist_stone;
	}

	public final int getRegistSleep() {
		return _regist_sleep;
	}

	public final int getRegistFreeze() {
		return _regist_freeze;
	}

	public final int getRegistSustain() {
		return _regist_sustain;
	}

	public final int getRegistBlind() {
		return _regist_blind;
	}
	
	/**
	 * 增加物理傷害 %
	 */
	public final int getPhysicsDmgUp() {
		return _physicsDmgUp;
	}
	
	/**
	 * 增加魔法傷害 %
	 */
	public final int getMagicDmgUp() {
		return _magicDmgUp;
	}
	
	/**
	 * 增加物理減免 %
	 */
	public final int getPhysicsDmgDown() {
		return _physicsDmgDown;
	}
	
	/**
	 * 增加魔傷減免 %
	 */
	public final int getMagicDmgDown() {
		return _magicDmgDown;
	}
	
	/**
	 * 增加有害魔法命中 %
	 */
	public final int getMagicHitUp() {
		return _magicHitUp;
	}
	
	/**
	 * 增加有害魔法抵抗 %
	 */
	public final int getMagicHitDown() {
		return _magicHitDown;
	}
	
	/**
	 * 增加物理爆擊傷害 %
	 */
	public final int getPhysicsDoubleHit() {
		return _physicsDoubleHit;
	}
	
	/**
	 * 增加魔法爆擊傷害 %
	 */
	public final int getMagicDoubleHit() {
		return _magicDoubleHit;
	}
}
