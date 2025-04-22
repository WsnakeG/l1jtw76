package com.lineage.server.templates;

public class L1Skills {

	public static final int ATTR_NONE = 0;

	public static final int ATTR_EARTH = 1;

	public static final int ATTR_FIRE = 2;

	public static final int ATTR_WATER = 4;

	public static final int ATTR_WIND = 8;

	public static final int ATTR_RAY = 16;

	public static final int TYPE_PROBABILITY = 1;

	public static final int TYPE_CHANGE = 2;

	public static final int TYPE_CURSE = 4;

	public static final int TYPE_DEATH = 8;

	public static final int TYPE_HEAL = 16;

	public static final int TYPE_RESTORE = 32;

	public static final int TYPE_ATTACK = 64;

	public static final int TYPE_OTHER = 128;

	/** 技能對自己 */
	public static final int TARGET_TO_ME = 0;

	/** 技能對人物 */
	public static final int TARGET_TO_PC = 1;

	/** 技能對NPC */
	public static final int TARGET_TO_NPC = 2;

	/** 技能對血盟 */
	public static final int TARGET_TO_CLAN = 4;

	/** 技能對隊伍 */
	public static final int TARGET_TO_PARTY = 8;

	/** 技能對寵物 */
	public static final int TARGET_TO_PET = 16;

	/** 技能對地點 */
	public static final int TARGET_TO_PLACE = 32;

	private int _skillId;

	public int getSkillId() {
		return _skillId;
	}

	public void setSkillId(final int i) {
		_skillId = i;
	}

	private String _name;

	public String getName() {
		return _name;
	}

	public void setName(final String s) {
		_name = s;
	}

	private int _skillLevel;

	/**
	 * 技能分級
	 * 
	 * @return
	 */
	public int getSkillLevel() {
		return _skillLevel;
	}

	/**
	 * 技能分級
	 * 
	 * @param i
	 */
	public void setSkillLevel(final int i) {
		_skillLevel = i;
	}

	private int _skillNumber;

	public int getSkillNumber() {
		return _skillNumber;
	}

	public void setSkillNumber(final int i) {
		_skillNumber = i;
	}

	private int _mpConsume;

	public int getMpConsume() {
		return _mpConsume;
	}

	public void setMpConsume(final int i) {
		_mpConsume = i;
	}

	private int _hpConsume;

	public int getHpConsume() {
		return _hpConsume;
	}

	public void setHpConsume(final int i) {
		_hpConsume = i;
	}

	private int _itmeConsumeId;

	public int getItemConsumeId() {
		return _itmeConsumeId;
	}

	public void setItemConsumeId(final int i) {
		_itmeConsumeId = i;
	}

	private int _itmeConsumeCount;

	public int getItemConsumeCount() {
		return _itmeConsumeCount;
	}

	public void setItemConsumeCount(final int i) {
		_itmeConsumeCount = i;
	}

	private int _reuseDelay; // 単位：ミリ秒

	public int getReuseDelay() {
		return _reuseDelay;
	}

	public void setReuseDelay(final int i) {
		_reuseDelay = i;
	}

	private int _buffDuration; // 效果時間(單位:秒)

	/**
	 * 效果時間(單位:秒)
	 * 
	 * @return
	 */
	public int getBuffDuration() {
		return _buffDuration;
	}

	/**
	 * 效果時間(單位:秒)
	 * 
	 * @param i
	 */
	public void setBuffDuration(final int i) {
		_buffDuration = i;
	}

	private String _target;

	public String getTarget() {
		return _target;
	}

	public void setTarget(final String s) {
		_target = s;
	}

	private int _targetTo; // 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置

	public int getTargetTo() {
		return _targetTo;
	}

	/**
	 * 施展對象
	 * 
	 * @param i 0:自己 1:玩家 2:NPC 4:血盟 8:隊伍 16:寵物 32:位置
	 */
	public void setTargetTo(final int i) {
		_targetTo = i;
	}

	private int _damageValue;

	/**
	 * 魔法基礎傷害
	 * 
	 * @return
	 */
	public int getDamageValue() {
		return _damageValue;
	}

	/**
	 * 魔法基礎傷害
	 * 
	 * @param i
	 */
	public void setDamageValue(final int i) {
		_damageValue = i;
	}

