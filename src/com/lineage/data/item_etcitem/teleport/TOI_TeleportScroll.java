package com.lineage.data.item_etcitem.teleport;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>傲慢之塔移動卷軸(11F)</font><BR>
 * TOI Teleport Scroll, 11F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(21F)</font><BR>
 * TOI Teleport Scroll, 21F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(31F)</font><BR>
 * TOI Teleport Scroll, 31F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(41F)</font><BR>
 * TOI Teleport Scroll, 41F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(51F)</font><BR>
 * TOI Teleport Scroll, 51F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(61F)</font><BR>
 * TOI Teleport Scroll, 61F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(71F)</font><BR>
 * TOI Teleport Scroll, 71F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(81F)</font><BR>
 * TOI Teleport Scroll, 81F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(91F)</font><BR>
 * TOI Teleport Scroll, 91F<BR>
 * <font color=#00800>傲慢之塔移動卷軸(100F)</font><BR>
 * TOI Teleport Scroll, 100F<BR>
 * 
 * @author dexc
 */
public class TOI_TeleportScroll extends ItemExecutor {

	/**
	 *
	 */
	private TOI_TeleportScroll() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new TOI_TeleportScroll();
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

		int x = 0;
		int y = 0;
		short map = 0;

		final String nameId = item.getItem().getNameId();

		if (nameId.equalsIgnoreCase("$2169")) {// 傲慢之塔移動卷軸(11F)
			x = 32630;
			y = 32935;
			map = 111;

		} else if (nameId.equalsIgnoreCase("$2168")) {// 傲慢之塔移動卷軸(21F)
			x = 32630;
			y = 32935;
			map = 121;

		} else if (nameId.equalsIgnoreCase("$2404")) {// 傲慢之塔移動卷軸(31F)
			x = 32630;
			y = 32935;
			map = 131;

		} else if (nameId.equalsIgnoreCase("$2405")) {// 傲慢之塔移動卷軸(41F)
			x = 32630;
			y = 32935;
			map = 141;

		} else if (nameId.equalsIgnoreCase("$2673")) {// 傲慢之塔移動卷軸(51F)
			x = 32630;
			y = 32935;
			map = 151;

		} else if (nameId.equalsIgnoreCase("$2674")) {// 傲慢之塔移動卷軸(61F)
			x = 32630;
			y = 32935;
			map = 161;

		} else if (nameId.equalsIgnoreCase("$2675")) {// 傲慢之塔移動卷軸(71F)
			x = 32630;
			y = 32935;
			map = 171;

		} else if (nameId.equalsIgnoreCase("$2676")) {// 傲慢之塔移動卷軸(81F)
			x = 32630;
			y = 32935;
			map = 181;

		} else if (nameId.equalsIgnoreCase("$2677")) {// 傲慢之塔移動卷軸(91F)
			x = 32630;
			y = 32935;
			map = 191;

		} else if (nameId.equalsIgnoreCase("$2862")) {// 傲慢之塔移動卷軸(100F)
			x = 32693;
			y = 32876;
			map = 200;
		}

		if (pc.getMap().isEscapable()) {
			// 刪除道具
			pc.getInventory().removeItem(item, 1);

			L1Teleport.teleport(pc, x, y, map, 5, true);

		} else {
			// 647 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
			pc.sendPackets(new S_ServerMessage(647));
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}
}
