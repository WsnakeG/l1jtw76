package com.lineage.data.npc.quest2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.Chapter02;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldQuest;

/**
 * @author terry0412
 */
public class Npc_Mimic extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Mimic.class);

	private Npc_Mimic() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Mimic();
	}

	@Override
	public int type() {
		return 12;
	}

	@Override
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			//
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if ((pc != null) && (npc.get_quest_id() > 0)) {
				final L1QuestUser quest = WorldQuest.get().get(npc.get_showId());
				if ((quest != null) && (quest.get_mapid() == Chapter02.MAPID)) {
					if (quest.get_orimR() != null) {
						CreateNewItem.createNewItem(pc, 56253, 1);
						quest.get_orimR().checkQuestOrder(pc, npc.get_quest_id());
					}
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
