package com.lineage.data.item_etcitem.wand;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

public class Create_Monster_Boss_Key extends ItemExecutor {

	/**
	 *
	 */
	private Create_Monster_Boss_Key() {
	}

	public static ItemExecutor get() {
		return new Create_Monster_Boss_Key();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {

		boolean isUse = true;

		if (pc.getMapId() == _mapid) {
			isUse = true;
		} else {
			isUse = false;
		}
		
		if(_consume > 1){
			isUse = false;
		}

		if (isUse) {
			if(_consume == 1){
				pc.getInventory().removeItem(item, 1);
			}
			if(_announcement > 0){
				World.get().broadcastPacketToAll(new S_BlueMessage(166, "\\f=玩家 【" + pc.getName() + "】 使用了 " + item.getName()));
			}
			L1SpawnUtil.spawn(pc, _npcid, 1, -1);
		}
		else {
			pc.sendPackets(new S_ServerMessage("此地圖無法使用此召喚石。"));
		}
	}

	private int _npcid = 0;
	private int _mapid = 4;
	private int _consume = 0;
	private int _announcement = 0;

	@Override
	public void set_set(String[] set) {
		try {
			_npcid = Integer.parseInt(set[1]);

		} catch (Exception e) {
		}
		try {
			_mapid = Integer.parseInt(set[2]);

		} catch (Exception e) {
		}
		try {
			_consume = Integer.parseInt(set[3]);

		} catch (Exception e) {
		}
		try {
			_announcement = Integer.parseInt(set[4]);

		} catch (Exception e) {
		}
	}

}
