package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv15_2;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 康<BR>
 * 70885<BR>
 * 說明:妖魔的侵入 (黑暗妖精15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Kanguard extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Kanguard.class);

	private Npc_Kanguard() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Kanguard();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else if (pc.isKnight()) {// 騎士
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else if (pc.isElf()) {// 精靈
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else if (pc.isWizard()) {// 法師
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv15_2.QUEST.get_id())) {
					// 我現在覺得你有些事情處理得不錯
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv15_2.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(DarkElfLv15_2.QUEST.get_id())) {
						// 接受簡單的任務
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard1"));

					} else {
						// 遞給妖魔長老首級
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard2"));
					}

				} else {
					// 我現在非常地忙，所以我沒空跟你閒扯蛋。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard5"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));

			} else {
				// 只有黑暗妖精才能住在這裡！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isDarkelf()) {// 黑暗精靈
			// 等級達成要求
			if (pc.getLevel() >= DarkElfLv15_2.QUEST.get_questlevel()) {
				// 任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv15_2.QUEST.get_id())) {
					return;
				}
				if (cmd.equalsIgnoreCase("quest 11 kanguard2")) {// 接受簡單的任務
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, DarkElfLv15_2.QUEST.get_id());
					// 遞給妖魔長老首級
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard2"));

				} else if (cmd.equalsIgnoreCase("request kanbag")) {// 遞給妖魔長老首級
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40585, // 任務完成需要物件(妖魔長老首級
																// x 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40585, 1, // 需要妖魔長老首級 x
																	// 1
								40598, 1);// 給予康之袋 x 1
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv15_2.QUEST.get_id());
						// 我現在覺得你有些事情處理得不錯
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kanguard3"));
					}
				}
			}

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
