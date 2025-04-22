package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 巴休<BR>
 * 70964<BR>
 * 說明:幫助羅伊逃脫(等級40以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Basch extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Basch.class);

	private Npc_Basch() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Basch();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 喂！你也是派遣來的傭兵嗎？
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "basch"));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
