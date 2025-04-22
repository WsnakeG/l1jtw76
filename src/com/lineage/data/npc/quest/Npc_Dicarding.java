package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CKEWLv50_1;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.data.quest.CrownLv50_1;
import com.lineage.data.quest.ElfLv45_1;
import com.lineage.data.quest.ElfLv50_1;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.data.quest.KnightLv50_1;
import com.lineage.data.quest.WizardLv45_1;
import com.lineage.data.quest.WizardLv50_1;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 迪嘉勒廷<BR>
 * 70739<BR>
 * 小惡魔的可疑行動 (王族50級以上官方任務)<BR>
 * 精靈們的騷動 (騎士50級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_Dicarding extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Dicarding.class);

	private Npc_Dicarding() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Dicarding();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				isCrown(pc, npc);

			} else if (pc.isKnight()) {// 騎士
				isKnight(pc, npc);

			} else if (pc.isElf()) {// 精靈
				isElf(pc, npc);

			} else if (pc.isWizard()) {// 法師
				isWizard(pc, npc);

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));

			} else {
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 法師
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isWizard(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getQuest().isEnd(CKEWLv50_1.QUEST.get_id())) {
				// 我代表亞丁城的村民，誠心感謝你。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw15"));
				return;
			}

			// LV50任務已經完成
			if (pc.getQuest().isEnd(WizardLv50_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= CKEWLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CKEWLv50_1.QUEST.get_id())) {
					case 0:
						// 辛苦了，間諜目前沒事吧？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw6"));
						break;

					case 1:
						// 魔族很強的，法師啊，請小心身體
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw10"));
						break;

					case 2:
						// 表示馬上再到再生聖殿一趟
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw11"));
						break;

					case 3:
						// 提供祭壇的碎片，並告知已破壞祭壇的事情
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw13"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}
				return;
			}

			// LV45任務已經完成
			if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= WizardLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(WizardLv50_1.QUEST.get_id())) {
					case 0:
						// 我正在等你呢！！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw1"));
						break;

					case 1:
					case 2:
						final L1ItemInstance item = pc.getInventory().checkItemX(49164, 1);// 間諜報告書
						if (item != null) {
							// 提供間諜報告書
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw5"));

						} else {
							// 若是間諜或是那份報告書落入魔族的手中
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw4"));
						}
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}

			} else {
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 精靈
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isElf(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getQuest().isEnd(CKEWLv50_1.QUEST.get_id())) {
				// 代表亞丁王國的子民誠心向你感謝。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge17"));
				return;
			}

			// LV50任務已經完成
			if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= CKEWLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CKEWLv50_1.QUEST.get_id())) {
					case 0:
						// 詢問該怎麼辦？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge10"));
						break;

					case 1:
						// 再生聖殿可從魔族神殿附近過去的
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge12"));
						break;

					case 2:
						// 準備好了要出發
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge13"));
						break;

					case 3:
						// 怎麼樣，成功了嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge15"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}
				return;
			}

			// LV45任務已經完成
			if (pc.getQuest().isEnd(ElfLv45_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= ElfLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv50_1.QUEST.get_id())) {
					case 0:
						// 歡迎光臨，受到生命之樹加持的妖精啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge1"));
						break;

					case 1:
						// 有找到什麼資料嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge4"));
						break;

					case 2:
						// 恭喜你平安歸來，辛苦你了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge5"));
						break;

					case 3:
					case 4:
					case 5:
						final L1ItemInstance item = pc.getInventory().checkItemX(49163, 1);// 密封的情報書
						if (item != null) {
							// 將她安全送回去了。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge9"));

						} else {
							// 打聽到她的消息再告訴我。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge8"));
						}
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}

			} else {
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 騎士
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isKnight(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getQuest().isEnd(CKEWLv50_1.QUEST.get_id())) {
				// 我代表亞丁王國的人民誠心的感謝你。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk16"));
				return;
			}

			// LV50任務已經完成
			if (pc.getQuest().isEnd(KnightLv50_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= CKEWLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CKEWLv50_1.QUEST.get_id())) {
					case 0:
						// 繼續聽他述說
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk10"));
						break;

					case 1:
						// 但若是試一次沒成功時，請再回來找我
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk11"));
						break;

					case 2:
						// 表明將再次挑戰
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk12"));
						break;

					case 3:
						// 提供祭壇的碎片，並告知已破壞祭壇的事情。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk14"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}
				return;
			}

			// LV45任務已經完成
			if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= KnightLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv50_1.QUEST.get_id())) {
					case 0:
						// 騎士啊～歡迎～歡迎～從馬沙那聽到很多有關你冒險的故事
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk1"));
						break;

					case 1:
						// 給予丹特斯的召書
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk4"));
						break;

					case 2:
						// 繼續聽他述說
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk6"));
						break;

					case 3:
						// 給予精靈的私語
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk9"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}

			} else {
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 王族
	 * 
	 * @param pc
	 * @param npc
	 */
	private void isCrown(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.getQuest().isEnd(CKEWLv50_1.QUEST.get_id())) {
				// 我代表亞丁王國的人民誠心的祝福你。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp15"));
				return;
			}

			// LV50任務已經完成
			if (pc.getQuest().isEnd(CrownLv50_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= CKEWLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CKEWLv50_1.QUEST.get_id())) {
					case 0:
					case 1:
						// 見到行政官奇浩了嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp10"));
						break;

					case 2:
						// 要求再次潛入
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp11"));
						break;

					case 3:
						// 提供祭壇的碎片，並告知已破壞祭壇的事情。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp13"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}
				return;
			}

			// LV45任務已經完成
			if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
				// 等級達成要求(LV50)
				if (pc.getLevel() >= CrownLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CrownLv50_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 我從馬沙那聽到很多關於你的冒險故事
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp1"));
						break;

					case 1:
						// 提供調職命令書
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp4"));
						break;

					case 2:
						// 要求再次變身
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp8"));
						break;

					default:
						break;
					}

				} else {
					// 古代的預言裡提到的英雄真的會出現嗎...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
				}

			} else {
				// 古代的預言裡提到的英雄真的會出現嗎...
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicarding"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isCrown()) {// 王族
			// 任務進度(LV50 1階段)
			switch (pc.getQuest().get_step(CrownLv50_1.QUEST.get_id())) {
			case 0:// 任務尚未開始
				if (cmd.equalsIgnoreCase("f")) {// 接受請求
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CrownLv50_1.QUEST.get_id());
					// 不知調查進行順不順利呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp3"));
				}
				break;

			case 1:
				if (cmd.equalsIgnoreCase("e")) {// 提供調職命令書
					final L1ItemInstance item = pc.getInventory().checkItemX(49159, 1);// 調職命令書
					if (item != null) {
						// 提升任務進度
						pc.getQuest().set_step(CrownLv50_1.QUEST.get_id(), 2);
						// 祝你好運！！請一定要拯救亞丁。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp7"));
						// 收到調職命令的小惡魔
						L1PolyMorph.doPoly(pc, 4261, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);

					} else {
						// 嗯...我什麼也看不到
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp4a"));
					}
				}
				break;

			case 2:
				if (cmd.equalsIgnoreCase("c")) {// 要求再次變身
					// 我的魔力不夠強，因此變身效果無法維持很久。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp9"));
					// 收到調職命令的小惡魔
					L1PolyMorph.doPoly(pc, 4261, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
				}
				break;

			default:
				break;
			}

			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("b")) {// 要求再次潛入
					// 目前...在附近的間諜因為你的失敗而被魔族追捕中。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp12"));

				} else if (cmd.equalsIgnoreCase("a")) {// 提供祭壇的碎片，並告知已破壞祭壇的事情。
					final L1ItemInstance item = pc.getInventory().checkItemX(49241, 1);// 祭壇的碎片
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CKEWLv50_1.QUEST.get_id());
						// 辛苦了，真的辛苦了
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp14"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 51, 1);// 黃金權杖

						// 刪除遺留任務道具
						delItem(pc);

					} else {
						// 要求再次潛入
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingp11"));
					}
				}
			}

		} else if (pc.isKnight()) {// 騎士
			// 任務進度(LV50 1階段)
			switch (pc.getQuest().get_step(KnightLv50_1.QUEST.get_id())) {
			case 0:
				if (cmd.equalsIgnoreCase("g")) {// 我願意幫忙
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, KnightLv50_1.QUEST.get_id());
					// 哇～真是太謝謝你了，我很期待你能幫忙帶些好消息回來。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk3"));
				}
				break;

			case 1:
				if (cmd.equalsIgnoreCase("h")) {// 給予丹特斯的召書
					final L1ItemInstance item = pc.getInventory().checkItemX(49160, 1);// 丹特斯的召書
					if (item != null) {
						// 提升任務進度
						pc.getQuest().set_step(KnightLv50_1.QUEST.get_id(), 2);
						// 辛苦了，不虧是勇猛的騎士，我看看... 嗯... 嗯...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk5"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

					} else {
						// 若是聽到任何的新消息，請一定要告知我，拜託你了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk8"));
					}
				}
				break;

			case 2:
				if (cmd.equalsIgnoreCase("i")) {// 繼續聽他述說
					// 提升任務進度
					pc.getQuest().set_step(KnightLv50_1.QUEST.get_id(), 3);
					// 真是太謝謝你了，那我再厚臉皮拜託你一件事好嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk7"));
				}
				break;

			case 3:
				if (cmd.equalsIgnoreCase("j")) {// 給予精靈的私語
					final L1ItemInstance item = pc.getInventory().checkItemX(49161, 10);// 精靈的私語
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, KnightLv50_1.QUEST.get_id());
						// 果然～真的是名不虛傳！還好有你的幫忙得到了充分的情報
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk10"));
						pc.getInventory().removeItem(item);// 刪除道具(全部)

						// 將任務設置為啟動
						QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());

					} else {
						// 好像還不太夠喔，可以再幫忙收集幾個嗎？嗯～大約10個就夠了
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk9a"));
					}
				}
				break;

			default:
				break;
			}

			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("k")) {// 表明將再次挑戰
					// 啊～這樣喔～了解，為了掩護及保護你
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk13"));

				} else if (cmd.equalsIgnoreCase("l")) {// 提供祭壇的碎片，並告知已破壞祭壇的事情。
					final L1ItemInstance item = pc.getInventory().checkItemX(49241, 1);// 祭壇的碎片
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CKEWLv50_1.QUEST.get_id());
						// 果然～你真不虧是優秀的騎士
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk15"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 56, 1);// 黑燄之劍

						// 刪除遺留任務道具
						delItem(pc);

					} else {
						// 表明將再次挑戰
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingk12"));
					}
				}
			}

		} else if (pc.isElf()) {// 精靈
			// 任務進度(LV50 1階段)
			switch (pc.getQuest().get_step(ElfLv50_1.QUEST.get_id())) {
			case 0:
				if (cmd.equalsIgnoreCase("m")) {// 詢問詳細事項
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, ElfLv50_1.QUEST.get_id());
					// 若在巨蟻洞穴找到什麼以前妖精的記錄請再拿給我。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge3"));
				}
				break;

			case 1:
				if (cmd.equalsIgnoreCase("n")) {// 提供秘笈書
					final L1ItemInstance item = pc.getInventory().checkItemX(49162, 1);// 古代黑妖的秘笈
					if (item != null) {
						// 提升任務進度
						pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 2);
						// 恭喜你平安歸來，辛苦你了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge5"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

					} else {
						// 若在巨蟻洞穴找到什麼以前妖精的記錄請再拿給我。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge3"));
					}
				}
				break;

			case 2:
				if (cmd.equalsIgnoreCase("o")) {// 現在立即去調查
					// 提升任務進度
					pc.getQuest().set_step(ElfLv50_1.QUEST.get_id(), 3);
					// 感謝你的持續協力幫忙，她目前可能在魔族神殿附近變裝為黑妖。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge7"));
				}
				break;

			case 3:
			case 4:
			case 5:
				if (cmd.equalsIgnoreCase("p")) {// 將她安全送回去了。
					final L1ItemInstance item = pc.getInventory().checkItemX(49163, 1);// 密封的情報書
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, ElfLv50_1.QUEST.get_id());
						// 誠心感謝你，託你的幫忙順利完成情報碎片
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge10"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 將任務設置為啟動
						QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());

					} else {
						// 打聽到她的消息再告訴我。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge8"));
					}
				}
				break;

			default:
				break;
			}

			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("q")) {// 準備好了要出發
					// 來，已經安排好了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge14"));

				} else if (cmd.equalsIgnoreCase("y")) {// 拿出破壞祭壇證明後接收赤焰之弓
					final L1ItemInstance item = pc.getInventory().checkItemX(49241, 1);// 祭壇的碎片
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CKEWLv50_1.QUEST.get_id());
						// 誠心感謝你，我代表亞丁感謝你
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge16"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 184, 1);// 赤焰之弓

						// 刪除遺留任務道具
						delItem(pc);

					} else {
						// 準備好了要出發
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge13"));
					}

				} else if (cmd.equalsIgnoreCase("s")) {// 拿出破壞祭壇證明後接收赤焰之劍
					final L1ItemInstance item = pc.getInventory().checkItemX(49241, 1);// 祭壇的碎片
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CKEWLv50_1.QUEST.get_id());
						// 誠心感謝你，我代表亞丁感謝你
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge16"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 50, 1);// 赤焰之劍

						// 刪除遺留任務道具
						delItem(pc);

					} else {
						// 準備好了要出發
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardinge13"));
					}
				}
			}

		} else if (pc.isWizard()) {// 法師
			// 任務進度(LV50 1階段)
			switch (pc.getQuest().get_step(WizardLv50_1.QUEST.get_id())) {
			case 0:
				if (cmd.equalsIgnoreCase("t")) {// 接受迪嘉勒廷公爵的請求
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, WizardLv50_1.QUEST.get_id());
					// 感謝你的勇敢。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw3"));
				}
				break;

			case 1:
			case 2:
				if (cmd.equalsIgnoreCase("u")) {// 提供間諜報告書
					final L1ItemInstance item = pc.getInventory().checkItemX(49164, 1);// 間諜報告書
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, WizardLv50_1.QUEST.get_id());
						// 辛苦了，間諜目前沒事吧？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw6"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

					} else {
						// 若是間諜或是那份報告書落入魔族的手中
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw4"));
					}
				}
				break;

			case 255:
				if (cmd.equalsIgnoreCase("v")) {// 我去破壞再生聖殿
					// 將任務設置為啟動
					QuestClass.get().startQuest(pc, CKEWLv50_1.QUEST.get_id());
					// 嗯...我想在說明所有事件的來龍去脈後
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw9"));
				}
				break;

			default:
				break;
			}

			// 任務開始
			if (pc.getQuest().isStart(CKEWLv50_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("w")) {// 表示馬上再到再生聖殿一趟
					// 曾經也有其他冒險者也去嘗試過...但大家好像都失敗了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw12"));

				} else if (cmd.equalsIgnoreCase("x")) {// 提供祭壇的碎片，並告知已破壞祭壇的事情。
					final L1ItemInstance item = pc.getInventory().checkItemX(49241, 1);// 祭壇的碎片
					if (item != null) {
						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CKEWLv50_1.QUEST.get_id());
						// 哇哇哇，殷海薩啊...謝謝您...真的感謝您！！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw14"));
						pc.getInventory().removeItem(item, 1);// 刪除道具

						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 20225, 1);// 瑪那水晶球

						// 刪除遺留任務道具
						delItem(pc);

					} else {
						// 表示馬上再到再生聖殿一趟
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dicardingw11"));
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

	private static int[] _itemIds = new int[] { 40742, // 古代之箭
			49165, // 聖殿 2樓鑰匙
			49166, // 聖殿 3樓鑰匙
			49167, // 魔之角笛
			49168, // 破壞之秘藥
			49239, // 消滅之意志
			65, // 天空之劍
			133, // 古代人的智慧
			191, // 水之豎琴
			192,// 水精靈之弓
	};

	/**
	 * 刪除遺留任務道具
	 * 
	 * @param pc
	 */
	private void delItem(final L1PcInstance pc) {
		for (final int itemId : _itemIds) {

			final L1ItemInstance reitem = pc.getInventory().findItemId(itemId);
			if (reitem != null) {
				if (reitem.isEquipped()) {
					// 解除裝備
					pc.getInventory().setEquipped(reitem, false, false, false);
				}
				// 165：\f1%0%s 強烈的顫抖後消失了。
				pc.sendPackets(new S_ServerMessage(165, reitem.getName()));
				pc.getInventory().removeItem(reitem);// 移除道具
			}
		}
	}
}