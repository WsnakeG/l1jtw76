package com.lineage.server.timecontroller.quest;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.serverpackets.S_HelpMessage;
import com.lineage.server.serverpackets.S_PackBoxMaptime;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldQuest;

/**
 * 副本任務可執行時間
 * 
 * @author dexc
 */
public class QuestTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(QuestTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1000;// 1秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 全部副本任務
			final Collection<L1QuestUser> allQuest = WorldQuest.get().all();
			// 不包含元素
			if (allQuest.isEmpty()) {
				return;
			}

			for (final L1QuestUser quest : allQuest) {
				if (quest.get_time() <= -1) {
					continue;
				}
				setQuest(quest);
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.error("副本任務可執行時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final QuestTimer questTimer = new QuestTimer();
			questTimer.start();
		}
	}

	private static void setQuest(final L1QuestUser quest) {
		try {
			// 任務時間
			switch (quest.get_time()) {
			case 3600:// 60分鐘
			case 1800:// 30分鐘
			case 900:// 15分鐘
			case 600:// 10分鐘
			case 300:// 5分鐘
			case 240:// 4分鐘
			case 180:// 3分鐘
			case 120:// 2分鐘
			case 60:// 1分鐘
				// 14:副本任務完成時間限制-剩餘：%s分
				quest.sendPackets(new S_PackBoxMaptime((quest.get_time() * 1)));
				quest.sendPackets(new S_HelpMessage("\\F2副本任務-剩餘時間：" + (quest.get_time() / 60)));
				break;

			case 30:// 30秒
			case 15:// 15秒
			case 10:// 10秒
			case 5:// 5秒
			case 4:// 4秒
			case 3:// 3秒
			case 2:// 2秒
			case 1:// 1秒
					// 15:副本任務完成時間限制-剩餘：%s秒
				quest.sendPackets(new S_PackBoxMaptime((quest.get_time() * 1)));
				quest.sendPackets(new S_HelpMessage("\\F2副本任務-剩餘時間：" + quest.get_time()));
				break;
			}

			// 時間倒數設置
			quest.set_time(quest.get_time() - 1);

			// 時間已為0
			if (quest.get_time() == 0) {
				// 3112:\f1[系統訊息] 超過任務可執行時間！！任務結束！
				quest.sendPackets(new S_SystemMessage("\\F2[系統訊息] 超過任務可執行時間！！任務結束！"));
				// 時間設置-1
				quest.set_time(-1);
				// 結束任務
				quest.endQuest();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
