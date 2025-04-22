package com.lineage.server.model.weaponskill;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 武器攻擊附加HP奪取(附加傷害) 使用這項技能武器將會在攻擊時隨設定機率附加HP奪取(以原始輸出傷害計算) _type1:最小吸收質
 * _type2:傷害次數 _type3:傷害倍率 計算後數字不加入傷害 範例: _type1=1 _type2=3 _type2=120
 * addsrcdmg=5 最小吸收1 吸收質傷害5% 傷害3次 原計算質增加*1.2
 * 
 * @author daien
 */
public class W_SK006 extends L1WeaponSkillType {

	private static final Log _log = LogFactory.getLog(W_SK006.class);

	private static final Random _random = new Random();

	public W_SK006() {
	}

	public static L1WeaponSkillType get() {
		return new W_SK006();
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
				int hpadd = (int) dmg2(srcdmg);
				hpadd = Math.max(hpadd, _type1);// 最小吸收1

				final short newHp = (short) (pc.getCurrentHp() + hpadd);
				pc.setCurrentHp(newHp);

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
				// 輸出動畫
				show(_pc, _cha);
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

				if (_type3 > 0) {
					damage *= (_type3 / 100D);
				}

				if (_cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) _cha;
					// pc.sendPacketsAll(new S_DoActionGFX(pc.getId(),
					// ActionCodes.ACTION_Damage));
					pc.receiveDamage(_pc, damage, false, true);

				} else if (_cha instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) _cha;
					// npc.broadcastPacketX10(new S_DoActionGFX(npc.getId(),
					// ActionCodes.ACTION_Damage));
					npc.receiveDamage(_pc, (int) damage);
				}
			}
		}
	}
}
