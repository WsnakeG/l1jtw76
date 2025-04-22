package com.lineage.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.templates.L1Account;

/**
 * 連線用戶管理
 * 
 * @author dexc
 */
public class OnlineUser {

	private static final Log _log = LogFactory.getLog(OnlineUser.class);

	private static OnlineUser _instance;

	// Map<K,V>
	private final Map<String, ClientExecutor> _clientList;

	private Collection<String> _allValues;

	private Collection<ClientExecutor> _allClient;

	public static OnlineUser get() {
		if (_instance == null) {
			_instance = new OnlineUser();
		}

		return _instance;
	}

	private OnlineUser() {
		_clientList = new ConcurrentHashMap<String, ClientExecutor>();
	}

	/**
	 * 增加連線用戶資料
	 * 
	 * @param value 帳號
	 * @param client 連線線程
	 * @return
	 */
	public boolean addClient(final L1Account value, final ClientExecutor client) {
		final String accountName = value.get_login();
		final ClientExecutor xclient = _clientList.get(accountName);
		if (xclient == null) {
			// 線程 帳戶 交叉設置
			client.setAccount(value);
			value.set_isLoad(true);
			value.set_server_no(Config.SERVERNO);
			AccountReading.get().updateLan(accountName, true);

			_clientList.put(accountName, client);
			_log.info("帳號登入: " + value.get_login() + " 目前連線帳號: " + _clientList.size());
			return true;

		} else {
			xclient.kick();
			client.kick();
			_log.error("連線列表中重複資料: " + value.get_login() + "\n");
			return false;
		}
	}

	/**
	 * 用戶連線中
	 * 
	 * @param accountName
	 * @return
	 */
	public boolean isLan(final String accountName) {
		final ClientExecutor client = _clientList.get(accountName);
		if (client != null) {
			return true;
		}
		return false;
	}

	/**
	 * 移除連線用戶資料
	 * 
	 * @param accountName
	 */
	public void remove(final String accountName) {
		final ClientExecutor xclient = _clientList.get(accountName);
		if (xclient != null) {
			final L1Account value = xclient.getAccount();
			value.set_isLoad(false);
			value.set_server_no(0);
			AccountReading.get().updateLan(accountName, false);

			_clientList.remove(accountName);
			// _log.info("\n 目前連線帳號: " + _clientList.size() + "\n");
		}
	}

	/**
	 * 取回連線用戶 ClientThread 資料
	 * 
	 * @param accountName
	 * @return 該帳戶未連線 傳回NULL
	 */
	public ClientExecutor get(final String accountName) {
		final ClientExecutor client = _clientList.get(accountName);
		return client;
	}

	/**
	 * 全部連線用戶(Map)
	 * 
	 * @return
	 */
	public Map<String, ClientExecutor> map() {
		return _clientList;
	}

	/**
	 * 全部連線用戶(Collection)
	 * 
	 * @return
	 */
	public Collection<ClientExecutor> all() {
		try {
			final Collection<ClientExecutor> vs = _allClient;
			return (vs != null) ? vs
					: (_allClient = Collections.unmodifiableCollection(_clientList.values()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * 傳回全部連線中帳戶
	 * 
	 * @return
	 */
	public Collection<String> getObject() {
		final Collection<String> vs = _allValues;
		return (vs != null) ? vs : (_allValues = Collections.unmodifiableCollection(_clientList.keySet()));
	}

	/**
	 * 是否已達最大連線數量
	 * 
	 * @return
	 */
	public boolean isMax() {
		if (_clientList.size() >= Config.MAX_ONLINE_USERS) {
			return true;
		}
		return false;
	}

	/**
	 * 連線數量
	 * 
	 * @return
	 */
	public int size() {
		return _clientList.size();
	}

	/**
	 * 中斷全部用戶
	 * 
	 * @return
	 */
	public void kickAll() {
		for (final String acc : _clientList.keySet()) {
			remove(acc);
		}
	}
}
