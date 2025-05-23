package com.lineage.data.item_etcitem.quest;

import java.util.HashMap;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 混沌鑰匙 40606
 */
public class DeLv50Key extends ItemExecutor {

	/**
	 *
	 */
	private DeLv50Key() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DeLv50Key();
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
		if (!pc.isDarkelf()) {// 不是黑暗精靈
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		final HashMap<Integer, L1Object> mapList = new HashMap<Integer, L1Object>();
		mapList.putAll(World.get().getVisibleObjects(DarkElfLv50_1.MAPID));

		int i = 0;
		for (final L1Object tgobj : mapList.values()) {
			if (tgobj instanceof L1NpcInstance) {
				final L1NpcInstance tgnpc = (L1NpcInstance) tgobj;
				if (tgnpc.getNpcId() == 70905) {// 黑暗妖精試煉用障礙
					if (tgnpc.get_showId() == pc.get_showId()) {
						i++;
					}
				}
			}
		}

		if (i > 0) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));

		} else {
			if (pc.getMapId() == (short) DarkElfLv50_1.MAPID) {
				pc.getInventory().removeItem(item, 1);
				// 傳送任務執行者
				L1Teleport.teleport(pc, 32591, 32813, (short) DarkElfLv50_1.MAPID, 5, true);

			} else {
				// 沒有任何事情發生
				pc.sendPackets(new S_ServerMessage(79));
			}
		}
		mapList.clear();
	}
}
