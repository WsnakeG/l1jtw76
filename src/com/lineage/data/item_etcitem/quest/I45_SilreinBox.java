package com.lineage.data.item_etcitem.quest;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * 希蓮恩之袋 49180
 */
public class I45_SilreinBox extends ItemExecutor {

	/**
	 *
	 */
	private I45_SilreinBox() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new I45_SilreinBox();
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
		pc.getInventory().removeItem(item, 1);// 移除道具
		// 任務已經開始
		if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
			// 給予任務道具(風木村莊瞬間移動卷軸 49184)
			CreateNewItem.createNewItem(pc, 49184, 1);

			// 給予任務道具(時空裂痕水晶(綠色)-3 49192)
			CreateNewItem.createNewItem(pc, 49192, 1);
			// 給予任務道具(時空裂痕水晶(綠色)-3 49192)
			CreateNewItem.createNewItem(pc, 49192, 1);
			// 給予任務道具(時空裂痕水晶(綠色)-3 49192)
			CreateNewItem.createNewItem(pc, 49192, 1);
		}
	}
}
