package com.lineage.echo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.commons.system.LanSecurityManager;// R
import com.lineage.config.Config;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.StreamUtil;

/**
 * 端口監聽
 * 
 * @author dexc
 */
public class ServerExecutor extends Thread {

	private static final Log _log = LogFactory.getLog(ServerExecutor.class);

	// 服務器監聽套接
	private ServerSocket _server;

	private int _port = 0;

	private static final String _t1 = "\n\r--------------------------------------------------";

	private static final String _t2 = "\n\r--------------------------------------------------";

	/**
	 * 端口監聽
	 */
	public ServerExecutor(final int port) {
		try {
			_port = port;

			if (!"*".equals(Config.GAME_SERVER_HOST_NAME)) {
				final InetAddress inetaddress = InetAddress.getByName(Config.GAME_SERVER_HOST_NAME);
				_server = new ServerSocket(_port, 50, inetaddress);

			} else {
				_server = new ServerSocket(_port);
			}

		} catch (final SocketTimeoutException e) {
			_log.fatal("連線超時:(" + _port + ")", e);

		} catch (final IOException e) {
			_log.fatal("IP位置加載錯誤 或 端口位置已被佔用:(" + _port + ")", e);

		} finally {
			_log.info("[D] " + getClass().getSimpleName() + " 開始監聽服務端口:(" + _port + ")");
		}
	}

	/**
	 * 啟動監聽
	 * 
	 * @throws IOException
	 */
	public void stsrtEcho() throws IOException {
		GeneralThreadPool.get().execute(this);
	}

	@Override
	public void run() {
		try {
			while (_server != null) {
				Socket socket = null;
				try {
					socket = _server.accept();

					if (socket != null) {
						socket.setSoTimeout(Config.VerSec);// IP封包驗證策略-R

						final String ipaddr = socket.getInetAddress().getHostAddress();

						LanSecurityManager.BANIPPACK.put(ipaddr, 300);// IP封包驗證策略-R

						// START 注解後對減輕CC負擔會有幫助
						final String info = _t1 + "\n       客戶端 連線遊戲伺服器 服務端口:(" + _port + ")" + "\n       "
								+ ipaddr + _t2;

						_log.info(info);// END

						final ClientExecutor client = new ClientExecutor(socket);
						GeneralThreadPool.get().execute(client);
					}

				} catch (final SecurityException e) {
					// _log.warn(e.getLocalizedMessage());
				}
			}

		} catch (final IOException e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			final String lanInfo = "[D] " + getClass().getSimpleName() + " 服務器核心關閉監聽端口(" + _port + ")";

			_log.warn(lanInfo);
		}
	}

	/**
	 * 停止監聽
	 */
	public void stopEcho() {
		try {
			if (_server != null) {
				StreamUtil.close(_server);
				StreamUtil.interrupt(this);
				_server = null;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {

		}
	}
}