	private int _damageDice;

	/**
	 * 魔法基礎傷害隨機附加值
	 * 
	 * @return
	 */
	public int getDamageDice() {
		return _damageDice;
	}

	/**
	 * 魔法基礎傷害隨機附加值
	 * 
	 * @param i
	 */
	public void setDamageDice(final int i) {
		_damageDice = i;
	}

	private int _damageDiceCount;

	/**
	 * 魔法基礎傷害隨機附加值 附加次數
	 * 
	 * @return
	 */
	public int getDamageDiceCount() {
		return _damageDiceCount;
	}

	/**
	 * 魔法基礎傷害隨機附加值 附加次數
	 * 
	 * @param i
	 */
	public void setDamageDiceCount(final int i) {
		_damageDiceCount = i;
	}

	private int _probabilityValue;

	public int getProbabilityValue() {
		return _probabilityValue;
	}

	public void setProbabilityValue(final int i) {
		_probabilityValue = i;
	}

	private int _probabilityDice;

	/**
	 * 技能計算機率
	 * 
	 * @return
	 */
	public int getProbabilityDice() {
		return _probabilityDice;
	}

	/**
	 * 技能計算機率
	 * 
	 * @param i
	 */
	public void setProbabilityDice(final int i) {
		_probabilityDice = i;
	}

	private int _attr;// 魔法屬性

	/**
	 * 魔法屬性<br>
	 * 0.無屬性魔法,1.地屬性魔法,2.火屬性魔法,4.水屬性魔法,8.風屬性魔法,16.光屬性魔法
	 */
	public int getAttr() {
		return _attr;
	}

	public void setAttr(final int i) {
		_attr = i;
	}

	private int _type; // 魔法種類

	/**
	 * 魔法種類<br>
	 * 1:破壞 2:輔助 4:詛咒 8:死亡 16:治療 32:復活 64:攻擊 128:其他特殊
	 */
	public int getType() {
		return _type;
	}

	public void setType(final int i) {
		_type = i;
	}

	private int _lawful;

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(final int i) {
		_lawful = i;
	}

	private int _ranged;

	/**
	 * 施放距離
	 * 
	 * @return
	 */
	public int getRanged() {
		return _ranged;
	}

	/**
	 * 施放距離
	 * 
	 * @param i
	 */
	public void setRanged(final int i) {
		_ranged = i;
	}

	private int _area;

	/***
	 * 技能範圍
	 * 
	 * @return
	 */
	public int getArea() {
		return _area;
	}

	/**
	 * 技能範圍
	 * 
	 * @param i
	 */
	public void setArea(final int i) {
		_area = i;
	}

	boolean _isThrough;

	public boolean isThrough() {
		return _isThrough;
	}

	public void setThrough(final boolean flag) {
		_isThrough = flag;
	}

	private int _id;

	public int getId() {
		return _id;
	}

	public void setId(final int i) {
		_id = i;
	}

	private String _nameId;

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(final String s) {
		_nameId = s;
	}

	private int _actionId;

	/**
	 * 技能動作代號
	 * 
	 * @return
	 */
	public int getActionId() {
		return _actionId;
	}

	/**
	 * 技能動作代號
	 * 
	 * @param i
	 */
	public void setActionId(final int i) {
		_actionId = i;
	}

	private int _castGfx;

	public int getCastGfx() {
		return _castGfx;
	}

	public void setCastGfx(final int i) {
		_castGfx = i;
	}

	private int _castGfx2;

	public int getCastGfx2() {
		return _castGfx2;
	}

	public void setCastGfx2(final int i) {
		_castGfx2 = i;
	}

	private int _sysmsgIdHappen;

	public int getSysmsgIdHappen() {
		return _sysmsgIdHappen;
	}

	public void setSysmsgIdHappen(final int i) {
		_sysmsgIdHappen = i;
	}

	private int _sysmsgIdStop;

	public int getSysmsgIdStop() {
		return _sysmsgIdStop;
	}

	public void setSysmsgIdStop(final int i) {
		_sysmsgIdStop = i;
	}

	private int _sysmsgIdFail;

	public int getSysmsgIdFail() {
		return _sysmsgIdFail;
	}

	public void setSysmsgIdFail(final int i) {
		_sysmsgIdFail = i;
	}

}
