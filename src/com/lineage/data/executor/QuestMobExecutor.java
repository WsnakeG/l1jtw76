package com.lineage.data.executor;

import com.lineage.server.templates.L1QuestUser;

/**
 * Quest(任務) 怪物剩餘0設置執行
 */
public abstract class QuestMobExecutor {

	/**
	 * 任務終止(怪物剩餘0)
	 * 
	 * @param quest
	 */
	public abstract void stopQuest(L1QuestUser quest);
}
