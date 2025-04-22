package com.lineage.data.item_etcitem.extra;

import com.lineage.config.ConfigAlt;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DragonDoor;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;

/**
 * 龍之鑰匙
 * 
 * @author terry0412
 */
public class DragonKey extends ItemExecutor {

	/**
	 *
	 */
	private DragonKey() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new DragonKey();
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
		final int castleId = L1CastleLocation.getCastleIdByArea(pc);
		if ((castleId > 0) && ServerWarExecutor.get().isNowWar(castleId)) {
			pc.sendPackets(new S_ServerMessage(1892));
			return;
		}
		boolean isChecked = false;
		for (final int mapid : ConfigAlt.DRAGON_KEY_MAP_LIST) {
			if (mapid == pc.getMapId()) {
				isChecked = true;
				break;
			}
		}
		if (!isChecked) {
			pc.sendPackets(new S_ServerMessage(1892));
			return;
		}
		int a = 6;
		int b = 6;
		int c = 6;
		final int d = 0;
		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1NpcInstance) {
				final L1NpcInstance npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_npcId() == 70932) { // 地
					a--;
				} else if (npc.getNpcTemplate().get_npcId() == 70937) { // 水
					b--;
				} else if (npc.getNpcTemplate().get_npcId() == 70934) { // 風
					c--;
				} /*
					 * else if (npc.getNpcTemplate().get_npcId() == 70933) { //
					 * 火 b--; }
					 */
			}
		}
		// 顯示對話
		pc.sendPackets(new S_DragonDoor(item.getId(), a, b, c, d));
	}
}
