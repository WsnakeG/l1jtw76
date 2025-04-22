package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 瑞奇<BR>
 * 70798<BR>
 * 瑞奇的抵抗 (騎士15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Riky extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Riky.class);

	private Npc_Riky() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Riky();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
					// 喔，你終於做到了! 以後應該稱你為紅騎士了。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rikycg"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv15_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv15_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 關於黑騎士的誓約
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky1"));
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, KnightLv15_1.QUEST.get_id());
						break;

					case 1:// 達到1(任務開始)
							// 交給黑騎士的誓約
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky3"));
						break;

					case 2:// 達到2
							// 你現在看起來也有騎士的威嚴了。哈哈哈...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky5"));
						break;

					default:// 其他
						// 喔，你終於做到了! 以後應該稱你為紅騎士了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "rikycg"));
						break;
					}
				} else {
					// 你是...想當騎士的自願者嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky6"));
				}

			} else if (pc.isElf()) {// 精靈
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else if (pc.isWizard()) {// 法師
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));

			} else {
				// 我已經不屬於反王的勢力...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isKnight()) {// 騎士
			// 任務已經完成
			if (pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("request hood of knight")) {// 交給黑騎士的誓約
				// 任務已經開始
				if (pc.getQuest().isStart(KnightLv15_1.QUEST.get_id())) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40608, // 任務完成需要物件(黑騎士的誓約
																// x 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40608, 1, // 需要黑騎士的誓約 x
																	// 1
								20005, 1);// 給予騎士頭巾 x 1
						// 嗯...我記得我之前對我朋友做了一些不好的事情...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "riky4"));
						// 提升任務進度
						pc.getQuest().set_step(KnightLv15_1.QUEST.get_id(), 2);
					}
				} else {
					isCloseList = true;
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
