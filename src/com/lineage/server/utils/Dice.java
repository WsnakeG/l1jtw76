package com.lineage.server.utils;

import java.util.Random;

/**
 * 隨機質取回
 * 
 * @author daien
 */
public class Dice {

	private static final Random _rnd = new Random();

	private final int _faces;// 基礎值

	/**
	 * 隨機質取回
	 * 
	 * @param faces 基礎值
	 */
	public Dice(final int faces) {
		_faces = faces;
	}

	/**
	 * 基礎值
	 * 
	 * @return
	 */
	public int getFaces() {
		return _faces;
	}

	/**
	 * 單次隨機質
	 * 
	 * @return
	 */
	public int roll() {
		return _rnd.nextInt(_faces) + 1;
	}

	/**
	 * 多次隨機質
	 * 
	 * @param count 計算次數
	 * @return 多次隨機質總合
	 */
	public int roll(final int count) {
		int n = 0;
		for (int i = 0; i < count; i++) {
			n += this.roll();
		}
		return n;
	}
}
