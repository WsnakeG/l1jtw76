package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.lineage.list.Announcements;

/**
 * 服務器基礎設置
 * 
 * @author dexc
 */
public final class Config {
	
	public static String ServerTime = "警告: 未檢測到系統核心，伺服器重新啟動。";
	
	/** 伺服器執行登入器驗證 */
	public static boolean LOGINS_TO_AUTOENTICATION;
	public static String RSA_KEY_E;
	public static String RSA_KEY_N;

	/** 版本編號 */
	public static final String VER = "760.00.00";

	/** 客戶端對應 */
	public static final String SRCVER = "Lineage7.6C";

	/** 客戶端版本號 */
	public static final int SVer = 0x08f29783;
	public static final int CVer = 0x08f29769;
	public static final int AVer = 0x77fdd029;
	public static final int NVer = 0x08f29769;

	/** 驗證模式 */
	public static int ServerVer;

	/** 除錯模式 */
	public static boolean DEBUG = false;

	/** 伺服器編號 */
	public static int SERVERNO;

	/** 作業系統是UBUNTU */
	public static boolean ISUBUNTU = false;

	/** 伺服器位置 */
	public static String GAME_SERVER_HOST_NAME;

	/** 伺服器端口 */
	// 服務器監聽端口以"-"減號分隔 允許設置多個(允許設置一個)
	public static String GAME_SERVER_PORT;

	/** 服務器名稱 */
	public static String SERVERNAME;

	/** 廣播伺服器位置 */
	public static String CHAT_SERVER_HOST_NAME;

	/** 廣播伺服器端口 */
	public static int CHAT_SERVER_PORT;

	/** 時區設置 */
	public static String TIME_ZONE;

	/** 伺服器語系 */
	public static int CLIENT_LANGUAGE;

	/** 伺服器語系字串源 */
	public static String CLIENT_LANGUAGE_CODE;

	/** 伺服器語系定位陣列 */
	public static String[] LANGUAGE_CODE_ARRAY = { "UTF8", "EUCKR", "UTF8", "BIG5", "SJIS", "GBK" };

	/** 重新啟動時間設置 */
	public static String[] AUTORESTART = null;

	/** 允許自動註冊 */
	public static boolean AUTO_CREATE_ACCOUNTS;

	/** 允許最大玩家 */
	public static short MAX_ONLINE_USERS = 10;

	/** 人物資料自動保存時間 */
	public static int AUTOSAVE_INTERVAL;

	/** 人物背包自動保存時間 */
	public static int AUTOSAVE_INTERVAL_INVENTORY;

	/** 客戶端接收信息範圍 (-1為畫面內可見) */
	public static int PC_RECOGNIZE_RANGE;

	/** 端口重置時間(單位:分鐘) */
	public static int RESTART_LOGIN;

	/** 是否顯示公告 */
	public static boolean NEWS;

	public static boolean AICHECK = false;

	/** 是否使用web point */
	public static boolean ISPOINT;
	
	/** 登入連線秒數 */
	public static int VerSec;
	
	private static final String SERVER_CONFIG_FILE = "./config/server.properties";

	public static void load() throws ConfigErrorException {
		// TODO 伺服器綑綁
		final Properties pack = new Properties();
		try {
			final InputStream is = new FileInputStream(new File("./config/pack.properties"));
			pack.load(is);
			is.close();
			LOGINS_TO_AUTOENTICATION = Boolean.parseBoolean(pack.getProperty("Autoentication", "false"));
			RSA_KEY_E = pack.getProperty("RSA_KEY_E", "0");
			RSA_KEY_N = pack.getProperty("RSA_KEY_N", "0");

		} catch (final Exception e) {
			System.err.println("沒有找到登入器加密設置檔案: ./config/pack.properties");

		} finally {
			pack.clear();
		}

		// _log.info("載入服務器基礎設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(SERVER_CONFIG_FILE));
			set.load(is);
			is.close();
			
			// 驗證單位
			ServerVer = Integer.parseInt(set.getProperty("ServerVer", "-1"));

			// 伺服器編號
			SERVERNO = Integer.parseInt(set.getProperty("ServerNo", "1"));

			// 通用
			GAME_SERVER_HOST_NAME = set.getProperty("GameserverHostname", "*");

			// 服務器監聽端口以"-"減號分隔 允許設置多個(允許設置一個)
			GAME_SERVER_PORT = set.getProperty("GameserverPort", "2000-2001");

			// 廣播伺服器位置
			CHAT_SERVER_HOST_NAME = set.getProperty("ChatHostname", "*");

			// 廣播伺服器端口
			CHAT_SERVER_PORT = Integer.parseInt(set.getProperty("ChatPort", "2001"));

			// 語系
			CLIENT_LANGUAGE = Integer.parseInt(set.getProperty("ClientLanguage", "3"));

			CLIENT_LANGUAGE_CODE = LANGUAGE_CODE_ARRAY[CLIENT_LANGUAGE];

			final String tmp = set.getProperty("AutoRestart", "");
			if (!tmp.equalsIgnoreCase("null")) {
				AUTORESTART = tmp.split(",");
			}

			TIME_ZONE = set.getProperty("TimeZone", "CST");

			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(set.getProperty("AutoCreateAccounts", "true"));

			MAX_ONLINE_USERS = Short.parseShort(set.getProperty("MaximumOnlineUsers", "30"));

			AUTOSAVE_INTERVAL = Integer.parseInt(set.getProperty("AutosaveInterval", "1200"), 10);

			AUTOSAVE_INTERVAL /= 60;
			if (AUTOSAVE_INTERVAL <= 0) {
				AUTOSAVE_INTERVAL = 20;
			}

			AUTOSAVE_INTERVAL_INVENTORY = Integer
					.parseInt(set.getProperty("AutosaveIntervalOfInventory", "300"), 10);

			AUTOSAVE_INTERVAL_INVENTORY /= 60;
			if (AUTOSAVE_INTERVAL_INVENTORY <= 0) {
				AUTOSAVE_INTERVAL_INVENTORY = 5;
			}

			PC_RECOGNIZE_RANGE = Integer.parseInt(set.getProperty("PcRecognizeRange", "13"));

			RESTART_LOGIN = Integer.parseInt(set.getProperty("restartlogin", "30"));

			NEWS = Boolean.parseBoolean(set.getProperty("News", "false"));

			AICHECK = Boolean.parseBoolean(set.getProperty("AI", "false"));

			if (NEWS) {
				Announcements.get().load();
			}

			ISPOINT = Boolean.parseBoolean(set.getProperty("WebPoint", "false"));
			
			VerSec = Integer.parseInt(set.getProperty("VerSec", "false"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + SERVER_CONFIG_FILE);

		} finally {
			set.clear();
		}
	}
}
