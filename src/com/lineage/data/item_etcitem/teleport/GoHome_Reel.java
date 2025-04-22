package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>傳送回家的卷軸40079</font><BR>
 * Scroll of Escape<BR>
 * <font color=#00800>象牙塔傳送回家的卷軸40095</font><BR>
 * Ivory Tower Scroll of Escape<BR>
 * <font color=#00800>精靈羽翼40521</font><BR>
 * Ala of Fairy<BR>
 * 
 * @author dexc
 */
public class GoHome_Reel extends ItemExecutor {

	/**
	 *
	 */
	private GoHome_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GoHome_Reel();
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
		if (pc.getMap().isEscapable() || pc.isGm()) {
			final int[] loc = GetbackTable.GetBack_Location(pc, true);
			L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
			pc.getInventory().removeItem(item, 1);

		} else {
			// 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
			pc.sendPackets(new S_ServerMessage(647));
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);
	}
}
