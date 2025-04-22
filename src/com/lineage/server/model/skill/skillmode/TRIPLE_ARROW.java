package com.lineage.server.model.skill.skillmode;

import com.lineage.server.datatables.SprTable;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 三重矢
 * 
 * @author dexc
 */
public class TRIPLE_ARROW extends SkillMode {

	public TRIPLE_ARROW() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {

		final int dmg = 0;

		final int playerGFX = srcpc.getTempCharGfx();
		if (!SprTable.get().containsTripleArrowSpr(playerGFX)) {
			return dmg;
		}

		for (int i = 0; i < 3; i++) {
			cha.onAction(srcpc);
		}
		// 三重矢 加速封包 (原4394)
		srcpc.sendPacketsX8(new S_SkillSound(srcpc.getId(), 11764));

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {

		final int dmg = 0;
		for (int i = 3; i > 0; i--) {
			npc.attackTarget(cha);
		}
		npc.broadcastPacketX10(new S_SkillSound(npc.getId(), 11764));
		return dmg;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {

	}
}
