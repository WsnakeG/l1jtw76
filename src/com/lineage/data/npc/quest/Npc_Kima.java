package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 奇馬<BR>
 * 70906<BR>
 * 說明:尋找黑暗之星 (黑暗妖精50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Kima extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Kima.class);

	private Npc_Kima() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Kima();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else if (pc.isKnight()) {// 騎士
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else if (pc.isElf()) {// 精靈
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else if (pc.isWizard()) {// 法師
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV50任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
					// 你活著的原因是什麼？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
					return;
				}
				// 等級達成要求(LV50-1)
				if (pc.getLevel() >= DarkElfLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 你活著的原因是什麼？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
						break;

					case 1:
					case 2:
						// 嗯...你就是布魯迪卡新收養的傢伙嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq1"));
						break;

					case 3:
						// 關於靈魂枯竭的土地
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq3"));
						break;

					default:
						// 靈魂枯竭的土地上是不會有生命體的存在。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq4"));
						break;
					}

				} else {
					// 你活著的原因是什麼？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));

			} else {
				// 你活著的原因是什麼？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kima1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isDarkelf()) {// 黑暗精靈
			// LV50任務已經完成
			if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
				isCloseList = true;

			} else {
				// 任務進度
				switch (pc.getQuest().get_step(DarkElfLv50_1.QUEST.get_id())) {
				case 1:
				case 2:
					if (cmd.equalsIgnoreCase("request mask of true")) {// 遞給調查結果物
						final L1ItemInstance item = pc.getInventory().checkItemX(40583, 1);// 安迪亞之信
						if (item == null) {
							// 337 \f1%0不足%s。
							pc.sendPackets(new S_ServerMessage(337, "$2654 (1)"));
							isCloseList = true;

						} else {
							pc.getInventory().removeItem(item, 1);// 刪除道具
							// 提升任務進度
							pc.getQuest().set_step(DarkElfLv50_1.QUEST.get_id(), 3);
							// 關於靈魂枯竭的土地
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq3"));
						}
					}
					break;

				case 3:
					if (cmd.equalsIgnoreCase("quest 26 kimaq4")) {// 關於靈魂枯竭的土地
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 20037, 1);// 真實的面具
						// 提升任務進度
						pc.getQuest().set_step(DarkElfLv50_1.QUEST.get_id(), 4);
						// 靈魂枯竭的土地上是不會有生命體的存在。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "kimaq4"));
					}
					break;

				default:
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