package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;

/**
 * 莎爾的祝福(水龍副本) HP+80 MP+10 體力恢復量+3 魔力恢復量+3 水屬性魔防+30 防禦力-8 ER+15
 * 
 * @author dexc
 */
public class ADLV80_2 extends SkillMode {

	public ADLV80_2() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.ADLV80_2)) {
			srcpc.addHpr(3);
			srcpc.addMpr(3);
			srcpc.addMaxHp(80);
			srcpc.addMaxMp(10);

			srcpc.addWater(30);

			srcpc.addAc(8);

			srcpc.setSkillEffect(L1SkillId.ADLV80_2, integer * 1000);

			srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
			srcpc.sendPackets(new S_MPUpdate(srcpc));
			srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
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
		cha.addMaxHp(-80);
		cha.addMaxMp(-10);

		cha.addWater(-30);

		cha.addAc(-8);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addHpr(-3);
			pc.addMpr(-3);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
	}
}
