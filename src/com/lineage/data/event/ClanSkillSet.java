package com.lineage.data.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.EventExecutor;
import com.lineage.server.templates.L1Event;
import com.lineage.server.timecontroller.event.ClanShowTimer;
import com.lineage.server.timecontroller.event.ClanSkillTimer;

/**
 * 血盟技能系統<BR>
 * 
 * @author dexc
 */
public class ClanSkillSet extends EventExecutor {

	private static final Log _log = LogFactory.getLog(ClanSkillSet.class);

	/**
	 *
	 */
	private ClanSkillSet() {
		// TODO Auto-generated constructor stub
	}

	public static EventExecutor get() {
		return new ClanSkillSet();
	}

	@Override
	public void execute(final L1Event event) {
		try {
			// 血盟技能使用時間時間軸
			final ClanSkillTimer useTimer = new ClanSkillTimer();
			useTimer.start();

			// 血盟技能光環 顯示處理
			final ClanShowTimer clanShowTimer = new ClanShowTimer();
			clanShowTimer.start();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
