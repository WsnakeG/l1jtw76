package com.lineage.data.item_etcitem.shop;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Suona;
import com.lineage.server.world.World;

/**
 * 全頻廣播器
 * 
 * @author simlin
 */
public class Suona extends ItemExecutor {

	/**
	 *
	 */
	private Suona() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Suona();
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
		final byte[] text = pc.getTextByte();
		if (text == null) {// 無字串傳回
			return;
		}
		// 清空字串
		pc.setTextByte(null);

		pc.getInventory().removeItem(item, 1);

		World.get().broadcastPacketToAll(new S_Suona(pc, text));
	}
}
