package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 屍魂塔-天使^艾澤奇爾
 * 
 * @author
 */
public class Npc_Shenter extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Shenter.class);

	public static NpcExecutor get() {
		return new Npc_Shenter();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		try {
			if (!pc.getInventory().checkItem(240984) && !pc.getInventory().checkItem(240985)) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "shenter1"));// 顯示領取屍魂水晶
			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "shenter2"));// 顯示排行
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
		try {
			// if (cmd.equalsIgnoreCase("r")) { // 查看排名
			// SoulTowerTable.getInstance().showRank(pc);
			// //pc.sendPackets(new S_SystemMessage("暫未實裝"));
			//
			// } else

			if (cmd.equalsIgnoreCase("a")) {// 接受屍魂的水晶
				if (pc.getInventory().checkItem(49479)) { // 封印的屍魂水晶
					pc.sendPackets(new S_SystemMessage("屍魂水晶還在封印中"));
					return;
				}
				if (pc.getInventory().checkItem(49478)) { // 藍色屍魂水晶
					pc.sendPackets(new S_SystemMessage("你已經領取過了"));
					return;
				}
				if (pc.getInventory().getSize() > 175) {
					pc.sendPackets(new S_SystemMessage("你身上持有道具過多，領取失敗"));
					return;
				}
				// pc.getInventory().storeItem(240984, 1); // 給予藍色屍魂水晶
				// pc.sendPackets(new S_SystemMessage("獲得藍色屍魂水晶"));
				CreateNewItem.createNewItem(pc, 49478, 1); // 給予藍色屍魂水晶
				pc.sendPackets(new S_CloseList(pc.getId())); // 關閉對話檔
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
