package com.lineage.server.templates;

/**
 * 購買技能 金幣/材料 設置資料
 * 
 * @author DaiEn
 */
public class L1SkillItem {

	private int _skill_id;// 技能編號

	/**
	 * 技能編號
	 * 
	 * @return
	 */
	public int get_skill_id() {
		return _skill_id;
	}

	/**
	 * 技能編號
	 * 
	 * @param i
	 */
	public void set_skill_id(final int i) {
		_skill_id = i;
	}

	private String _name;// 技能名稱

	/**
	 * 技能名稱
	 * 
	 * @return
	 */
	public String get_name() {
		return _name;
	}

	/**
	 * 技能名稱
	 * 
	 * @param s
	 */
	public void set_name(final String s) {
		_name = s;
	}

	private int[] _items;// 物件組

	/**
	 * 耗用物件組
	 * 
	 * @return
	 */
	public int[] get_items() {
		return _items;
	}

	/**
	 * 物件組
	 * 
	 * @param is
	 */
	public void set_items(final int[] is) {
		_items = is;
	}

	private int[] _counts;// 數量組

	/**
	 * 耗用數量組
	 * 
	 * @return
	 */
	public int[] get_counts() {
		return _counts;
	}

	/**
	 * 數量組
	 * 
	 * @param is
	 */
	public void set_counts(final int[] is) {
		_counts = is;
	}

	// private int _adena;// 金幣

	/**
	 * 耗用金幣
	 * 
	 * @return
	 */
	/*
	 * public int get_adena() { return this._adena; }
	 */

	/**
	 * 金幣
	 * 
	 * @param i
	 */
	/*
	 * public void set_adena(final int i) { this._adena = i; }
	 */
}
