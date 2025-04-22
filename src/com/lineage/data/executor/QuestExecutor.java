package com.lineage.data.executor;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1Quest;

/**
 * Quest(任務) 設置執行
 */
public abstract class QuestExecutor {

	/**
	 * Quest(任務) 開機設置啟用執行<BR>
	 * 召喚相關物件
	 * 
	 * @param quest
	 */
	public abstract void execute(L1Quest quest);

	/**
	 * Quest(任務) 設置執行<BR>
	 * 設置任務開始執行(任務進度提升為1)<BR>
	 * 相關NPC可與執行者進行對話<BR>
	 * 
	 * @param pc
	 */
	public abstract void startQuest(final L1PcInstance pc);

	/**
	 * Quest(任務) 設置結束<BR>
	 * 設置任務結束(任務進度提升為255)<BR>
	 * 假設該任務可以重複執行<BR>
	 * 在此設置任務狀態移除<BR>
	 * 
	 * @param pc
	 */
	public abstract void endQuest(final L1PcInstance pc);

	/**
	 * 展示任務說明
	 * 
	 * @param pc
	 */
	public abstract void showQuest(final L1PcInstance pc);

	/**
	 * 任務終止
	 * 
	 * @param pc
	 */
	public abstract void stopQuest(L1PcInstance pc);
}
