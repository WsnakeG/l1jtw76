package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 武器攻擊附加HP奪取 使用這項技能武器將會在攻擊時隨設定機率附加HP奪取(以原始輸出傷害計算) _type1:傷害值等份量 _type2:傷害值等份
 * 計算後數字不加入傷害 範例: _type1 = 1 _type2 = 8 每次奪取原始輸出傷害的1/8
 * 
 * @author daien
 */
public class W_SK003 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK003.class);

	private static final Random _random = new Random();

	public W_SK003() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK003();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			final int chance = _random.nextInt(1000);
			final int random = random(weapon);
			if (random >= chance) {
				final int hpadd = Math.max((int) ((srcdmg * _type1) / _type2), 1);
				final short newHp = (short) (pc.getCurrentHp() + hpadd);
				pc.setCurrentHp(newHp);
				return 0;
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
}
