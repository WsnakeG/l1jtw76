package com.lineage.server.model.Instance;

import java.util.HashMap;
import java.util.Map;

/**
 * 物品能力值
 * 
 * @author dexc
 */
public class L1ItemPower {

	private final L1ItemInstance _itemInstance;

	// 抗魔 = 追加值
	public static final Map<Integer, Integer> MR2 = new HashMap<Integer, Integer>();

	/**
	 * 載入強化值影響抗魔的裝備
	 */
	public static void load() {
		// MR * 1
		MR2.put(20011, new Integer(1));// 抗魔法頭盔
		MR2.put(120011, new Integer(1));// 抗魔法頭盔
		MR2.put(20110, new Integer(1));// 抗魔法鏈甲
		MR2.put(21108, new Integer(1));// 魔法抵抗內衣

		// MR * 2
		MR2.put(20056, new Integer(2));// 抗魔法斗篷
		MR2.put(120056, new Integer(2));// 抗魔法斗篷

		MR2.put(70092, new Integer(3));// 馬昆斯斗篷
		MR2.put(70034, new Integer(1));// 塔拉斯長靴

		// 林德拜爾
		MR2.put(30328, new Integer(1));// 林德拜爾的力量
		MR2.put(30329, new Integer(1));// 林德拜爾的魅惑
		MR2.put(30330, new Integer(1));// 林德拜爾的泉源
		MR2.put(30331, new Integer(1));// 林德拜爾的霸氣

		MR2.put(70019, new Integer(1));// 酷金盔甲

		MR2.put(70181, new Integer(1));// 究極抗魔法T恤
		MR2.put(70190, new Integer(1));// 特製究極抗魔法T恤
	}

	protected L1ItemPower(final L1ItemInstance itemInstance) {
		_itemInstance = itemInstance;
	}

	/**
	 * 抗魔裝備設置
	 * 
	 * @param armor
	 * @return
	 */
	protected int getMr() {
		int mr = _itemInstance.getItem().get_mdef();
		// 強化值影響的增減抗魔值 by terry0412
		final int influence_mr = _itemInstance.getItem().getInfluenceMr();
		if (influence_mr != 0) {
			mr += (_itemInstance.getEnchantLevel() * influence_mr);

		} else {
			final Integer integer = MR2.get(_itemInstance.getItemId());
			if (integer != null) {
				mr += (_itemInstance.getEnchantLevel() * integer);
			}
		}
		return mr;
	}

	protected int getSp() {
		int sp = _itemInstance.getItem().get_addsp();
		final int influence_sp = _itemInstance.getItem().getInfluenceSp();
		if (influence_sp != 0) {
			sp += (_itemInstance.getEnchantLevel() * influence_sp);
		}
		return sp;
	}

	protected int getHp() {
		int hp = _itemInstance.getItem().get_addhp();
		final int influence_hp = _itemInstance.getItem().getInfluenceHp();
		if (influence_hp != 0) {
			hp += (_itemInstance.getEnchantLevel() * influence_hp);
		}
		return hp;
	}

	protected int getMp() {
		int mp = _itemInstance.getItem().get_addmp();
		final int influence_mp = _itemInstance.getItem().getInfluenceMp();
		if (influence_mp != 0) {
			mp += (_itemInstance.getEnchantLevel() * influence_mp);
		}
		return mp;
	}

