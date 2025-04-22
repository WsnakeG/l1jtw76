package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv30_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 變種巨蟻女皇<BR>
 * 45342<BR>
 * 
 * @author dexc
 */
public class C30_MutantGiantQueenAnt extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(C30_MutantGiantQueenAnt.class);

	private C30_MutantGiantQueenAnt() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new C30_MutantGiantQueenAnt();
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
				if (pc.getQuest().isEnd(CrownLv30_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(CrownLv30_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40547)) { // 已經具有物品
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 40547, 1);// 村民的遺物
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
