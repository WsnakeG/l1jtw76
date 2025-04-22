package com.lineage;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigSQL;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 登入資料庫連接設置管理
 */
public class DatabaseFactoryLogin {

	private static final Log _log = LogFactory.getLog(DatabaseFactoryLogin.class);

	private static DatabaseFactoryLogin _instance;

	// 連接池
	private ComboPooledDataSource _source;

	// 驅動程式
	private static String _driver;

	// 資料庫位置
	private static String _url;

	// 使用者名稱
	private static String _user;

	// 使用者密碼
	private static String _password;

	/**
	 * 初始化設置
	 * 
	 * @param driver 驅動程式
	 * @param url 資料庫位置
	 * @param user 使用者名稱
	 * @param password 使用者密碼
	 */
	public static void setDatabaseSettings() {
		_driver = ConfigSQL.DB_DRIVER;
		_url = ConfigSQL.DB_URL1_LOGIN + ConfigSQL.DB_URL2_LOGIN + ConfigSQL.DB_URL3_LOGIN;
		_user = ConfigSQL.DB_LOGIN_LOGIN;
		_password = ConfigSQL.DB_PASSWORD_LOGIN;
	}

	/**
	 * 設置資料載入
	 * 
	 * @throws SQLException
	 */
	public DatabaseFactoryLogin() throws SQLException {
		try {
			_source = new ComboPooledDataSource();
			_source.setDriverClass(_driver);
			_source.setJdbcUrl(_url);
			_source.setUser(_user);
			_source.setPassword(_password);

			_source.getConnection().close();

		} catch (final SQLException e) {
			_log.fatal("資料庫讀取錯誤!", e);

		} catch (final Exception e) {
			_log.fatal("資料庫讀取錯誤!", e);

		}
	}

	/**
	 * 資料庫連線關閉
	 */
	public void shutdown() {
		try {
			_source.close();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		try {
			_source = null;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	public static DatabaseFactoryLogin get() throws SQLException {
		if (_instance == null) {
			_instance = new DatabaseFactoryLogin();
		}
		return _instance;
	}

	/**
	 * 傳回資料庫連接
	 * 
	 * @return Connection
	 * @throws SQLException
	 */
	public Connection getConnection() {
		Connection con = null;

		while (con == null) {
			try {
				con = _source.getConnection();

			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
		return con;
	}
}
