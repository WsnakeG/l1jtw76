package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.WizardLv30_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 人形殭屍<BR>
 * 81108<BR>
 * 
 * @author dexc
 */
public class W30_HumanZombie extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(W30_HumanZombie.class);

	private W30_HumanZombie() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new W30_HumanZombie();
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
				// LV30任務已經完成
				if (pc.getQuest().isEnd(WizardLv30_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(WizardLv30_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40594)) { // 已經具有物品
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 40594, 1);// 僵屍鑰匙 x 1
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
