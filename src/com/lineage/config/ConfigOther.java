package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 服務器活動設置
 * 
 * @author dexc
 */
public final class ConfigOther {

	public static int INJUSTICE_COUNT; // 加速器偵測 不正常封包

	public static int JUSTICE_COUNT; // 加速器偵測 正常封包允許數

	public static int CHECK_STRICTNESS; // 加速器偵測 攻擊封包偵測

	public static int CHECK_MOVE_STRICTNESS; // 加速器偵測 移動封包偵測

	public static boolean LOGGING_ACCELERATOR; // 加速器偵測 紀錄加速器違規

	public static boolean CHECK_MOVE_INTERVAL; // 加速器偵測 (移動間隔)

	public static boolean CHECK_ATTACK_INTERVAL; // 加速器偵測 (攻擊間隔)

	public static boolean CHECK_SPELL_INTERVAL; // 加速器偵測 (技能使用間隔)

	public static boolean DEBUG_MODE; // 加速器檢測(管理員模式)

	public static int PUNISHMENT_TYPE; // 加速器偵測 (懲罰類別)

	public static int PUNISHMENT_TIME; // 加速器偵測 (懲罰時間)

	public static int PUNISHMENT_MAP_ID; // 加速器偵測 (傳送地圖ID)

	public static boolean KILLRED = true;// 怪物是否主動攻擊紅人

	public static int RATE_XP_WHO = 1;

	public static boolean CLANDEL;// 允許盟主解散血盟

	public static boolean CLANTITLE;// 允許盟員自行建立封號

	public static int CLANCOUNT;// 自行建立血盟人數上限

	public static boolean LIGHT;// 啟用人物全時光照(true啟用 false關閉)

	public static int WEAPON100;// 百分百武卷最高可追加值

	public static int ARMOR100;// 百分百防卷最高可追加值

	public static boolean HPBAR;// 顯示怪物血條

	public static boolean SHOPINFO;// 一般商店是否顯示詳細資訊

	public static int HOMEHPR;// 血盟小屋HP恢復增加

	public static int HOMEMPR;// 血盟小屋MP恢復增加

	public static int CUSTOM_HPR;// 自定義地圖回血量

	public static int CUSTOM_MPR;// 自定義地圖回魔量

	public static int CUSTOM_MAPID;// 自定義回血魔地圖

	public static boolean WAR_DOLL;// 攻城旗幟內是否允許攜帶娃娃 true:允許 false:禁止

	public static int SET_GLOBAL;// 廣播扣除金幣或是飽食度(0:飽食度 1:金幣)

	public static int SET_GLOBAL_COUNT;// 廣播扣除質(set_global設置0:扣除飽食度量
										// set_global設置1:扣除金幣量)

	public static int SET_GLOBAL_TIME;// 廣播/買賣頻道間隔秒數

	// 戰鬥特化遭遇的守護等級
	// 設置等級以下角色 被超過10級以上的玩家攻擊而死亡時，不會失去經驗值，也不會掉落物品
	public static int ENCOUNTER_LV;

	// 武器+9(含)以上附加額外增加傷害值 by terry0412
	/** 是否開啟 */
	public static boolean WEAPON_POWER;

	/** 各階段強化值附加傷害 */
	public static int[] WEAPON_POWER_LIST;

	private static final String LIANG = "./config/other.properties";

	public static boolean CHECK_SPAWN_BOSS;

	// AI 懲罰金額設定
	public static int ai_count;

	/** 魔族保護卷軸(防具)2014/10/01 ByRoy新增 */
	public static int ArmorSet;
	/** 魔族保護卷軸(武器)2014/10/01 ByRoy新增 */
	public static int WeaponSet;

	/** 陣營威望搶奪開關 */
	public static boolean Prestigesnatch;
	/** 同陣營搶奪積分設定 */
	public static double camp1;
	/** 非同陣營搶奪積分設定 */
	public static double camp2;
	/** 陣營加入等級限制 */
	public static int CAMPLEVEL;
	/** 陣營加入特效顯示 */
	public static int CAMPGFX;

	/** 魂體轉換轉一次多少低魔 by erics4179 */
	public static int BLOODY_SOULADDMP;

	/** 全怪物皆有機率掉落設定物品 by erics4179 */
	public static boolean AllNpcDropItem;
	public static int Rnd;
	public static int ItemId;
	public static int ItemCount;

	public static boolean DOUBLE_GIFT; // 首儲雙倍送開關
	
	/** 防具 強化公告 2016/10/10 By Erics4179新增 */
	public static int StrengthenArmor;
	
	/** 武器 強化公告 2016/10/10 By Erics4179新增 */
	public static int StrengthenWeapon;
	
