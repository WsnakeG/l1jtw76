package com.lineage.server.datatables;

import com.lineage.config.ConfigOther;

/**
 * 經驗質資料庫
 */
public final class ExpTable {

	public static final int MAX_LEVEL = 99;

	public static final long MAX_EXP = 1626853681L;

	private static final int LV50_EXP = 1;

	private static final int LV51_EXP = 1;

	private static final int LV52_EXP = 1;

	private static final int LV53_EXP = 1;

	private static final int LV54_EXP = 1;

	private static final int LV55_EXP = 1;

	private static final int LV56_EXP = 1;

	private static final int LV57_EXP = 1;

	private static final int LV58_EXP = 1;

	private static final int LV59_EXP = 1;

	private static final int LV60_EXP = 1;

	private static final int LV61_EXP = 1;

	private static final int LV62_EXP = 1;

	private static final int LV63_EXP = 1;

	private static final int LV64_EXP = 1;

	private static final int LV65_EXP = 2;

	private static final int LV66_EXP = 2;

	private static final int LV67_EXP = 2;

	private static final int LV68_EXP = 2;

	private static final int LV69_EXP = 2;

	private static final int LV70_EXP = 4;

	private static final int LV71_EXP = 4;

	private static final int LV72_EXP = 4;

	private static final int LV73_EXP = 4;

	private static final int LV74_EXP = 4;

	private static final int LV75_EXP = 8;

	private static final int LV76_EXP = 8;

	private static final int LV77_EXP = 8;

	private static final int LV78_EXP = 8;

	private static final int LV79_EXP = 16;

	private static final int LV80_EXP = 32;

	private static final int LV81_EXP = 32;

	private static final int LV82_EXP = 64;

	private static final int LV83_EXP = 64;

	private static final int LV84_EXP = 128;

	private static final int LV85_EXP = 128;

	private static final int LV86_EXP = 256;

	private static final int LV87_EXP = 512;

	private static final int LV88_EXP = 1024;

	private static final int LV89_EXP = 2048;

	private static final int LV90_EXP = ConfigOther.LV90EXP;//4096

	private static final int LV91_EXP = ConfigOther.LV91EXP;//8192

	private static final int LV92_EXP = ConfigOther.LV92EXP;//16384

	private static final int LV93_EXP = ConfigOther.LV93EXP;//32768

	private static final int LV94_EXP = ConfigOther.LV94EXP;//65536

	private static final int LV95_EXP = ConfigOther.LV95EXP;//131072

	private static final int LV96_EXP = ConfigOther.LV96EXP;//262144

	private static final int LV97_EXP = ConfigOther.LV97EXP;//524288

	private static final int LV98_EXP = ConfigOther.LV98EXP;//1048576

	private static final int LV99_EXP = ConfigOther.LV99EXP;//2097152

	/**
	 * 経験値テーブル(累積値) Lv0-65
	 */
	private static final int _expTable[] = { 0, 125, 300, 500, 750, 1296, 2401, 4096, 6561, 10000, 14641,
			20736, 28561, 38416, 50625, 65536, 83521, 104976, 130321, 160000, 194481, 234256, 279841, 331776,
			390625, 456976, 531441, 614656, 707281, 810000, 923521, 1048576, 1185921, 1336336, 1500625,
			1679616, 1874161, 2085136, 2313441, 2560000, 2825761, 3111696, 3418801, 3748096, 4100625, 4829985,
			6338401, 9833664, 19745853, 31292598, 44473900, 59289759, 75740173, 93825145, 113544672,
			134898756, 157887397, 182510594, 208768347, 236660657, 266187523, 297348946, 330144925, 364575461,
			400640553 };

	/**
	 * 死亡時経験値ペナルティテーブル
	 */
	private static final int _expPenalty[] = { LV50_EXP, LV51_EXP, LV52_EXP, LV53_EXP, LV54_EXP, LV55_EXP,
			LV56_EXP, LV57_EXP, LV58_EXP, LV59_EXP, LV60_EXP, LV61_EXP, LV62_EXP, LV63_EXP, LV64_EXP,
			LV65_EXP, LV66_EXP, LV67_EXP, LV68_EXP, LV69_EXP, LV70_EXP, LV71_EXP, LV72_EXP, LV73_EXP,
			LV74_EXP, LV75_EXP, LV76_EXP, LV77_EXP, LV78_EXP, LV79_EXP, LV80_EXP, LV81_EXP, LV82_EXP,
			LV83_EXP, LV84_EXP, LV85_EXP, LV86_EXP, LV87_EXP, LV88_EXP, LV89_EXP, LV90_EXP, LV91_EXP,
			LV92_EXP, LV93_EXP, LV94_EXP, LV95_EXP, LV96_EXP, LV97_EXP, LV98_EXP, LV99_EXP };

	private static ExpTable _instance;

	public static ExpTable get() {
		if (_instance == null) {
			_instance = new ExpTable();
		}
		return _instance;
	}

	/**
	 * 指定等級所需要的經驗直
	 * 
	 * @param level
	 * @return 所需要的經驗直
	 */
	public static long getExpByLevel(final int level) {
		if (level <= 65) {
			return _expTable[level - 1];
		}
		final long exp = ((level - 65) * 36065092L) + 400640553L;
		return exp;
	}

	/**
	 * 下一個等級需要的經驗直
	 * 
	 * @param level
	 * @return 所需要的經驗直
	 */
	public static long getNeedExpNextLevel(final int level) {
		return getExpByLevel(level + 1) - getExpByLevel(level);
	}

	/**
	 * 累積經驗直對應的等級
	 * 
	 * @param exp 累積経験値
	 * @return
	 */
	public static int getLevelByExp(final long exp) {
		int level;
		for (level = 1; level < MAX_LEVEL; level++) {
			if (exp < getExpByLevel(level + 1)) {
				break;
			}
		}
		return Math.min(level, MAX_LEVEL);
	}

	/**
	 * 經驗直百分比(寵物等級顯示使用)
	 * 
	 * @param level
	 * @param exp
	 * @return
	 */
	public static int getExpPercentage(final int level, final long exp) {
		return (int) (100.0 * ((double) (exp - getExpByLevel(level)) / (double) getNeedExpNextLevel(level)));
	}

	/**
	 * 目前等即可取回的經驗直
	 * 
	 * @param level
	 * @return
	 */
	public static double getPenaltyRate(final int level, final int levelmet) {
		if (level < 50) {
			return 1.0;
		}
		double expPenalty = 1.0;
		expPenalty = 1.0 / _expPenalty[level - 50];

		return expPenalty;
	}
}
