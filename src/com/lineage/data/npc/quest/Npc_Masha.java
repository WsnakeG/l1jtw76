package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.data.quest.ElfLv30_1;
import com.lineage.data.quest.ElfLv45_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_PacketBox;

/**
 * 馬沙<BR>
 * 70653<BR>
 * 說明:王族的信念 (王族45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Masha extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Masha.class);

	private Npc_Masha() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Masha();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// LV30任務未完成
				if (!pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
					return;
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
					// 王族徽章片塊代表著你君主的資格
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha4"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(CrownLv45_1.QUEST.get_id())) {
						// 我從艾莉亞那裡已經聽到了有關你在風木村的功蹟
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha1"));

					} else {// 任務已經開始
						// 遞給王族徽章片塊
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha3"));
					}

				} else {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
				}

			} else if (pc.isKnight()) {// 騎士
				// LV30任務未完成
				if (!pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
					return;
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
					// 謝謝你為了亞丁所做協助。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashak3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv45_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(KnightLv45_1.QUEST.get_id())) {
						// 我己經透過傑瑞德得知你的消息。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashak1"));

					} else {// 任務已經開始
						// 給予尋找調查員的結果
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashak2"));
					}

				} else {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
				}

			} else if (pc.isElf()) {// 精靈
				// LV30任務未完成
				if (!pc.getQuest().isEnd(ElfLv30_1.QUEST.get_id())) {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
					return;
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
					// 謝謝你為了亞丁所做協助。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashae3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv45_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(ElfLv45_1.QUEST.get_id())) {
						// 我已收到森林之母送來的消息正等待著你的到來。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashae1"));

					} else {// 任務已經開始
						// 給予尋找調查員的結果
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashae2"));
					}

				} else {
					// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
				}

			} else if (pc.isWizard()) {// 法師
				// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));

			} else {
				// 你好，我是迪嘉勒廷公爵的侍從長 馬沙。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isCrown()) {// 王族
			// LV45任務已經完成
			if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 15 masha2")) {// 接受馬沙的試練
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, CrownLv45_1.QUEST.get_id());
				// 很久以前亞丁王國裡有個只傳給王族的王族徽章。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha2"));

			} else if (cmd.equalsIgnoreCase("request ring of guardian")) {// 遞給王族徽章片塊
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, new int[] { 40586, 40587 }, // 王族徽章的碎片A(背叛的妖魔隊長)
																				// x
																				// 1
																				// 王族徽章的碎片B
																				// x
																				// 1
						new int[] { 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40586, 40587 }, // 王族徽章的碎片A
																				// x
																				// 1
																				// 王族徽章的碎片B
																				// x
																				// 1
							new int[] { 1, 1 }, new int[] { 20287 }, // 守護者的戒指 x
																		// 1
							1, new int[] { 1 });// 給予

					// 將任務設置為結束
					QuestClass.get().endQuest(pc, CrownLv45_1.QUEST.get_id());

					/** 授予一般君主權限 或者 聯盟王權限 added by terry0412 */
					// LV45任務已經完成
					if (pc.getClanid() > 0) {
						final L1Clan clan = ClanReading.get().getTemplate(pc.getClanid());
						if ((clan != null) && (clan.getLeaderId() == pc.getId())) { // 為血盟領導人
							// 聯盟君主
							pc.setClanRank(L1Clan.CLAN_RANK_PRINCE);

							// 你的階級變更為%s
							pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, pc.getClanRank(),
									pc.getName()));

							try {
								// 資料存檔
								pc.save();

							} catch (final Exception e) {
								_log.error(e.getLocalizedMessage(), e);
							}
						}
					}

					// 王族徽章片塊代表著你君主的資格
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "masha4"));
				}
			}

		} else if (pc.isKnight()) {// 騎士
			// LV45任務已經完成
			if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 20 mashak2")) {// 關於邪惡勢力
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, KnightLv45_1.QUEST.get_id());
				// 給予尋找調查員的結果
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashak2"));

			} else if (cmd.equalsIgnoreCase("request belt of bravery")) {// 給予尋找調查員的結果
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, new int[] { 40597, 40593 }, // 破損的調查簿、調查簿的缺頁(夜之視野
																				// 可有可無)
						new int[] { 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40597, 40593 }, // 破損的調查簿、調查簿的缺頁(夜之視野
																				// 可有可無)
							new int[] { 1, 1 }, new int[] { 20318 }, // 勇敢皮帶 x 1
							1, new int[] { 1 });// 給予

					// 夜之視野(夜之視野 可有可無)
					final L1ItemInstance item = pc.getInventory().findItemId(20026);
					if (item != null) {
						if (item.isEquipped()) {
							// 脫除裝備
							pc.getInventory().setEquipped(item, false);
						}
						pc.getInventory().removeItem(item, 1);// 刪除道具
					}

					// 將任務設置為結束
					QuestClass.get().endQuest(pc, KnightLv45_1.QUEST.get_id());
					// 謝謝你為了亞丁所做協助。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashak3"));
				}
			}

		} else if (pc.isElf()) {// 精靈
			// LV45任務已經完成
			if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 14 mashae2")) {// 關於邪惡勢力
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, ElfLv45_1.QUEST.get_id());
				// 給予尋找調查員的結果
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashae2"));

			} else if (cmd.equalsIgnoreCase("request bag of masha")) {// 給予調查的結果
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, new int[] { 40533, 192 }, // 古代鑰匙、水之豎琴
						new int[] { 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40533, 192 }, // 古代鑰匙、水之豎琴
							new int[] { 1, 1 }, new int[] { 40546 }, // 馬沙之袋
							1, new int[] { 1 });// 給予

					// 神秘貝殼(神秘貝殼 可有可無)
					final L1ItemInstance item = pc.getInventory().findItemId(40566);
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具
					}

					// 將任務設置為結束
					QuestClass.get().endQuest(pc, ElfLv45_1.QUEST.get_id());
					// 謝謝你為了亞丁所做協助。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mashae3"));
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
