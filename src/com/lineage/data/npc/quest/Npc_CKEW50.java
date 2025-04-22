package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.world.WorldQuest;

/**
 * 往聖殿的入口<BR>
 * 80135<BR>
 * <BR>
 * 80136<BR>
 * <BR>
 * 80134<BR>
 * <BR>
 * 80133<BR>
 * <BR>
 * 說明:不死魔族再生的秘密 (王族,騎士,妖精,法師50級以上官方任務-50級後半段)
 * 
 * @author dexc
 */
public class Npc_CKEW50 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_CKEW50.class);

	private Npc_CKEW50() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_CKEW50();
	}

	@Override
	public int type() {
		return 3;
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
				return;
			}
			if (pc.isCrown()) {// 王族
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80135)) {
					// 周圍繞著暗紅不吉利的光
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_p"));

				} else {
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
				}

			} else if (pc.isKnight()) {// 騎士
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80136)) {
					// 周圍繞著暗紅不吉利的光
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_k"));

				} else {
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
				}

			} else if (pc.isElf()) {// 精靈
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80133)) {
					// 周圍繞著暗紅不吉利的光
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_e"));

				} else {
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
				}

			} else if (pc.isWizard()) {// 法師
				// 任務開始
				if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80134)) {
					// 周圍繞著暗紅不吉利的光
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "50quest_w"));

				} else {
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
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

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {

		boolean isCloseList = false;
		if (pc.isCrown()) {// 王族
			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80135)) {
				if (cmd.equalsIgnoreCase("ent")) {// 手去觸摸再進場
					staraQuest(pc);
				}
			}
			isCloseList = true;

		} else if (pc.isKnight()) {// 騎士
			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80136)) {
				if (cmd.equalsIgnoreCase("ent")) {// 手去觸摸再進場
					staraQuest(pc);
				}
			}
			isCloseList = true;

		} else if (pc.isElf()) {// 精靈
			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80133)) {
				if (cmd.equalsIgnoreCase("ent")) {// 手去觸摸再進場
					staraQuest(pc);
				}
			}
			isCloseList = true;

		} else if (pc.isWizard()) {// 法師
			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id()) && (npc.getNpcId() == 80134)) {
				if (cmd.equalsIgnoreCase("ent")) {// 手去觸摸再進場
					staraQuest(pc);
				}
			}
			isCloseList = true;

		} else if (pc.isDarkelf()) {// 黑暗精靈
			isCloseList = true;
		} else if (pc.isDragonKnight()) {// 龍騎士
			isCloseList = true;
		} else if (pc.isIllusionist()) {// 幻術師
			isCloseList = true;
		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}

	/**
	 * 進入副本執行任務
	 * 
	 * @param pc
	 * @return
	 */
	private void staraQuest(final L1PcInstance pc) {
		try {
			if (!pc.isInParty()) {// 未加入隊伍
				return;
			}
			// 任務編號
			final int questid = CKEWLv50_1.QUEST.get_id();

			// 任務地圖編號
			final int mapid = CKEWLv50_1.MAPID;

			// 任務副本編號
			int showId = -1;

			// 進入人數限制
			int users = QuestMapTable.get().getTemplate(mapid);
			if (users == -1) {// 無限制
				users = Byte.MAX_VALUE;// 設置為127
			}

			int i = 0;
			// 隊伍成員
			for (final L1PcInstance otherPc : pc.getParty().getMemberList()) {
				if (otherPc.get_showId() != -1) {
					showId = otherPc.get_showId();
				}
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
				return;
			}

			// 取回新的任務副本編號
			if (showId == -1) {
				showId = WorldQuest.get().nextId();
			}

			// 加入副本執行成員
			final L1QuestUser quest = WorldQuest.get().put(showId, mapid, questid, pc);

			if (quest == null) {
				_log.error("副本設置過程發生異常!!");
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}

			quest.set_info(false);// 不公告NPC死亡
			quest.set_outStop(true);// 該副本參加者其中之一離開 立即結束

			// 取回進入時間限制
			final Integer time = QuestMapTable.get().getTime(mapid);
			if (time != null) {
				quest.set_time(time.intValue());
			}

			if (pc.isCrown()) {// 王族
				// 傳送任務執行者
				L1Teleport.teleport(pc, 32720, 32900, (short) mapid, 2, true);

			} else if (pc.isKnight()) {// 騎士
				// 傳送任務執行者
				L1Teleport.teleport(pc, 32721, 32853, (short) mapid, 3, true);

			} else if (pc.isElf()) {// 精靈
				// 傳送任務執行者
				L1Teleport.teleport(pc, 32725, 32940, (short) mapid, 1, true);

			} else if (pc.isWizard()) {// 法師
				// 傳送任務執行者
				L1Teleport.teleport(pc, 32810, 32941, (short) mapid, 7, true);
			}

			// 設置副本參加編號(已經在WorldQuest加入編號)
			// pc.set_showId(showId);

			// 將任務進度提升為2
			pc.getQuest().set_step(CKEWLv50_1.QUEST.get_id(), 2);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
