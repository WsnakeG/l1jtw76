package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_OwnCharStatus2;

/**
 * 龍印魔石(鬥士) 體力上限+120、近距離命中率+3、體力恢復量+6、近距離攻擊力+3、力量+1
 * 
 * @author dexc
 */
public class DS_GX09 extends SkillMode {

	private static final int _addhp = 120;
	private static final int _addhit = 3;
	private static final int _addhpr = 6;
	private static final int _adddmg = 3;
	private static final int _addstr = 1;

	public DS_GX09() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.DS_GX09)) {
			srcpc.addMaxHp(_addhp);
			srcpc.addHitup(_addhit);
			srcpc.addHpr(_addhpr);
			srcpc.addDmgup(_adddmg);
			srcpc.addStr(_addstr);
			srcpc.setSkillEffect(L1SkillId.DS_GX09, integer * 1000);
			srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
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
		cha.addHitup(-_addhit);
		cha.addDmgup(-_adddmg);
		cha.addStr(-_addstr);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addHpr(-_addhpr);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendDetails();
		}
	}
}
