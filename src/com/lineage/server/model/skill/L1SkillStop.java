package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.sql.Timestamp;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRecord;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.EffectAISet;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.FishingTable;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.ServerAIEffectTable;
import com.lineage.server.datatables.ServerEtcItemTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.T_OnlineGiftTable;
import com.lineage.server.datatables.lock.IpReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1PolyMorph;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.skillmode.SkillMode;
import com.lineage.server.serverpackets.S_BlueMessage;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_Chat;
import com.lineage.server.serverpackets.S_Dexup;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_FishTime;
import com.lineage.server.serverpackets.S_HPUpdate;
import com.lineage.server.serverpackets.S_Liquor;
import com.lineage.server.serverpackets.S_MPUpdate;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_PacketBoxIconAura;
import com.lineage.server.serverpackets.S_PacketBoxWaterLife;
import com.lineage.server.serverpackets.S_PacketBoxWisdomPotion;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_Poison;
import com.lineage.server.serverpackets.S_SPMR;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillIconBlessOfEva;
import com.lineage.server.serverpackets.S_SkillIconShield;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_Strup;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1Fishing;
import com.lineage.server.templates.L1Item;
import com.lineage.server.templates.L1ServerEtcItem;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.world.World;
import com.lineage.william.EtcItemForChar;

/**
 * 技能停止
 * 
 * @author dexc
 */
public class L1SkillStop {

	private static final Log _log = LogFactory.getLog(L1SkillStop.class);

	private static final Random _random = new Random();

