package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_MPUpdate;

/**
 * 附魔石(遠攻) 【+8】HP+180、MP+180、體力恢復量+3、魔力恢復量+3、遠距離攻擊力+3、遠距離命中率+2
 * 06/20新增遠距離命中與攻擊傷害設置參照BS_AX09.JAVA內的寫法
 * 
 * @author dexc
 */
public class BS_AX08 extends SkillMode {

	private static final int _addhp = 180;
	private static final int _addmp = 180;
	private static final int _addhpr = 3;
	private static final int _addmpr = 3;
	private static final int _addhitbow = 2;
	private static final int _adddmgbow = 3;

	public BS_AX08() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.BS_AX08)) {
			srcpc.addMaxHp(_addhp);
			srcpc.addMaxMp(_addmp);
			srcpc.addHpr(_addhpr);
			srcpc.addMpr(_addmpr);
			srcpc.addBowHitup(_addhitbow);
			srcpc.addBowDmgup(_adddmgbow);
			srcpc.setSkillEffect(L1SkillId.BS_AX08, integer * 1000);
			srcpc.sendPackets(new S_HPUpdate(srcpc.getCurrentHp(), srcpc.getMaxHp()));
			srcpc.sendPackets(new S_MPUpdate(srcpc));
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
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addHpr(-_addhpr);
			pc.addMpr(-_addmpr);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc));
		}
	}
}
