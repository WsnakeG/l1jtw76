package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_2;
import com.lineage.data.quest.ElfLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 迷幻森林之母<BR>
 * 70844<BR>
 * 說明:達克馬勒的威脅 (妖精30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Mother extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Mother.class);

	private Npc_Mother() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Mother();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else if (pc.isKnight()) {// 騎士
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else if (pc.isElf()) {// 精靈
				// LV15任務未完成
				if (!pc.getQuest().isEnd(ElfLv15_2.QUEST.get_id())) {
					// 歡迎 世界樹的小葉子
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mothere1"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(ElfLv30_1.QUEST.get_id())) {
					// 世界樹的幼小葉子啊，你承受了這個試練。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherEE3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv30_1.QUEST.get_id())) {
					case 0:// 達到0(任務未開始)
							// 你已經能夠接受成人儀式了，真高興啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherEE1"));
						break;

					default:// 其他
						// 遞給受詛咒的精靈書
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherEE2"));
						break;
					}
				} else {
					// 歡迎 世界樹的小葉子
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mothere1"));
				}

			} else if (pc.isWizard()) {// 法師
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));

			} else {
				// 在這裡看見人類是一件罕見的事情
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherm1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isElf()) {// 精靈
			// 任務已經完成
			if (pc.getQuest().isEnd(ElfLv30_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 12 motherEE2")) {// 進行成人儀式
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, ElfLv30_1.QUEST.get_id());
				// 遞給受詛咒的精靈書
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherEE2"));

			} else if (cmd.equalsIgnoreCase("request questitem2")) {// 遞給受詛咒的精靈書
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, new int[] { 40592 }, // 受詛咒的精靈書
																		// x 1
						new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40592 }, // 受詛咒的精靈書
																			// x
																			// 1
							new int[] { 1 }, new int[] { 40588 }, // 妖精族寶物 x 1
							1, new int[] { 1 });// 給予

					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ElfLv30_1.QUEST.get_id());
					// 世界樹的幼小葉子啊，你承受了這個試練。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "motherEE3"));
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