	/** 自創陣營戰系統 2016/10/12 By Erics4179新增 */
	public static int RedBlueJoin_itemid;//報名道具編號
	public static int RedBlueJoin_count;//報名道具數量
	public static int RedBluePc_amount;//各隊報名人數
	public static int RedBlueLv_min;//角色最低等級限制
	public static int RedBlueLv_max;//角色最高等級限制
	public static int RedBlueTime_all;//活動時間
	public static int RedBlueTime_clear;//活動場次清潔時間
	public static int RedBlueEffect_time;//活動開始凍結玩家時間
	public static int[] RedBlueEnd_map;//活動結束傳送的地圖座標
	public static int[] RedBlueRed_map1;//ROOM1 紅隊傳送的地圖座標
	public static int[] RedBlueBlue_map1;//ROOM1 藍隊傳送的地圖座標
	public static int[] RedBlueRed_map2;//ROOM2 紅隊傳送的地圖座標
	public static int[] RedBlueBlue_map2;//ROOM2 藍隊傳送的地圖座標
	public static int RedBlueStart_point;//各隊角色初始積分
	public static int RedBlueNormal_point;//擊殺敵方成員所得積分
	public static int RedBlueLeader_point;//擊殺敵方對長所得積分
	public static int RedBlueBonus_itemid;//活動獎勵道具
	public static int RedBlueBonus_count;//活動獎勵數量
	
	/** EXP倍率外置設定 2016/10/15 By Erics4179新增 */
	public static int LV90EXP;
	public static int LV91EXP;
	public static int LV92EXP;
	public static int LV93EXP;
	public static int LV94EXP;
	public static int LV95EXP;
	public static int LV96EXP;
	public static int LV97EXP;
	public static int LV98EXP;
	public static int LV99EXP;
	
	/** 殷海薩龍之祝福指數外置設定 2017/02/28 By Erics4179新增 */
	public static int LEAVES_MAXEXP;
	
	public static void load() throws ConfigErrorException {
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(LIANG));
			set.load(is);
			is.close();
			
			//殷海薩龍之祝福指數外置設定
			LEAVES_MAXEXP = Integer.parseInt(set.getProperty("LEAVES_MAXEXP", "500"));
			
			//EXP倍率外置設定
			LV90EXP = Integer.parseInt(set.getProperty("LV90EXP", "4096"));
			LV91EXP = Integer.parseInt(set.getProperty("LV91EXP", "8192"));
			LV92EXP = Integer.parseInt(set.getProperty("LV92EXP", "16384"));
			LV93EXP = Integer.parseInt(set.getProperty("LV93EXP", "32768"));
			LV94EXP = Integer.parseInt(set.getProperty("LV94EXP", "65536"));
			LV95EXP = Integer.parseInt(set.getProperty("LV95EXP", "131072"));
			LV96EXP = Integer.parseInt(set.getProperty("LV96EXP", "262144"));
			LV97EXP = Integer.parseInt(set.getProperty("LV97EXP", "524288"));
			LV98EXP = Integer.parseInt(set.getProperty("LV98EXP", "1048576"));
			LV99EXP = Integer.parseInt(set.getProperty("LV99EXP", "2097152"));
			
			//自創陣營戰系統 相關設定
			RedBlueJoin_itemid  = Integer.parseInt(set.getProperty("RedBlueJoin_itemid", "40308"));
			RedBlueJoin_count = Integer.parseInt(set.getProperty("RedBlueJoin_count", "100"));
			RedBluePc_amount = Integer.parseInt(set.getProperty("RedBluePc_amount", "5"));
			RedBlueLv_min = Integer.parseInt(set.getProperty("RedBlueLv_min", "70"));
			RedBlueLv_max = Integer.parseInt(set.getProperty("RedBlueLv_max", "99"));
			RedBlueTime_all = Integer.parseInt(set.getProperty("RedBlueTime_all", "600"));
			RedBlueTime_clear = Integer.parseInt(set.getProperty("RedBlueTime_clear", "1800"));
			RedBlueEffect_time = Integer.parseInt(set.getProperty("RedBlueEffect_time", "10"));
			RedBlueStart_point = Integer.parseInt(set.getProperty("RedBlueStart_point", "5"));
			RedBlueNormal_point = Integer.parseInt(set.getProperty("RedBlueNormal_point", "1"));
			RedBlueLeader_point = Integer.parseInt(set.getProperty("RedBlueLeader_point", "5"));
			RedBlueBonus_itemid = Integer.parseInt(set.getProperty("RedBlueBonus_itemid", "40308"));
			RedBlueBonus_count = Integer.parseInt(set.getProperty("RedBlueBonus_count", "1000"));
			
