/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.lineage.server.utils;

/*
 * import jp.l1j.server.random.RandomGenerator; import
 * jp.l1j.server.random.RandomGeneratorFactory;
 */
/**
 * <p>
 * 最低値lowと最大値highによって囲まれた、数値の範囲を指定するクラス。
 * </p>
 * <p>
 * <b>このクラスは同期化されない。</b> 複数のスレッドが同時にこのクラスのインスタンスにアクセスし、
 * 1つ以上のスレッドが範囲を変更する場合、外部的な同期化が必要である。
 * </p>
 */
public class IntRange {
	/*
	 * private static final RandomGenerator _rnd = RandomGeneratorFactory
	 * .newRandom();
	 */
	private final int _low;
	private final int _high;

	public IntRange(final int low, final int high) {
		_low = low;
		_high = high;
	}

	public IntRange(final IntRange range) {
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

	/**
	 * 数値<code>i</code>が、<code>low</code>以上<code>high</code>以下の範囲内にあるかを返す。
	 * 
	 * @param i 数値
	 * @param low 最小値
	 * @param high 最大値
	 * @return 範囲内であればtrue
	 */
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
	/*
	 * public int randomValue() { return _rnd.nextInt(getWidth() + 1) + _low; }
	 */

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
		if (!(obj instanceof IntRange)) {
			return false;
		}
		final IntRange range = (IntRange) obj;
		return (_low == range._low) && (_high == range._high);
	}

	@Override
	public String toString() {
		return "low=" + _low + ", high=" + _high;
	}
}
