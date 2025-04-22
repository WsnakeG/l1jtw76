package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 甘特<BR>
 * 70522<BR>
 * 王族的自知 (王族15級以上官方任務)<BR>
 * 拯救被幽禁的吉姆 (騎士30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Qunter extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Qunter.class);

	private Npc_Qunter() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Qunter();
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
					// 你終於成功了！你現在應該已經了解到生命的可貴吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp11"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv15_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 關於策略家傑羅
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp9"));
						break;

					case 1:// 達到1(任務開始)
							// 關於策略家傑羅
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp9"));
						break;

					case 2:// 達到2
							// 請求『甘特的測試
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp1"));
						break;

					case 3:// 達到3
							// 學習精準目標魔法
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterpev1"));
						break;
					}

				} else {
					// 難道你自己沒有感覺以你目前的經驗根本就不成氣候嗎！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp12"));
				}

			} else if (pc.isKnight()) {// 騎士
				if (pc.getLawful() < 0) {// 邪惡
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterk12"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 哈哈哈，你終於通過了成為紅騎士所需要的所有過程。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkEcg"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
					case 1:// 達到1(任務開始)
							// <P align=justify>眼神看來很不尋常喔...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterk9"));
						break;

					case 2:// 達到2(交談吉姆)
							// 接受甘特的試練
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkE1"));
						break;

					case 3:// 達到3(接受試練)
							// 遞給楊果里恩的爪子
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkE2"));
						break;

					default:// 其他
						// 恭喜你～你終於成為有資格帶著武器保衛世界並負起責任的人啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkE3"));
						break;
					}

				} else {
					// <P align=justify>眼神看來很不尋常喔...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterk9"));
				}

			} else if (pc.isElf()) {// 精靈
				// 哦~<a link="guntere2">森林深處</a>的靈魂啊，
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "guntere1"));

			} else if (pc.isWizard()) {// 法師
				// 用人類的方法</a>實現<a link="gunterw3">神的旨意</a>的人啊
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterw1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 存在於光與黑的警戒點，
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterdw1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 歡迎你呀～擁有龍之力量的外地人！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterdwk1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 歡迎擁有古老知識以及神之力量的你們來到這裡
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunteriw1"));

			} else {
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				// pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isCrown()) {// 王族
			if (cmd.equalsIgnoreCase("guntertest")) {// 請求『甘特的測試』
				// 任務未完成
				if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					// 任務進度
					switch (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 關於策略家傑羅
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp9"));
						break;

					case 1:// 達到1(任務開始)
							// 關於策略家傑羅
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp9"));
						break;

					case 2:
						// 提升任務進度
						pc.getQuest().set_step(CrownLv15_1.QUEST.get_id(), 3);
						// 學習精準目標魔法
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterpev1"));
						break;
					}
				}

			} else if (cmd.equalsIgnoreCase("request spellbook112")) {// 學習精準目標魔法
				// 任務進度達到3
				if (pc.getQuest().get_step(CrownLv15_1.QUEST.get_id()) == 3) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40564, // 任務完成需要物件(生命的卷軸
																// x 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40564, 1, // 需要生命的卷軸 x 1
								40226, 1);// 給予魔法書(精準目標) x 1
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CrownLv15_1.QUEST.get_id());
						isCloseList = true;
					}

				} else {
					// 關於策略家傑羅
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterp9"));
				}
			}

		} else if (pc.isKnight()) {// 騎士
			if (cmd.equalsIgnoreCase("quest 14 gunterkE2")) {// 接受甘特的試練
				// 提升任務進度
				pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 3);
				// 遞給楊果里恩的爪子
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkE2"));

			} else if (cmd.equalsIgnoreCase("request sword of red knights")) {// 遞給楊果里恩的爪子
				// 任務進度達到3
				if (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id()) == 3) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40590, // 任務完成需要物件(楊果里恩之爪
																// x 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40590, 1, // 需要楊果里恩之爪 x
																	// 1
								30, 1);// 紅騎士之劍 x 1
						// 提升任務進度
						pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 4);
						// 恭喜你～你終於成為有資格帶著武器保衛世界並負起責任的人啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gunterkE3"));
					}

				} else {
					isCloseList = true;
				}
			}

		} else if (pc.isElf()) {// 精靈
			if (cmd.equalsIgnoreCase("guntertest")) {// 請求『甘特的測試』
				// 你的教師不是那些『永恆的樹』與『永久的大地』嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "guntereev1"));
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
