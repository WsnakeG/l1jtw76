package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv50_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 紅色靈魂之火<BR>
 * 80138<BR>
 * 說明:時空彼端的線索 (龍騎士50級以上官方任務)
 * 
 * @author dexc
 */
public class Npc_RedSoul extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_RedSoul.class);

	private Npc_RedSoul() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_RedSoul();
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
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));

			} else if (pc.isElf()) {// 精靈
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));

			} else if (pc.isWizard()) {// 法師
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				// LV50任務已經開始
				if (pc.getQuest().isStart(DragonKnightLv50_1.QUEST.get_id())) {
					// 任務進度
					switch (pc.getQuest().get_step(DragonKnightLv50_1.QUEST.get_id())) {
					case 2:
						// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f1"));
						break;

					case 3:
						// 你想知道哪些事？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f2"));
						break;

					default:
						// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
						break;
					}

				} else {
					// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
				}

			} else if (pc.isIllusionist()) {// 幻術師
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));

			} else {
				// 哇嗚嗚嗚嗚嗚...是誰叫醒我的？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f4"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (pc.isDragonKnight()) {// 龍騎士
			// LV50任務已經開始
			if (pc.getQuest().isStart(DragonKnightLv50_1.QUEST.get_id())) {
				// 任務進度
				switch (pc.getQuest().get_step(DragonKnightLv50_1.QUEST.get_id())) {
				case 2:
				case 3:
					if (cmd.equalsIgnoreCase("a")) {// 奉獻異界邪念粉末
						final L1ItemInstance item = pc.getInventory().checkItemX(49229, 10);// 異界邪念粉末
																							// 49229
						if (item != null) {
							pc.getInventory().removeItem(item, 10);// 刪除道具

							// 你想知道些什麼？
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f2"));

							// 提升任務進度
							pc.getQuest().set_step(DragonKnightLv50_1.QUEST.get_id(), 3);

							// 給予任務道具(靈魂之火灰燼)
							CreateNewItem.getQuestItem(pc, npc, 49207, 1);
							// 給予任務道具(紅色之火碎片)
							CreateNewItem.getQuestItem(pc, npc, 49227, 1);

						} else {
							// 可用<font fg=ffffaf>異界邪念粉末</font>10個進行儀式
							pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "redsoul_f3"));
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
