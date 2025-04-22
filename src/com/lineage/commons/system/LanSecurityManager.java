package com.lineage.commons.system;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;//IP封包驗證策略 R
import java.util.concurrent.CopyOnWriteArrayList;

import com.lineage.config.ConfigIpCheck;
import com.lineage.server.datatables.lock.IpCheckReading;

/**
 * 安全管理器
 * 
 * @author dexc
 */
public class LanSecurityManager extends SecurityManager {

	/** IP封包驗證策略 */
	private static final CopyOnWriteArrayList<String> _iplist = new CopyOnWriteArrayList<String>();

	/** IP封包驗證策略-R */
	public static final Map<String, Integer> BANIPPACK = new ConcurrentHashMap<String, Integer>();

	/** 禁止連線IP位置 */
	public static final Map<String, Integer> BANIPMAP = new HashMap<String, Integer>();

	/** 禁止連線NAME位置 */
	public static final Map<String, Integer> BANNAMEMAP = new HashMap<String, Integer>();

	public static void clear() {
		_iplist.clear();
	}

	/**
	 * 如果不允許調用線程從指定的主機和埠號接受套接字連接，則拋出 SecurityException。
	 */
	@Override
	public void checkAccept(final String host, final int port) {
		// 禁止IP
		if (BANIPMAP.containsKey(host)) {
			throw new SecurityException();
		}
		// IP封包驗證策略-R
		if (BANIPPACK.containsKey(host)) {
			throw new SecurityException();
		}
		if (ConfigIpCheck.IPTABLE) {
			// IP驗證庫
			if (!_iplist.contains(host)) {
				// IP驗證庫
				if (!IpCheckReading.get().check(host)) {
					throw new SecurityException();

				} else {
					_iplist.add(host);
				}
			}
		}
	}

	/**
	 * 如果不允許調用線程修改 thread 參數，則拋出 SecurityException
	 */
	@Override
	public void checkAccess(final Thread t) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 如果基於當前有效的安全策略，不允許執行根據給定許可權所指定的請求訪問，則拋出 SecurityException。
	 */
	@Override
	public void checkPermission(final Permission perm) {
		// TODO Auto-generated constructor stub
	}
}
