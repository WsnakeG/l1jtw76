package com.lineage.data.npc.quest;

import com.lineage.data.event.SoulQueen.L1SoulTower;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 封印空間-開始屍魂塔
 * 
 * @author XXX
 */
public class Npc_SoulTower extends NpcExecutor {

	private Npc_SoulTower() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_SoulTower();
	}

	@Override
	public int type() {
		return 3;
	}

	// ch_sihonez1
	// <html>
	// <body>
	// <font fg=ffffff><p align=left>屍魂之塔傳送：</p></font>
	// <br>
	// <br>
	// <a action="enter">進入屍魂之塔</a><br>
	// <br>
	// </body>
	// </html>

	@Override
	public void talk(L1PcInstance pc, L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ch_sihonez1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("enter")) {
			L1SoulTower.get().soulTowerStart(pc);
		}
	}

}
