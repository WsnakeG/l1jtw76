package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 麥<BR>
 * 70776<BR>
 * 說明:王族的信念 (王族45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Meg extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Meg.class);

	private Npc_Meg() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Meg();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// LV45任務已經完成
				if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
					// 我很想追隨像您這樣的君主。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CrownLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
						break;

					case 1:// 達到1(任務開始)
							// 被奪的靈魂
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg1"));
						break;

					case 2:// 達到2(被奪的靈魂)
					case 3:// 達到3(請接受這個)
					case 4:// 達到4(再次需要神秘袋子)
							// 遞給靈魂之證
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg2"));
						break;

					default:
						// 我很想追隨像您這樣的君主。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
						break;
					}

				} else {
					// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
				}

			} else if (pc.isKnight()) {// 騎士
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else if (pc.isElf()) {// 精靈
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else if (pc.isWizard()) {// 法師
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));

			} else {
				// 你是冒險者嗎？你有看到沙漠上的那個大洞嗎？那是巨大螞蟻生存的地方。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isCrown()) {// 王族
			// LV45任務已經完成
			if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 17 meg2")) {// 被奪的靈魂
				// 將任務進度提升為2
				pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 2);
				// 遞給靈魂之證
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg2"));

			} else if (cmd.equalsIgnoreCase("request royal family piece b")) {// 遞給靈魂之證
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc,
						// 40573 靈魂之證(白)妖精
						// 40574 靈魂之證(黑)法師
						// 40575 靈魂之證(紅)騎士
						new int[] { 40573, 40574, 40575 }, new int[] { 1, 1, 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 關閉對話窗
					pc.sendPackets(new S_CloseList(pc.getId()));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc,
							// 40573 靈魂之證(白)妖精
							// 40574 靈魂之證(黑)法師
							// 40575 靈魂之證(紅)騎士
							new int[] { 40573, 40574, 40575 }, new int[] { 1, 1, 1 }, new int[] { 40587 }, // 王族徽章的碎片B
																											// x
																											// 1
							1, new int[] { 1 });// 給予

					// 將任務進度提升為5
					pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 5);
					// 我很想追隨像您這樣的君主。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "meg3"));
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
