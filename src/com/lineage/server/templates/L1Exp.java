package com.lineage.server.templates;

public class L1Exp {

	private int _level;

	private long _exp;

	private double _expPenalty;

	/**
	 * @return 傳出 _level
	 */
	public int get_level() {
		return _level;
	}

	/**
	 * @param level 對 _level 進行設置
	 */
	public void set_level(final int level) {
		_level = level;
	}

	/**
	 * @return 傳出 _exp
	 */
	public long get_exp() {
		return _exp;
	}

	/**
	 * @param exp 對 _exp 進行設置
	 */
	public void set_exp(final long exp) {
		_exp = exp;
	}

	/**
	 * @return 傳出 _expPenalty
	 */
	public double get_expPenalty() {
		return _expPenalty;
	}

	/**
	 * @param penalty 對 _expPenalty 進行設置
	 */
	public void set_expPenalty(final double penalty) {
		_expPenalty = penalty;
	}

}
