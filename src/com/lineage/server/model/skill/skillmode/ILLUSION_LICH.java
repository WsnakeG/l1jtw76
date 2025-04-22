package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SPMR;

/**
 * 幻覺：巫妖
 * 
 * @author dexc
 */
public class ILLUSION_LICH extends SkillMode {

	public ILLUSION_LICH() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!cha.hasSkillEffect(L1SkillId.ILLUSION_LICH)) {
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.setSkillEffect(L1SkillId.ILLUSION_LICH, integer * 1000);

			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
					|| (cha instanceof L1PetInstance)) {
				final L1NpcInstance tgnpc = (L1NpcInstance) cha;
				tgnpc.addSp(2);

				tgnpc.setSkillEffect(L1SkillId.ILLUSION_LICH, integer * 1000);
			}
		}
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

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
			pc.addSp(-2);
			pc.sendPackets(new S_SPMR(pc));

		} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
				|| (cha instanceof L1PetInstance)) {
			final L1NpcInstance tgnpc = (L1NpcInstance) cha;
			tgnpc.addSp(-2);
		}
	}
}
