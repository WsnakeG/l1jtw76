package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv30_1;
import com.lineage.data.quest.DarkElfLv45_1;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 布魯迪卡<BR>
 * 70895<BR>
 * 說明:糾正錯誤的觀念 (黑暗妖精45級以上官方任務)<BR>
 * 說明:尋找黑暗之星 (黑暗妖精50級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_Bluedika extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Bluedika.class);

	private Npc_Bluedika() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Bluedika();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else if (pc.isKnight()) {// 騎士
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else if (pc.isElf()) {// 精靈
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else if (pc.isWizard()) {// 法師
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV30任務未完成
				if (!pc.getQuest().isEnd(DarkElfLv30_1.QUEST.get_id())) {
					// 總之請你好好保重身體。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq3"));
					return;
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
					// LV50任務已經完成
					if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
						// 多虧你及時完成任務，才能阻止黑暗之星變質。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq8"));
						return;
					}
					// 等級達成要求(LV50-1)
					if (pc.getLevel() >= DarkElfLv50_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 我希望你能找回黑暗之星
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq6"));
							break;

						default:// 達到1(任務開始)
							// 你可以去找奇馬，他會幫助你的。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq7"));
							break;
						}

					} else {
						// 今天也很悠閒
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq5"));
					}
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 關於糾正錯誤觀念
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq1"));
						break;

					default:// 達到1(任務開始)
						// 給予死亡之證及刺客之證
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq2"));
						break;
					}

				} else {
					// 你的實力還不足以讓我放心的將事情託付給你，多去鍛練鍛練吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ronde7"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));

			} else {
				// 真是好久沒有見到外人啊。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isDarkelf()) {// 黑暗精靈
			// LV45-1任務已經完成
			if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
				isCloseList = true;
				// LV50任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
					isCloseList = true;

				} else {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
						if (cmd.equalsIgnoreCase("quest 24 bluedikaq7")) {// 執行任務
							// 將任務設置為執行中
							QuestClass.get().startQuest(pc, DarkElfLv50_1.QUEST.get_id());
							// 你可以去找奇馬，他會幫助你的。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq7"));
							isCloseList = false;
						}
						break;

					default:
						if (cmd.equalsIgnoreCase("request finger of death")) {// 遞給黑暗之星
							// 需要物件不足
							if (CreateNewItem.checkNewItem(pc,
									// 真實的面具、蘑菇毒液、黑暗之星
									new int[] { 20037, 40603, 40541 }, new int[] { 1, 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
								isCloseList = true;

							} else {// 需要物件充足
								// 收回任務需要物件 給予任務完成物件
								CreateNewItem.createNewItem(pc,
										// 真實的面具、蘑菇毒液、黑暗之星
										new int[] { 20037, 40603, 40541 }, new int[] { 1, 1, 1 },
										new int[] { 13 }, // 布魯迪卡之袋
															// x
															// 1
										1, new int[] { 1 });// 給予
															// 將任務設置為結束
								QuestClass.get().endQuest(pc, DarkElfLv50_1.QUEST.get_id());
								// 多虧你及時完成任務，才能阻止黑暗之星變質。
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq8"));
								isCloseList = false;
							}
						}
						break;
					}
				}

			} else {
				// LV45-1任務進度
				switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
				case 0:// 達到0
					if (cmd.equalsIgnoreCase("quest 17 bluedikaq2")) {// 關於糾正錯誤觀念
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DarkElfLv45_1.QUEST.get_id());
						// 給予死亡之證及刺客之證
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq2"));
					}
					break;

				default:
					if (cmd.equalsIgnoreCase("request bluedikabag")) {// 給予死亡之證及刺客之證
						// 需要物件不足
						if (CreateNewItem.checkNewItem(pc,
								// 刺客之證,死亡之證
								new int[] { 40572, 40595 }, new int[] { 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
							isCloseList = true;

						} else {// 需要物件充足
							// 收回任務需要物件 給予任務完成物件
							CreateNewItem.createNewItem(pc,
									// 刺客之證,死亡之證
									new int[] { 40572, 40595 }, new int[] { 1, 1 }, new int[] { 40553 }, // 布魯迪卡之袋
																											// x
																											// 1
									1, new int[] { 1 });// 給予
														// 將任務設置為結束
							QuestClass.get().endQuest(pc, DarkElfLv45_1.QUEST.get_id());
							// 總之請你好好保重身體。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluedikaq3"));
						}
					}
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