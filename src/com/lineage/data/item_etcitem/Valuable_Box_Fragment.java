package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 歐西里斯初級寶箱碎片(上)49093<br>
 * 歐西里斯初級寶箱碎片(下)49094<br>
 * 歐西里斯高級寶箱碎片(上)49097<br>
 * 歐西里斯高級寶箱碎片(下)49098<br>
 * <br>
 * 庫庫爾坎初級寶箱碎片(上)49269<br>
 * 庫庫爾坎初級寶箱碎片(下)49270<br>
 * 庫庫爾坎高級寶箱碎片(上)49271<br>
 * 庫庫爾坎高級寶箱碎片(下)49272<br>
 */
public class Valuable_Box_Fragment extends ItemExecutor {

	/**
	 *
	 */
	private Valuable_Box_Fragment() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Valuable_Box_Fragment();
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
		switch (itemId) {
		case 49093: // 歐西裡斯初級寶箱碎片：上
			if (pc.getInventory().checkItem(49094, 1)) {
				pc.getInventory().consumeItem(49093, 1);
				pc.getInventory().consumeItem(49094, 1);
				CreateNewItem.createNewItem(pc, 49095, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49094: // 歐西裡斯初級寶箱碎片：下
			if (pc.getInventory().checkItem(49093, 1)) {
				pc.getInventory().consumeItem(49093, 1);
				pc.getInventory().consumeItem(49094, 1);
				CreateNewItem.createNewItem(pc, 49095, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49097: // 歐西裡斯高級寶箱碎片：上
			if (pc.getInventory().checkItem(49098, 1)) {
				pc.getInventory().consumeItem(49097, 1);
				pc.getInventory().consumeItem(49098, 1);
				CreateNewItem.createNewItem(pc, 49099, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49098: // 歐西裡斯高級寶箱碎片：下
			if (pc.getInventory().checkItem(49097, 1)) {
				pc.getInventory().consumeItem(49097, 1);
				pc.getInventory().consumeItem(49098, 1);
				CreateNewItem.createNewItem(pc, 49099, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		// //////////////// 庫庫爾坎/////////////////FIXME
		case 49269: // 庫庫爾坎初級寶箱碎片(上)
			if (pc.getInventory().checkItem(49270, 1)) {
				pc.getInventory().consumeItem(49270, 1);
				pc.getInventory().consumeItem(49269, 1);
				CreateNewItem.createNewItem(pc, 49274, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49270: // 庫庫爾坎初級寶箱碎片(下)
			if (pc.getInventory().checkItem(49269, 1)) {
				pc.getInventory().consumeItem(49269, 1);
				pc.getInventory().consumeItem(49270, 1);
				CreateNewItem.createNewItem(pc, 49274, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49271: // 庫庫爾坎高級寶箱碎片(上)
			if (pc.getInventory().checkItem(49272, 1)) {
				pc.getInventory().consumeItem(49272, 1);
				pc.getInventory().consumeItem(49271, 1);
				CreateNewItem.createNewItem(pc, 49275, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;

		case 49272: // 庫庫爾坎高級寶箱碎片(下)
			if (pc.getInventory().checkItem(49271, 1)) {
				pc.getInventory().consumeItem(49271, 1);
				pc.getInventory().consumeItem(49272, 1);
				CreateNewItem.createNewItem(pc, 49275, 1);

			} else {
				pc.sendPackets(new S_ServerMessage(79)); // 没有任何事情发生。
			}
			break;
		}

	}
}
