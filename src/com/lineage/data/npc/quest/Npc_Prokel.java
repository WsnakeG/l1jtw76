package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv15_1;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.data.quest.DragonKnightLv45_1;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 長老 普洛凱爾<BR>
 * 85023<BR>
 * 
 * @author dexc
 */
public class Npc_Prokel extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Prokel.class);

	private Npc_Prokel() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Prokel();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else if (pc.isKnight()) {// 騎士
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else if (pc.isElf()) {// 精靈
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else if (pc.isWizard()) {// 法師
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// LV50任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv50_1.QUEST.get_id())) {
					// 你來啦？有你這樣的勇士，我就放心多了。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel32"));
					return;
				}
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					// 等級達成要求(LV50)
					if (pc.getLevel() >= DragonKnightLv50_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(DragonKnightLv50_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 歡迎啊～勇士！
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel21"));
							break;

						case 1:
							// 交出時空裂痕碎片100個。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel24"));
							break;

						case 2:
							if (pc.getInventory().checkItem(49202)) { // 已經具有物品(時空裂痕邪念碎片)
								// <font fg=ffffaf>時空裂痕邪念碎片</font>將會開啟往 <font
								// fg=ffff0>異界</font>次元之門的。
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel33"));

							} else {
								// 在<font fg=ffff0>異界 奎斯特</font>調查得如何啊？有找到<font
								// fg=ffafff>靈魂之火</font>嗎？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel27"));
							}
							break;

						case 3:
							if (pc.getInventory().checkItem(49202)) { // 已經具有物品(時空裂痕邪念碎片)
								// <font fg=ffffaf>時空裂痕邪念碎片</font>將會開啟往 <font
								// fg=ffff0>異界</font>次元之門的。
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel33"));

							} else {
								// 在<font fg=ffff0>異界 奎斯特</font>調查得如何啊？有找到<font
								// fg=ffafff>靈魂之火</font>了嗎？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel30"));
							}
							break;

						case 4:
							if (pc.getInventory().checkItem(49231)) { // 路西爾斯邪念碎片
								// 哇～哇～你手上拿著是什麼呢？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel37"));

							} else {
								// 在<font fg=ffff0>異界 奎斯特</font>調查得如何啊？有找到<font
								// fg=ffafff>靈魂之火</font>嗎？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel25"));
							}
							break;
						}

					} else {
						// 修練做的如何了？過不久會給你新任務的。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel20"));
					}
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) {
					// 等級達成要求(LV45)
					if (pc.getLevel() >= DragonKnightLv45_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(DragonKnightLv45_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 歡迎啊～勇猛的龍騎士！
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel15"));
							break;

						case 1:
						case 2:
						case 3:
							// 幻術士的村莊應該不好找的
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel16"));
							break;

						case 4:
							if (pc.getInventory().checkItem(49224)) { // 幻術士同盟徽印
								// 交出幻術士同盟徽印
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel17"));

							} else {
								// <font fg=ffffaf>幻術士同盟徽印</font>在哪呢？
								pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel19"));
							}
							break;
						}

					} else {
						// 上次教你的<font fg=ffffaf>血之渴望</font>魔法都學會了嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel14"));
					}
					return;
				}
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv15_1.QUEST.get_id())) {
					// 等級達成要求(LV30)
					if (pc.getLevel() >= DragonKnightLv30_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(DragonKnightLv30_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 嗯～感覺與之前狀況有所不同呢，修練得如何？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel8"));
							break;

						case 1:// 達到1(任務開始)
								// 交出妖魔密使首領間諜書
								// 需要普洛凱爾的礦物袋
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel10"));
							break;
						}

					} else {
						// 上次，依你所找到的<font fg=ffffaf>妖魔搜索文件</font>為基礎，正在調查相關事項
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel7"));
					}

				} else {
					// 等級達成要求
					if (pc.getLevel() >= DragonKnightLv15_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(DragonKnightLv15_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 執行普洛凱爾的課題
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel2"));
							break;

						case 1:// 達到1(任務開始)
								// 將妖魔搜索文件交出
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel4"));
							break;
						}

					} else {
						// 真正龍騎士是需擁有親自去解決所有問題的勇氣啊！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel22"));
					}
				}

			} else if (pc.isIllusionist()) {// 幻術師
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));

			} else {
				// 呵呵... 怎麼會跑到這麼遠來了呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isDragonKnight()) {// 龍騎士
			if (cmd.equalsIgnoreCase("a")) {// 執行普洛凱爾的課題 TODO
				// 等級達成要求(LV15任務)
				if (pc.getLevel() >= DragonKnightLv15_1.QUEST.get_questlevel()) {
					// 給予任務道具(普洛凱爾的第1次指令書)
					CreateNewItem.getQuestItem(pc, npc, 49210, 1);
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, DragonKnightLv15_1.QUEST.get_id());
					// 這指令書上寫著你要執行的任務，請快點完成它吧。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel3"));

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("b")) {// 將妖魔搜索文件交出 TODO
				// 等級達成要求(LV15任務)
				if (pc.getLevel() >= DragonKnightLv15_1.QUEST.get_questlevel()) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							new int[] { 49217, // 49217
												// 妖魔搜索文件(妖魔森林)
									49218, // 49218 妖魔搜索文件(古魯丁)
									49219,// 49219 妖魔搜索文件(風木)
							}, new int[] { 1, 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 妖魔搜索文件在哪？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel6"));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								new int[] { 49217, // 49217
													// 妖魔搜索文件(妖魔森林)
										49218, // 49218 妖魔搜索文件(古魯丁)
										49219,// 49219 妖魔搜索文件(風木)
								}, new int[] { 1, 1, 1, }, new int[] { 49102, // 龍騎士書板(龍之護鎧)
																				// x
																				// 1
										275,// 龍騎士雙手劍 x 1
								}, 1, new int[] { 1, 1, });// 給予

						final L1ItemInstance item = pc.getInventory().checkItemX(49210, 1);// 普洛凱爾的第一次指令書
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具
						}

						// 將任務設置為結束
						QuestClass.get().endQuest(pc, DragonKnightLv15_1.QUEST.get_id());
						// 比想像中回來滿快的嘛，應該很隱密進行的吧？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel5"));
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("c")) {// 執行普洛凱爾的第二次課題 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv15_1.QUEST.get_id())) {
					// 等級達成要求(LV30任務)
					if (pc.getLevel() >= DragonKnightLv30_1.QUEST.get_questlevel()) {
						// 給予任務道具(普洛凱爾的第2次指令書)
						CreateNewItem.getQuestItem(pc, npc, 49211, 1);
						// 給予任務道具(普洛凱爾的礦物袋)
						CreateNewItem.getQuestItem(pc, npc, 49215, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DragonKnightLv30_1.QUEST.get_id());
						// 若想取得只有妖魔密使首領所知道的高級情報，需變裝成他們的樣子會比較好。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel9"));

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("d")) {// 交出妖魔密使首領間諜書 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv15_1.QUEST.get_id())) {
					// 等級達成要求(LV30任務)
					if (pc.getLevel() >= DragonKnightLv30_1.QUEST.get_questlevel()) {
						final L1ItemInstance item = pc.getInventory().checkItemX(49221, 1);// 妖魔密使首領間諜書
																							// 49221
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49211, 1);// 普洛凱爾的第二次指令書
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, DragonKnightLv30_1.QUEST.get_id());
							// 哈哈哈！你一定可以達到目標的。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel11"));

							// 給予任務道具(普洛凱爾的第一次信件)
							CreateNewItem.getQuestItem(pc, npc, 49213, 1);
							// 給予任務道具(龍騎士書板(血之渴望))
							CreateNewItem.getQuestItem(pc, npc, 49107, 1);

						} else {
							// 妖魔密使首領間諜書到底在哪呢？ 該不會任務失敗吧？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel12"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("e")) {// 需要普洛凱爾的礦物袋 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv15_1.QUEST.get_id())) {
					// 等級達成要求(LV30任務)
					if (pc.getLevel() >= DragonKnightLv30_1.QUEST.get_questlevel()) {
						if (pc.getInventory().checkItem(49223)) { // 已經具有物品-妖魔密使的徽印
							// 礦物袋子不是已經給過了嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel35"));
							return;
						}
						if (pc.getInventory().checkItem(49215)) { // 已經具有物品
							// 礦物袋子不是已經給過了嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel35"));

						} else {
							// 給予任務道具(普洛凱爾的礦物袋)
							CreateNewItem.getQuestItem(pc, npc, 49215, 1);
							// 什麼？？給你的礦物袋子遺失了？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel13"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("f")) {// 執行普洛凱爾的第三次課題 TODO
				// LV30任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) {
					// 等級達成要求(LV45任務)
					if (pc.getLevel() >= DragonKnightLv45_1.QUEST.get_questlevel()) {
						// 給予任務道具(普洛凱爾的第三次指令書)
						CreateNewItem.getQuestItem(pc, npc, 49212, 1);
						// 給予任務道具(普洛凱爾的信件)
						CreateNewItem.getQuestItem(pc, npc, 49209, 1);
						// 給予任務道具(結盟瞬間移動卷軸)
						CreateNewItem.getQuestItem(pc, npc, 49226, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DragonKnightLv45_1.QUEST.get_id());
						// 幻術士的村莊應該不好找的
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel16"));

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("g")) {// 交出幻術士同盟徽印 TODO
				// LV30任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) {
					// 等級達成要求(LV45任務)
					if (pc.getLevel() >= DragonKnightLv45_1.QUEST.get_questlevel()) {
						final L1ItemInstance item = pc.getInventory().checkItemX(49224, 1);// 幻術士同盟徽印
																							// 49224
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49212, 1);// 普洛凱爾的第三次指令書
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, DragonKnightLv45_1.QUEST.get_id());
							// 辛苦了～果然得以信任，我會持續觀察的
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel18"));

							// 給予任務道具(普洛凱爾的第二次信件)
							CreateNewItem.getQuestItem(pc, npc, 49214, 1);

						} else {
							// <font fg=ffffaf>幻術士同盟徽印</font>在哪呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel19"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("h")) {// 執行普洛凱爾第四課題 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					// 等級達成要求(LV50)
					if (pc.getLevel() >= DragonKnightLv50_1.QUEST.get_questlevel()) {
						// 給予任務道具(普洛凱爾的第四次指令書)
						CreateNewItem.getQuestItem(pc, npc, 49546, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, DragonKnightLv50_1.QUEST.get_id());
						// 真正龍騎士是需擁有親自去解決所有問題的勇氣啊！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel22"));

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("i")) {// 交出時空裂痕碎片100個 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					// 等級達成要求(LV50)
					if (pc.getLevel() >= DragonKnightLv50_1.QUEST.get_questlevel()) {

						final L1ItemInstance item = pc.getInventory().checkItemX(49101, 100);// 時空裂痕碎片49101
						if (item != null) {
							pc.getInventory().removeItem(item, 100);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49546, 1);// 普洛凱爾的第四次指令書
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// <font fg=ffffaf>時空裂痕邪念碎片</font>將會開啟往 <font
							// fg=ffff0>異界</font>次元之門的。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel33"));

							// 提升任務進度
							pc.getQuest().set_step(DragonKnightLv50_1.QUEST.get_id(), 2);
							// 給予任務道具(普洛凱爾的第五次指令書)
							CreateNewItem.getQuestItem(pc, npc, 49547, 1);
							// 給予任務道具(時空裂痕邪念碎片)
							CreateNewItem.getQuestItem(pc, npc, 49202, 1);
							// 給予任務道具(普洛凱爾的護身符)
							CreateNewItem.getQuestItem(pc, npc, 49216, 1);

						} else {
							// <font fg=ffffaf>時間裂痕碎片</font>100個在哪呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel31"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("j")) {// 交出路西爾斯邪念碎片與靈魂之火灰燼 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					// 等級達成要求(LV50)
					if (pc.getLevel() >= DragonKnightLv50_1.QUEST.get_questlevel()) {

						final L1ItemInstance item = pc.getInventory().checkItemX(49231, 1);// 路西爾斯邪念碎片
																							// 49231
						if (item != null) {
							pc.getInventory().removeItem(item);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49547, 1);// 普洛凱爾的第五次指令書
							if (item2 != null) {
								pc.getInventory().removeItem(item2);// 刪除道具
							}

							final L1ItemInstance item3 = pc.getInventory().checkItemX(49207, 1);// 靈魂之火灰燼
							if (item3 != null) {
								pc.getInventory().removeItem(item3);// 刪除道具
							}

							final L1ItemInstance item4 = pc.getInventory().checkItemX(49202, 1);// 空裂痕邪念碎片
							if (item4 != null) {
								pc.getInventory().removeItem(item4);// 刪除道具
							}

							final L1ItemInstance item5 = pc.getInventory().checkItemX(49216, 1);// 普洛凱爾的護身符
							if (item5 != null) {
								pc.getInventory().removeItem(item5);// 刪除道具
							}

							final L1ItemInstance item6 = pc.getInventory().checkItemX(49229, 1);// 異界邪念粉末
							if (item6 != null) {
								pc.getInventory().removeItem(item6);// 刪除道具
							}

							final L1ItemInstance item7 = pc.getInventory().checkItemX(49227, 1);// 紅色之火碎片
							if (item7 != null) {
								pc.getInventory().removeItem(item7);// 刪除道具
							}

							// 辛苦了，自豪的勇士啊！以你為榮啊。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel26"));

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, DragonKnightLv50_1.QUEST.get_id());
							// 給予任務道具(發光的銀塊)
							CreateNewItem.getQuestItem(pc, npc, 49228, 1);

						} else {
							// 337 \f1%0不足%s。
							pc.sendPackets(new S_ServerMessage(337, "$5733(1)"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("k")) {// 取得時空裂痕碎片 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
					// 等級達成要求(LV50)
					if (pc.getLevel() >= DragonKnightLv50_1.QUEST.get_questlevel()) {
						if (pc.getInventory().checkItem(49202)) { // 已經具有物品(時空裂痕邪念碎片)
							// 不是已經交給你<font fg=ffffaf>時空裂痕邪念碎片</font>不是嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel29"));

						} else {
							if (!pc.getInventory().checkItem(49216)) { // 不具有物品
								// 給予任務道具(普洛凱爾的護身符)
								CreateNewItem.getQuestItem(pc, npc, 49216, 1);
							}
							// 給予任務道具(時空裂痕邪念碎片)
							CreateNewItem.getQuestItem(pc, npc, 49202, 1);

							// 這有<font fg=ffffaf>時空裂痕邪念碎片</font>與<font
							// fg=ffffaf>普洛凱爾的護身符</font>。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "prokel28"));
						}

					} else {
						isCloseList = true;
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
