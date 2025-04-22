/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.lineage.server.model.classes;

import com.lineage.config.ConfigCharSetting;
import com.lineage.server.utils.IntRange;
import com.lineage.server.utils.RandomArrayList;

public abstract class L1ClassFeature {
	
	/**
	 * 職業專屬模組資料
	 */
	private static final L1ClassFeature[] _classFeatures = new L1ClassFeature[8];
	
	static {
		int i = 0;
		_classFeatures[i++] = new L1RoyalClassFeature();// 王族 
		_classFeatures[i++] = new L1KnightClassFeature();// 騎士
		_classFeatures[i++] = new L1ElfClassFeature();// 妖精
		_classFeatures[i++] = new L1WizardClassFeature();// 法師 
		_classFeatures[i++] = new L1DarkElfClassFeature();// 黑妖 
		_classFeatures[i++] = new L1DragonKnightClassFeature();// 龍騎 
		_classFeatures[i++] = new L1IllusionistClassFeature();// 幻術
		_classFeatures[i++] = new L1WarriorClassFeature();// 戰士 
	}
	
	/**
	 * 返回職業專屬計算模組
	 * @param key (職業type)
	 * @return
	 */
	public static L1ClassFeature newClassFeature(final int key) {
		if (!IntRange.includes(key, 0, 7)) {
			throw new IllegalArgumentException();
		}
		return _classFeatures[key];
	}
	
	/**
	 * 職業魔法等級
	 * @param playerLevel
	 * @return
	 */
	public abstract int getMagicLevel(int playerLevel);
	
	/**
	 * 職業AC補正
	 * @param ac
	 * @return
	 */
	public abstract int getAcDefenseMax(int ac);
	
	/**
	 * 職業等級對AC的增益
	 * @param playerLevel
	 * @return
	 */
	public abstract int getAcLevel(int playerLevel);
	
	/**
	 * 職業初始抗魔補正
	 * @return
	 */
	public abstract int getClassOriginalMr();
	
	/**
	 * 初始能力未分配力量 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_STR = new int[] { 13, 16, 10, 8, 15, 13, 9, 16};
	
	/**
	 * 初始能力未分配敏捷 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_DEX = new int[] { 9, 12, 12, 7, 12, 11, 10, 13 };
	
	/**
	 * 初始能力未分配體質 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_CON = new int[] { 11, 16, 12, 12, 12, 14, 12, 16 };
	
	/**
	 * 初始能力未分配精神 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術  7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_WIS = new int[] { 11, 9, 12, 14, 10, 10, 14, 7 };
	
	/**
	 * 初始能力未分配魅力 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_CHA = new int[] { 13, 10, 9, 8, 8, 8, 8, 9 };
	
	/**
	 * 初始能力未分配智力 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_INT = new int[] { 9, 8, 12, 14, 11, 10, 12, 10 };
	
	/**
	 * 初始能力可分配最大力量 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXSTR = new int[] { 20, 20, 18, 20, 20, 20, 19, 20 };
	
	/**
	 * 初始能力可分配最大敏捷 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXDEX = new int[] { 18, 16, 20, 19, 19, 20, 20, 17 };
	
	/**
	 * 初始能力可分配最大體質 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXCON = new int[] { 20, 20, 20, 20, 19, 20, 20, 20 };
	
	/**
	 * 初始能力可分配最大精神 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXWIS = new int[] { 20, 13, 20, 20, 17, 19, 20, 11 };
	
	/**
	 * 初始能力可分配最大魅力 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXCHA = new int[] { 20, 14, 17, 20, 15, 17, 18, 13 };
	
	/**
	 * 初始能力可分配最大智力 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C change
	 */
	public static final int[] ORIGINAL_MAXINT = new int[] { 18, 12, 20, 20, 18, 19, 20, 14 };
	
	/**
	 * 初始化人物hp陣列  0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士  XXX 7.6C ADD
	 */
	public static final int[] INIT_HP = {14, 16, 15, 12, 12, 16, 14, 16};
	
	/**
	 * 初始化人物mp陣列  0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C ADD
	 */
	public static final int[] INIT_MP = {2, 1, 4, 6, 3, 1, 5, 0};
	