	public static void broadcastPacketWorld(final ServerBasePacket packet) {
		try {
			for (L1PcInstance pc : World.get().getAllPlayers()) {
				if (pc != null)
					pc.sendPackets(packet);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public static void stopSkill(final L1Character cha, final int skillId) {
		try {
			// System.out.println("技能停止:"+skillId);
			// TODO SKILL移轉
			final SkillMode mode = L1SkillMode.get().getSkill(skillId);
			if (mode != null) {
				mode.stop(cha);
			}
			// else if (ServerEtcItemTable.get().getItem(skillId - 10000000) != null) {
			// if ((cha instanceof L1PcInstance)) {
			// final L1ServerEtcItem etcitem = ServerEtcItemTable.get()
			// .getItem(skillId - 10000000);
			// EtcItemForChar.get((L1PcInstance) cha, etcitem)
			// .cancelEffect();
			// }
			// }
			else {
				switch (skillId) {
				case 7020:
					if ((cha instanceof L1PcInstance)) {
						L1PcInstance pc = (L1PcInstance) cha;
						Random _random = new Random();
						int rndxx = pc.getX() + _random.nextInt(20) - 10;
						int rndyy = pc.getY() + _random.nextInt(20) - 10;

						while (CheckUtil.checkPassable(pc, rndxx, rndyy, pc.getMapId()) || !pc.getMap().isInMap(rndxx, rndyy) || pc.getMap().getOriginalTile(rndxx, rndyy) == 0
								|| pc.getX() == rndxx && pc.getY() == rndyy || !pc.glanceCheck(rndxx, rndyy) || !pc.getMap().isPassable(rndxx, rndyy, 0, null)
								|| !pc.getMap().isPassable(rndxx, rndyy, 1, null) || !pc.getMap().isPassable(rndxx, rndyy, 2, null) || !pc.getMap().isPassable(rndxx, rndyy, 3, null)
								|| !pc.getMap().isPassable(rndxx, rndyy, 4, null) || !pc.getMap().isPassable(rndxx, rndyy, 5, null) || !pc.getMap().isPassable(rndxx, rndyy, 6, null)
								|| !pc.getMap().isPassable(rndxx, rndyy, 7, null)) {
							rndxx = pc.getX() + _random.nextInt(20) - 10;
							rndyy = pc.getY() + _random.nextInt(20) - 10;
						}
						int[] xyz = { rndxx, rndyy };
						pc.set_aixyz(xyz);
						// }
						pc.sendPackets(new S_PacketBoxGree(0x01));
						pc.sendPackets(new S_EffectLocation(pc.get_aixyz()[0], pc.get_aixyz()[1], ServerAIEffectTable.getEffectId()));
						String msg = "請 " + (EffectAISet.AI_ANSWER_TIME / 1000) + " 秒內移動至指定位置完成驗証。";
						pc.sendPackets(new S_BlueMessage(166, "\\f3" + msg));
						pc.sendPackets(new S_ServerMessage("\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg + "\n\r" + msg));
						pc.setSkillEffect(7021, EffectAISet.AI_ANSWER_TIME);
					}
					break;
				case 7021:
					if ((cha instanceof L1PcInstance)) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAIERROR();
						int error = pc.getAIERROR();
						if (error >= EffectAISet.AI_ERROR_COUNT) {
							int type = EffectAISet.AI_ERROR_TYPE;
							if (type == 3) {
								WriteLogTxt.Recording("未通過驗證紀錄", pc.getName() + "(" + pc.getId() + ") " + "未通過驗證 已封鎖IP");
								broadcastPacketWorld(new S_BlueMessage(166, "\\f=玩家 " + pc.getName() + " 經過系統驗證未通過 所以被GM大大羈押了！"));
								IpReading.get().add(pc.getAccountName().toString(), "驗證失敗" + ":封鎖帳號");
								pc.getNetConnection().kick();
							}
							if (type == 2) {
								WriteLogTxt.Recording("未通過驗證紀錄", pc.getName() + "(" + pc.getId() + ") " + "未通過驗證 已封鎖IP");
								broadcastPacketWorld(new S_BlueMessage(166, "\\f=玩家 " + pc.getName() + " 經過系統驗證未通過 所以被GM大大羈押了！"));
								IpReading.get().add(pc.getNetConnection().getIp().toString(), "驗證失敗" + ":封鎖IP");
								pc.getNetConnection().kick();
							} else if (type == 1) {
								pc.setAIERROR();
								L1Teleport.teleport(pc, EffectAISet.AI_LOCX, EffectAISet.AI_LOCY, EffectAISet.AI_MAPID, pc.getHeading(), false);
							}
						} else {
							pc.setSkillEffect(7020, 1000);
						}
					}
					break;
				// 淨化藥水 by terry0412
				case POTION_OF_PURIFICATION:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 清空允許攻擊BOSS列表
						pc.set_allow_list(null);
						// 訊息提示
						pc.sendPackets(new S_SystemMessage("Boss淨化藥水效果已結束。"));
					}
					break;

				case AICHECK:
					if (Config.AICHECK) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							IpReading.get().add(pc.getAccountName(), "AI系統自動封鎖");
							pc.getNetConnection().kick();
						}

					} else {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.setSkillEffect(L1SkillId.AICHECK, 180 * 1000);
							/*
							 * IpReading.get() .add(pc.getAccountName(), "AI系統自動封鎖");
							 */
							pc.getNetConnection().kick();
						}
					}
					break;

				case MAGIC_ITEM_POWER_A:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SystemMessage("\\aH★物理與魔法傷害無效化★效果已結束。"));
					}
					break;

				case MAGIC_ITEM_POWER_B:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SystemMessage("\\aH★使用魔法治癒術或生命的祝福(補血量X2倍)★效果已結束。"));
					}
					break;

