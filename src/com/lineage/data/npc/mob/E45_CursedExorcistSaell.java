package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv45_2;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 受詛咒的巫女莎爾<BR>
 * 45941<BR>
 * 
 * @author dexc
 */
public class E45_CursedExorcistSaell extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(E45_CursedExorcistSaell.class);

	private E45_CursedExorcistSaell() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new E45_CursedExorcistSaell();
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
				if (pc.getQuest().isEnd(ElfLv45_2.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(ElfLv45_2.QUEST.get_id())) {
					if (pc.getInventory().checkItem(41349)) { // 已經具有物品
						return pc;
					}
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 41349, 1);// 莎爾之戒 x 1
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
