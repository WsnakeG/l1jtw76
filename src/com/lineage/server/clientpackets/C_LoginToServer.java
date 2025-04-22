package com.lineage.server.clientpackets;

import static com.lineage.server.model.skill.L1SkillId.ADENA_CHECK_TIMER;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.data.event.CampSet;
import com.lineage.data.event.CardSet;
import com.lineage.data.event.EffectAISet;
import com.lineage.data.event.LeavesSet;
import com.lineage.data.event.OnlineGiftSet;
import com.lineage.data.event.ProtectorSet;
import com.lineage.data.item_armor.set.ArmorSet;
import com.lineage.data.npc.Npc_clan;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.ArmorSetTable;
import com.lineage.server.datatables.C1_Name_Table;
import com.lineage.server.datatables.CastleWarGiftTable;
import com.lineage.server.datatables.CharApprenticeTable;
import com.lineage.server.datatables.GetBackRestartTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.T_OnlineGiftTable;
import com.lineage.server.datatables.lock.CharBookReading;
import com.lineage.server.datatables.lock.CharBuffReading;
import com.lineage.server.datatables.lock.CharMapsTimeReading;
import com.lineage.server.datatables.lock.CharSkillReading;
import com.lineage.server.datatables.lock.CharacterC1Reading;
import com.lineage.server.datatables.lock.CharacterConfigReading;
import com.lineage.server.datatables.lock.ClanRecommendReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.serverpackets.S_AddSkill;
import com.lineage.server.serverpackets.S_BookmarkList;
import com.lineage.server.serverpackets.S_CastleMaster;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_CharReset;
import com.lineage.server.serverpackets.S_CharResetInfo;
import com.lineage.server.serverpackets.S_CreateName;
import com.lineage.server.serverpackets.S_EnterGame;
import com.lineage.server.serverpackets.S_EquipmentSlot;
import com.lineage.server.serverpackets.S_InitialAbilityGrowth;
import com.lineage.server.serverpackets.S_InvList;
import com.lineage.server.serverpackets.S_Invis;
import com.lineage.server.serverpackets.S_Karma;
import com.lineage.server.serverpackets.S_Mail;
import com.lineage.server.serverpackets.S_MapID;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_NewMaster;
import com.lineage.server.serverpackets.S_OtherCharPacks;
import com.lineage.server.serverpackets.S_OwnCharPack;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxActiveSpells;
import com.lineage.server.serverpackets.S_PacketBoxCharEr;
import com.lineage.server.serverpackets.S_PacketBoxConfig;
import com.lineage.server.serverpackets.S_PacketBoxExp;
import com.lineage.server.serverpackets.S_PacketBoxIcon1;
import com.lineage.server.serverpackets.S_PacketBoxProtection;
import com.lineage.server.serverpackets.S_PledgeName;
import com.lineage.server.serverpackets.S_PledgeUI;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_War;
import com.lineage.server.serverpackets.S_WarriorSkill;
import com.lineage.server.serverpackets.S_Weather;
import com.lineage.server.serverpackets.ability.S_BaseAbility;
import com.lineage.server.serverpackets.ability.S_BaseAbilityDetails;
import com.lineage.server.serverpackets.ability.S_ConDetails;
import com.lineage.server.serverpackets.ability.S_DexDetails;
import com.lineage.server.serverpackets.ability.S_ElixirCount;
import com.lineage.server.serverpackets.ability.S_IntDetails;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.serverpackets.ability.S_WisDetails;
import com.lineage.server.templates.L1ArmorSets;
import com.lineage.server.templates.L1BookMark;
import com.lineage.server.templates.L1Config;
import com.lineage.server.templates.L1GetBackRestart;
import com.lineage.server.templates.L1PcOtherList;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.templates.L1UserSkillTmp;
import com.lineage.server.templates.L1User_Power;
import com.lineage.server.timecontroller.server.ServerUseMapTimer;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldSummons;
import com.lineage.server.world.WorldWar;

import static com.lineage.server.model.skill.L1SkillId.*;
import nick.forMYSQL.ControlSoulTowerNumber;
import nick.forMYSQL.ControlSoulTowerTable;


