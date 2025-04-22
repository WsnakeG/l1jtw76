package com.lineage.server.utils;

import java.util.Random;

import com.lineage.config.ConfigCharSetting;

public class CalcStat {

	private static Random rnd = new Random();

	private CalcStat() {

	}

	/**
	 * 敏捷追加防禦力計算
	 * 
	 * @param level
	 * @param dex
	 * @return acBonus
	 */
	public static int calcAc(final int level, final int dex) {
		int acBonus = 10;
		switch (dex) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			acBonus -= level >> 3;// / 8;
			break;

		case 10:
		case 11:
		case 12:
			acBonus -= level / 7;
			break;

		case 13:
		case 14:
		case 15:
			acBonus -= level / 6;
			break;

		case 16:
		case 17:
			acBonus -= level / 5;
			break;

		default:
			acBonus -= level >> 2;// / 4;
			break;
		}
		return acBonus;
	}

	/**
	 * 精神追加抗魔計算
	 * 
	 * @param wis
	 * @return mrBonus
	 */
	public static int calcStatMr(final int wis) {
		int mrBonus = 0;
		switch (wis) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
			mrBonus = 0;
			break;

		case 15:
		case 16:
			mrBonus = 3;
			break;

		case 17:
			mrBonus = 6;
			break;

		case 18:
			mrBonus = 10;
			break;

		case 19:
			mrBonus = 15;
			break;

		case 20:
			mrBonus = 21;
			break;

		case 21:
			mrBonus = 28;
			break;

		case 22:
			mrBonus = 37;
			break;

		case 23:
			mrBonus = 47;
			break;

		default:
			mrBonus = 50;
			break;
		}
		return mrBonus;
	}

	public static int calcDiffMr(final int wis, final int diff) {
		return calcStatMr(wis + diff) - calcStatMr(wis);
	}

	/**
	 * 各職業等級提升時HP上昇值計算
	 * 
	 * @param charType 職業
	 * @param baseMaxHp 目前HP最大值
	 * @param baseCon 體質
	 * @param originalHpup
	 * @return HP上昇後數値
	 */
	public static short calcStatHp(final int charType, final int baseMaxHp, final int baseCon,
			final int originalHpup) {
		short randomhp = 0;
		if (baseCon > 15) {
			randomhp = (short) (baseCon - 15);
		}
		switch (charType) {
		case 0: // 王族
			randomhp += (short) (11 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.PRINCE_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.PRINCE_MAX_HP - baseMaxHp);
			}
			break;

		case 1: // 騎士
			randomhp += (short) (17 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.KNIGHT_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.KNIGHT_MAX_HP - baseMaxHp);
			}
			break;

		case 2: // 精靈
			randomhp += (short) (10 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.ELF_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.ELF_MAX_HP - baseMaxHp);
			}
			break;

		case 3: // 法師
			randomhp += (short) (7 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.WIZARD_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.WIZARD_MAX_HP - baseMaxHp);
			}
			break;

		case 4: // 黑妖
			randomhp += (short) (10 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.DARKELF_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.DARKELF_MAX_HP - baseMaxHp);
			}
			break;

		case 5: // 龍騎
			randomhp += (short) (13 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.DRAGONKNIGHT_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.DRAGONKNIGHT_MAX_HP - baseMaxHp);
			}
			break;

		case 6: // 幻術
			randomhp += (short) (9 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.ILLUSIONIST_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.ILLUSIONIST_MAX_HP - baseMaxHp);
			}
			break;

		case 7:// 戰士
			randomhp += (short) (17 + rnd.nextInt(2)); // 初期値分追加

			if ((baseMaxHp + randomhp) > ConfigCharSetting.WARRIOR_MAX_HP) {
				randomhp = (short) (ConfigCharSetting.WARRIOR_MAX_HP - baseMaxHp);
			}
			break;
		}

		randomhp += originalHpup;

		if (randomhp < 0) {
			randomhp = 0;
		}
		return randomhp;
	}

	/**
	 * 各職業等級提升時MP上昇值計算
	 * 
	 * @param charType
	 * @param baseMaxMp
	 * @param baseWis
	 * @param originalMpup
	 * @return MP上昇後數値
	 */
	public static short calcStatMp(final int charType, final int baseMaxMp, final int baseWis,
			final int originalMpup) {
		int randommp = 0;
		int seedY = 0;
		int seedZ = 0;
		switch (baseWis) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 8:
		case 10:
		case 11:
			seedY = 2;
			break;

		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:

		case 7:
		case 9:
			seedY = 3;
			break;

		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:

		case 25:
		case 26:
		case 29:
		case 30:
		case 34:
			seedY = 4;
			break;

		default:
			seedY = 5;
			break;
		}

		switch (baseWis) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			seedZ = 0;
			break;

		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
			seedZ = 1;
			break;

		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
			seedZ = 2;
			break;

		case 21:
		case 22:
		case 23:
		case 24:
			seedZ = 3;
			break;

		case 25:
		case 26:
		case 27:
		case 28:
			seedZ = 4;
			break;

		case 29:
		case 30:
		case 31:
		case 32:
			seedZ = 5;
			break;

		default:
			seedZ = 6;
			break;
		}

		randommp = rnd.nextInt(seedY) + 1 + seedZ;

		switch (charType) {
		case 0: // 王族
			if ((baseMaxMp + randommp) > ConfigCharSetting.PRINCE_MAX_MP) {
				randommp = ConfigCharSetting.PRINCE_MAX_MP - baseMaxMp;
			}
			break;

		case 1: // 騎士
			randommp = ((randommp * 2) / 3);
			if ((baseMaxMp + randommp) > ConfigCharSetting.KNIGHT_MAX_MP) {
				randommp = ConfigCharSetting.KNIGHT_MAX_MP - baseMaxMp;
			}
			break;

		case 2: // 精靈
			randommp = (int) (randommp * 1.5);

			if ((baseMaxMp + randommp) > ConfigCharSetting.ELF_MAX_MP) {
				randommp = ConfigCharSetting.ELF_MAX_MP - baseMaxMp;
			}
			break;

		case 3: // 法師
			randommp *= 2;

			if ((baseMaxMp + randommp) > ConfigCharSetting.WIZARD_MAX_MP) {
				randommp = ConfigCharSetting.WIZARD_MAX_MP - baseMaxMp;
			}
			break;

		case 4: // 黑妖
			randommp = (int) (randommp * 1.5);

			if ((baseMaxMp + randommp) > ConfigCharSetting.DARKELF_MAX_MP) {
				randommp = ConfigCharSetting.DARKELF_MAX_MP - baseMaxMp;
			}
			break;

		case 5: // 龍騎
			randommp = ((randommp * 2) / 3);

			if ((baseMaxMp + randommp) > ConfigCharSetting.DRAGONKNIGHT_MAX_MP) {
				randommp = ConfigCharSetting.DRAGONKNIGHT_MAX_MP - baseMaxMp;
			}
			break;

		case 6: // 幻術
			randommp = ((randommp * 5) / 3);

			if ((baseMaxMp + randommp) > ConfigCharSetting.ILLUSIONIST_MAX_MP) {
				randommp = ConfigCharSetting.ILLUSIONIST_MAX_MP - baseMaxMp;
			}
			break;

		case 7: // 戰士
			randommp = (randommp << 1) / 3;

			if ((baseMaxMp + randommp) > ConfigCharSetting.WARRIOR_MAX_MP) {
				randommp = ConfigCharSetting.WARRIOR_MAX_MP - baseMaxMp;
			}
			break;
		}

		randommp += originalMpup;

		if (randommp < 0) {
			randommp = 0;
		}
		return (short) randommp;
	}
}
