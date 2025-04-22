package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;

/**
 * 象牙塔魔法師^漢
 * 
 * @author XXX
 *
 */
public class Npc_Hamo extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Hamo.class);

	public static NpcExecutor get() {
		return new Npc_Hamo();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hamo2"));

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {

		if (cmd.equalsIgnoreCase("a")) {
			if (pc.getLevel() >= 52) {
				L1PcQuest quest = pc.getQuest();
				int questStep = quest.get_step(L1PcQuest.QUEST_HAMO);
				if (!pc.getInventory().checkItem(80001)// 漢的袋子
						&& questStep != L1PcQuest.QUEST_END) {
					// pc.getQuest().set_end(L1PcQuest.QUEST_HAMO);
					pc.getQuest().set_step(L1PcQuest.QUEST_HAMO, 255);
					L1ItemInstance item = null;
					item = pc.getInventory().storeItem(80001, 1);// 漢的袋子
					pc.sendPackets(new S_ServerMessage(143, npc.getNameId(), item.getName()));
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), ""));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hamo1"));
				}
			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hamo3"));
				pc.sendPackets(new S_SystemMessage("52級以上的角色就可以了."));
			}
		}
	}
}
