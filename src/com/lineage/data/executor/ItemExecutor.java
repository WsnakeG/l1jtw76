package com.lineage.data.executor;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.BinaryOutputStream;

/**
 * 道具物件執行
 */
public abstract class ItemExecutor {

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	public abstract void execute(int[] data, L1PcInstance pc, L1ItemInstance item);

	private String[] as;

	public String[] get_set() {
		return as;
	}

	public void set_set(final String[] set) {
		as = set;
	}

	public BinaryOutputStream itemStatus(final L1ItemInstance item) {
		return null;
	}
}
