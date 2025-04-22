package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * IP防禦
 * 
 * @author loli
 */
public final class ConfigIpCheck {

	// IP驗證庫

	/** IP驗證庫 */
	public static boolean IPTABLE;

	/** IP驗證資料庫位置1 */
	public static String DB_IP_URL1;

	/** IP驗證資料庫位置2 */
	public static String DB_IP_URL2;

	/** IP驗證資料庫位置3 */
	public static String DB_IP_URL3;

	/** IP驗證資料庫 帳戶名稱 */
	public static String DB_IP_LOGIN;

	/** IP驗證資料庫 帳戶密碼 */
	public static String DB_IP_PASSWORD;

	/** 驗證後允許登入時間(秒) */
	public static int TIME;

	/** 允許驗證無效次數 */
	public static int ERROR;

	/** 如果是LINUX系統 是否加入ufw 封鎖清單(true:是 false:否) */
	public static boolean UFW;

	private static final String _ipcheck = "./config/ipcheck.properties";

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(_ipcheck));
			set.load(is);
			is.close();

			// IP驗證庫
			IPTABLE = Boolean.parseBoolean(set.getProperty("IPTABLE", "false"));

			if (IPTABLE) {
				DB_IP_URL1 = set.getProperty("DB_IP_URL1", "jdbc:mysql://localhost/");
				DB_IP_URL2 = set.getProperty("DB_IP_URL2", "ipcheck");
				DB_IP_URL3 = set.getProperty("DB_IP_URL3", "?useUnicode=true&characterEncoding=UTF8");
				DB_IP_LOGIN = set.getProperty("DB_IP_LOGIN", "root");
				DB_IP_PASSWORD = set.getProperty("DB_IP_PASSWORD", "123456");
			}
			TIME = Integer.parseInt(set.getProperty("TIME", "300"));

			ERROR = Integer.parseInt(set.getProperty("ERROR", "3"));

			UFW = Boolean.parseBoolean(set.getProperty("UFW", "true"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + _ipcheck);

		} finally {
			set.clear();
		}
	}
}