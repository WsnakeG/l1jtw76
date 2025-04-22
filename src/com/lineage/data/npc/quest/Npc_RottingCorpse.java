package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 被遺棄的肉身<BR>
 * 80137<BR>
 * 說明:不死魔族再生的秘密 (王族,騎士,妖精,法師50級以上官方任務-50級後半段)
 * 
 * @author dexc
 */
public class Npc_RottingCorpse extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_RottingCorpse.class);

	private Npc_RottingCorpse() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_RottingCorpse();
	}

	@Override
	public int type() {
		return 1;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (!pc.isInParty()) {// 未加入隊伍
				return;
			}

			int i = 0;
			// 隊伍成員
			for (final L1PcInstance otherPc : pc.getParty().getMemberList()) {
				if (otherPc.isCrown()) {// 王族
					i += 1;
				} else if (otherPc.isKnight()) {// 騎士
					i += 2;
				} else if (otherPc.isElf()) {// 精靈
					i += 4;
				} else if (otherPc.isWizard()) {// 法師
					i += 8;
				}
			}
			if (i != CKEWLv50_1.USER) {// 人數異常
				final L1QuestUser quest = WorldQuest.get().get(pc.get_showId());
				quest.endQuest();
				return;
			}
			if (pc.isCrown()) {// 王族
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
					// 取得任務道具
					if (!pc.getInventory().checkItem(49239)) {
						CreateNewItem.getQuestItem(pc, npc, 49239, 1);// 消滅之意志
					}
				}

			} else if (pc.isKnight()) {// 騎士
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
					// 取得任務道具
					if (!pc.getInventory().checkItem(49239)) {
						CreateNewItem.getQuestItem(pc, npc, 49239, 1);// 消滅之意志
					}
				}

			} else if (pc.isElf()) {// 精靈
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
					// 取得任務道具
					if (!pc.getInventory().checkItem(49239)) {
						CreateNewItem.getQuestItem(pc, npc, 49239, 1);// 消滅之意志
					}
				}

			} else if (pc.isWizard()) {// 法師
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
					// 取得任務道具
					if (!pc.getInventory().checkItem(49239)) {
						CreateNewItem.getQuestItem(pc, npc, 49239, 1);// 消滅之意志
					}
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈

			} else if (pc.isDragonKnight()) {// 龍騎士

			} else if (pc.isIllusionist()) {// 幻術師

			} else {
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
