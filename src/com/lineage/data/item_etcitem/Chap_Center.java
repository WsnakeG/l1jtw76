package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 龜裂之核49092
 */
public class Chap_Center extends ItemExecutor {

	/**
	 *
	 */
	private Chap_Center() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Chap_Center();
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
		final L1ItemInstance tgItem = pc.getInventory().getItem(itemobj);
		final int targetItemId = tgItem.getItem().getItemId();
		switch (targetItemId) {
		case 49095:// 上鎖的歐西裡斯初級寶箱
			if (!pc.getInventory().consumeItem(49092, 1)) {
				return;
			}
			if (!pc.getInventory().consumeItem(targetItemId, 1)) {
				return;
			}
			// 開鎖的歐西裡斯初級寶箱
			CreateNewItem.createNewItem(pc, 49096, 1);
			break;

		case 49099:// 上鎖的歐西裡斯高級寶箱
			if (!pc.getInventory().consumeItem(49092, 1)) {
				return;
			}
			if (!pc.getInventory().consumeItem(targetItemId, 1)) {
				return;
			}
			// 開鎖的歐西裡斯高級寶箱
			CreateNewItem.createNewItem(pc, 49100, 1);
			break;

		case 49274:// 上鎖的庫庫爾坎初級寶箱
			if (!pc.getInventory().consumeItem(49092, 1)) {
				return;
			}
			if (!pc.getInventory().consumeItem(targetItemId, 1)) {
				return;
			}
			// 開鎖的庫庫爾坎初級寶箱
			CreateNewItem.createNewItem(pc, 49284, 1);
			break;

		case 49275:// 上鎖的庫庫爾坎高級寶箱
			if (!pc.getInventory().consumeItem(49092, 1)) {
				return;
			}
			if (!pc.getInventory().consumeItem(targetItemId, 1)) {
				return;
			}
			// 開鎖的庫庫爾坎高級寶箱
			CreateNewItem.createNewItem(pc, 49285, 1);
			break;

		default:
			pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			break;
		}
	}
}
