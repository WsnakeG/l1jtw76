package com.lineage.server.utils;

import java.util.Random;

/**
 * <p>
 * 最低値lowと最大値highによって囲まれた、数値の範囲を指定するクラス。
 * </p>
 * <p>
 * <b>このクラスは同期化されない。</b> 複数のスレッドが同時にこのクラスのインスタンスにアクセスし、
 * 1つ以上のスレッドが範囲を変更する場合、外部的な同期化が必要である。
 * </p>
 */
public class RangeInt {

	private static final Random _rnd = new Random();

	private final int _low;

	private final int _high;

	public RangeInt(final int low, final int high) {
		_low = low;
		_high = high;
	}

	public RangeInt(final RangeInt range) {
		this(range._low, range._high);
	}

	/**
	 * 数値iが、範囲内にあるかを返す。
	 * 
	 * @param i 数値
	 * @return 範囲内であればtrue
	 */
	public boolean includes(final int i) {
		return (_low <= i) && (i <= _high);
	}

	public static boolean includes(final int i, final int low, final int high) {
		return (low <= i) && (i <= high);
	}

	/**
	 * 数値iを、この範囲内に丸める。
	 * 
	 * @param i 数値
	 * @return 丸められた値
	 */
	public int ensure(final int i) {
		int r = i;
		r = (_low <= r) ? r : _low;
		r = (r <= _high) ? r : _high;
		return r;
	}

	/**
	 * @param n
	 * @param low
	 * @param high
	 * @return
	 */
	public static int ensure(final int n, final int low, final int high) {
		int r = n;
		r = (low <= r) ? r : low;
		r = (r <= high) ? r : high;
		return r;
	}

	/**
	 * この範囲内からランダムな値を生成する。
	 * 
	 * @return 範囲内のランダムな値
	 */
	public int randomValue() {
		return _rnd.nextInt(getWidth() + 1) + _low;
	}

	public int getLow() {
		return _low;
	}

	public int getHigh() {
		return _high;
	}

	public int getWidth() {
		return _high - _low;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RangeInt)) {
			return false;
		}
		final RangeInt range = (RangeInt) obj;
		return (_low == range._low) && (_high == range._high);
	}

	@Override
	public String toString() {
		return "low=" + _low + ", high=" + _high;
	}
}
