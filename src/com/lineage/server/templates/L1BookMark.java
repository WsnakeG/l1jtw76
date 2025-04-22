package com.lineage.server.templates;

public class L1BookMark {

	private int _charId;

	private int _id;

	private String _name;

	private int _locX;

	private int _locY;

	private short _mapId;

	public int getId() {
		return _id;
	}

	public void setId(final int i) {
		_id = i;
	}

	public int getCharId() {
		return _charId;
	}

	public void setCharId(final int i) {
		_charId = i;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String s) {
		_name = s;
	}

	public int getLocX() {
		return _locX;
	}

	public void setLocX(final int i) {
		_locX = i;
	}

	public int getLocY() {
		return _locY;
	}

	public void setLocY(final int i) {
		_locY = i;
	}

	public short getMapId() {
		return _mapId;
	}

	public void setMapId(final short i) {
		_mapId = i;
	}
}