// import com.lineage.server.serverpackets.S_Emblem;

/**
 * 要求進入遊戲
 * 
 * @author daien
 */
public class C_LoginToServer extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_LoginToServer.class);

	/*
	 * public C_LoginToServer() { } public C_LoginToServer(final byte[] abyte0,
	 * final ClientExecutor client) { super(abyte0); try { this.start(abyte0,
	 * client); } catch (final Exception e) {
	 * _log.error(e.getLocalizedMessage(), e); } }
	 */

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final String loginName = client.getAccountName();
			// System.out.println("帳號: " + login);
			// System.out.println("人物名稱: " + charName);

			if (client.getActiveChar() != null) {
				_log.error("帳號重複登入人物: " + loginName + "強制中斷連線");
				client.kick();
				return;
			}

			final String charName = readS();

			final L1PcInstance pc = L1PcInstance.load(charName);

			if ((pc == null) || !loginName.equals(pc.getAccountName())) {
				_log.info("無效登入要求: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");
				client.kick();
				return;
			}

			/*
			 * if (Config.LEVEL_DOWN_RANGE != 0) { if (pc.getHighLevel() -
			 * pc.getLevel() >= Config.LEVEL_DOWN_RANGE) { _log.info(
			 * "超出人物可創範圍: " + charName + " 帳號(" + loginName + ", " +
			 * client.getIp() + ")"); client.kick(); return; } }
			 */

			_log.info("登入遊戲: " + charName + " 帳號(" + loginName + ", " + client.getIp() + ")");

			pc.setNetConnection(client);// 登記封包接收組
			pc.setPacketOutput(client.out());// 登記封包發送組

			final int currentHpAtLoad = pc.getCurrentHp();
			final int currentMpAtLoad = pc.getCurrentMp();

			// 重置錯誤次數
			client.set_error(0);

			pc.clearSkillMastery();// 清除技能資訊

			World.get().storeObject(pc);
			
			pc.setNetConnection(client);// 登記封包接收組
			pc.setPacketOutput(client.out());// 登記封包發送組
			client.setActiveChar(pc);// 登記玩家資料

			getOther(pc);// 額外紀錄資料

			// 初始能力加成
			pc.sendPackets(new S_InitialAbilityGrowth(pc));

			// 宣告進入遊戲
			pc.sendPackets(new S_EnterGame());

			// 讀取角色道具
			items(pc);

			// 取得記憶座標資料
			bookmarks(pc);

			// 判斷座標資料
			backRestart(pc);

			// 取得遊戲焦點
			getFocus(pc);

			pc.sendVisualEffectAtLogin();

			skills(pc);// 取得角色魔法技能資料

			pc.turnOnOffLight();

			if (pc.getCurrentHp() > 0) {
				pc.setDead(false);
				pc.setStatus(0);

			} else {
				pc.setDead(true);
				pc.setStatus(ActionCodes.ACTION_Die);
			}

			pc.sendPackets(new S_PacketBox(32));

			// XXX 7.6 ADD
			pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新

			// XXX 7.6 能力基本資訊-力量
			pc.sendPackets(new S_StrDetails(2,
					L1ClassFeature.calcStrDmg(pc.getStr(), pc.getBaseStr()),
					L1ClassFeature.calcStrHit(pc.getStr(), pc.getBaseStr()),
					L1ClassFeature.calcStrDmgCritical(pc.getStr(), pc.getBaseStr()),
					L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon())
					));
									
			// XXX 7.6 重量程度資訊
			pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight100(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
									
			// XXX 7.6 能力基本資訊-智力
			pc.sendPackets(new S_IntDetails(2,
					L1ClassFeature.calcIntMagicDmg(pc.getInt(), pc.getBaseInt()),
					L1ClassFeature.calcIntMagicHit(pc.getInt(), pc.getBaseInt()),
					L1ClassFeature.calcIntMagicCritical(pc.getInt(), pc.getBaseInt()),
					L1ClassFeature.calcIntMagicBonus(pc.getType(), pc.getInt()),
					L1ClassFeature.calcIntMagicConsumeReduction(pc.getInt())
					));
									
			// XXX 7.6 能力基本資訊-精神
			pc.sendPackets(new S_WisDetails(2,
					L1ClassFeature.calcWisMpr(pc.getWis(), pc.getBaseWis()),
					L1ClassFeature.calcWisPotionMpr(pc.getWis(), pc.getBaseWis()),
					L1ClassFeature.calcStatMr(pc.getWis()) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr(),
					L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())
					));
									
			// XXX 7.6 能力基本資訊-敏捷
			pc.sendPackets(new S_DexDetails(2,
					L1ClassFeature.calcDexDmg(pc.getDex(), pc.getBaseDex()),
					L1ClassFeature.calcDexHit(pc.getDex(), pc.getBaseDex()),
					L1ClassFeature.calcDexDmgCritical(pc.getDex(), pc.getBaseDex()),
					L1ClassFeature.calcDexAc(pc.getDex()),
					L1ClassFeature.calcDexEr(pc.getDex())
					));
									
			// XXX 7.6 能力基本資訊-體質
			pc.sendPackets(new S_ConDetails(2,
					L1ClassFeature.calcConHpr(pc.getCon(), pc.getBaseCon()),
					L1ClassFeature.calcConPotionHpr(pc.getCon(), pc.getBaseCon()),
					L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon()),
					L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon())
					));
									
			// XXX 7.6 重量程度資訊
			pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
									
			// XXX 7.6 純能力詳細資訊 階段:25
			pc.sendPackets(new S_BaseAbilityDetails(25));
									
			// XXX 7.6 純能力詳細資訊 階段:35
			pc.sendPackets(new S_BaseAbilityDetails(35));
												
			// XXX 7.6 純能力詳細資訊 階段:45
			pc.sendPackets(new S_BaseAbilityDetails(45));
												
			// XXX 7.6 純能力資訊
			pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
									
			// XXX 7.6 萬能藥使用數量
			pc.sendPackets(new S_ElixirCount(pc.getElixirStats()));

			pc.sendPackets(new S_PacketBox(189));

			// 取回快速鍵紀錄
			final L1Config config = CharacterConfigReading.get().get(pc.getId());
			if (config != null) {
				pc.sendPackets(new S_PacketBoxConfig(config));
			}

			serchSummon(pc);// 取得殘留寵物資訊

			ServerWarExecutor.get().checkCastleWar(pc);// 檢查城堡戰爭狀態

			if (LeavesSet.START) {
				final int logintime = (int) (System.currentTimeMillis() / 60 / 1000);// 分鐘
				final int mm = logintime - pc.get_other().get_login_time();
				if (mm > 0) {
					if ((mm / LeavesSet.TIME) > 0) {
						final int addexp = (mm / LeavesSet.TIME) * LeavesSet.EXP;
						pc.get_other().set_teaves_time_exp(addexp);
						pc.sendPackets(
								new S_PacketBoxExp(pc.get_other().get_teaves_time_exp() / LeavesSet.EXP));
					}
				}
			}

			war(pc);// 戰爭狀態

			marriage(pc);// 取得婚姻資料

			if (currentHpAtLoad > pc.getCurrentHp()) {
				pc.setCurrentHp(currentHpAtLoad);
			}
			if (currentMpAtLoad > pc.getCurrentMp()) {
				pc.setCurrentMp(currentMpAtLoad);
			}

			buff(pc);// 取得物品與魔法特殊效果

			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startObjectAutoUpdate();// PC 可見物更新處理
			crown(pc);// 送出王冠資料

			pc.save(); // 資料回存

			if (pc.getHellTime() > 0) {
				pc.beginHell(false);
			}
			
			// 送出人物屬性資料
			//pc.sendPackets(new S_CharResetInfo(pc));
			S_CharReset statusinfo = new S_CharReset(pc, 0x04);// 初始能力加成顯示
			pc.sendPackets(statusinfo);
			
			statsReward(pc);// 點數獎勵
			
			// 設置原始資料
			pc.load_src();

			// 載入人物任務資料
			pc.getQuest().load();

			pc.sendPackets(new S_EquipmentSlot(1, 16));

			// 送出展示視窗
			pc.showWindows();

			if (pc.get_food() >= 225) { // LOLI 生存吶喊
				final Calendar cal = Calendar.getInstance();
				final long h_time = cal.getTimeInMillis() / 1000;// 換算為秒
				pc.set_h_time(h_time);// 紀錄登入時間
			}

			if (pc.getLevel() <= 20) { // LOLI 戰鬥特化
				pc.sendPackets(new S_PacketBoxProtection(S_PacketBoxProtection.ENCOUNTER, 1));
			}
			pc.lawfulUpdate();

			pc.setOnlineStatus(1);// 設定連線狀態

			CharacterTable.updateOnlineStatus(pc);

