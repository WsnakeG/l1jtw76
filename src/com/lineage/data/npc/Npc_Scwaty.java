package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyList;
import com.lineage.server.serverpackets.S_ShopBuyListAll;
import com.lineage.server.serverpackets.S_ShopBuyListCn;
import com.lineage.server.serverpackets.S_ShopSellList;
import com.lineage.server.serverpackets.S_ShopSellListCn;

public class Npc_Scwaty extends NpcExecutor {

	boolean aaa = false;

	private Npc_Scwaty() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Scwaty();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		if (pc.isDarkelf()) { // 黑妖
			aaa = true;
		} else {
			aaa = false;
		}

		if (aaa == true) {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "scwaty1"));
		} else {
		 	pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "scwaty2"));
		}
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("buy") && pc.isDarkelf()) {
			pc.sendPackets(new S_ShopSellList(npc.getId()));

		} else if (cmd.equalsIgnoreCase("sell") && pc.isDarkelf()) {
			pc.sendPackets(new S_ShopBuyList(npc.getId(), pc));
		}
		
		
		
		if (cmd.equalsIgnoreCase("sell")) {
			pc.sendPackets(new S_ShopBuyListAll(pc, npc));
		}
	}
}
