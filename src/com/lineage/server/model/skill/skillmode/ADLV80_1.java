package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.ability.S_WeightStatus;

/**
 * 卡瑞的祝福(地龍副本) HP+100 MP+50 體力恢復量+3 魔力恢復量+3 地屬性魔防+30 額外攻擊點數+1 攻擊成功+5 ER+30 現有負重
 * / 1.04
 * 
 * @author dexc
 */
public class ADLV80_1 extends SkillMode {

	public ADLV80_1() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.ADLV80_1)) {
			srcpc.addHpr(3);
			srcpc.addMpr(3);
			srcpc.addMaxHp(100);
			srcpc.addMaxMp(50);

			srcpc.addEarth(30);

			srcpc.addDmgup(1);
			srcpc.addBowDmgup(1);

			srcpc.addHitup(5);
			srcpc.addBowHitup(5);

			srcpc.addWeightReduction(4);

			srcpc.setSkillEffect(L1SkillId.ADLV80_1, integer * 1000);

			srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
			srcpc.sendPackets(new S_MPUpdate(srcpc));
			srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
			srcpc.sendPackets(new S_WeightStatus(srcpc.getInventory().getWeight() * 100 / (int)srcpc.getMaxWeight(), srcpc.getInventory().getWeight(), (int)srcpc.getMaxWeight()));

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
		cha.addMaxHp(-100);
		cha.addMaxMp(-50);

		cha.addEarth(-30);

		cha.addDmgup(-1);
		cha.addBowDmgup(-1);

		cha.addHitup(-5);
		cha.addBowHitup(-5);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addHpr(-3);
			pc.addMpr(-3);
			pc.addWeightReduction(-4);

			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
		}
	}
}
