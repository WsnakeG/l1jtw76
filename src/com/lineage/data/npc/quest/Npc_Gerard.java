package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 傑瑞德<BR>
 * 70794<BR>
 * 說明:拯救被幽禁的吉姆 (騎士30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Gerard extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Gerard.class);

	private Npc_Gerard() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Gerard();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你就是躲避反王的欺壓而逃離的王族嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardp1"));

			} else if (pc.isKnight()) {// 騎士
				// LV30任務已經完成
				if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 我們認定你是一個真正的紅騎士。。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkEcg"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
					case 1:// 達到1(任務開始)
					case 2:// 達到2(交談吉姆)
					case 3:// 達到3(接受試練)
							// 歡迎光臨，像你一樣想要成為<a
							// link="gerardk2">騎士</a>的許多年輕人會在這聚集。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardk7"));
						break;

					case 4:// 達到4
							// 接受傑瑞德的試鍊
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE1"));
						break;

					case 5:// 達到5
							// 交給蛇女之鱗
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE2"));
						break;

					case 6:// 達到6
							// 返生藥水的真實
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE3"));
						break;

					case 7:// 達到7
							// 交出得到的感謝信
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE4"));
						break;

					default:// 其他
						// 恭喜你～你終於成為有資格帶著武器保衛世界並負起責任的人啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE3"));
						break;
					}

				} else {
					// 歡迎光臨，像你一樣想要成為<a link="gerardk2">騎士</a>的許多年輕人會在這聚集。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardk7"));
				}

			} else if (pc.isElf()) {// 精靈
				// 唷~原來你就是妖精族的朋友
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerarde1"));

			} else if (pc.isWizard()) {// 法師
				// 魔法師，歡迎歡迎。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardw1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 嗯...你是墮落妖精的後代黑暗妖精
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardde1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 當亞丁唯一的國王杜克過世後
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardk4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 當亞丁唯一的國王杜克過世後
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardk4"));

			} else {
				// 當亞丁唯一的國王杜克過世後
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardk4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isKnight()) {// 騎士
			// LV30任務已經完成
			if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
				isCloseList = true;

			} else {// 任務已經開始
				// 任務進度
				switch (pc.getQuest().get_step(KnightLv30_1.QUEST.get_id())) {
				case 4:// 達到4
					if (cmd.equalsIgnoreCase("quest 16 gerardkE2")) {// 接受傑瑞德的試鍊
						// 提升任務進度
						pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 5);
						// 交給蛇女之鱗
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE2"));
					}
					break;

				case 5:// 達到5
					if (cmd.equalsIgnoreCase("request potion of rebirth")) {// 交給蛇女之鱗
						// 需要物件不足
						if (CreateNewItem.checkNewItem(pc, 40544, // 任務完成需要物件(蛇女之鱗
																	// x 1)
								1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
							isCloseList = true;

						} else {// 需要物件充足
							// 收回任務需要物件 給予任務完成物件
							CreateNewItem.createNewItem(pc, 40544, 1, // 蛇女之鱗 x
																		// 1
									40607, 1);// 返生藥水 x 1
							// 提升任務進度
							pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 6);
							// 返生藥水的真實
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE3"));
						}
					}
					break;

				case 6:// 達到6
					if (cmd.equalsIgnoreCase("quest 18 gerardkE4")) {// 返生藥水的真實
						// 提升任務進度
						pc.getQuest().set_step(KnightLv30_1.QUEST.get_id(), 7);
						// 交出得到的感謝信
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE4"));
					}
					break;

				case 7:// 達到7
					if (cmd.equalsIgnoreCase("request shield of red knights")) {// 交出得到的感謝信
						// 需要物件不足
						if (CreateNewItem.checkNewItem(pc, 40529, // 任務完成需要物件(感謝信
																	// x 1)
								1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
							isCloseList = true;

						} else {// 需要物件充足
							// 收回任務需要物件 給予任務完成物件
							CreateNewItem.createNewItem(pc, 40529, 1, // 感謝信 x 1
									20230, 1);// 紅騎士盾牌 x 1
							// 將任務設置為結束
							QuestClass.get().endQuest(pc, KnightLv30_1.QUEST.get_id());
							// 恭喜你。 你終於成為一位有資格使用你身上武器的人類啊。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerardkE5"));
						}
					}
					break;

				default:// 其他
					isCloseList = true;
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
