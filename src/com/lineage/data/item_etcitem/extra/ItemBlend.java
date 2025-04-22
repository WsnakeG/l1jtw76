package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Blend;

/**
 * 道具融合系統
 * 
 * @author terry0412
 */
public class ItemBlend extends ItemExecutor {

	/**
	 *
	 */
	private ItemBlend() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ItemBlend();
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
		// 道具融合系統類
		if (L1Blend.checkItemId(item.getItem().getItemId()) != 0) {
			L1Blend.getItemBlend(pc, item, item.getItem().getItemId());

		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1沒有任何事情發生。
		}
	}
}
