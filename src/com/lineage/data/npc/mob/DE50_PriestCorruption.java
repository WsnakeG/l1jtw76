package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv50_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 墮落的司祭<BR>
 * 70972
 * 
 * @author dexc
 */
public class DE50_PriestCorruption extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(DE50_PriestCorruption.class);

	private DE50_PriestCorruption() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new DE50_PriestCorruption();
	}

	@Override
	public int type() {
		return 8;
	}

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);

			if (pc != null) {
				// 任務已經完成
				if (pc.getQuest().isEnd(DarkElfLv50_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(DarkElfLv50_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40600)) { // 已經具有物品
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 40600, 1);// 墮落鑰匙
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
