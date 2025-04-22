package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

//81445	史奈普
public class Npc_Snip extends NpcExecutor {

	public static NpcExecutor get() {
		return new Npc_Snip();
	}

	public int type() {
		return 3;
	}

	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		if (pc.getQuest().get_step(58001) == 1 && pc.getQuest().get_step(58002) == 1
				&& pc.getQuest().get_step(58003) == 1) {

			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "slot5"));
		} else {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "slot7"));
		}
	}

	public void action(L1PcInstance pc, L1NpcInstance npc, String cmd, long amount) {
		if (cmd.equalsIgnoreCase("A")) {// 76
			if (pc.getQuest().get_step(58003) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
				return;
			}
			if (pc.getLevel() < 76) {
				pc.sendPackets(new S_ServerMessage("您的條件不足。"));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			if (!pc.getInventory().checkItem(40308, 10000000)) {
				pc.sendPackets(new S_ServerMessage("您的條件不足。"));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			// System.out.println("slot76");
			if (pc.getQuest().get_step(58003) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
			} else {
				pc.setSlot(76);
				//pc.sendPackets(new S_Message_YN(3312));
				pc.getInventory().consumeItem(40308, 10000000);// 扣除金幣
				pc.getQuest().set_step(58003, 1);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
			}

		} else if (cmd.equalsIgnoreCase("B")) {// 81
			if (pc.getQuest().get_step(58003) != 1) {
				pc.sendPackets(new S_ServerMessage("尚未開通Lv76戒指欄位。"));
				return;
			}
			if (pc.getQuest().get_step(58002) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
				return;
			}
			if (pc.getLevel() < 81) {
				pc.sendPackets(new S_ServerMessage("您的條件不足。"));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			if (!pc.getInventory().checkItem(40308, 30000000)) {
				pc.sendPackets(new S_ServerMessage("您的條件不足。"));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			// System.out.println("slot81");
			if (pc.getQuest().get_step(58002) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
			} else {
				pc.setSlot(81);
				//pc.sendPackets(new S_Message_YN(3313));
				pc.getInventory().consumeItem(40308, 30000000);// 扣除金幣
				pc.getQuest().set_step(58002, 1);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
			}
		} else if (cmd.equalsIgnoreCase("C")) { // 59
			if (pc.getQuest().get_step(58001) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
				return;
			}
			if (pc.getLevel() < 59) {
				pc.sendPackets(new S_ServerMessage(3253));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			if (!pc.getInventory().checkItem(40308, 20000000)) {
				pc.sendPackets(new S_ServerMessage("您的條件不足。"));
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot11"));
				return;
			}
			if (pc.getQuest().get_step(58001) == 1) {
				pc.sendPackets(new S_ServerMessage(3254));
			} else {
				pc.setSlot(59);
				//pc.sendPackets(new S_Message_YN(3589));
				pc.getInventory().consumeItem(40308, 20000000);// 扣除金幣
				pc.getQuest().set_step(58001, 1);
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "slot9"));
			}
		}
	}
}
