package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_PARALYZED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_CURSE_PARALYZING;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillIconPoison;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 詛咒型麻痹
 * 
 * @author dexc
 */
public class L1CurseParalysis extends L1Paralysis {

	private static final Log _log = LogFactory.getLog(L1CurseParalysis.class);

	private final L1Character _target;

	private final int _delay;

	private final int _time;

	private Thread _timer;

	private class ParalysisDelayTimer extends Thread {
		@Override
		public void run() {
			_target.setSkillEffect(STATUS_CURSE_PARALYZING, 0);

			try {
				Thread.sleep(_delay); // 麻痺するまでの猶予時間を待つ。
			} catch (final InterruptedException e) {
				_target.killSkillEffectTimer(STATUS_CURSE_PARALYZING);

				ModelError.isError(_log, e.getLocalizedMessage(), e);
				return;
			}

			if (_target instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) _target;
				if (!player.isDead()) {
					player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, true, (_time / 1000))); // 麻痺狀態
				}
			}
			_target.setParalyzed(true);
			_timer = new ParalysisTimer();
			GeneralThreadPool.get().execute(_timer); // 麻痺計時開始
			if (this.isInterrupted()) {// XXX
				_timer.interrupt();
			}
		}
	}

	private class ParalysisTimer extends Thread {
		@Override
		public void run() {
			_target.killSkillEffectTimer(STATUS_CURSE_PARALYZING);
			_target.setSkillEffect(STATUS_CURSE_PARALYZED, 0);

			try {
				Thread.sleep(_time);

			} catch (final InterruptedException e) {
				ModelError.isError(_log, e.getLocalizedMessage(), e);
			}

			_target.killSkillEffectTimer(STATUS_CURSE_PARALYZED);
			if (_target instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) _target;
				if (!player.isDead()) {
					player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false, 0)); // 麻痺狀態解除
				}
			}
			_target.setParalyzed(false);
			cure(); // 解呪処理
		}
	}

	/**
	 * 魔法效果:麻痺
	 * 
	 * @param cha 對象
	 * @param delay 延遲時間(毫秒)
	 * @param time 麻痺時間(毫秒)
	 */
	private L1CurseParalysis(final L1Character cha, final int delay, final int time, final int mode) {
		this._target = cha;
		this._delay = delay;
		this._time = time;

		this.curse(mode);
	}

	private void curse(final int mode) {
		if (this._target instanceof L1PcInstance) {
			final L1PcInstance player = (L1PcInstance) this._target;
			switch (mode) {
			case 1:
				// 212 \f1你的身體漸漸麻痺。
				player.sendPackets(new S_ServerMessage(212));
				break;

			case 2:
				// 291 \f1你的身體正在迅速痲痹。
				player.sendPackets(new S_ServerMessage(291));
				break;
			}
		}

		this._target.setPoisonEffect(2);

		this._timer = new ParalysisDelayTimer();
		GeneralThreadPool.get().execute(this._timer);
	}

	/**
	 * 魔法效果:麻痺
	 * 
	 * @param cha 對象
	 * @param delay 延遲時間(毫秒)
	 * @param time 麻痺時間(毫秒)
	 * @param mode 1:你的身體漸漸麻痺。 2:你的身體正在迅速痲痹。
	 * @return
	 */
	public static boolean curse(final L1Character cha, final int delay, final int time, final int mode) {
		if (!((cha instanceof L1PcInstance) || (cha instanceof L1MonsterInstance))) {
			return false;
		}
		if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING) || cha.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
			return false; // 既に麻痺している
		}

		cha.setParalaysis(new L1CurseParalysis(cha, delay, time, mode));
		return true;
	}

	@Override
	public int getEffectId() {
		return 2;
	}

	@Override
	public void cure() {
		this._target.setPoisonEffect(0);
		this._target.setParalaysis(null);

		if (_target instanceof L1PcInstance) {
			final L1PcInstance tgpc = (L1PcInstance) _target;
			S_SkillIconPoison packet = new S_SkillIconPoison(0, 0);// 解除毒圖標
			tgpc.sendPackets(packet);
		}

		if (this._timer != null) {// XXX
			this._timer.interrupt();
		}
	}
}
