package com.lineage.data.npc.mob;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ALv15_1;
import com.lineage.data.quest.ElfLv15_2;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 阿吐巴妖魔<BR>
 * 45149<BR>
 * 
 * @author dexc
 */
public class A_AtubaOrc extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(A_AtubaOrc.class);

	private A_AtubaOrc() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new A_AtubaOrc();
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
				// 任務已經開始
				if (pc.getQuest().isStart(ElfLv15_2.QUEST.get_id())) {
					if (_random.nextInt(100) < 20) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40612, 1);// 阿吐巴妖魔魔法書
					}
				}
				// 任務已經開始
				if (pc.getQuest().isStart(ALv15_1.QUEST.get_id())) {
					if (_random.nextInt(100) < 10) {
						// 取得任務道具
						CreateNewItem.getQuestItem(pc, npc, 40135, 1);// 阿吐巴圖騰
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
