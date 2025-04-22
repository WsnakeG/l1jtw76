package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.poison.L1DamagePoison;

/**
 * 武器附加毒性攻擊 使用這項技能武器將會在攻擊時隨設定機率產生毒性 _type1:毒性時間(秒) _type2:毒性損耗HP值 計算後數字不加入傷害 範例:
 * _type1 = 3 _type2 = 5 中毒者每3秒減少HP5
 * 
 * @author daien
 */
public class W_SK002 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK002.class);

	private static final Random _random = new Random();

	public W_SK002() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK002();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			// 已經中毒
			if (target.getPoison() != null) {
				return 0;
			}
			final int chance = _random.nextInt(1000);
			final int random = random(weapon);
			if (random >= chance) {
				L1DamagePoison.doInfection(pc, target, (_type1 * 1000), _type2);
				return 0;
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}
}
