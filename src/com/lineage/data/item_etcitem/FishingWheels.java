package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 保羅的快速繞線輪<BR>
 * 
 * @author simlin
 */
public class FishingWheels extends ItemExecutor {

	/**
	 *
	 */
	private FishingWheels() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new FishingWheels();
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
		final int itemobj = data[0];
		final L1ItemInstance item1 = pc.getInventory().getItem(itemobj);
		if (item1 == null) {
			return;
		}

		final int poleId = item1.getItem().getItemId();
		if (poleId != 41484) {
			pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			return;
		}

		pc.getInventory().removeItem(item1, 1);
		pc.getInventory().removeItem(item, 1);

		CreateNewItem.createNewItem(pc, 41495, 1);
	}
}
