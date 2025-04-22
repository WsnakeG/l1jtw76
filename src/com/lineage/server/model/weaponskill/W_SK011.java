package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 特殊系列武器傷害公式-2【體質隨機數】*2+ 【敏捷+智慧】*2 +【浮動0-50】 發動機率：基礎機率3% 隨武器加成提高增加 +2=5% +4=7%
 * +5=8% +7=10% 之後每+2 多1% _type1:體質隨機倍率 _type2:敏捷+智慧隨機直倍率 _type3:傷害倍率 計算後數字加入傷害
 * 
 * @author erics4179
 */
public class W_SK011 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK011.class);

	private static final Random _random = new Random();

	public W_SK011() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK011();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			final int ev = weapon.getEnchantLevel() / 2;
			int random = _random1;
			if (ev > 0) {
				random += (ev * _random2);// 強化每2加1%
			}

			switch (_ac_mr) {
			case 1:// 1:防禦
				random -= (target.getAc() * -1) >> 3;// AC / 8
				break;
			case 2:// 2:抗魔
				random -= target.getMr() >> 6;// MR / 8
				break;
			}

			final int chance = _random.nextInt(1000);
			if (random >= chance) {
				// 【體質隨機數】*2+ 【敏捷+智慧】*2 +【浮動0-50】
				double damage = ((_random.nextInt(pc.getCon()) + 1) * _type1)
						+ ((_random.nextInt(pc.getDex() + pc.getInt()) + 1) * _type2) + +dmg1();

				if (_type3 > 0) {
					damage *= (_type3 / 100D);
				}
				// 輸出動畫
				show(pc, target);

				int outdmg = (int) (dmg2(srcdmg) + dmg3(pc) + damage);
				if ((target.getCurrentHp() - outdmg) < 0) {
					outdmg = 1;
				}

				return calc_dmg(pc, target, outdmg);
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
}