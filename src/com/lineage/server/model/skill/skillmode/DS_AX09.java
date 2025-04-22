package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus2;

/**
 * 龍印魔石(弓手) 體力上限+60、魔力上限+43、遠距離命中率+3、體力恢復量+3、魔力恢復量+3、遠距離攻擊力+3、敏捷+1
 * 
 * @author dexc
 */
public class DS_AX09 extends SkillMode {

	private static final int _addhp = 60;
	private static final int _addmp = 43;
	private static final int _addhitbow = 3;
	private static final int _addhpr = 3;
	private static final int _addmpr = 3;
	private static final int _adddmgbow = 3;
	private static final int _adddex = 1;

	public DS_AX09() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.DS_AX09)) {
			srcpc.addMaxHp(_addhp);
			srcpc.addMaxMp(_addmp);
			srcpc.addBowHitup(_addhitbow);
			srcpc.addHpr(_addhpr);
			srcpc.addMpr(_addmpr);
			srcpc.addBowDmgup(_adddmgbow);
			srcpc.addDex(_adddex);
			srcpc.setSkillEffect(L1SkillId.DS_AX09, integer * 1000);
			srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
			srcpc.sendPackets(new S_MPUpdate(srcpc));
			srcpc.sendPackets(new S_OwnCharStatus2(srcpc));
			srcpc.sendDetails();
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
		cha.addMaxHp(-_addhp);
		cha.addMaxMp(-_addmp);
		cha.addBowHitup(-_addhitbow);
		cha.addBowDmgup(-_adddmgbow);
		cha.addDex(_adddex);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addHpr(-_addhpr);
			pc.addMpr(-_addmpr);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc));
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendDetails();
		}
	}
}
