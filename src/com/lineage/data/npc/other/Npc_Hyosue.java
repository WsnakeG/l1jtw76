package com.lineage.data.npc.other;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Npc_Hyosue extends NpcExecutor {
	public static NpcExecutor get() {
		return new Npc_Hyosue();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hyosue4"));
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
		if (cmd.equals("a")) {
			try {
				if (pc.getInventory().checkItem(40308, 2000)) {
					Thread.sleep(500);
					pc.getInventory().consumeItem(40308, 2000);
					pc.beginGhost(32814, 33183, (short) 4, true, 600);
					pc.sendPackets(new S_CloseList(pc.getId()));
//					pc.save();
				} else {
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hyosue1"));
				}
			} catch (Exception e) {
			}
		} else if (cmd.equals("1")) {
			if (pc.getInventory().checkItem(40308, 10000)) {
				pc.getInventory().consumeItem(40308, 10000);
				L1Teleport.teleport(pc, 32779, 33167, (short) 4, 5, true);
				pc.sendPackets(new S_CloseList(pc.getId()));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "hyosue1"));
			}
		}
	}
}