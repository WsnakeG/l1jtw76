package com.lineage.server.datatables.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryIP;
import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.ConfigIpCheck;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.storage.IpCheckStorage;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.SQLUtil;

/**
 * IP驗證庫
 * 
 * @author dexc
 */
public class IpCheckTable implements IpCheckStorage {

	private static final Log _log = LogFactory.getLog(IpCheckTable.class);

	private static final Map<String, Integer> _lanList = new HashMap<String, Integer>();

	private void del() {
		// 清空資料庫紀錄
		Connection cn = null;
		PreparedStatement ps = null;
		try {
			cn = DatabaseFactoryIP.get().getConnection();
			ps = cn.prepareStatement("DELETE FROM `check_table`");
			ps.execute();

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
			LanSecurityManager.clear();
		}
	}

	@Override
	public void stsrt_cmd_tmp() {
		final LoadTmpIp removeIp = new LoadTmpIp();
		GeneralThreadPool.get().execute(removeIp);
	}

	private static final int _time = ConfigIpCheck.TIME;

	private static final int _error = ConfigIpCheck.ERROR;

	@Override
	public boolean check(final String ipaddr) {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			co = DatabaseFactoryIP.get().getConnection();
			final String sqlstr = "SELECT * FROM `check_table` WHERE `ip`=?";
			ps = co.prepareStatement(sqlstr);
			ps.setString(1, ipaddr);
			rs = ps.executeQuery();
			while (rs.next()) {
				/*
				 * if (_lanList.get(ipaddr) != null) { _lanList.remove(ipaddr);
				 * }
				 */
				return true;
			}

			final Integer count = _lanList.get(ipaddr);
			if (count == null) {
				_lanList.put(ipaddr, 1);

			} else {
				_lanList.put(ipaddr, count + 1);

				if ((count + 1) >= _error) {
					// 加入IP封鎖
					IpReading.get().add(ipaddr, "IP驗證錯誤次數:" + (count + 1));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return false;
	}

	private class LoadTmpIp implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(_time * 1000);
					del_ip();
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		private void del_ip() {
			try {
				del();

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
