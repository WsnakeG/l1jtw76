package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_MPUpdate;

/**
 * 附魔石(恢復) 【+7】MP+320、魔力恢復量+3、魔法攻擊力+1 06/20新增近魔法攻擊力設置參照BS_WX09.JAVA內的寫法
 * 
 * @author dexc
 */
public class BS_WX07 extends SkillMode {

	private static final int _addmp = 320;
	private static final int _addmpr = 3;
	private static final int _addsp = 1;

	public BS_WX07() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.BS_WX07)) {
			srcpc.addMaxMp(_addmp);
			srcpc.addMpr(_addmpr);
			srcpc.addSp(_addsp);
			srcpc.setSkillEffect(L1SkillId.BS_WX07, integer * 1000);
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
		cha.addMaxMp(-_addmp);
		cha.addSp(-_addsp);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addMpr(-_addmpr);
			pc.sendPackets(new S_MPUpdate(pc));
		}
	}
}
