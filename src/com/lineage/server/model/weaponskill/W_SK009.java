package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;

/**
 * 惡魔王系列武器傷害公式 【力量+敏捷】*2 +【浮動0-50】 發動機率：基礎機率3% 隨武器加成提高增加+8=5% 9=6% 10=8% +11=
 * 10% 之後每多1+1% 隨機施展「疾病術」機率1% +9 2% _type1:力量+敏捷隨機直倍率 _type2:無 _type3:傷害倍率
 * 計算後數字加入傷害
 * 
 * @author daien
 */
public class W_SK009 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK009.class);

	private static final Random _random = new Random();

	public W_SK009() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK009();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			final int ev = weapon.getEnchantLevel();
			if (ev < 0) {
				return 0;
			}
			int random = 0;
			int random2 = 10;

			switch (ev) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				random = _random1;
				break;
			case 8:
				random = _random1 + 20;
				break;
			case 9:
				random = _random1 + 30;
				random2 = 20;
				break;
			case 10:
				random = _random1 + 50;
				random2 = 20;
				break;
			case 11:
				random = _random1 + 70;
				random2 = 20;
				break;
			default:
				random = _random1 + (ev * 10);
				random2 = 20;
				break;
			}

			switch (_ac_mr) {
			case 1:// 1:防禦
				random -= (target.getAc() * -1) >> 3;// AC / 8
				break;
			case 2:// 2:抗魔
				random -= target.getMr() >> 6;// MR / 8
				break;
			}

			// 疾病術施展機率計算
			final int chance2 = _random.nextInt(1000);
			if (random2 >= chance2) {
				final L1SkillUse l1skilluse = new L1SkillUse();
				l1skilluse.handleCommands(pc, L1SkillId.DISEASE, target.getId(), target.getX(), target.getY(),
						0, L1SkillUse.TYPE_GMBUFF);
			}

			final int chance = _random.nextInt(1000);
			if (random >= chance) {
				// 【力量+敏捷】*2 +【浮動0-50】
				double damage = ((_random.nextInt(pc.getStr() + pc.getDex()) + 1) * _type1) + +dmg1();

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
