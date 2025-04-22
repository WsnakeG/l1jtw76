package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/***
 * 第二次邪念碎片 49198
 */
public class ThoughtFragment2 extends ItemExecutor {

	/**
	 *
	 */
	private ThoughtFragment2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ThoughtFragment2();
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
		if (item1 != null) {
			if (item1.getItemId() == 49197) {// 第一次邪念碎片 49197
				pc.getInventory().removeItem(item, 1);
				pc.getInventory().removeItem(item1, 1);
				// 任務已經開始
				if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
					// 給予任務道具(未完成的時間水晶球 49200)
					CreateNewItem.createNewItem(pc, 49200, 1);
				}

			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}

		} else {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
