package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.QuestClass;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv15_1;
import com.lineage.data.quest.KnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 馬克<BR>
 * 70775<BR>
 * 說明:拯救被幽禁的吉姆 (騎士30級以上官方任務)<BR>
 * 
 * @author dexc
 */
public class Npc_Mark extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Mark.class);

	private Npc_Mark() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Mark();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else if (pc.isKnight()) {// 騎士
				// LV15任務未完成
				if (!pc.getQuest().isEnd(KnightLv15_1.QUEST.get_id())) {
					// 你現在有沒有能夠真心為對方付出的朋友呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
					return;
				}
				// LV30任務已經完成
				if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
					// 終於做到了。 真是恭喜你，願殷海薩祝福你...
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "markcg"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= KnightLv30_1.QUEST.get_questlevel()) {
					// 任務尚未開始
					if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
						// 關於吉姆
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark1"));

					} else {// 任務已經開始
						// 但請幫助我的好朋友吉姆，減輕他的痛苦
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark2"));
					}

				} else {
					// 你現在有沒有能夠真心為對方付出的朋友呢？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
				}

			} else if (pc.isElf()) {// 精靈
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else if (pc.isWizard()) {// 法師
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));

			} else {
				// 你現在有沒有能夠真心為對方付出的朋友呢？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (pc.isKnight()) {// 騎士
			// LV30任務已經完成
			if (pc.getQuest().isEnd(KnightLv30_1.QUEST.get_id())) {
				return;
			}

			// 任務尚未開始
			if (!pc.getQuest().isStart(KnightLv30_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("quest 13 mark2")) {// 關於吉姆
					// 將任務設置為執行中
					QuestClass.get().startQuest(pc, KnightLv30_1.QUEST.get_id());
					// 但請幫助我的好朋友吉姆，減輕他的痛苦
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "mark2"));
				}

			} else {
				isCloseList = true;
			}
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
