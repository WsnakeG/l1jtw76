package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1User_Power;

/**
 * 人物陣營紀錄
 * 
 * @author dexc
 */
public interface CharacterC1Storage {

	/**
	 * 初始化載入
	 */
	public void load();

	/**
	 * 傳回 L1User_Power
	 */
	public L1User_Power get(int objectId);

	/**
	 * 新建 L1User_Power
	 */
	public void storeCharacterC1(L1PcInstance pc);

	/**
	 * 更新 L1User_Power
	 */
	public void updateCharacterC1(final int object_id, final int c1_type, final String note);
}
