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
 * 莎爾的祝福(水龍副本-強化) HP+150 MP+50 4屬性魔防+30 額外攻擊點數+1 攻擊成功+5 防禦力-10
 * 
 * @author dexc
 */
public class AGLV85_1X extends SkillMode {

	public AGLV85_1X() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.AGLV85_1X)) {
			srcpc.addMaxHp(150);
			srcpc.addMaxMp(50);

			srcpc.addWater(30);
			srcpc.addEarth(30);
			srcpc.addFire(30);
			srcpc.addWind(30);

			srcpc.addDmgup(1);
			srcpc.addBowDmgup(1);

			srcpc.addHitup(5);
			srcpc.addBowHitup(5);
			srcpc.addAc(10);

			srcpc.setSkillEffect(L1SkillId.AGLV85_1X, integer * 1000);

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
		cha.addMaxHp(-150);
		cha.addMaxMp(-50);

		cha.addWater(-30);
		cha.addEarth(-30);
		cha.addFire(-30);
		cha.addWind(-30);

		cha.addDmgup(-1);
		cha.addBowDmgup(-1);

		cha.addHitup(-5);
		cha.addBowHitup(-5);
		cha.addAc(-10);

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc));
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
	}
}
