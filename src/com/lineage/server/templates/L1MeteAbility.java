package com.lineage.server.templates;

/**
 * 轉生附加能力 資料暫存
 * 
 * @author terry0412
 */
public class L1MeteAbility {

	private final String _title; // 轉生封號

	private final int _ac; // 防禦

	private final int _hp; // 血量

	private final int _mp; // 魔量

	private final int _hpr; // 回血量

	private final int _mpr; // 回魔量

	private final int _str; // 力量

	private final int _con; // 體質

	private final int _dex; // 敏捷

	private final int _wis; // 精神

	private final int _cha; // 魅力

	private final int _int; // 智力

	private final int _sp; // 魔攻

	private final int _mr; // 魔防

	private final int _hit_modifier; // 近戰攻擊命中

	private final int _dmg_modifier; // 近戰攻擊傷害

	private final int _bow_hit_modifier; // 遠距攻擊命中

	private final int _bow_dmg_modifier; // 遠距攻擊傷害

	private final int _magic_dmg_modifier; // 額外魔法傷害

	private final int _magic_dmg_reduction; // 魔法傷害減免

	private final int _reduction_dmg; // 全傷害減免 (含物理與魔法傷害)

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

	private final int _expPenalty; // 轉生經驗減少

	private final int _physicsDmgUp; // 物理傷害增加+%

	private final int _magicDmgUp; // 魔法傷害增加+%

	private final int _physicsDmgDown; // 物理傷害減免+%

	private final int _magicDmgDown; // 魔法傷害減免+%

	private final int _magicHitUp; // 有害魔法成功率+%

	private final int _magicHitDown; // 抵抗有害魔法成功率+%

	private final int _physicsDoubleHit; // 物理暴擊發動機率+% (發動後普攻傷害*1.5倍)

	private final int _magicDoubleHit; // 魔法暴擊發動機率+% (發動後技能傷害*1.5倍)

	public L1MeteAbility(final String title, final int ac, final int hp, final int mp, final int hpr,
			final int mpr, final int str, final int con, final int dex, final int wis, final int cha,
			final int intel, final int sp, final int mr, final int hit_modifier, final int dmg_modifier,
			final int bow_hit_modifier, final int bow_dmg_modifier, final int magic_dmg_modifier,
			final int magic_dmg_reduction, final int reduction_dmg, final int defense_water,
			final int defense_wind, final int defense_fire, final int defense_earth, final int regist_stun,
			final int regist_stone, final int regist_sleep, final int regist_freeze, final int regist_sustain,
			final int regist_blind, final int expPenalty, final int physicsDmgUp, final int magicDmgUp,
			final int physicsDmgDown, final int magicDmgDown, final int magicHitUp, final int magicHitDown,
			final int physicsDoubleHit, final int magicDoubleHit) {
		_title = title;
		_ac = ac;
		_hp = hp;
		_mp = mp;
		_hpr = hpr;
		_mpr = mpr;
		_str = str;
		_con = con;
		_dex = dex;
		_wis = wis;
		_cha = cha;
		_int = intel;
		_sp = sp;
		_mr = mr;
		_hit_modifier = hit_modifier;
		_dmg_modifier = dmg_modifier;
		_bow_hit_modifier = bow_hit_modifier;
		_bow_dmg_modifier = bow_dmg_modifier;
		_magic_dmg_modifier = magic_dmg_modifier;
		_magic_dmg_reduction = magic_dmg_reduction;
		_reduction_dmg = reduction_dmg;
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
		_expPenalty = expPenalty;
		_physicsDmgUp = physicsDmgUp;
		_magicDmgUp = magicDmgUp;
		_physicsDmgDown = physicsDmgDown;
		_magicDmgDown = magicDmgDown;
		_magicHitUp = magicHitUp;
		_magicHitDown = magicHitDown;
		_physicsDoubleHit = physicsDoubleHit;
		_magicDoubleHit = magicDoubleHit;
	}

	public final String getTitle() {
		return _title;
	}

	public final int getAc() {
		return _ac;
	}

	public final int getHp() {
		return _hp;
	}

	public final int getMp() {
		return _mp;
	}

	public final int getHpr() {
		return _hpr;
	}

	public final int getMpr() {
		return _mpr;
	}

	public final int getStr() {
		return _str;
	}

	public final int getCon() {
		return _con;
	}

	public final int getDex() {
		return _dex;
	}

	public final int getWis() {
		return _wis;
	}

	public final int getCha() {
		return _cha;
	}

	public final int getInt() {
		return _int;
	}

	public final int getSp() {
		return _sp;
	}

	public final int getMr() {
		return _mr;
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

	public final int getMagicDmgModifier() {
		return _magic_dmg_modifier;
	}

	public final int getMagicDmgReduction() {
		return _magic_dmg_reduction;
	}

	public final int getReductionDmg() {
		return _reduction_dmg;
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

	public final int getExpPenalty() {
		return _expPenalty;
	}

	public final int getPhysicsDmgUp() {
		return _physicsDmgUp;
	}

	public final int getMagicDmgUp() {
		return _magicDmgUp;
	}

	public final int getPhysicsDmgDown() {
		return _physicsDmgDown;
	}

	public final int getMagicDmgDown() {
		return _magicDmgDown;
	}

	public final int getMagicHitUp() {
		return _magicHitUp;
	}

	public final int getMagicHitDown() {
		return _magicHitDown;
	}

	public final int getPhysicsDoubleHit() {
		return _physicsDoubleHit;
	}

	public final int getMagicDoubleHit() {
		return _magicDoubleHit;
	}
}
