package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 靈魂的獵食者<BR>
 * 46041<BR>
 * 
 * @author dexc
 */
public class C45_DevourerSouls extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(C45_DevourerSouls.class);

	private C45_DevourerSouls() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new C45_DevourerSouls();
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
				// LV45任務已經完成
				if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
					return pc;
				}
				// 任務已經開始
				if (pc.getQuest().isStart(CrownLv45_1.QUEST.get_id())) {
					if (pc.getInventory().checkItem(41422)) { // 已經具有物品
						return pc;
					}
					if (_random.nextInt(100) < 40) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 41422, 1);// 失去光明的靈魂
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
