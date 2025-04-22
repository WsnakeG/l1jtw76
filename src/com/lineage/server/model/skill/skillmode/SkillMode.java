package com.lineage.server.model.skill.skillmode;

import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

public abstract class SkillMode {

	/**
	 * @param pc 施展者
	 * @param cha 目標
	 * @param magic L1Magic
	 * @param integer 相關數字項目(技能時間/秒)
	 * @return
	 * @throws Exception
	 */
	public abstract int start(final L1PcInstance pc, final L1Character cha, final L1Magic magic,
			final int integer) throws Exception;

	/**
	 * @param npc 施展者
	 * @param cha 目標
	 * @param magic L1Magic
	 * @param integer 相關數字項目(技能時間/秒)
	 * @return
	 * @throws Exception
	 */
	public abstract int start(final L1NpcInstance npc, final L1Character cha, final L1Magic magic,
			final int integer) throws Exception;

	public abstract void stop(final L1Character cha) throws Exception;

	public abstract void start(final L1PcInstance pc, final Object obj) throws Exception;

}
