package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 底比斯武器魔法 _type1:機率對象抗魔影響值(10代表 對方抗魔除以10) _type2:傷害次數 _type3:傷害倍率 計算後數字不加入傷害
 * 
 * @author daien
 */
public class W_SK007 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK007.class);

	private static final Random _random = new Random();

	public W_SK007() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK007();
	}

	@Override
	public double start_weapon_skill(final L1PcInstance pc, final L1Character target,
			final L1ItemInstance weapon, final double srcdmg) {
		try {
			// (>> 1: 除) (<< 1: 乘)
			final int mr = target.getMr() - (pc.getOriginalMagicHit() << 1);
			final double probability = (random(weapon) + (pc.getTrueSp() * 2.5)) - (mr / _type1);
			int random = (int) (probability / 1.1);

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
				final DmgSet dmgSet = new DmgSet(pc, target, srcdmg);
				dmgSet.begin();
				return 0;
			}
			return 0;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	private class DmgSet implements Runnable {

		private int _timeCounter = 0;

		private double _srcdmg = 0D;

		private final L1PcInstance _pc;

		private final L1Character _cha;

		private DmgSet(final L1PcInstance pc, final L1Character cha, final double srcdmg) {
			_cha = cha;
			_pc = pc;
			_srcdmg = srcdmg;
		}

		@Override
		public void run() {
			try {
				while (_timeCounter < _type2) {
					if ((_cha == null) || _cha.isDead()) {
						break;
					}
					attack();
					_timeCounter++;
					Thread.sleep(1000);// 每秒
				}

			} catch (final Throwable e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		private void begin() {
			GeneralThreadPool.get().schedule(this, 100);
		}

		private void attack() {
			double damage = DmgAcMr.getDamage(_pc, _cha);// 計算傷害
			if (((_cha.getCurrentHp() - (int) damage) <= 0) && (_cha.getCurrentHp() != 1)) {
				damage = _cha.getCurrentHp() - 1;

			} else if (_cha.getCurrentHp() == 1) {
				damage = 0;
			}

			if (damage > 0) {
				// 傷害最終計算
				damage = calc_dmg(_pc, _cha, damage) + dmg2(_srcdmg) + dmg3(_pc);
				// 輸出動畫
				show(_pc, _cha);

				if (_type3 > 0) {
					damage *= (_type3 / 100D);
				}

				if (_cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) _cha;
					pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
					pc.receiveDamage(_pc, damage, false, true);

				} else if (_cha instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) _cha;
					npc.broadcastPacketX10(new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Damage));
					npc.receiveDamage(_pc, (int) damage);
				}
			}
		}
	}
}
