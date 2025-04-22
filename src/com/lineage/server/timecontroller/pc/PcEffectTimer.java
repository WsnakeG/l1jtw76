package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.ProtectorSet;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * PC 特效編號時間軸 (每隔幾秒出現1次)
 * 
 * @author terry0412
 */
public class PcEffectTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PcEffectTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 3000; // 3秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = World.get().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();

				/** 使用`S_EffectLocation`避免人物異常斷線 */

				// 特效編號 (每XX秒出現1次) by terry0412
				final int effectId = tgpc.isProtector() ? ProtectorSet.EFFECT_ID : tgpc.getEffectId();
				if (effectId > 0) {
					tgpc.sendPacketsAll(new S_EffectLocation(tgpc.getX(), tgpc.getY(), effectId));
				}
			}

		} catch (final Exception e) {
			_log.error("PC特效編號時間軸 異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PcEffectTimer pcEffectTimer = new PcEffectTimer();
			pcEffectTimer.start();
		}
	}
}
