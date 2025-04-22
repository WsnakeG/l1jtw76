package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 白魔法師皮爾塔 <BR>
 * 71200<BR>
 * 說明:王族的信念 (王族45級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_Pieta extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Pieta.class);

	private Npc_Pieta() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Pieta();
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
					// 我知道你是為了那些沒被救援到的百姓
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta9"));
					return;
				}
				// 等級達成要求
				if (pc.getLevel() >= CrownLv45_1.QUEST.get_questlevel()) {
					// 任務進度
					switch (pc.getQuest().get_step(CrownLv45_1.QUEST.get_id())) {
					case 0:// 任務尚未開始
							// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta8"));
						break;

					case 1:// 達到1(任務開始)
					case 2:// 達到2(被奪的靈魂)
							// 當然任何事我都已有心理準備了。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta2"));
						// 將任務進度提升為3
						pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 3);
						break;

					case 3:// 達到3(請接受這個)
							// 請接受這個。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta4"));
						break;

					case 4:// 達到4(再次需要神秘袋子)
							// 再次需要神秘袋子
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta6"));
						break;

					default:
						// 我知道你是為了那些沒被救援到的百姓
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta9"));
						break;
					}

				} else {
					// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta8"));
				}

			} else if (pc.isKnight()) {// 騎士
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else if (pc.isElf()) {// 精靈
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else if (pc.isWizard()) {// 法師
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else if (pc.isIllusionist()) {// 幻術師
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));

			} else {
				// 我在研究人類靈魂的復活，人類靈魂一旦在肉體時間限制一到
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta1"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (pc.isCrown()) {// 王族
			// LV45任務已經完成
			if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
				return;
			}
			if (cmd.equalsIgnoreCase("a")) {// 請接受這個。
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc,
						// 失去光明的靈魂
						new int[] { 41422 }, new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 好像還沒找到失去光明的靈魂阿。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta10"));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc,
							// 失去光明的靈魂
							new int[] { 41422 }, new int[] { 1 }, new int[] { 40568 }, // 神秘的袋子
																						// x
																						// 1
							1, new int[] { 1 });// 給予

					// 將任務進度提升為5
					pc.getQuest().set_step(CrownLv45_1.QUEST.get_id(), 4);
					// 這就是.. 謝謝
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta5"));
				}

			} else if (cmd.equalsIgnoreCase("b")) {// 取來了
				// 需要物件不足
				if (CreateNewItem.checkNewItem(pc,
						// 失去光明的靈魂
						new int[] { 41422 }, new int[] { 1 }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
					// 好像還沒找到失去光明的靈魂阿。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta10"));

				} else {// 需要物件充足
					// 收回任務需要物件 給予任務完成物件
					CreateNewItem.createNewItem(pc,
							// 失去光明的靈魂
							new int[] { 41422 }, new int[] { 1 }, new int[] { 40568 }, // 神秘的袋子
																						// x
																						// 1
							1, new int[] { 1 });// 給予

					// 這就是.. 謝謝
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "pieta5"));
				}
			}
		}
	}
}
