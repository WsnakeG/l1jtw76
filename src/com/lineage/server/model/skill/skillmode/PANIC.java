package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharStatus2;

/**
 * 恐慌
 * 
 * @author dexc
 */
public class PANIC extends SkillMode {

	public PANIC() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!cha.hasSkillEffect(L1SkillId.PANIC)) {
			// 全屬性扣除 1點
			cha.addStr(-1);
			cha.addCon(-1);
			cha.addDex(-1);
			cha.addWis(-1);
			cha.addInt(-1);

			cha.setSkillEffect(L1SkillId.PANIC, integer * 1000);

			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				// 發送更新封包
				pc.sendPackets(new S_OwnCharStatus2(pc));
				pc.sendDetails();
			
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
		// 全屬性補回 1點
		cha.addStr(1);
		cha.addCon(1);
		cha.addDex(1);
		cha.addWis(1);
		cha.addInt(1);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			// 發送更新封包
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendDetails();
		
		}
	}
}
