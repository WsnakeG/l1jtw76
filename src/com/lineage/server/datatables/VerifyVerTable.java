package com.lineage.server.datatables;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.DatabaseFactoryVerify;
import com.lineage.config.Config;
import com.lineage.server.utils.SQLUtil;

public class VerifyVerTable {
	private static final Log log = LogFactory.getLog(VerifyVerTable.class);
	private static VerifyVerTable _instance;

	public static VerifyVerTable get() {
		if (_instance == null) {
			_instance = new VerifyVerTable();
		}
		return _instance;
	}

	public void load(final int server_ver) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean use = false;
		try {
			con = DatabaseFactoryVerify.get().getConnection();
			pstm = con.prepareStatement("SELECT * FROM server_ver ORDER BY user_ver");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int user_ver = rs.getInt("user_ver");
				final long user_host = rs.getLong("user_host");
				final String used = rs.getString("used");
				final int ver = rs.getInt("ver");

				if (server_ver == user_ver) {
					// 若設定為 false 則不給予執行開服
					if (used.equalsIgnoreCase("false")) {
						use = false;
						System.out.println("授權已過期，請聯繫管理人員。");
						break;
					}
					// 連接轉換為10進位IP大於0 以及相同於客戶端驗證IP位置予以通過
					if (user_host > 0) {
						final long ip = ipToLong(getIpAddress());
						if (user_host == ip) {
							use = true;
						}
					}
					// 當客戶端版本驗證不等於資料庫設定時予以取消連接
					if (Config.ServerVer != ver) {
						use = false;
						System.out.println("版本號不符合，請聯繫管理人員。");
						break;
					}
					// 當連接轉換10進位IP不等於0時予以取消連接
					if (user_host != 0) {
						break;
					}
					insert(server_ver, ipToLong(getIpAddress()), getIpAddress());
					use = true;
					break;
				}
			}

			if (!use) {
				insert(server_ver, ipToLong(getIpAddress()), getIpAddress());
				System.exit(0);
			}
		} catch (final SQLException e) {
			System.exit(0);
			log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
			log.info("加載 安全驗證系統: VerifySecurityManager");
		}
	}

	private static String getIpAddress() {
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (final UnknownHostException e) {
			e.printStackTrace();
		}
		return address.getHostAddress();
	}

	public static long ipToLong(final String strIp) {
		final long[] ip = new long[4];

		final int position1 = strIp.indexOf(".");
		final int position2 = strIp.indexOf(".", position1 + 1);
		final int position3 = strIp.indexOf(".", position2 + 1);

		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static void insert(final int ver, final long host, final String ip) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = DatabaseFactoryVerify.get().getConnection();
			pstm = con.prepareStatement(
					"INSERT INTO user_data (user_ver, user_host, ip, date) VALUE (?, ?, ?, SYSDATE())");
			int i = 0;
			pstm.setInt(++i, ver);
			pstm.setLong(++i, host);
			pstm.setString(++i, ip);

			pstm.execute();
		} catch (final SQLException e) {
			log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}