package com.lineage.server.timecontroller.event;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 聊天計時器
 * 
 * @author dexc
 */
public class WorldChatTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(WorldChatTimer.class);

	private ScheduledFuture<?> _timer;

	private static final LinkedBlockingQueue<String> _queue = new LinkedBlockingQueue<String>();

	public void start() {
		final int timeMillis = 1000;// 15秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final String chat = _queue.poll();
			if (chat != null) {
				World.get().broadcastChatAll(new S_PacketBoxGree(0x02, chat));
				Thread.sleep(15000);
			}
		} catch (final Exception e) {
			_log.error("螢幕公告時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final WorldChatTimer timer = new WorldChatTimer();
			timer.start();
		}
	}

	public static void addchat(final String chat) throws InterruptedException {
		_queue.put(chat);
	}
}
