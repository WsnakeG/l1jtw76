package com.lineage.server.model;

import java.util.ArrayList;

public class L1ObjectAmount<T> {

	private final T _obj;

	private final long _amount;

	private int _enchatLevel;

	private int _bless;

	private int _random;

	private ArrayList<L1ObjectAmount<Integer>> _list;

	public L1ObjectAmount(final T obj, final long amount, final int enchantLevel, final int bless) {
		this._obj = obj;
		this._amount = amount;
		this._enchatLevel = enchantLevel;
		this._bless = bless;
	}

	public L1ObjectAmount(final T _obj, final long _amount, final int enchantLevel, final int bless,
			final int random) {
		this._obj = _obj;
		this._amount = _amount;
		this._enchatLevel = enchantLevel;
		this._bless = bless;
		this._random = random;
	}

	public L1ObjectAmount(final T obj, final long amount) {
		this._obj = obj;
		this._amount = amount;
	}

	public T getObject() {
		return this._obj;
	}

	public long getAmount() {
		return this._amount;
	}

	public ArrayList<L1ObjectAmount<Integer>> getAmountList() {
		return this._list;
	}

	public void setAmountList(final ArrayList<L1ObjectAmount<Integer>> substitutes) {
		this._list = substitutes;
	}

	public int getAmountEnchantLevel() {
		return this._enchatLevel;
	}

	public void setAmountEnchantLevel(final int enchantLevel) {
		this._enchatLevel = enchantLevel;
	}

	public int getAmountBless() {
		return this._bless;
	}

	public void setAmountBless(final int bless) {
		this._bless = bless;
	}

	public int getAmountRandom() {
		return this._random;
	}

	public void setAmountRandom(final int random) {
		this._random = random;
	}
}
