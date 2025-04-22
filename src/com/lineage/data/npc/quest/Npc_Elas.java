package com.lineage.data.npc.quest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 愛爾菈絲<BR>
 * 85019<BR>
 * 
 * @author dexc
 */
public class Npc_Elas extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_Elas.class);

	private Npc_Elas() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Elas();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			if (pc.isCrown()) {// 王族
				// 這位兄弟，你有什麼事嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));

			} else if (pc.isKnight()) {// 騎士
				// 這位兄弟，你有什麼事嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));

			} else if (pc.isElf()) {// 精靈
				// 這位兄弟，你有什麼事嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));

			} else if (pc.isWizard()) {// 法師
				// 這位兄弟，你有什麼事嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));

			} else if (pc.isDarkelf()) {// 黑暗精靈
				// 這位兄弟，你有什麼事嗎？
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas2"));

			} else if (pc.isDragonKnight()) {// 龍騎士
				if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
					// 接受妖魔密使變形卷軸
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas1"));

				} else {
					// 亞丁 上頭的黑烏雲實在有點可怕。
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas6"));
				}

			} else if (pc.isIllusionist()) {// 幻術師
				// 我是守護<font fg=ffff0>貝希摩斯</font>的龍騎士之一
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas3"));

			} else {
				// 我是守護<font fg=ffff0>貝希摩斯</font>的龍騎士之一
				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas3"));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		boolean isCloseList = false;

		if (pc.isDragonKnight()) {// 龍騎士
			if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
				if (cmd.equalsIgnoreCase("a")) {// 接受妖魔密使變形卷軸
					if (pc.getInventory().checkItem(49220)) { // 已經具有物品
						// 已經提供給你<font fg=ffffaf>妖魔密使變形卷軸</font>了喔。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas5"));

					} else {
						// 給予任務道具(妖魔密使變形卷軸)
						CreateNewItem.getQuestItem(pc, npc, 49220, 1);
						// <font fg=ffffaf>妖魔密使變形卷軸</font>在這裡。
						pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "elas4"));
					}
				}

			} else {
				isCloseList = true;
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
