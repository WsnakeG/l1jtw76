package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DragonKnightLv30_1;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_CloseList;
import com.lineage.server.serverpackets.S_NPCTalkReturn;

/**
 * 妖魔密使(海音地監)<BR>
 * 84004<BR>
 * 
 * @author dexc
 */
public class DK30_OrcEmissaryA extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(DK30_OrcEmissaryA.class);

	private DK30_OrcEmissaryA() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new DK30_OrcEmissaryA();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			boolean isTak = false;
			if (pc.getTempCharGfx() == 6984) {// 妖魔密使
				isTak = true;
			}
			if (!isTak) {
				return;
			}

			if (pc.isCrown()) {// 王族

			} else if (pc.isKnight()) {// 騎士

			} else if (pc.isElf()) {// 精靈

			} else if (pc.isWizard()) {// 法師

			} else if (pc.isDarkelf()) {// 黑暗精靈

			} else if (pc.isDragonKnight()) {// 龍騎士
				if (pc.getQuest().isStart(DragonKnightLv30_1.QUEST.get_id())) {
					// 對話動作
					npc.onTalkAction(pc);
					// 調查結束了嗎？
					pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "spy_orc1"));
				}

			} else if (pc.isIllusionist()) {// 幻術師

			} else {
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
				if (cmd.equalsIgnoreCase("request flute of spy")) {// 交出妖魔密使的徽印
					final L1ItemInstance item = pc.getInventory().checkItemX(49223, 1);// 妖魔密使的徽印
					if (item != null) {
						pc.getInventory().removeItem(item, 1);// 刪除道具
						// 給予任務道具(妖魔密使之笛子)
						CreateNewItem.getQuestItem(pc, npc, 49222, 1);
					}
				}
			}
			isCloseList = true;

		} else {
			isCloseList = true;
		}

		if (isCloseList) {
			// 關閉對話窗
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
