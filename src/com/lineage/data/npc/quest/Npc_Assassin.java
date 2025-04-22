package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 刺客首領護衛<BR>
 * 70824<BR>
 * 說明:糾正錯誤的觀念 (黑暗妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Assassin extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Assassin.class);

	private Npc_Assassin() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Assassin();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			boolean isTak = false;
			if (pc.getTempCharGfx() == 3634) {// 刺客
				isTak = true;
			}
			if (!isTak) {
				// 刺客首領會解決掉你這傢伙！
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin4"));
				return;
			}

			if (pc.isCrown()) {// 王族
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else if (pc.isKnight()) {// 騎士
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else if (pc.isElf()) {// 精靈
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else if (pc.isWizard()) {// 法師
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
					// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
					case 1:// 達到1(任務開始)
							// 如何才能見到刺客首領
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin1"));
						break;

					default:
						// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
						break;
					}

				} else {
					// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));

			} else {
				// 傳說中的”刺客首領”正在與黑暗妖精研討改變世界的計劃。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isDarkelf()) {// 黑暗精靈
			// LV45-1任務已經完成
			if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
				return;
			}
			// LV45-1任務進度
			switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
			case 1:// 達到1
				if (cmd.equalsIgnoreCase("quest 18 assassin2")) {// 如何才能見到刺客首領
					// 提升任務進度
					pc.getQuest().set_step(DarkElfLv45_1.QUEST.get_id(), 2);
					// 每個人都想見到他，但必須要有羅吉手上的刺客之證才能見到他。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "assassin2"));
				}
				break;

			default:// 達到1
				isCloseList = true;
				break;
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