package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_SPMR;

/**
 * 附魔石(防禦) 【+7】魔防+9、防禦力-10、傷害減免+5 06/20新增傷害減免位置參照BS_ASX08.JAVA內的寫法
 * 
 * @author dexc
 */
public class BS_ASX07 extends SkillMode {

	private static final int _addac = -10;
	private static final int _addmr = 9;
	private static final int _adddmgdown = 5;

	public BS_ASX07() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.BS_ASX07)) {
			srcpc.addAc(_addac);
			srcpc.addMr(_addmr);
			srcpc.addDamageReductionByArmor(_adddmgdown);
			srcpc.setSkillEffect(L1SkillId.BS_ASX07, integer * 1000);
			srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
			srcpc.sendPackets(new S_SPMR(srcpc));
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
		cha.addAc(-_addac);
		cha.addMr(-_addmr);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addDamageReductionByArmor(-_adddmgdown);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_SPMR(pc));
		}
	}
}
