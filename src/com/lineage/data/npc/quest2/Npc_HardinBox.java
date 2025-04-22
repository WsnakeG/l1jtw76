package com.lineage.data.npc.quest2;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter02;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * @author terry0412
 */
public class Npc_HardinBox extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_HardinBox.class);

	private Npc_HardinBox() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_HardinBox();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			if (npc.get_quest_id() > 0) {
				final ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
				if (!targetList.isEmpty()) {
					for (final L1Character cha : targetList) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							if (pc.getMapId() == Chapter02.MAPID) {
								CreateNewItem.createNewItem(pc, npc.get_quest_id(), 1);
							}
						}
					}
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
