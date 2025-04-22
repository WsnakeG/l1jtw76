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
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.WorldQuest;

/**
 * @author terry0412
 */
public class Npc_OrimMonster extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_OrimMonster.class);

	private Npc_OrimMonster() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_OrimMonster();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);
			if ((pc != null) && (pc.getMapId() == Chapter02.MAPID)) {
				final L1QuestUser quest = WorldQuest.get().get(npc.get_showId());
				if ((quest != null) && (quest.get_orimR() != null)) {
					quest.add_score(npc.getNpcTemplate().get_quest_score());
				}
				if (npc.get_quest_id() > 0) {
					final ArrayList<L1Character> targetList = npc.getHateList().toTargetArrayList();
					if (!targetList.isEmpty()) {
						for (final L1Character cha : targetList) {
							if (cha instanceof L1PcInstance) {
								final L1PcInstance find_pc = (L1PcInstance) cha;
								CreateNewItem.createNewItem(find_pc, npc.get_quest_id(), 1);
							}
						}
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
