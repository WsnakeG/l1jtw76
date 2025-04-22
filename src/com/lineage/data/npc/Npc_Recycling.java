package com.lineage.data.npc;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ShopBuyListAll;

/**
 * 亞丁商團<BR>
 * 99999
 * 
 * @author dexc
 */
public class Npc_Recycling extends NpcExecutor {

	/**
	 *
	 */
	private Npc_Recycling() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Recycling();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		
		final int diffLocX = Math.abs(pc.getX() - npc.getX());
		final int diffLocY = Math.abs(pc.getY() - npc.getY());
		// 距離3格以上無效 20240926
		if ((diffLocX > 5) || (diffLocY > 5)) return;
		else pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "tzmerchant"));
		
		
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("sell")) {
			pc.sendPackets(new S_ShopBuyListAll(pc, npc));
		}
	}
}
