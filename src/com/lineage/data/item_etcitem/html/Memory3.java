package com.lineage.data.item_etcitem.html;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 49196 第三次記憶碎片
 */
public class Memory3 extends ItemExecutor {

	/**
	 *
	 */
	private Memory3() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Memory3();
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

		// 顯示內容
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "memory_3st"));
	}
}
