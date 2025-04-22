package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 傑羅<BR>
 * 70554<BR>
 * 王族的自知 (王族15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Zero extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Zero.class);

	private Npc_Zero() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Zero();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 任務已經完成
				if (pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					// 你也是為了對抗反王之勢力，而來自遠方陸地的人嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero6"));

				} else {
					// 等級達成要求
					if (pc.getLevel() >= CrownLv15_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 傑羅的課題
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero1"));
							// 將任務設置為執行中
							QuestClass.get().startQuest(pc, CrownLv15_1.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
								// 傑羅的課題
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero1"));
							break;

						default:// 達到2以上
							// 有關甘特
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
							break;
						}

					} else {
						// 你也是為了對抗反王之勢力，而來自遠方陸地的人嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero6"));
					}
				}

			} else if (pc.isKnight()) {// 騎士
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else if (pc.isElf()) {// 精靈
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else if (pc.isWizard()) {// 法師
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));

			} else {
				// 是否在尋找可跟隨的王族呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isCrown()) {// 王族
			if (cmd.equalsIgnoreCase("request cloak of red")) {// 給予搜索狀。
				// 任務進度
				switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
				case 0:// 任務尚未開始
					break;

				case 1:// 達到1(任務開始)
						// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40565, // 任務完成需要物件(搜索狀 x
																// 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40565, 1, // 需要搜索狀 x 1
								20065, 1);// 給予紅色斗篷 x 1
						// 提升任務進度
						pc.getQuest().set_step(CrownLv15_1.QUEST.get_id(), 2);
						// 有關甘特
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
					}
					break;

				default:// 達到2以上
					// 有關甘特
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "zero5"));
					break;
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
