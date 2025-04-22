package com.lineage.server.model.poison;

import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_PARALYZED;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_PARALYZING;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.ModelError;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_SkillIconPoison;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 麻痺型中毒
 * 
 * @author dexc
 */
public class L1ParalysisPoison extends L1Poison {

	private static final Log _log = LogFactory.getLog(L1ParalysisPoison.class);

	// 麻痺毒の性能一覧 猶予 持続 (参考値、未適用)
	// グール 20 45
	// アステ 10 60
	// 蟻穴ムカデ 14 30
	// D-グール 39 45

	private final L1Character _target;

	private Thread _timer;

	private final int _delay;

	private final int _time;

	private int _effectId = 1;

	private class ParalysisPoisonTimer extends Thread {
		@Override
		public void run() {
			_target.setSkillEffect(STATUS_POISON_PARALYZING, 0);

			try {
				Thread.sleep(_delay); // 麻痺するまでの猶予時間を待つ。

			} catch (final InterruptedException e) {
				ModelError.isError(_log, e.getLocalizedMessage(), e);
				return;
			}

			// 綠色改灰色
			_effectId = 2;
			_target.setPoisonEffect(2);

			if (_target instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) _target;
				if (player.isDead() == false) {
					player.sendPackets(new S_Paralysis(1, true, _time / 1000)); // 麻痺状態にする
					_timer = new ParalysisTimer();
					GeneralThreadPool.get().execute(_timer); // 麻痺計時開始
					if (isInterrupted()) {// XXX
						_timer.interrupt();
					}
				}
			}
		}
	}

	private class ParalysisTimer extends Thread {
		@Override
		public void run() {
			_target.killSkillEffectTimer(STATUS_POISON_PARALYZING);
			_target.setSkillEffect(STATUS_POISON_PARALYZED, 0);
			try {
				Thread.sleep(_time);

			} catch (final InterruptedException e) {
				ModelError.isError(_log, e.getLocalizedMessage(), e);
			}

			_target.killSkillEffectTimer(STATUS_POISON_PARALYZED);
			if (_target instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) _target;
				if (!player.isDead()) {
					player.sendPackets(new S_Paralysis(1, false, 0)); // 麻痺状態を解除する
					cure(); // 解毒処理
				}
			}
		}
	}

	private L1ParalysisPoison(final L1Character cha, final int delay, final int time) {
		_target = cha;
		_delay = delay;
		_time = time;

		this.doInfection();
	}

	public static boolean doInfection(final L1Character cha, final int delay, final int time) {
		if (!L1Poison.isValidTarget(cha)) {
			return false;
		}

		cha.setPoison(new L1ParalysisPoison(cha, delay, time));
		return true;
	}

	private void doInfection() {
		sendMessageIfPlayer(_target, 212);
		_target.setPoisonEffect(1);

		if (_target instanceof L1PcInstance) {
			_timer = new ParalysisPoisonTimer();
			GeneralThreadPool.get().execute(_timer);

			final L1PcInstance pc = (L1PcInstance) _target;
			// 要被麻痺了前置時間圖標
			S_SkillIconPoison type = new S_SkillIconPoison(2, 3);
			pc.sendPackets(type);
		}
	}

	@Override
	public int getEffectId() {
		return _effectId;
	}

	@Override
	public void cure() {
		_target.setPoisonEffect(0);
		_target.setPoison(null);
		if (_target instanceof L1PcInstance) {
			final L1PcInstance tgpc = (L1PcInstance) _target;
			S_SkillIconPoison packet = new S_SkillIconPoison(0, 0);// 解除毒圖標
			tgpc.sendPackets(packet);
		}
		if (_timer != null) {// XXX
			_timer.interrupt(); // 麻痺毒タイマー解除
		}
	}
}
