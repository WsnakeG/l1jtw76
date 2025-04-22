package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1SkillId;

/**
 * 覺醒：巴拉卡斯 (3.63C改版新技能)
 * 
 * @author terry0412
 */
public class AWAKEN_VALAKAS extends SkillMode {

	public AWAKEN_VALAKAS() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		// 已經有其他覺醒技能 by terry0412
		if (srcpc.hasSkillEffect(L1SkillId.AWAKEN_ANTHARAS)) {
			srcpc.removeSkillEffect(L1SkillId.AWAKEN_ANTHARAS);

		} else if (srcpc.hasSkillEffect(L1SkillId.AWAKEN_FAFURION)) {
			srcpc.removeSkillEffect(L1SkillId.AWAKEN_FAFURION);
		}

		if (!srcpc.hasSkillEffect(L1SkillId.AWAKEN_VALAKAS)) {
			/**
			 * 覺醒：巴拉卡斯 使用期間：600秒 效果：以火龍之力的狀態覺醒， 持續時間內提升昏迷耐性+10、攻擊成功+5
			 */
			srcpc.addRegistStun(10);
			srcpc.addHitup(5);
			srcpc.setSkillEffect(L1SkillId.AWAKEN_VALAKAS, integer * 1000);
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
			pc.addRegistStun(-10);
			pc.addHitup(-5);
		}
	}
}
