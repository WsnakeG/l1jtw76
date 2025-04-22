package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1Npc;

/**
 * 任務NPC控制項
 * 
 * @author daien
 */
public class L1QuestInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1QuestInstance.class);

	/**
	 * 任務NPC
	 * 
	 * @param template
	 */
	public L1QuestInstance(final L1Npc template) {
		super(template);
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		final int npcId = getNpcTemplate().get_npcId();
		switch (npcId) {
		// 指定NPC停止移動AI
		case 71075:// 疲憊的蜥蜴人戰士
		case 70957:// 羅伊
		case 81209:// 羅伊-人形殭屍
		case 80012:// 迪嘉勒廷的女間諜
			break;

		default:
			setActived(false);
			startAI();
			break;
		}
	}

	/**
	 * 受到攻擊
	 */
	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			attack.action();
			attack.commit();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 對話
	 */
	@Override
	public void onTalkAction(final L1PcInstance pc) {
		/*
		 * final int pcX = pc.getX(); final int pcY = pc.getY(); final int npcX
		 * = this.getX(); final int npcY = this.getY();
		 */

		final int npcId = getNpcTemplate().get_npcId();

		// 改變面向
		setHeading(targetDirection(pc.getX(), pc.getY()));
		broadcastPacketAll(new S_ChangeHeading(this));

		if (npcId == 71062) { // 卡米特
			if (pc.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 2) {
				pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1b"));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
			}
		} else if (npcId == 71075) { // 疲憊的蜥蜴人戰士
			if (pc.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 1) {
				pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1b"));
			} else {
				pc.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
			}
		}

		// 動作暫停
		set_stop_time(REST_MILLISEC);
		setRest(true);
	}

	/**
	 * NPC對話結果的處理
	 */
	@Override
	public void onFinalAction(final L1PcInstance pc, final String action) {
		if (action.equalsIgnoreCase("start")) {
			final int npcId = getNpcTemplate().get_npcId();
			if ((npcId == 71062)// 卡米特
					&& (pc.getQuest().get_step(L1PcQuest.QUEST_CADMUS) == 2)) {
				final L1Npc l1npc = NpcTable.get().getTemplate(71062);
				new L1FollowerInstance(l1npc, this, pc);
				pc.sendPackets(new S_NPCTalkReturn(getId(), ""));

			} else if ((npcId == 71075)// 疲憊的蜥蜴人戰士
					&& (pc.getQuest().get_step(L1PcQuest.QUEST_LIZARD) == 1)) {
				final L1Npc l1npc = NpcTable.get().getTemplate(71075);
				new L1FollowerInstance(l1npc, this, pc);
				pc.sendPackets(new S_NPCTalkReturn(getId(), ""));
			}
		}
	}
}
