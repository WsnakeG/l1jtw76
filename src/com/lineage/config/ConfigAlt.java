package com.lineage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 服務器限制設置
 * 
 * @author dexc
 */
public final class ConfigAlt {

	private static final Log _log = LogFactory.getLog(ConfigAlt.class);

	/** AltSettings control */
	public static short GLOBAL_CHAT_LEVEL;

	public static short WHISPER_CHAT_LEVEL;

	/** 设定自动取得道具的方式0-掉落地上, 1-掉落宠物身上, 2-掉落角色身上 */
	public static byte AUTO_LOOT;

	public static int LOOTING_RANGE;

	/** 允許pk */
	public static boolean ALT_NONPVP;

	/** GM上線是否自動隱身 */
	public static boolean ALT_GM_HIDE;

	/** GM上線是否自動隱身 */
	public static boolean ALT_GM_LOGIN_MSG;

	/** PK處分 */
	public static boolean ALT_PUNISHMENT;

	/** 攻擊資訊 */
	public static boolean ALT_ATKMSG;

	/** 聯盟系統開關 */
	public static boolean CLAN_ALLIANCE;

	/** 地面物品自動刪除時間(單位:分鐘) */
	public static int ALT_ITEM_DELETION_TIME;

	// 指定範圍內有人物不執行清除 2012-05-07 異動為可見範圍內有玩家不執行清除
	// public static int ALT_ITEM_DELETION_RANGE;

	public static boolean ALT_WHO_COMMANDX;

	public static int ALT_WHO_TYPE;

	public static double ALT_WHO_COUNT;

	public static int ALT_WAR_TIME;

	public static int ALT_WAR_TIME_UNIT;

	public static int ALT_WAR_INTERVAL;

	public static int ALT_WAR_INTERVAL_UNIT;

	public static int ALT_RATE_OF_DUTY;

	public static boolean SPAWN_HOME_POINT;

	public static int SPAWN_HOME_POINT_RANGE;

	public static int SPAWN_HOME_POINT_COUNT;

	public static int SPAWN_HOME_POINT_DELAY;

	public static int ELEMENTAL_STONE_AMOUNT;

	public static int HOUSE_TAX_INTERVAL;

	public static int HOUSE_TAX_ADENA;

	/** 最大娃娃攜帶量 */
	public static int MAX_DOLL_COUNT;

	/** NPC(Summon, Pet)背包最大容量 */
	public static int MAX_NPC_ITEM;

	/** 個人倉庫容許收容數量 */
	public static int MAX_PERSONAL_WAREHOUSE_ITEM;

	/** 血盟倉庫容許收容數量 */
	public static int MAX_CLAN_WAREHOUSE_ITEM;

	/** 指定刪除保留7天時間等級 */
	public static int DELETE_CHARACTER_AFTER_LV;

	/** 指定等級刪除保留7天時間 */
	public static boolean DELETE_CHARACTER_AFTER_7DAYS;

	/** NPC死亡後刪除時間(單位:秒) */
	public static int NPC_DELETION_TIME;

	/** 初始可建立人物數量 */
	public static int DEFAULT_CHARACTER_SLOT;

	/** 萬能藥使用限制(數量) */
	public static int MEDICINE;

	/** 能力質上限 */
	public static int POWER;

	/** 萬能藥使用限制(能力質上限) */
	public static int POWERMEDICINE;

	/** 是否允許丟棄道具至地面true允許 false不允許 */
	public static boolean DORP_ITEM;

	/** 最大拖怪數量 */
	public static final int MAX_NPC = 35;

	/** 最高可組隊人數 by terry0412 */
	public static int MAX_PARTY_SIZE;

	// 轉生系統 by terry0412
	/** 轉生所需等級 */
	public static int METE_LEVEL;
	/** 最大允許轉生次數 */
	public static int METE_MAX_COUNT;
	/** 系統是否給予返生藥水 */
	public static boolean METE_GIVE_POTION;
	/**每次轉生血量保留 (%數)*/
	public static int METE_REMAIN_HP;
	/**每次轉生魔量保留 (%數)*/
	public static int METE_REMAIN_MP;

	// 師徒系統 by terry0412
	/** 啟動開關 */
	public static boolean APPRENTICE_SWITCH;
	/** XX級(含)以上的角色才可成為師父；未達XX級的角色才可成為徒弟 */
	public static int APPRENTICE_LEVEL;
	/** 師徒組隊中，師父將額外獲得徒弟打怪經驗值加成 (每組一個徒弟+XX%) */
	public static int APPRENTICE_EXP_BONUS;
	/** 徒弟達到指定等級後,畢業可獲得的道具編號 (守護傳承之鍊) */
	public static int APPRENTICE_ITEM_ID;

	// 魔族武器保護卷軸 by terry0412
	/** 強化成功機率 (強化值增加1) */
	public static int ELYOS_ENCHANT_SUCCESS;
	/** 強化失敗機率 (強化值歸零) */
	public static int ELYOS_ENCHANT_FAILURE;
	/** 強化失敗機率 (強化值倒扣1) */
	public static int ELYOS_ENCHANT_FAILURE2;

