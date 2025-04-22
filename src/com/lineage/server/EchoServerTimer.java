package com.lineage.server;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ServerExecutor;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 監聽端口控制器<br>
 * 監聽端口啟動重置作業
 * 
 * @author dexc
 */
public class EchoServerTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(EchoServerTimer.class);

	private static EchoServerTimer _instance;

	private static final Map<Integer, ServerExecutor> _echoList = new HashMap<Integer, ServerExecutor>();

	public static EchoServerTimer get() {
		if (_instance == null) {
			_instance = new EchoServerTimer();
		}
		return _instance;
	}

	/**
	 * 啟動監聽
	 */
	public void start() {
		try {
			if (_echoList.isEmpty()) {
				startEcho();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			if (Config.RESTART_LOGIN > 0) {
				_log.warn("監聽端口重置作業啟動 間隔時間:" + Config.RESTART_LOGIN + "分鐘。");
				final int timeMillis = Config.RESTART_LOGIN * 60 * 1000;
				GeneralThreadPool.get().aiScheduleAtFixedRate(this, timeMillis, timeMillis);
			}
		}
	}

	@Override
	public void run() {
		try {
			_log.warn("監聽端口重置作業!");
			try {
				stopEcho();

				startEcho();

			} catch (final Exception e) {
				_log.error("重新啟動端口作業失敗!!", e);
			}

			// System.gc();

		} catch (final Exception e) {
			_log.error("監聽端口重置作業失敗!!", e);

		} finally {
			_log.warn("監聽端口重置作業完成!!");
		}
	}

	/**
	 * 重新啟動
	 */
	public void reStart() {
		try {
			if (Shutdown.SHUTDOWN) {
				return;
			}
			if (!_echoList.isEmpty()) {
				stopEcho();

				Thread.sleep(2000);

				startEcho();

			} else {
				_log.error("監聽端口重置作業失敗(目前無任何監聽線程)!!");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 啟用全部監聽
	 */
	public void startEcho() {
		try {
			final String[] portList = Config.GAME_SERVER_PORT.split("-");
			for (final String ports : portList) {
				final int key = Integer.parseInt(ports);
				final ServerExecutor echoServer = new ServerExecutor(key);
				if (echoServer != null) {
					_echoList.put(new Integer(key), echoServer);
					echoServer.stsrtEcho();
				}

				Thread.sleep(100);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 停止全部監聽
	 */
	public void stopEcho() {
		try {
			if (!_echoList.isEmpty()) {
				for (final Integer key : _echoList.keySet()) {
					final ServerExecutor echoServer = _echoList.get(key);
					if (echoServer != null) {
						echoServer.stopEcho();
					}

					Thread.sleep(100);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 指定端口是否已在列表中
	 * 
	 * @param port
	 * @return
	 */
	public boolean isPort(final int key) {
		try {
			return _echoList.get(new Integer(key)) != null;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

	/**
	 * 關閉指定監聽端口
	 * 
	 * @param port
	 */
	public void stopPort(final int key) {
		try {
			final ServerExecutor echoServer = _echoList.get(new Integer(key));
			if (echoServer != null) {
				echoServer.stopEcho();
				_echoList.remove(new Integer(key));

			} else {
				_log.warn("關閉指定監聽端口 作業失敗:該端口未在作用中!");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 啟用指定監聽端口
	 * 
	 * @param port
	 */
	public void startPort(final int key) {
		try {
			ServerExecutor echoServer = _echoList.get(new Integer(key));
			if (echoServer == null) {
				echoServer = new ServerExecutor(key);
				_echoList.put(new Integer(key), echoServer);
				echoServer.stsrtEcho();

			} else {
				_log.warn("啟用指定監聽端口 作業失敗:該端口已在作用中!");
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
