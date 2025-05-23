package com.lineage.data.item_etcitem.quest;

import java.util.HashMap;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 49227 紅色之火碎片
 */
public class DK50_Flute extends ItemExecutor {

	/**
	 *
	 */
	private DK50_Flute() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DK50_Flute();
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
		if (pc.getMapId() != DragonKnightLv50_1.MAPID) {// 奎斯特
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		final HashMap<Integer, L1Object> mapList = new HashMap<Integer, L1Object>();
		mapList.putAll(World.get().getVisibleObjects(pc.getMapId()));

		int i = 0;
		for (final L1Object tgobj : mapList.values()) {
			if (tgobj instanceof L1MonsterInstance) {
				final L1MonsterInstance tgnpc = (L1MonsterInstance) tgobj;
				// 不同副本忽略
				if (pc.get_showId() != tgnpc.get_showId()) {
					continue;
				}
				if (tgnpc.getNpcId() == 80139) {// 路西爾斯
					i += 1;
				}
			}
		}

		if (i > 0) {// 已有路西爾斯
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));

		} else {// 召喚路西爾斯
			// 隨機周邊座標
			final L1Location loc = pc.getLocation().randomLocation(5, false);
			pc.sendPacketsXR(new S_EffectLocation(loc, 7004), 8);
			final L1MonsterInstance mob = L1SpawnUtil.spawnX(80139, loc, pc.get_showId());
			mob.setLink(pc);
			pc.getInventory().removeItem(item, 1);// 移除道具
		}
		mapList.clear();
	}
}
