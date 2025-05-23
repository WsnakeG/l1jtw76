package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.DarkElfLv30_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 背叛者<BR>
 * 45883-暗殺名單(古魯丁村) 45884-暗殺名單(燃柳村) 45885-暗殺名單(肯特村) 45886-暗殺名單(風木村)
 * 45887-暗殺名單(海音村) 45888-暗殺名單(亞丁城鎮) 45889-暗殺名單(奇巖村)
 * 
 * @author dexc
 */
public class DE30_Traitor extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(DE30_Traitor.class);

	private DE30_Traitor() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new DE30_Traitor();
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
				if (pc.getQuest().isEnd(DarkElfLv30_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(DarkElfLv30_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40596)) { // 已經具有物品
						return pc;
					}
					if (!pc.get_otherList().ATKNPC.contains(npc.getNpcId())) {
						pc.get_otherList().ATKNPC.add(npc.getNpcId());
					}
					if (pc.get_otherList().ATKNPC.size() >= 7) { // 殺死全部NPC完成
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40596, 1);// 死亡誓約 x
																		// 1
						pc.get_otherList().ATKNPC.clear();
						return pc;
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
