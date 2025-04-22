package com.lineage.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * 服務器儲存設置
 * 
 * @author dexc
 */
public final class ConfigRecord {

	// 防具 武器 強化失敗紀錄
	public static boolean LOGGING_BAN_ENCHANT = false;

	// 對話紀錄
	public static boolean LOGGING_CHAT_NORMAL = false;

	public static boolean LOGGING_CHAT_SHOUT = false;

	public static boolean LOGGING_CHAT_WORLD = false;

	public static boolean LOGGING_CHAT_CLAN = false;

	public static boolean LOGGING_CHAT_WHISPER = false;

	public static boolean LOGGING_CHAT_PARTY = false;

	public static boolean LOGGING_CHAT_BUSINESS = false;

	public static boolean LOGGING_CHAT_COMBINED = false;

	public static boolean LOGGING_CHAT_CHAT_PARTY = false;

	private static final String RECORD_FILE = "./config/record.properties";

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器儲存設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(RECORD_FILE));
			set.load(is);
			is.close();

			LOGGING_BAN_ENCHANT = Boolean.parseBoolean(set.getProperty("LoggingBanEnchant", "false"));

			LOGGING_CHAT_NORMAL = Boolean.parseBoolean(set.getProperty("LoggingChatNormal", "false"));

			LOGGING_CHAT_SHOUT = Boolean.parseBoolean(set.getProperty("LoggingChatShout", "false"));

			LOGGING_CHAT_WORLD = Boolean.parseBoolean(set.getProperty("LoggingChatWorld", "false"));

			LOGGING_CHAT_CLAN = Boolean.parseBoolean(set.getProperty("LoggingChatClan", "false"));

			LOGGING_CHAT_WHISPER = Boolean.parseBoolean(set.getProperty("LoggingChatWhisper", "false"));

			LOGGING_CHAT_PARTY = Boolean.parseBoolean(set.getProperty("LoggingChatParty", "false"));

			LOGGING_CHAT_BUSINESS = Boolean.parseBoolean(set.getProperty("LoggingBusiness", "false"));

			LOGGING_CHAT_COMBINED = Boolean.parseBoolean(set.getProperty("LoggingChatCombined", "false"));

			LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(set.getProperty("LoggingChatChatParty", "false"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + RECORD_FILE);

		} finally {
			set.clear();
		}
	}

	// 記錄時間格式(注意: 這裡不需重複new來作同步化, 因為是記錄同一天) by terry0412
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	// 記錄文件檔 by terry0412
	public static final void recordToFiles(final String filename, final String info,
			final Timestamp timestamp) {
		// Java 1.7 新功能 by terry0412
		try (final BufferedWriter out = new BufferedWriter(
				new FileWriter("./物品操作日誌/" + filename + "/" + sdf.format(timestamp) + ".txt", true))) {
			out.write(info);
			out.newLine();
			out.flush();

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final void recordToFiles(final String filename, final String info) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(
					"./物品操作日誌/" + filename + "/" + sdf.format(new Timestamp(System.currentTimeMillis())) + ".txt",
					true));
			out.write(info);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
