package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv30_1;
import com.lineage.data.quest.WizardLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 塔拉斯<BR>
 * 70763<BR>
 * 說明:不死族的叛徒 (法師30級以上官方任務)<BR>
 * 說明:法師的考驗 (法師45級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_Talass extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Talass.class);

	private Npc_Talass() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Talass();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else if (pc.isKnight()) {// 騎士
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else if (pc.isElf()) {// 精靈
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else if (pc.isWizard()) {// 法師
				// LV45任務已經完成
				if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
					// 你為了找尋什麼而來到這裡呢？冒險者！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv45_1.QUEST.get_questlevel()) {
						// 任務尚未開始
						if (!pc.getQuest().isStart(WizardLv45_1.QUEST.get_id())) {
							// 關於神奇的生命體
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq1"));

						} else {// 任務已經開始
							// 給予調查的結果
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq2"));
						}

					} else {
						// 你為了找尋什麼而來到這裡呢？冒險者！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
					}
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= WizardLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(WizardLv30_1.QUEST.get_id())) {
					case 4:// 達到4(關於不死族的骨頭碎片)
							// 關於不死族的骨頭碎片
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassE1"));
						break;

					default:
						// 你為了找尋什麼而來到這裡呢？冒險者！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
						break;
					}

				} else {
					// 你為了找尋什麼而來到這裡呢？冒險者！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));

			} else {
				// 你為了找尋什麼而來到這裡呢？冒險者！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talass"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isWizard()) {// 法師
			if (cmd.equalsIgnoreCase("quest 16 talassE2")) {// 關於不死族的骨頭碎片
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					return;
				}
				// 遞給不死族的骨頭碎片
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassE2"));

			} else if (cmd.equalsIgnoreCase("request crystal staff")) {// 遞給不死族的骨頭碎片
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					return;
				}
				final int[] items = new int[] { 40580, 40569 }; // 不死族的骨頭碎片 x 1
																// 神秘魔杖 x
				// 1
				final int[] counts = new int[] { 1, 1 };
				final int[] gitems = new int[] { 115 };// 水晶魔杖 x 1
				final int[] gcounts = new int[] { 1 };

				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, items, counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, items, // 不死族的骨頭碎片 x 1 神秘魔杖
															// x 1
							counts, gitems, // 水晶魔杖 x 1
							1, gcounts);// 給予

					// 結束任務
					pc.getQuest().set_end(WizardLv30_1.QUEST.get_id());
					// 關閉對話窗
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("quest 18 talassmq2")) {// 關於神奇的生命體
				// LV45任務已經完成
				if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
					return;
				}
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, WizardLv45_1.QUEST.get_id());
				// 給予調查的結果
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "talassmq2"));

			} else if (cmd.equalsIgnoreCase("request magic bag of talass")) {// 給予調查的結果
				// LV45任務已經完成
				if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
					return;
				}
				final int[] items = new int[] { 40536 }; // 古代惡魔的記載 x 1
				final int[] counts = new int[] { 1 };
				final int[] gitems = new int[] { 40599 };// 塔拉斯的魔法袋 x 1
				final int[] gcounts = new int[] { 1 };

				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, items, counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, items, // 古代惡魔的記載 x 1
							counts, gitems, // 塔拉斯的魔法袋 x 1
							1, gcounts);// 給予

					// 結束任務
					pc.getQuest().set_end(WizardLv45_1.QUEST.get_id());
					// 關閉對話窗
					isCloseList = true;
				}
			}
		}

		if (cmd.equalsIgnoreCase("request bow of sayha")) {// 製造沙哈之弓
			final int[] items = new int[] { 40491, 40498, 40394 };
			final int[] counts = new int[] { 30, 50, 15 };
			final int[] gitems = new int[] { 181 };
			final int[] gcounts = new int[] { 1 };
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				isCloseList = true;

			} else {// 需要物件充足
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				isCloseList = true;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
