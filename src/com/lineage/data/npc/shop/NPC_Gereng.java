package com.lineage.data.npc.shop;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_1;
import com.lineage.data.quest.WizardLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 吉倫<BR>
 * 70009<BR>
 * 說明:不死族的叛徒 (法師30級以上官方任務)
 * 
 * @author dexc
 */
public class NPC_Gereng extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(NPC_Gereng.class);

	private static Random _random = new Random();

	/**
	 *
	 */
	private NPC_Gereng() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new NPC_Gereng();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你是擁有杜克血脈的後裔？哈哈哈...嗯~好吧，你說的也許是事實。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengp1"));

			} else if (pc.isKnight()) {// 騎士
				// 啊！甘特的學生，為什麼你會來到這裡？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengk1"));

			} else if (pc.isElf()) {// 精靈
				// 嗯~我從沒想到我會在一個如此荒涼的地方遇見一個妖精。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerenge1"));

			} else if (pc.isWizard()) {// 法師
				// LV15任務未完成
				if (!pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
					final int htmlid = _random.nextInt(6) + 1;
					// 很高興看到你。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengTe" + htmlid));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					// 歡迎啊。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw5"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= WizardLv30_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(WizardLv30_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 接受吉倫的建議
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT1"));
						break;

					case 1:// 達到1(交出不死族的骨頭)
							// 交出不死族的骨頭
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT2"));
						break;

					case 2:// 達到2(被奪的靈魂)
							// 取得魔法師必須的物品
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT3"));
						break;

					case 3:// 達到3(請接受這個)
							// 交給神秘水晶球
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT4"));
						break;

					default:
						// 歡迎啊。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengw5"));
						break;
					}
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你是黑暗的一族，但你在追求光明
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengde1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 呵呵～這不是貝希摩斯的龍術士嗎？真心歡迎你～
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengdk1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 哇～哇～哇～這不是席琳的子孫嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengi1"));
			} else if (pc.isWarrior()) {// 戰士
				// 哇～哇～哇～這不是席琳的子孫嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengTe1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isWizard()) {// 法師
			// LV15任務未完成
			if (!pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
				return;
			}
			// LV30任務已經完成
			if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 12 gerengT2")) {// 接受吉倫的建議
				// 將任務設置為執行中
				QuestClass.get().startQuest(pc, WizardLv30_1.QUEST.get_id());
				// 交出不死族的骨頭
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT2"));

			} else if (cmd.equalsIgnoreCase("request bone piece of undead")) {// 交出不死族的骨頭
				// 任務完成需要物件(不死族骨頭x 1)
				final L1ItemInstance tgitem = pc.getInventory().checkItemX(40579, 1);
				if (tgitem != null) {// 需要物件充足
					if (pc.getInventory().removeItem(tgitem, 1) == 1) {// 刪除道具
						// 將任務進度提升為2
						pc.getQuest().set_step(WizardLv30_1.QUEST.get_id(), 2);
						// 取得魔法師必須的物品
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT3"));
					}

				} else {
					// 337：\f1%0不足%s。 不死族的骨頭
					pc.sendPackets(new S_ServerMessage(337, "$2033 (1)"));
					isCloseList = true;
				}

			} else if (cmd.equalsIgnoreCase("quest 14 gerengT4")) {// 取得魔法師必須的物品
				// 將任務進度提升為3
				pc.getQuest().set_step(WizardLv30_1.QUEST.get_id(), 3);
				// 取得魔法師必須的物品
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gerengT4"));

			} else if (cmd.equalsIgnoreCase("request mystery staff")) {// 交給神秘水晶球
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, 40567, // 任務完成需要物件(神秘水晶球x 1)
						1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40567 }, // 神秘水晶球x
																			// 1
							new int[] { 1 }, new int[] { 40580, 40569 }, // 不死族的骨頭碎片
																			// x
																			// 1
																			// 神秘魔杖
																			// x
																			// 1
							1, new int[] { 1, 1 });// 給予

					// 將任務進度提升為4
					pc.getQuest().set_step(WizardLv30_1.QUEST.get_id(), 4);
					// 關閉對話窗
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
