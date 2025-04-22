package com.lineage.server.datatables.sql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryLogin;
import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigIpCheck;
import com.lineage.server.datatables.storage.IpStorage;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.utils.SQLUtil;

/**
 * 禁用ip/帳號資料
 * 
 * @author dexc
 */
public class IpTable implements IpStorage {

	private static final Log _log = LogFactory.getLog(IpTable.class);

	/**
	 * 作業系統是UBUNTU<BR>
	 * 加入UFY防火牆規則 - 加入禁止IP
	 */
	private void ufwDeny(final String key) {
		try {
			if (ConfigIpCheck.UFW) {
				// 要執行的命令
				final String command = "sudo ufw deny from " + key;
				final Process process = Runtime.getRuntime().exec(command);
				final BufferedReader input = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				for (String line = null; (line = input.readLine()) != null;) {
					_log.info("Linux 系統命令執行: 防火牆" + line);
				}
			}

		} catch (final IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 作業系統是UBUNTU<BR>
	 * 加入UFY防火牆規則 - 移出禁止IP
	 */
	private void ufwDeleteDeny(final String key) {
		try {
			// 要執行的命令
			final String command = "sudo ufw delete deny from " + key;
			final Process process = Runtime.getRuntime().exec(command);
			final BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line = null; (line = input.readLine()) != null;) {
				_log.info("Linux 系統命令執行: 防火牆" + line);
			}

		} catch (final IOException e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 初始化載入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			cn = DatabaseFactoryLogin.get().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `ban_ip`");

			rs = ps.executeQuery();

			while (rs.next()) {
				final String key = rs.getString("ip");
				if (key.lastIndexOf(".") != -1) {
					if (!LanSecurityManager.BANIPMAP.containsKey(key)) {
						// 加入IP封鎖
						LanSecurityManager.BANIPMAP.put(key, 100);
					}

				} else {
					if (!LanSecurityManager.BANNAMEMAP.containsKey(key)) {
						// 加入NAME封鎖
						LanSecurityManager.BANNAMEMAP.put(key, 100);
					}
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("載入禁止登入IP資料數量: " + LanSecurityManager.BANIPMAP.size() + "(" + timer.get() + "ms)");
		_log.info("載入禁止登入NAME資料數量: " + LanSecurityManager.BANNAMEMAP.size() + "(" + timer.get() + "ms)");
	}

	/**
	 * 加入禁止位置
	 * 
	 * @param key
	 * @param info 原因
	 */
	@Override
	public void add(final String key, final String info) {
		boolean isBan = false;// 已經是禁止清單
		if (key.lastIndexOf(".") != -1) {
			if (!LanSecurityManager.BANIPMAP.containsKey(key)) {
				// 加入IP封鎖
				LanSecurityManager.BANIPMAP.put(key, 100);
				isBan = true;

				// 作業系統是UBUNTU
				if (Config.ISUBUNTU) {
					ufwDeny(key);
				}
			}

		} else {
			if (!LanSecurityManager.BANNAMEMAP.containsKey(key)) {
				// 加入NAME封鎖
				LanSecurityManager.BANNAMEMAP.put(key, 100);
				isBan = true;
			}
		}

		if (check(key)) {// 已經在SQL資料表中
			isBan = false;
		}

		// 已確定不在SQL資料表中
		if (isBan) {
			Connection con = null;
			PreparedStatement pstm = null;
			try {
				// 建立禁止資料
				con = DatabaseFactoryLogin.get().getConnection();
				pstm = con.prepareStatement("INSERT INTO `ban_ip` SET `ip`=?,`info`=?,`datetime`=SYSDATE()");
				int i = 0;
				pstm.setString(++i, key);
				pstm.setString(++i, info);

				pstm.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	/**
	 * 移出禁止位置
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public void remove(final String key) {
		boolean isBan = false;// 已經是禁止清單
		if (key.lastIndexOf(".") != -1) {
			if (LanSecurityManager.BANIPMAP.containsKey(key)) {
				LanSecurityManager.BANIPMAP.remove(key);
				isBan = true;

				// 作業系統是UBUNTU
				if (Config.ISUBUNTU) {
					_log.info("******Linux 系統命令執行**************************");
					ufwDeleteDeny(key);
					_log.info("******Linux 系統命令完成**************************");
				}
			}

		} else {
			if (LanSecurityManager.BANNAMEMAP.containsKey(key)) {
				LanSecurityManager.BANNAMEMAP.remove(key);
				isBan = true;
			}
		}

		if (!check(key)) {// 已經不在SQL資料表中
			isBan = false;
		}

		// 已確定在SQL資料表中
		if (isBan) {
			Connection con = null;
			PreparedStatement pstm = null;

			try {
				con = DatabaseFactoryLogin.get().getConnection();
				pstm = con.prepareStatement("DELETE FROM `ban_ip` WHERE `ip`=?");
				pstm.setString(1, key);

				pstm.execute();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);

			} finally {
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}

	/**
	 * 檢查該位置 是否在資料表中
	 * 
	 * @param key
	 * @return
	 */
	private boolean check(final String key) {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			cn = DatabaseFactoryLogin.get().getConnection();
			final String sqlstr = "SELECT * FROM `ban_ip` WHERE `ip` LIKE '%" + key + "%'";
			ps = cn.prepareStatement(sqlstr);
			rs = ps.executeQuery();

			while (rs.next()) {
				return true;
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		return false;
	}
}
