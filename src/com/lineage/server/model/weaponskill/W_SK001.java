package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 武器毀滅性攻擊 使用這項技能武器將會毀滅並造成設定的傷害 _type1:對方HP殘留值等份量 _type2:對方HP殘留值等份 計算後數字加入傷害質
 * 範例: _type1 = 2 _type2 = 3 取回對方殘留HP的1/3 後 * 2次 也就是殘留HP 2/3的意思
 * 
 * @author daien
 */
public class W_SK001 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK001.class);

	private static final Random _random = new Random();

	public W_SK001() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK001();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			final int chance = _random.nextInt(1000);
			final int random = random(weapon);
			if (random >= chance) {
				final double dmg = dmg1() + ((target.getCurrentHp() * _type1) / _type2);
				// 158:\f1%0%s 消失。
				pc.sendPackets(new S_ServerMessage(158, weapon.getLogName()));
				pc.getInventory().removeItem(weapon, 1);

				int outdmg = (int) (dmg2(srcdmg) + dmg3(pc) + dmg);
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
