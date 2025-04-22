package com.lineage.server.templates;

/**
 * 屬性武器系統(DB自製)
 * 
 * @author terry0412
 */
public class L1AttrWeapon {

	private final String _name; // 顯示在武器上的名稱

	private final int _stage; // 屬性階級 (請從1開始)

	private final int _chance; // 衝過機率 (1/1000)

	private final int _probability; // 發動機率 (1/1000)

	// type list
	private final double _type_bind; // 能力(束缚) (單位:秒)

	private final double _type_drain_hp; // 能力(吸血) (單位:傷害倍率)

	private final int _type_drain_mp; // 能力(吸魔) (單位:傷害隨機值)

	private final double _type_dmgup; // 額外傷害倍率

	private final int _type_range; // 範圍傷害 (XX格)

	private final int _type_range_dmg; // 範圍傷害 (固定輸出XX + 隨機0~50)

	private final int _type_light_dmg; // 光裂術傷害 (固定輸出XX + 隨機0~100)

	private final int _type_ice_dmg; // 冰裂術傷害 (固定輸出XX + 隨機0~50)

	private final boolean _type_skill_1; // 發動闇盲術 (0:沒效果 1:有效果)

	private final boolean _type_skill_2; // 發動魔法封印 (0:沒效果 1:有效果)

	private final boolean _type_skill_3; // 發動變形術 (0:沒效果 1:有效果)

	private final boolean _type_skill_4; // 發動緩速術 (0:沒效果 1:有效果)

	private final double _type_skill_time; // 被施展法術類後的秒數

	private final String[] _type_poly_list; // 被變身後的polyid (設置多樣隨機一種)

	private final boolean _type_remove_weapon; // 解除玩家武器 (0:沒效果 1:有效果)

	private final boolean _type_remove_doll; // 解除玩家的娃娃 (0:沒效果 1:有效果)

	private final int _type_remove_armor; // 解除玩家裝備 (含件數)

	public L1AttrWeapon(final String name, final int stage, final int chance, final int probability,
			final double type_bind, final double type_drain_hp, final int type_drain_mp,
			final double type_dmgup, final int type_range, final int type_range_dmg, final int type_light_dmg,
			final int type_ice_dmg, final boolean type_skill_1, final boolean type_skill_2,
			final boolean type_skill_3, final boolean type_skill_4, final double type_skill_time,
			final String[] type_poly_list, final boolean type_remove_weapon, final boolean type_remove_doll,
			final int type_remove_armor) {
		_name = name;
		_stage = stage;
		_chance = chance;
		_probability = probability;

		_type_bind = type_bind;
		_type_drain_hp = type_drain_hp;
		_type_drain_mp = type_drain_mp;
		_type_dmgup = type_dmgup;
		_type_range = type_range;
		_type_range_dmg = type_range_dmg;
		_type_light_dmg = type_light_dmg;
		_type_ice_dmg = type_ice_dmg;
		_type_skill_1 = type_skill_1;
		_type_skill_2 = type_skill_2;
		_type_skill_3 = type_skill_3;
		_type_skill_4 = type_skill_4;
		_type_skill_time = type_skill_time;
		_type_poly_list = type_poly_list;
		_type_remove_weapon = type_remove_weapon;
		_type_remove_doll = type_remove_doll;
		_type_remove_armor = type_remove_armor;
	}

	public final String getName() {
		return _name;
	}

	public final int getStage() {
		return _stage;
	}

	public final int getChance() {
		return _chance;
	}

	public final int getProbability() {
		return _probability;
	}

	public final double getTypeBind() {
		return _type_bind;
	}

	public final double getTypeDrainHp() {
		return _type_drain_hp;
	}

	public final int getTypeDrainMp() {
		return _type_drain_mp;
	}

	public final double getTypeDmgup() {
		return _type_dmgup;
	}

	public final int getTypeRange() {
		return _type_range;
	}

	public final int getTypeRangeDmg() {
		return _type_range_dmg;
	}

	public final int getTypeLightDmg() {
		return _type_light_dmg;
	}

	public final int getTypeiceDmg() {
		return _type_ice_dmg;
	}

	public final boolean getTypeSkill1() {
		return _type_skill_1;
	}

	public final boolean getTypeSkill2() {
		return _type_skill_2;
	}

	public final boolean getTypeSkill3() {
		return _type_skill_3;
	}

	public final boolean getTypeSkill4() {
		return _type_skill_4;
	}

	public final double getTypeSkillTime() {
		return _type_skill_time;
	}

	public final String[] getTypePolyList() {
		return _type_poly_list;
	}

	public final boolean getTypeRemoveWeapon() {
		return _type_remove_weapon;
	}

	public final boolean getTypeRemoveDoll() {
		return _type_remove_doll;
	}

	public final int getTypeRemoveArmor() {
		return _type_remove_armor;
	}
}
