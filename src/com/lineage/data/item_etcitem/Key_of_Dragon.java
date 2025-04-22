package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 龍之鑰匙
 * 
 * @author simlin
 */
public class Key_of_Dragon extends ItemExecutor {

	private int _npcId;

	private Key_of_Dragon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Key_of_Dragon();
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		if ((pc.getMapId() == 16384) || (pc.getMapId() == 16896) || (pc.getMapId() == 17408)
				|| (pc.getMapId() == 17920) || (pc.getMapId() == 18432) || (pc.getMapId() == 18944)
				|| (pc.getMapId() == 19968) || (pc.getMapId() == 19456) || (pc.getMapId() == 20480)
				|| (pc.getMapId() == 20992) || (pc.getMapId() == 21504) || (pc.getMapId() == 22016)
				|| (pc.getMapId() == 22528) || (pc.getMapId() == 23040) || (pc.getMapId() == 23552)
				|| (pc.getMapId() == 24064) || (pc.getMapId() == 24576) || (pc.getMapId() == 25088)
				|| L1CastleLocation.checkInAllWarArea(pc.getLocation())) {
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (_npcId == 0) {
			pc.sendPackets(new S_SystemMessage("龍門編號設定錯誤，請通知線上GM"));
			return;
		}
		// 刪除龍之鑰匙
		pc.getInventory().deleteItem(item);
		final int timeDragon = 120 * 60 * 1000;
		L1SpawnUtil.spawn(pc, _npcId, 0, timeDragon);
		World.get().broadcastPacketToAll(new S_ServerMessage(2921));
	}

	@Override
	public void set_set(final String[] set) {
		try {
			_npcId = Integer.parseInt(set[1]);

		} catch (final Exception e) {
		}
	}
}
