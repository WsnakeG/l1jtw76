package com.lineage.data.npc.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.william.BossRoom;

/**
 * BOSS館
 * 
 * @author XXX
 */
public class Npc_BossRoom extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_BossRoom.class);

	private Npc_BossRoom() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_BossRoom();
	}

	@Override
	public int type() {
		return 2;
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		try {
			// 160619 新增限制等級低於70以上不能參加活動
			if (pc.getLevel() < 70) {
				pc.sendPackets(new S_ServerMessage("\\aG等級不夠，您無法參加這麼危險的活動喔！"));
				return;
			}
			if (cmd.equalsIgnoreCase("enterboosroom")) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), BossRoom.getInstance().enterBossRoom(pc)));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
