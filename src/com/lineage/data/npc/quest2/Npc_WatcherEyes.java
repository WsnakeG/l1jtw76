package com.lineage.data.npc.quest2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * @author terry0412
 */
public class Npc_WatcherEyes extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_WatcherEyes.class);

	private Npc_WatcherEyes() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_WatcherEyes();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 顯示對話
			if (pc.isGhost()) {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "exitghostel"));

			} else {
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "exitghostel1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
