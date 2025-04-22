package com.lineage.data.npc.quest2;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.templates.L1Rank;

/**
 * @author terry0412
 */
public class Npc_OrimBoard extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_OrimBoard.class);

	private Npc_OrimBoard() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_OrimBoard();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "id_s"));
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("query")) {
			final List<L1Rank> totalList = BoardOrimReading.get().getTotalList();
			final List<L1Rank> tempList = new CopyOnWriteArrayList<L1Rank>();
			int totalSize = 0;
			for (int i = 0, r = 5, n = totalList.size(); (i < r) && (i < n); i++) {
				final L1Rank rank = totalList.get(i);
				if (rank != null) {
					tempList.add(rank);
					totalSize += rank.getMemberSize();
				}
			}
			pc.sendPackets(new S_PacketBoxGree(tempList, totalSize, 0, 0));
		}
	}
}
