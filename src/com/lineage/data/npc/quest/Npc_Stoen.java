package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 神秘的岩石<BR>
 * 81105<BR>
 * 說明:法師的考驗 (法師45級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_Stoen extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Stoen.class);

	private Npc_Stoen() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Stoen();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else if (pc.isKnight()) {// 騎士
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else if (pc.isElf()) {// 精靈
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else if (pc.isWizard()) {// 法師
				// LV45任務已經完成
				if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
					// 喀喀喀，人類果然比較愚蠢啊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm3"));
					return;
				}

				// 等級達成要求
				if (pc.getLevel() >= WizardLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(WizardLv45_1.QUEST.get_id())) {
					case 1:// 任務開始
							// 咦？原來不是我要找的魔法師。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm1"));
						break;

					case 2:// 任務進度2
							// 遞給所要求的物品
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm2"));
						break;

					case 3:// 任務進度3
							// 喀喀喀，人類果然比較愚蠢啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm3"));
						break;

					default:
						// 咦？原來不是我要找的魔法師。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));
						break;
					}

				} else {
					// 咦？原來不是我要找的魔法師。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));

			} else {
				// 咦？原來不是我要找的魔法師。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isWizard()) {// 法師
			// LV45任務已經完成
			if (pc.getQuest().isEnd(WizardLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 19 stoenm2")) {// 神秘岩石的要求
				// 將任務進度提升為2
				pc.getQuest().set_step(WizardLv45_1.QUEST.get_id(), 2);
				// 遞給所要求的物品
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm2"));

			} else if (cmd.equalsIgnoreCase("request scroll about ancient evil")) {// 遞給所要求的物品
				final int[] items = new int[] { 40542, 40189 }; // 變形怪的血 x 1 魔法書
				// (魔法相消術) x 1
				final int[] counts = new int[] { 1, 1 };
				final int[] gitems = new int[] { 40536 };// 古代惡魔的記載 x 1
				final int[] gcounts = new int[] { 1 };

				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, items, counts) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, items, // 變形怪的血 x 1 魔法書
															// (魔法相消術) x 1
							counts, gitems, // 古代惡魔的記載 x 1
							1, gcounts);// 給予
										// 將任務進度提升為3
					pc.getQuest().set_step(WizardLv45_1.QUEST.get_id(), 3);
					// 喀喀喀，人類果然比較愚蠢啊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "stoenm3"));
				}
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
