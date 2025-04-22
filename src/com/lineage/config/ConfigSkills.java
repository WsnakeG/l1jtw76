package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 技能獨立設置
 * 
 * @author dexc
 */
public final class ConfigSkills {

	/** 幻術技能設置 */
	public static int ILLUSION_AVATAR_DMG;
	// 因有小數點改為Double
	public static double ILLUSION_AVATAR_DAMAGE;

	private static final String SKILLS_SETTINGS_FILE = "./config/skills.properties";

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(SKILLS_SETTINGS_FILE));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			set.load(isr);
			is.close();

			ILLUSION_AVATAR_DMG = Integer.parseInt(set.getProperty("ILLUSION_AVATAR_DMG", "10"));

			ILLUSION_AVATAR_DAMAGE = Double.parseDouble(set.getProperty("ILLUSION_AVATAR_DAMAGE", "1.5"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + SKILLS_SETTINGS_FILE);

		} finally {
			set.clear();
		}
	}
}