	/**
	 * 初始化人物空身精神增加mp陣列  0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C ADD
	 */
	public static final int[][] INIT_BASE_WIS_MP_UP = {
	// 11, 9, 12, 14, 10, 10, 14, 7
	//wis:0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3},// 王族
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},// 騎士
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 3},// 妖精
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 4},// 法師
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 3},// 黑妖	
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},// 龍騎
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 3}, // 幻術
		{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 戰士
	};
	
	/**
	 * 初始化人物hp  XXX 7.6C ADD
	 * @param chartype
	 * @return hp
	 */
	public static int calcInitHp(final int chartype) {
		return INIT_HP[chartype];
	}
	
	/**
	 * 初始化人物 mp  XXX 7.6C ADD
	 * @param chartype
	 * @param wis
	 * @return
	 */
	public static int calcInitMp(final int chartype, final int wis) {
		return wis <= 20 ? INIT_MP[chartype] + INIT_BASE_WIS_MP_UP[chartype][wis] : INIT_MP[chartype] + INIT_BASE_WIS_MP_UP[chartype][20];	
	}
	
	
	/**
	 * 力量追加傷害值(力量值計算範圍0~255)  XXX 7.6C add
	 */
	private static final int[] _strDmg = { 
		-2, // str 0
		-1, -1, 0, 0, 1, 1, 2, 2, 2, 3,// str 1~10
		3, 4, 4, 5, 5, 6, 6, 7, 7, 8,// str 11~20
		8, 9, 9, 10, 10, 11, 11, 12, 12, 13,// str 21~30
		13, 14, 14, 15, 15, 16, 16, 17, 17, 18, // str 31~40
		18, 19, 19, 20, 20, 21, 21, 22, 22, 23, // str 41~50
		23, 24, 24, 25, 25, 26, 26, 27, 27, 28, // str 51~60
		28, 29, 29, 30, 30, 31, 31, 32, 32, 33, // str 61~70
		33, 34, 34, 35, 35, 36, 36, 37, 37, 38, 
		38, 39, 39, 40, 40, 41, 41, 42, 42, 43, 
		43, 44, 44, 45, 45, 46, 46, 47, 47, 48, 
		48, 49, 49, 50, 50, 51, 51, 52, 52, 53, 
		53, 54, 54, 55, 55, 56, 56, 57, 57, 58, 
		58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 
		63, 64, 64, 65, 65, 66, 66, 67, 67, 68, 
		68, 69, 69, 70, 70, 71, 71, 72, 72, 73, 
		73, 74, 74, 75, 75, 76, 76, 77, 77, 78, 
		78, 79, 79, 80, 80, 81, 81, 82, 82, 83, 
		83, 84, 84, 85, 85, 86, 86, 87, 87, 88, 
		88, 89, 89, 90, 90, 91, 91, 92, 92, 93, 
		93, 94, 94, 95, 95, 96, 96, 97, 97, 98, 
		98, 99, 99, 100, 100, 101, 101, 102, 102, 103, 
		103, 104, 104, 105, 105, 106, 106, 107, 107, 108, 
		108, 109, 109, 110, 110, 111, 111, 112, 112, 113, 
		113, 114, 114, 115, 115, 116, 116, 117, 117, 118, 
		118, 119, 119, 120, 120, 121, 121, 122, 122, 123, 
		123, 124, 124, 125, 125
		};
	
	/**
	 * 返回力量影響近距離傷害 XXX 7.6C add
	 * @param str BUFF加成+裝備加成+空身力量 總加成的力量
	 * @param baseStr 空身力量 用來計算純力獎勵額外的近距離傷害
	 * @return
	 */
	public static int calcStrDmg(final int str, final int baseStr) {
		// XXX 近距離傷害，自力量8=+2開始，每2點力量多1傷害值
		// XXX 純力25與35點時額外多1傷害值，純力45點時額外多3傷害值。(註：純力=升等獎勵點數的力量，不含裝備道具或魔法技能的力量。)
		// int dmg = 2 + (str - 8) / 2;
		int add = 0;
		
		if (baseStr >= 25) {
			add++;
		}
		
		if (baseStr >= 35) {
			add++;
		}
		
		if (baseStr >= 45) {
			add += 3;
		}
		
		return _strDmg[str] + add;
	}
	
	/**
	 * 力量命中追加值(力量值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _strHit = { 
		0, // str 0
		0, 1, 2, 2, 3, 4, 4, 5, 6, 6, // str 1~10
		7, 8, 8, 9, 10, 10, 11, 12, 12, 13, // str 11~20
		14, 14, 15, 16, 16, 17, 18, 18, 19, 20, // str 21~30
		20, 21, 22, 22, 23, 24, 24, 25, 26, 26, // str 31~40
		27, 28, 28, 29, 30, 30, 31, 32, 32, 33, // str 41~50
		34, 34, 35, 36, 36, 37, 38, 38, 39, 40, // str 51~60
		40, 41, 42, 42, 43, 44, 44, 45, 46, 46, // str 61~70
		47, 48, 48, 49, 50, 50, 51, 52, 52, 53, 
		54, 54, 55, 56, 56, 57, 58, 58, 59, 60, 
		60, 61, 62, 62, 63, 64, 64, 65, 66, 66, 
		67, 68, 68, 69, 70, 70, 71, 72, 72, 73, 
		74, 74, 75, 76, 76, 77, 78, 78, 79, 80, 
		80, 81, 82, 82, 83, 84, 84, 85, 86, 86, 
		87, 88, 88, 89, 90, 90, 91, 92, 92, 93, 
		94, 94, 95, 96, 96, 97, 98, 98, 99, 100, 
		100, 101, 102, 102, 103, 104, 104, 105, 106, 106, 
		107, 108, 108, 109, 110, 110, 111, 112, 112, 113, 
		114, 114, 115, 116, 116, 117, 118, 118, 119, 120, 
		120, 121, 122, 122, 123, 124, 124, 125, 126, 126, 
		127, 128, 128, 129, 130, 130, 131, 132, 132, 133, 
		134, 134, 135, 136, 136, 137, 138, 138, 139, 140, 
		140, 141, 142, 142, 143, 144, 144, 145, 146, 146, 
		147, 148, 148, 149, 150, 150, 151, 152, 152, 153, 
		154, 154, 155, 156, 156, 157, 158, 158, 159, 160, 
		160, 161, 162, 162, 163, 164, 164, 165, 166, 166, 
		167, 168, 168, 169, 170, };
	
	/**
	 * 返回力量影響近距離命中 XXX 7.6C add
	 * @param str BUFF加成+裝備加成+空身力量 總加成的力量
	 * @param baseStr 空身力量 用來計算純力獎勵額外的近距離命中
	 * @return
	 */
	public static int calcStrHit(final int str, final int baseStr) {
		// XXX 近距離命中，每3點力量多2命中，自力量=8開始，先連續增加2點命中，第3次增加力量時，不增加命中
		// XXX 純力25與35點時額外多1命中，純力45點時額外多3命中。
		
		// int hit = (int)((str / 3.0D) * 2.0D);
		int add = 0;
		
		if (baseStr >= 25) {
			add++;
		}
		
		if (baseStr >= 35) {
			add++;
		}
		
		if (baseStr >= 45) {
			add += 3;
		}
		
		return _strHit[str] + add;
	}
	
	/**
	 * 返回力量影響近距離最大傷害值發動率 XXX 7.6C add
	 * @param str BUFF加成+裝備加成+空身力量 總加成的力量
	 * @param baseStr 空身力量 用來計算純力獎勵額外的近距離最大傷害值發動率
	 * @return
	 */
	public static int calcStrDmgCritical(final int str, final int baseStr) {
		// XXX 力量40、50與60點時，都會增加近距離最大傷害值發動率1%。
		// XXX 純力45點時，近距離最大傷害值發動率再增加1%
		// XXX 近距離最大傷害發動時，會有特殊圖示(不同武器會有不同的特殊圖示)
		
		int critical = 0;
		
		if (str >= 40) {
			critical = 1 + (str - 40) / 10;
		}

		int add = 0;
		
		if (baseStr >= 45) {
			add++;
		}
		
		return critical + add;
	}
	
	/**
	 * 智力追加傷害值(智力值計算範圍0~255)  XXX 7.6C add
	 */
	private static final int[] _intDmg = { 
		0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 
		2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 
		4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 
		6, 6, 6, 6, 7, 7, 7, 7, 7, 8, 
		8, 8, 8, 8, 9, 9, 9, 9, 9, 10, 
		10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 
		12, 12, 12, 12, 13, 13, 13, 13, 13, 14, 
		14, 14, 14, 14, 15, 15, 15, 15, 15, 16, 
		16, 16, 16, 16, 17, 17, 17, 17, 17, 18, 
		18, 18, 18, 18, 19, 19, 19, 19, 19, 20, 
		20, 20, 20, 20, 21, 21, 21, 21, 21, 22, 
		22, 22, 22, 22, 23, 23, 23, 23, 23, 24, 
		24, 24, 24, 24, 25, 25, 25, 25, 25, 26, 
		26, 26, 26, 26, 27, 27, 27, 27, 27, 28, 
		28, 28, 28, 28, 29, 29, 29, 29, 29, 30, 
		30, 30, 30, 30, 31, 31, 31, 31, 31, 32, 
		32, 32, 32, 32, 33, 33, 33, 33, 33, 34, 
		34, 34, 34, 34, 35, 35, 35, 35, 35, 36, 
		36, 36, 36, 36, 37, 37, 37, 37, 37, 38, 
		38, 38, 38, 38, 39, 39, 39, 39, 39, 40, 
		40, 40, 40, 40, 41, 41, 41, 41, 41, 42, 
		42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 
		44, 44, 44, 44, 45, 45, 45, 45, 45, 46, 
		46, 46, 46, 46, 47, 47, 47, 47, 47, 48, 
		48, 48, 48, 48, 49, };
	
	/**
	 * 返回智力影響魔法傷害 XXX 7.6C add
	 * @param intel BUFF加成+裝備加成+空身智力 總加成的智力
	 * @param baseIntel 空身智力 用來計算純智獎勵額外的魔法傷害
	 * @return
	 */
	public static int calcIntMagicDmg(final int intel, final int baseIntel) {
		// XXX 魔法傷害從INT=15開始+1魔法傷害，之後每5個等級+1魔法傷害。
		// XXX 純智25、35與45，分別再額外+1、+1與+3魔法傷害。(註：純智=升等獎勵點數的智力，不含裝備道具或魔法技能的智力。)
		// int dmg = 0 + (str - 10) / 5;
		int add = 0;
		
		if (baseIntel >= 25) {
			add++;
		}
		
		if (baseIntel >= 35) {
			add++;
		}
		
		if (baseIntel >= 45) {
			add += 3;
		}
		
		return _intDmg[intel] + add;
	}
	
	/**
	 * 智力命中追加值(智力值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _intHit = { 
		-6, 
		-6, -6, -5, -5, -5, -4, -4, -4, -3, -3, 
		-3, -2, -2, -2, -1, -1, -1, 0, 0, 0, 
		0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 
		3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 
		7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 
		10, 10, 11, 11, 11, 12, 12, 12, 13, 13, 
		13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 
		17, 17, 17, 18, 18, 18, 19, 19, 19, 20, 
		20, 20, 21, 21, 21, 22, 22, 22, 23, 23, 
		23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 
		27, 27, 27, 28, 28, 28, 29, 29, 29, 30, 
		30, 30, 31, 31, 31, 32, 32, 32, 33, 33, 
		33, 34, 34, 34, 35, 35, 35, 36, 36, 36, 
		37, 37, 37, 38, 38, 38, 39, 39, 39, 40, 
		40, 40, 41, 41, 41, 42, 42, 42, 43, 43, 
		43, 44, 44, 44, 45, 45, 45, 46, 46, 46, 
		47, 47, 47, 48, 48, 48, 49, 49, 49, 50, 
		50, 50, 51, 51, 51, 52, 52, 52, 53, 53, 
		53, 54, 54, 54, 55, 55, 55, 56, 56, 56, 
		57, 57, 57, 58, 58, 58, 59, 59, 59, 60, 
		60, 60, 61, 61, 61, 62, 62, 62, 63, 63, 
		63, 64, 64, 64, 65, 65, 65, 66, 66, 66, 
		67, 67, 67, 68, 68, 68, 69, 69, 69, 70, 
		70, 70, 71, 71, 71, 72, 72, 72, 73, 73, 
		73, 74, 74, 74, 75, 75, 75, 76, 76, 76, 
		77, 77, 77, 78, 78, };
	
	/**
	 * 返回智力影響魔法命中 XXX 7.6C add
	 * @param intel BUFF加成+裝備加成+空身智力 總加成的智力
	 * @param baseIntel 空身智力 用來計算純智獎勵額外的魔法命中
	 * @return
	 */
	public static int calcIntMagicHit(final int intel, final int baseIntel) {
		// XXX INT=8時，魔法命中=-4。魔法命中的增加分兩個階段，魔法命中從INT=9開始=-3，每3點INT，增加1點魔法命中，直到INT=18為止魔法命中+0；再來就需要到INT=23開始=+1，仍是每3點INT，增加1點魔法命中
		// XXX 純智25、35與45，分別再額外+1、+1與+3魔法命中
		//int hit = -4 + (intel - 6) / 3;
		//if (intel >= 24) {
		//	hit = 1 + (intel - 23) / 3;
		//}
		
		int add = 0;
		
		if (baseIntel >= 25) {
			add++;
		}
		
		if (baseIntel >= 35) {
			add++;
		}
		
		if (baseIntel >= 45) {
			add += 3;
		}
		
		return _intHit[intel] + add;
	}
	
	/**
	 * 返回智力影響魔法暴擊率 XXX 7.6C add
	 * @param intel BUFF加成+裝備加成+空身智力 總加成的智力
	 * @param baseIntel 空身力量 用來計算純智獎勵額外的魔法暴擊率
	 * @return
	 */
	public static int calcIntMagicCritical(final int intel, final int baseIntel) {
		// XXX 魔法暴擊率從智力35開始，每5等+1魔法暴擊率
		// XXX 純智45，再額外+1魔法暴擊率
		
		int critical = 0;
		
		if (intel >= 35) {
			critical = 1 + (intel - 35) / 5;
		}
		
		int add = 0;
		
		if (baseIntel >= 45) {
			add++;
		}
		
		return critical + add;
	}
	
	/**
	 * 智力額外魔法點追加值(智力值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _intMB = { 
		0, 
		0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 
		2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 
		5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 
		7, 8, 8, 8, 8, 9, 9, 9, 9, 10, 
		10, 10, 10, 11, 11, 11, 11, 12, 12, 12, 
		12, 13, 13, 13, 13, 14, 14, 14, 14, 15, 
		15, 15, 15, 16, 16, 16, 16, 17, 17, 17, 
		17, 18, 18, 18, 18, 19, 19, 19, 19, 20, 
		20, 20, 20, 21, 21, 21, 21, 22, 22, 22, 
		22, 23, 23, 23, 23, 24, 24, 24, 24, 25, 
		25, 25, 25, 26, 26, 26, 26, 27, 27, 27, 
		27, 28, 28, 28, 28, 29, 29, 29, 29, 30, 
		30, 30, 30, 31, 31, 31, 31, 32, 32, 32, 
		32, 33, 33, 33, 33, 34, 34, 34, 34, 35, 
		35, 35, 35, 36, 36, 36, 36, 37, 37, 37, 
		37, 38, 38, 38, 38, 39, 39, 39, 39, 40, 
		40, 40, 40, 41, 41, 41, 41, 42, 42, 42, 
		42, 43, 43, 43, 43, 44, 44, 44, 44, 45, 
		45, 45, 45, 46, 46, 46, 46, 47, 47, 47, 
		47, 48, 48, 48, 48, 49, 49, 49, 49, 50, 
		50, 50, 50, 51, 51, 51, 51, 52, 52, 52, 
		52, 53, 53, 53, 53, 54, 54, 54, 54, 55, 
		55, 55, 55, 56, 56, 56, 56, 57, 57, 57, 
		57, 58, 58, 58, 58, 59, 59, 59, 59, 60, 
		60, 60, 60, 61, 61, 61, 61, 62, 62, 62, 
		62, 63, 63, 63, 63, };
	
	/**
	 * 職業影響額外魔法點加成 0:王族 1:騎士 2:妖精 3:法師 4:黑妖 5:龍騎 6:幻術 7:戰士 XXX 7.6C ADD
	 */
	private static final int[] _classMB = {0, 0, 0, 1, 0, 0, 1, 0 };
	
	/**
	 * 返回智力影響額外魔法點 XXX 7.6C add
	 * @param intel BUFF加成+裝備加成+空身智力 總加成的智力
	 * @return
	 */
	public static int calcIntMagicBonus(final int charType, final int intel) {
		// XXX 額外魔法點，自INT=8開始+2額外魔法點數，之後每4個等級+1額外魔法點數
		//int mb = 2 + (intel - 8) / 4;
		return _intMB[intel] + _classMB[charType];
	}
	
	/**
	 * 智力MP消耗減少%追加值(智力值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _intMagicConsumeReduction = { 
		0, 
		0, 1, 2, 2, 3, 4, 4, 5, 6, 6, 
		7, 8, 8, 9, 10, 10, 11, 12, 12, 13, 
		14, 14, 15, 16, 16, 17, 18, 18, 19, 20, 
		20, 21, 22, 22, 23, 24, 24, 25, 26, 26, 
		27, 28, 28, 29, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 
		30, 30, 30, 30, 30, };
	
	/**
	 * 返回智力影響MP消耗減少% XXX 7.6C add
	 * @param intel BUFF加成+裝備加成+空身智力 總加成的智力
	 * @return
	 */
	public static int calcIntMagicConsumeReduction(final int intel) {
		// XXX  MP消耗減少%，每多3點增加2%MP消耗減少，自INT=8的-5%開始，先連續增加2% MP消耗減少，第三次增加智力時，不增加MP消耗減少。最多到智力=45時，MP消耗減少30%
		return _intMagicConsumeReduction[intel];
	}
	
	/**
	 * 返回精神MP恢復增加量 XXX 7.6C add
	 * @param wis BUFF加成+裝備加成+空身精神 總加成的精神
	 * @param baseWis 空身精神 用來計算純精獎勵額外的MP恢復增加量
	 * @return
	 */
	public static int calcWisMpr(final int wis, final int baseWis) {
		// 純精25、35的額外加成，MP恢復分別+1，純精45的額外加成MP恢復+3
		
		// 純精25 35 45 額外MP恢復增加量計算
		int extra = 0;
		
		// 純精25時，增加量+1
		if (baseWis >= 25) {
			extra++;
		}
				
		// 純精35時，增加量+1
		if (baseWis >= 35) {
			extra++;
		}
		
		// 純精45時，增加量+3
		if (baseWis >= 45) {
			extra += 3;
		}
		
		final int mpr = Math.max(wis / 5, 1);
		
		return mpr + extra;
	}
	
	/**
	 * 精神0~255對應MP藥水回復 XXX 7.6C add
	 */
	private static final int[] _calWisPotionMpr = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,// wis:0~11
		2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, // wis:12~23
		8, 8, 9, 9, 10, 10, 11, 11, 12, 12, // wis:24~33
		13, 13, 14, 14, 15, 15, 16, 16, 17, 17, // wis:34~43
		18, 18, 19, 19, 20, 20, 21, 21, 22, 22, // wis:44~53
		23, 23, 24, 24, 25, 25, 26, 26, 27, 27, 
		28, 28, 29, 29, 30, 30, 31, 31, 32, 32, 
		33, 33, 34, 34, 35, 35, 36, 36, 37, 37, 
		38, 38, 39, 39, 40, 40, 41, 41, 42, 42, 
		43, 43, 44, 44, 45, 45, 46, 46, 47, 47, 
		48, 48, 49, 49, 50, 50, 51, 51, 52, 52, 
		53, 53, 54, 54, 55, 55, 56, 56, 57, 57, 
		58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 
		63, 63, 64, 64, 65, 65, 66, 66, 67, 67, 
		68, 68, 69, 69, 70, 70, 71, 71, 72, 72, 
		73, 73, 74, 74, 75, 75, 76, 76, 77, 77, 
		78, 78, 79, 79, 80, 80, 81, 81, 82, 82, 
		83, 83, 84, 84, 85, 85, 86, 86, 87, 87, 
		88, 88, 89, 89, 90, 90, 91, 91, 92, 92, 
		93, 93, 94, 94, 95, 95, 96, 96, 97, 97, 
		98, 98, 99, 99, 100, 100, 101, 101, 102, 102, 
		103, 103, 104, 104, 105, 105, 106, 106, 107, 107, 
		108, 108, 109, 109, 110, 110, 111, 111, 112, 112, 
		113, 113, 114, 114, 115, 115, 116, 116, 117, 117, 
		118, 118, 119, 119, 120, 120, 121, 121, 122, 122, 
		123, 123
		};
		
	/**
	 * 返回精神MP藥水恢復增加量 XXX 7.6C add
	 * @param wis BUFF加成+裝備加成+空身精神 總加成的精神
	 * @param baseWis 空身精神 用來計算純精獎勵額外的MP藥水恢復增加量
	 * @return
	 */
	public static int calcWisPotionMpr(final int wis, final int baseWis) {
		// 純精25、35的額外加成，MP藥水恢復分別+1，純精45的額外加成，MP藥水恢復+3
		
		// 純精25 35 45 額外藥水恢復增加量計算
		int extra = 0;
		
		// 純精25時，增加量+1
		if (baseWis >= 25) {
			extra++;
		}
				
		// 純精35時，增加量+1
		if (baseWis >= 35) {
			extra++;
		}
		
		// 純精45時，增加量+3
		if (baseWis >= 45) {
			extra += 3;
		}
		
		return _calWisPotionMpr[wis] + extra;
	}
	
	// XXX 7.6C add
	private static final int[][] _baseWisLevUpMinMpUpType = {
	//wis:0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45
		{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 9, 9, 9, 9, 9, 10},// 王族
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6},// 騎士
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 13, 13, 13, 13, 13, 14},// 妖精
		{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 16, 16, 16, 16, 16, 18, 18, 18, 18, 18, 20},// 法師
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 13, 13, 13, 13, 13, 14},// 黑妖	
		{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 7},// 龍騎
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 9, 9, 9, 9, 9, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 16},// 幻術
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6},// 戰士
	};
	
	// XXX 7.6C add
	private static final int[][] _baseWisLevUpMaxMpUpType = {
	//wis:0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45
		{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 15, 16},// 王族
		{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 10},// 騎士
		{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 10, 10, 10, 11, 11, 11, 13, 13, 13, 14, 14, 14, 16, 16, 16, 17, 17, 17, 19, 19, 19, 20, 20, 20, 21, 21, 21, 23},// 妖精
		{ 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 14, 14, 14, 16, 16, 16, 18, 18, 18, 20, 20, 20, 22, 22, 22, 24, 24, 24, 26, 26, 26, 28, 28, 28, 30, 30, 30, 32},// 法師
		{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 8, 8, 8, 10, 10, 10, 11, 11, 11, 13, 13, 13, 14, 14, 14, 16, 16, 16, 17, 17, 17, 19, 19, 19, 20, 20, 20, 22, 22, 22, 23},// 黑妖	
		{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10, 10, 10, 11},// 龍騎
		{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 9, 9, 9, 11, 11, 11, 12, 12, 12, 14, 14, 14, 16, 16, 16, 18, 18, 18, 19, 19, 19, 21, 21, 21, 23, 23, 23, 24, 24, 24, 26},// 幻術
		{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 4, 4, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 8, 8, 9, 9, 9, 10},// 戰士
	};
	
	/**
	 * 返回各職業空身精神影響升等時MP增加量 XXX 7.6C add
	 * @param charType
	 * @param baseWis
	 * @return 各職業升等時MP增加量範圍陣列
	 */
	public static int[] calcBaseWisLevUpMpUp(final int charType, final int baseWis) {
		final int[] range = new int[2];
		if (baseWis > 45) {
			range[0] = _baseWisLevUpMinMpUpType[charType][45];
			range[1] = _baseWisLevUpMaxMpUpType[charType][45];
		} else {
			range[0] = _baseWisLevUpMinMpUpType[charType][baseWis];
			range[1] = _baseWisLevUpMaxMpUpType[charType][baseWis];
		}
		return range;
	}
	
	// 各職業空身MP最大值  XXX 7.6C ADD
	private static final int[] _classMaxMp = {ConfigCharSetting.PRINCE_MAX_MP, ConfigCharSetting.KNIGHT_MAX_MP, ConfigCharSetting.ELF_MAX_MP, ConfigCharSetting.WIZARD_MAX_MP, 
		ConfigCharSetting.DARKELF_MAX_MP, ConfigCharSetting.DRAGONKNIGHT_MAX_MP, ConfigCharSetting.ILLUSIONIST_MAX_MP, ConfigCharSetting.WARRIOR_MAX_MP};
	
	/**
	 * 各職業等級提升時MP上昇值計算  XXX 7.6C ADD
	 * @param charType
	 * @param baseMaxMp
	 * @param baseWis
	 * @return MP上昇後數値
	 */
	public static int calcStatMp(final int charType, final int baseMaxMp, final byte baseWis) {
		int min = 0;
		int max = 0;
		if (baseWis > 45) {
			min = _baseWisLevUpMinMpUpType[charType][45];
			max = _baseWisLevUpMaxMpUpType[charType][45];
		} else {
			min = _baseWisLevUpMinMpUpType[charType][baseWis];
			max = _baseWisLevUpMaxMpUpType[charType][baseWis];
		}
		
		int randommp = min + RandomArrayList.getInt(max - min + 1);
		
		// 確保增加後的空身MP小於等於空身MP最大限制量
		if (baseMaxMp + randommp > _classMaxMp[charType]) {
			randommp = _classMaxMp[charType] - baseMaxMp;
		}
		
		return Math.max(randommp, 0);
	}
	
	/**
	 * <b> 傳回空身精神點數 wis 對應的抗魔值  XXX 7.6C change</b>
	 * 
	 * @param wis
	 *            精神點數
	 * 
	 * @return mrBonus 抗魔值
	 */
	public static int calcStatMr(final int wis) {	
		// XXX 7.6C 魔法防禦額外加值 (精神10開始，每增加1點精神，就增加4點MR)
		return wis > 10 ? wis - 10 << 2 : 0;
	}
	
	/**
	 * 返回空身精神MP上限增加量 XXX 7.6C add
	 * @param baseWis 空身精神 用來計算純精獎勵MP上限增加量
	 * @return
	 */
	public static int calcWisAddMaxMp(final int baseWis) {
		// 純精 25 35 45 額外MP上限增加量計算
		int extra = 0;
		
		// 純精25時，MP上限增加50
		if (baseWis >= 25) {
			extra += 50;
		}
		
		// 純精35時，MP上限增加100
		if (baseWis >= 35) {
			extra += 100;
		}
		
		// 純精45時，MP上限增加150
		if (baseWis >= 45) {
			extra += 150;
		}
		
		return extra;
	}
	
	/**
	 * 空身敏捷追加防禦力計算(空身AC的計算=敏捷對AC的影響+各職業的等級，對AC的增益) XXX 7.6C add
	 * @param classType
	 * @param level
	 * @param dex
	 * @return
	 */
	public static int calcAc(final int classType, final int level, final int dex) {
		return 10 + _dexAc[dex] - _classFeatures[classType].getAcLevel(level);
	}
	
	/**
	 * 敏捷AC追加值(敏捷值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _dexAc = { 
		0, 
		-1, -1, -1, -2, -2, -2, -2, -2, -3, -3, 
		-3, -4, -4, -4, -5, -5, -5, -6, -6, -6, 
		-7, -7, -7, -8, -8, -8, -9, -9, -9, -10, 
		-10, -10, -11, -11, -11, -12, -12, -12, -13, -13, 
		-13, -14, -14, -14, -15, -15, -15, -16, -16, -16, 
		-17, -17, -17, -18, -18, -18, -19, -19, -19, -20, 
		-20, -20, -21, -21, -21, -22, -22, -22, -23, -23, 
		-23, -24, -24, -24, -25, -25, -25, -26, -26, -26, 
		-27, -27, -27, -28, -28, -28, -29, -29, -29, -30, 
		-30, -30, -31, -31, -31, -32, -32, -32, -33, -33, 
		-33, -34, -34, -34, -35, -35, -35, -36, -36, -36, 
		-37, -37, -37, -38, -38, -38, -39, -39, -39, -40, 
		-40, -40, -41, -41, -41, -42, -42, -42, -43, -43, 
		-43, -44, -44, -44, -45, -45, -45, -46, -46, -46, 
		-47, -47, -47, -48, -48, -48, -49, -49, -49, -50, 
		-50, -50, -51, -51, -51, -52, -52, -52, -53, -53, 
		-53, -54, -54, -54, -55, -55, -55, -56, -56, -56, 
		-57, -57, -57, -58, -58, -58, -59, -59, -59, -60, 
		-60, -60, -61, -61, -61, -62, -62, -62, -63, -63, 
		-63, -64, -64, -64, -65, -65, -65, -66, -66, -66, 
		-67, -67, -67, -68, -68, -68, -69, -69, -69, -70, 
		-70, -70, -71, -71, -71, -72, -72, -72, -73, -73, 
		-73, -74, -74, -74, -75, -75, -75, -76, -76, -76, 
		-77, -77, -77, -78, -78, -78, -79, -79, -79, -80, 
		-80, -80, -81, -81, -81, -82, -82, -82, -83, -83, 
		-83, -84, -84, -84, -85, };
	
	/**
	 * 返回敏捷影響AC XXX 7.6C add
	 * @param dex
	 * @return
	 */
	public static int calcDexAc(final int dex) {
		//final int calc = -2 + (dex - 6) / 3 * -1;
		return _dexAc[dex];
	}
	
	/**
	 * 敏捷ER追加值(敏捷值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _dexEr = { 0, 
		0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 
		5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 
		10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 
		15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 
		20, 21, 21, 22, 22, 23, 23, 24, 24, 25, 
		25, 26, 26, 27, 27, 28, 28, 29, 29, 30, 
		30, 31, 31, 32, 32, 33, 33, 34, 34, 35, 
		35, 36, 36, 37, 37, 38, 38, 39, 39, 40, 
		40, 41, 41, 42, 42, 43, 43, 44, 44, 45, 
		45, 46, 46, 47, 47, 48, 48, 49, 49, 50, 
		50, 51, 51, 52, 52, 53, 53, 54, 54, 55, 
		55, 56, 56, 57, 57, 58, 58, 59, 59, 60, 
		60, 61, 61, 62, 62, 63, 63, 64, 64, 65, 
		65, 66, 66, 67, 67, 68, 68, 69, 69, 70, 
		70, 71, 71, 72, 72, 73, 73, 74, 74, 75, 
		75, 76, 76, 77, 77, 78, 78, 79, 79, 80, 
		80, 81, 81, 82, 82, 83, 83, 84, 84, 85, 
		85, 86, 86, 87, 87, 88, 88, 89, 89, 90, 
		90, 91, 91, 92, 92, 93, 93, 94, 94, 95, 
		95, 96, 96, 97, 97, 98, 98, 99, 99, 100, 
		100, 101, 101, 102, 102, 103, 103, 104, 104, 105, 
		105, 106, 106, 107, 107, 108, 108, 109, 109, 110, 
		110, 111, 111, 112, 112, 113, 113, 114, 114, 115, 
		115, 116, 116, 117, 117, 118, 118, 119, 119, 120, 
		120, 121, 121, 122, 122, 123, 123, 124, 124, 125, 
		125, 126, 126, 127, 127, };
	
	/**
	 * 返回敏捷影響ER XXX 7.6C add
	 * @param dex
	 * @return
	 */
	public static int calcDexEr(final int dex) {
		//final int calc = dex >> 1;
		return _dexEr[dex];
	}
	
	/**
	 * 敏捷追加傷害值(敏捷值計算範圍0~255)  XXX 7.6C add
	 */
	private static final int[] _dexDmg = { 
		0, 
		1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 
		3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 
		7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 
		10, 10, 11, 11, 11, 12, 12, 12, 13, 13, 
		13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 
		17, 17, 17, 18, 18, 18, 19, 19, 19, 20, 
		20, 20, 21, 21, 21, 22, 22, 22, 23, 23, 
		23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 
		27, 27, 27, 28, 28, 28, 29, 29, 29, 30, 
		30, 30, 31, 31, 31, 32, 32, 32, 33, 33, 
		33, 34, 34, 34, 35, 35, 35, 36, 36, 36, 
		37, 37, 37, 38, 38, 38, 39, 39, 39, 40, 
		40, 40, 41, 41, 41, 42, 42, 42, 43, 43, 
		43, 44, 44, 44, 45, 45, 45, 46, 46, 46, 
		47, 47, 47, 48, 48, 48, 49, 49, 49, 50, 
		50, 50, 51, 51, 51, 52, 52, 52, 53, 53, 
		53, 54, 54, 54, 55, 55, 55, 56, 56, 56, 
		57, 57, 57, 58, 58, 58, 59, 59, 59, 60, 
		60, 60, 61, 61, 61, 62, 62, 62, 63, 63, 
		63, 64, 64, 64, 65, 65, 65, 66, 66, 66, 
		67, 67, 67, 68, 68, 68, 69, 69, 69, 70, 
		70, 70, 71, 71, 71, 72, 72, 72, 73, 73, 
		73, 74, 74, 74, 75, 75, 75, 76, 76, 76, 
		77, 77, 77, 78, 78, 78, 79, 79, 79, 80, 
		80, 80, 81, 81, 81, 82, 82, 82, 83, 83, 
		83, 84, 84, 84, 85, };
	
	/**
	 * 返回敏捷影響遠距離傷害 XXX 7.6C add
	 * @param dex BUFF加成+裝備加成+空身敏捷 總加成的敏捷
	 * @param baseDex 空身敏捷 用來計算純敏獎勵額外的遠距離傷害
	 * @return
	 */
	public static int calcDexDmg(final int dex, final int baseDex) {
		// 在敏捷=7時，遠距離傷害+2，自敏捷=9開始，每3點敏捷，遠距離傷害+1
		// 純敏25與35時，遠距離傷害額外再+1，純敏45時，遠距離傷害再+3。(註：純敏=升等獎勵點數的敏捷，不含裝備道具或魔法技能的敏捷。)
		// int dmg = 2 + (dex - 6) / 3;
		int add = 0;
		
		if (baseDex >= 25) {
			add++;
		}
		
		if (baseDex >= 35) {
			add++;
		}
		
		if (baseDex >= 45) {
			add += 3;
		}
		
		return _dexDmg[dex] + add;
	}
	
	/**
	 * 敏捷命中追加值(敏捷值計算範圍0~255) XXX 7.6C add
	 */
	private static final int[] _dexHit = { 
		-10, 
		-9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
		11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 
		21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
		31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 
		41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
		51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 
		61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 
		71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
		81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 
		91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 
		101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
		111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
		121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 
		131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 
		141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 
		151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 
		161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 
		171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 
		181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 
		191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 
		201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 
		211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 
		221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 
		231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
		241, 242, 243, 244, 245, };
	
	/**
	 * 返回敏捷影響遠距離命中 XXX 7.6C add
	 * @param dex BUFF加成+裝備加成+空身敏捷 總加成的敏捷
	 * @param baseDex 空身敏捷 用來計算純敏獎勵額外的遠距離命中
	 * @return
	 */
	public static int calcDexHit(final int dex, final int baseDex) {
		// XXX 在敏捷=7時，遠距離命中=-3，之後每1點敏捷，就增加1點的遠距離命中
		// XXX 純敏25與35時，遠距離命中再+1，純敏45時，遠距離命中再+3
		
		// int hit = -3 + dex - 7;
		
		int add = 0;
		
		if (baseDex >= 25) {
			add++;
		}
		
		if (baseDex >= 35) {
			add++;
		}
		
		if (baseDex >= 45) {
			add += 3;
		}
		
		return _dexHit[dex] + add;
	}
	
	/**
	 * 返回敏捷影響遠距離最大傷害值發動率 XXX 7.6C add
	 * @param dex BUFF加成+裝備加成+空身敏捷 總加成的敏捷
	 * @param baseDex 空身敏捷 用來計算純敏獎勵額外的遠距離最大傷害值發動率
	 * @return
	 */
	public static int calcDexDmgCritical(final int dex, final int baseDex) {
		// XXX 敏捷40、50與60點時，都會增加遠距離最大傷害值發動率1%
		// XXX 純敏45點時，遠距離最大傷害值發動率再增加1%。遠距離最大傷害發動時，會有特殊圖示
		
		int critical = 0;
		
		if (dex >= 40) {
			critical = 1 + (dex - 40) / 10;
		}
		
		int add = 0;
		
		if (baseDex >= 45) {
			add++;
		}
		
		return critical + add;
	}
	
	/**
	 * 各職業升等時HP基礎增加量 XXX 7.6C change
	 */
	private static final int[] _calHpUpDefaultType = 
		{ 12, 21, 10, 7, 11, 15, 9, 21 };
	
	/**
	 * 各職業空身體質0~25升等時HP額外增加量 XXX 7.6C add
	 */
	private static final int[][] _baseCon25LevUpExtraHpUpType = {
	//con:0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},// 王族 體質初始11
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},// 騎士 體質初始16
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},// 妖精 體質初始12
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},// 法師 體質初始12
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13},// 黑妖 體質初始12
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},// 龍騎 體質初始14
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13}, // 幻術 體質初始12
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}, // 戰士 體質初始16
	};
		
	/**
	 * 返回各職業升等時HP基礎增加量 XXX 7.6C add
	 * @param charType
	 * @return 
	 */
	public static int calcBaseClassLevUpHpUp(final int charType) {
		return _calHpUpDefaultType[charType];
	}
	
	/**
	 * 返回各職業空身體質影響升等時HP增加量 XXX 7.6C add
	 * @param charType
	 * @param baseCon
	 * @return 各職業空身體質升等時HP額外增加量
	 */
	public static int calcBaseConLevUpExtraHpUp(final int charType, final int baseCon) {
		// 體質大於25:各職業體質25為止的增加量(體質13~25，每1點+1) + 25以上(體質26~45，則每2點+1)增加量
		// 體質小於等於25:各職業初始體質到25增加量
		return baseCon > 25 ? _baseCon25LevUpExtraHpUpType[charType][25] + (baseCon - 25 >> 1) : _baseCon25LevUpExtraHpUpType[charType][baseCon];
	}
	
	// 各職業空身血量最大值  XXX 7.6C ADD
	private static final int[] _classMaxHp = {ConfigCharSetting.PRINCE_MAX_HP, ConfigCharSetting.KNIGHT_MAX_HP, ConfigCharSetting.ELF_MAX_HP, ConfigCharSetting.WIZARD_MAX_HP, 
		ConfigCharSetting.DARKELF_MAX_HP, ConfigCharSetting.DRAGONKNIGHT_MAX_HP, ConfigCharSetting.ILLUSIONIST_MAX_HP, ConfigCharSetting.WARRIOR_MAX_HP};
	
	/**
	 * 各職業等級提升時HP上昇值計算  XXX 7.6C ADD
	 * @param charType
	 * @param baseMaxHp
	 * @param baseCon
	 * @return
	 */
	public static int calcStatHp(final int charType, final int baseMaxHp, final byte baseCon) {	
		// 增加量 = 各職業升等時HP基礎增加量 + 各職業空身體質影響升等時HP增加量 + 隨機數字0~1(小數點0.5)
		int randomhp = calcBaseClassLevUpHpUp(charType) + calcBaseConLevUpExtraHpUp(charType, baseCon) + RandomArrayList.getInt(2);

		// 確保增加後的空身HP小於等於空身HP最大限制量
		if (baseMaxHp + randomhp > _classMaxHp[charType]) {
			randomhp = _classMaxHp[charType] - baseMaxHp;
		}
			
		return Math.max(randomhp, 0);		
	}
	
	/**
	 * 返回體質HP恢復量 XXX 7.6C add
	 * @param con BUFF加成+裝備加成+空身體質 總加成的體質
	 * @param baseCon 空身體質 用來計算純體獎勵額外的恢復量
	 * @return
	 */
	public static int calcConHpr(final int con, final int baseCon) {
		// XXX HP恢復：體質11時，HP恢復為1~5；
		// XXX 體質12點之後，每2點體質增加1點HP最大恢復量，平均HP恢復提升0.5；
		// XXX 純體25、35時，HP恢復多+1、純體45時，HP恢復多+3
		// XXX (註：純體=升等獎勵點數的體質，不含裝備道具或魔法技能的體質。)
		
		// 純體 25 35 45 額外恢復量計算
		int extra = 0;
		
		// 純體25時，HP恢復多+1
		if (baseCon >= 25) {
			extra++;
		}
		
		// 純體35時，HP恢復多+1
		if (baseCon >= 35) {
			extra++;
		}
		
		// 純體45時，HP恢復多+3
		if (baseCon >= 45) {
			extra += 3;
		}
		
		//final int hpr = Math.max(con >> 1, 5);// 取回最大 確保最小恢復量為5
		final int hpr = Math.max(con >> 1, 1);// 取回最大 確保最小恢復量為1
		
		return hpr + extra;
	}
	
	/**
	 * 返回體質HP藥水恢復增加量 XXX 7.6C add
	 * @param con BUFF加成+裝備加成+空身體質 總加成的體質
	 * @param baseCon 空身體質 用來計算純體獎勵額外的HP藥水恢復增加量
	 * @return
	 */
	public static int calcConPotionHpr(final int con, final int baseCon) {
		// XXX HP藥水恢復增加：
		// XXX 體質20開始+1%，之後每10等+1%。此外，純體35、45，分別增加1%、2%HP藥水恢復增加。
		
		// 純體 35 45 額外藥水恢復增加量計算
		int extra = 0;

		// 純體35時，增加量+1
		if (baseCon >= 35) {
			extra++;
		}
		
		// 純體45時，增加量+2
		if (baseCon >= 45) {
			extra += 2;
		}
		
		int bonus = 0;
		
		// 體質20開始+1%，之後每10等+1%
		if (con >= 20) {
			bonus = 1 + (con - 20) / 10;
		    //bonus = (con - 10) / 10;
		}

		return bonus + extra;
	}
	
	/**
	 * 返回基礎負重能力 XXX 7.6C add
	 * @param str
	 * @param con
	 * @return
	 */
	public static int calcAbilityMaxWeight(final int str, final int con) {
		// XXX 基礎負重能力=1,000 + Rounddown [ (力量+體質)/2,0 ]x100
		// XXX 註：Rounddown= 無條件捨位，至整數
		double maxWeight = 1000.0D;
		
		final int status = str + con >> 1;
		
		if (status > 0) {
			maxWeight += status * 100;
		}
		
		return (int)maxWeight;
	}
	
	/**
	 * 返回空身體質HP上限增加量 XXX 7.6C add
	 * @param baseCon 空身體質 用來計算純體獎勵HP上限增加量
	 * @return
	 */
	public static int calcConAddMaxHp(final int baseCon) {
		// 純體 25 35 45 額外HP上限增加量計算
		int extra = 0;
		
		// 純體25時，HP上限增加50
		if (baseCon >= 25) {
			extra += 50;
		}
		
		// 純體35時，HP上限增加100
		if (baseCon >= 35) {
			extra += 100;
		}
		
		// 純體45時，HP上限增加150
		if (baseCon >= 45) {
			extra += 150;
		}
		
		return extra;
	}
	
	/**
	 * 職業物理攻擊補正
	 * 
	 * @param playerLevel
	 * @return
	 */
	public abstract int getAttackLevel(int playerLevel);

	/**
	 * 職業物理命中補正
	 * 
	 * @param playerLevel
	 * @return
	 */
	public abstract int getHitLevel(int playerLevel);
}