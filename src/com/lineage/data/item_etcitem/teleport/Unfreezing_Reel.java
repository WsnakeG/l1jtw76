package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.lock.UpdateLocReading;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 解卡卷轴500051
 */
public class Unfreezing_Reel extends ItemExecutor {

	/**
	 *
	 */
	private Unfreezing_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Unfreezing_Reel();
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
		UpdateLocReading.get().setPcLoc(pc.getAccountName());
		// pc.getInventory().removeItem(item, 1);
		// 2403 帳號內其他人物傳送回指定位置！
		pc.sendPackets(new S_ServerMessage("帳號內其他人物傳送回指定位置！"));
	}
}