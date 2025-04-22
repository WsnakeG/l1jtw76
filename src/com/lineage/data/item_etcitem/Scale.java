package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 暗之鱗41154<br>
 * 火之鱗41155<br>
 * 叛之鱗41156<br>
 * 恨之鱗41157<br>
 * 
 * @author dexc
 */
public class Scale extends ItemExecutor {

	/**
	 *
	 */
	private Scale() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Scale();
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
		final int itemId = item.getItemId();
		usePolyScale(pc, itemId);
		pc.getInventory().removeItem(item, 1);
	}

	private void usePolyScale(final L1PcInstance pc, final int itemId) {
		int polyId = 0;
		if (itemId == 41154) { // 暗之鱗
			polyId = 3101;

		} else if (itemId == 41155) { // 火之鱗
			polyId = 3126;

		} else if (itemId == 41156) { // 叛之鱗
			polyId = 3888;

		} else if (itemId == 41157) { // 恨之鱗
			polyId = 3784;
		}
		L1PolyMorph.doPoly(pc, polyId, 600, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	}
}
