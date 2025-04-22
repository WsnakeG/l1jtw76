package com.lineage.server.datatables.storage;

import java.util.Map;

import com.lineage.server.templates.CharQuest;

/**
 * 任務紀錄
 * 
 * @author dexc
 */
public interface CharacterQuestStorage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回任務組
	 * 
	 * @param char_id 人物OBJID
	 * @return
	 */
	public Map<Integer, Integer> get(final int char_id);

	/**
	 * 新建任務
	 * 
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 * @param value 任務進度
	 */
	public void storeQuest(final int char_id, final int key, final int value);

	/**
	 * 更新任務
	 * 
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 * @param value 任務進度
	 */
	public void updateQuest(final int char_id, final int key, final int value);
	
	/**
	 * 解除任務
	 * 
	 * @param char_id 人物OBJID
	 * @param key 任務編號
	 */
	public void delQuest(final int char_id, final int key);

}
