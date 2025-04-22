package com.lineage.data.npc.mob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.executor.NpcExecutor;
import com.lineage.data.quest.ADLv80_3;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CheckUtil;

/**
 * 新風龍副本 守門員(B)<BR>
 * 
 * @author terry0412
 */
public class ADLv80_Keeper2 extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(ADLv80_Keeper2.class);

	private ADLv80_Keeper2() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new ADLv80_Keeper2();
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
				// 任務已經開始
				if (pc.getMapId() == ADLv80_3.MAPID) {
					// 取得任務道具
					CreateNewItem.getQuestItem(pc, npc, 56436, 1);
				}
			}
			return pc;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
