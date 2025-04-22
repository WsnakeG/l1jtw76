package com.lineage.data.npc.mob;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.utils.CalcExp;

/**
 * 掛網區木人 (可自定義等級區間)
 * 
 * @author terry0412
 */
public class Scarecrow extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Scarecrow.class);

	private Scarecrow() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Scarecrow();
	}

	@Override
	public int type() {
		return 4;
	}

	/**
	 * NPC受到攻擊(4)<BR>
	 * 任務NPC作為抵達目標檢查的方法
	 * 
	 * @param pc
	 * @param npc
	 */
	@Override
	public void attack(final L1PcInstance pc, final L1NpcInstance npc) {
		try {
			// 判斷攻擊者
			if ((pc.getLevel() >= _minLevel) && (pc.getLevel() <= _maxLevel)) {
				final ArrayList<L1PcInstance> targetList = new ArrayList<L1PcInstance>();

				targetList.add(pc);
				final ArrayList<Integer> hateList = new ArrayList<Integer>();
				hateList.add(1);
				CalcExp.calcExp(pc, npc.getId(), targetList, hateList, npc.getExp());

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private int _minLevel;
	private int _maxLevel;

	@Override
	public void set_set(final String[] set) {
		try {
			_minLevel = Integer.parseInt(set[1]);
			_maxLevel = Integer.parseInt(set[2]);

		} catch (final Exception e) {
			_log.error("NPC掛網區木人設置錯誤:檢查CLASSNAME為Scarecrow的NPC設置!");
		}
	}
}
