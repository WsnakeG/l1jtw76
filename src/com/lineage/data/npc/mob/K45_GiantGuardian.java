package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.KnightLv45_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 巨人守護神<BR>
 * 45302<BR>
 * 
 * @author dexc
 */
public class K45_GiantGuardian extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(K45_GiantGuardian.class);

	private K45_GiantGuardian() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new K45_GiantGuardian();
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
				// LV45任務已經完成
				if (pc.getQuest().isEnd(KnightLv45_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(KnightLv45_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40528)) { // 已經具有物品-守護神之袋
						return pc;
					}
					if (pc.getInventory().checkItem(40597)) { // 已經具有物品-破損的調查簿
						return pc;
					}
					if (pc.getInventory().checkItem(40537)) { // 已經具有物品-古代的遺物
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 40528, 1);// 守護神之袋 x 1
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
