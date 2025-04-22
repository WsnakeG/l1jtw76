package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_EffectLocation;

/**
 * 新林德拜爾-空中尖刺
 * 
 * @author terry0412
 */
public class LINDVIOR_SKY_SPIKED extends SkillMode {

	public LINDVIOR_SKY_SPIKED() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = magic.calcMagicDamage(L1SkillId.LINDVIOR_SKY_SPIKED);

		// 取得差異面向
		final int dir = npc.targetDirection(cha.getX(), cha.getY());
		if ((dir == 3) || (dir == 4)) {
			npc.broadcastPacketAll(new S_EffectLocation(cha.getX(), cha.getY(), 7987));
		} else if (dir == 5) {
			npc.broadcastPacketAll(new S_EffectLocation(cha.getX(), cha.getY(), 8050));
		} else if ((dir == 6) || (dir == 7)) {
			npc.broadcastPacketAll(new S_EffectLocation(cha.getX(), cha.getY(), 8051));
		}
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
