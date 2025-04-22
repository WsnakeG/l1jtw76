package com.lineage.data.npc.shop;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.TimeLimit.L1TimeLimit;
import com.lineage.server.model.TimeLimit.S_TimeLimitBuyList;
import com.lineage.server.model.TimeLimit.ShopTimeLimitTable;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 限時商人
 * 類名稱：Npc_TimeLimit<br>
 * 修改備註:<br>
 * @version 2.7c<br>
 */
public class Npc_TimeLimit extends NpcExecutor {

    /**
     *
     */
    private Npc_TimeLimit() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_TimeLimit();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_shop2"));
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a")) {
			int npcId = npc.getNpcId();
			L1TimeLimit shop = ShopTimeLimitTable.getInstance().get(npcId);
			if (shop != null) {
				pc.sendPackets(new S_TimeLimitBuyList(npc.getId(), pc));
			}
		}/* else if (cmd.equalsIgnoreCase("b")) {
			int npcId = npc.getNpcId();
			L1TimeLimit shop = ShopTimeLimitTable.getInstance().get(npcId);
			if (shop != null) {
				pc.sendPackets(new S_CenterSellList(npc.getId(), pc));
			}
		}*/
    }
}
