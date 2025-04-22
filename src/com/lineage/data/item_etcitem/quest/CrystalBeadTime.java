package com.lineage.data.item_etcitem.quest;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 完整的時間水晶球 49201
 */
public class CrystalBeadTime extends ItemExecutor {

	/**
	 *
	 */
	private CrystalBeadTime() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new CrystalBeadTime();
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
		if (pc.getMapId() != 67) {// 巴拉卡斯棲息地
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}

		// 任務已經開始
		if (pc.getQuest().isStart(IllusionistLv45_1.QUEST.get_id())) {
			// 隨機周邊座標
			final L1Location loc = pc.getLocation().randomLocation(5, false);
			pc.sendPacketsXR(new S_EffectLocation(loc, 7004), 8);
			L1SpawnUtil.spawnX(80140, loc, pc.get_showId());// 時空裂痕 80140
		}
		pc.getInventory().removeItem(item, 1);// 移除道具
	}
}