//			if (client.getIp().toString().equalsIgnoreCase("127.0.0.1")) {
//				pc.setAccessLevel((short) 20000);
//				pc.setGm(true);
//				pc.sendPackets(new S_ServerMessage("\\aD親愛的神啊 ~ 歡迎您的到來！"));
//			}
			
			if (EffectAISet.START) {
				if (EffectAISet.AI_TIME_RANDOM != 0 && pc.getAITimer() == 0) {
					Random _random = new Random();
					pc.setAITimer(_random.nextInt(EffectAISet.AI_TIME_RANDOM)
							+ EffectAISet.AI_TIME);
				}
			}
			
			// 玩家上線通知GM
			if (ConfigAlt.ALT_GM_LOGIN_MSG) {
				final Collection<L1PcInstance> allGM = World.get().getAllPlayers();
				for (final L1PcInstance object : allGM) {
					if (object instanceof L1PcInstance) {
						final L1PcInstance GM = object;
						if ((pc.getClanid() >= 0) && (GM.getAccessLevel() > 0)) {
							String msg = "";
							if (pc.isCrown()) {
								msg = "王族";
							} else if (pc.isKnight()) {
								msg = "騎士";
							} else if (pc.isElf()) {
								msg = "妖精";
							} else if (pc.isWizard()) {
								msg = "法師";
							} else if (pc.isDarkelf()) {
								msg = "黑妖";
							} else if (pc.isDragonKnight()) {
								msg = "龍騎";
							} else if (pc.isIllusionist()) {
								msg = "幻術";
							} else if (pc.isWarrior()) {
								msg = "戰士";
							}
							GM.sendPackets(new S_SystemMessage(
									("\\aG玩家：【" + pc.getName() + "】帳號：【" + client.getAccountName()
											+ "】\n \\aDIP：【" + client.getIp() + "】職業：" + msg + "，登入遊戲。")));
						}
					}
				}
			}

			if (CardSet.START) { // 月卡系統
				CardSet.load_card_mode(pc);
			}

			if (CampSet.CAMPSTART) {
				final L1User_Power value = CharacterC1Reading.get().get(pc.getId());
				if (value != null) {
					pc.set_c_power(value);
					if (value.get_c1_type() != 0) {
						pc.get_c_power().set_power(pc, true);
						// 改變顯示
						pc.sendPacketsAll(new S_ChangeName(pc, true));

						final String type = C1_Name_Table.get().get(pc.get_c_power().get_c1_type());
						pc.sendPackets(new S_ServerMessage("\\aH您目前所屬的國度(種族陣營): " + type));
					}
				}
			}
			
			/**
			 * 信件強制發送顯示 by terry0412 (暫時設定..正確應該讓客戶端自行發送C封包取得資料)
			 */
			pc.sendPackets(new S_Mail(pc, 0));
			pc.sendPackets(new S_Mail(pc, 1));
			pc.sendPackets(new S_Mail(pc, 2));

			/*if (pc.getClanid() > 0) {
				L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_INFO));
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_PLEDGE_MEMBER_INFO));
				pc.sendPackets(new S_PledgeUI(clan, S_PledgeUI.ES_ONLINE_MEMBER_INFO));
			}*/
			if (pc.getClanid() != 0) { // 具有血盟
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					if (pc.getClanid() == clan.getClanId() && pc.getClanname().toLowerCase().equals(clan.getClanName().toLowerCase())) {
						// XXX 7.6C ADD
						pc.sendPackets(new S_PledgeName(pc.getClanname(), pc.getClanRank()));
					}
				}
			}
			
			// 師徒系統 - 資料載入 by terry0412
			if (ConfigAlt.APPRENTICE_SWITCH) {
				final L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc);
				if (apprentice != null) {
					pc.setApprentice(apprentice);
					pc.checkEffect();
				}
			}

			// 給予城堡額外附加能力效果 by terry0412
			CastleWarGiftTable.get().login_gift(pc);

			// 元寶偵測紀錄 by terry0412
			if (ConfigAlt.ADENA_CHECK_SWITCH) {
				final long adenaCount = pc.getInventory().countItems(44070);
				if (adenaCount > 0) {
					pc.setShopAdenaRecord(adenaCount);
				}
				pc.setSkillEffect(ADENA_CHECK_TIMER, ConfigAlt.ADENA_CHECK_TIME_SEC * 1000);
			}
			/** GM 上線後自動隱身 */
			if (ConfigAlt.ALT_GM_HIDE) {
				if (pc.isGm() || pc.isMonitor()) {
					pc.setGmInvis(true);
					pc.sendPackets(new S_Invis(pc.getId(), 1));
					pc.broadcastPacketAll(new S_RemoveObject(pc));
					pc.sendPackets(new S_SystemMessage("\\aE啟用線上GM隱身模式。"));

				}
				/** GM 上線後自動隱身 */

				if ((pc.getClanid() == 0) || (pc.getClan() == null)) {
					if (pc.isCrown()) {
						pc.sendPackets(new S_ServerMessage(3247));

					} else {
						boolean isFound = false;
						final Map<Integer, CopyOnWriteArrayList<Integer>> check_list = ClanRecommendReading
								.get().getApplyList();
						for (final CopyOnWriteArrayList<Integer> list : check_list.values()) {
							if (list.contains(pc.getId())) {
								isFound = true;
								break;
							}
						}
						if (!isFound) {
							pc.sendPackets(new S_ServerMessage(3245));
						}
					}
				}
				/** 修正守護騎士可以收人 141031 Roy */
			} else if ((pc.getClanRank() == L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN)
					|| (pc.getClanRank() == L1Clan.CLAN_RANK_PRINCE)
					|| (pc.getClanRank() == L1Clan.ALLIANCE_CLAN_RANK_GUARDIAN)
					|| (pc.getClanRank() == L1Clan.NORMAL_CLAN_RANK_GUARDIAN)
					|| (pc.getClanRank() == L1Clan.NORMAL_CLAN_RANK_PRINCE)) {

				if (ClanRecommendReading.get().getRecommendsList().containsKey(pc.getClanid())) {
					if (ClanRecommendReading.get().getApplyList().containsKey(pc.getClanid())) {
						pc.sendPackets(new S_ServerMessage(3246));
					}

				} else {
					pc.sendPackets(new S_ServerMessage(3248));
				}
			}

			T_OnlineGiftTable.get().check(pc);// 線上獎勵

			pc.setVipStatus();// VIP

			pc.getLottery().getData(pc);
			
			deleteSoulTowerItem(pc);// 刪除屍魂副本道具
			deleteSiulTowerBuff(pc);// 刪除屍魂副本狀態

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	/**
	 * 送出王冠資料
	 * 
	 * @param pc
	 */
	private void crown(final L1PcInstance pc) {
		try {
			final Map<Integer, L1Clan> map = L1CastleLocation.mapCastle();
			for (final Integer key : map.keySet()) {
				final L1Clan clan = map.get(key);
				if (clan != null) {
					if (key.equals(2)) {
						pc.sendPackets(new S_CastleMaster(8, clan.getLeaderId()));

					} else {
						pc.sendPackets(new S_CastleMaster(key, clan.getLeaderId()));
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得焦點
	 * 
	 * @param pc
	 */
	private void getFocus(final L1PcInstance pc) {
		try {
			// 重置副本編號
			pc.set_showId(-1);

			// 將物件增加到MAP世界裡
			World.get().addVisibleObject(pc);

			// 是否開啟轉生系統 by terry0412
			if ((ConfigAlt.METE_LEVEL > 0) && (pc.getMeteLevel() > 0)) {
				pc.resetMeteAbility();
			}

			// 角色資訊
			pc.sendPackets(new S_OwnCharStatus(pc));

			// 更新角色所在的地圖
			pc.sendPackets(new S_MapID(pc, pc.getMapId(), pc.getMap().isUnderwater()));

			// 物件封包(本身)
			pc.sendPackets(new S_OwnCharPack(pc));

			final ArrayList<L1PcInstance> otherPc = World.get().getVisiblePlayer(pc);
			if (otherPc.size() > 0) {
				for (final L1PcInstance tg : otherPc) {
					// 物件封包(其他人物)
					tg.sendPackets(new S_OtherCharPacks(pc));
				}
			}

			// 更新魔攻與魔防
			pc.sendPackets(new S_SPMR(pc));

			// 閃避率更新 修正 thatmystyle (UID: 3602)
			pc.sendPackets(new S_PacketBoxIcon1(true, pc.get_dodge()));

			// 友好度
			pc.sendPackets(new S_Karma(pc));

			// 天氣效果
			pc.sendPackets(new S_Weather(World.get().getWeather()));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得婚姻資料
	 * 
	 * @param pc
	 */
	private void marriage(final L1PcInstance pc) {
		try {
			if (pc.getPartnerId() != 0) { // 結婚中
				final L1PcInstance partner = (L1PcInstance) World.get().findObject(pc.getPartnerId());
				if ((partner != null) && (partner.getPartnerId() != 0)) {
					if ((pc.getPartnerId() == partner.getId()) && (partner.getPartnerId() == pc.getId())) {
						// 548 你的情人目前正在線上。
						pc.sendPackets(new S_ServerMessage(548));
						// 549 你的情人上線了!!
						partner.sendPackets(new S_ServerMessage(549));
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 其它數據
	 * 
	 * @param pc
	 * @throws Exception
	 */
	private void getOther(final L1PcInstance pc) throws Exception {
		try {
			pc.set_otherList(new L1PcOtherList(pc));

			pc.addMaxHp(pc.get_other().get_addhp());
			pc.addMaxMp(pc.get_other().get_addmp());

			// 在線獎勵
			OnlineGiftSet.add(pc);

			final int time = pc.get_other().get_usemapTime();
			if ((time > 0) && (pc.getMapId() == pc.get_other().get_usemap())) {
				ServerUseMapTimer.put(pc, time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 升級點數獎勵
	 * 
	 * @param pc
	 */
	private void statsReward(final L1PcInstance pc) { // 點數獎勵
		if ((pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats())
				|| (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc
						.getBonusStats() - 49)) {
			if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
					+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < (ConfigAlt.POWER * 6)) { // 設定能力值上限
				//pc.sendPackets(new S_bonusstats(pc.getId(), 1));
				int bonus = (pc.getLevel() - 50) - pc.getBonusStats();// 可以點的點數 XXX 7.6C ADD
				pc.sendPackets(new S_Message_YN(479, bonus));
			}
		}
	}
	
	/**
	 * 取得血盟 與 血盟戰爭資料
	 * 
	 * @param pc
	 */
	private void war(final L1PcInstance pc) {
		try {
			if (pc.getClanid() != 0) { // 血盟資料不為0
				final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
				if (clan != null) {
					// 判斷血盟名稱相等 雨 血盟編號相等
					if ((pc.getClanid() == clan.getClanId())
							&& pc.getClanname().equalsIgnoreCase(clan.getClanName())) {
						final L1PcInstance[] clanMembers = clan.getOnlineClanMember();
						for (final L1PcInstance clanMember : clanMembers) {
							if (clanMember.getId() != pc.getId()) {
								// 843 血盟成員%0%s剛進入遊戲。
								//clanMember.sendPackets(new S_ServerMessage(843, pc.getName()));
							}
						}
						pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_EMBLEM_STATUS, 0));

						final int clanMan = clan.getOnlineClanMember().length;
						pc.sendPackets(new S_ServerMessage("\\fU線上血盟成員:" + clanMan));

						if (clan.isClanskill()) {
							switch (pc.get_other().get_clanskill()) {
							case 1:// 狂暴
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[0]));
								break;
							case 2:// 寂靜
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[1]));
								break;
							case 4:// 魔擊
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[2]));
								break;
							case 8:// 消魔
								pc.sendPackets(new S_ServerMessage(Npc_clan.SKILLINFO[3]));
								break;
							}
						}

						// 目前全部戰爭資訊取得
						for (final L1War war : WorldWar.get().getWarList()) {
							final boolean ret = war.checkClanInWar(pc.getClanname());
							if (ret) { // 是否正在戰爭中
								final String enemy_clan_name = war.getEnemyClanName(pc.getClanname());
								if (enemy_clan_name != null) {
									// \f1目前你的血盟與 %0 血盟交戰當中。
									pc.sendPackets(new S_War(8, pc.getClanname(), enemy_clan_name));
								}
								break;
							}
						}
					}

				} else {
					pc.setClanid(0);
					pc.setClanname("");
					pc.setClanRank(0);
					pc.save();
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 所在座標位置資料判斷
	 * 
	 * @param pc
	 */
	private void backRestart(final L1PcInstance pc) {
		try {

			// 指定MAP回村設置
			final L1GetBackRestart gbr = GetBackRestartTable.get().getGetBackRestart(pc.getMapId());
			if (gbr != null) {
				pc.setX(gbr.getLocX());
				pc.setY(gbr.getLocY());
				pc.setMap(gbr.getMapId());
			}

			// 戰爭區域回村設置
			final int castle_id = L1CastleLocation.getCastleIdByArea(pc);
			if (castle_id > 0) {
				if (ServerWarExecutor.get().isNowWar(castle_id)) {
					final L1Clan clan = WorldClan.get().getClan(pc.getClanname());
					if (clan != null) {
						if (clan.getCastleId() != castle_id) {
							// 城主クランではない
							int[] loc = new int[3];
							loc = L1CastleLocation.getGetBackLoc(castle_id);
							pc.setX(loc[0]);
							pc.setY(loc[1]);
							pc.setMap((short) loc[2]);
						}

					} else {
						// クランに所属して居ない場合は帰還
						int[] loc = new int[3];
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						pc.setX(loc[0]);
						pc.setY(loc[1]);
						pc.setMap((short) loc[2]);
					}
				}
			}
			pc.setOleLocX(pc.getX());
			pc.setOleLocY(pc.getY());
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得物品資料
	 * 
	 * @param pc
	 */
	private void items(final L1PcInstance pc) {
		try {
			// 背包物品封包傳遞
			CharacterTable.restoreInventory(pc);
			final List<L1ItemInstance> items = pc.getInventory().getItems();
			if (items.size() > 0) {
				pc.sendPackets(new S_InvList(items));

				for (final L1ItemInstance item : items) {
					if (item.getItemId() == ProtectorSet.ITEM_ID) {//守護者靈魂系統相關
						pc.giveProtector(true);
					}
					if (item.getItem().getType2() == 0) {
						continue;
					}
					// pandora
					final int pandora_type = item.get_pandora_type();
					if (pandora_type > 0) {
						item.set_pandora_type(pc, pandora_type);
					}
					// creater
					if (item.get_creater_name() != null) {
						pc.sendPackets(new S_CreateName(item, pc));
					}
					// 照明道具
					if ((item.getItem().getType2() == 0) && (item.getItem().getType() == 2)) {
						item.setRemainingTime(item.getItem().getLightFuel());
					}
				}
				pc.getInventory().equippedLoad();
				pc.getInventory().viewItem();
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得記憶座標資料
	 * 
	 * @param pc
	 */
	private void bookmarks(final L1PcInstance pc) {
		try {
			final ArrayList<L1BookMark> bookList = CharBookReading.get().getBookMarks(pc);

			if (bookList != null) {
				if (bookList.size() > 0) {
					pc.sendPackets(new S_BookmarkList(bookList));
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得人物技能清單
	 * 
	 * @param pc
	 */
	private void skills(final L1PcInstance pc) {
		try {
			final ArrayList<L1UserSkillTmp> skillList = CharSkillReading.get().skills(pc.getId());

			final int[] skills = new int[30];

			if (skillList != null) {
				if (skillList.size() > 0) {
					for (final L1UserSkillTmp userSkillTmp : skillList) {
						// 取得魔法資料
						final L1Skills skill = SkillsTable.get().getTemplate(userSkillTmp.get_skill_id());
						skills[(skill.getSkillLevel() - 1)] += skill.getId();

						if ((skill.getSkillId() >= 233) && (skill.getSkillId() <= 239)) {
							pc.sendPackets(new S_WarriorSkill(S_WarriorSkill.LOGIN, skill.getSkillNumber()));
						}
					}
					// 送出資料
					pc.sendPackets(new S_AddSkill(pc, skills));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得殘留寵物資訊
	 * 
	 * @param pc
	 */
	private void serchSummon(final L1PcInstance pc) {
		try {
			final Collection<L1SummonInstance> summons = WorldSummons.get().all();
			if (summons.size() > 0) {
				for (final L1SummonInstance summon : summons) {
					if (summon.getMaster().getId() == pc.getId()) {
						summon.setMaster(pc);
						pc.addPet(summon);
						final S_NewMaster packet = new S_NewMaster(pc.getName(), summon);
						for (final L1PcInstance visiblePc : World.get().getVisiblePlayer(summon)) {
							visiblePc.sendPackets(packet);
						}
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 取得保留技能紀錄
	 * 
	 * @param pc
	 */
	private void buff(final L1PcInstance pc) {
		try {
			// 保留技能紀錄
			CharBuffReading.get().buff(pc);
			pc.sendPackets(new S_PacketBoxActiveSpells(pc));

			CharMapsTimeReading.get().getTime(pc);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 * 刪除屍魂副本道具
	 *
	 * @param pc
	 */
	private static void deleteSoulTowerItem(final L1PcInstance _pc) {
		 final String[] aaa = ControlSoulTowerNumber.soulTowerDeleteItem.split(",");
//		 Integer.valueOf(aaa[0])
		 
		 for (int i = 0; i <= aaa.length; i++) {
				final L1ItemInstance[] itemlist = _pc.getInventory().findItemsId(Integer.valueOf(aaa[i]));
				if (itemlist != null && itemlist.length > 0) {
					for (final L1ItemInstance item : itemlist) {
						_pc.getInventory().removeItem(item);
					}
				}
			}
		
		// 刪除副本道具
		// 240977 下層雷擊爆彈 wand.Firestorml_Magic_Wand
		// 240978 下層旋風爆彈 wand.Purificationl_Magic_Wand
		// 240979 下層戰鬥強化卷軸 Battl_Reel
		// 240980 下層防禦強化卷軸 Battt_Reel
		// 240981 下層治癒藥水 hp.UserAddHp 10 20 189
		// 240982 下層強力治癒藥水 hp.UserAddHp 60 80 197
		// 240983 下層魔力藥水 mp.UserAddMp 10 25 190
//		for (int i = 240977; i <= 240983; i++) {
//			final L1ItemInstance[] itemlist = _pc.getInventory().findItemsId(i);
//			if (itemlist != null && itemlist.length > 0) {
//				for (final L1ItemInstance item : itemlist) {
//					_pc.getInventory().removeItem(item);
//				}
//			}
//		}

//		final L1ItemInstance item = _pc.getInventory().findItemId(240967);// 屍魂幣
//		if (item != null) {
//			_pc.getInventory().removeItem(item);
//		}
	}
	
	/**
	 * 刪除屍魂副本狀態
	 *
	 * @param pc
	 */
	private static void deleteSiulTowerBuff(final L1PcInstance pc){
		if (pc.getMapId() <= 4001 && pc.getMapId() >= 4050) {
			pc.removeSkillEffect(LOWER_FLOOR_GREATER_BATTLE_SCROLL);
			pc.removeSkillEffect(LOWER_FLOOR_GREATER_DEFENSE_SCROLL);
		}
	}
}
