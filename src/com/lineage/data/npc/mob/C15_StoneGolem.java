package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv15_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 高侖石頭怪<BR>
 * 45126<BR>
 * 
 * @author dexc
 */
public class C15_StoneGolem extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(C15_StoneGolem.class);

	private C15_StoneGolem() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new C15_StoneGolem();
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
				if (pc.getQuest().isEnd(CrownLv15_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(CrownLv15_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(40564)) { // 已經具有物品
						return pc;
					}
					if (_random.nextInt(100) < 40) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40564, 1);// 生命的卷軸 x
																		// 1
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
