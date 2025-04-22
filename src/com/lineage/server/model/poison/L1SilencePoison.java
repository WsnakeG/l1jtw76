package com.lineage.server.model.poison;

import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillIconPoison;

/**
 * 沈黙型中毒
 * 
 * @author dexc
 */
public class L1SilencePoison extends L1Poison {

	private final L1Character _target;

	public static boolean doInfection(final L1Character cha) {
		if (!L1Poison.isValidTarget(cha)) {
			return false;
		}

		cha.setPoison(new L1SilencePoison(cha));
		return true;
	}

	private L1SilencePoison(final L1Character cha) {
		_target = cha;

		this.doInfection();
	}

	private void doInfection() {
		_target.setPoisonEffect(1);
		sendMessageIfPlayer(_target, 310);

		_target.setSkillEffect(STATUS_POISON_SILENCE, 0);
	}

	@Override
	public int getEffectId() {
		return 1;
	}

	@Override
	public void cure() {
		_target.setPoisonEffect(0);
		sendMessageIfPlayer(_target, 311);
		if (_target instanceof L1PcInstance) {
			final L1PcInstance tgpc = (L1PcInstance) _target;
			S_SkillIconPoison packet = new S_SkillIconPoison(0, 0);// 解除毒圖標
			tgpc.sendPackets(packet);
		}
		_target.killSkillEffectTimer(STATUS_POISON_SILENCE);
		_target.setPoison(null);
	}
}
