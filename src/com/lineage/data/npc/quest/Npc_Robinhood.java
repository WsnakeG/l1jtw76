package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_2;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 羅賓孫<BR>
 * 71256<BR>
 * 說明:妖精族傳說中的弓 (妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Robinhood extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Robinhood.class);

	private Npc_Robinhood() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Robinhood();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else if (pc.isKnight()) {// 騎士
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else if (pc.isElf()) {// 精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
					// 完成了! 太棒了! 可不可以捏我一下
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood12"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= ElfLv45_2.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 你有事要找我嗎?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood1"));
						break;

					case 1:// 達到1(任務開始)
							// 你所說的傳說中的長弓好像是熾炎天使弓
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood5"));
						break;

					case 2:// 達到2
					case 3:// 達到3
					case 4:// 達到4
					case 5:// 達到5
							// 你到伊娃的聖地去找神官 '知布烈'。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
						break;

					case 6:// 達到6
							// 月光之氣息
						if (pc.getInventory().checkItem(41351, 1)) {
							// 拿出材料和便條紙
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood9"));

						} else {
							// 你到伊娃的聖地去找神官 '知布烈'。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
						}
						break;

					case 7:// 達到7
							// 羅賓孫之戒
						if (pc.getInventory().checkItem(41350, 1)) {
							// 將羅賓孫的戒指和材料交給他
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood11"));

						} else {
							// 為了製作熾炎天使弓的弓架，材料我都寫在清單上，那材料都在哪裡呢?
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood18"));
						}
						break;
					}

				} else {
					// 哼哼...我口好渴，這時候如果有蘋果汁會更好
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood19"));
				}

			} else if (pc.isWizard()) {// 法師
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));

			} else {
				// 你是怎麼到這裡來的？這裡是精靈才能來的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isElf()) {// 精靈
			// LV45任務已經完成
			if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
				return;
			}
			// 任務進度
			switch (pc.getQuest().get_step(ElfLv45_2.QUEST.get_id())) {
			case 0:// 達到0
				if (cmd.equals("A")) {// 拿出蘋果汁
					final L1ItemInstance item = pc.getInventory().findItemId(40028);
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具(蘋果汁)
						// 將任務設置為執行中
						QuestClass.get().startQuest(pc, ElfLv45_2.QUEST.get_id());
						// 這不是蘋果汁嗎...好吧，你想知道什麼?
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood4"));

					} else {
						// 哼哼...我口好渴，這時候如果有蘋果汁會更好
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood19"));
					}
				}
				break;

			case 1:// 達到1
				if (cmd.equals("B")) {// 我一定會拿回來的
					// 提升任務進度
					pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 2);
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 41348, 1);// 羅賓孫的推薦書
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 41346, 1);// 羅賓孫的便條紙
					// 你到伊娃的聖地去找神官 '知布烈'。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood13"));
				}
				break;

			case 2:// 達到2
			case 3:// 達到3
			case 4:// 達到4
			case 5:// 達到5
				break;

			case 6:// 達到6
				if (cmd.equals("C")) {// 拿出材料和便條紙
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							new int[] { 41352, 40618, 40643, 40645, 40651, 40676, 40514, 41351, 41346 },
							new int[] { 4, 30, 30, 30, 30, 30, 20, 1, 1 }) < 1) {
						// 傳回可交換道具數小於1(需要物件不足)

						// 月光之氣息 神聖獨角獸之角 4個
						if (CreateNewItem.checkNewItem(pc, new int[] { 41352, 41351 },
								new int[] { 4, 1 }) < 1) {
							// 月光之氣息</font>和 <font fg=ffffaf>神聖獨角獸之角</font>
							// 4個已經都準備好了嗎？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood15"));

						} else {
							// 你有帶各 <font fg=ffffaf>元素的氣息</font> 各30個？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood14"));
						}

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 材料
								new int[] { 41352, 40618, 40643, 40645, 40651, 40676, 40514, 41351, 41346 },
								new int[] { 4, 30, 30, 30, 30, 30, 20, 1, 1 },

								new int[] { 41347, 41350 }, // 羅賓孫的便條紙,羅賓孫之戒
								1, new int[] { 1, 1 });// 給予
														// 提升任務進度
						pc.getQuest().set_step(ElfLv45_2.QUEST.get_id(), 7);
						// 很好! 材料已經都有了，就讓我來做做看吧。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood10"));
					}
				}
				break;

			case 7:// 達到7
				if (cmd.equals("E")) {// 將羅賓孫的戒指和材料交給他
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc,
							new int[] { 40491, 40495, 100, 40509, 40052, 40053, 40054, 40055, 41347, 41350 },
							new int[] { 30, 40, 1, 12, 1, 1, 1, 1, 1, 1 }) < 1) {
						// 傳回可交換道具數小於1(需要物件不足)

						// 高品質紅寶石，高品質藍寶石，高品質綠寶石，高品質鑽石
						if (CreateNewItem.checkNewItem(pc, new int[] { 40052, 40053, 40054, 40055 },
								new int[] { 1, 1, 1, 1 }) < 1) {
							// 最頂尖的弓當然也要稍微包裝一下才好看阿!
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood16"));

						} else {
							// 糟糕糟糕...材料不夠？就讓我再說一次吧。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood17"));
						}

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc,
								// 材料
								new int[] { 40491, 40495, 100, 40509, 40052, 40053, 40054, 40055, 41347,
										41350 },
								new int[] { 30, 40, 1, 12, 1, 1, 1, 1, 1, 1 },

								new int[] { 205 }, // 熾炎天使弓
								1, new int[] { 1 });// 給予
													// 將任務設置為結束
						QuestClass.get().endQuest(pc, ElfLv45_2.QUEST.get_id());
						// 完成了! 太棒了! 可不可以捏我一下？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "robinhood12"));
					}
				}
				break;

			default:// 其他
				isCloseList = true;
				break;
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