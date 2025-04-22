package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1ItemIntegration;

/**
 * william 道具融合系統
 * 
 * @author Roy
 */
public class ItemIntegration extends ItemExecutor {

	/**
	 *
	 */
	private ItemIntegration() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ItemIntegration();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 對象OBJID
		final int targObjId = data[0];

		// 目標物品
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);
		if (tgItem == null) {
			return;
		}
		// 道具融合系統類
		L1ItemIntegration.forItemIntegration(pc, item, tgItem);
	}
}
