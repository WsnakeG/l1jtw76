package com.lineage.data.npc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 亞希兒<BR>
 * 85029<BR>
 * 
 * @author dexc
 */
public class Npc_Azel extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Azel.class);

	private Npc_Azel() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Azel();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 我是負責護衛長老希蓮恩的亞希兒。
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "azel1"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
