package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 祭壇<BR>
 * 80094<BR>
 * 說明:艾爾摩戰場的軌跡 (幻術士30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Altar extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Altar.class);

	private Npc_Altar() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Altar();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isKnight()) {// 騎士
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isElf()) {// 精靈
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isWizard()) {// 法師
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));

			} else if (pc.isIllusionist()) {// 幻術師
				if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
					// 用虔誠的心祭拜死去的怨靈
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar1"));

				} else {
					// 證明你的勇氣吧，把該攜帶的物品帶來吧。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));
				}

			} else {
				// 證明你的勇氣吧，把該攜帶的物品帶來吧。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar2"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		int[] items = null;
		int[] counts = null;
		int[] gitems = null;
		int[] gcounts = null;

		if (cmd.equalsIgnoreCase("A")) {// 菊花花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41319 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("B")) {// 黛西花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41320 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("C")) {// 玫瑰花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41321 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("D")) {// 卡拉花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41322 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("E")) {// 太陽花花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41323 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("F")) {// 小蒼蘭花束(領取證明擊敗幽靈的勇士之證)
			items = new int[] { 41327, 41324 };
			counts = new int[] { 20, 1 };
			gitems = new int[] { 41325 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("G")) {// 菊花花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41319 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("H")) {// 黛西花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41320 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("I")) {// 玫瑰花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41321 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("J")) {// 卡拉花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41322 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("K")) {// 太陽花花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41323 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("L")) {// 小蒼蘭花束(領取證明擊敗哈蒙將軍怨靈的勇士之證)
			items = new int[] { 41328, 41324 };
			counts = new int[] { 1, 1 };
			gitems = new int[] { 41326 };
			gcounts = new int[] { 1 };

		} else if (cmd.equalsIgnoreCase("M")) {// 祭拜艾爾摩將軍
			if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
				// 需要物件不足(艾爾摩將軍之心以及一束菊花花束)
				if (CreateNewItem.checkNewItem(pc, new int[] { 49187, // 艾爾摩將軍之心
						41319,// 菊花花束
				}, new int[] { 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 粹取幽靈之魂的材料不足！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 49187, // 艾爾摩將軍之心
							41319,// 菊花花束
					}, new int[] { 1, 1, }, new int[] { 49188,// 49188 索夏依卡靈魂之心
							// 274,// 274 反王肯恩的權杖
					}, 1, new int[] { 1,
							// 1,
					});// 給予
					if (!pc.getInventory().checkItem(274)) { // 不具有物品
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 274, 1);// 反王肯恩的權杖
					}
					// 將任務進度提升為2
					pc.getQuest().set_step(IllusionistLv30_1.QUEST.get_id(), 2);
					// 已粹取艾爾摩大將軍的靈魂了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar9"));
				}

			} else {
				isCloseList = true;
			}

		} else if (cmd.equalsIgnoreCase("N")) {// 解開將軍遺物的封印
			if (pc.getQuest().isStart(IllusionistLv30_1.QUEST.get_id())) {
				// 需要物件不足(封印的索夏依卡遺物的封印/反王肯恩的權杖/卡拉花束)
				if (CreateNewItem.checkNewItem(pc,
						new int[] { 49190, // 49190
											// 封印的索夏依卡遺物
								274, // 274 反王肯恩的權杖
								41322,// 41322 卡拉花束
						}, new int[] { 1, 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 粹取幽靈之魂的材料不足！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc,
							new int[] { 49190, // 49190
												// 封印的索夏依卡遺物
									274, // 274 反王肯恩的權杖
									41322,// 41322 卡拉花束
							}, new int[] { 1, 1, 1, }, new int[] { 49191,// 49191
																			// 艾爾摩部隊日記
							}, 1, new int[] { 1, });// 給予
													// 將任務進度提升為3
					pc.getQuest().set_step(IllusionistLv30_1.QUEST.get_id(), 3);
					// 已經解開了被封印的索夏依卡遺物
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar10"));
				}

			} else {
				isCloseList = true;
			}

		} else {
			isCloseList = true;
		}

		if (items != null) {
			// 需要物件不足
			if (CreateNewItem.checkNewItem(pc, items, // 需要物件
					counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
				// 粹取幽靈之魂的材料不足！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar8"));

			} else {// 需要物件充足
				// 收回需要物件 給予完成物件
				CreateNewItem.createNewItem(pc, items, counts, // 需要
						gitems, 1, gcounts);// 給予
				// 你確實有勇士的資格。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "altar3"));
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
