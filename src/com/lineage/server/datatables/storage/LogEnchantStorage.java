package com.lineage.server.datatables.storage;

import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 強化紀錄
 * 
 * @author dexc
 */
public interface LogEnchantStorage {

	/**
	 * 強化紀錄(失敗)
	 * 
	 * @param pc
	 * @param item
	 */
	public void failureEnchant(final L1PcInstance pc, final L1ItemInstance item);

}
