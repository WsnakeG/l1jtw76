package com.lineage.echo.to;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ChatGlobal;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.StreamUtil;
import com.lineage.server.world.World;

/**
 * 監聽廣播服務端
 * 
 * @author dexc
 */
public class TServer implements Runnable {

	private static final Log _log = LogFactory.getLog(TServer.class);

	private static TServer _ins;

	private static Socket _ser;// 連線的服務端

	private static InputStream _inp;// 輸入流

	private static OutputStream _out = null;// 輸出流

	public static TServer get() {
		if (_ins == null) {
			_ins = new TServer();
		}
		return _ins;
	}

	public TServer() {
		try {
			if (Config.CHAT_SERVER_HOST_NAME != null) {
				_ser = new Socket(Config.CHAT_SERVER_HOST_NAME, Config.CHAT_SERVER_PORT);
				_inp = _ser.getInputStream();
				_out = new BufferedOutputStream(_ser.getOutputStream());
			}

		} catch (final Exception e) {
			// _log.error("無法連線廣播服務器!");
			_inp = null;
			_out = null;
			_ser = null;
			_ins = null;
		}
	}

	public void start_cmd() {
		GeneralThreadPool.get().execute(this);
	}

	@Override
	public void run() {
		try {
			while (true) {
				try {
					final byte[] encrypt = readPacket();
					if (encrypt.length > 1) {
						final String chatText = new String(encrypt, "utf-8");
						if (chatText.startsWith("[******]")) {// GM
							World.get().broadcastPacketToAll(new S_ChatGlobal(chatText));

						} else {
							for (final L1PcInstance listner : World.get().getAllPlayers()) {
								// 拒絕接收廣播頻道
								if (!listner.isShowWorldChat()) {
									continue;
								}
								Thread.sleep(10);
								listner.sendPackets(new S_ChatGlobal(chatText));
							}
						}
					}

				} catch (final Exception e) {
					break;
				}
			}

		} catch (final Exception e) {
			_log.error("廣播服務器連線中斷!");

		} finally {
			stopS();
		}
	}

	private byte[] readPacket() throws Exception {
		try {
			final int hi = _inp.read();
			final int lo = _inp.read();
			if (lo < 0) {
				throw new RuntimeException();
			}
			final int l = ((lo << 8) + hi) - 2;

			final byte[] d = new byte[l];

			int size = 0;

			for (int i = 0; (i != -1) && (size < l); size += i) {
				i = _inp.read(d, size, l - size);
			}

			if (size != l) {
				throw new RuntimeException();
			}
			return d;

		} catch (final Exception e) {
			throw e;
		}
	}

	public void outServer(final byte[] encrypt) {
		if (_out == null) {
			return;
		}
		synchronized (this) {
			try {
				final int j = encrypt.length + 2;
				_out.write(j & 0xff);
				_out.write((j >> 8) & 0xff);
				_out.write(encrypt);
				_out.flush();

			} catch (final Exception e) {
			}
		}
	}

	public void stopS() {
		try {
			if (_inp != null) {
				StreamUtil.close(_inp);
			}
			if (_out != null) {
				StreamUtil.close(_out);
			}
			if (_ser != null) {
				StreamUtil.close(_ser);
			}
			_inp = null;
			_out = null;
			_ser = null;
			_ins = null;

		} catch (final Exception e) {
			// e.fillInStackTrace();
		}
	}
}
