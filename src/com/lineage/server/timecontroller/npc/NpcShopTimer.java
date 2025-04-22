package com.lineage.server.timecontroller.npc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.WorldDe;

/**
 * Npc 虛擬商店時間軸
 * 
 * @author dexc
 */
public class NpcShopTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcShopTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 8 * 1000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1DeInstance> allNpc = WorldDe.get().all();
			// 不包含元素
			if (allNpc.isEmpty()) {
				return;
			}

			for (final Iterator<L1DeInstance> iter = allNpc.iterator(); iter.hasNext();) {
				final L1DeInstance de = iter.next();
				if (de.destroyed() && de.isDead()) {// 死亡
					continue;
				}
				if (de.getCurrentHp() <= 0) {// 目前HP小於0
					continue;
				}

				if (de.isShop()) {
					de.shopChat();
				}
				if (de.get_chat() != null) {
					de.globalChat();
				}
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.error("Npc 虛擬商店時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final NpcShopTimer shopTimer = new NpcShopTimer();
			shopTimer.start();
		}
	}
}
