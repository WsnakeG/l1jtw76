package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.CKEW_LV50;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_1_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_2_7_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_0_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_1_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_N;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_2_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_3_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_4_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_5_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_6_S;
import static com.lineage.server.model.skill.L1SkillId.COOKING_3_7_S;
import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DARK_BLIND;
import static com.lineage.server.model.skill.L1SkillId.DECAY_POTION;
import static com.lineage.server.model.skill.L1SkillId.DESPERADO;
import static com.lineage.server.model.skill.L1SkillId.DISEASE;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.GUARD_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.HORROR_OF_DEATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.I_LV30;
import static com.lineage.server.model.skill.L1SkillId.MANA_DRAIN;
import static com.lineage.server.model.skill.L1SkillId.MASS_SLOW;
import static com.lineage.server.model.skill.L1SkillId.PATIENCE;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;
import static com.lineage.server.model.skill.L1SkillId.RESIST_FEAR;
import static com.lineage.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_BARLOG;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_YAHEE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.WEAKNESS;
import static com.lineage.server.model.skill.L1SkillId.WEAPON_BREAK;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.HashMap;

import com.lineage.server.datatables.lock.SpawnBossReading;

/**
 * 攻擊判定
 * 
 * @author dexc
 */
public class L1AttackList {

	// 料理命中降低或追加(_weaponType != 20) && (_weaponType != 62)近距離武器
	protected static final HashMap<Integer, Integer> SKU1 = new HashMap<Integer, Integer>();

	// 料理命中降低或追加(_weaponType == 20) && (_weaponType == 62)遠距離武器
	protected static final HashMap<Integer, Integer> SKU2 = new HashMap<Integer, Integer>();

	// 技能增加閃避
	// protected static final HashMap<Integer, Integer> SKU3 = new
	// HashMap<Integer, Integer>();

	// NPC需附加技能可攻擊
	protected static final HashMap<Integer, Integer> SKNPC = new HashMap<Integer, Integer>();

	// NPC指定外型不可攻擊
	protected static final HashMap<Integer, Integer> PLNPC = new HashMap<Integer, Integer>();

	// 料理追加傷害(_weaponType != 20) && (_weaponType != 62)近距離武器
	protected static final HashMap<Integer, Integer> SKD1 = new HashMap<Integer, Integer>();

	// 料理追加傷害(_weaponType == 20) && (_weaponType == 62)遠距離武器
	protected static final HashMap<Integer, Integer> SKD2 = new HashMap<Integer, Integer>();

	// 傷害降低
	public static final HashMap<Integer, Integer> SKD3 = new HashMap<Integer, Integer>();

	// 傷害為0
	public static final HashMap<Integer, Integer> SKM0 = new HashMap<Integer, Integer>();

	// 力量增加命中
	protected static final HashMap<Integer, Integer> STRH = new HashMap<Integer, Integer>();

	// 敏捷增加命中
	protected static final HashMap<Integer, Integer> DEXH = new HashMap<Integer, Integer>();

	// 力量增加傷害
	protected static final HashMap<Integer, Integer> STRD = new HashMap<Integer, Integer>();

	// 敏捷增加傷害
	protected static final HashMap<Integer, Integer> DEXD = new HashMap<Integer, Integer>();

	// TODO 技能專用

	// NPC抵抗技能(NPCID / 技能編號) 列表中該技能對該NPC施展失敗
	protected static final HashMap<Integer, Integer[]> DNNPC = new HashMap<Integer, Integer[]>();

	// 安全區域不可使用的技能
	protected static final HashMap<Integer, Boolean> NZONE = new HashMap<Integer, Boolean>();

	// MR傷害降低
	public static final HashMap<Integer, Double[]> MRDMG = new HashMap<Integer, Double[]>();

