package com.lineage.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 背包物品自動保存
 * 
 * @author dexc
 */
public class PcAutoSaveInventoryTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PcAutoSaveInventoryTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<ClientExecutor> allClien = OnlineUser.get().all();
			// 不包含元素
			if (allClien.isEmpty()) {
				return;
			}

			for (final Iterator<ClientExecutor> iter = allClien.iterator(); iter.hasNext();) {
				final ClientExecutor client = iter.next();
				int time = client.get_saveInventory();
				if (time == -1) {
					continue;
				}
				time--;
				save(client, time);
			}

			/*
			 * for (final ClientExecutor client : allClien) { int time =
			 * client.get_saveInventory(); if (time == -1) { continue; } time--;
			 * save(client, time); }
			 */

		} catch (final Exception e) {
			_log.error("背包物品自動保存時間軸異常重啟", e);
			GeneralThreadPool.get().cancel(_timer, false);
			final PcAutoSaveInventoryTimer restart = new PcAutoSaveInventoryTimer();
			restart.start();
		}
	}

	/**
	 * 執行背包資料存檔
	 * 
	 * @param pc
	 */
	private void save(final ClientExecutor client, final Integer time) {
		try {
			if (client.get_socket() == null) {
				return;
			}
			if (time > 0) {
				// 更新
				client.set_saveInventory(time);

			} else {
				client.set_saveInventory(Config.AUTOSAVE_INTERVAL_INVENTORY);

				final L1PcInstance pc = client.getActiveChar();
				if (pc != null) {
					pc.saveInventory();
				}
			}

		} catch (final Exception e) {
			_log.debug("執行背包資料存檔處理異常 帳號:" + client.getAccountName());
		}
	}
}