				case MAGIC_ITEM_POWER_C:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SystemMessage("\\aH★受到近距離傷害將以兩倍反擊傷害回去★效果已結束。"));
					}
					break;

				case ADENA_CHECK_TIMER:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						final long adenaCount = pc.getInventory().countItems(44070);
						if (adenaCount > 0) {
							final long difference = adenaCount - pc.getShopAdenaRecord();
							if (difference >= ConfigAlt.ADENA_CHECK_COUNT_DIFFER) {
								final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								ConfigRecord.recordToFiles("元寶差異紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的在線元寶數量增加:【" + difference + "】個, 時間:(" + timestamp
										+ ")", timestamp);

							} else if (difference <= -ConfigAlt.ADENA_CHECK_COUNT_DIFFER) {
								final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
								ConfigRecord.recordToFiles("元寶差異紀錄", "IP(" + pc.getNetConnection().getIp() + ")玩家:【" + pc.getName() + "】的在線元寶數量減少:【" + Math.abs(difference) + "】個, 時間:("
										+ timestamp + ")", timestamp);
							}
							pc.setShopAdenaRecord(adenaCount);
						}

						pc.setSkillEffect(ADENA_CHECK_TIMER, ConfigAlt.ADENA_CHECK_TIME_SEC * 1000);
					}
					break;

				// XX色霸氣 (清除變數) by terry0412
				case DOMINATE_POWER_A:
				case DOMINATE_POWER_B:
				case DOMINATE_POWER_C:
				case DOMINATE_POWER_D:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.setValue(0);
						pc.setEffectId(0);
						pc.sendPackets(new S_SystemMessage("\\aG★加持效果已結束★"));
					}
					break;

				case HAPPY_TIME: // 快樂時光 (150130 By erics4179 加入)
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						final int i = _random.nextInt(6); // 0~5
						String msg = "";
						switch (i) {
						case 0:
							// 5秒,變成豬(941)
							L1PolyMorph.doPoly(pc, 941, 5, L1PolyMorph.MORPH_BY_GM);
							// 奇岩商村
							L1Teleport.teleport(pc, 34041, 33007, (short) 4, 5, true);// 風龍點
							msg = "GM大人我錯了，我對不起大家!";
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aD再給我亂講話就切斷你小GG。"));
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						case 1:
							// 5秒,變成鴨子(1014)
							L1PolyMorph.doPoly(pc, 1014, 5, L1PolyMorph.MORPH_BY_GM);
							L1Teleport.teleport(pc, 32696, 32824, (short) 37, 5, true);// 地龍點
							msg = "GM大人我錯了，我對不起大家!";
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aG再給我亂講話就切斷你小GG。"));
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						case 2:
							// 5秒,變成牛(945)
							L1PolyMorph.doPoly(pc, 945, 5, L1PolyMorph.MORPH_BY_GM);
							L1Teleport.teleport(pc, 32867, 32863, (short) 535, 5, true);// 小吉點
							msg = "GM大人我錯了，我對不起大家!";
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aD再給我亂講話就切斷你小GG。"));
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						case 3:
							// 5秒,變成雞(943)
							L1PolyMorph.doPoly(pc, 943, 5, L1PolyMorph.MORPH_BY_GM);
							L1Teleport.teleport(pc, 32773, 32832, (short) 65, 5, true);// 水龍點
							msg = "GM大人我錯了，我對不起大家!";
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aG再給我亂講話就切斷你小GG。"));
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						case 4:
							// 5秒,變成狗(938)
							L1PolyMorph.doPoly(pc, 938, 5, L1PolyMorph.MORPH_BY_GM);
							L1Teleport.teleport(pc, 32751, 32863, (short) 784, 5, true); // 雙蛇點
							msg = "GM大人我錯了，我對不起大家!";
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aD再給我亂講話就切斷你小GG。"));
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						case 5:
							L1Teleport.teleport(pc, 32724, 32800, (short) 67, 5, true);// 火龍點
							pc.sendPackets(new S_PacketBoxGree(4));// 送海戰畫面變黑閃電的封包
							pc.sendPackets(new S_SystemMessage("\\aG再給我亂講話就切斷你小GG。"));
							msg = "GM大人我錯了，我對不起大家!";
							// pc.sendPackets(new S_Disconnect());// 強制斷線
							break;
						/*
						 * case 6: 不使用此功能 L1Teleport.teleport(pc, 32724, 32800, (short) 67, 5, true); pc.sendPackets(new S_PacketBoxGree(4));//送海戰畫面變黑閃電的封包 pc.sendPackets(new
						 * S_Paralysis(S_Paralysis.TYPE_PARALYSIS, true)); pc.sendPackets(new S_SystemMessage("\\aG再給我亂講話就切斷你小GG。")); msg = "GM大人我錯了，請原諒我的不是!"; break;
						 */
						default: // 可不加
							break;
						}
						// 一般頻道的喊話
						final S_Chat packet = new S_Chat(pc, msg, 0);
						pc.sendPacketsAll(packet);
						pc.sendPackets(new S_BlueMessage(166, "\\fZ恭喜你\\f2<快樂時間開始>"));
						pc.setSkillEffect(HAPPY_TIME, 3000); // 快樂時光
					}
					break;

				/*
				 * case IMMUNE_TO_HARM: case BOUNCE_ATTACK: case SHIELD:
				 */
				case LIGHT: // ライト
					if (cha instanceof L1PcInstance) {
						if (!cha.isInvisble()) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.turnOnOffLight();
						}
					}
					break;

				case GLOWING_WEAPON: // グローウィング オーラ
					cha.addHitup(-5);
					cha.addBowHitup(-5);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(113, 0));
					}
					break;

				case SHINING_AURA: // 鋼鐵士氣
					cha.addAc(8);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(114, 0));
					}
					break;

				case BRAVE_MENTAL: // 衝擊士氣
					cha.addDmgup(-5);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(116, 0));
					}
					break;

				case SHIELD: // シールド
					cha.addAc(2);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconShield(2, 0));
					}
					break;

				case BLIND_HIDING: // ブラインドハイディング
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.delBlindHiding();
					}
					break;

				case SHADOW_ARMOR: // シャドウ アーマー
					// 魔法防禦值-5 by terry0412
					cha.addMr(-5);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_SkillIconShield(3, 0));
					}
					break;

				case DRESS_DEXTERITY: // ドレス デクスタリティー
					// 改成 敏捷-3 by terry0412
					cha.addDex((byte) -3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Dexup(pc, 3, 0));
					}
					break;

				case DRESS_MIGHTY: // ドレス マイティー
					// 改成 力量-3 by terry0412
					cha.addStr((byte) -3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Strup(pc, 3, 0));
					}
					break;

				case SHADOW_FANG: // シャドウ ファング
					cha.addDmgup(-5);
					break;

				case ENCHANT_WEAPON: // エンチャント ウェポン
					cha.addDmgup(-2);
					break;

				case BLESSED_ARMOR: // ブレスド アーマー
					cha.addAc(3);
					break;

				case EARTH_GUARDIAN: // アース ブレス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconShield(7, 0));
						pc.addDamageReductionByArmor(-2);
					}
					break;

				case RESIST_MAGIC: // レジスト マジック
					cha.addMr(-10);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SPMR(pc));
					}
					break;

				case CLEAR_MIND: // クリアー マインド
					cha.addWis((byte) -3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.resetBaseMr();
					}
					break;

				case RESIST_ELEMENTAL: // レジスト エレメント
					cha.addWind(-10);
					cha.addWater(-10);
					cha.addFire(-10);
					cha.addEarth(-10);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
					break;

				case ELEMENTAL_PROTECTION: // エレメンタルプロテクション
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						final int attr = pc.getElfAttr();
						if (attr == 1) {
							cha.addEarth(-50);
						} else if (attr == 2) {
							cha.addFire(-50);
						} else if (attr == 4) {
							cha.addWater(-50);
						} else if (attr == 8) {
							cha.addWind(-50);
						}
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
					break;

				case WATER_LIFE: // 水之元氣
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxWaterLife());
					}
					break;

				/*
				 * case ELEMENTAL_FALL_DOWN: // エレメンタルフォールダウン if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; final int attr = pc.getAddAttrKind(); final int
				 * i = 50; switch (attr) { case 1: pc.addEarth(i); break; case 2: pc.addFire(i); break; case 4: pc.addWater(i); break; case 8: pc.addWind(i); break; default: break; }
				 * pc.setAddAttrKind(0); pc.sendPackets(new S_OwnCharAttrDef(pc)); } else if (cha instanceof L1NpcInstance) { final L1NpcInstance npc = (L1NpcInstance) cha; final int attr
				 * = npc.getAddAttrKind(); final int i = 50; switch (attr) { case 1: npc.addEarth(i); break; case 2: npc.addFire(i); break; case 4: npc.addWater(i); break; case 8:
				 * npc.addWind(i); break; default: break; } npc.setAddAttrKind(0); } break;
				 */

				case IRON_SKIN: // アイアン スキン
					cha.addAc(10);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconShield(10, 0));
					}
					break;

				case EARTH_SKIN: // アース スキン
					cha.addAc(6);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconShield(6, 0));
					}
					break;

				case PHYSICAL_ENCHANT_STR: // フィジカル エンチャント：STR
					cha.addStr((byte) -5);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Strup(pc, 1, 0));
					}
					break;

				case PHYSICAL_ENCHANT_DEX: // フィジカル エンチャント：DEX
					cha.addDex((byte) -5);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Dexup(pc, 1, 0));
					}
					break;

				case FIRE_WEAPON: // ファイアー ウェポン
					cha.addDmgup(-4);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(147, 0));
					}
					break;

				case DANCING_BLAZE:
					cha.setBraveSpeed(0);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
					}
					break;

				case BURNING_WEAPON: // バーニング ウェポン
					cha.addDmgup(-6);
					cha.addHitup(-3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(162, 0));
					}
					break;

				case BLESS_WEAPON: // ブレス ウェポン
					cha.addDmgup(-2);
					cha.addHitup(-2);
					cha.addBowHitup(-2);
					break;

				case WIND_SHOT: // ウィンド ショット
					cha.addBowHitup(-6);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(148, 0));
					}
					break;

				case STORM_EYE: // ストーム アイ
					cha.addBowHitup(-2);
					cha.addBowDmgup(-3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(155, 0));
					}
					break;

				case STORM_SHOT: // ストーム ショット
					cha.addBowDmgup(-6);
					cha.addBowHitup(-3);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxIconAura(165, 0));
					}
					break;

				case BERSERKERS: // バーサーカー
					cha.addAc(-10);
					cha.addDmgup(-5);
					cha.addHitup(-2);
					break;

				case SHAPE_CHANGE: // シェイプ チェンジ
					L1PolyMorph.undoPoly(cha);
					break;

				/*
				 * case ADVANCE_SPIRIT: // アドバンスド スピリッツ if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.addMaxHp(-pc.getAdvenHp());
				 * pc.addMaxMp(-pc.getAdvenMp()); pc.setAdvenHp(0); pc.setAdvenMp(0); pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp())); if (pc.isInParty()) { // パーティー中
				 * pc.getParty().updateMiniHP(pc); } pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp())); } break;
				 */

				// case HASTE:
				case GREATER_HASTE: // ヘイスト、グレーターヘイスト
					cha.setMoveSpeed(0);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
					}
					break;

				case HOLY_WALK:
				case MOVING_ACCELERATION:
				case WIND_WALK:
					// case BLOODLUST: //
					// ホーリーウォーク、ムービングアクセレーション、ウィンドウォーク、ブラッドラスト
					cha.setBraveSpeed(0);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
					}
					break;

				case ILLUSION_OGRE: // イリュージョン：オーガ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-4);
						pc.addHitup(-4);
					}
					break;

				/*
				 * case ILLUSION_LICH: // イリュージョン：リッチ if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.addSp(-2); pc.sendPackets(new S_SPMR(pc)); } break;
				 */

				case ILLUSION_DIA_GOLEM: // イリュージョン：ダイアモンドゴーレム
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(8);
					}
					break;

				/*
				 * case ILLUSION_AVATAR: // イリュージョン：アバター if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.addDmgup(-10); } break;
				 */

				/*
				 * case INSIGHT: // インサイト if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.addStr((byte) -1); pc.addCon((byte) -1); pc.addDex((byte) -1);
				 * pc.addWis((byte) -1); pc.addInt((byte) -1); } break;
				 */

				/*
				 * case CURSE_BLIND: case DARKNESS: if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new S_CurseBlind(0)); } break;
				 */

				/*
				 * case CURSE_PARALYZE: // カーズ パラライズ if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPacketsAll(new S_Poison(pc.getId(), 0));
				 * pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false)); } break;
				 */

				case WEAKNESS: // ウィークネス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.addHitup(1);
					}
					break;

				case DISEASE: // ディジーズ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addAc(-12);
					}
					break;

				case ICE_LANCE: // アイスランス
				case FREEZING_BREATH: // フリージングブレス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_Poison(pc.getId(), 0));

						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
					} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						npc.broadcastPacketAll(new S_Poison(npc.getId(), 0));
						npc.setParalyzed(false);
					}
					break;

				case EARTH_BIND: // アースバインド
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_Poison(pc.getId(), 0));

						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
					} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
						final L1NpcInstance npc = (L1NpcInstance) cha;
						npc.broadcastPacketAll(new S_Poison(npc.getId(), 0));
						npc.setParalyzed(false);
					}
					break;

				/*
				 * case SHOCK_STUN: // ショック スタン if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN,
				 * false)); } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) { final L1NpcInstance npc =
				 * (L1NpcInstance) cha; npc.setParalyzed(false); } break;
				 */

				case FOG_OF_SLEEPING: // フォグ オブ スリーピング
					cha.setSleeped(false);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
					break;

				case ABSOLUTE_BARRIER: // アブソルート バリア
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.startHpRegeneration();
						pc.startMpRegeneration();
					}
					break;

				/*
				 * case WIND_SHACKLE: // ウィンド シャックル if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(),
				 * 0)); } break;
				 */

				case SLOW:
				case ENTANGLE:
				case MASS_SLOW: // 緩速術/集體緩速術/地面障礙
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
					}
					cha.setMoveSpeed(0);
					break;

				/*
				 * case STATUS_FREEZE: // Freeze if (cha instanceof L1PcInstance) { final L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND,
				 * false)); } else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) { final L1NpcInstance npc =
				 * (L1NpcInstance) cha; npc.setParalyzed(false); } break;
				 */

				case GUARD_BRAKE: // ガードブレイク
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-15);
					}
					break;

				case HORROR_OF_DEATH: // ホラーオブデス
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr(5);
						pc.addInt(5);
					}
					break;

				case STATUS_CUBE_IGNITION_TO_ALLY: // キューブ[イグニション]：味方
					cha.addFire(-30);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
					break;

				case STATUS_CUBE_QUAKE_TO_ALLY: // キューブ[クエイク]：味方
					cha.addEarth(-30);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
					break;

				case STATUS_CUBE_SHOCK_TO_ALLY: // キューブ[ショック]：味方
					cha.addWind(-30);
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
					break;

				case STATUS_CUBE_IGNITION_TO_ENEMY: // キューブ[イグニション]：敵
				case STATUS_CUBE_QUAKE_TO_ENEMY: // キューブ[クエイク]：敵
				case STATUS_CUBE_SHOCK_TO_ENEMY: // キューブ[ショック]：敵
				case STATUS_MR_REDUCTION_BY_CUBE_SHOCK: // キューブ[ショック]によるMR減少
				case STATUS_CUBE_BALANCE: // キューブ[バランス]
					break;

				case STATUS_BRAVE: // 勇敢藥水效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
					}
					cha.setBraveSpeed(0);
					break;

				case STATUS_BRAVE3: // 巧克力蛋糕
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_Liquor(pc.getId(), 0x00));
					}
					break;

				case EXP13: // 第一段經驗加倍效果
				case EXP15: // 第一段經驗加倍效果
				case EXP17: // 第一段經驗加倍效果
				case EXP20: // 第一段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 2402 經驗直加倍效果消失！
						pc.sendPackets(new S_ServerMessage("經驗加倍效果消失！"));
						pc.sendPackets(new S_PacketBoxCooking(pc, 32, 0));
					}
					break;

				case SEXP13: // 第二段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 3077 第二段經驗1.3倍效果結束。
						pc.sendPackets(new S_ServerMessage("第二段經驗1.3倍效果結束。"));
					}
					break;

				case SEXP15: // 第二段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 3079 第二段經驗1.5倍效果結束。
						pc.sendPackets(new S_ServerMessage("第二段經驗1.5倍效果結束。"));
					}
					break;

				case SEXP17: // 第二段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 3081 第二段經驗1.7倍效果結束。
						pc.sendPackets(new S_ServerMessage("第二段經驗1.7倍效果結束"));
					}
					break;

				case SEXP20: // 第二段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 3075 第二段經驗雙倍效果結束。
						pc.sendPackets(new S_ServerMessage("第二段經驗雙倍效果結束。"));
					}
					break;

				case REEXP20: // 第三段經驗加倍效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						// 3073 第三段經驗雙倍效果結束。
						pc.sendPackets(new S_ServerMessage("第三段經驗雙倍效果結束。"));
					}
					break;

				case STATUS_ELFBRAVE: // 精靈餅乾效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
					}
					cha.setBraveSpeed(0);
					break;

				case STATUS_RIBRAVE: // 生命之樹果實效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.setBraveSpeed(0);
						pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
						// XXX ユグドラの実のアイコンを消す方法が不明

					} else {
						cha.setBraveSpeed(0);
					}
					break;

				case STATUS_HASTE: // 加速藥水效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPacketsAll(new S_SkillHaste(pc.getId(), 0, 0));
					}
					cha.setMoveSpeed(0);
					break;

				case STATUS_BLUE_POTION: // 魔力回復藥水效果
					break;

				case STATUS_UNDERWATER_BREATH: // 伊娃的祝福藥水效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
					}
					break;

				case STATUS_WISDOM_POTION: // 慎重藥水效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						cha.addSp(-2);
						pc.sendPackets(new S_PacketBoxWisdomPotion(0));
					}
					break;

				case STATUS_CHAT_PROHIBITED: // 禁言效果
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_ServerMessage(288)); // チャットができるようになりました。
					}
					break;

				case STATUS_POISON: // 毒素效果
				case STATUS_POISON_SILENCE:// 沉默型中毒
					cha.curePoison();
					break;

				case COOKING_1_0_N:
				case COOKING_1_0_S: // フローティングアイステーキ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addWind(-10);
						pc.addWater(-10);
						pc.addFire(-10);
						pc.addEarth(-10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 0, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_1_N:
				case COOKING_1_1_S: // ベアーステーキ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(-30);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) { // パーティー中
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_PacketBoxCooking(pc, 1, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_2_N:
				case COOKING_1_2_S: // ナッツ餅
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 2, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_3_N:
				case COOKING_1_3_S: // 蟻脚のチーズ焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(1);
						pc.sendPackets(new S_PacketBoxCooking(pc, 3, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_4_N:
				case COOKING_1_4_S: // フルーツサラダ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxMp(-20);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
						pc.sendPackets(new S_PacketBoxCooking(pc, 4, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_5_N:
				case COOKING_1_5_S: // フルーツ甘酢あんかけ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 5, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_6_N:
				case COOKING_1_6_S: // 猪肉の串焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-5);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 6, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_1_7_N:
				case COOKING_1_7_S: // キノコスープ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 7, 0));
						pc.setDessertId(0);
					}
					break;

				case COOKING_2_0_N:
				case COOKING_2_0_S: // キャビアカナッペ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 8, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_1_N:
				case COOKING_2_1_S: // アリゲーターステーキ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(-30);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) { // パーティー中
							pc.getParty().updateMiniHP(pc);
						}
						pc.addMaxMp(-30);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
						pc.sendPackets(new S_PacketBoxCooking(pc, 9, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_2_N:
				case COOKING_2_2_S: // タートルドラゴンの菓子
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(2);
						pc.sendPackets(new S_PacketBoxCooking(pc, 10, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_3_N:
				case COOKING_2_3_S: // キウィパロット焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 11, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_4_N:
				case COOKING_2_4_S: // スコーピオン焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 12, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_5_N:
				case COOKING_2_5_S: // イレッカドムシチュー
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-10);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 13, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_6_N:
				case COOKING_2_6_S: // クモ脚の串焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addSp(-1);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 14, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_2_7_N:
				case COOKING_2_7_S: // クラブスープ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 15, 0));
						pc.setDessertId(0);
					}
					break;

				case COOKING_3_0_N:
				case COOKING_3_0_S: // クラスタシアンのハサミ焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 16, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_1_N:
				case COOKING_3_1_S: // グリフォン焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(-50);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) { // パーティー中
							pc.getParty().updateMiniHP(pc);
						}
						pc.addMaxMp(-50);
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
						pc.sendPackets(new S_PacketBoxCooking(pc, 17, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_2_N:
				case COOKING_3_2_S: // コカトリスステーキ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 18, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_3_N:
				case COOKING_3_3_S: // タートルドラゴン焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(3);
						pc.sendPackets(new S_PacketBoxCooking(pc, 19, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_4_N:
				case COOKING_3_4_S: // レッサードラゴンの手羽先
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(-15);
						pc.sendPackets(new S_SPMR(pc));
						pc.addWind(-10);
						pc.addWater(-10);
						pc.addFire(-10);
						pc.addEarth(-10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 20, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_5_N:
				case COOKING_3_5_S: // ドレイク焼き
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addSp(-2);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_PacketBoxCooking(pc, 21, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_6_N:
				case COOKING_3_6_S: // 深海魚のシチュー
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(-30);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) { // パーティー中
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_PacketBoxCooking(pc, 22, 0));
						pc.setCookingId(0);
					}
					break;

				case COOKING_3_7_N:
				case COOKING_3_7_S: // バシリスクの卵スープ
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_PacketBoxCooking(pc, 23, 0));
						pc.setDessertId(0);
					}
					break;

				case ONLINE_GIFT:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.setOnlineGiftWiatEnd(true);
						T_OnlineGiftTable.get().check(pc);
					}
					break;

				case VIP:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.endVipStatus();
					}
					break;

				case ARMOR_SETS_GFX:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (!pc.isDead() && !pc.isTeleport()) {
							final int length = pc.get_armorsets_gfx().length;
							final Random rnd = new Random();
							final int show = pc.get_armorsets_gfx()[rnd.nextInt(length)];
							pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), show));
						}
						pc.setSkillEffect(ARMOR_SETS_GFX, pc.get_gfx_times() * 1000);
					}
					break;

				case WEAPON_SETS_GFX:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (!pc.isDead() && !pc.isTeleport()) {
							final L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								final int value = weapon.getEnchantLevel() - weapon.getItem().get_safeenchant();
								if (value >= 13) {
									if (ConfigAlt.WEAPON_EFFECT_6 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_6));
									}
								} else if (value >= 11) {
									if (ConfigAlt.WEAPON_EFFECT_5 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_5));
									}
								} else if (value >= 9) {
									if (ConfigAlt.WEAPON_EFFECT_4 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_4));
									}
								} else if (value >= 7) {
									if (ConfigAlt.WEAPON_EFFECT_3 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_3));
									}
								} else if (value >= 5) {
									if (ConfigAlt.WEAPON_EFFECT_2 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_2));
									}
								} else if (value >= 3) {
									if (ConfigAlt.WEAPON_EFFECT_1 > -1) {
										// 送出封包
										pc.sendPacketsAll(new S_EffectLocation(pc.getX(), pc.getY(), ConfigAlt.WEAPON_EFFECT_1));
									}
								}
							}
						}
						pc.setSkillEffect(WEAPON_SETS_GFX, ConfigAlt.WEAPON_EFFECT_DELAY * 1000);
					}
					break;
				case FISHING:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						if (!pc.isFishing() || (pc.getMapId() != 5490) || (pc.getOnlineStatus() != 1) || (pc.getNetConnection() == null)) {
							return;
						}
						final L1Fishing temp = FishingTable.get().get_item(pc.get_pole());
						if (temp == null) {
							pc.sendPackets(new S_ServerMessage(1136));
							// 此處可新增釣取失敗之特效
						} else {
							final L1Item item = ItemTable.get().getTemplate(temp.get_itemid());
							if (item != null) {
								CreateNewItem.createNewItem(pc, temp.get_itemid(), temp.get_count());
								// 151218 新增魚獲後會人物會出現特效
								final S_SkillSound sound = new S_SkillSound(pc.getId(), 13639);
								pc.sendPacketsX8(sound);
							}
						}
						if (pc.getInventory().consumeItem(41487, 1)) {
							pc.setSkillEffect(FISHING, pc.get_fishTime() * 1000);
							pc.sendPackets(new S_FishTime(pc.get_fishTime()));
						} else {
							pc.setFishing(false, -1, -1, -1, -1);
							pc.sendPackets(new S_ServerMessage(1163));
							pc.sendPacketsAll(new S_CharVisualUpdate(pc));
						}
					}
					break;

				case GIGANTIC:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addMaxHp(-pc.getGiganticHp());
						pc.setGiganticHp(0);
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
						if (pc.isInParty()) {
							pc.getParty().updateMiniHP(pc);
						}
					}
					break;
				}
			}
			// cha.removeSkillEffect(skillId);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		if (cha instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	// メッセージの表示（終了するとき）
	private static void sendStopMessage(final L1PcInstance charaPc, final int skillid) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillid);
		if ((l1skills == null) || (charaPc == null)) {
			return;
		}

		final int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}
