/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package com.lineage.server.utils;

public class Random {
	private static final int _max = Short.MAX_VALUE;

	private static int _idx = 0;

	private static double[] _value = new double[_max + 1];

	static {
		for (_idx = 0; _idx < _max + 1; _idx++) {
			_value[_idx] = (Math.random() + Math.random() + Math.random()
					+ Math.random() + Math.random()) % 1.0;
		}
	}

	// 取得整數
	public static int nextInt(int n) {
		_idx = _idx & _max;
		return (int) (_value[_idx++] * n);
	}

	// 取得整數+偏移
	public static int nextInt(int n, int offset) {
		_idx = _idx & _max;
		return offset + (int) (_value[_idx++] * n);
	}

	// 隨機布林值
	public static boolean nextBoolean() {
		return (nextInt(2) == 1);
	}

	// 隨機位元
	public static byte nextByte() {
		return (byte) nextInt(256);
	}

	// 隨機長整數
	public static long nextLong() {
		long l = nextInt(Integer.MAX_VALUE) << 32 + nextInt(Integer.MAX_VALUE);
		return l;
	}

	public static int getInt(int rang) {
		return (int) (getValue() * rang);
	}

	private static double getValue() {
		return _nArray[getIndex()];
	}

	// 泛用型随机矩阵，所使用的指标
	private static int _nIndex = 0;

	// 新型泛用型，适用Int的正数范围
	private static double[] _nArray = new double[0x7FFF + 1];

	static {
		do {
			_nArray[_nIndex] = Math.random();
		} while (getIndex() != 0x00);
	}

	private static int getIndex() {
		return _nIndex = 0x7FFF & (++_nIndex);
	}
}