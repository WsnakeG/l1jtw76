package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv45_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 羅吉<BR>
 * 70744<BR>
 * 說明:糾正錯誤的觀念 (黑暗妖精45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Roje extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Roje.class);

	private Npc_Roje() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Roje();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else if (pc.isKnight()) {// 騎士
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else if (pc.isElf()) {// 精靈
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else if (pc.isWizard()) {// 法師
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// LV45任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv45_1.QUEST.get_id())) {
					// 又是你這個臭東西出現在我眼前，我不會給你任何的幫忙。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje15"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= DarkElfLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(DarkElfLv45_1.QUEST.get_id())) {
					case 2:// 任務尚未開始
							// 我要證明你是錯的
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje11"));
						break;

					case 3:
						// 給予雪怪首級
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje12"));
						break;

					case 4:
						// 關於生鏽的刺客之劍
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje13"));
						break;

					default:// 達到1(任務開始)
						// 又是你這個臭東西出現在我眼前，我不會給你任何的幫忙。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje15"));
						break;
					}

				} else {
					// 我在反省過去的罪孽並且迎接嶄新的生活。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));
				}

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));

			} else {
				// 我在反省過去的罪孽並且迎接嶄新的生活。
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje16"));
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
			case 2:// 達到1
				if (cmd.equalsIgnoreCase("quest 19 roje12")) {// 我要證明你是錯的
					// 提升任務進度
					pc.getQuest().set_step(DarkElfLv45_1.QUEST.get_id(), 3);
					// 給予雪怪首級
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje12"));
				}
				break;

			case 3:// 達到2
				if (cmd.equalsIgnoreCase("request mark of assassin")) {// 給予雪怪首級
					final L1ItemInstance item = pc.getInventory().checkItemX(40584, 1);// 雪怪首級
																						// x
																						// 1

					if (item != null) {
						// 刪除道具(雪怪首級 x 1)
						pc.getInventory().removeItem(item, 1);
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40572, 1);// 刺客之證 x
																		// 1
						// 提升任務進度
						pc.getQuest().set_step(DarkElfLv45_1.QUEST.get_id(), 4);
						// 關於生鏽的刺客之劍
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje13"));

					} else {
						// 需要物件不足
						isCloseList = true;
						// 337 \f1%0不足%s。
						pc.sendPackets(new S_ServerMessage(337, "$2424 (1)"));
					}
				}
				break;

			case 4:// 達到3
				if (cmd.equalsIgnoreCase("quest 21 roje14")) {// 關於生鏽的刺客之劍
					// 提升任務進度
					pc.getQuest().set_step(DarkElfLv45_1.QUEST.get_id(), 5);
					// 你想知道關於生鏽的刺客之劍的事！
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "roje14"));
				}
				break;

			default:
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