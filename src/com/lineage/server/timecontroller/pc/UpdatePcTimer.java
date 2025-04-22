package com.lineage.server.timecontroller.pc;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 可見物更新處理時間軸
 * 
 * @author roy
 */
public class UpdatePcTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(UpdatePcTimer.class);

	private ScheduledFuture<?> _timer;

	private L1PcInstance _pc;

	private boolean _isend = false;

	public void start(final L1PcInstance pc) {
		final int timeMillis = 300;// 0.30秒
		_timer = GeneralThreadPool.get().scheduleAtFixedRate(this, timeMillis, timeMillis);
		_pc = pc;
	}

	@Override
	public void run() {
		try {
			if (_pc == null) {
				_isend = true;
				return;
			}
			if (_pc.isTeleport()) {
				return;
			}

			if (_pc.isInCharReset()) {
				return;
			}

			if (_pc.getOnlineStatus() == 0) {
				_isend = true;
				return;
			}

			if (_pc.getNetConnection() == null) {
				_isend = true;
				return;
			}

			_pc.updateObject();
		} catch (final Exception e) {
			GeneralThreadPool.get().cancel(_timer, false);
			final UpdatePcTimer objectWTimer = new UpdatePcTimer();
			objectWTimer.start(_pc);
			_log.error("Pc (" + _pc.getName() + ")可見物更新處理時間軸異常重啟", e);
		} finally {
			if (_isend) {
				GeneralThreadPool.get().cancel(_timer, false);
			}
		}

	}

}
