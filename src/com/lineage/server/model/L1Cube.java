package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.MOVE_STOP;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CUBE_BALANCE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CUBE_IGNITION_TO_ENEMY;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CUBE_QUAKE_TO_ENEMY;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CUBE_SHOCK_TO_ENEMY;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;
import static com.lineage.server.model.skill.L1SkillId.STATUS_MR_REDUCTION_BY_CUBE_SHOCK;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.thread.GeneralThreadPool;

public class L1Cube extends TimerTask {

	private static final Log _log = LogFactory.getLog(L1Cube.class);

	private ScheduledFuture<?> _future = null;
	private int _timeCounter = 0;
	private final L1Character _effect;
	private final L1Character _cha;
	private final int _skillId;

	public L1Cube(final L1Character effect, final L1Character cha, final int skillId) {
		_effect = effect;
		_cha = cha;
		_skillId = skillId;
	}

	@Override
	public void run() {
		try {
			if (_cha.isDead()) {
				stop();
				return;
			}
			if (!_cha.hasSkillEffect(_skillId)) {
				stop();
				return;
			}
			_timeCounter++;
			giveEffect();

		} catch (final Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void begin() {
		// 効果時間が8秒のため、4秒毎のスキルの場合処理時間を考慮すると実際には1回しか効果が現れない
		// よって開始時間を0.9秒後に設定しておく
		_future = GeneralThreadPool.get().scheduleAtFixedRate(this, 900, 1000);
	}

	public void stop() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	public void giveEffect() {
		switch (_skillId) {
		case STATUS_CUBE_IGNITION_TO_ENEMY:
			if ((_timeCounter % 4) != 0) {
				return;
			}
			if (_cha.hasSkillEffect(STATUS_FREEZE)) {
				return;
			}
			if (_cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
				return;
			}
			if (_cha.hasSkillEffect(ICE_LANCE)) {
				return;
			}
			if (_cha.hasSkillEffect(FREEZING_BREATH)) {
				return;
			}
			if (_cha.hasSkillEffect(EARTH_BIND)) {
				return;
			}

			if (_cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) _cha;
				pc.sendPacketsAll(new S_DoActionGFX(pc.getId(), ActionCodes.ACTION_Damage));
				pc.receiveDamage(_effect, 10, false, true);

			} else if (_cha instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) _cha;
				mob.broadcastPacketX10(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Damage));
				mob.receiveDamage(_effect, 10);
			}
			break;

		case STATUS_CUBE_QUAKE_TO_ENEMY:
			if ((_timeCounter % 4) != 0) {
				return;
			}
			if (_cha.hasSkillEffect(STATUS_FREEZE)) {
				return;
			}
			if (_cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
				return;
			}
			if (_cha.hasSkillEffect(ICE_LANCE)) {
				return;
			}
			if (_cha.hasSkillEffect(FREEZING_BREATH)) {
				return;
			}
			if (_cha.hasSkillEffect(EARTH_BIND)) {
				return;
			}

			if (_cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) _cha;
				pc.setSkillEffect(MOVE_STOP, 1000);
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

			} else if (_cha instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) _cha;
				mob.setSkillEffect(STATUS_FREEZE, 1000);
				mob.setParalyzed(true);
			}
			break;

		case STATUS_CUBE_SHOCK_TO_ENEMY:
			// if (_timeCounter % 5 != 0) {
			// return;
			// }
			// _cha.addMr(-10);
			// if (_cha instanceof L1PcInstance) {
			// L1PcInstance pc = (L1PcInstance) _cha;
			// pc.sendPackets(new S_SPMR(pc));
			// }
			_cha.setSkillEffect(STATUS_MR_REDUCTION_BY_CUBE_SHOCK, 4000);
			break;

		case STATUS_CUBE_BALANCE:
			if ((_timeCounter % 4) == 0) {
				int newMp = _cha.getCurrentMp() + 5;
				if (newMp < 0) {
					newMp = 0;
				}
				_cha.setCurrentMp(newMp);
			}
			if ((_timeCounter % 5) == 0) {
				if (_cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) _cha;
					pc.receiveDamage(_effect, 25, false, true);

				} else if (_cha instanceof L1MonsterInstance) {
					final L1MonsterInstance mob = (L1MonsterInstance) _cha;
					mob.receiveDamage(_effect, 25);
				}
			}
			break;
		}
	}
}
