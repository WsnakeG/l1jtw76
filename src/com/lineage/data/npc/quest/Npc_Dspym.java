package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 迪嘉勒廷的男間諜<BR>
 * 80012<BR>
 * <BR>
 * 說明:取回間諜的報告書 (法師50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Dspym extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Dspym.class);

	private Npc_Dspym() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Dspym();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 對話動作
			npc.onTalkAction(pc);

			if (pc.isCrown()) {// 王族
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else if (pc.isKnight()) {// 騎士
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else if (pc.isElf()) {// 精靈
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else if (pc.isWizard()) {// 法師
				// 任務已經完成
				if (pc.getQuest().isEnd(WizardLv50_1.QUEST.get_id())) {
					// 啊～我從公爵聽到已收到報告書了
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym4"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= WizardLv50_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(WizardLv50_1.QUEST.get_id())) {
					case 0:// 達到0
							// 雖然我不認識你，但是請當做路過沒看到。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
						break;

					case 1:// 達到1
							// 你是...收到迪嘉勒廷公爵命令而來的那位？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym1"));
						break;

					default:// 其他
						// 怎麼樣了，有找到報告書了嗎？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym3"));
						break;
					}

				} else {
					// 雖然我不認識你，但是請當做路過沒看到。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
				}

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));

			} else {
				// 雖然我不認識你，但是請當做路過沒看到。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym5"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;
		if (npc.getMaster() != null) {
			return;
		}
		if (pc.isWizard()) {// 法師
			// 任務已經完成
			if (pc.getQuest().isEnd(WizardLv50_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("quest 27 dspym2")) {// 詢問可以幫忙什麼？
				// 提升任務進度
				pc.getQuest().set_step(WizardLv50_1.QUEST.get_id(), 2);
				// 我依照迪嘉勒廷公爵的指示
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dspym2"));
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
