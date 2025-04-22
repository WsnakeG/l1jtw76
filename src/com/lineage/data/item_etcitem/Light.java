package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ItemName;

/**
 * 照明工具：<br>
 * 燈40001<br>
 * 燈籠40002<br>
 * 魔法燈籠40004<br>
 * 蠟燭40005<br>
 */
public class Light extends ItemExecutor {

	/**
	 *
	 */
	private Light() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Light();
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
		if (item.isNowLighting()) {
			item.setNowLighting(false);
			pc.turnOnOffLight();

		} else {
			item.setNowLighting(true);
			pc.turnOnOffLight();
		}
		pc.sendPackets(new S_ItemName(item));
	}
}