			String rb1 = set.getProperty("RedBlueEnd_map", "33080,33392,4");
			if (!rb1.equalsIgnoreCase("null")) {
				String[] rb11 = rb1.split(",");
				int[] rb111 = {Integer.valueOf(rb11[0]),Integer.valueOf(rb11[1]),Integer.valueOf(rb11[2])};
				RedBlueEnd_map = rb111;
			}
			String rb2 = set.getProperty("RedBlueRed_map1", "33080,33392,4");
			if (!rb2.equalsIgnoreCase("null")) {
				String[] rb22 = rb2.split(",");
				int[] rb222 = {Integer.valueOf(rb22[0]),Integer.valueOf(rb22[1]),Integer.valueOf(rb22[2])};
				RedBlueRed_map1 = rb222;
			}
			String rb3 = set.getProperty("RedBlueBlue_map1", "33080,33392,4");
			if (!rb3.equalsIgnoreCase("null")) {
				String[] rb33 = rb3.split(",");
				int[] rb333 = {Integer.valueOf(rb33[0]),Integer.valueOf(rb33[1]),Integer.valueOf(rb33[2])};
				RedBlueBlue_map1 = rb333;
			}
			String rb4 = set.getProperty("RedBlueRed_map2", "33080,33392,4");
			if (!rb4.equalsIgnoreCase("null")) {
				String[] rb44 = rb4.split(",");
				int[] rb444 = {Integer.valueOf(rb44[0]),Integer.valueOf(rb44[1]),Integer.valueOf(rb44[2])};
				RedBlueRed_map2 = rb444;
			}
			String rb5 = set.getProperty("RedBlueBlue_map2", "33080,33392,4");
			if (!rb5.equalsIgnoreCase("null")) {
				String[] rb55 = rb5.split(",");
				int[] rb555 = {Integer.valueOf(rb55[0]),Integer.valueOf(rb55[1]),Integer.valueOf(rb55[2])};
				RedBlueBlue_map2 = rb555;
			}
			
			// 自訂義回血魔區

			CUSTOM_HPR = Integer.parseInt(set.getProperty("costom_hpr", "20"));

			CUSTOM_MPR = Integer.parseInt(set.getProperty("costom_mpr", "20"));

			CUSTOM_MAPID = Integer.parseInt(set.getProperty("costom_mapid", "4"));

			// 是否驗證刷BOSS時間
			CHECK_SPAWN_BOSS = Boolean.parseBoolean(set.getProperty("check", "false"));

			// 首儲雙倍送開關
			DOUBLE_GIFT = Boolean.parseBoolean(set.getProperty("true", "false"));

			ENCOUNTER_LV = Integer.parseInt(set.getProperty("encounter_lv", "20"));

			KILLRED = Boolean.parseBoolean(set.getProperty("kill_red", "false"));

			RATE_XP_WHO = Integer.parseInt(set.getProperty("rate_xp_who", "1"));

			CLANDEL = Boolean.parseBoolean(set.getProperty("clanadel", "false"));

			CLANTITLE = Boolean.parseBoolean(set.getProperty("clanatitle", "false"));

			CLANCOUNT = Integer.parseInt(set.getProperty("clancount", "100"));

			// 啟用人物全時光照(true啟用 false關閉)
			LIGHT = Boolean.parseBoolean(set.getProperty("light", "false"));

			WEAPON100 = Integer.parseInt(set.getProperty("weapon100", "30"));
			ARMOR100 = Integer.parseInt(set.getProperty("armor100", "30"));

			// 顯示怪物血條(true啟用 false關閉)
			HPBAR = Boolean.parseBoolean(set.getProperty("hpbar", "false"));

			SHOPINFO = Boolean.parseBoolean(set.getProperty("shopinfo", "false"));

			HOMEHPR = Integer.parseInt(set.getProperty("homehpr", "100"));

			HOMEMPR = Integer.parseInt(set.getProperty("homempr", "100"));

			SET_GLOBAL = Integer.parseInt(set.getProperty("set_global", "100"));

			SET_GLOBAL_COUNT = Integer.parseInt(set.getProperty("set_global_count", "100"));

			SET_GLOBAL_TIME = Integer.parseInt(set.getProperty("set_global_time", "5"));

			WAR_DOLL = Boolean.parseBoolean(set.getProperty("war_doll", "true"));

			// 武器+9(含)以上附加額外增加傷害值 by terry0412
			/** 是否開啟 */
			WEAPON_POWER = Boolean.parseBoolean(set.getProperty("WeaponPower", "false"));

