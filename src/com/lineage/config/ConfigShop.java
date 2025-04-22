package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 商店設置
 * 
 * @author dexc
 */
public final class ConfigShop {

	private static final String SHOP_SETTINGS_FILE = "./config/shop.properties";
	/** 隨商商城 */
	public static int SHOPCN_01;
	public static int SHOPCN_02;
	public static int SHOPCN_03;
	/** 一般展示商人 */
	public static int DROPDEMO_01;
	public static int DROPDEMO_02;
	public static int DROPDEMO_03;
	public static int DROPDEMO_04;
	public static int DROPDEMO_05;
	public static int DROPDEMO_06;
	public static int DROPDEMO_07;
	public static int DROPDEMO_08;
	/** 寶區展示商人 */
	public static int SDROPDEMO_01;
	public static int SDROPDEMO_02;
	public static int SDROPDEMO_03;
	public static int SDROPDEMO_04;
	public static int SDROPDEMO_05;
	public static int SDROPDEMO_06;
	public static int SDROPDEMO_07;
	public static int SDROPDEMO_08;

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(SHOP_SETTINGS_FILE));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			set.load(isr);
			is.close();
			// 一般展示商人
			DROPDEMO_01 = Integer.parseInt(set.getProperty("DropDemo_01", "0"));
			DROPDEMO_02 = Integer.parseInt(set.getProperty("DropDemo_02", "0"));
			DROPDEMO_03 = Integer.parseInt(set.getProperty("DropDemo_03", "0"));
			DROPDEMO_04 = Integer.parseInt(set.getProperty("DropDemo_04", "0"));
			DROPDEMO_05 = Integer.parseInt(set.getProperty("DropDemo_05", "0"));
			DROPDEMO_06 = Integer.parseInt(set.getProperty("DropDemo_06", "0"));
			DROPDEMO_07 = Integer.parseInt(set.getProperty("DropDemo_07", "0"));
			DROPDEMO_08 = Integer.parseInt(set.getProperty("DropDemo_08", "0"));
			// 寶區展示商人
			SDROPDEMO_01 = Integer.parseInt(set.getProperty("SDropDemo_01", "0"));
			SDROPDEMO_02 = Integer.parseInt(set.getProperty("SDropDemo_02", "0"));
			SDROPDEMO_03 = Integer.parseInt(set.getProperty("SDropDemo_03", "0"));
			SDROPDEMO_04 = Integer.parseInt(set.getProperty("SDropDemo_04", "0"));
			SDROPDEMO_05 = Integer.parseInt(set.getProperty("SDropDemo_05", "0"));
			SDROPDEMO_06 = Integer.parseInt(set.getProperty("SDropDemo_06", "0"));
			SDROPDEMO_07 = Integer.parseInt(set.getProperty("SDropDemo_07", "0"));
			SDROPDEMO_08 = Integer.parseInt(set.getProperty("SDropDemo_08", "0"));
		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + SHOP_SETTINGS_FILE);

		} finally {
			set.clear();
		}
	}
}