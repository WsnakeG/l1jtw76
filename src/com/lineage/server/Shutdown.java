package com.lineage.server;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.event.MarySet;
import com.lineage.data.npc.gam.Npc_Mary;
import com.lineage.echo.ClientExecutor;
import com.lineage.echo.QuitGame;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.CharMapsTimeReading;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;

/**
 * 關機管理
 * 
 * @author dexc
 */
public class Shutdown extends Thread {

	private static final Log _log = LogFactory.getLog(Shutdown.class);

	private static Shutdown _instance;

	private static Shutdown _counterInstance = null;

	private int _secondsShut;

	private int _shutdownMode;

	private int _overTime = 5;

	public static final int SIGTERM = 0;// 即時關機

	public static final int GM_SHUTDOWN = 1;// 倒數關機

	public static final int GM_RESTART = 2;// 重新啟動

	public static final int ABORT = 3;// 取消指令

	public static boolean SHUTDOWN = false;

	public Shutdown() {
		_secondsShut = -1;
		_shutdownMode = SIGTERM;
	}

	/**
	 * <font color=#00800>執行關機讀秒</font>
	 * 
	 * @param seconds 時間:單位(秒)
	 * @param mode 關機模式(true關機後重新啟動 false關機後不重新啟動)
	 */
	public Shutdown(int seconds, final boolean restart) {
		if (seconds < 0) {
			seconds = 0;
		}
		_secondsShut = seconds;
		if (restart) {
			_shutdownMode = GM_RESTART;
		} else {
			_shutdownMode = GM_SHUTDOWN;
		}
	}

	public static Shutdown getInstance() {
		if (_instance == null) {
			_instance = new Shutdown();
		}
		return _instance;
	}

	@Override
	public void run() {
		if (this == _instance) {
			if (_shutdownMode != ABORT) {
				try {
					saveData();
					Thread.sleep(3000);
					while (_overTime > 0) {
						_log.warn("距離核心完全關閉 : " + _overTime + "秒!");
						_overTime--;
						Thread.sleep(1000);
					}

					final int list = OnlineUser.get().size();
					_log.warn("核心關閉殘餘連線帳號數量: " + list);
					Thread.sleep(1000);
					if (list > 0) {
						for (final String account : OnlineUser.get().map().keySet()) {
							_log.warn("核心關閉殘留帳號: " + account);
						}
					}

				} catch (final InterruptedException e) {
					_log.error(e.getLocalizedMessage(), e);
				}
			}
			// mysqldump -u wcnc -p smgp_apps_wcnc > wcnc.sql2
		} else {
			countdown();
			if (_shutdownMode != ABORT) {
				switch (_shutdownMode) {
				case GM_SHUTDOWN:
					_instance.setMode(GM_SHUTDOWN);
					System.exit(0);
					break;

				case GM_RESTART:
					_instance.setMode(GM_RESTART);
					System.exit(1);
					break;
				}
			}
		}
	}

	/**
	 * <font color=#00800>啟動關機計時</font>
	 * 
	 * @param pc 指令執行者
	 * @param seconds 時間:單位(秒)
	 * @param mode 關機模式(true關機後重新啟動 false關機後不重新啟動)
	 */
	public void startShutdown(final L1PcInstance activeChar, final int seconds, final boolean restart) {
		if (_counterInstance != null) {
			return;
		}
		if (activeChar != null) {
			_log.warn(activeChar.getName() + " 啟動關機計時: " + seconds + " 秒!");
		}
		if (_counterInstance != null) {
			_counterInstance._abort();
		}

		_counterInstance = new Shutdown(seconds, restart);
		GeneralThreadPool.get().execute(_counterInstance);
	}

	/**
	 * <font color=#00800>取消關機計時</font>
	 * 
	 * @param pc 指令執行者
	 */
	public void abort(final L1PcInstance activeChar) {
		if (_counterInstance != null) {
			_counterInstance._abort();
		}
	}