	// 魔族防具保護卷軸 by terry0412
	/** 強化成功機率 (強化值增加1) */
	public static int ELYOS2_ENCHANT_SUCCESS;
	/** 強化失敗機率 (強化值歸零) */
	public static int ELYOS2_ENCHANT_FAILURE;
	/** 強化失敗機率 (強化值倒扣1) */
	public static int ELYOS2_ENCHANT_FAILURE2;

	// [龍之鑰匙限制可開啟地圖清單] (請填入地圖編號) (如要輸入多項請以逗號隔開) by terry0412
	public static List<Integer> DRAGON_KEY_MAP_LIST = new ArrayList<Integer>();

	// [可丟給怪物的道具清單] (請填入道具編號) (如要輸入多項請以逗號隔開) by terry0412
	public static List<Integer> GIVE_ITEM_LIST = new ArrayList<Integer>();

	// [可丟給怪物的道具清單] (請填入道具編號) (如要輸入多項請以逗號隔開) by terry0412
	public static List<Integer> DROP_ITEM_LIST = new ArrayList<Integer>();

	// 武器加成特效 by terry0412
	/** 特效延遲時間 (單位:秒) */
	public static int WEAPON_EFFECT_DELAY;
	/** 武器額外持續特效1 (過3-過4) */
	public static int WEAPON_EFFECT_1;
	/** 武器額外持續特效2 (過5-過6) */
	public static int WEAPON_EFFECT_2;
	/** 武器額外持續特效3 (過7-過8) */
	public static int WEAPON_EFFECT_3;
	/** 武器額外持續特效4 (過9-過10) */
	public static int WEAPON_EFFECT_4;
	/** 武器額外持續特效5 (過11-過12) */
	public static int WEAPON_EFFECT_5;
	/** 武器額外持續特效6 (過13-過14) */
	public static int WEAPON_EFFECT_6;

	// 寵物最高等级限制 by terry0412
	public static int PET_MAX_LEVEL;

	// 寵物可使用進化道具的進化等級 by terry0412
	public static int PET_EVOLVE_LEVEL;

	// 每日一題系統 by terry0412
	/** 啟動開關 */
	public static boolean QUIZ_SET_SWITCH;
	/** 每日重置時間 (格式: hh:mm:ss) */
	public static Calendar QUIZ_SET_RESET_TIME;
	/** 每日重置後更換題目類型 (0 = 不換, 1 = 固定下一題, 2 = 隨機題目) */
	public static int QUIZ_SET_TYPE;
	/** 答題限制最低等級 */
	public static int QUIZ_SET_LEVEL;
	/** 答對贈送道具列表 (系統將隨機抽取其中之一來贈送) */
	public static int[][] QUIZ_SET_LIST;

	// 每日任務系統 by erics
	/** 啟動開關 */
	public static boolean QUIZ_SET_SWITCH1;
	/** 每日重置時間 (格式: hh:mm:ss) */
	public static Calendar QUIZ_SET_RESET_TIME1;
	/** 每日重置後更換題目類型 (0 = 不換, 1 = 固定下一題, 2 = 隨機題目) */
	public static int QUIZ_SET_TYPE1;
	/** 答題限制最低等級 */
	public static int QUIZ_SET_LEVEL1;
	/** 答對贈送道具列表 (系統將隨機抽取其中之一來贈送) */
	public static int[][] QUIZ_SET_LIST1;

	// 角色出生座標 (格式: locx, locy, mapid) (設置 null 則啟動內建出生座標) by terry0412
	public static int[] NEW_CHAR_LOC;

	// 元寶偵測紀錄 by terry0412
	/** 啟動開關 */
	public static boolean ADENA_CHECK_SWITCH;
	/** 每XX秒判斷一次 */
	public static int ADENA_CHECK_TIME_SEC;
	/** 差異數量達到多少以上才紀錄 (位置:\物品操作日誌\元寶差異紀錄) */
	public static int ADENA_CHECK_COUNT_DIFFER;

	// 道具成功升級公告 (可用%s對應玩家名稱, 第二個%s為升級後的道具名稱) by terry0412
	public static String ITEMS_UPDATE_MSG;

	// BOSS館
	public static int BossPlayermin;//最低人數
	public static int BossPlayermax;//最高人數
	public static int CleantimeSet;//清館時間
	public static int BossId1;//怪物編號 1-10
	public static int BossId2;
	public static int BossId3;
	public static int BossId4;
	public static int BossId5;
	public static int BossId6;
	public static int BossId7;
	public static int BossId8;
	public static int BossId9;
	public static int BossId10;
	public static int BossIdItemId;//進入所需道具編號
	public static int BossIdItemCount;//道具數量
	public static int bossroommapid;//BOSS館地圖編號
	public static int bossclearmap;//地圖清潔指定座標(務必設置)
	public static int bossmapid;//BOSS館地圖編號
	public static int bosslocx;//BOSS館地圖座標X
	public static int bosslocy;//BOSS館地圖座標Y
	