	public static void load() {
		// NPC抵抗技能
		for (final Integer bossid : SpawnBossReading.get().bossIds()) {
			final Integer[] ids = new Integer[] { new Integer(FOG_OF_SLEEPING), // 沉睡之霧
					new Integer(ICE_LANCE), // 冰矛圍籬
					new Integer(CURSE_PARALYZE), // 木乃伊
					new Integer(EARTH_BIND), // 大地屏障
					new Integer(FREEZING_BREATH), // 寒冰噴吐
					new Integer(DARK_BLIND), // 闇盲咒術
					new Integer(DARKNESS), // 黑闇之影
					new Integer(CURSE_BLIND), // 闇盲咒術
					new Integer(SILENCE),// 魔法封印
			};
			if (DNNPC.get(bossid) == null) {
				DNNPC.put(bossid, ids);
			}
		}

		// MR傷害降低
		for (int mr = 0; mr < 255; mr++) {
			double mrFloor = 0;
			double mrCoefficient = 0;
			if (mr == 0) {
				mrFloor = 1.0;
				mrCoefficient = 1.0;

			} else if ((mr > 0) && (mr <= 50)) {
				mrFloor = 2.0;
				mrCoefficient = 1.0;

			} else if ((mr > 50) && (mr <= 100)) {
				mrFloor = 3.0;
				mrCoefficient = 0.9;

			} else if ((mr > 100) && (mr <= 120)) {
				mrFloor = 4.0;
				mrCoefficient = 0.9;

			} else if ((mr > 120) && (mr <= 140)) {
				mrFloor = 5.0;
				mrCoefficient = 0.8;

			} else if ((mr > 140) && (mr <= 160)) {
				mrFloor = 6.0;
				mrCoefficient = 0.8;

			} else if ((mr > 160) && (mr <= 180)) {
				mrFloor = 7.0;
				mrCoefficient = 0.7;

			} else if ((mr > 180) && (mr <= 200)) {
				mrFloor = 8.0;
				mrCoefficient = 0.7;

			} else if ((mr > 200) && (mr <= 220)) {
				mrFloor = 9.0;
				mrCoefficient = 0.6;

			} else if ((mr > 220) && (mr <= 240)) {
				mrFloor = 10.0;
				mrCoefficient = 0.6;

			} else if (mr > 240) {
				mrFloor = 11.0;
				mrCoefficient = 0.5;
			}
			MRDMG.put(new Integer(mr), new Double[] { mrFloor, mrCoefficient });
		}

		// 安全區域不可使用的技能
		NZONE.put(new Integer(WEAPON_BREAK), false);// 壞物術
		NZONE.put(new Integer(SLOW), false);// 緩速術
		NZONE.put(new Integer(CURSE_PARALYZE), false);// 木乃伊的詛咒
		NZONE.put(new Integer(MANA_DRAIN), false);// 魔力奪取
		NZONE.put(new Integer(DARKNESS), false);// 黑闇之影
		NZONE.put(new Integer(WEAKNESS), false);// 弱化術
		NZONE.put(new Integer(DISEASE), false);// 疾病術
		NZONE.put(new Integer(CANCELLATION), false);// 魔法相消術
		NZONE.put(new Integer(DECAY_POTION), false);// 藥水霜化術
		NZONE.put(new Integer(MASS_SLOW), false);// 集體緩速術
		NZONE.put(new Integer(ENTANGLE), false);// 地面障礙
		NZONE.put(new Integer(ERASE_MAGIC), false);// 魔法消除
		NZONE.put(new Integer(EARTH_BIND), false);// 大地屏障
		NZONE.put(new Integer(AREA_OF_SILENCE), false);// 封印禁地
		NZONE.put(new Integer(WIND_SHACKLE), false);// 風之枷鎖
		NZONE.put(new Integer(STRIKER_GALE), false);// 精準射擊
		NZONE.put(new Integer(SHOCK_STUN), false);// 衝擊之暈
		NZONE.put(new Integer(FOG_OF_SLEEPING), false);// 沉睡之霧
		NZONE.put(new Integer(ICE_LANCE), false);// 冰矛圍籬
		NZONE.put(new Integer(FREEZING_BREATH), false);// 寒冰噴吐
		NZONE.put(new Integer(POLLUTE_WATER), false);// 污濁之水
		NZONE.put(new Integer(ELEMENTAL_FALL_DOWN), false);// 弱化屬性
		NZONE.put(new Integer(RETURN_TO_NATURE), false);// 釋放元素
		NZONE.put(new Integer(SILENCE), false);// 魔法封印
		NZONE.put(new Integer(HORROR_OF_DEATH), false);// 驚悚死神
		NZONE.put(new Integer(RESIST_FEAR), false);// 恐懼無助
		NZONE.put(new Integer(GUARD_BRAKE), false);// 護衛毀滅
		NZONE.put(new Integer(DARK_BLIND), false);// 黑暗盲咒
		NZONE.put(new Integer(POWERGRIP), false);// 拘束移動
		NZONE.put(new Integer(DESPERADO), false);// 亡命之徒

		// 料理追加傷害(_weaponType != 20) && (_weaponType != 62)近距離武器
		SKD1.put(new Integer(COOKING_2_0_N), new Integer(+1));
		SKD1.put(new Integer(COOKING_2_0_S), new Integer(+1));
		SKD1.put(new Integer(COOKING_2_0_N), new Integer(+1));
		SKD1.put(new Integer(COOKING_2_0_N), new Integer(+1));

		// 命中追加(_weaponType == 20) && (_weaponType == 62)遠距離武器
		SKD2.put(new Integer(COOKING_2_3_N), new Integer(+1));
		SKD2.put(new Integer(COOKING_2_3_S), new Integer(+1));
		SKD2.put(new Integer(COOKING_3_0_N), new Integer(+1));
		SKD2.put(new Integer(COOKING_3_0_S), new Integer(+1));

		// 傷害降低
		SKD3.put(new Integer(COOKING_1_0_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_1_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_2_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_3_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_4_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_5_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_6_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_0_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_1_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_2_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_3_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_4_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_5_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_6_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_0_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_1_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_2_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_3_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_4_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_5_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_6_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_1_7_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_2_7_S), new Integer(-5));
		SKD3.put(new Integer(COOKING_3_7_S), new Integer(-5));
		SKD3.put(new Integer(DRAGON_SKIN), new Integer(-10));// 2011-11-26 -2
		SKD3.put(new Integer(PATIENCE), new Integer(-15));// 原始 -2
		SKD3.put(new Integer(IMMUNE_TO_HARM), new Integer(IMMUNE_TO_HARM));

		// 受到下列法術效果 傷害為0
		SKM0.put(new Integer(STATUS_FREEZE), new Integer(0));
		SKM0.put(new Integer(ABSOLUTE_BARRIER), new Integer(0));
		SKM0.put(new Integer(ICE_LANCE), new Integer(0));
		SKM0.put(new Integer(FREEZING_BREATH), new Integer(0));
		SKM0.put(new Integer(EARTH_BIND), new Integer(0));

		// 追加命中(_weaponType != 20) && (_weaponType != 62)近距離武器
		SKU1.put(new Integer(COOKING_2_0_N), new Integer(+1));
		SKU1.put(new Integer(COOKING_2_0_S), new Integer(+1));
		SKU1.put(new Integer(COOKING_3_2_N), new Integer(+2));
		SKU1.put(new Integer(COOKING_3_2_S), new Integer(+2));

		// 命中追加(_weaponType == 20) && (_weaponType == 62)遠距離武器
		SKU2.put(new Integer(COOKING_2_3_N), new Integer(+1));
		SKU2.put(new Integer(COOKING_2_3_S), new Integer(+1));
		SKU2.put(new Integer(COOKING_3_0_N), new Integer(+1));
		SKU2.put(new Integer(COOKING_3_0_S), new Integer(+1));

		// 技能增加閃避
		/*
		 * SKU3.put(new Integer(UNCANNY_DODGE), new Integer(-10));//
		 * 2012-1-29(-8) SKU3.put(new Integer(MIRROR_IMAGE), new Integer(-5));//
		 * 原始 -5 SKU3.put(new Integer(AQUA_PROTECTER), new Integer(-5));// 原始 -5
		 * SKU3.put(new Integer(RESIST_FEAR), new Integer(+25));// TEMP
		 * 2010/05/19(5) SKU3.put(new Integer(DRAGON2), new Integer(-1));// 地
		 * 攻擊迴避提升 石化耐性+3 SKU3.put(new Integer(DRAGON5), new Integer(-5));//
		 * 生命-物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 額外攻擊點數+2 防護中毒狀態 SKU3.put(new
		 * Integer(DRAGON6), new Integer(-5));// 誕生-物理攻擊迴避率+10% 魔法傷害減免+50 暗黑耐性+3
		 * SKU3.put(new Integer(DRAGON7), new Integer(-5));// 形象-物理攻擊迴避率+10%
		 * 魔法傷害減免+50 魔法暴擊率+1 支撐耐性+3
		 */

		// NPC需附加技能可攻擊
		SKNPC.put(new Integer(45912), new Integer(STATUS_HOLY_WATER));
		SKNPC.put(new Integer(45913), new Integer(STATUS_HOLY_WATER));
		SKNPC.put(new Integer(45914), new Integer(STATUS_HOLY_WATER));
		SKNPC.put(new Integer(45915), new Integer(STATUS_HOLY_WATER));
		SKNPC.put(new Integer(45916), new Integer(STATUS_HOLY_MITHRIL_POWDER));
		SKNPC.put(new Integer(45941), new Integer(STATUS_HOLY_WATER_OF_EVA));
		SKNPC.put(new Integer(45752), new Integer(STATUS_CURSE_BARLOG));
		SKNPC.put(new Integer(45753), new Integer(STATUS_CURSE_BARLOG));
		SKNPC.put(new Integer(45675), new Integer(STATUS_CURSE_YAHEE));
		SKNPC.put(new Integer(81082), new Integer(STATUS_CURSE_YAHEE));
		SKNPC.put(new Integer(45625), new Integer(STATUS_CURSE_YAHEE));
		SKNPC.put(new Integer(45674), new Integer(STATUS_CURSE_YAHEE));
		SKNPC.put(new Integer(45685), new Integer(STATUS_CURSE_YAHEE));
		SKNPC.put(new Integer(87000), new Integer(CKEW_LV50));
		SKNPC.put(new Integer(45020), new Integer(I_LV30));

		// NPC指定外型不可攻擊
		PLNPC.put(new Integer(46069), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46070), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46071), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46072), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46073), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46074), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46075), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46076), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46077), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46078), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46079), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46080), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46081), new Integer(6035));// 被拋棄的魔族
		PLNPC.put(new Integer(46082), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46083), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46084), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46085), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46086), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46087), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46088), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46089), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46090), new Integer(6035));// 巡察兵
		PLNPC.put(new Integer(46091), new Integer(6035));// 步哨兵
		PLNPC.put(new Integer(46092), new Integer(6034));// 被拋棄的魔族
		PLNPC.put(new Integer(46093), new Integer(6034));// 被拋棄的魔族
		PLNPC.put(new Integer(46094), new Integer(6034));// 被拋棄的魔族
		PLNPC.put(new Integer(46095), new Integer(6034));// 被拋棄的魔族
		PLNPC.put(new Integer(46096), new Integer(6034));// 被拋棄的魔族
		PLNPC.put(new Integer(46097), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46098), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46099), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46100), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46100), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46101), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46102), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46103), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46104), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46105), new Integer(6034));// 巡察兵
		PLNPC.put(new Integer(46106), new Integer(6034));// 步哨兵

		int strH = 0;
		STRH.put(new Integer(++strH), new Integer(-2));// 1
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-2));
		STRH.put(new Integer(++strH), new Integer(-1));
		STRH.put(new Integer(++strH), new Integer(-1));
		STRH.put(new Integer(++strH), new Integer(0));
		STRH.put(new Integer(++strH), new Integer(0));
		STRH.put(new Integer(++strH), new Integer(1));
		STRH.put(new Integer(++strH), new Integer(1));
		STRH.put(new Integer(++strH), new Integer(2));
		STRH.put(new Integer(++strH), new Integer(2));
		STRH.put(new Integer(++strH), new Integer(3));
		STRH.put(new Integer(++strH), new Integer(3));
		STRH.put(new Integer(++strH), new Integer(4));
		STRH.put(new Integer(++strH), new Integer(4));
		STRH.put(new Integer(++strH), new Integer(5));
		STRH.put(new Integer(++strH), new Integer(5));
		STRH.put(new Integer(++strH), new Integer(5));
		STRH.put(new Integer(++strH), new Integer(6));
		STRH.put(new Integer(++strH), new Integer(6));
		STRH.put(new Integer(++strH), new Integer(6));
		STRH.put(new Integer(++strH), new Integer(7));
		STRH.put(new Integer(++strH), new Integer(7));
		STRH.put(new Integer(++strH), new Integer(7));
		STRH.put(new Integer(++strH), new Integer(8));
		STRH.put(new Integer(++strH), new Integer(8));
		STRH.put(new Integer(++strH), new Integer(8));
		STRH.put(new Integer(++strH), new Integer(9));
		STRH.put(new Integer(++strH), new Integer(9));
		STRH.put(new Integer(++strH), new Integer(9));
		STRH.put(new Integer(++strH), new Integer(10));
		STRH.put(new Integer(++strH), new Integer(10));
		STRH.put(new Integer(++strH), new Integer(10));
		STRH.put(new Integer(++strH), new Integer(11));
		STRH.put(new Integer(++strH), new Integer(11));
		STRH.put(new Integer(++strH), new Integer(11));
		STRH.put(new Integer(++strH), new Integer(12));
		STRH.put(new Integer(++strH), new Integer(12));
		STRH.put(new Integer(++strH), new Integer(12));
		STRH.put(new Integer(++strH), new Integer(13));
		STRH.put(new Integer(++strH), new Integer(13));
		STRH.put(new Integer(++strH), new Integer(13));
		STRH.put(new Integer(++strH), new Integer(14));
		STRH.put(new Integer(++strH), new Integer(14));
		STRH.put(new Integer(++strH), new Integer(14));
		STRH.put(new Integer(++strH), new Integer(15));
		STRH.put(new Integer(++strH), new Integer(15));
		STRH.put(new Integer(++strH), new Integer(15));
		STRH.put(new Integer(++strH), new Integer(16));
		STRH.put(new Integer(++strH), new Integer(16));
		STRH.put(new Integer(++strH), new Integer(16));
		STRH.put(new Integer(++strH), new Integer(17));
		STRH.put(new Integer(++strH), new Integer(17));
		STRH.put(new Integer(++strH), new Integer(17));
		STRH.put(new Integer(++strH), new Integer(18));// 60

		int dexH = 0;
		DEXH.put(new Integer(++dexH), new Integer(-2));// 1
		DEXH.put(new Integer(++dexH), new Integer(-2));
		DEXH.put(new Integer(++dexH), new Integer(-2));
		DEXH.put(new Integer(++dexH), new Integer(-2));
		DEXH.put(new Integer(++dexH), new Integer(-2));
		DEXH.put(new Integer(++dexH), new Integer(-2));
		DEXH.put(new Integer(++dexH), new Integer(-1));
		DEXH.put(new Integer(++dexH), new Integer(-1));
		DEXH.put(new Integer(++dexH), new Integer(0));
		DEXH.put(new Integer(++dexH), new Integer(0));
		DEXH.put(new Integer(++dexH), new Integer(1));
		DEXH.put(new Integer(++dexH), new Integer(1));
		DEXH.put(new Integer(++dexH), new Integer(2));
		DEXH.put(new Integer(++dexH), new Integer(2));
		DEXH.put(new Integer(++dexH), new Integer(3));
		DEXH.put(new Integer(++dexH), new Integer(3));
		DEXH.put(new Integer(++dexH), new Integer(4));
		DEXH.put(new Integer(++dexH), new Integer(4));
		DEXH.put(new Integer(++dexH), new Integer(5));
		DEXH.put(new Integer(++dexH), new Integer(6));
		DEXH.put(new Integer(++dexH), new Integer(7));
		DEXH.put(new Integer(++dexH), new Integer(8));
		DEXH.put(new Integer(++dexH), new Integer(9));
		DEXH.put(new Integer(++dexH), new Integer(10));
		DEXH.put(new Integer(++dexH), new Integer(11));
		DEXH.put(new Integer(++dexH), new Integer(12));
		DEXH.put(new Integer(++dexH), new Integer(13));
		DEXH.put(new Integer(++dexH), new Integer(14));
		DEXH.put(new Integer(++dexH), new Integer(15));
		DEXH.put(new Integer(++dexH), new Integer(16));
		DEXH.put(new Integer(++dexH), new Integer(17));
		DEXH.put(new Integer(++dexH), new Integer(18));
		DEXH.put(new Integer(++dexH), new Integer(19));
		DEXH.put(new Integer(++dexH), new Integer(19));
		DEXH.put(new Integer(++dexH), new Integer(19));
		DEXH.put(new Integer(++dexH), new Integer(20));
		DEXH.put(new Integer(++dexH), new Integer(20));
		DEXH.put(new Integer(++dexH), new Integer(20));
		DEXH.put(new Integer(++dexH), new Integer(21));
		DEXH.put(new Integer(++dexH), new Integer(21));
		DEXH.put(new Integer(++dexH), new Integer(21));
		DEXH.put(new Integer(++dexH), new Integer(22));
		DEXH.put(new Integer(++dexH), new Integer(22));
		DEXH.put(new Integer(++dexH), new Integer(22));
		DEXH.put(new Integer(++dexH), new Integer(23));
		DEXH.put(new Integer(++dexH), new Integer(23));
		DEXH.put(new Integer(++dexH), new Integer(23));
		DEXH.put(new Integer(++dexH), new Integer(24));
		DEXH.put(new Integer(++dexH), new Integer(24));
		DEXH.put(new Integer(++dexH), new Integer(24));
		DEXH.put(new Integer(++dexH), new Integer(25));
		DEXH.put(new Integer(++dexH), new Integer(25));
		DEXH.put(new Integer(++dexH), new Integer(25));
		DEXH.put(new Integer(++dexH), new Integer(26));
		DEXH.put(new Integer(++dexH), new Integer(26));
		DEXH.put(new Integer(++dexH), new Integer(26));
		DEXH.put(new Integer(++dexH), new Integer(27));
		DEXH.put(new Integer(++dexH), new Integer(27));
		DEXH.put(new Integer(++dexH), new Integer(27));
		DEXH.put(new Integer(++dexH), new Integer(28));// 60

		// 力量傷害補正
		int dmgStr = -6;
		for (int str = 0; str <= 22; str++) { // 0~22 每2+1
			if ((str % 2) == 1) {
				dmgStr++;
			}
			STRD.put(new Integer(str), new Integer(dmgStr));
		}
		for (int str = 23; str <= 28; str++) { // 23~28 每3+1
			if ((str % 3) == 2) {
				dmgStr++;
			}
			STRD.put(new Integer(str), new Integer(dmgStr));
		}
		for (int str = 29; str <= 32; str++) { // 29~32 每2+1
			if ((str % 2) == 1) {
				dmgStr++;
			}
			STRD.put(new Integer(str), new Integer(dmgStr));
		}
		for (int str = 33; str <= 34; str++) { // 33~34 每1+1
			dmgStr++;
			STRD.put(new Integer(str), new Integer(dmgStr));
		}
		for (int str = 35; str <= 254; str++) { // 35~254 每4+1
			if ((str % 4) == 1) {
				dmgStr++;
			}
			STRD.put(new Integer(str), new Integer(dmgStr));
		}

		// 印出結果
		/*
		 * Map<Integer, Integer> testStr = new TreeMap<Integer, Integer>();
		 * testStr.putAll(strDmg); for (Integer integer : testStr.keySet()) {
		 * System.out.println("strDmg:"+integer + "/" + testStr.get(integer)); }
		 */

		// 敏捷傷害補正
		for (int dex = 0; dex <= 14; dex++) {
			// 0~14 = 0
			DEXD.put(new Integer(dex), new Integer(0));
		}

		DEXD.put(new Integer(15), new Integer(1));
		DEXD.put(new Integer(16), new Integer(2));
		DEXD.put(new Integer(17), new Integer(3));
		DEXD.put(new Integer(18), new Integer(4));
		DEXD.put(new Integer(19), new Integer(4));
		DEXD.put(new Integer(20), new Integer(4));
		DEXD.put(new Integer(21), new Integer(5));
		DEXD.put(new Integer(22), new Integer(5));
		DEXD.put(new Integer(23), new Integer(5));

		int dmgDex = 5;
		for (int dex = 24; dex <= 35; dex++) { // 24~35 毎3+1
			if ((dex % 3) == 1) {
				dmgDex++;
			}
			DEXD.put(new Integer(dex), new Integer(dmgDex));
		}
		for (int dex = 36; dex <= 127; dex++) { // 36~127 毎4+1
			if ((dex % 4) == 1) {
				dmgDex++;
			}
			DEXD.put(new Integer(dex), new Integer(dmgDex));
		}

		// 印出結果
		/*
		 * Map<Integer, Integer> testDex = new TreeMap<Integer, Integer>();
		 * testDex.putAll(dexDmg); for (Integer integer : testDex.keySet()) {
		 * System.out.println("dexDmg:"+integer + "/" + testDex.get(integer)); }
		 */

	}
}