	/**
	 * 強化飾品設置
	 * 
	 * @param armor 飾品
	 * @param equipment true穿著 false脫除
	 */
	protected void greater(final L1PcInstance owner, final boolean equipment) {
		final int level = _itemInstance.getEnchantLevel();
		if (level <= 0) {
			return;
		}
		if (equipment) {
			switch (_itemInstance.getItem().get_greater()) {
			case 0:// 高等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addEarth(1);
					owner.addWind(1);
					owner.addWater(1);
					owner.addFire(1);
					break;
				case 2:
					owner.addEarth(2);
					owner.addWind(2);
					owner.addWater(2);
					owner.addFire(2);
					break;
				case 3:
					owner.addEarth(3);
					owner.addWind(3);
					owner.addWater(3);
					owner.addFire(3);
					break;
				case 4:
					owner.addEarth(4);
					owner.addWind(4);
					owner.addWater(4);
					owner.addFire(4);
					break;
				case 5:
					owner.addEarth(5);
					owner.addWind(5);
					owner.addWater(5);
					owner.addFire(5);
					break;
				case 6:
					owner.addEarth(6);
					owner.addWind(6);
					owner.addWater(6);
					owner.addFire(6);
					owner.addHpr(1);
					owner.addMpr(1);
					break;
				case 7:
					owner.addEarth(10);
					owner.addWind(10);
					owner.addWater(10);
					owner.addFire(10);
					owner.addHpr(3);
					owner.addMpr(3);
					break;
				default:
					owner.addEarth(15);
					owner.addWind(15);
					owner.addWater(15);
					owner.addFire(15);
					owner.addHpr(3);
					owner.addMpr(3);
					break;
				}
				break;

			case 1:// 中等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addMaxHp(5);
					break;
				case 2:
					owner.addMaxHp(10);
					break;
				case 3:
					owner.addMaxHp(15);
					break;
				case 4:
					owner.addMaxHp(20);
					break;
				case 5:
					owner.addMaxHp(25);
					break;
				case 6:
					owner.addMaxHp(30);
					owner.addMr(2);
					break;
				case 7:
					owner.addMaxHp(40);
					owner.addMr(7);
					break;
				default:
					owner.addMaxHp((40 + level));
					owner.addMr((12 + level));
					break;
				}
				break;

			case 2:// 初等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addMaxMp(3);
					break;
				case 2:
					owner.addMaxMp(6);
					break;
				case 3:
					owner.addMaxMp(9);
					break;
				case 4:
					owner.addMaxMp(12);
					break;
				case 5:
					owner.addMaxMp(15);
					break;
				case 6:
					owner.addMaxMp(25);
					owner.addSp(1);
					break;
				case 7:
					owner.addMaxMp(40);
					owner.addSp(2);
					break;
				default:
					owner.addMaxMp((40 + level));
					owner.addSp(3);
					break;
				}
				break;
			}

		} else {
			switch (_itemInstance.getItem().get_greater()) {
			case 0:// 高等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addEarth(-1);
					owner.addWind(-1);
					owner.addWater(-1);
					owner.addFire(-1);
					break;
				case 2:
					owner.addEarth(-2);
					owner.addWind(-2);
					owner.addWater(-2);
					owner.addFire(-2);
					break;
				case 3:
					owner.addEarth(-3);
					owner.addWind(-3);
					owner.addWater(-3);
					owner.addFire(-3);
					break;
				case 4:
					owner.addEarth(-4);
					owner.addWind(-4);
					owner.addWater(-4);
					owner.addFire(-4);
					break;
				case 5:
					owner.addEarth(-5);
					owner.addWind(-5);
					owner.addWater(-5);
					owner.addFire(-5);
					break;
				case 6:
					owner.addEarth(-6);
					owner.addWind(-6);
					owner.addWater(-6);
					owner.addFire(-6);
					owner.addHpr(-1);
					owner.addMpr(-1);
					break;
				case 7:
					owner.addEarth(-10);
					owner.addWind(-10);
					owner.addWater(-10);
					owner.addFire(-10);
					owner.addHpr(-3);
					owner.addMpr(-3);
					break;
				default:
					owner.addEarth(-15);
					owner.addWind(-15);
					owner.addWater(-15);
					owner.addFire(-15);
					owner.addHpr(-3);
					owner.addMpr(-3);
					break;
				}
				break;

			case 1:// 中等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addMaxHp(-5);
					break;
				case 2:
					owner.addMaxHp(-10);
					break;
				case 3:
					owner.addMaxHp(-15);
					break;
				case 4:
					owner.addMaxHp(-20);
					break;
				case 5:
					owner.addMaxHp(-25);
					break;
				case 6:
					owner.addMaxHp(-30);
					owner.addMr(-2);
					break;
				case 7:
					owner.addMaxHp(-40);
					owner.addMr(-7);
					break;
				default:
					owner.addMaxHp(-(40 + level));
					owner.addMr(-(12 + level));
					break;
				}
				break;

			case 2:// 初等
				switch (level) {
				case 0:
					break;
				case 1:
					owner.addMaxMp(-3);
					break;
				case 2:
					owner.addMaxMp(-6);
					break;
				case 3:
					owner.addMaxMp(-9);
					break;
				case 4:
					owner.addMaxMp(-12);
					break;
				case 5:
					owner.addMaxMp(-15);
					break;
				case 6:
					owner.addMaxMp(-25);
					owner.addSp(-1);
					break;
				case 7:
					owner.addMaxMp(-40);
					owner.addSp(-2);
					break;
				default:
					owner.addMaxMp(-(40 + level));
					owner.addSp(-3);
					break;
				}
				break;
			}
		}
	}
}
