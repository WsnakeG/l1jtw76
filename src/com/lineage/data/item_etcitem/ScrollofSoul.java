package com.lineage.data.item_etcitem;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔族的卷軸49013</font><BR>
 * Scroll of Soul
 * 
 * @see 使用者死亡 並取得任務道具 靈魂之球<BR>
 * @author dexc
 */
public class ScrollofSoul extends ItemExecutor {

	/**
	 *
	 */
	private ScrollofSoul() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollofSoul();
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

		final String itenName = item.getLogName();

		if (pc.castleWarResult() == true) { // 戰爭中
			// 330 \f1無法使用 %0%o。
			pc.sendPackets(new S_ServerMessage(403, itenName));

		} else if (pc.getMapId() == 303) { // 夢幻之島
			// 330 \f1無法使用 %0%o。
			pc.sendPackets(new S_ServerMessage(403, itenName));

		} else {
			// 刪除道具
			pc.getInventory().removeItem(item, 1);

			// 使用者死亡
			pc.death(null);

			final int newItemId = 49014;// 靈魂之球
			// 取得任務道具
			CreateNewItem.createNewItem(pc, newItemId, 1);
		}
	}
}
