package com.lineage.server;



import nick.forMYSQL.ControlBuffTable;
import nick.forMYSQL.ControlSoulTowerTable;
import nick.forMYSQL.ControlTeleportTable;
import nick.forMYSQL.N1AutoMaticConfigTable;
import nick.forMYSQL.NpcBuffSkills;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.config.ConfigSQL;
import com.lineage.data.item_etcitem.extra.ItemBuffTable;
import com.lineage.data.item_etcitem.extra.Reward;
import com.lineage.list.BadNamesList;
import com.lineage.server.datatables.ArmorSetTable;
import com.lineage.server.datatables.BeginnerTable;
import com.lineage.server.datatables.BlendTable;
import com.lineage.server.datatables.BuddyTable;
import com.lineage.server.datatables.CastleWarGiftTable;
import com.lineage.server.datatables.CharApprenticeTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.CommandsTable;
import com.lineage.server.datatables.DeClanTable;
import com.lineage.server.datatables.DeGlobalChatTable;
import com.lineage.server.datatables.DeNameTable;
import com.lineage.server.datatables.DeShopChatTable;
import com.lineage.server.datatables.DeShopItemTable;
import com.lineage.server.datatables.DeTitleTable;
import com.lineage.server.datatables.DollPowerTable;
import com.lineage.server.datatables.DoorSpawnTable;
import com.lineage.server.datatables.DropItemTable;
import com.lineage.server.datatables.DropMapTable;
import com.lineage.server.datatables.DropTable;
import com.lineage.server.datatables.DungeonRTable;
import com.lineage.server.datatables.DungeonTable;
import com.lineage.server.datatables.EventSpawnTable;
import com.lineage.server.datatables.EventTable;
import com.lineage.server.datatables.ExtraAttrWeaponTable;
import com.lineage.server.datatables.ExtraItemStealTable;
import com.lineage.server.datatables.ExtraMagicStoneTable;
import com.lineage.server.datatables.ExtraMagicWeaponTable;
import com.lineage.server.datatables.ExtraMeteAbilityTable;
import com.lineage.server.datatables.ExtraQuiz1SetTable;
import com.lineage.server.datatables.ExtraQuizSetTable;
import com.lineage.server.datatables.FishingTable;
import com.lineage.server.datatables.GetBackRestartTable;
import com.lineage.server.datatables.GetbackTable;
import com.lineage.server.datatables.GfxIdOrginal;
import com.lineage.server.datatables.ItemBoxTable;
import com.lineage.server.datatables.ItemIntegrationTable;
import com.lineage.server.datatables.ItemLimitation;
import com.lineage.server.datatables.ItemMsgTable;
import com.lineage.server.datatables.ItemPowerUpdateTable;
import com.lineage.server.datatables.ItemRestrictionsTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ItemTeleportTable;
import com.lineage.server.datatables.ItemTimeTable;
import com.lineage.server.datatables.MapExpTable;
import com.lineage.server.datatables.MapLevelTable;
import com.lineage.server.datatables.MapsGroupTable;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.MobGroupTable;
import com.lineage.server.datatables.MobSkillGroupTable;
import com.lineage.server.datatables.MonsterEnhanceTable;
import com.lineage.server.datatables.NPCTalkDataTable;
import com.lineage.server.datatables.NpcActionTable;
import com.lineage.server.datatables.NpcBoxTable;
import com.lineage.server.datatables.NpcChatTable;
import com.lineage.server.datatables.NpcScoreTable;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.NpcTeleportTable;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.PolyTable;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.datatables.ResolventTable;
import com.lineage.server.datatables.SceneryTable;
import com.lineage.server.datatables.ShopCnTable;
import com.lineage.server.datatables.ShopTable;
import com.lineage.server.datatables.SkillsItemTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SpawnTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.T_GameMallTable;
import com.lineage.server.datatables.T_OnlineGiftTable;
import com.lineage.server.datatables.T_RankTable;
import com.lineage.server.datatables.TrapTable;
import com.lineage.server.datatables.TrapsSpawn;
import com.lineage.server.datatables.VipSetsTable;
import com.lineage.server.datatables.WeaponPowerTable;
import com.lineage.server.datatables.WeaponSkillPowerTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.datatables.William_killnpc_quest;
import com.lineage.server.datatables.lock.AccountReading;
import com.lineage.server.datatables.lock.AuctionBoardReading;
import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.datatables.lock.BoardReading;
import com.lineage.server.datatables.lock.CastleReading;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.lock.CharItemPowerHoleReading;
import com.lineage.server.datatables.lock.CharItemPowerReading;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.datatables.lock.CharItemsTimeReading;
import com.lineage.server.datatables.lock.CharMapsTimeReading;
import com.lineage.server.datatables.lock.CharOtherReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.CharWeaponTimeReading;
import com.lineage.server.datatables.lock.CharacterConfigReading;
import com.lineage.server.datatables.lock.CharacterQuestReading;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.ClanRecommendReading;
import com.lineage.server.datatables.lock.DwarfForClanReading;
import com.lineage.server.datatables.lock.DwarfForElfReading;
import com.lineage.server.datatables.lock.DwarfReading;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.datatables.lock.MailReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.datatables.lock.ServerReading;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.datatables.lock.TownReading;
//import com.lineage.server.datatables.sql.CharWeaponTimeTable;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.datatables.sql.ClanStepTable;
import com.lineage.server.model.L1AttackList;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1ItemPower;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.TimeLimit.ShopTimeLimitTable;
import com.lineage.server.model.TimeLimit.TimeLimitCharTable;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.map.L1WorldMap;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.templates.L1PcOther;
import com.lineage.server.thread.DeAiThreadPool;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.thread.NpcAiThreadPool;
import com.lineage.server.timecontroller.StartTimer_Npc;
import com.lineage.server.timecontroller.StartTimer_Pc;
import com.lineage.server.timecontroller.StartTimer_Pet;
import com.lineage.server.timecontroller.StartTimer_Server;
import com.lineage.server.timecontroller.StartTimer_Skill;
import com.lineage.server.utils.PerformanceTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldCrown;
import com.lineage.server.world.WorldDarkelf;
import com.lineage.server.world.WorldDragonKnight;
import com.lineage.server.world.WorldElf;
import com.lineage.server.world.WorldIllusionist;
import com.lineage.server.world.WorldKnight;
import com.lineage.server.world.WorldPet;
import com.lineage.server.world.WorldSummons;
import com.lineage.server.world.WorldWarrior;
import com.lineage.server.world.WorldWizard;

