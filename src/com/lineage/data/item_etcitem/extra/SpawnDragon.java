package com.lineage.data.item_etcitem.extra;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcInventory;
import com.lineage.server.model.Instance.L1ItemInstance;
// import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BoxMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 仿正道具：龍之鑰匙 (怪物編號/數量/地圖X/地圖Y/地圖編號/召喚是否公告)
 * 
 * @author Smile
 */
public class SpawnDragon extends ItemExecutor {

	/**
	 *
	 */
	private SpawnDragon() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SpawnDragon();
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

		for (L1Object object : World.get().getVisibleObjects(_mapid).values()) {
			if (object instanceof L1NpcInstance) {
				L1NpcInstance find_npc = (L1NpcInstance) object;
				if (find_npc.getNpcId() == _mobId) {
					pc.sendPackets(new S_SystemMessage("\\aG該NPC已經存在囉!"));
					return;
				}
			}
		}

		// 若剩餘次數剩餘1次，再次使用後將自動刪除道具
		if (item.getChargeCount() > 1) {
			item.setChargeCount(item.getChargeCount() - 1);
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
		} else {
			pc.getInventory().removeItem(item, 1);
		}

		for (int i = 0; i < _mobCount; i++) {
			L1SpawnUtil.spawnT(_mobId, _locX, _locY, _mapid, 0, 0);
		}

		final L1Npc npcTemp = NpcTable.get().getTemplate(_mobId);
		final L1Location loc = new L1Location(_locX, _locY, _mapid);
		NpcSpawnTable.get().storeSpawn(_mobId, loc.getX(), loc.getY(), loc.getMapId(), 7200000);
		L1SpawnUtil.spawn(_mobId, loc, 0, -1);

		if (_isShow) {
			World.get().broadcastChatAll(new S_BoxMessage("\\aD★" + npcTemp.get_name() + "\\aD★出現了!"));
		}
	}

	private int _mobId; // 召喚怪物ID

	private int _mobCount; // 召喚數量

	private int _locX;

	private int _locY;

	private short _mapid;

	private boolean _isShow;

	@Override
	public void set_set(String[] set) {
		try {
			_mobId = Integer.parseInt(set[1]);
			_mobCount = Integer.parseInt(set[2]);
			_locX = Integer.parseInt(set[3]);
			_locY = Integer.parseInt(set[4]);
			_mapid = Short.parseShort(set[5]);
			_isShow = Boolean.parseBoolean(set[6]);
		} catch (Exception e) {
		}
	}
}