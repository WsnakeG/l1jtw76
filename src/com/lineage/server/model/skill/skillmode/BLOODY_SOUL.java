package com.lineage.server.model.skill.skillmode;

import com.lineage.config.ConfigOther;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 魂體轉換
 * 
 * @author terry0412
 */
public class BLOODY_SOUL extends SkillMode {

	public BLOODY_SOUL() {
	}

	@Override
	public int start(final L1PcInstance srcpc, final L1Character cha, final L1Magic magic, final int integer)
			throws Exception {
		final int dmg = 0;

		// 魂體轉換更改為Config.server內設定 (預設值為12) by erics4179
		srcpc.setCurrentMp(srcpc.getCurrentMp() + ConfigOther.BLOODY_SOULADDMP);

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
		// TODO Auto-generated method stub
	}
}
