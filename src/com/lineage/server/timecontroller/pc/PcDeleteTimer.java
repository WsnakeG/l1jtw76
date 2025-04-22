package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.echo.ClientExecutor;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_PacketBoxSelect;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * PC 死亡刪除處理 時間軸
 * 
 * @author dexc
 */
public class PcDeleteTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PcDeleteTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 1100;// 1秒
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
				if (!tgpc.isDead()) {
					continue;
				}
				if (tgpc.get_delete_time() <= 0) {
					continue;
				}
				final int newtime = tgpc.get_delete_time() - 1;
				tgpc.set_delete_time(newtime);
				if (tgpc.get_delete_time() <= 0) {
					outPc(tgpc);
				}
			}

		} catch (final Exception e) {
			_log.error("PC 死亡刪除處理時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PcDeleteTimer pcHprTimer = new PcDeleteTimer();
			pcHprTimer.start();
		}
	}

	/**
	 * 登出死亡角色
	 * 
	 * @param tgpc
	 */
	private static void outPc(final L1PcInstance tgpc) {
		try {
			// 人物還在死亡狀態
			if (tgpc.isDead()) {
				final ClientExecutor client = tgpc.getNetConnection();
				// 改變顯示(復原正常)
				// tgpc.sendPacketsAll(new S_ChangeName(tgpc, false));
				client.quitGame();

				_log.info("角色死亡登出: " + tgpc.getName());
				// 返回人物選取畫面
				client.out().encrypt(new S_PacketBoxSelect());
			}

		} catch (final Exception e) {
			_log.error("登出死亡角色時發生異常!", e);
		}
	}
}
