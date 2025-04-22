package com.lineage.server.templates;

import java.util.Calendar;

/**
 * 城堡數據暫存
 * 
 * @author DaiEn
 */
public class L1Castle {

	/**
	 * 城堡數據暫存
	 * 
	 * @param id 城堡編號
	 * @param name 城堡名稱
	 */
	public L1Castle(final int id, final String name) {
		_id = id;
		_name = name;
	}

	private final int _id;

	public int getId() {
		return _id;
	}

	private final String _name;

	public String getName() {
		return _name;
	}

	private Calendar _warTime;

	public Calendar getWarTime() {
		return _warTime;
	}

	public void setWarTime(final Calendar i) {
		_warTime = i;
	}

	private int _taxRate;

	public int getTaxRate() {
		return _taxRate;
	}

	public void setTaxRate(final int i) {
		_taxRate = i;
	}

	private long _publicMoney;

	public long getPublicMoney() {
		return _publicMoney;
	}

	public void setPublicMoney(final long i) {
		_publicMoney = i;
	}

}
