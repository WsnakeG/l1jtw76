package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 武器攻擊附加MP奪取 使用這項技能武器將會在攻擊時隨設定機率附加MP奪取(以原始輸出傷害計算) _type1:最小吸收質 _type2:最大吸收質
 * _type3:設置 0:無 1:受到武器強化質影響 計算後數字不加入傷害 範例: _type1 = 3 _type2 = 9 _type2 = 1
 * 最小奪取(武器強化質 + 3)隨機 最大奪取9 範例: _type1 = 1 _type2 = 1 _type2 = 0 最小奪取1 最大奪取1
 * 
 * @author daien
 */
public class W_SK004 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK004.class);

	private static final Random _random = new Random();

	public W_SK004() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK004();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			if (target.getCurrentMp() <= 0) {
				return 0;
			}
			final int chance = _random.nextInt(1000);
			int random = random(weapon);
			switch (_ac_mr) {
			case 1:// 1:防禦
				random -= (target.getAc() * -1) << 2;// AC * 4
				break;
			case 2:// 2:抗魔
				random -= target.getMr() << 2;// MR * 4
				break;
			}

			if (random >= chance) {
				int mpadd = 0;
				if (_type1 > 1) {
					mpadd += _random.nextInt(_type1) + 1;
				} else {
					mpadd += _type1;
				}

				if (_type3 == 1) {
					if (weapon.getEnchantLevel() > 0) {
						mpadd += _random.nextInt(weapon.getEnchantLevel()) + 1;
					}
				}
				mpadd = Math.max(mpadd, _type1);// 最小吸收_type1
				mpadd = Math.min(mpadd, _type2);// 最大吸收_type2

				int tg_new_mp = target.getCurrentMp() - mpadd;
				tg_new_mp = Math.max(tg_new_mp, 0);
				target.setCurrentMp(tg_new_mp);

				pc.setCurrentMp(pc.getCurrentMp() + mpadd);
				return 0;
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
}
