package com.lineage.data.item_etcitem;

import java.util.Iterator;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.world.WorldMob;

/**
 * BOSS雷達
 * 
 * @author erics4179
 */

public class Boss_TrackingTechnique extends ItemExecutor {
	private Boss_TrackingTechnique() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Boss_TrackingTechnique();
	}

	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// TODO 自動產生的方法 Stub0
		// final int chargeCount = item.getChargeCount();

		pc.sendPackets(new S_SystemMessage("\\aD雷達開啟搜尋中..."));
		L1MonsterInstance find_npc = null;
		final Iterator<L1MonsterInstance> itr = WorldMob.get().getAllMonster().iterator();
		while (itr.hasNext()) {

			final L1MonsterInstance boss = itr.next();
			if ((boss.getMapId() == pc.getMapId()) && (!boss.isDead()) && (boss.getNpcTemplate().is_boss())) {
				find_npc = boss;
			}
			if (find_npc != null) {
				break;
			}
		}
		if (find_npc != null) {
			L1Teleport.teleport(pc, find_npc.getX(), find_npc.getY(), find_npc.getMapId(), pc.getHeading(),
					true);
			pc.sendPackets(new S_SystemMessage("\\aD使用BOSS雷達將您傳送到Boss：" + find_npc.getName() + " 的面前..."));
		} else {
			pc.sendPackets(new S_SystemMessage("\\aG此地圖中已經搜尋不到Boss了..."));
		}
		pc.getInventory().removeItem(item, 1);
	}
}