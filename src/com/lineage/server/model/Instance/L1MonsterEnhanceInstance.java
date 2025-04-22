package com.lineage.server.model.Instance;

/**
 * 怪物強化系統
 * 
 * @author by erics4179<BR>
 *         <BR>
 *         怪物強化公式 - (current_dc / dc_enhance) * 屬性 + 原始屬性 = 最後屬性<BR>
 *         屬性 - monster_enhance裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr<BR>
 *         原始屬性 - npc裡面的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr<BR>
 *         最後屬性 - 怪物重生後的level、hp、mp、ac、str、dex、con、wis、int、mr、Hpr<BR>
 */
public class L1MonsterEnhanceInstance {

	/** Npcid */
	private int _npcid;

	public int getNpcId() {
		return _npcid;
	}

	public void setNpcId(int i) {
		_npcid = i;
	}

	/** 怪物的死亡次數(dc_enhance不等於0，才會累積喔!!) */
	private int _currentdc;

	public int getCurrentDc() {
		return _currentdc;
	}

	public void setCurrentDc(int i) {
		_currentdc = i;
	}

	/** 怪物死亡幾次強化一次 */
	private int _dcenhance;

	public int getDcEnhance() {
		return _dcenhance;
	}

	public void setDcEnhance(int i) {
		_dcenhance = i;
	}

	private int _level;

	public int getLevel() {
		return _level;
	}

	public void setLevel(int i) {
		_level = i;
	}

	private int _hp;

	public int getHp() {
		return _hp;
	}

	public void setHp(int i) {
		_hp = i;
	}

	private int _mp;

	public int getMp() {
		return _mp;
	}

	public void setMp(int i) {
		_mp = i;
	}

	private int _ac;

	public int getAc() {
		return _ac;
	}

	public void setAc(int i) {
		_ac = i;
	}

	private int _str;

	public int getStr() {
		return _str;
	}

	public void setStr(int i) {
		_str = i;
	}

	private int _dex;

	public int getDex() {
		return _dex;
	}

	public void setDex(int i) {
		_dex = i;
	}

	private int _con;

	public int getCon() {
		return _con;
	}

	public void setCon(int i) {
		_con = i;
	}

	private int _wis;

	public int getWis() {
		return _wis;
	}

	public void setWis(int i) {
		_wis = i;
	}

	private int _int;

	public int getInt() {
		return _int;
	}

	public void setInt(int i) {
		_int = i;
	}

	private int _mr;

	public int getMr() {
		return _mr;
	}

	public void setMr(int i) {
		_mr = i;
	}

	private int _Hpr;

	public int getHpr() {
		return _Hpr;
	}

	public void setHpr(int i) {
		_Hpr = i;
	}
}