package com.lineage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.LogManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.lineage.commons.system.LanSecurityManager;
import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigBad;
import com.lineage.config.ConfigBoxMsg;
import com.lineage.config.ConfigCharSetting;
import com.lineage.config.ConfigDescs;
// import com.lineage.config.ConfigDropMsg;
import com.lineage.config.ConfigIpCheck;
import com.lineage.config.ConfigKill;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.config.ConfigRecord;
import com.lineage.config.ConfigSQL;
import com.lineage.config.ConfigShop;
import com.lineage.config.ConfigSkills;
import com.lineage.server.GameServer;
//import com.lineage.server.datatables.VerifyVerTable;

/**
 * 服務器啟動
 */
public class Server {

	private static final String _log_prop = "./config/logging.properties";

	private static final String _log_4j = "./config/log4j.properties";

	private static final String _loginfo = "./loginfo";

	private static final String _back = "./back";

	private static final String _c3p0 = "./config/c3p0-config.xml";

	public static String IPADRESS = "127.0.0.1";

	public static String PORT = "2001";

	/**
	 * MAIN
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {

		final Calendar date = Calendar.getInstance();
		// 1=周日 7=周六
		System.out.println(">>>>" + date.get(Calendar.DAY_OF_WEEK));
		System.out.println(">>>>" + date.get(Calendar.HOUR_OF_DAY));

		// 測試用核心
		try {
			// 在核心啟動命令後面加上 test 可以用來作顯示測試
			if (args[0].equalsIgnoreCase("test")) {
				Config.DEBUG = true;
			}

		} catch (final Exception e) {
			// e.printStackTrace();
		}

		// 壓縮舊檔案
		final CompressFile bean = new CompressFile();
		try {
			// 建立備份用資料夾
			final File file = new File(_back);
			if (!file.exists()) {
				file.mkdir();
			}

			final String nowDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
			bean.zip(_loginfo, "./back/" + nowDate + ".zip");

			final File loginfofile = new File(_loginfo);
			final String[] loginfofileList = loginfofile.list();
			for (final String fileName : loginfofileList) {
				final File readfile = new File(_loginfo + "/" + fileName);
				if (readfile.exists() && !readfile.isDirectory()) {
					readfile.delete();
				}
			}

		} catch (final IOException e) {
			System.out.println("資料夾不存在: " + _back + " 已經自動建立!");
		}

		boolean error = false;

		try {
			final InputStream is = new BufferedInputStream(new FileInputStream(_log_prop));
			LogManager.getLogManager().readConfiguration(is);
			is.close();

		} catch (final IOException e) {
			System.out.println("檔案遺失: " + _log_prop);
			error = true;
		}

		try {
			PropertyConfigurator.configure(_log_4j);

		} catch (final Exception e) {
			System.out.println("檔案遺失: " + _log_4j);
			System.exit(0);
		}

		try {
			System.setProperty("com.mchange.v2.c3p0.cfg.xml", _c3p0);

		} catch (final Exception e) {
			System.out.println("檔案遺失: " + _c3p0);
			System.exit(0);
		}

		final String filetime = FileData();
		try {
			Config.load();
			ConfigAlt.load();
			ConfigCharSetting.load();
			ConfigOther.load();
			ConfigRate.load();
			ConfigSQL.load();
			ConfigRecord.load();

			ConfigDescs.load();
			ConfigBad.load();
			ConfigKill.load();
			ConfigIpCheck.load();
			ConfigBoxMsg.load();
			// ConfigDropMsg.load();
			ConfigSkills.load();
			ConfigShop.load();
			Config.ServerTime = filetime;
		} catch (final Exception e) {
			System.out.println("CONFIG 資料加載異常!" + e);
			error = true;
		}

		System.out.println(_licence + "\n");
		final Log log = LogFactory.getLog(Server.class);

		final String infoX = "\n\r##################################################"
				+ "\n\r       服務器 (Lineage核心版本:" + Config.VER + "/" + Config.SRCVER + ")"
				+ "\n\r##################################################";

		log.info(infoX);

		final File file = new File("./jar");
		final String[] fileNameList = file.list();

		for (final String fileName : fileNameList) {
			final File readfile = new File(fileName);
			if (!readfile.isDirectory()) {
				log.info("加載引用JAR: " + fileName);
			}
		}

		if (error) {
			System.exit(0);
		}

		// log.info("訊息辨識(色彩涵義): [INFO]資訊");
		// log.debug("訊息辨識(色彩涵義): [DEBUG]除錯");
		// log.warn("訊息辨識(色彩涵義): [WARN]警告");
		// log.error("訊息辨識(色彩涵義): [ERROR]錯誤");
		// log.fatal("訊息辨識(色彩涵義): [FATAL]嚴重錯誤");

		// SQL讀取初始化
		DatabaseFactoryLogin.setDatabaseSettings();
		DatabaseFactory.setDatabaseSettings();

		DatabaseFactoryLogin.get();
		DatabaseFactory.get();
		
		if (ConfigIpCheck.IPTABLE) {
			DatabaseFactoryIP.setDatabaseSettings();
			DatabaseFactoryIP.get();
		}

		// 安全管理器
		final LanSecurityManager securityManager = new LanSecurityManager();
		System.setSecurityManager(securityManager);
		log.info("加載 安全管理器: LanSecurityManager");

		final String osname = System.getProperties().getProperty("os.name");
		if (osname.lastIndexOf("Linux") != -1) {
			Config.ISUBUNTU = true;
		}

		GameServer.getInstance().initialize();
	}

	// License
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	private static final String _licence = "Copyright (C) 2012-2017 <Yiwei 3.52C-7.60C>\n" + "授權時間: 2017/03\n";

	/**
	 * <font color=#00800>取得核心檔案最後修正日期</font>
	 * 
	 * @return 檔案日期
	 */
	private static String FileData() {
		final DecimalFormat df = new DecimalFormat("#,00");
		String fileName = "Server_Game.jar";
		String m_date = null;

		final File m_file = new File(fileName);// 檔案名稱

		if (m_file.exists()) {// 檢查檔案是否存在
			final Date d = new Date(m_file.lastModified());// 傳回檔案最後被修改時間
			final double kbs = m_file.length();// 檔案大小
			final double kb = kbs / 1000;
			if (kb < 1073741824) {
				fileName = df.format(kb / 1048576) + "M";
			}
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
			m_date = "       核心大小:" + kb + "位元組" + "\n\r       更新時間:" + sdf.format(d);
		} else {
			System.exit(0);// 檔案名稱不吻合(強制服務端檔案禁止改名稱)
		}
		return m_date;
	}
}
