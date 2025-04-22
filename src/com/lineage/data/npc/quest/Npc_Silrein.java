package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.data.quest.DragonKnightLv45_1;
import com.lineage.data.quest.IllusionistLv15_1;
import com.lineage.data.quest.IllusionistLv30_1;
import com.lineage.data.quest.IllusionistLv45_1;
import com.lineage.data.quest.IllusionistLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 長老 希蓮恩<BR>
 * 85027<BR>
 * 
 * @author dexc
 */
public class Npc_Silrein extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Silrein.class);

	private Npc_Silrein() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Silrein();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));

			} else if (pc.isKnight()) {// 騎士
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));

			} else if (pc.isElf()) {// 精靈
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));

			} else if (pc.isWizard()) {// 法師
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				isDragonKnight(pc, npc);

			} else if (pc.isIllusionist()) {// 幻術師
				isIllusionist(pc, npc);

			} else {
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 幻術師
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isIllusionist(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// LV50任務已經完成
			if (pc.getQuest().isEnd(IllusionistLv50_1.QUEST.get_id())) {
				// 修練的如何呢? 托你執行任務上的優秀結果，將複雜的事件漸漸解開了。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein36"));
				return;
			}
			// LV45任務已經完成
			if (pc.getQuest().isEnd(IllusionistLv45_1.QUEST.get_id())) {
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 執行希蓮恩的第四次課題
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein27"));
						break;

					case 1:
						// 交出時空裂痕碎片100個
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein29"));
						break;

					case 2:
					case 3:
					case 4:
						if (pc.getInventory().checkItem(49206)) { // 塞維斯邪念碎片
																	// 49206
							// 安全回來了麻. 執行這次任務花的時間較久因此非常為你擔心呢
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein35"));

						} else {
							// 利用<font fg=ffffaf>時空裂痕邪念碎片</font>到過異界了嗎?
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein34"));
						}
						break;
					}

				} else {
					// 最近 <font fg=ffff0>亞丁</font>聽說很多人看到 <font
					// fg=ffffaf>時空裂痕</font>看到的人越來越多了.
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein26"));
				}
				return;
			}
			// LV30任務已經完成
			if (pc.getQuest().isEnd(IllusionistLv30_1.QUEST.get_id())) {
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(IllusionistLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 現在可是越來越有幻術士的樣子喔。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein18"));
						break;

					case 1:
						// 調查過白螞蟻的痕跡嗎？在那邊到底發生甚麼事了？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein20"));
						break;

					case 2:
					case 3:
						if (pc.getInventory().checkItem(49201)) { // 完整的時間水晶球
							// 已拿來了<font fg=ffffaf>完成的時間水晶球</font>啊。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein50"));
							return;
						}
						if (pc.getInventory().checkItem(49202)) { // 時空裂痕邪念碎片
																	// 49202
							// 你手上拿的是 <font fg=ffffaf>時空裂痕邪念碎片</font>嗎?
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein48"));
							return;
						}
						// 請到<font fg=ffff0>巴拉卡斯棲息地火龍窟</font>找尋白蟻的痕跡
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein23"));
						break;
					}

				} else {
					// 辛苦了～
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein13"));
				}
				return;
			}
			// LV15任務已經完成
			if (pc.getQuest().isEnd(IllusionistLv15_1.QUEST.get_id())) {
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(IllusionistLv30_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 執行希蓮恩的第二課題
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein11"));
						break;

					case 1:
					case 2:
					case 3:
						if (pc.getInventory().checkItem(49191)) { // 艾爾摩部隊日記
							// 手上拿的是什麼書呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein46"));
							return;
						}
						if (pc.getInventory().checkItem(49190)) { // 封印的索夏依卡遺物
							// 你手裡拿的東西是什麼呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein52"));
							return;
						}
						// 有查到歐瑞村莊附近出末的艾爾摩軍團真面目到底是甚麼嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein14"));
						break;
					}

				} else {
					// 目前已經熟悉該怎麼使用魔法立方了嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein5"));
				}

			} else {
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv15_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(IllusionistLv15_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 希蓮恩的第一次課題。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein2"));
						break;

					case 1:// 達到1(任務開始)
							// 交出分析污染原因的物品。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein4"));
						break;
					}

				} else {
					// 歡迎光臨～真年輕的幻術士啊....
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein3"));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 龍騎士
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isDragonKnight(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// LV45任務已經完成
			if (pc.getQuest().isEnd(DragonKnightLv45_1.QUEST.get_id())) {
				// 時間停止的地方... 永恆的生命啊..
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein43"));
				return;
			}
			// 等級達成要求(LV45)
			if (pc.getLevel() >= DragonKnightLv45_1.QUEST.get_questlevel()) {
				// 任務進度
				switch (pc.getQuest().get_step(DragonKnightLv45_1.QUEST.get_id())) {
				case 1:
					// 旅途愉快嗎? 風好涼喔. 偉大的光龍 <font fg=fff00>奧拉奇里亞</font>的子孫啊
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein37"));
					break;

				case 2:
					// 妖魔的行動非常不尋常，聽說召開元老會議，以一起渡過這難關的同事表達點誠意。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein38"));
					break;

				case 3:
					// 交出雪怪之心 10個
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein40"));
					break;

				default:
					// 歡迎～我們是伺候死亡女神席琳的幻術士
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));
					break;
				}

			} else {
				// 歡迎～我們是伺候死亡女神席琳的幻術士
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isIllusionist()) {// 幻術師
			if (cmd.equalsIgnoreCase("a")) {// 希蓮恩的第一次課題。 TODO
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv15_1.QUEST.get_questlevel()) {
					// 給予任務道具(希蓮恩的第一次信件)
					CreateNewItem.getQuestItem(pc, npc, 49172, 1);
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, IllusionistLv15_1.QUEST.get_id());
					// 你對妖精森林的<font fg=ffffaf>世界樹 迷幻森林之母</font>瞭解嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein6"));

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("b")) {// 交出分析污染原因的物品。 TODO
				// 等級達成要求
				if (pc.getLevel() >= IllusionistLv15_1.QUEST.get_questlevel()) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							new int[] { 40510, // 污濁安特的樹皮
												// x
												// 1
									40512, // 污濁安特的樹枝 x 1
									40511, // 污濁安特的水果 x 1
									49169, // 污濁妖魔之心 x 10
									49170,// 污濁精靈核晶 x 1
							}, new int[] { 1, 1, 1, 10, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 調查完畢了嗎？請將可以分析被污染原因的物品交給我吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein8"));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								new int[] { 40510, // 污濁安特的樹皮
													// x
													// 1
										40512, // 污濁安特的樹枝 x 1
										40511, // 污濁安特的水果 x 1
										49169, // 污濁妖魔之心 x 10
										49170,// 污濁精靈核晶 x 1
								}, new int[] { 1, 1, 1, 10, 1, }, new int[] { 49121, // 記憶水晶(立方：燃燒)
																						// x
																						// 1
										269,// 幻術士魔杖 x 1
								}, 1, new int[] { 1, 1, });// 給予

						final L1ItemInstance item = pc.getInventory().checkItemX(49172, 1);// 希蓮恩的第一次信件
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具
						}

						// 將任務設置為結束
						QuestClass.get().endQuest(pc, IllusionistLv15_1.QUEST.get_id());
						// 辛苦了！透過這些可以查出<font fg=ffff0>眠龍洞穴</font>受污染事件的問題點了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein7"));
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("c")) {// 執行希蓮恩的第二課題 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv15_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv30_1.QUEST.get_questlevel()) {
						// 給予任務道具(希蓮恩的第二次信件)
						CreateNewItem.getQuestItem(pc, npc, 49173, 1);
						// 給予任務道具(希蓮恩之袋)
						CreateNewItem.getQuestItem(pc, npc, 49179, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, IllusionistLv30_1.QUEST.get_id());
						// 聽說歐瑞村莊附近出現艾爾摩軍團欺負亞丁騎士與修練中的幼小幻術士.
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein12"));

					} else {
						isCloseList = true;
					}
				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("d")) {// 交出艾爾摩部隊日記 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv15_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv30_1.QUEST.get_questlevel()) {
						final L1ItemInstance item = pc.getInventory().checkItemX(49191, 1);// 艾爾摩部隊日記
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49173, 1);// 希蓮恩的第二次信件
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, IllusionistLv30_1.QUEST.get_id());
							// 辛苦了～
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein13"));

							// 給予任務道具(記憶水晶(立方：衝擊))
							CreateNewItem.getQuestItem(pc, npc, 49131, 1);
							// 給予任務道具(幻術士法書)
							CreateNewItem.getQuestItem(pc, npc, 21101, 1);

						} else {
							// 337 \f1%0不足%s。
							pc.sendPackets(new S_ServerMessage(337, "$5634(1)"));
							isCloseList = true;
						}

					} else {
						isCloseList = true;
					}
				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("o")) {// 重新接收生鏽的笛子 TODO
				// LV15任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv15_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv30_1.QUEST.get_questlevel()) {
						if (pc.getInventory().checkItem(49189)) { // 已經具有物品-索夏依卡靈魂之笛
							// 應該已經給予了 <font
							// fg=ffffaf>生鏽的笛子</font>吧，請好好找一下我送你的袋子裡面喔。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein17"));
							return;
						}
						if (pc.getInventory().checkItem(49186)) { // 已經具有物品
							// 應該已經給予了 <font
							// fg=ffffaf>生鏽的笛子</font>吧，請好好找一下我送你的袋子裡面喔。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein17"));

						} else {
							// 給予任務道具(生鏽的笛子)
							CreateNewItem.getQuestItem(pc, npc, 49186, 1);
							// 這邊有 <font fg=ffffaf>生鏽的笛子</font>，這是重要的物品請小心保管。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein16"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("e")) {// 執行希蓮恩的第三課題 TODO
				// LV30任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv30_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv45_1.QUEST.get_questlevel()) {
						// 給予任務道具(希蓮恩的第三次信件)
						CreateNewItem.getQuestItem(pc, npc, 49174, 1);
						// 給予任務道具(希蓮恩之袋)
						CreateNewItem.getQuestItem(pc, npc, 49180, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, IllusionistLv45_1.QUEST.get_id());
						// 來，我剛給你的是<font fg=ffffaf>時空裂痕水晶(綠色)</font>。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein19"));

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("f")) {// 交出記憶的碎片 TODO
				// LV30任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv30_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv45_1.QUEST.get_questlevel()) {
						// 需要物件不足
						if (CreateNewItem.checkNewItem(pc,
								new int[] { 49194, // 第一次記憶碎片
													// 49194
										49195, // 第二次記憶碎片 49195
										49196,// 第三次記憶碎片 49196
								}, new int[] { 1, 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
							// <font fg=ffffaf>記憶碎片</font>在哪呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein21"));

						} else {// 需要物件充足
							// 收回任務需要物件 給予任務完成物件
							CreateNewItem.createNewItem(pc,
									new int[] { 49194, // 第一次記憶碎片
														// 49194
											49195, // 第二次記憶碎片 49195
											49196,// 第三次記憶碎片 49196
									}, new int[] { 1, 1, 1, }, new int[] { 49193,// 時空裂痕水晶(藍色)-3
																					// 49193
									}, 1, new int[] { 3, });// 給予

							// 將任務進度提升為2
							pc.getQuest().set_step(IllusionistLv45_1.QUEST.get_id(), 2);
							// 你也看過這記憶碎片了嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein22"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("g")) {// 交出時空裂痕邪念碎片 TODO
				// LV30任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv30_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv45_1.QUEST.get_questlevel()) {
						final L1ItemInstance item = pc.getInventory().checkItemX(49202, 1);// 時空裂痕邪念碎片
																							// 49202
						if (item != null) {
							pc.getInventory().removeItem(item, 1);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49174, 1);// 希蓮恩的第三次信件
																								// 49174
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, IllusionistLv45_1.QUEST.get_id());

							// <font fg=ffffaf>時空裂痕</font>!! <font
							// fg=fff00>吉爾塔斯</font>的出現引起 <font
							// fg=ffff0>亞丁</font>區域變的很混亂.
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein25"));
							// 給予任務道具(幻術士斗篷)
							CreateNewItem.getQuestItem(pc, npc, 21100, 1);

						} else {
							// 有找到有關白蟻的真理嗎？透過<font
							// fg=ffffaf>時空裂痕水晶(綠色)</font>查看相關痕跡的記憶嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein24"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("h")) {// 執行希蓮恩的第四次課題 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv45_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv50_1.QUEST.get_questlevel()) {
						// 給予任務道具(希蓮恩的第五次信件)
						CreateNewItem.getQuestItem(pc, npc, 49176, 1);
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, IllusionistLv50_1.QUEST.get_id());
						// 在亞丁世界中聽說 <font fg=ffffaf>時空裂痕</font>不規則的開啟
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein28"));

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("i")) {// 交出時空裂痕碎片100個 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv45_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv50_1.QUEST.get_questlevel()) {

						final L1ItemInstance item = pc.getInventory().checkItemX(49101, 100);// 時空裂痕碎片49101
						if (item != null) {
							pc.getInventory().removeItem(item, 100);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49176, 1);// 希蓮恩的第五次信件
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 啊! 這就是 <font fg=ffffaf>時空裂痕</font>中掉落的碎片
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein31"));

							// 提升任務進度
							pc.getQuest().set_step(IllusionistLv50_1.QUEST.get_id(), 2);
							// 給予任務道具(希蓮恩的第六次信件 49177)
							CreateNewItem.getQuestItem(pc, npc, 49177, 1);
							// 給予任務道具(時空裂痕邪念碎片)
							CreateNewItem.getQuestItem(pc, npc, 49202, 1);
							// 給予任務道具(希蓮恩的護身符 49178)
							CreateNewItem.getQuestItem(pc, npc, 49178, 1);

						} else {
							// 你找到<font fg=ffffaf>時空裂痕碎片100個</font>嗎?
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein30"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("j")) {// 交出塞維斯邪念碎片與靈魂之火灰燼 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv45_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv50_1.QUEST.get_questlevel()) {

						final L1ItemInstance item = pc.getInventory().checkItemX(49206, 1);// 塞維斯邪念碎片
																							// 49206
						if (item != null) {
							pc.getInventory().removeItem(item);// 刪除道具

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49177, 1);// 希蓮恩的第六次信件
							if (item2 != null) {
								pc.getInventory().removeItem(item2);// 刪除道具
							}

							final L1ItemInstance item3 = pc.getInventory().checkItemX(49207, 1);// 靈魂之火灰燼
							if (item3 != null) {
								pc.getInventory().removeItem(item3);// 刪除道具
							}

							final L1ItemInstance item4 = pc.getInventory().checkItemX(49202, 1);// 時空裂痕邪念碎片
							if (item4 != null) {
								pc.getInventory().removeItem(item4);// 刪除道具
							}

							final L1ItemInstance item5 = pc.getInventory().checkItemX(49178, 1);// 希蓮恩的護身符
							if (item5 != null) {
								pc.getInventory().removeItem(item5);// 刪除道具
							}

							final L1ItemInstance item6 = pc.getInventory().checkItemX(49203, 1);// 食腐獸之血
							if (item6 != null) {
								pc.getInventory().removeItem(item6);// 刪除道具
							}

							final L1ItemInstance item7 = pc.getInventory().checkItemX(49204, 1);// 翼龍之血
							if (item7 != null) {
								pc.getInventory().removeItem(item7);// 刪除道具
							}

							// 這次你的功勞最大～來～拿這個去找 <font fg=fff00>鐵匠 巴特爾</font>吧。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein45"));

							// 將任務設置為結束
							QuestClass.get().endQuest(pc, IllusionistLv50_1.QUEST.get_id());
							// 給予任務道具(希蓮恩的推薦書 49181)
							CreateNewItem.getQuestItem(pc, npc, 49181, 1);
							// 給予任務道具(特別的原石 49205)
							CreateNewItem.getQuestItem(pc, npc, 49205, 1);

						} else {
							// 利用<font fg=ffffaf>時空裂痕邪念碎片</font>到過異界了嗎?
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein34"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("k")) {// 需要再調查 TODO
				// LV45任務已經完成
				if (pc.getQuest().isEnd(IllusionistLv45_1.QUEST.get_id())) {
					// 等級達成要求
					if (pc.getLevel() >= IllusionistLv50_1.QUEST.get_questlevel()) {
						if (pc.getInventory().checkItem(49202)) { // 已經具有物品(時空裂痕邪念碎片)
							// 不是已經交給你<font fg=ffffaf>時空裂痕邪念碎片</font>不是嗎？
							// pc.sendPackets(new S_NPCTalkReturn(npc.getId(),
							// "prokel29"));
							isCloseList = true;

						} else {
							if (!pc.getInventory().checkItem(49178)) { // 不具有物品
								// 給予任務道具(希蓮恩的護身符 49178)
								CreateNewItem.getQuestItem(pc, npc, 49178, 1);
							}
							// 給予任務道具(時空裂痕邪念碎片)
							CreateNewItem.getQuestItem(pc, npc, 49202, 1);

							// 利用你所取來的 <font fg=ffffaf>時空裂痕碎片</font>可引發 <font
							// fg=ffffaf>時空裂痕邪念碎片</font>的力量
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein32"));
						}

					} else {
						isCloseList = true;
					}

				} else {
					isCloseList = true;
				}
			}

		} else if (pc.isDragonKnight()) {// 龍騎士
			// LV30任務已經完成
			if (pc.getQuest().isEnd(DragonKnightLv30_1.QUEST.get_id())) {
				// 等級達成要求(LV45任務)
				if (pc.getLevel() >= DragonKnightLv45_1.QUEST.get_questlevel()) {
					if (cmd.equalsIgnoreCase("l")) {// 交出長老普洛凱爾的信件
						final L1ItemInstance item = pc.getInventory().checkItemX(49209, 1);// 普洛凱爾的信件
						if (item != null) {
							// 將任務進度提升為2
							pc.getQuest().set_step(DragonKnightLv45_1.QUEST.get_id(), 2);
							pc.getInventory().removeItem(item, 1);// 刪除道具
							// 妖魔的行動非常不尋常，聽說召開元老會議，以一起渡過這難關的同事表達點誠意。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein38"));
						}

					} else if (cmd.equalsIgnoreCase("m")) {// 接受希蓮恩的請求
						// 將任務進度提升為3
						pc.getQuest().set_step(DragonKnightLv45_1.QUEST.get_id(), 3);
						// 為了加工我們特有的原石需要 <font fg=ffffaf>雪怪之心</font> 10個.
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein39"));

						// 給予任務道具(希蓮恩的指令書)
						CreateNewItem.getQuestItem(pc, npc, 49171, 1);

					} else if (cmd.equalsIgnoreCase("n")) {// 交出雪怪之心 10個
						final L1ItemInstance item = pc.getInventory().checkItemX(49225, 10);// 雪怪之心
						if (item != null) {

							final L1ItemInstance item2 = pc.getInventory().checkItemX(49171, 1);// 希蓮恩的指令書
							if (item2 != null) {
								pc.getInventory().removeItem(item2, 1);// 刪除道具
							}

							// 將任務進度提升為4
							pc.getQuest().set_step(DragonKnightLv45_1.QUEST.get_id(), 4);
							pc.getInventory().removeItem(item);// 刪除道具
							// 謝謝. <font fg=ffffaf>雪怪之心</font>擁有神秘的力量.
							// 為了讓我們特有的水晶加工需要相當多的材料。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein41"));

							// 給予任務道具(幻術士同盟徽印)
							CreateNewItem.getQuestItem(pc, npc, 49224, 1);

						} else {
							// <font fg=ffffaf>雪怪之心</font> 10個在哪裡呢？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "silrein42"));
						}
					}

				} else {
					isCloseList = true;
				}

			} else {
				isCloseList = true;
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
