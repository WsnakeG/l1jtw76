package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 堅固防護
 * 
 * @author dexc
 */
public class SOLID_CARRIAGE extends SkillMode {

	public SOLID_CARRIAGE() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha,
			final L1Magic magic, final int integer) throws Exception {
		final int dmg = 0;
		final L1PcInstance pc = (L1PcInstance) cha;
		pc.setSkillEffect(L1SkillId.SOLID_CARRIAGE, integer * 1000);
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha,
			final L1Magic magic, final int integer) throws Exception {
		final int dmg = 0;

		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj)
			throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		// TODO Auto-generated method stub
	}
}
