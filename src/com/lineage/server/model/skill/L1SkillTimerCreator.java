package com.lineage.server.model.skill;

import com.lineage.server.model.L1Character;

/**
 * @author terry0412
 */
public final class L1SkillTimerCreator {

	/**
	 * @param cha
	 * @return
	 */
	public static final Runnable create(final L1Character cha) {
		return new L1SkillTimerImpl(cha);
	}
}
