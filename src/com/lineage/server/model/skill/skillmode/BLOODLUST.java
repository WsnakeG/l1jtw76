package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_SkillBrave;

/**
 * 血之渴望
 * 
 * @author dexc
 */
public class BLOODLUST extends SkillMode {

	public BLOODLUST() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {

		final int dmg = 0;
		final L1PcInstance pc = (L1PcInstance) cha;
		// 強化勇氣的藥水效果 XXX 效果已在使用前判斷
		/*
		 * if (pc.hasSkillEffect(STATUS_BRAVE2)) { // 1,413：目前情況是無法使用。
		 * pc.sendPackets(new S_ServerMessage(1413)); return 0; } // 具有生命之樹果實效果
		 * if (pc.hasSkillEffect(L1SkillId.STATUS_RIBRAVE)) { //
		 * 1,413：目前情況是無法使用。 pc.sendPackets(new S_ServerMessage(1413)); return 0;
		 * }
		 */

		// 勇敢效果 抵銷對應技能
		L1BuffUtil.braveStart(pc);

		pc.setSkillEffect(L1SkillId.BLOODLUST, integer * 1000);

		/**
		 * 因無正確封包，只好使用代替方案 6 -> 1
		 */
		pc.setBraveSpeed(6);
		pc.sendPackets(new S_SkillBrave(pc.getId(), 6, integer));
		pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 6, 0));

		pc.sendPackets(new S_SkillBrave(pc.getId(), 1, integer));
		pc.broadcastPacketAll(new S_SkillBrave(pc.getId(), 1, 0));

		return dmg;
	}

	@Override
	public int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		return 0;
	}

	@Override
	public void start(final L1PcInstance srcpc, final Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(final L1Character cha) throws Exception {
		cha.setBraveSpeed(0);
		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
		}
	}
}
