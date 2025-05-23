package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_Paralysis;

/**
 * 幻想
 * 
 * @author dexc
 */
public class PHANTASM extends SkillMode {

	public PHANTASM() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;// magic.calcMagicDamage(L1SkillId.PHANTASM);
		if (!cha.hasSkillEffect(L1SkillId.FOG_OF_SLEEPING)) {
			cha.setSkillEffect(L1SkillId.FOG_OF_SLEEPING, integer * 1000);

			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
			}
			cha.setSleeped(true);
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
		// TODO Auto-generated method stub
	}
}
