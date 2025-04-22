package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv15_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 詹姆<BR>
 * 70531<BR>
 * 說明:詹姆的請求 (法師15級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Jem extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jem.class);

	private Npc_Jem() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jem();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else if (pc.isKnight()) {// 騎士
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else if (pc.isElf()) {// 精靈
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else if (pc.isWizard()) {// 法師
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
					// 如果想要了解更多魔法的事情，請去找吉倫吧。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));

				} else {
					// 等級達成要求
					if (pc.getLevel() >= WizardLv15_1.QUEST.get_questlevel()) {
						// 任務進度
						switch (pc.getQuest().get_step(WizardLv15_1.QUEST.get_id())) {
						case 0:// 任務尚未開始
								// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem1"));

							// 將任務設置為啟動
							QuestClass.get().startQuest(pc, WizardLv15_1.QUEST.get_id());
							break;

						case 1:// 達到1(任務開始)
								// 關於死亡的靈魂
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem1"));
							break;

						case 2:// 達到2(任務開始)
								// 交給骷髏頭及受詛咒的魔法書
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem4"));
							break;
						}

					} else {
						// 所有生物在死亡時，會變成什麼樣子呢？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem2"));
					}
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));

			} else {
				// 所有生物在死亡時，會變成什麼樣子呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem7"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isWizard()) {// 法師
			// 任務已經完成
			if (pc.getQuest().isEnd(WizardLv15_1.QUEST.get_id())) {
				return;
			}

			if (cmd.equalsIgnoreCase("request cursed spellbook")) {// 交給食屍鬼的牙齒與指甲。
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv15_1.QUEST.get_id())) {
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, new int[] { 40539, // 食屍鬼的牙齒
							40538,// 食屍鬼的指甲
					}, new int[] { 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						// 關閉對話窗
						pc.sendPackets(new S_CloseList(pc.getId()));

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, new int[] { 40539, // 食屍鬼的牙齒
								40538,// 食屍鬼的指甲
						}, new int[] { 1, 1, }, new int[] { 40591,// 受詛咒的魔法書 x 1
						}, 1, new int[] { 1, });// 給予

						// 提升任務進度
						pc.getQuest().set_step(WizardLv15_1.QUEST.get_id(), 2);
						// 交給骷髏頭及受詛咒的魔法書
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem4"));
					}
				}

			} else if (cmd.equalsIgnoreCase("request book of magical powers")) {// 交給骷髏頭及受詛咒的魔法書。
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, new int[] { 40605, // 骷髏頭
						40591,// 受詛咒的魔法書 x 1
				}, new int[] { 1, 1, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					pc.sendPackets(new S_CloseList(pc.getId()));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, new int[] { 40605, // 骷髏頭
							40591,// 受詛咒的魔法書 x 1
					}, new int[] { 1, 1, }, new int[] { 20226,// 魔法能量之書 x 1
					}, 1, new int[] { 1, });// 給予

					// 將任務設置為結束
					QuestClass.get().endQuest(pc, WizardLv15_1.QUEST.get_id());
					// 如果想要了解更多魔法的事情，請去找吉倫吧。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jem6"));
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
