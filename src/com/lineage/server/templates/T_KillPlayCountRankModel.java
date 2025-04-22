package com.lineage.server.templates;

/**
 * PK排行榜暫存
 * 
 * @author simlin
 */
public class T_KillPlayCountRankModel {

	private String _name;

	private int _type;

	private int _count;

	public T_KillPlayCountRankModel(final String name, final int type, final int count) {
		_name = name;
		_type = type;
		_count = count;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	public int getType() {
		return _type;
	}

	public void setType(final int type) {
		_type = type;
	}

	public int getCount() {
		return _count;
	}

	public void setCount(final int count) {
		_count = count;
	}
}