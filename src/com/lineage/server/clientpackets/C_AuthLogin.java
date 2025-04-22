package com.lineage.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.list.OnlineUser;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.serverpackets.S_CommonNews;
import com.lineage.server.serverpackets.S_Disconnect;
import com.lineage.server.serverpackets.S_LoginResult;
import com.lineage.server.templates.L1Account;
import com.lineage.server.thread.GeneralThreadPool;

/**
 * 要求登入伺服器
 * 
 * @author dexc
 */
public class C_AuthLogin extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_AuthLogin.class);

	/*
	 * public C_AuthLogin() { } public C_AuthLogin(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	/*
	 * private static final String[] _check_accname = new String[]{
	 * "a","b","c","d"
	 * ,"e","f","g","h","i","j","k","l","m","n","o","p","q","r","s"
	 * ,"t","u","v","w","x","y","z", "0","1","2","3","4","5","6","7","8","9", };
	 * private static final String[] _check_pwd = new String[]{
	 * "a","b","c","d","e"
	 * ,"f","g","h","i","j","k","l","m","n","o","p","q","r","s"
	 * ,"t","u","v","w","x","y","z", "0","1","2","3","4","5","6","7","8","9",
	 * "!","_","=","+","-","*","^","#","?",".", };
	 */

	private static final String _check_accname = "abcdefghijklmnopqrstuvwxyz0123456789";

	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			boolean iserror = false;
			// 登入名稱轉為小寫
			final String loginName = readS().toLowerCase();
			if (loginName.length() > 12) {
				_log.warn("不合法的帳號長度:" + client.getIp().toString() + "/" + loginName);
				client.set_error(client.get_error() + 1);
				return;
			}
			for (int i = 0; i < loginName.length(); i++) {
				final String ch = loginName.substring(i, i + 1);
				if (!_check_accname.contains(ch)) {
					_log.warn("不被允許的帳號字元!");
					iserror = true;
					break;
				}
			}
			final String password = readS();
			if (password.length() > 13) {
				_log.warn("不合法的密碼長度:" + client.getIp().toString());
				client.set_error(client.get_error() + 1);
				return;
			}
			for (int i = 0; i < password.length(); i++) {
				final String ch = password.substring(i, i + 1);
				if (!_check_pwd.contains(ch.toLowerCase())) {
					_log.warn("不被允許的密碼字元!");
					iserror = true;
					break;
				}
			}

			if (!iserror) {
				checkLogin(client, loginName, password, false);

			} else {
				client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_PASS_CHECK));
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	public void checkLogin(final ClientExecutor client, final String loginName, final String password,
			final boolean auto) {
		try {
			if (loginName == null) {
				return;
			}
			if (loginName.equals("")) {
				return;
			}
			if (password == null) {
				return;
			}
			if (password.equals("")) {
				return;
			}
			final StringBuilder ip = client.getIp();
			StringBuilder mac = client.getMac();

			// 帳號禁止登入
			if (LanSecurityManager.BANNAMEMAP.containsKey(loginName)) {
				_log.warn("禁止登入帳號位置: account=" + loginName + " host=" + client.getIp());
				client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_CANT_USE));
				final KickTimeController kickTime = new KickTimeController(client, null);
				kickTime.schedule();
				return;
			}

			boolean isError = false;
			L1Account account = AccountReading.get().getAccount(loginName);

			if (account == null) {
				if (Config.AUTO_CREATE_ACCOUNTS) {
					if (mac == null) {
						mac = ip;
					}
					account = AccountReading.get().create(loginName, password, ip.toString(), mac.toString(),
							"未建立超級密碼");

				} else {
					if (auto) {
						client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_ERROR_USER));
						return;
					}
					client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCESS_FAILED));
					isError = true;
				}
			}

			// 驗證密碼
			if (!account.get_password().equals(password) && !isError) {
				if (auto) {
					client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_ERROR_PASS));
				} else {
					client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCESS_FAILED));
				}
				isError = true;
			}

			// 人數上限
			if (OnlineUser.get().isMax() && !isError) {
				_log.info("人數已達上限");
				client.out().encrypt(new S_LoginResult(S_LoginResult.EVENT_LAN_ERROR));
				isError = true;
			}

			if (isError) {
				final int error = client.get_error();
				client.set_error(error + 1);
				return;
			}

			final ClientExecutor inGame = OnlineUser.get().get(loginName);

			if (inGame != null) {
				_log.info("相同帳號重複登入: account=" + loginName + " host=" + ip);
				client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
				final KickTimeController kickTime = new KickTimeController(client, inGame);
				kickTime.schedule();

			} else {
				if ((account.get_server_no() != 0) && (account.get_server_no() != Config.SERVERNO)) {
					_log.info("帳號登入其他服務器: account=" + loginName + " host=" + ip + " 已經登入:"
							+ account.get_server_no() + "伺服器");
					client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
					final KickTimeController kickTime = new KickTimeController(client, null);
					kickTime.schedule();
					return;
				}
				// 帳號已經登入
				if (account.is_isLoad()) {
					_log.info("相同帳號重複登入: account=" + loginName + " host=" + ip);
					client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
					final KickTimeController kickTime = new KickTimeController(client, null);
					kickTime.schedule();
					return;
				}
				// 增加連線用戶資料
				if (OnlineUser.get().addClient(account, client)) {
					account.set_ip(ip.toString());
					if (mac != null) {
						account.set_mac(mac.toString());
					}
					AccountReading.get().updateLastActive(account);

					client.setAccount(account);
					// 註冊測試
					// client.out().encrypt(new
					// S_LoginResult(S_LoginResult.EVENT_REGISTER_ACCOUNTS));

					client.out().encrypt(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));

					if (Config.NEWS) {
						// 顯示公告
						client.out().encrypt(new S_CommonNews());

					} else {
						// 進入人物列表
						final C_CommonClick common = new C_CommonClick();
						common.start(null, client);
					}

					// 測試公告視窗
					/*
					 * ATest test = new ATest(); test.start(client);//
					 */
				}
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 斷線延遲軸
	 * 
	 * @author dexc
	 */
	private class KickTimeController implements Runnable {

		private ClientExecutor _kick1 = null;

		private ClientExecutor _kick2 = null;

		private KickTimeController(final ClientExecutor kick1, final ClientExecutor kick2) {
			_kick1 = kick1;
			_kick2 = kick2;
		}

		private void schedule() {
			GeneralThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
				_kick1.out().encrypt(new S_Disconnect());
				Thread.sleep(1000);
				_kick1.set_error(10);

				if (_kick2 != null) {
					_kick2.set_error(10);
				}

				// XXX
				// this.finalize();

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);

				/*
				 * } catch (Throwable e) { _log.error(e.getLocalizedMessage(),
				 * e);
				 */
			}
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}