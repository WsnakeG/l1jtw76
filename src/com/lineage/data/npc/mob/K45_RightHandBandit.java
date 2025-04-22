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
 * 強盜頭目<BR>
 * 45194<BR>
 * 
 * @author dexc
 */
public class K45_RightHandBandit extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(K45_RightHandBandit.class);

	private K45_RightHandBandit() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new K45_RightHandBandit();
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
					if (pc.getInventory().checkItem(20026)) { // 已經具有物品
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 20026, 1);// 夜之視野 x 1
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
