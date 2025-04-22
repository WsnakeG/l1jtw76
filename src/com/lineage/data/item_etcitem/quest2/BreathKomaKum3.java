package com.lineage.data.item_etcitem.quest2;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.ADLv80_1;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 42512 喀瑪王之心 (C)
 */
public class BreathKomaKum3 extends ItemExecutor {

	/**
	 *
	 */
	private BreathKomaKum3() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new BreathKomaKum3();
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
		if (pc.getMapId() == ADLv80_1.MAPID) {
			pc.getInventory().removeItem(item, 1);

			for (final L1Object obj : World.get().getVisibleObjects(ADLv80_1.MAPID).values()) {
				if (obj instanceof L1DoorInstance) {
					final L1DoorInstance door = (L1DoorInstance) obj;
					if (door.get_showId() != pc.get_showId()) {
						continue;
					}
					if (door.getDoorId() == 10007) {// 喀瑪王(C)
						// if (door.getX() == 32781 && door.getY() == 32919) {//
						// 喀瑪王(C)
						door.open();
						door.deleteMe();
					}
				}
			}

		} else {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
