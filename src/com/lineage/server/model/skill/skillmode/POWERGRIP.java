package com.lineage.server.model.skill.skillmode;

import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;

import java.util.Random;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 拘束移動
 * 
 * @author simlin
 */
public class POWERGRIP extends SkillMode {

	public POWERGRIP() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		final Random random = new Random();
		int grip = random.nextInt(4) + 3;// 隨機時間2~5

		if ((cha instanceof L1PcInstance) && cha.hasSkillEffect(POWERGRIP)) {
			grip += cha.getSkillEffectTimeSec(POWERGRIP);// 累計時間
		}

		if (grip > 6) {
			grip = 6;
		}

		cha.setSkillEffect(POWERGRIP, grip * 1000);

		L1SpawnUtil.spawnEffect(93004, grip, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
		L1SpawnUtil.spawnEffect(93003, grip, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_POWERGRIP, true));
		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance npc = (L1NpcInstance) cha;
			npc.setGripped(true);
		}

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		return 0;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_POWERGRIP, false));
		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance npc = (L1NpcInstance) cha;
			npc.setGripped(false);
		}
	}
}
