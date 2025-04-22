package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服務器人物屬性設置
 * 
 * @author dexc
 */
public final class ConfigCharSetting {

	/** CharSettings control */
	public static int PRINCE_MAX_HP;

	public static int PRINCE_MAX_MP;

	public static int KNIGHT_MAX_HP;

	public static int KNIGHT_MAX_MP;

	public static int ELF_MAX_HP;

	public static int ELF_MAX_MP;

	public static int WIZARD_MAX_HP;

	public static int WIZARD_MAX_MP;

	public static int DARKELF_MAX_HP;

	public static int DARKELF_MAX_MP;

	public static int DRAGONKNIGHT_MAX_HP;

	public static int DRAGONKNIGHT_MAX_MP;

	public static int ILLUSIONIST_MAX_HP;

	public static int ILLUSIONIST_MAX_MP;

	public static int WARRIOR_MAX_HP;

	public static int WARRIOR_MAX_MP;

	private static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器人物屬性設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(CHAR_SETTINGS_CONFIG_FILE));
			set.load(is);
			is.close();

			PRINCE_MAX_HP = Integer.parseInt(set.getProperty("PrinceMaxHP", "1000"));
			PRINCE_MAX_MP = Integer.parseInt(set.getProperty("PrinceMaxMP", "800"));
			KNIGHT_MAX_HP = Integer.parseInt(set.getProperty("KnightMaxHP", "1400"));
			KNIGHT_MAX_MP = Integer.parseInt(set.getProperty("KnightMaxMP", "600"));
			ELF_MAX_HP = Integer.parseInt(set.getProperty("ElfMaxHP", "1000"));
			ELF_MAX_MP = Integer.parseInt(set.getProperty("ElfMaxMP", "900"));
			WIZARD_MAX_HP = Integer.parseInt(set.getProperty("WizardMaxHP", "800"));
			WIZARD_MAX_MP = Integer.parseInt(set.getProperty("WizardMaxMP", "1200"));
			DARKELF_MAX_HP = Integer.parseInt(set.getProperty("DarkelfMaxHP", "1000"));
			DARKELF_MAX_MP = Integer.parseInt(set.getProperty("DarkelfMaxMP", "900"));
			DRAGONKNIGHT_MAX_HP = Integer.parseInt(set.getProperty("DragonKnightMaxHP", "1400"));
			DRAGONKNIGHT_MAX_MP = Integer.parseInt(set.getProperty("DragonKnightMaxMP", "600"));
			ILLUSIONIST_MAX_HP = Integer.parseInt(set.getProperty("IllusionistMaxHP", "900"));
			ILLUSIONIST_MAX_MP = Integer.parseInt(set.getProperty("IllusionistMaxMP", "1100"));
			WARRIOR_MAX_HP = Integer.parseInt(set.getProperty("WarriorMaxHP", "1400"));
			WARRIOR_MAX_MP = Integer.parseInt(set.getProperty("WarriorMaxMP", "600"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + CHAR_SETTINGS_CONFIG_FILE);

		} finally {
			set.clear();
		}
	}
}