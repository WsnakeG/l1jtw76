package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;

/**
 * 覺醒：安塔瑞斯 (3.63C改版新技能)
 * 
 * @author terry0412
 */
public class AWAKEN_ANTHARAS extends SkillMode {

	public AWAKEN_ANTHARAS() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		// 已經有其他覺醒技能 by terry0412
		if (srcpc.hasSkillEffect(L1SkillId.AWAKEN_FAFURION)) {
			srcpc.removeSkillEffect(L1SkillId.AWAKEN_FAFURION);

		} else if (srcpc.hasSkillEffect(L1SkillId.AWAKEN_VALAKAS)) {
			srcpc.removeSkillEffect(L1SkillId.AWAKEN_VALAKAS);
		}

		if (!srcpc.hasSkillEffect(L1SkillId.AWAKEN_ANTHARAS)) {
			/**
			 * 覺醒：安塔瑞斯 使用期間：600秒 效果：以地龍之力的狀態覺醒， 持續時間內提高支撐耐性+10、防禦-3
			 */
			srcpc.addRegistSustain(10);
			srcpc.addAc(-3);
			srcpc.sendPackets(new S_OwnCharAttrDef(srcpc));
			srcpc.setSkillEffect(L1SkillId.AWAKEN_ANTHARAS, integer * 1000);
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
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.addRegistSustain(-10);
			pc.addAc(3);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		}
	}
}
