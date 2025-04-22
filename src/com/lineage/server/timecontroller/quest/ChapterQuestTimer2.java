package com.lineage.server.timecontroller.quest;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldQuest;

/**
 * @author terry0412
 */
public class ChapterQuestTimer2 extends TimerTask {

	private static final Log _log = LogFactory.getLog(ChapterQuestTimer2.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000; // 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1QuestUser> allQuest = WorldQuest.get().all();
			if (allQuest.isEmpty()) {
				return;
			}

			for (final L1QuestUser quest : allQuest) {
				if (quest.get_orimR() == null) {
					continue;
				}
				final L1PcInstance leader = quest.get_orimR().party.getLeader();
				if ((leader.getX() == 32799) && (leader.getY() == 32808)) {
					if (leader.get_actionId() == ActionCodes.ACTION_Think) {
						quest.get_orimR().attack();
						leader.set_actionId(ActionCodes.ACTION_Walk);
					} else if (leader.get_actionId() == ActionCodes.ACTION_Cheer) {
						quest.get_orimR().defense();
						leader.set_actionId(ActionCodes.ACTION_Walk);
					}
				} else if ((leader.get_actionId() == ActionCodes.ACTION_Think)
						|| (leader.get_actionId() == ActionCodes.ACTION_Cheer)) {
					leader.set_actionId(ActionCodes.ACTION_Walk);
				}
				if (quest.get_orimR().portal != null) {
					for (final L1PcInstance member : quest.pcList()) {
						quest.get_orimR().teleport(member, quest.get_orimR().getCabinLocation());
					}
				}
				quest.get_orimR().calcScore();

				Thread.sleep(1);
			}
		} catch (final Exception e) {
			_log.error("副本任務檢查時間軸<海戰副本>異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final ChapterQuestTimer2 questTimer = new ChapterQuestTimer2();
			questTimer.start();
		}
	}
}
