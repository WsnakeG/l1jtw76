package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 塗著膠水的航海日誌第1頁41048 塗著膠水的航海日誌第2頁41049 塗著膠水的航海日誌第3頁41050 塗著膠水的航海日誌第4頁41051
 * 塗著膠水的航海日誌第5頁41052 塗著膠水的航海日誌第6頁41053 塗著膠水的航海日誌第7頁41054 塗著膠水的航海日誌第8頁41055
 */
public class Log_Book extends ItemExecutor {

	/**
	 *
	 */
	private Log_Book() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Log_Book();
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
		final int itemId = item.getItemId();
		final L1ItemInstance tgItem = pc.getInventory().getItem(itemobj);

		if (tgItem == null) {
			return;
		}

		// 塗著膠水的航海日誌1~8页
		final int logbookId = tgItem.getItem().getItemId();
		if (logbookId == (itemId + 8034)) {
			CreateNewItem.createNewItem(pc, logbookId + 2, 1);
			pc.getInventory().removeItem(tgItem, 1);
			pc.getInventory().removeItem(item, 1);

		} else {
			pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
		}
	}
}
