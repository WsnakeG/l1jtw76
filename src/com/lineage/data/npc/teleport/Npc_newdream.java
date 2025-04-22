package com.lineage.data.npc.teleport;

import java.util.HashMap;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1TeleportLoc;

/**
 * 新夢幻之島<BR>
 * 50045
 * 
 * @author dexc
 */
public class Npc_newdream extends NpcExecutor {

	/**
	 *
	 */
	private Npc_newdream() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_newdream();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "edlen1"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		if (cmd.matches("[0-9]+")) {// 傳送-數字選項
			final String pagecmd = pc.get_other().get_page() + cmd;
			Npc_Teleport.teleport(pc, npc, Integer.valueOf(pagecmd));
		} else {// 傳送-國度
			final HashMap<Integer, L1TeleportLoc> teleportMap = NpcTeleportTable
					.get().get_teles(cmd);
			if (teleportMap != null) {
				if (teleportMap.size() <= 0) {
					// 1,447：目前暫不開放。
					pc.sendPackets(new S_ServerMessage(1447));
					return;
				}
				pc.get_otherList().teleport(teleportMap);
				Npc_Teleport.showPage(pc, npc, 0);

			} else {
				// 1,447：目前暫不開放。
				pc.sendPackets(new S_ServerMessage(1447));
			}
		}
	}
}
