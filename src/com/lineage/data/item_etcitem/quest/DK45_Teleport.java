package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 結盟瞬間移動卷軸 49226
 */
public class DK45_Teleport extends ItemExecutor {

	/**
	 *
	 */
	private DK45_Teleport() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DK45_Teleport();
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
		if (pc.isDragonKnight()) {// 龍騎
			pc.getInventory().removeItem(item, 1);
			// 傳送任務執行者(幻術師村莊)
			L1Teleport.teleport(pc, 32839, 32860, (short) 1000, 2, true);

		} else {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
