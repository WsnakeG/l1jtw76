package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.IllusionistLv50_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 藍色靈魂之火<BR>
 * 70669<BR>
 * 說明:時空彼端的線索 (幻術士50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_BlueSoul extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_BlueSoul.class);

	private Npc_BlueSoul() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_BlueSoul();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族

			} else if (pc.isKnight()) {// 騎士
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));

			} else if (pc.isElf()) {// 精靈
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));

			} else if (pc.isWizard()) {// 法師
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));

			} else if (pc.isIllusionist()) {// 幻術師
				// LV50任務已經開始
				if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
					// 任務進度
					switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
					case 2:
						// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f1"));
						break;

					case 3:
						// 你想知道哪些事？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f2"));
						break;

					default:
						// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
						break;
					}

				} else {
					// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
				}

			} else {
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (pc.isIllusionist()) {// 幻術師
			// LV50任務已經開始
			if (pc.getQuest().isStart(IllusionistLv50_1.QUEST.get_id())) {
				// 任務進度
				switch (pc.getQuest().get_step(IllusionistLv50_1.QUEST.get_id())) {
				case 2:
					if (cmd.equalsIgnoreCase("a")) {// 供奉魔族之血
						// 需要物件不足
						if (CreateNewItem.checkNewItem(pc, new int[] { 49203, // 食腐獸之血
																				// 49203
								49204,// 翼龍之血 49204
						}, new int[] { 5, 5, }) < 1) {// 傳回可交換道具數小於1(需要物件不足)
							// 請去幫我取來<font fg=ffffaf>食腐獸之血</font>5個、<font
							// fg=ffffaf>翼龍之血</font>5個。
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f3"));

						} else {// 需要物件充足
							// 收回任務需要物件 給予任務完成物件
							CreateNewItem.createNewItem(pc, new int[] { 49203, // 食腐獸之血
																				// 49203
									49204,// 翼龍之血 49204
							}, new int[] { 5, 5, }, new int[] { 49207, // 靈魂之火灰燼
																		// 49207
									49208,// 藍色之火碎片 49208
							}, 1, new int[] { 1, 1, });// 給予

							// 提升任務進度
							pc.getQuest().set_step(IllusionistLv50_1.QUEST.get_id(), 3);
							// 你想知道些什麼？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "bluesoul_f2"));
						}
					}
					break;

				default:
					// 關閉對話窗
					pc.sendPackets(new S_CloseList(pc.getId()));
					break;
				}

			} else {
				// 關閉對話窗
				pc.sendPackets(new S_CloseList(pc.getId()));
			}
		}
	}
}
