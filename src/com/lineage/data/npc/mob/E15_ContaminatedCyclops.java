package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv15_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 污濁獨眼巨人<BR>
 * 45137<BR>
 * 
 * @author dexc
 */
public class E15_ContaminatedCyclops extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(E15_ContaminatedCyclops.class);

	private E15_ContaminatedCyclops() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new E15_ContaminatedCyclops();
	}

	@Override
	public int type() {
		return 8;
	}

	private static Random _random = new Random();

	@Override
	public L1PcInstance death(final L1Character lastAttacker, final L1NpcInstance npc) {
		try {
			// 判斷主要攻擊者
			final L1PcInstance pc = CheckUtil.checkAtkPc(lastAttacker);

			if (pc != null) {
				// LV15任務已經完成
				if (pc.getQuest().isEnd(ElfLv15_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(ElfLv15_1.QUEST.get_id())) {
					if (_random.nextInt(100) < 25) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40696, 1);// 遠征隊的遺物
																		// x 1
					}
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