			/** 各階段強化值附加傷害 */
			if (WEAPON_POWER == true) {
				WEAPON_POWER_LIST = new int[] { Integer.parseInt(set.getProperty("WeaponPower09", "0")),
						Integer.parseInt(set.getProperty("WeaponPower10", "0")),
						Integer.parseInt(set.getProperty("WeaponPower11", "0")),
						Integer.parseInt(set.getProperty("WeaponPower12", "0")),
						Integer.parseInt(set.getProperty("WeaponPower13", "0")),
						Integer.parseInt(set.getProperty("WeaponPower14", "0")),
						Integer.parseInt(set.getProperty("WeaponPower15", "0")),
						Integer.parseInt(set.getProperty("WeaponPower16", "0")),
						Integer.parseInt(set.getProperty("WeaponPower17", "0")),
						Integer.parseInt(set.getProperty("WeaponPower18", "0")),
						Integer.parseInt(set.getProperty("WeaponPower19", "0")),
						Integer.parseInt(set.getProperty("WeaponPower20", "0")), };
			}

			// AI 懲罰金額設定
			ai_count = Integer.parseInt(set.getProperty("ai_count", "100000"));

			/** 加速偵測微調相關 */
			INJUSTICE_COUNT = Integer.parseInt(set.getProperty("InjusticeCount", "5"));

			JUSTICE_COUNT = Integer.parseInt(set.getProperty("JusticeCount", "5"));

			CHECK_STRICTNESS = Integer.parseInt(set.getProperty("CheckStrictness", "5"));

			CHECK_MOVE_STRICTNESS = Integer.parseInt(set.getProperty("CheckMoveStrictness", "5"));

			PUNISHMENT_TYPE = Integer.parseInt(set.getProperty("PunishmentType", "0"));
			PUNISHMENT_TIME = Integer.parseInt(set.getProperty("PunishmentTime", "0"));
			PUNISHMENT_MAP_ID = Integer.parseInt(set.getProperty("PunishmentMap", "0"));

			CHECK_MOVE_INTERVAL = Boolean.parseBoolean(set.getProperty("CheckMoveInterval", "false"));

			CHECK_ATTACK_INTERVAL = Boolean.parseBoolean(set.getProperty("CheckAttackInterval", "false"));

			CHECK_SPELL_INTERVAL = Boolean.parseBoolean(set.getProperty("CheckSpellInterval", "false"));

			LOGGING_ACCELERATOR = Boolean.parseBoolean(set.getProperty("LoggingAccelerator", "false"));

			// 2014/10/01 ByRoy新增 魔族保護卷軸(武防卷)可由config內控制強化上限
			ArmorSet = Integer.parseInt(set.getProperty("ArmorSet", "10"));
			// 2014/10/01 ByRoy新增 魔族保護卷軸(武防卷)可由config內控制強化上限
			WeaponSet = Integer.parseInt(set.getProperty("WeaponSet", "20"));

			// 150429 Smile 新增威望搶奪系統(開關與設定值)
			Prestigesnatch = Boolean.parseBoolean(set.getProperty("Prestigesnatch", "false"));
			// 同陣營搶奪積分設定
			camp1 = Double.parseDouble(set.getProperty("camp1", "0.25"));
			// 非同陣營搶奪積分設定
			camp2 = Double.parseDouble(set.getProperty("camp2", "0.5"));
			// 陣營等級加入限制外置設定
			CAMPLEVEL = Integer.parseInt(set.getProperty("CAMPLEVEL", "50"));
			// 陣營加入特效顯示外置設定
			CAMPGFX = Integer.parseInt(set.getProperty("CAMPGFX", "12335"));

			// 魂體轉換轉一次多少低魔 by erics4179
			BLOODY_SOULADDMP = Integer.parseInt(set.getProperty("BLOODY_SOULADDMP", "12"));

			// 首儲雙倍送開關
			DOUBLE_GIFT = Boolean.parseBoolean(set.getProperty("DoubleGift", "false"));

			// 全怪物皆有機率掉落所設定物品
			AllNpcDropItem = Boolean.parseBoolean(set.getProperty("AllNpcDropItem", "false"));
			Rnd = Integer.parseInt(set.getProperty("Rnd", "10"));
			ItemId = Integer.parseInt(set.getProperty("ItemId", "10"));
			ItemCount = Integer.parseInt(set.getProperty("ItemCount", "10"));
			
			// 強化防具公告
			StrengthenArmor = Integer.parseInt(set.getProperty("StrengthenArmor", "9"));
			
			// 強化武器公告
			StrengthenWeapon = Integer.parseInt(set.getProperty("StrengthenWeapon", "9"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + LIANG);

		} finally {
			set.clear();
		}
	}
}