package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 藥水霜化術
 * 
 * @author dexc
 */
public class DECAY_POTION extends SkillMode {

	public DECAY_POTION() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (cha.hasSkillEffect(L1SkillId.DECAY_POTION)) {
			return dmg;
		}
		cha.set_decay_potion(true);
		cha.setSkillEffect(L1SkillId.DECAY_POTION, integer * 1000);
		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (cha.hasSkillEffect(L1SkillId.DECAY_POTION)) {
			return dmg;
		}
		cha.set_decay_potion(true);
		cha.setSkillEffect(L1SkillId.DECAY_POTION, integer * 1000);
		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		cha.set_decay_potion(false);
	}
}