	public static int[] BossXYZ01 = { 32638, 32898, 5153 };
	public static int[] BossXYZ02 = { 32638, 32898, 5153 };
	public static int[] BossXYZ03 = { 32638, 32898, 5153 };
	public static int[] BossXYZ04 = { 32638, 32898, 5153 };
	public static int[] BossXYZ05 = { 32638, 32898, 5153 };
	public static int[] BossXYZ06 = { 32638, 32898, 5153 };
	public static int[] BossXYZ07 = { 32638, 32898, 5153 };
	public static int[] BossXYZ08 = { 32638, 32898, 5153 };
	public static int[] BossXYZ09 = { 32638, 32898, 5153 };
	public static int[] BossXYZ10 = { 32638, 32898, 5153 };

	// 是否開放給其他人看見[陣營稱號]和[轉生稱號] by terry0412
	public static boolean SHOW_SP_TITLE;

	// 玩家出生公告 (null = 不公告) by terry0412
	public static String CreateCharInfo;

	private static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";

	// 組隊任務加成 (數字=%)
	public static double PARTY_EXP_BONUS;

	// 轉生藥水使用時是否公告
	public static boolean ReincarnationBroad = false;

	// 專屬vip系統外置設定 --START-- //
	public static int _viplevel02;
	public static int _vipmapid02;
	public static int _vipmap02_locx;
	public static int _vipmap02_locy;
	public static int _viplevel03;
	public static int _vipmapid03;
	public static int _vipmap03_locx;
	public static int _vipmap03_locy;
	public static int _viplevel04;
	public static int _vipmapid04;
	public static int _vipmap04_locx;
	public static int _vipmap04_locy;
	public static int _viplevel05;
	public static int _vipmapid05;
	public static int _vipmap05_locx;
	public static int _vipmap05_locy;
	public static int _viplevel06;
	public static int _vipmapid06;
	public static int _vipmap06_locx;
	public static int _vipmap06_locy;
	public static int _viplevel07;
	public static int _vipmapid07;
	public static int _vipmap07_locx;
	public static int _vipmap07_locy;
	// 專屬vip系統外置設定 --END-- //
	
	// 血盟技能所需道具外置  //
	public static int Clanskillitem;  //道具編號
	public static int Clanskillcount; //道具數量
	
	// 送出封包 血盟技能特效-20人以下
	public static int showClanskill1;
	// 送出封包 血盟技能特效-35人以上
	public static int showClanskill2;
	
	// 全能力藥水使用後給予道具
	public static int Nostrumitem;  //給予道具編號
	public static int Nostrumcount; //給予道具數量
	
	// 古文字系統在掉落武防具時是否隨機給予
	public static boolean AncientSetDrop = false;
	
	// 屬性抵抗石 (道具編號外置)
    public static int ResistStone;
    
    // 屬性能力發動特效 (編號外置)
    public static int AttackPower1;
    public static int AttackPower2;
    public static int AttackPower3;
    public static int AttackPower4;
    public static int AttackPower5;
    public static int AttackPower6;
    public static int AttackPower7;
    public static int AttackPower8;
    
    // 屬性能力強化道具失敗是否歸零
 	public static boolean AttrSwitch = false;

