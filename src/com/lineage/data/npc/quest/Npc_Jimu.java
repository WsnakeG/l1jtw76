package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 志武<BR>
 * 70715<BR>
 * 騎士的證明 (騎士45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Jimu extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Jimu.class);

	private Npc_Jimu() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Jimu();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else if (pc.isKnight()) {// 騎士
				// 任務已經完成
				if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
					// 你也是為了巨人的寶物而來的吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
					case 1:// 達到1(任務開始)
							// 看到巨人守護神的方法
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk1"));
						break;

					default:// 其他
						// 你也是為了巨人的寶物而來的吧！
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
						break;
					}
				} else {
					// 你也是為了巨人的寶物而來的吧！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
				}

			} else if (pc.isElf()) {// 精靈
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else if (pc.isWizard()) {// 法師
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));

			} else {
				// 你也是為了巨人的寶物而來的吧！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk4"));
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
			if (cmd.equalsIgnoreCase("quest 21 jimuk2")) {// 看到巨人守護神的方法
				// 任務進度
				switch (pc.getQuest().get_step(KnightLv45_1.QUEST.get_id())) {
				case 1:// 達到1(任務開始)
						// 想要看到巨人守護神必須要有強盜頭目所擁有的夜之視野...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "jimuk2"));
					// 提升任務進度
					pc.getQuest().set_step(KnightLv45_1.QUEST.get_id(), 2);
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
