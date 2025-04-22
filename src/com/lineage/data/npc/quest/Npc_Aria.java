package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 艾莉亞<BR>
 * 70783<BR>
 * 說明:艾莉亞的請求 (王族30級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Aria extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Aria.class);

	private Npc_Aria() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Aria();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// LV15任務未完成
				if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
					// 唉～雖然無法拯救被螞蟻抓走的村民
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv30_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
						// 願意協助村民
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria1"));

					} else {// 任務已經開始
						// 交出村莊居民的遺物
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria2"));
					}

				} else {
					// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));
				}

			} else if (pc.isKnight()) {// 騎士
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else if (pc.isElf()) {// 精靈
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else if (pc.isWizard()) {// 法師
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));

			} else {
				// 冒險者啊。 如果要經過沙漠時請小心巨蟻。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isCrown()) {// 王族
			// LV15任務未完成
			if (!pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
				return;
			}
			// LV30任務已經完成
			if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
				return;
			}
			// 任務尚未開始
			if (!pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("quest 13 aria2")) {// 願意協助村民
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, CrownLv30_1.QUEST.get_id());
					// 交出村莊居民的遺物
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria2"));
				}

			} else {// 任務已經開始
				if (cmd.equalsIgnoreCase("request questitem")) {// 交出村莊居民的遺物
					// 需要物件不足
					if (CreateNewItem.checkNewItem(pc, 40547, // 任務完成需要物件(村民的遺物
																// x 1)
							1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
						isCloseList = true;

					} else {// 需要物件充足
						// 收回任務需要物件 給予任務完成物件
						CreateNewItem.createNewItem(pc, 40547, 1, // 需要 x
																	// 1(村民的遺物)
								40570, 1);// 給予 x 1(艾莉亞的回報)

						// 將任務設置為結束
						QuestClass.get().endQuest(pc, CrownLv30_1.QUEST.get_id());
						// 唉～雖然無法拯救被螞蟻抓走的村民
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "aria3"));
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
}
