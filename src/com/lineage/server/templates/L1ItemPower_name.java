package com.lineage.server.templates;

import java.sql.Timestamp;

/**
 * 古文字名稱記錄
 * 
 * @author daien
 */
public class L1ItemPower_name {

	private int _power_id;

	private String _power_name;

	private int _dice;

	public int get_power_id() {
		return _power_id;
	}

	public void set_power_id(final int _power_id) {
		this._power_id = _power_id;
	}

	public String get_power_name() {
		return _power_name;
	}

	public void set_power_name(final String _power_name) {
		this._power_name = _power_name;
	}

	public int get_dice() {
		return _dice;
	}

	public void set_dice(final int _dice) {
		this._dice = _dice;
	}

	private Timestamp _date_time;

	public final Timestamp get_date_time() {
		return _date_time;
	}

	public final void set_date_time(final Timestamp value) {
		_date_time = value;
	}
}