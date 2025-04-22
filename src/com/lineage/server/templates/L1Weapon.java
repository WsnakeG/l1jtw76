package com.lineage.server.templates;

import java.util.Random;

public class L1Weapon extends L1Item {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public L1Weapon() {
	}

	private static Random _random = new Random();

	private int _add_dmg_min = 0;

	private int _add_dmg_max = 0;

	public void set_add_dmg(final int add_dmg_min, final int add_dmg_max) {
		_add_dmg_min = add_dmg_min;
		_add_dmg_max = add_dmg_max;
	}

	@Override
	public int get_add_dmg() {
		int add_dmg = 0;
		if ((_add_dmg_min != 0) && (_add_dmg_max != 0)) {
			add_dmg = _add_dmg_min + _random.nextInt(_add_dmg_max - _add_dmg_min);
		}
		return add_dmg;
	}

	private int _range = 0; // ● 射程範囲

	@Override
	public int getRange() {
		return _range;
	}

	public void setRange(final int i) {
		_range = i;
	}

	private int _hitModifier = 0; // ● 命中率補正

	@Override
	public int getHitModifier() {
		return _hitModifier;
	}

	public void setHitModifier(final int i) {
		_hitModifier = i;
	}

	private int _dmgModifier = 0; // ● ダメージ補正

	@Override
	public int getDmgModifier() {
		return _dmgModifier;
	}

	public void setDmgModifier(final int i) {
		_dmgModifier = i;
	}

	private int _doubleDmgChance; // ● DB、クロウの発動確率

	@Override
	public int getDoubleDmgChance() {
		return _doubleDmgChance;
	}

	public void setDoubleDmgChance(final int i) {
		_doubleDmgChance = i;
	}

	private int _magicDmgModifier = 0; // ● 攻撃魔法のダメージ補正

	@Override
	public int getMagicDmgModifier() {
		return _magicDmgModifier;
	}

	public void setMagicDmgModifier(final int i) {
		_magicDmgModifier = i;
	}

	private int _canbedmg = 0; // ● 損傷の有無

	@Override
	public int get_canbedmg() {
		return _canbedmg;
	}

	public void set_canbedmg(final int i) {
		_canbedmg = i;
	}

	@Override
	public boolean isTwohandedWeapon() {
		final int weapon_type = getType();

		final boolean bool = ((weapon_type == 3) || (weapon_type == 4) || (weapon_type == 5)
				|| (weapon_type == 11) || (weapon_type == 12) || (weapon_type == 15) || (weapon_type == 16)
				|| (weapon_type == 18));

		return bool;
	}

	// 強化值影響的增減魔防值 by terry0412
	private int _influenceMr;

	public void setInfluenceMr(final int i) {
		_influenceMr = i;
	}

	@Override
	public int getInfluenceMr() {
		return _influenceMr;
	}

	// 強化值影響的增減魔攻值 by terry0412
	private int _influenceSp;

	public void setInfluenceSp(final int i) {
		_influenceSp = i;
	}

	@Override
	public int getInfluenceSp() {
		return _influenceSp;
	}

	// 強化值影響的增減HP值 by terry0412
	private int _influenceHp;

	public void setInfluenceHp(final int i) {
		_influenceHp = i;
	}

	@Override
	public int getInfluenceHp() {
		return _influenceHp;
	}

	// 強化值影響的增減MP值 by terry0412
	private int _influenceMp;

	public void setInfluenceMp(final int i) {
		_influenceMp = i;
	}

	@Override
	public int getInfluenceMp() {
		return _influenceMp;
	}

	// 強化值影響的增減傷害減免值 by terry0412
	private int _influenceDmgR;

	public void setInfluenceDmgR(final int i) {
		_influenceDmgR = i;
	}

	@Override
	public int getInfluenceDmgR() {
		return _influenceDmgR;
	}

	// 強化值影響的增減近距離命中以及近距離攻擊值 by terry0412
	private int _influenceHitAndDmg;

	public void setInfluenceHitAndDmg(final int i) {
		_influenceHitAndDmg = i;
	}

	@Override
	public int getInfluenceHitAndDmg() {
		return _influenceHitAndDmg;
	}

	// 強化值影響的增減遠距離命中以及遠距離攻擊值 by terry0412
	private int _influenceBowHitAndDmg;

	public void setInfluenceBowHitAndDmg(final int i) {
		_influenceBowHitAndDmg = i;
	}

	@Override
	public int getInfluenceBowHitAndDmg() {
		return _influenceBowHitAndDmg;
	}
}
