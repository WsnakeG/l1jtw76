package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服務器資料庫設置
 * 
 * @author dexc
 */
public final class ConfigSQL {

	/** 資料庫連接驅動程式 */
	public static String DB_DRIVER;

	/** 登入資料庫位置1 */
	public static String DB_URL1_LOGIN;

	/** 登入資料庫位置2 */
	public static String DB_URL2_LOGIN;

	/** 登入資料庫位置3 */
	public static String DB_URL3_LOGIN;

	/** 登入資料庫 帳戶名稱 */
	public static String DB_LOGIN_LOGIN;

	/** 登入資料庫 帳戶密碼 */
	public static String DB_PASSWORD_LOGIN;

	/** 資料庫位置1 */
	public static String DB_URL1;

	/** 資料庫位置2 */
	public static String DB_URL2;

	/** 資料庫位置3 */
	public static String DB_URL3;

	/** 資料庫 帳戶名稱 */
	public static String DB_LOGIN;

	/** 資料庫 帳戶密碼 */
	public static String DB_PASSWORD;

	private static final String SQL_CONFIG = "./config/sql.properties";

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器資料庫設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(SQL_CONFIG));
			set.load(is);
			is.close();

			DB_DRIVER = set.getProperty("Driver", "com.mysql.jdbc.Driver");

			DB_URL1_LOGIN = set.getProperty("URL1_LOGIN", "jdbc:mysql://localhost/");
			DB_URL2_LOGIN = set.getProperty("URL2_LOGIN", "l1jsrc");
			DB_URL3_LOGIN = set.getProperty("URL3_LOGIN", "?useUnicode=true&characterEncoding=UTF8");
			DB_LOGIN_LOGIN = set.getProperty("Login_LOGIN", "root");
			DB_PASSWORD_LOGIN = set.getProperty("Password_LOGIN", "123456");

			DB_URL1 = set.getProperty("URL1", "jdbc:mysql://localhost/");
			DB_URL2 = set.getProperty("URL2", "l1jsrc");
			DB_URL3 = set.getProperty("URL3", "?useUnicode=true&characterEncoding=UTF8");
			DB_LOGIN = set.getProperty("Login", "root");
			DB_PASSWORD = set.getProperty("Password", "123456");

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + SQL_CONFIG);

		} finally {
			set.clear();
		}
	}
}