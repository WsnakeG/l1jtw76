package com.lineage.server.model.weaponskill;

import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1AttackList;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @author daien
 */
public class DmgAcMr {

	private static final Log _log = LogFactory.getLog(DmgAcMr.class);

	private static final Random _random = new Random();

	/**
	 * 防禦力傷害減低
	 * 
	 * @return
	 */
	public static int calcDefense(final L1Character target) {
		try {
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;
				final int ac = Math.max(0, 10 - targetPc.getAc());
				final int acDefMax = targetPc.getClassFeature().getAcDefenseMax(ac);
				if (acDefMax != 0) {
					// (>> 1: 除) (<< 1: 乘)
					final int srcacd = Math.max(1, (acDefMax >> 3));// /8
					final int acdown = _random.nextInt(acDefMax) + srcacd;
					return acdown;
				}

			} else if (target instanceof L1MonsterInstance) {
				final L1MonsterInstance targetNpc = (L1MonsterInstance) target;
				final int damagereduction = targetNpc.getNpcTemplate().get_damagereduction();// 額外傷害減低
				final int srcac = targetNpc.getAc();
				final int ac = Math.max(0, 10 - srcac);

				final int acDefMax = ac / 7;// 防禦力傷害減免降低1/7
				if (acDefMax != 0) {
					final int srcacd = Math.max(1, acDefMax);
					return _random.nextInt(acDefMax) + srcacd + damagereduction;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * ＭＲ魔法傷害減輕
	 * 
	 * @param dmg
	 * @return
	 */
	public static double calcMrDefense(final L1PcInstance pc, final L1Character target, double dmg) {
		// 取回目標抗魔
		final int mr = getTargetMr(target);

		double mrFloor = 0;
		double mrCoefficient = 0;
		final Double[] mrF = L1AttackList.MRDMG.get(new Integer(mr));
		if (mrF != null) {
			mrFloor = mrF[0].doubleValue();
			mrCoefficient = mrF[1].doubleValue();

		} else {
			mrFloor = 11;
			mrCoefficient = 0.5;
		}

		// 計算減低的傷害
		dmg *= (mrCoefficient - (0.01 * Math.floor((mr - pc.getOriginalMagicHit()) / mrFloor)));
		return dmg;
	}

	/**
	 * 目標魔防
	 * 
	 * @return
	 */
	private static int getTargetMr(final L1Character target) {
		int mr = 0;
		if (target instanceof L1PcInstance) {
			final L1PcInstance targetPc = (L1PcInstance) target;
			mr = targetPc.getMr();
			switch (targetPc.guardianEncounter()) {
			case 0:// 正義的守護 Lv.1
				mr += 3;
				break;

			case 1:// 正義的守護 Lv.2
				mr += 6;
				break;

			case 2:// 正義的守護 Lv.3
				mr += 9;
				break;
			}

		} else if (target instanceof L1MonsterInstance) {
			final L1MonsterInstance targetNpc = (L1MonsterInstance) target;
			mr = targetNpc.getMr();
		}
		return mr;
	}

	/**
	 * 底比斯武器傷害公式
	 * 
	 * @param pc
	 * @param cha
	 * @return
	 */
	public static double getDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int spByItem = pc.getSp() - pc.getTrueSp();
		final int intel = pc.getInt();
		final int charaIntelligence = (pc.getInt() + spByItem) - 12;

		double coefficientA = 1 + ((3.0 / 32.0) * charaIntelligence);
		if (coefficientA < 1) {
			coefficientA = 1;
		}

		double coefficientB = 0;
		if (intel > 25) {
			coefficientB = 25 * 0.065;

		} else if (intel <= 12) {
			coefficientB = 12 * 0.065;

		} else {
			coefficientB = intel * 0.065;
		}
		double coefficientC = 0;
		if (intel > 25) {
			coefficientC = 25;

		} else if (intel <= 12) {
			coefficientC = 12;

		} else {
			coefficientC = intel;
		}
		double bsk = 0;
		if (pc.hasSkillEffect(BERSERKERS)) {
			bsk = 0.1;
		}
		dmg = (((_random.nextInt(6) + 1 + 7) * (1 + bsk) * coefficientA * coefficientB) / 10.5) * coefficientC
				* 2.0;
		return dmg;
	}
}