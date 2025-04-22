package com.lineage.server.templates;

public class L1Armor extends L1Item {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public L1Armor() {
	}

	private int _ac; // ● ＡＣ

	@Override
	public int get_ac() {
		return _ac;
	}

	public void set_ac(final int i) {
		_ac = i;
	}

	private int _damageReduction; // ● ダメージ軽減

	@Override
	public int getDamageReduction() {
		return _damageReduction;
	}

	public void setDamageReduction(final int i) {
		_damageReduction = i;
	}

	private int _weightReduction; // ● 重量軽減

	@Override
	public int getWeightReduction() {
		return _weightReduction;
	}

	public void setWeightReduction(final int i) {
		_weightReduction = i;
	}

	private int _hitModifierByArmor; // ● 命中率補正

	@Override
	public int getHitModifierByArmor() {
		return _hitModifierByArmor;
	}

	public void setHitModifierByArmor(final int i) {
		_hitModifierByArmor = i;
	}

	private int _dmgModifierByArmor; // ● ダメージ補正

	@Override
	public int getDmgModifierByArmor() {
		return _dmgModifierByArmor;
	}

	public void setDmgModifierByArmor(final int i) {
		_dmgModifierByArmor = i;
	}

	private int _bowHitModifierByArmor = 0; // ● 弓の命中率補正

	@Override
	public int getBowHitModifierByArmor() {
		return _bowHitModifierByArmor;
	}

	public void setBowHitModifierByArmor(final int i) {
		_bowHitModifierByArmor = i;
	}

	private int _bowDmgModifierByArmor = 0; // ● 弓のダメージ補正

	@Override
	public int getBowDmgModifierByArmor() {
		return _bowDmgModifierByArmor;
	}

	public void setBowDmgModifierByArmor(final int i) {
		_bowDmgModifierByArmor = i;
	}

	private int _defense_water = 0; // 增加水屬性

	/**
	 * 增加水屬性
	 * 
	 * @param i
	 */
	public void set_defense_water(final int i) {
		_defense_water = i;
	}

	@Override
	public int get_defense_water() {
		return _defense_water;
	}

	private int _defense_wind = 0; // 增加風屬性

	/**
	 * 增加風屬性
	 * 
	 * @param i
	 */
	public void set_defense_wind(final int i) {
		_defense_wind = i;
	}

	@Override
	public int get_defense_wind() {
		return _defense_wind;
	}

	private int _defense_fire = 0; // 增加火屬性

	/**
	 * 增加火屬性
	 * 
	 * @param i
	 */
	public void set_defense_fire(final int i) {
		_defense_fire = i;
	}

	@Override
	public int get_defense_fire() {
		return _defense_fire;
	}

	private int _defense_earth = 0; // 增加地屬性

	/**
	 * 增加地屬性
	 * 
	 * @param i
	 */
	public void set_defense_earth(final int i) {
		_defense_earth = i;
	}

	@Override
	public int get_defense_earth() {
		return _defense_earth;
	}

	private int _regist_stun = 0; // 昏迷耐性

	/**
	 * 昏迷耐性
	 * 
	 * @param i
	 */
	public void set_regist_stun(final int i) {
		_regist_stun = i;
	}

	@Override
	public int get_regist_stun() {
		return _regist_stun;
	}

	private int _regist_stone = 0; // 石化耐性

	/**
	 * 石化耐性
	 * 
	 * @param i
	 */
	public void set_regist_stone(final int i) {
		_regist_stone = i;
	}

	@Override
	public int get_regist_stone() {
		return _regist_stone;
	}

	private int _regist_sleep = 0; // 睡眠耐性

	/**
	 * 睡眠耐性
	 * 
	 * @param i
	 */
	public void set_regist_sleep(final int i) {
		_regist_sleep = i;
	}

	@Override
	public int get_regist_sleep() {
		return _regist_sleep;
	}

	private int _regist_freeze = 0; // 寒冰耐性

	/**
	 * 寒冰耐性
	 * 
	 * @param i
	 */
	public void set_regist_freeze(final int i) {
		_regist_freeze = i;
	}

	@Override
	public int get_regist_freeze() {
		return _regist_freeze;
	}

	private int _regist_sustain = 0; // 支撑耐性

	/**
	 * 支撑耐性
	 * 
	 * @param i
	 */
	public void set_regist_sustain(final int i) {
		_regist_sustain = i;
	}

	@Override
	public int get_regist_sustain() {
		return _regist_sustain;
	}

	private int _regist_blind = 0; // 暗黑耐性

	/**
	 * 暗黑耐性
	 * 
	 * @param i
	 */
	public void set_regist_blind(final int i) {
		_regist_blind = i;
	}

	@Override
	public int get_regist_blind() {
		return _regist_blind;
	}

	private int _greater = 3; // 強度

	public void set_greater(final int greater) {
		_greater = greater;
	}

	@Override
	public int get_greater() {
		return _greater;
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

	// 強化值影響的增減幸運值 by terry0412
	private int _influenceLuck;

	public void setInfluenceLuck(final int i) {
		_influenceLuck = i;
	}

	@Override
	public int getInfluenceLuck() {
		return _influenceLuck;
	}

	// 是否為活動戒指或收費戒指 by terry0412
	private boolean _activity;

	public void setActivity(final boolean i) {
		_activity = i;
	}

	@Override
	public boolean isActivity() {
		return _activity;
	}
}
