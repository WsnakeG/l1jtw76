package com.lineage.server.templates;

/**
 * 等級排行榜暫存
 * 
 * @author simlin
 */
public class T_LevelRankModel {

	private int _level;

	private String _name;

	private long _exp;

	private int _turnLife;

	private int _type;

	public T_LevelRankModel(final int level, final String name, final long exp, final int turnLife,
			final int type) {
		_level = level;
		_name = name;
		_exp = exp;
		_turnLife = turnLife;
		_type = type;
	}

	public int getLevel() {
		return _level;
	}

	public void setLevel(final int level) {
		_level = level;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	public long getExp() {
		return _exp;
	}

	public void setExp(final long exp) {
		_exp = exp;
	}

	public int getTurnLife() {
		return _turnLife;
	}

	public void setTurnLife(final int turnLife) {
		_turnLife = turnLife;
	}

	public int getType() {
		return _type;
	}

	public void setType(final int type) {
		_type = type;
	}
}