	/**
	 * <font color=#00800>設定關機模式為(取消)</font>
	 */
	private void setMode(final int mode) {
		_shutdownMode = mode;
	}

	/**
	 * set shutdown mode to ABORT
	 */
	private void _abort() {
		_shutdownMode = ABORT;
	}

	/**
	 * <font color=#00800>關機倒數 以及 倒數公告 如果關機模式變更為(取消) 停止運作</font>
	 */
	private void countdown() {
		try {
			SHUTDOWN = true;
			// 產生訊息封包 (72 \f3伺服器將會在 %0 秒後關機。請玩家先行離線。)
			World.get().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
			while (_secondsShut > 0) {
				switch (_secondsShut) {
				case 300:
				case 240:
				case 180:
					// 停止端口監聽
					EchoServerTimer.get().stopEcho();
					break;

				case 150:
				case 120:
				case 60:
				case 30:
				case 10:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					// 服務端訊息
					_log.warn("關機倒數: " + _secondsShut + " 秒!");
					// 產生訊息封包 (72 \f3伺服器將會在 %0 秒後關機。請玩家先行離線。)
					World.get().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
					break;
				}

				_secondsShut--;

				// 每秒計算一次
				Thread.sleep(1000);

				if (_shutdownMode == ABORT) {
					if (_secondsShut > 5) {
						SHUTDOWN = false;
						// 監聽端口啟動重置作業
						EchoServerTimer.get().reStart();
						// 產生訊息封包 (3063 取消關機倒數!!遊戲將會正常執行!!)
						World.get().broadcastPacketToAll(new S_ServerMessage(166, "取消關機倒數!!遊戲將會正常執行!!"));
						return;
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			if (_shutdownMode != ABORT) {
				saveData();

			} else {
				_secondsShut = -1;
				_shutdownMode = SIGTERM;
				_counterInstance = null;
			}
		}
	}

	private static boolean _isMsg = false;

	/**
	 * 線上人物資料的存檔
	 */
	private synchronized void saveData() {
		try {
			// 小瑪莉系統資料紀錄
			if (MarySet.START) {
				Npc_Mary.update();
			}
			World.get().broadcastPacketToAll(new S_Disconnect());

			final Collection<ClientExecutor> list = OnlineUser.get().all();

			if (!_isMsg) {
				_isMsg = true;
				_log.info("人物/物品 資料的存檔 - 關閉核心前連線帳號數量: " + list.size());
			}
			for (final ClientExecutor client : list) {
				final L1PcInstance tgpc = client.getActiveChar();
				if (tgpc != null) {
					QuitGame.quitGame(tgpc);
					client.setActiveChar(null);
					client.kick();
					client.close();
				}

				final L1Account value = client.getAccount();
				if (value != null) {
					if (value.is_isLoad()) {
						// 帳號連線狀態 移出連線列表
						OnlineUser.get().remove(client.getAccountName());
					}
					value.set_isLoad(false);
					AccountReading.get().updateLan(client.getAccountName(), false);
				}

				// final Save save = new Save(client);
				// GeneralThreadPool.get().execute(save);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			// 全部設置離線
			AccountReading.get().updateLan();
			CharMapsTimeReading.get().saveAllTime();
			ServerReading.get().isStop();// 設置服務器為關機
		}
	}

	/*
	 * private class Save extends Thread { private ClientExecutor _client;
	 * public Save(final ClientExecutor client) { _client = client; }
	 * @Override public void run() { try { final L1PcInstance tgpc =
	 * _client.getActiveChar(); if (tgpc != null) { QuitGame.quitGame(tgpc);
	 * _client.setActiveChar(null); _client.kick(); _client.close(); } final
	 * L1Account value = _client.getAccount(); if (value != null) { if
	 * (value.is_isLoad()) { // 帳號連線狀態 移出連線列表
	 * OnlineUser.get().remove(_client.getAccountName()); }
	 * value.set_isLoad(false);
	 * AccountReading.get().updateLan(_client.getAccountName(), false); } }
	 * catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } } }
	 */
}