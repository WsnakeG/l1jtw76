package com.lineage.server.utils;

public class RandomArrayList {
	/** 泛用型隨機矩陣，所使用的指標 */
	private static int listint = 0;

	/** 新型泛用型，適用Int的正數範圍 */
	private static double[] ArrayDouble = new double[32767];

	static {
		for (listint = 0; listint < 32767; listint += 1) {
			ArrayDouble[listint] = Math.random();
		}

		haveNextGaussian = false;
	}

	public static void setArrayList() {
		for (listint = 0; listint < 32767; listint += 1) {
			ArrayDouble[listint] = Math.random();
		}
	}

	private static int getlistint() {
		if (listint < 32766) {
			return (++listint);
		}
		return (RandomArrayList.listint = 0);
	}

	/**
	 * getByte(byte[] 容器) ：模仿Random.nextBytes(byte[]) 製作
	 */
	public static void getByte(final byte[] arr) {
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = (byte) (int) getValue(128);
		}
	}

	private static boolean haveNextGaussian;
	private static double nextGaussian;

	/**
	 * getGaussian() ：回傳 高斯分配
	 */
	public static double getGaussian() {
		double v1;
		double v2;
		double s;
		if (haveNextGaussian) {
			haveNextGaussian = false;
			return nextGaussian;
		}
		do {
			v1 = (2.0D * ArrayDouble[getlistint()]) - 1.0D;
			v2 = (2.0D * ArrayDouble[getlistint()]) - 1.0D;
			s = (v1 * v1) + (v2 * v2);
		} while ((s >= 1.0D) || (s == 0.0D));
		final double multiplier = Math.sqrt((-2.0D * Math.log(s)) / s);
		nextGaussian = v2 * multiplier;
		haveNextGaussian = true;
		return (v1 * multiplier);
	}

	/**
	 * getValue() ：return between 0.0 and 1.0
	 */
	private static double getValue() {
		return ArrayDouble[getlistint()];
	}

	private static double getValue(final int rang) {
		return (getValue() * rang);
	}

	private static double getValue(final double rang) {
		return (getValue() * rang);
	}

	/**
	 * getInt(int 數值) 隨機值的?靜態，速度是nextInt(int 數值) 的數倍 根據呼叫的數值傳回
	 * 靜態表內加工後的數值,並採共同指標來決定傳回的依據. EX:getInt(92988) => 0~92987
	 * 
	 * @param rang - Int類型
	 * @return 0 ~ (數值-1)
	 */
	public static int getInt(final int rang) {
		return (int) getValue(rang);
	}

	public static int getInt(final double rang) {
		return (int) getValue(rang);
	}

	public static double getDouble() {
		return getValue();
	}

	public static double getDouble(final double rang) {
		return getValue(rang);
	}

	/**
	 * getInc(int 數值, int 輸出偏移值) 隨機值的?靜態，速度是nextInt(int 數值) 的數倍 根據呼叫的數值傳回
	 * 靜態表內加工後的數值,並採共同指標來決定傳回的依據. EX:getInc(92988, 10) => (0~92987) + 10 =>
	 * 10~92997
	 * 
	 * @param rang - Int類型
	 * @param increase - 修正輸出結果的範圍
	 * @return 0 ~ (數值-1) + 輸出偏移值
	 */
	public static int getInc(final int rang, final int increase) {
		return (getInt(rang) + increase);
	}

	public static int getInc(final double rang, final int increase) {
		return (getInt(rang) + increase);
	}

	public static double getDc(final int rang, final int increase) {
		return (getValue(rang) + increase);
	}

	public static double getDc(final double rang, final int increase) {
		return (getValue(rang) + increase);
	}
}