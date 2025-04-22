package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 巨人長老<BR>
 * 70711<BR>
 * 騎士的證明 (騎士45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Giant extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Giant.class);

	private Npc_Giant() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Giant();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
					// 巨人守護神的陰謀啊...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
					case 0:// 達到0
					case 1:// 達到1
							// 喂！你是不是來搶奪古代的遺物！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));
						break;

					case 2:// 達到2
							// 尋找調查員
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk1"));
						break;

					case 3:// 達到3
							// 遞給古代的遺物
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk2"));
						break;

					default:// 其他
						// 巨人守護神的陰謀啊...
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk3"));
						break;
					}
				} else {
					// 喂！你是不是來搶奪古代的遺物！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));
				}

			} else if (pc.isElf()) {// 精靈
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else if (pc.isWizard()) {// 法師
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));

			} else {
				// 喂！你是不是來搶奪古代的遺物！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isKnight()) {// 騎士
			// 任務已經完成
			if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 23 giantk2")) {// 尋找調查員
				// 遞給古代的遺物
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk2"));
				// 提升任務進度
				pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 3);

			} else if (cmd.equalsIgnoreCase("request head part of ancient key")) {// 遞給古代的遺物
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc, 40537, // 任務完成需要物件(古代的遺物 x 1)
						1) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					isCloseList = true;

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc, 40537, 1, // 古代的遺物 x 1
							40534, 1);// 古代鑰匙 x 1
					// 提升任務進度
					pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 4);
					// 巨人守護神的陰謀啊...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "giantk3"));
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
