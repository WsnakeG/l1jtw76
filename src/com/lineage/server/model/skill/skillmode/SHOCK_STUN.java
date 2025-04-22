package com.lineage.server.model.skill.skillmode;

import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;

import java.util.Random;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1GuardInstance;
import com.lineage.server.model.Instance.L1GuardianInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 衝擊之暈 07/14更改隨機時間2~5秒、最大衝暈時間6秒
 * 
 * @author dexc
 */
public class SHOCK_STUN extends SkillMode {

	public SHOCK_STUN() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		// final int[] stunTimeArray = { 500, 1000, 1500, 2000, 2500, 3000 };
		// final int[] stunTimeArray = { 1, 2, 3 };// 技能時間(秒)

		final Random random = new Random();
		int shock = random.nextInt(4) + 3;// 隨機時間2~5

		// 取回目標是否已被施展衝暈
		if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(SHOCK_STUN)) {
			shock += cha.getSkillEffectTimeSec(SHOCK_STUN);// 累計時間
		}

		if (shock > 6) {// 最大衝暈時間6秒
			shock = 6;
		}

		cha.setSkillEffect(L1SkillId.SHOCK_STUN, shock * 1000);
		// 騎士技能(衝擊之暈)
		L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));

		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance tgnpc = (L1NpcInstance) cha;
			tgnpc.setParalyzed(true);
		}

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		// final int[] stunTimeArray = { 500, 1000, 1500, 2000, 2500, 3000 };
		// final int[] stunTimeArray = { 1, 2, 3 };// 技能時間(秒)

		final Random random = new Random();
		int shock = random.nextInt(3) + 2;// 隨機時間2~3

		// 取回目標是否已被施展衝暈
		if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(SHOCK_STUN)) {
			shock += cha.getSkillEffectTimeSec(SHOCK_STUN);// 累計時間
		}

		if (shock > 6) {// 最大衝暈時間6秒
			shock = 6;
		}

		cha.setSkillEffect(L1SkillId.SHOCK_STUN, shock * 1000);
		// 騎士技能(衝擊之暈)
		L1SpawnUtil.spawnEffect(81162, shock, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));

		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1GuardianInstance) || (cha instanceof L1GuardInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance tgnpc = (L1NpcInstance) cha;
			tgnpc.setParalyzed(true);
		}

		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));

		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1GuardianInstance) || (cha instanceof L1GuardInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance npc = (L1NpcInstance) cha;
			npc.setParalyzed(false);
		}
	}
}
