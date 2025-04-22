package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 皮爾斯<BR>
 * 70908<BR>
 * 說明:皮爾斯的憂鬱 (黑暗妖精15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Pears extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Pears.class);

	private Npc_Pears() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Pears();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族

			} else if (pc.isKnight()) {// 騎士

			} else if (pc.isElf()) {// 精靈

			} else if (pc.isWizard()) {// 法師

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv15_1.QUEST.get_id())) {
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv15_1.QUEST.get_questlevel()) {
					// 啊......為何服侍神的女性都如此高貴呢！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pears1"));
				}

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

		if (pc.isDarkelf()) {// 黑暗精靈
			// 等級達成要求
			if (pc.getLevel() >= DarkElfLv15_1.QUEST.get_questlevel()) {
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv15_1.QUEST.get_id())) {
					return;
				}
				if (cmd.equalsIgnoreCase("request silver sting knife")) {// 給予二級黑魔石
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40321, // 任務完成需要物件(二級黑魔石)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40321, 1, // 需要二級黑魔石 x 1
								40738, 1000);// 給予銀飛刀 x 1000
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 二級黑魔石 → 銀飛刀(1,000)
						isCloseList = true;
					}

				} else if (cmd.equalsIgnoreCase("request heavy sting knife")) {// 給予三級黑魔石
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40322, // 任務完成需要物件(三級黑魔石)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40322, 1, // 需要三級黑魔石 x 1
								40740, 2000);// 給予重飛刀 x 2000
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 三級黑魔石 → 重飛刀(2,000
						isCloseList = true;
					}

				} else if (cmd.equalsIgnoreCase("request pears itembag")) {// 給予四級黑魔石
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40323, // 任務完成需要物件(四級黑魔石)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40323, 1, // 需要四級黑魔石 x 1
								40715, 1);// 給予皮爾斯的禮物 x 1
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 四級黑魔石 → 皮爾斯的禮物
						isCloseList = true;
					}

				} else if (cmd.equalsIgnoreCase("request jin gauntlet")) {// 給予五級黑魔石
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40324, // 任務完成需要物件(五級黑魔石)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40324, 1, // 需要五級黑魔石 x 1
								194, 1);// 真鐵手甲 x 1
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DarkElfLv15_1.QUEST.get_id());
						// 五級黑魔石 → 真鐵手甲
						isCloseList = true;
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
