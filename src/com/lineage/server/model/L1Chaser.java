package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;

import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 底比斯武器魔法的效果
 * 
 * @author dexc
 */
public class L1Chaser extends TimerTask {

	private static final Log _log = LogFactory.getLog(L1Chaser.class);

	private static final Random _random = new Random();
	private ScheduledFuture<?> _future = null;
	private int _timeCounter = 0;
	private final L1PcInstance _pc;
	private final L1Character _cha;

	public L1Chaser(final L1PcInstance pc, final L1Character cha) {
		_cha = cha;
		_pc = pc;
	}

	@Override
	public void run() {
		try {
			if ((_cha == null) || _cha.isDead()) {
				stop();
				return;
			}
			attack();
			_timeCounter++;
			if (_timeCounter >= 3) {
				stop();
				return;
			}

		} catch (final Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void begin() {
		// 効果時間が8秒のため、4秒毎のスキルの場合処理時間を考慮すると実際には1回しか効果が現れない
		// よって開始時間を0.9秒後に設定しておく
		_future = GeneralThreadPool.get().scheduleAtFixedRate(this, 0, 1000);
	}

	public void stop() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	private void attack() {
		double damage = getDamage(_pc, _cha);
		if (((_cha.getCurrentHp() - (int) damage) <= 0) && (_cha.getCurrentHp() != 1)) {
			damage = _cha.getCurrentHp() - 1;

		} else if (_cha.getCurrentHp() == 1) {
			damage = 0;
		}

		_pc.sendPacketsAll(new S_EffectLocation(_cha.getX(), _cha.getY(), 7025));

		if (_cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) _cha;
			pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
			pc.receiveDamage(_pc, damage, false, false);

		} else if (_cha instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) _cha;
			npc.broadcastPacketX10(new S_DoActionGFX(npc.getId(), ActionCodes.ACTION_Damage));
			npc.receiveDamage(_pc, (int) damage);
		}
	}

	private double getDamage(final L1PcInstance pc, final L1Character cha) {
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

		return (L1WeaponSkill.calcDamageReduction(pc, cha, dmg, 0) * 0.66);
	}

}
