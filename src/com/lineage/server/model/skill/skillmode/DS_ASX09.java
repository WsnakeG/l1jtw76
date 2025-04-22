package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_SPMR;

/**
 * 龍印魔石(衝鋒) 防禦-5、魔法防禦額外點數+21、額外傷害減免+4、昏迷耐性+5、體質+1
 * 
 * @author dexc
 */
public class DS_ASX09 extends SkillMode {

	private static final int _addac = -5;
	private static final int _addmr = 21;
	private static final int _adddmgdown = 4;
	private static final int _addstun = 5;
	private static final int _addcon = 1;

	public DS_ASX09() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;
		if (!srcpc.hasSkillEffect(L1SkillId.DS_ASX09)) {
			srcpc.addAc(_addac);
			srcpc.addMr(_addmr);
			srcpc.addDamageReductionByArmor(_adddmgdown);
			srcpc.addRegistStun(_addstun);
			srcpc.addCon(_addcon);
			srcpc.setSkillEffect(L1SkillId.DS_ASX09, integer * 1000);
			srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
			srcpc.sendPackets(new S_SPMR(srcpc));
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
		cha.addAc(-_addac);
		cha.addMr(-_addmr);
		cha.addRegistStun(-_addstun);
		cha.addCon(-_addcon);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addDamageReductionByArmor(-_adddmgdown);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendDetails();
		}
	}
}
