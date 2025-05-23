package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ElfLv50_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 巨大兵蟻<BR>
 * 45190<BR>
 * 
 * @author dexc
 */
public class E50_GiantAntSoldier extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(E50_GiantAntSoldier.class);

	private E50_GiantAntSoldier() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new E50_GiantAntSoldier();
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
				switch (pc.getMapId()) {
				case 43:// 巨蟻洞穴
				case 44:// 巨蟻洞穴
				case 45:// 巨蟻洞穴
				case 46:// 巨蟻洞穴
				case 47:// 巨蟻洞穴
				case 48:// 巨蟻洞穴
				case 49:// 巨蟻洞穴
				case 50:// 巨蟻洞穴
				case 51:// 巨蟻洞穴
					// LV50任務已經完成
					if (pc.getQuest().isEnd(ElfLv50_1.QUEST.get_id())) {
						return pc;
					}
					// 任務已經開始
					if (pc.getQuest().isStart(ElfLv50_1.QUEST.get_id())) {
						if (pc.getInventory().checkItem(49162)) { // 已經具有物品
							return pc;
						}
						// 任務進度
						switch (pc.getQuest().get_step(ElfLv50_1.QUEST.get_id())) {
						case 1:
							if (_random.nextInt(100) < 40) {
								// 取得任務道具
								CreateNewItem.getQuestItem(pc, npc, 49162, 1);// 古代黑妖的秘笈
							}
							break;
						}
					}
					break;
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