/**
 * 加載服務器設置
 * 
 * @author dexc
 */
public class GameServer {

	private static final Log _log = LogFactory.getLog(GameServer.class);

	private static GameServer _instance;

	public static GameServer getInstance() {
		if (_instance == null) {
			_instance = new GameServer();
		}
		return _instance;
	}

	public final int startTime = (int) (System.currentTimeMillis() / 1000);

	public void initialize() throws Exception {
		final PerformanceTimer timer = new PerformanceTimer();
		try {
			_log.info("\n\r--------------------------------------------------" + 
					"\n\r       外部設置：經驗倍率: "
					+ ConfigRate.RATE_XP + "\n\r       外部設置：正義質倍率: " + ConfigRate.RATE_LA
					+ "\n\r       外部設置：有好度倍率: " + ConfigRate.RATE_KARMA
					+ "\n\r       外部設置：物品掉落倍率: "
					+ ConfigRate.RATE_DROP_ITEMS
					+ "\n\r       外部設置：金幣掉落倍率: "
					+ ConfigRate.RATE_DROP_ADENA
					+ "\n\r       外部設置：廣播等級限制: "
					+ ConfigAlt.GLOBAL_CHAT_LEVEL
					+ "\n\r       外部設置：PK設置: "
					+ (ConfigAlt.ALT_NONPVP ? "允許" : "不允許")
					+ "\n\r       外部設置：最大連線設置: "
					+ Config.MAX_ONLINE_USERS + "\n\r       外部設置：AI驗證設定: " + Config.AICHECK
					+ "\n\r--------------------------------------------------");
			
			ServerReading.get().load();// 載入保留的服務器資料

			IdFactory.get().load();// OBJID

			CharObjidTable.get().load();// 人物已用OBJID預先加載/血盟已用OBJID預先加載

			AccountReading.get().load();// 帳戶名稱資料

			GeneralThreadPool.get();// 線程工廠設置

			NpcAiThreadPool.get();// 線程工廠設置

			DeAiThreadPool.get();// 線程工廠設置

			SprTable.get().load();// 圖形影格資料

			MapsTable.get().load();// 地圖設置

			MapExpTable.get().load();// 地圖經驗設置

			MapLevelTable.get().load();// 地圖等級限制

			ItemTimeTable.get().load();// 物品可用時間指定

			L1WorldMap.get().load();// MAP

			L1GameTimeClock.init();// 遊戲時間時計

			NpcTable.get().load();// NPC資料

			NpcScoreTable.get().load();// NPC積分資料

			CharacterTable.loadAllCharName();// 載入已用名稱

			CharacterTable.clearOnlineStatus();// 全部狀態離線

			// 世界儲存中心資料建立
			World.get();

			WorldCrown.get();

			WorldKnight.get();

			WorldElf.get();

			WorldWizard.get();

			WorldDarkelf.get();

			WorldDragonKnight.get();

			WorldIllusionist.get();

			WorldWarrior.get();

			WorldPet.get();

			WorldSummons.get();
			
			TrapTable.get().load();// 陷阱資料

			TrapsSpawn.get().load();// 陷阱召喚資料

			ItemTable.get().load();// 道具物品資料

			DropTable.get().load();// 掉落物品資料

			DropMapTable.get().load();// 掉落物品資料

			DropItemTable.get().load();// 掉落物品機率資料

			SkillsTable.get().load();// 技能設置資料

			SkillsItemTable.get().load();// 購買技能 金幣/材料 設置資料

			MobGroupTable.get().load();// MOB隊伍資料

			SpawnTable.get().load();// 召喚清單

			PolyTable.get().load();// 人物變身資料

			ShopTable.get().load();// 商店販賣資料

			ShopCnTable.get().load();// 特殊商店販賣資料
			
			DungeonTable.get().load();// 地圖切換點設置

			DungeonRTable.get().load();// 地圖切換點設置(多點)

			NPCTalkDataTable.get().load();// NPC對話資料

			NpcSpawnTable.get().load();// 召喚NPC資料

			DwarfForClanReading.get().load();// 血盟倉庫資料建立

			ClanReading.get().load();// 血盟資料

			ClanEmblemReading.get().load();// 血盟盟輝資料

			ClanAllianceReading.get().load();// 血盟同盟資料 by terry0412

			CastleReading.get().load();// 城堡資料

			L1CastleLocation.setCastleTaxRate(); // 城堡稅收數據

			GetBackRestartTable.get().load();// 回城座標資料

			DoorSpawnTable.get().load();// 門資料

			WeaponSkillTable.get().load();// 技能武器資料

			WeaponSkillPowerTable.get().load();// 技能武器資料

			NpcActionTable.load();// NPC XML對話結果資料

			GetbackTable.loadGetBack();// 回村座標設置

			PetTypeTable.load();// 寵物種族資料

			PetItemTable.get().load();// 寵物道具資料

			ItemBoxTable.get().load();// 箱子開出物設置

			ResolventTable.get().load();// 溶解物品設置

			NpcTeleportTable.get().load();// NPC傳送點設置

			NpcChatTable.get().load();// NPC會話資料

			ArmorSetTable.get().load();// 套裝設置

			ItemTeleportTable.get().load();// 傳送捲軸傳送點定義

			ItemPowerUpdateTable.get().load();// 特殊物品升級資料

			CommandsTable.get().load();// GM命令

			BeginnerTable.get().load();// 新手物品資料

			ItemRestrictionsTable.get().load();// 物品交易限制清單

			// TODO 預先加載SQL資料
			
			// 召喚BOSS資料
			SpawnBossReading.get().load();

			// 血盟小屋
			HouseReading.get().load();

			// 禁止位置
			IpReading.get().load();

			// 村莊資料
			TownReading.get().load();

			// 信件資料
			MailReading.get().load();

			// 拍賣公告欄資料
			AuctionBoardReading.get().load();

			// 佈告欄資料
			BoardReading.get().load();

			// 保留技能紀錄
			CharBuffReading.get().load();

			// 人物技能紀錄
			CharSkillReading.get().load();

			// 人物快速鍵紀錄
			CharacterConfigReading.get().load();

			// 人物好友紀錄
			BuddyTable.getInstance();

			// 人物記憶座標紀錄資料
			CharBookReading.get().load();

			// 人物額外紀錄資料
			CharOtherReading.get().load();

			// 任務紀錄
			CharacterQuestReading.get().load();

			// 建立角色名稱時禁止使用的字元
			BadNamesList.get().load();

			// 景觀設置資料
			SceneryTable.get().load();

			// 各項技能設置啟用
			L1SkillMode.get().load();

			// 物理攻擊/魔法攻擊判定
			L1AttackList.load();

			// 物品能力值
			L1ItemPower.load();

			// 加載連續魔法減低損傷資料
			L1PcInstance.load();

			// 背包資料建立
			CharItemsReading.get().load();

			// 倉庫資料建立
			DwarfReading.get().load();

			// 精靈倉庫資料建立
			DwarfForElfReading.get().load();

			// 娃娃能力資料
			DollPowerTable.get().load();

			// 寵物資料
			PetReading.get().load();

			// 人物背包物品使用期限資料
			CharItemsTimeReading.get().load();

			// 人物其他相關設置表
			L1PcOther.load();

			// 各項特殊活動設置啟動
			EventTable.get().load();

			// 特殊活動設置召喚啟動
			if (EventTable.get().size() > 0) {
				EventSpawnTable.get().load();
			}

			// 任務(副本)地圖設置加載
			QuestMapTable.get().load();

			// 家具召喚資料
			FurnitureSpawnReading.get().load();

			// 打寶公告
			ItemMsgTable.get().load();

			// 武器額外傷害資料
			WeaponPowerTable.get().load();

			// 漁獲資料
			FishingTable.get().load();

			// 攻城獎勵
			CastleWarGiftTable.get().load();

			// PC檢查時間軸 by terry0412
			CheckTimeController.getInstance().start();

			// MOB技能組資料 by terry0412
			MobSkillGroupTable.get().load();

			// 載入資料 [轉生附加能力系統 (含轉生頭銜)] by terry0412
			ExtraMeteAbilityTable.getInstance().load();

			// 載入資料 [師徒系統] by terry0412
			CharApprenticeTable.getInstance().load();

			// 載入資料 [屬性武器系統(DB自製)] by terry0412
			ExtraAttrWeaponTable.getInstance().load();

			// 載入資料 [歐林佈告欄] by terry0412
			BoardOrimReading.get().load();

			// 建立資料 [地圖群組設置資料 (入場時間限制)] by terry0412
			MapsGroupTable.get().load();

			// 載入資料 [地圖入場時間紀錄] by terry0412
			CharMapsTimeReading.get().load();

			// 載入資料 [每日一題系統(DB自製)] by terry0412
			if (ConfigAlt.QUIZ_SET_SWITCH) {
				ExtraQuizSetTable.getInstance().load();
			}

			// 載入資料 [每日任務系統(DB自製)] by erics4179
			if (ConfigAlt.QUIZ_SET_SWITCH1) {
				ExtraQuiz1SetTable.getInstance().load();
			}

			// 載入資料 [NPC寶箱資料] by terry0412
			NpcBoxTable.get().load();

			// 載入資料 [道具奪取系統] by terry0412
			ExtraItemStealTable.getInstance().load();

			// 載入資料 [血盟推薦登陸紀錄] by terry0412
			ClanRecommendReading.get().load();

			// 載入資料 [魔法武器DIY系統(DB自製)] by terry0412
			ExtraMagicWeaponTable.getInstance().load();

			// 載入資料 [古文字系統紀錄] by terry0412
			CharItemPowerReading.get().load();

			// 載入資料 [人物物品凹槽資料] by terry0412
			CharItemPowerHoleReading.get().load();

			// 載入資料 [物品融合系統(DB自製)] by terry0412
			BlendTable.getInstance().load();

			// 特殊變身效果功能 2014/08/11 by Roy 新增
			GfxIdOrginal.get();

			// 載入資料 [寶石鑲嵌系統(DB自製)] by terry0412
			ExtraMagicStoneTable.getInstance().load();
			
			// 載入資料 [武器魔法DIY系統]
			CharWeaponTimeReading.get().load();
			
			// 道具能力設置 by Roy
//			ServerEtcItemTable.get();

			// 特殊裝備融合
			ItemIntegrationTable.load();

			// 掉落物總量管制
			ItemLimitation.get();

			// 怪物強化系統 erics4179
			MonsterEnhanceTable.getInstance().load();

			// 血盟技能階段化系統
			ClanStepTable.load();

			// 線上獎勵
			T_OnlineGiftTable.get();

			// 世界排行榜
			T_RankTable.get();

			VipSetsTable.get();

			T_GameMallTable.get();
			
			CharWeaponTimeReading.get().load();

			// 等級獎勵
			Reward.get();

			// 殺怪任務預先載入
			William_killnpc_quest.getInstance();
			
			// 限時商人
			ShopTimeLimitTable.getInstance().loadShops();

			// 限時商人個人限購資料
			TimeLimitCharTable.get().load();
			
			ItemBuffTable.load();
			
			NpcBuffSkills.load();
			
			ControlTeleportTable.get().load();
			
			ControlBuffTable.get().load();
			
			N1AutoMaticConfigTable.get().load();
			
			ControlSoulTowerTable.get().load();
			
			// TODO TIMER

			// 服務器專用時間軸
			final StartTimer_Server startTimer = new StartTimer_Server();
			startTimer.start();

			// PC專用時間軸
			final StartTimer_Pc pcTimer = new StartTimer_Pc();
			pcTimer.start();

			// NPC專用時間軸
			final StartTimer_Npc npcTimer = new StartTimer_Npc();
			npcTimer.start();

			// PET專用時間軸
			final StartTimer_Pet petTimer = new StartTimer_Pet();
			petTimer.start();

			// SKILL專用時間軸
			final StartTimer_Skill skillTimer = new StartTimer_Skill();
			skillTimer.start();

			// 設置關機導向
			Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

			// TODO 它向設置

			// 虛擬系統
			DeNameTable.get().load();

			DeClanTable.get().load();

			DeTitleTable.get().load();

			DeShopChatTable.get().load();

			DeGlobalChatTable.get().load();

			DeShopItemTable.get().load();

			// 監聽端口啟動重置作業
			EchoServerTimer.get().start();

			// 打開關閉的門
			L1DoorInstance.openDoor();
			
			// _log.info("遊戲伺服器啟動完成!!");

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			final String osname = System.getProperties().getProperty("os.name");
			final String username = System.getProperties().getProperty("user.name");

			final String ver = "\n\r--------------------------------------------------"
					+ "\n\r       遊戲伺服器核心版本: " + Config.VER + " " + Config.SRCVER
					+ "\n\r\n\r       Server Verion: " + Config.SVer + "\n\r       Cache  Verion: "
					+ Config.CVer + "\n\r       Npc    Verion: " + Config.NVer + "\n\r       Auth   Verion: "
					+ Config.AVer + "\n\r\n\r       主機位置: " + Config.GAME_SERVER_HOST_NAME
					+ "\n\r       監聽端口: " + Config.GAME_SERVER_PORT + "\n\r\n\r       伺服器作業系統: " + osname
					+ "\n\r       伺服器使用者: " + username + "\n\r       使用者名稱資料庫: " + ConfigSQL.DB_URL2_LOGIN
					+ "\n\r       伺服器檔案資料庫: " + ConfigSQL.DB_URL2 + "\n\r       綁定登入器設置: "
					+ Config.LOGINS_TO_AUTOENTICATION
					+ "\n\r--------------------------------------------------" 
					+ "\n\r       此程式碼為伊薇技術原作Chiang DaiEn建立"
					+ "\n\r       核心僅供當前使用者&團隊研究使用"
					+ "\n\r       並未限制當前使用者&團隊任何使用行為"
					+ "\n\r       任何違法行[原作與目前程式作者]不附連帶責任"
					+ "\n\r       伊薇7.6C版-程式作者：Chiang DaiEn"
					+ "\n\r       由 天堂交流群 發佈 伺服器版本號: v1.1"
					+ "\n\r--------------------------------------------------";
			_log.info(ver);

			// 啟動視窗命令接收器
			final CmdEcho cmdEcho = new CmdEcho(timer.get());
			cmdEcho.runCmd();
		}
	}
}