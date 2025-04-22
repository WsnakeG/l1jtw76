package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 尖刺盔甲
 * 
 * @author terry0412
 */
public class BOUNCE_ATTACK extends SkillMode {

	public BOUNCE_ATTACK() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		if (!srcpc.hasSkillEffect(L1SkillId.BOUNCE_ATTACK)) {
			// 近距離命中+6
			srcpc.addHitup(6);
		}

		srcpc.setSkillEffect(L1SkillId.BOUNCE_ATTACK, integer * 1000);

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
			// 近距離命中-6
			pc.addHitup(-6);
		}
	}
}