	public static void load() throws ConfigErrorException {
		// _log.info("載入服務器限制設置!");
		final Properties set = new Properties();
		try {
			final InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
			// 指定檔案編碼
			final InputStreamReader isr = new InputStreamReader(is, "utf-8");
			set.load(isr);
			is.close();

			// GM上線是否隱身
			ALT_GM_HIDE = Boolean.parseBoolean(set.getProperty("GmHide", "true"));

			// 玩家上線是否通知GM
			ALT_GM_LOGIN_MSG = Boolean.parseBoolean(set.getProperty("GmLoginMsg", "true"));

			GLOBAL_CHAT_LEVEL = Short.parseShort(set.getProperty("GlobalChatLevel", "30"));

			WHISPER_CHAT_LEVEL = Short.parseShort(set.getProperty("WhisperChatLevel", "5"));

			AUTO_LOOT = Byte.parseByte(set.getProperty("AutoLoot", "2"));

			LOOTING_RANGE = Integer.parseInt(set.getProperty("LootingRange", "3"));

			ALT_NONPVP = Boolean.parseBoolean(set.getProperty("NonPvP", "true"));

			ALT_PUNISHMENT = Boolean.parseBoolean(set.getProperty("Punishment", "true"));

			CLAN_ALLIANCE = Boolean.parseBoolean(set.getProperty("ClanAlliance", "true"));

			ALT_ITEM_DELETION_TIME = Integer.parseInt(set.getProperty("ItemDeletionTime", "10"));
			if (ALT_ITEM_DELETION_TIME > 60) {
				ALT_ITEM_DELETION_TIME = 60;// 最大設置60分鐘
			}

			// ALT_ITEM_DELETION_RANGE =
			// Integer.parseInt(set.getProperty("ItemDeletionRange", "5"));

			ALT_WHO_COMMANDX = Boolean.parseBoolean(set.getProperty("WhoCommandx", "false"));

			// WHO 顯示 額外設置方式 0:對話視窗顯示 1:視窗顯示
			// 這一項設置必須在WhoCommandx = true才有作用
			ALT_WHO_TYPE = Integer.parseInt(set.getProperty("Who_type", "0"));

			ALT_WHO_COUNT = Double.parseDouble(set.getProperty("WhoCommandcount", "1.0"));
			if (ALT_WHO_COUNT < 1.0) {
				ALT_WHO_COUNT = 1.0;
			}

			String strWar;
			strWar = set.getProperty("WarTime", "2h");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");

			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");

			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_TIME_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}

			ALT_WAR_TIME = Integer.parseInt(strWar);
			strWar = set.getProperty("WarInterval", "4d");
			if (strWar.indexOf("d") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
				strWar = strWar.replace("d", "");

			} else if (strWar.indexOf("h") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
				strWar = strWar.replace("h", "");

			} else if (strWar.indexOf("m") >= 0) {
				ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
				strWar = strWar.replace("m", "");
			}

			ALT_WAR_INTERVAL = Integer.parseInt(strWar);

			SPAWN_HOME_POINT = Boolean.parseBoolean(set.getProperty("SpawnHomePoint", "true"));

			SPAWN_HOME_POINT_COUNT = Integer.parseInt(set.getProperty("SpawnHomePointCount", "2"));

			SPAWN_HOME_POINT_DELAY = Integer.parseInt(set.getProperty("SpawnHomePointDelay", "100"));

			SPAWN_HOME_POINT_RANGE = Integer.parseInt(set.getProperty("SpawnHomePointRange", "8"));

			ELEMENTAL_STONE_AMOUNT = Integer.parseInt(set.getProperty("ElementalStoneAmount", "300"));

			HOUSE_TAX_INTERVAL = Integer.parseInt(set.getProperty("HouseTaxInterval", "10"));

			HOUSE_TAX_ADENA = Integer.parseInt(set.getProperty("HouseTaxAdena", "2000"));

			MAX_DOLL_COUNT = Integer.parseInt(set.getProperty("MaxDollCount", "1"));

			MAX_NPC_ITEM = Integer.parseInt(set.getProperty("MaxNpcItem", "8"));

			MAX_PERSONAL_WAREHOUSE_ITEM = Integer
					.parseInt(set.getProperty("MaxPersonalWarehouseItem", "100"));

			MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(set.getProperty("MaxClanWarehouseItem", "200"));

			DELETE_CHARACTER_AFTER_LV = Integer.parseInt(set.getProperty("DeleteCharacterAfterLV", "60"));

			DELETE_CHARACTER_AFTER_7DAYS = Boolean
					.parseBoolean(set.getProperty("DeleteCharacterAfter7Days", "True"));

			NPC_DELETION_TIME = Integer.parseInt(set.getProperty("NpcDeletionTime", "10"));

			DEFAULT_CHARACTER_SLOT = Integer.parseInt(set.getProperty("DefaultCharacterSlot", "4"));

			MEDICINE = Integer.parseInt(set.getProperty("Medicine", "20"));

			POWER = Integer.parseInt(set.getProperty("Power", "35"));

			POWERMEDICINE = Integer.parseInt(set.getProperty("MedicinePower", "45"));

			DORP_ITEM = Boolean.parseBoolean(set.getProperty("dorpitem", "true"));

			/** 最高可組隊人數 by terry0412 */
			MAX_PARTY_SIZE = Integer.parseInt(set.getProperty("MaxPT", "8"));

			// 轉生系統 by terry0412
			/** 轉生所需等級 */
			METE_LEVEL = Integer.parseInt(set.getProperty("MeteLevel", "99"));
			/** 最大允許轉生次數 */
			METE_MAX_COUNT = Integer.parseInt(set.getProperty("MeteMaxCount", "20"));
			/** 系統是否給予返生藥水 */
			METE_GIVE_POTION = Boolean.parseBoolean(set.getProperty("MeteGivePotion", "false"));
			/**每次轉生血量保留 (%數)*/
			METE_REMAIN_HP = Integer.parseInt(set.getProperty("MeteRemainHp", "15"));
			/**每次轉生魔量保留 (%數)*/
			METE_REMAIN_MP = Integer.parseInt(set.getProperty("MeteRemainMp", "15"));

			// 師徒系統 by terry0412
			/** 啟動開關 */
			APPRENTICE_SWITCH = Boolean.parseBoolean(set.getProperty("ApprenticeSwitch", "false"));
			/** XX級(含)以上的角色才可成為師父；未達XX級的角色才可成為徒弟 */
			APPRENTICE_LEVEL = Integer.parseInt(set.getProperty("ApprenticeLevel", "70"));
			/** 師徒組隊中，師父將額外獲得徒弟打怪經驗值加成 (每組一個徒弟+XX%) */
			APPRENTICE_EXP_BONUS = Integer.parseInt(set.getProperty("ApprenticeExpBonus", "0"));
			/** 徒弟達到指定等級後,畢業可獲得的道具編號 (守護傳承之鍊) */
			APPRENTICE_ITEM_ID = Integer.parseInt(set.getProperty("ApprenticeItemId", "30125"));

			// 魔族武器保護卷軸 by terry0412
			/** 強化成功機率 (強化值增加1) */
			ELYOS_ENCHANT_SUCCESS = Integer.parseInt(set.getProperty("ElyosEnchantSuccess", "0"));
			/** 強化失敗機率 (強化值歸零) */
			ELYOS_ENCHANT_FAILURE = Integer.parseInt(set.getProperty("ElyosEnchantFailure", "0"));
			/** 強化失敗機率 (強化值倒扣1) */
			ELYOS_ENCHANT_FAILURE2 = Integer.parseInt(set.getProperty("ElyosEnchantFailure2", "0"));

			// 魔族防具保護卷軸 by terry0412
			/** 強化成功機率 (強化值增加1) */
			ELYOS2_ENCHANT_SUCCESS = Integer.parseInt(set.getProperty("Elyos2EnchantSuccess", "0"));
			/** 強化失敗機率 (強化值歸零) */
			ELYOS2_ENCHANT_FAILURE = Integer.parseInt(set.getProperty("Elyos2EnchantFailure", "0"));
			/** 強化失敗機率 (強化值倒扣1) */
			ELYOS2_ENCHANT_FAILURE2 = Integer.parseInt(set.getProperty("Elyos2EnchantFailure2", "0"));

			// [龍之鑰匙限制可開啟地圖清單] (請填入地圖編號) (如要輸入多項請以逗號隔開) by terry0412
			if (set.getProperty("DragonKeyMapList") != null) {
				for (final String str : set.getProperty("DragonKeyMapList").split(",")) {
					DRAGON_KEY_MAP_LIST.add(Integer.parseInt(str));
				}
			}

			// [可丟給怪物的道具清單] (請填入道具編號) (如要輸入多項請以逗號隔開) by terry0412
			if (set.getProperty("GiveItemList") != null) {
				for (final String str : set.getProperty("GiveItemList").split(",")) {
					GIVE_ITEM_LIST.add(Integer.parseInt(str));
				}
			}

			// [可丟給怪物的道具清單] (請填入道具編號) (如要輸入多項請以逗號隔開) by terry0412
			if (set.getProperty("DropItemList") != null) {
				for (final String str : set.getProperty("DropItemList").split(",")) {
					DROP_ITEM_LIST.add(Integer.parseInt(str));
				}
			}

			// 武器加成特效 by terry0412
			/** 特效延遲時間 (單位:秒) */
			WEAPON_EFFECT_DELAY = Integer.parseInt(set.getProperty("WeaponEffectDelay", "0"));
			/** 武器額外持續特效1 (過3-過4) */
			WEAPON_EFFECT_1 = Integer.parseInt(set.getProperty("WeaponEffect1", "-1"));
			/** 武器額外持續特效2 (過5-過6) */
			WEAPON_EFFECT_2 = Integer.parseInt(set.getProperty("WeaponEffect2", "-1"));
			/** 武器額外持續特效3 (過7-過8) */
			WEAPON_EFFECT_3 = Integer.parseInt(set.getProperty("WeaponEffect3", "-1"));
			/** 武器額外持續特效4 (過9-過10) */
			WEAPON_EFFECT_4 = Integer.parseInt(set.getProperty("WeaponEffect4", "-1"));
			/** 武器額外持續特效5 (過11-過12) */
			WEAPON_EFFECT_5 = Integer.parseInt(set.getProperty("WeaponEffect5", "-1"));
			/** 武器額外持續特效6 (過13-過14) */
			WEAPON_EFFECT_6 = Integer.parseInt(set.getProperty("WeaponEffect6", "-1"));

			// 寵物最高等级限制 by terry0412
			PET_MAX_LEVEL = Integer.parseInt(set.getProperty("PetMaxLevel", "0"));

			// 寵物可使用進化道具的進化等級 by terry0412
			PET_EVOLVE_LEVEL = Integer.parseInt(set.getProperty("PetEvolveLevel", "30"));

			// 每日一題系統 by terry0412
			/** 啟動開關 */
			QUIZ_SET_SWITCH = Boolean.parseBoolean(set.getProperty("QuizSetSwitch", "false"));
			/** 每日重置時間 (格式: hh:mm:ss) */
			final String tmp10 = set.getProperty("QuizSetResetTime", "");
			if (!tmp10.equalsIgnoreCase("null")) {
				final String[] temp = tmp10.split(":");
				if (temp.length == 3) { // 固定值: 3
					final Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
					cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
					cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));

					QUIZ_SET_RESET_TIME = cal;

				} else {
					_log.info("[每日一題系統] 重置時間有誤, 請重新設置!");
				}
			}
			/** 每日重置後更換題目類型 (0 = 不換, 1 = 固定下一題, 2 = 隨機題目) */
			QUIZ_SET_TYPE = Integer.parseInt(set.getProperty("QuizSetType", "1"));
			/** 答題限制最低等級 */
			QUIZ_SET_LEVEL = Integer.parseInt(set.getProperty("QuizSetLevel", "0"));
			/** 答對贈送道具列表 (系統將隨機抽取其中之一來贈送) */
			final String tmp11 = set.getProperty("QuizSetList", "");
			if (!tmp11.equalsIgnoreCase("null")) {
				final String[] temp1 = tmp11.split(",");
				final int size = temp1.length;
				QUIZ_SET_LIST = new int[size][2];

				for (int i = 0; i < size; i++) {
					final String[] temp2 = temp1[i].split(":");
					QUIZ_SET_LIST[i][0] = Integer.valueOf(temp2[0]);
					QUIZ_SET_LIST[i][1] = Integer.valueOf(temp2[1]);
				}
			}

			// 每日任務系統 by erics4179
			/** 啟動開關 */
			QUIZ_SET_SWITCH1 = Boolean.parseBoolean(set.getProperty("QuizSetSwitch", "false"));
			/** 每日重置時間 (格式: hh:mm:ss) */
			final String tmp101 = set.getProperty("QuizSetResetTime", "");
			if (!tmp101.equalsIgnoreCase("null")) {
				final String[] temp = tmp101.split(":");
				if (temp.length == 3) { // 固定值: 3
					final Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
					cal.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
					cal.set(Calendar.SECOND, Integer.parseInt(temp[2]));

					QUIZ_SET_RESET_TIME1 = cal;

				} else {
					_log.info("[每日任務系統] 重置時間有誤, 請重新設置!");
				}
			}
			/** 每日重置後更換題目類型 (0 = 不換, 1 = 固定下一題, 2 = 隨機題目) */
			QUIZ_SET_TYPE1 = Integer.parseInt(set.getProperty("QuizSetType1", "1"));
			/** 答題限制最低等級 */
			QUIZ_SET_LEVEL1 = Integer.parseInt(set.getProperty("QuizSetLevel1", "0"));
			/** 答對贈送道具列表 (系統將隨機抽取其中之一來贈送) */
			final String tmp111 = set.getProperty("QuizSetList1", "");
			if (!tmp111.equalsIgnoreCase("null")) {
				final String[] temp01 = tmp111.split(",");
				final int size = temp01.length;
				QUIZ_SET_LIST1 = new int[size][2];

				for (int i = 0; i < size; i++) {
					final String[] temp02 = temp01[i].split(":");
					QUIZ_SET_LIST1[i][0] = Integer.valueOf(temp02[0]);
					QUIZ_SET_LIST1[i][1] = Integer.valueOf(temp02[1]);
				}
			}

			// 角色出生座標 (格式: locx, locy, mapid) (設置 null 則啟動內建出生座標) by terry0412
			final String tmp12 = set.getProperty("NewCharLoc", "");
			if (!tmp12.equalsIgnoreCase("null")) {
				final String[] temp = tmp12.trim().split(","); // 去掉空白再分開
				if (temp.length == 3) { // 固定值: 3
					NEW_CHAR_LOC = new int[3];
					NEW_CHAR_LOC[0] = Integer.parseInt(temp[0]);
					NEW_CHAR_LOC[1] = Integer.parseInt(temp[1]);
					NEW_CHAR_LOC[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[角色出生座標] 座標格式有誤, 請重新設置!");
				}
			}

			// 元寶偵測紀錄 by terry0412
			/** 啟動開關 */
			ADENA_CHECK_SWITCH = Boolean.parseBoolean(set.getProperty("AdenaCheckSwitch", "false"));
			/** 每XX秒判斷一次 */
			ADENA_CHECK_TIME_SEC = Integer.parseInt(set.getProperty("AdenaCheckTimeSec", "5"));
			/** 差異數量達到多少以上才紀錄 (位置:\物品操作日誌\元寶差異紀錄) */
			ADENA_CHECK_COUNT_DIFFER = Integer.parseInt(set.getProperty("AdenaCheckCountDiffer", "100"));

			// 道具成功升級公告 (可用%s對應玩家名稱, 第二個%s為升級後的道具名稱) by terry0412
			ITEMS_UPDATE_MSG = set.getProperty("ItemsUpdateMsg", "");

			// BOSS館遊戲設定
			CleantimeSet = Short.parseShort(set.getProperty("CleantimeSet", "3600"));
			BossPlayermin = Integer.parseInt(set.getProperty("BossPlayermin", "5"));
			BossPlayermax = Integer.parseInt(set.getProperty("BossPlayermax", "10"));
			BossId1 = Integer.parseInt(set.getProperty("BossId1", "45573"));
			BossId2 = Integer.parseInt(set.getProperty("BossId2", "45583"));
			BossId3 = Integer.parseInt(set.getProperty("BossId3", "45584"));
			BossId4 = Integer.parseInt(set.getProperty("BossId4", "45600"));
			BossId5 = Integer.parseInt(set.getProperty("BossId5", "45601"));
			BossId6 = Integer.parseInt(set.getProperty("BossId6", "45610"));
			BossId7 = Integer.parseInt(set.getProperty("BossId7", "45649"));
			BossId8 = Integer.parseInt(set.getProperty("BossId8", "45673"));
			BossId9 = Integer.parseInt(set.getProperty("BossId9", "81163"));
			BossId10 = Integer.parseInt(set.getProperty("BossId10", "45684"));
			BossIdItemId = Integer.parseInt(set.getProperty("BossIdItemId", "40308"));
			BossIdItemCount = Integer.parseInt(set.getProperty("BossIdItemCount", "100000"));
			
			// BOSS館地圖設定
			bossroommapid = Integer.parseInt(set.getProperty("bossroommapid", "5153"));
			bossclearmap = Integer.parseInt(set.getProperty("bossclearmap", "5153"));
			bossmapid = Integer.parseInt(set.getProperty("bossmapid", "5153"));
			bosslocx = Integer.parseInt(set.getProperty("bosslocx", "32638"));
			bosslocy = Integer.parseInt(set.getProperty("bosslocy", "32898"));
            
			// BOSS館怪物出生座標
			final String tmpb1 = set.getProperty("BossXYZ01", "");
			if (!tmpb1.equalsIgnoreCase("null")) {
				final String[] temp = tmpb1.trim().split(",");
				if (temp.length == 3) {
					BossXYZ01 = new int[3];
					BossXYZ01[0] = Integer.parseInt(temp[0]);
					BossXYZ01[1] = Integer.parseInt(temp[1]);
					BossXYZ01[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 1座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb2 = set.getProperty("BossXYZ02", "");
			if (!tmpb2.equalsIgnoreCase("null")) {
				final String[] temp = tmpb2.trim().split(",");
				if (temp.length == 3) {
					BossXYZ02 = new int[3];
					BossXYZ02[0] = Integer.parseInt(temp[0]);
					BossXYZ02[1] = Integer.parseInt(temp[1]);
					BossXYZ02[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 2座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb3 = set.getProperty("BossXYZ03", "");
			if (!tmpb3.equalsIgnoreCase("null")) {
				final String[] temp = tmpb3.trim().split(",");
				if (temp.length == 3) {
					BossXYZ03 = new int[3];
					BossXYZ03[0] = Integer.parseInt(temp[0]);
					BossXYZ03[1] = Integer.parseInt(temp[1]);
					BossXYZ03[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 3座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb4 = set.getProperty("BossXYZ04", "");
			if (!tmpb4.equalsIgnoreCase("null")) {
				final String[] temp = tmpb4.trim().split(",");
				if (temp.length == 3) {
					BossXYZ04 = new int[3];
					BossXYZ04[0] = Integer.parseInt(temp[0]);
					BossXYZ04[1] = Integer.parseInt(temp[1]);
					BossXYZ04[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 4座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb5 = set.getProperty("BossXYZ05", "");
			if (!tmpb5.equalsIgnoreCase("null")) {
				final String[] temp = tmpb5.trim().split(",");
				if (temp.length == 3) {
					BossXYZ05 = new int[3];
					BossXYZ05[0] = Integer.parseInt(temp[0]);
					BossXYZ05[1] = Integer.parseInt(temp[1]);
					BossXYZ05[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 5座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb6 = set.getProperty("BossXYZ06", "");
			if (!tmpb6.equalsIgnoreCase("null")) {
				final String[] temp = tmpb6.trim().split(",");
				if (temp.length == 3) {
					BossXYZ06 = new int[3];
					BossXYZ06[0] = Integer.parseInt(temp[0]);
					BossXYZ06[1] = Integer.parseInt(temp[1]);
					BossXYZ06[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 6座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb7 = set.getProperty("BossXYZ07", "");
			if (!tmpb7.equalsIgnoreCase("null")) {
				final String[] temp = tmpb7.trim().split(",");
				if (temp.length == 3) {
					BossXYZ07 = new int[3];
					BossXYZ07[0] = Integer.parseInt(temp[0]);
					BossXYZ07[1] = Integer.parseInt(temp[1]);
					BossXYZ07[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 7座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb8 = set.getProperty("BossXYZ08", "");
			if (!tmpb8.equalsIgnoreCase("null")) {
				final String[] temp = tmpb8.trim().split(",");
				if (temp.length == 3) {
					BossXYZ08 = new int[3];
					BossXYZ08[0] = Integer.parseInt(temp[0]);
					BossXYZ08[1] = Integer.parseInt(temp[1]);
					BossXYZ08[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 8座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb9 = set.getProperty("BossXYZ09", "");
			if (!tmpb9.equalsIgnoreCase("null")) {
				final String[] temp = tmpb9.trim().split(",");
				if (temp.length == 3) {
					BossXYZ09 = new int[3];
					BossXYZ09[0] = Integer.parseInt(temp[0]);
					BossXYZ09[1] = Integer.parseInt(temp[1]);
					BossXYZ09[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 9座標] 座標格式有誤, 請重新設置!");
				}
			}
			final String tmpb10 = set.getProperty("BossXYZ10", "");
			if (!tmpb10.equalsIgnoreCase("null")) {
				final String[] temp = tmpb10.trim().split(",");
				if (temp.length == 3) {
					BossXYZ10 = new int[3];
					BossXYZ10[0] = Integer.parseInt(temp[0]);
					BossXYZ10[1] = Integer.parseInt(temp[1]);
					BossXYZ10[2] = Integer.parseInt(temp[2]);

				} else {
					_log.info("[BOSS館 10座標] 座標格式有誤, 請重新設置!");
				}
			}

			// 是否開放給其他人看見[陣營稱號]和[轉生稱號] by terry0412
			SHOW_SP_TITLE = Boolean.parseBoolean(set.getProperty("ShowSpTitle", "false"));

			// 玩家喝下轉生藥水時是否公告
			ReincarnationBroad = Boolean.parseBoolean(set.getProperty("ReincarnationBroad", "false"));

			// 玩家出生公告 (null = 不公告) by terry0412
			final String tmp13 = set.getProperty("CreateCharInfo", "");
			if (!tmp13.equalsIgnoreCase("null")) {
				CreateCharInfo = tmp13;
			}

			// 組隊任務加成 (數字=%)
			PARTY_EXP_BONUS = Integer.parseInt(set.getProperty("PartyExpBonus", "4"));
			
			// 專屬vip系統外置設定 --START-- //
			_viplevel02 = Integer.parseInt(set.getProperty("viplevel02", "1"));
			_vipmapid02 = Integer.parseInt(set.getProperty("vipmapid02", "4"));
			_vipmap02_locx = Integer.parseInt(set.getProperty("vipmap02_locx", "32704"));
			_vipmap02_locy = Integer.parseInt(set.getProperty("vipmap02_locy", "32768"));
			
			_viplevel03 = Integer.parseInt(set.getProperty("viplevel03", "1"));
			_vipmapid03 = Integer.parseInt(set.getProperty("vipmapid03", "4"));
			_vipmap03_locx = Integer.parseInt(set.getProperty("vipmap03_locx", "32704"));
			_vipmap03_locy = Integer.parseInt(set.getProperty("vipmap03_locy", "32768"));
			
			_viplevel04 = Integer.parseInt(set.getProperty("viplevel04", "1"));
			_vipmapid04 = Integer.parseInt(set.getProperty("vipmapid04", "4"));
			_vipmap04_locx = Integer.parseInt(set.getProperty("vipmap04_locx", "32704"));
			_vipmap04_locy = Integer.parseInt(set.getProperty("vipmap04_locy", "32768"));
			
			_viplevel05 = Integer.parseInt(set.getProperty("viplevel05", "1"));
			_vipmapid05 = Integer.parseInt(set.getProperty("vipmapid05", "4"));
			_vipmap05_locx = Integer.parseInt(set.getProperty("vipmap05_locx", "32704"));
			_vipmap05_locy = Integer.parseInt(set.getProperty("vipmap05_locy", "32768"));
			
			_viplevel06 = Integer.parseInt(set.getProperty("viplevel06", "1"));
			_vipmapid06 = Integer.parseInt(set.getProperty("vipmapid06", "4"));
			_vipmap06_locx = Integer.parseInt(set.getProperty("vipmap06_locx", "32704"));
			_vipmap06_locy = Integer.parseInt(set.getProperty("vipmap06_locy", "32768"));
			
			_viplevel07 = Integer.parseInt(set.getProperty("viplevel07", "1"));
			_vipmapid07 = Integer.parseInt(set.getProperty("vipmapid07", "4"));
			_vipmap07_locx = Integer.parseInt(set.getProperty("vipmap07_locx", "32704"));
			_vipmap07_locy = Integer.parseInt(set.getProperty("vipmap07_locy", "32768"));
			// 專屬vip系統外置設定 --END-- //
			
			// 轉生所需等級
			METE_LEVEL = Integer.parseInt(set.getProperty("MeteLevel", "99"));
			
			// 血盟人數20人以下送出特效
			showClanskill1 = Integer.parseInt(set.getProperty("showClanskill1", "16171"));
			// 血盟人數35人以上送出特效
			showClanskill2 = Integer.parseInt(set.getProperty("showClanskill2", "16169"));
			
			// 血盟技能所需道具編號(外置)
			Clanskillitem = Integer.parseInt(set.getProperty("Clanskillitem", "240959"));
			// 血盟技能所需道具數量(外置)
			Clanskillcount = Integer.parseInt(set.getProperty("Clanskillcount", "1"));
			
			// 全能力藥水使用後給予道具(外置)
			Nostrumitem = Integer.parseInt(set.getProperty("Nostrumitem", "40308"));
			// 全能力藥水使用後給予數量(外置)
			Nostrumcount = Integer.parseInt(set.getProperty("Nostrumcount", "1"));
			
			// 古文字系統在掉落武防具時是否隨機給予
			AncientSetDrop = Boolean.parseBoolean(set.getProperty("AncientSetDrop", "false"));
			
			// 屬性抵抗石 (道具編號外置)
		    ResistStone = Integer.parseInt(set.getProperty("ResistStone", "44073"));
		    
		    // 屬性能力發動特效 (編號外置) 地/水/火/風/光/冰/解除武器/解除防具
		    AttackPower1 = Integer.parseInt(set.getProperty("AttackPower1", "16231"));
		    AttackPower2 = Integer.parseInt(set.getProperty("AttackPower2", "16176"));
		    AttackPower3 = Integer.parseInt(set.getProperty("AttackPower3", "16177"));
		    AttackPower4 = Integer.parseInt(set.getProperty("AttackPower4", "16216"));
		    AttackPower5 = Integer.parseInt(set.getProperty("AttackPower5", "555"));
		    AttackPower6 = Integer.parseInt(set.getProperty("AttackPower6", "16178"));
		    AttackPower7 = Integer.parseInt(set.getProperty("AttackPower7", "16186"));
		    AttackPower8 = Integer.parseInt(set.getProperty("AttackPower8", "16186"));
		    
		    // 屬性能力強化道具失敗是否歸零
		 	AttrSwitch = Boolean.parseBoolean(set.getProperty("AttrSwitch", "false"));

		} catch (final Exception e) {
			throw new ConfigErrorException("設置檔案遺失: " + ALT_SETTINGS_FILE);

		} finally {
			set.clear();
		}
	}
}