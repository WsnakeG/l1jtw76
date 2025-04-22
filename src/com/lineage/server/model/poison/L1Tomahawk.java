package com.lineage.server.model.poison;

import static com.lineage.server.model.skill.L1SkillId.TOMAHAWK;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.thread.GeneralThreadPool;

public class L1Tomahawk {

	private Thread _thread;

	private final L1Character _attacker;

	private final L1Character _target;

	private final int _damageSpan;

	private final int _damage;

	private L1Tomahawk(final L1Character attacker, final L1Character cha, final int damageSpan,
			final int damage) {
		_attacker = attacker;
		_target = cha;
		_damageSpan = damageSpan;
		_damage = damage;

		doInfection();
	}

	private class TomahawkTimer extends Thread {

		@Override
		public void run() {
			L1PcInstance player = null;
			L1MonsterInstance mob = null;
			while (true) {
				try {
					Thread.sleep(_damageSpan);
				} catch (final InterruptedException e) {
					break;
				}

				if (!_target.hasSkillEffect(TOMAHAWK)) {
					break;
				}
				if (_target instanceof L1PcInstance) {
					player = (L1PcInstance) _target;
					player.sendPacketsAll(new S_DoActionGFX(player.getId(), ActionCodes.ACTION_Damage));
					player.receiveDamage(_attacker, _damage, false, false);
					if (player.isDead()) {
						break;
					}
				} else if (_target instanceof L1MonsterInstance) {
					mob = (L1MonsterInstance) _target;
					mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
					mob.receiveDamage(_attacker, _damage);
					if (mob.isDead()) {
						return;
					}
				}
			}
			cure();
		}
	}

	boolean isDamageTarget(final L1Character cha) {
		return (cha instanceof L1PcInstance) || (cha instanceof L1MonsterInstance);
	}

	private void doInfection() {
		if (isDamageTarget(_target)) {
			_target.setSkillEffect(TOMAHAWK, 6000);
			_thread = new TomahawkTimer();
			GeneralThreadPool.get().execute(_thread);
		}
	}

	public static boolean doInfection(final L1Character attacker, final L1Character cha, final int damageSpan,
			final int damage) {
		if (!isValidTarget(cha)) {
			return false;
		}

		new L1Tomahawk(attacker, cha, damageSpan, damage);
		return true;
	}

	private static boolean isValidTarget(final L1Character cha) {
		if (cha == null) {
			return false;
		}

		if (cha.hasSkillEffect(TOMAHAWK)) {
			return false;
		}

		return true;
	}

	public void cure() {
		if (_thread != null) {
			_thread.interrupt();
		}

		_target.removeSkillEffect(TOMAHAWK);
	}
}