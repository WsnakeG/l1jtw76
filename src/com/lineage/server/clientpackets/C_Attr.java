package com.lineage.server.clientpackets;

import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.data.quest.CrownLv45_1;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.WriteLogTxt;
import com.lineage.server.datatables.CharApprenticeTable;
import com.lineage.server.datatables.CharObjidTable;
import com.lineage.server.datatables.ClanMembersTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.QuestMapTable;
import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.datatables.lock.ClanAllianceReading;
import com.lineage.server.datatables.lock.ClanEmblemReading;
import com.lineage.server.datatables.lock.ClanReading;
import com.lineage.server.datatables.lock.HouseReading;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.datatables.sql.CharacterTable;
import com.lineage.server.model.L1Alliance;
import com.lineage.server.model.L1Apprentice;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1ChatParty;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1War;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.classes.L1ClassFeature;
import com.lineage.server.model.item.L1ItemId;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_CharVisualUpdate;
import com.lineage.server.serverpackets.S_ClanUpdate;
import com.lineage.server.serverpackets.S_Message_YN;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_OwnCharStatus2;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxCharEr;
import com.lineage.server.serverpackets.S_PacketBoxPledge;
import com.lineage.server.serverpackets.S_Resurrection;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_Trade;
import com.lineage.server.serverpackets.ability.S_BaseAbility;
import com.lineage.server.serverpackets.ability.S_ConDetails;
import com.lineage.server.serverpackets.ability.S_DexDetails;
import com.lineage.server.serverpackets.ability.S_IntDetails;
import com.lineage.server.serverpackets.ability.S_StrDetails;
import com.lineage.server.serverpackets.ability.S_WeightStatus;
import com.lineage.server.serverpackets.ability.S_WisDetails;
import com.lineage.server.templates.L1House;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldWar;

/**
 * 要求點選項目的結果(Y/N)
 * 
 * @author dexc
 */
public class C_Attr extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Attr.class);

	/*
	 * public C_Attr() { } public C_Attr(final byte[] abyte0, final
	 * ClientExecutor client) { super(abyte0); try { this.start(abyte0, client);
	 * } catch (final Exception e) { _log.error(e.getLocalizedMessage(), e); } }
	 */

	// private static final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1
	// };

	// private static final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1
	// };

	@Override
	public void start(final byte[] decrypt, final ClientExecutor client) {
		try {
			// 資料載入
			read(decrypt);

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) { // 角色為空
				return;
			}

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			final int mode;

			// int uid = 0; (與血盟系統有關)

			if ((decrypt[1] != 0) && (decrypt[2] != 0)) {
				mode = readH();
			} else {
				readH();
				readD();
				mode = readH();
			}

			int c;

			switch (mode) {
			case 97: // \f3%0%s 想加入你的血盟。你接受嗎。(Y/N)
				c = readC();
				final L1PcInstance joinPc = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (joinPc != null) {
					if (c == 0) { // No
						// 96 \f1%0%s 拒絕你的請求
						joinPc.sendPackets(new S_ServerMessage(96, pc.getName()));

					} else if (c == 1) { // Yes
						final int clan_id = pc.getClanid();
						final String clanName = pc.getClanname();
						final L1Clan clan = WorldClan.get().getClan(clanName);
						if (clan != null) {
							int maxMember = 0;
							final int charisma = pc.getCha();
							boolean lv45quest = false;
							if (pc.getQuest().isEnd(CrownLv45_1.QUEST.get_id())) {
								lv45quest = true;
							}
							if (pc.getLevel() >= 50) { // Lv50以上
								if (lv45quest == true) { // Lv45クエストクリア済み
									maxMember = charisma * 9;

								} else {
									maxMember = charisma * 3;
								}
							} else { // Lv50未満
								if (lv45quest == true) { // Lv45クエストクリア済み
									maxMember = charisma * 6;

								} else {
									maxMember = charisma * 2;
								}
							}
							if (ConfigOther.CLANCOUNT != 0) {
								maxMember = ConfigOther.CLANCOUNT;
							}
							if (joinPc.getClanid() == 0) { // クラン未加入
								final String clanMembersName[] = clan.getAllMembers();
								if (maxMember <= clanMembersName.length) {
									// 188 %0%s 無法接受你成為該血盟成員。
									joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
									return;
								}

								for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
									// 94 \f1你接受%0當你的血盟成員。
									clanMembers.sendPackets(new S_ServerMessage(94, joinPc.getName()));
								}

								joinPc.setClanid(clan_id);
								joinPc.setClanname(clanName);
								joinPc.setClanRank(L1Clan.NORMAL_CLAN_RANK_ATTEND);

								joinPc.save();
								// 新入盟成員發送更新血盟數據
								joinPc.sendPackets(new S_ClanUpdate(joinPc.getId(), joinPc.getClanname(),
										joinPc.getClanRank()));
								clan.addMemberName(joinPc.getName());
								ClanMembersTable.getInstance().newMember(joinPc);
								// 在線上的血盟成員發送新加入成員血盟數據
								for (final L1PcInstance clanMembers : clan.getOnlineClanMember()) {
									clanMembers.sendPackets(new S_ClanUpdate(joinPc.getId(),
											joinPc.getClanname(), joinPc.getClanRank()));
								}
								// 95 \f1加入%0血盟。
								joinPc.sendPackets(new S_ServerMessage(95, clanName));

								// 王族發送加入血盟更新列表
								pc.sendPackets(new S_PacketBoxPledge(3, null, joinPc.getName(), joinPc.getClanRank()));		
								
							} else { // クラン加入済み（クラン連合）
								if (ConfigAlt.CLAN_ALLIANCE) {
									// 同盟系統 by terry0412
									final L1Clan clan2 = joinPc.getClan();
									if (clan2 != null) {
										// 取得指定同盟資料
										if (ClanAllianceReading.get()
												.getAlliance(clan2.getClanId()) != null) {
											// 同盟中無法加入聯合血盟.
											joinPc.sendPackets(new S_ServerMessage(2063));
											return;
										}
									}
									changeClan(client, pc, joinPc, maxMember);

								} else {
									// 89 \f1你已經有血盟了。
									joinPc.sendPackets(new S_ServerMessage(89));
								}
							}
						}
					}
				}
				break;

			case 217: // %0 血盟向你的血盟宣戰。是否接受？(Y/N)
			case 221: // %0 血盟要向你投降。是否接受？(Y/N)
			case 222: // %0 血盟要結束戰爭。是否接受？(Y/N)
				c = readC();
				// 宣戰者
				final L1PcInstance enemyLeader = (L1PcInstance) World.get().findObject(pc.getTempID());
				if (enemyLeader == null) {
					return;
				}
				pc.setTempID(0);
				final String clanName = pc.getClanname();
				final String enemyClanName = enemyLeader.getClanname();// 宣戰盟
				if (c == 0) { // No
					if (mode == 217) {
						// 236 %0 血盟拒絕你的宣戰。
						enemyLeader.sendPackets(new S_ServerMessage(236, clanName));

					} else if ((mode == 221) || (mode == 222)) {
						// 237 %0 血盟拒絕你的提案。
						enemyLeader.sendPackets(new S_ServerMessage(237, clanName));
					}

				} else if (c == 1) { // Yes
					if (mode == 217) {
						final L1War war = new L1War();
						war.handleCommands(2, enemyClanName, clanName); // 模擬戦開始

					} else if ((mode == 221) || (mode == 222)) {
						// 取回全部戰爭清單
						for (final L1War war : WorldWar.get().getWarList()) {
							if (war.checkClanInWar(clanName)) { // 戰爭中
								if (mode == 221) {
									war.surrenderWar(enemyClanName, clanName); // 投降

								} else if (mode == 222) {
									war.ceaseWar(enemyClanName, clanName); // 結束
								}
								break;
							}
						}
					}
				}
				break;

			case 252: // \f2%0%s 要與你交易。願不願交易？ (Y/N)
				c = readC();
				final L1PcInstance trading_partner = (L1PcInstance) World.get().findObject(pc.getTradeID());
				if (trading_partner != null) {
					if (c == 0) {// No
						// 253 %0%d 拒絕與你交易。
						trading_partner.sendPackets(new S_ServerMessage(253, pc.getName()));
						pc.setTradeID(0);
						trading_partner.setTradeID(0);

					} else if (c == 1) {// Yes
						pc.sendPackets(new S_Trade(trading_partner.getName()));
						trading_partner.sendPackets(new S_Trade(pc.getName()));
					}
				}
				break;

			case 321: // 321 是否要復活？ (Y/N)
				c = readC();
				final L1PcInstance resusepc1 = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (resusepc1 != null) { // 復活スクロール
					if (c == 0) { // No
						;
					} else if (c == 1) { // Yes
						pc.sendPacketsX8(new S_SkillSound(pc.getId(), '\346'));
						// pc.resurrect(pc.getLevel());
						// pc.setCurrentHp(pc.getLevel());
						pc.resurrect(pc.getMaxHp() / 2);
						pc.setCurrentHp(pc.getMaxHp() / 2);
						pc.startHpRegeneration();
						pc.startMpRegeneration();
						pc.stopPcDeleteTimer();
						pc.sendPacketsAll(new S_Resurrection(pc, resusepc1, 0));
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
					}
				}
				break;

			case 322: // 322 是否要復活？ (Y/N)
				c = readC();
				final L1PcInstance resusepc2 = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (resusepc2 != null) { // 祝福された 復活スクロール、リザレクション、グレーター リザレクション
					if (c == 0) { // No
						;
					} else if (c == 1) { // Yes

						pc.sendPacketsX8(new S_SkillSound(pc.getId(), '\346'));
						pc.resurrect(pc.getMaxHp());
						pc.setCurrentHp(pc.getMaxHp());
						pc.startHpRegeneration();
						pc.startMpRegeneration();
						pc.stopPcDeleteTimer();
						pc.sendPacketsAll(new S_Resurrection(pc, resusepc2, 0));
						pc.sendPacketsAll(new S_CharVisualUpdate(pc));
						// EXPロストしている、G-RESを掛けられた、EXPロストした死亡
						// 全てを満たす場合のみEXP復旧
						if ((pc.getExpRes() == 1) && pc.isGres() && pc.isGresValid()) {
							pc.resExp();
							pc.setExpRes(0);
							pc.setGres(false);
						}
					}
				}
				break;

			case 325: // 你想叫牠什麼名字？
				c = readC();
				final String petName = readS();

				if (pc.is_rname()) {
					String name = Matcher.quoteReplacement(petName);
					name = name.replaceAll("\\s", "");
					name = name.replaceAll("　", "");
					name = name.substring(0, 1).toUpperCase() + name.substring(1);

					for (final String ban : C_CreateChar.BANLIST) {
						if (name.indexOf(ban) != -1) {
							name = "";
						}
					}

					if (name.length() == 0) {
						// 53 無效的名字。 若您擅自修改，將無法進行遊戲。
						pc.sendPackets(new S_ServerMessage(53));
						return;
					}
					// 名稱是否包含禁止字元
					if (!C_CreateChar.isInvalidName(name)) {
						// 53 無效的名字。 若您擅自修改，將無法進行遊戲。
						pc.sendPackets(new S_ServerMessage(53));
						return;
					}

					// 檢查名稱是否以被使用
					if (CharObjidTable.get().charObjid(name) != 0) {
						// 58 已經有同樣的角色名稱。請重新輸入。
						pc.sendPackets(new S_ServerMessage(58));
						return;
					}
					// String srcname = pc.getName();
					World.get().removeObject(pc);// 移出世界

					pc.getInventory().consumeItem(41227, 1);

					BoardOrimReading.get().renewPcName(pc.getName(), name);

					// 加入玩家更名後的紀錄會紀錄於alllog資料夾下-20150327
					WriteLogTxt.Recording("角色更名記錄", "修改前名字為" + pc.getName() + "，修改後名字為" + name);

					pc.setName(name);
					CharObjidTable.get().reChar(pc.getId(), name);
					CharacterTable.get().newCharName(pc.getId(), name);
					World.get().storeObject(pc);// 重新加入世界
					// 改變顯示(復原正常)
					pc.sendPacketsAll(new S_ChangeName(pc, true));

					pc.sendPackets(new S_ServerMessage(166, "由於人物名稱異動<請重新登入遊戲>將於5秒後強制斷線!"));
					final KickPc kickPc = new KickPc(pc);
					kickPc.start_cmd();

				} else {
					final L1PetInstance pet = (L1PetInstance) World.get().findObject(pc.getTempID());
					pc.setTempID(0);
					renamePet(pet, petName);
				}
				pc.rename(false);
				break;

			case 512: // 請輸入血盟小屋名稱?
				c = readC(); // ?
				final String houseName = readS();
				final int houseId = pc.getTempID();
				pc.setTempID(0);
				if (houseName.length() <= 16) {
					final L1House house = HouseReading.get().getHouseTable(houseId);
					house.setHouseName(houseName);
					HouseReading.get().updateHouse(house); // DBに書き込み

				} else {
					pc.sendPackets(new S_ServerMessage(513)); // 血盟小屋名稱太長。
				}
				break;

			case 630: // %0%s 要與你決鬥。你是否同意？(Y/N)
				c = readC();
				final L1PcInstance fightPc = (L1PcInstance) World.get().findObject(pc.getFightId());
				if (c == 0) {
					pc.setFightId(0);
					fightPc.setFightId(0);
					// 631 %0%d 拒絕了與你的決鬥。
					fightPc.sendPackets(new S_ServerMessage(631, pc.getName()));

				} else if (c == 1) {
					fightPc.sendPackets(
							new S_PacketBox(S_PacketBox.MSG_DUEL, fightPc.getFightId(), fightPc.getId()));

					pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc.getFightId(), pc.getId()));
				}
				break;

			case 653: // 若你離婚，你的結婚戒指將會消失。你決定要離婚嗎？(Y/N)
				c = readC();
				final L1PcInstance target653 = (L1PcInstance) World.get().findObject(pc.getPartnerId());
				if (c == 0) { // No
					return;
				} else if (c == 1) { // Yes
					if (target653 != null) {
						target653.setPartnerId(0);
						target653.save();
						// 662 \f1你(妳)目前未婚。
						target653.sendPackets(new S_ServerMessage(662));

					} else {
						// 人物離線狀態
						CharacterTable.get();
						CharacterTable.updatePartnerId(pc.getPartnerId());
					}
				}

				pc.setPartnerId(0);
				pc.save(); // 資料存檔

				// 662 \f1你(妳)目前未婚。
				pc.sendPackets(new S_ServerMessage(662));
				break;

			case 654: // %0 向你(妳)求婚，你(妳)答應嗎?
				c = readC();
				final L1PcInstance partner = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (partner != null) {
					if (c == 0) { // No
						// 656 %0 拒絕你(妳)的求婚。
						partner.sendPackets(new S_ServerMessage(656, pc.getName()));

					} else if (c == 1) { // Yes
						/*
						 * if ((pc.getX() == 32730) && (pc.getY() == 32425) &&
						 * (pc.getMapId() == 4) && (pc.getHeading() == 2)) {
						 * FaceToFace.smt(pc); return; }
						 */
						pc.setPartnerId(partner.getId());
						pc.save();
						// 790 倆人的結婚在所有人的祝福下完成
						pc.sendPackets(new S_ServerMessage(790));
						// 655 恭喜!! %0 已接受你(妳)的求婚。
						pc.sendPackets(new S_ServerMessage(655, partner.getName()));

						partner.setPartnerId(pc.getId());
						partner.save();
						// 790 倆人的結婚在所有人的祝福下完成
						partner.sendPackets(new S_ServerMessage(790));
						// 655 恭喜!! %0 已接受你(妳)的求婚。
						partner.sendPackets(new S_ServerMessage(655, pc.getName()));
					}
				}
				break;

			/*
			 * case 729: // 盟主正在呼喚你，你要接受他的呼喚嗎？(Y/N) c = this.readC(); if (c ==
			 * 0) { // No ; } else if (c == 1) { // Yes this.callClan(pc); }
			 * break;
			 */

			case 738: // 恢復經驗值需消耗%0金幣。想要恢復經驗值嗎?
				c = readC();
				if (c == 0) { // No
					;
				} else if ((c == 1) && (pc.getExpRes() == 1)) { // Yes
					int cost = 0;
					final int level = pc.getLevel();
					final int lawful = pc.getLawful();
					if (level < 45) {
						cost = level * level * 100;

					} else {
						cost = level * level * 200;
					}

					if (lawful >= 0) {
						cost = (cost / 2);
					}

					if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
						pc.resExp();
						pc.setExpRes(0);

					} else {
						// 189 \f1金幣不足。
						pc.sendPackets(new S_ServerMessage(189));
					}
				}
				break;

			case 748:// 你的血盟成員想要傳送你。你答應嗎？(Y/N)
				c = readC();
				if (c == 0) { // No
					pc.setTeleportX(0);
					pc.setTeleportY(0);
					pc.setTeleportMapId((short) 0);
				} else if (c == 1) { // Yes
					final int newX = pc.getTeleportX();
					final int newY = pc.getTeleportY();
					final short mapId = pc.getTeleportMapId();
					L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
				}
				break;

			case 951: // 您要接受玩家 %0%s 提出的隊伍對話邀請嗎？(Y/N)
				c = readC();
				final L1PcInstance chatPc = (L1PcInstance) World.get().findObject(pc.getPartyID());
				if (chatPc != null) {
					if (c == 0) { // No
						// 423 %0%s 拒絕了您的邀請。
						chatPc.sendPackets(new S_ServerMessage(423, pc.getName()));
						pc.setPartyID(0);

					} else if (c == 1) { // Yes
						if (chatPc.isInChatParty()) {
							if (chatPc.getChatParty().isVacancy() || chatPc.isGm()) {
								chatPc.getChatParty().addMember(pc);

							} else {
								// 417 你的隊伍已經滿了，無法再接受隊員。
								chatPc.sendPackets(new S_ServerMessage(417));
							}

						} else {
							final L1ChatParty chatParty = new L1ChatParty();
							chatParty.addMember(chatPc);
							chatParty.addMember(pc);
							// 424 %0%s 加入了您的隊伍。
							chatPc.sendPackets(new S_ServerMessage(424, pc.getName()));
						}
					}
				}
				break;

			case 953: // 玩家 %0%s 邀請您加入隊伍？(Y/N)
				if (pc.getMapId() == 5140) {
					return;
				}
				c = readC();
				final L1PcInstance target = (L1PcInstance) World.get().findObject(pc.getPartyID());
				if (target != null) {
					if (c == 0) {// No
						// 423 %0%s 拒絕了您的邀請。
						target.sendPackets(new S_ServerMessage(423, pc.getName()));
						pc.setPartyID(0);

					} else if (c == 1) {// Yes
						if (target.isInParty()) {
							// 邀請加入者 已成立隊伍
							if (target.getParty().isVacancy()) {
								// 加入新的隊伍成員
								target.getParty().addMember(pc);

							} else {
								// 417：你的隊伍已經滿了，無法再接受隊員。
								target.sendPackets(new S_ServerMessage(417));
							}

						} else {
							// 邀請加入者 尚未成立隊伍
							final L1Party party = new L1Party();
							party.addMember(target);// 第一個加入隊伍者將為隊長
							party.addMember(pc);
							// 424：%0%s 加入了您的隊伍。
							target.sendPackets(new S_ServerMessage(424, pc.getName()));
						}
					}
				}
				break;

			case 479: // 你想提昇那一種屬性?
				c = readC();
				if (c == 1) {
					final String s = readS();
					if (!((pc.getLevel() - 50) > pc.getBonusStats())) {
						return;
					}
					if (s.equalsIgnoreCase("str")) {
						if (pc.getBaseStr() < ConfigAlt.POWER) {
							pc.addBaseStr((byte) 1); // 素のSTR値に+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 能力基本資訊-力量
							pc.sendPackets(new S_StrDetails(2,
									L1ClassFeature.calcStrDmg(pc.getStr(), pc.getBaseStr()),
									L1ClassFeature.calcStrHit(pc.getStr(), pc.getBaseStr()),
									L1ClassFeature.calcStrDmgCritical(pc.getStr(), pc.getBaseStr()),
									L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon())
									));
							
							// XXX 重量程度資訊
							pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));;
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}

					} else if (s.equalsIgnoreCase("dex")) {
						if (pc.getBaseDex() < ConfigAlt.POWER) {
							pc.addBaseDex((byte) 1); // 素のDEX値に+1
							// XXX 7.6 ADD
							pc.sendPackets(new S_PacketBoxCharEr(pc));// 角色迴避率更新
							pc.resetBaseAc();
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 能力基本資訊-敏捷
							pc.sendPackets(new S_DexDetails(2,
									L1ClassFeature.calcDexDmg(pc.getDex(), pc.getBaseDex()),
									L1ClassFeature.calcDexHit(pc.getDex(), pc.getBaseDex()),
									L1ClassFeature.calcDexDmgCritical(pc.getDex(), pc.getBaseDex()),
									L1ClassFeature.calcDexAc(pc.getDex()),
									L1ClassFeature.calcDexEr(pc.getDex())
									));
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}

					} else if (s.equalsIgnoreCase("con")) {
						if (pc.getBaseCon() < ConfigAlt.POWER) {
							pc.addBaseCon((byte) 1); // 素のCON値に+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 能力基本資訊-體質
							pc.sendPackets(new S_ConDetails(2,
									L1ClassFeature.calcConHpr(pc.getCon(), pc.getBaseCon()),
									L1ClassFeature.calcConPotionHpr(pc.getCon(), pc.getBaseCon()),
									L1ClassFeature.calcAbilityMaxWeight(pc.getStr(), pc.getCon()),
									L1ClassFeature.calcBaseClassLevUpHpUp(pc.getType()) + L1ClassFeature.calcBaseConLevUpExtraHpUp(pc.getType(), pc.getBaseCon())
									));
							// XXX 重量程度資訊
							pc.sendPackets(new S_WeightStatus(pc.getInventory().getWeight() * 100 / (int)pc.getMaxWeight(), pc.getInventory().getWeight(), (int)pc.getMaxWeight()));
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}

					} else if (s.equalsIgnoreCase("int")) {
						if (pc.getBaseInt() < ConfigAlt.POWER) {
							pc.addBaseInt((byte) 1); // 素のINT値に+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 能力基本資訊-智力
							pc.sendPackets(new S_IntDetails(2,
									L1ClassFeature.calcIntMagicDmg(pc.getInt(), pc.getBaseInt()),
									L1ClassFeature.calcIntMagicHit(pc.getInt(), pc.getBaseInt()),
									L1ClassFeature.calcIntMagicCritical(pc.getInt(), pc.getBaseInt()),
									L1ClassFeature.calcIntMagicBonus(pc.getType(), pc.getInt()),
									L1ClassFeature.calcIntMagicConsumeReduction(pc.getInt())
									));
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}

					} else if (s.equalsIgnoreCase("wis")) {
						if (pc.getBaseWis() < ConfigAlt.POWER) {
							pc.addBaseWis((byte) 1); // 素のWIS値に+1
							pc.resetBaseMr();
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 能力基本資訊-精神
							pc.sendPackets(new S_WisDetails(2,
									L1ClassFeature.calcWisMpr(pc.getWis(), pc.getBaseWis()),
									L1ClassFeature.calcWisPotionMpr(pc.getWis(), pc.getBaseWis()),
									L1ClassFeature.calcStatMr(pc.getWis()) + L1ClassFeature.newClassFeature(pc.getType()).getClassOriginalMr(),
									L1ClassFeature.calcBaseWisLevUpMpUp(pc.getType(), pc.getBaseWis())
									));
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}

					} else if (s.equalsIgnoreCase("cha")) {
						if (pc.getBaseCha() < ConfigAlt.POWER) {
							pc.addBaseCha((byte) 1); // 素のCHA値に+1
							pc.setBonusStats(pc.getBonusStats() + 1);
							// XXX 純能力資訊
							pc.sendPackets(new S_BaseAbility(pc.getBaseStr(), pc.getBaseInt(), pc.getBaseWis(), pc.getBaseDex(), pc.getBaseCon(), pc.getBaseCha()));
							pc.sendPackets(new S_OwnCharStatus(pc));
							pc.sendPackets(new S_OwnCharStatus2(pc));
							pc.sendPackets(new S_CharVisualUpdate(pc));
							pc.save(); // 人物資料記錄

						} else {
							// 481 \f1屬性最大值只能到35。 請重試一次。
							pc.sendPackets(new S_ServerMessage("\\aH屬性最大值只能到25 ，請重試一次。"));
						}
					}
					// 判斷是否還有剩餘獎勵點數
					if ((pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats())
							|| (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc
									.getBonusStats() - 49)) {
						if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
								+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < (ConfigAlt.POWER * 6)) { // 設定能力值上限
							int bonus = (pc.getLevel() - 50) - pc.getBonusStats();// 可以點的點數 XXX 7.6C ADD
							pc.sendPackets(new S_Message_YN(479, bonus));
						}
					}
				}
				break;

			// 師徒系統 by terry0412
			case 2958: // 要和 %0 締結師徒關係嗎？
				L1PcInstance targetPc = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (targetPc == null) {
					return;
				}
				if (readC() == 1) {
					L1PcInstance pc_1;
					L1PcInstance pc_2;
					// 判斷師徒關係
					if (pc.getLevel() > targetPc.getLevel()) {
						pc_1 = pc;
						pc_2 = targetPc;
					} else {
						pc_1 = targetPc;
						pc_2 = pc;
					}
					L1Apprentice apprentice = CharApprenticeTable.getInstance().getApprentice(pc_1);
					if (apprentice != null) {
						if (apprentice.checkSize()) {
							// put-in
							apprentice.getTotalList().add(pc_2);
							pc_1.setApprentice(apprentice);
							pc_1.setPunishTime(null);
							pc_2.setApprentice(apprentice);
							pc_2.setPunishTime(null);
							CharApprenticeTable.getInstance().updateApprentice(pc_1.getId(),
									apprentice.getTotalList());
							// 師徒締結成功。
							pc_1.sendPackets(new S_ServerMessage(2964));
							pc_2.sendPackets(new S_ServerMessage(2964));
							return;
						}
					} else {
						apprentice = new L1Apprentice(pc_1, pc_2);
						pc_1.setApprentice(apprentice);
						pc_1.setPunishTime(null);
						pc_2.setApprentice(apprentice);
						pc_2.setPunishTime(null);
						CharApprenticeTable.getInstance().insertApprentice(apprentice);
						// 師徒締結成功。
						pc_1.sendPackets(new S_ServerMessage(2964));
						pc_2.sendPackets(new S_ServerMessage(2964));
						return;
					}
				}
				pc.sendPackets(new S_ServerMessage(2965));
				targetPc.sendPackets(new S_ServerMessage(2965));
				break;

			case 2967: // 要將 %0 奉為師父嗎？
			case 2968: // 要接受 %0 為弟子嗎？
				if (readC() == 1) {
					targetPc = (L1PcInstance) World.get().findObject(pc.getTempID());
					pc.setTempID(0);
					if (targetPc == null) {
						return;
					}
					targetPc.setTempID(pc.getId());
					targetPc.sendPackets(new S_Message_YN(2958, pc.getName()));
				}
				break;

			// 同盟系統 by terry0412
			case 223: // %0 血盟要與你同盟。是否接受？(Y/N)
				final L1PcInstance alliancePc = (L1PcInstance) World.get().findObject(pc.getTempID());
				pc.setTempID(0);
				if (alliancePc == null) {
					return;
				}
				c = readC();

				if (c == 1) {
					final L1Clan clan = pc.getClan();
					if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
						// 血盟君主才可使用此命令。
						pc.sendPackets(new S_ServerMessage(518));
						return;
					}

					final L1Clan target_clan = alliancePc.getClan();
					if (target_clan == null) {
						return;
					}

					L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
					if (alliance == null) {
						alliance = new L1Alliance(clan.getClanId(), clan, target_clan);
						ClanAllianceReading.get().insertAlliance(alliance);

					} else {
						if (!alliance.checkSize()) {
							alliancePc.sendPackets(new S_ServerMessage(1201));
							return;
						}
						alliance.addAlliance(target_clan);
						ClanAllianceReading.get().updateAlliance(alliance.getOrderId(),
								alliance.getTotalList());
					}

					World.get().broadcastPacketToAll(
							new S_ServerMessage(224, clan.getClanName(), target_clan.getClanName()));

					alliance.sendPacketsAll("", new S_ServerMessage(1200, target_clan.getClanName()));

				} else if (c == 0) {
					alliancePc.sendPackets(new S_ServerMessage(1198));
				}
				break;

			case 1210: // 確定要退出同盟嗎? (Y/N)
				if (readC() == 1) {
					final L1Clan clan = pc.getClan();
					if ((clan == null) || (pc.getId() != clan.getLeaderId())) {
						// 血盟君主才可使用此命令。
						pc.sendPackets(new S_ServerMessage(518));
						return;
					}

					final L1Alliance alliance = ClanAllianceReading.get().getAlliance(clan.getClanId());
					if (alliance == null) {
						return;
					}

					for (final L1Clan l1clan : alliance.getTotalList()) {
						if (l1clan.getClanId() == clan.getClanId()) {
							alliance.getTotalList().remove(l1clan);
							break;
						}
					}

					if (alliance.getTotalList().size() < 2) {
						ClanAllianceReading.get().deleteAlliance(alliance.getOrderId());

					} else {
						ClanAllianceReading.get().updateAlliance(alliance.getOrderId(),
								alliance.getTotalList());
					}

					World.get().broadcastPacketToAll(new S_ServerMessage(225,
							alliance.getTotalList().get(0).getClanName(), clan.getClanName()));

					alliance.sendPacketsAll("", new S_ServerMessage(1204, clan.getClanName()));

				}
				break;
				
			case 4774:
					c = this.readC();
					if (c == 0) { // No
						;
					} else if (c == 1) { // Yes
						this.callClan_W(pc);
					}
				break;
			default:
				break;
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);

		} finally {
			over();
		}
	}

	private void changeClan(final ClientExecutor clientthread, final L1PcInstance pc,
			final L1PcInstance joinPc, final int maxMember) {
		final int clanId = pc.getClanid();
		final String clanName = pc.getClanname();
		final L1Clan clan = WorldClan.get().getClan(clanName);
		final String clanMemberName[] = clan.getAllMembers();
		final int clanNum = clanMemberName.length;

		final int oldClanId = joinPc.getClanid();
		final String oldClanName = joinPc.getClanname();
		final L1Clan oldClan = WorldClan.get().getClan(oldClanName);
		final String oldClanMemberName[] = oldClan.getAllMembers();
		final int oldClanNum = oldClanMemberName.length;
		if ((clan != null) && (oldClan != null) && joinPc.isCrown() && // 自分が君主
				(joinPc.getId() == oldClan.getLeaderId())) {
			if (maxMember < (clanNum + oldClanNum)) { // 空きがない
				// 188 %0%s 無法接受你成為該血盟成員。
				joinPc.sendPackets(new S_ServerMessage(188, pc.getName()));
				return;
			}

			final L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				// 94 \f1你接受%0當你的血盟成員。
				clanMember[cnt].sendPackets(new S_ServerMessage(94, joinPc.getName()));
			}

			for (int i = 0; i < oldClanMemberName.length; i++) {
				final L1PcInstance oldClanMember = World.get().getPlayer(oldClanMemberName[i]);
				if (oldClanMember != null) { // オンライン中の旧クランメンバー
					ClanMembersTable.getInstance().deleteMember(oldClanMember.getId());
					oldClanMember.setClanid(clanId);
					oldClanMember.setClanname(clanName);
					// 血盟連合に加入した君主はガーディアン
					// 君主が連れてきた血盟員は見習い
					if (oldClanMember.getId() == joinPc.getId()) {
						oldClanMember.setClanRank(L1Clan.CLAN_RANK_GUARDIAN);

					} else {
						oldClanMember.setClanRank(L1Clan.ALLIANCE_CLAN_RANK_ATTEND);
					}

					try {
						// 資料存檔
						oldClanMember.save();

					} catch (final Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}

					clan.addMemberName(oldClanMember.getName());
					ClanMembersTable.getInstance().newMember(oldClanMember); // 加入成員資料
					// 95 \f1加入%0血盟。
					oldClanMember.sendPackets(new S_ServerMessage(95, clanName));

				} else { // オフライン中の旧クランメンバー
					try {
						final L1PcInstance offClanMember = CharacterTable.get()
								.restoreCharacter(oldClanMemberName[i]);
						offClanMember.setClanid(clanId);
						offClanMember.setClanname(clanName);
						offClanMember.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
						offClanMember.save(); // 資料存檔
						clan.addMemberName(offClanMember.getName());
						ClanMembersTable.getInstance().newMember(offClanMember); // 加入成員資料

					} catch (final Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}
			}

			// 資料刪除
			ClanEmblemReading.get().deleteIcon(oldClanId);
			/*
			 * final String emblem_file = String.valueOf(oldClanId); final File
			 * file = new File("emblem/" + emblem_file); file.delete();
			 */
			ClanReading.get().deleteClan(oldClanName);
		}
	}

	private static void renamePet(final L1PetInstance pet, final String name) {
		if ((pet == null) || (name == null)) {
			throw new NullPointerException();
		}

		final int petItemObjId = pet.getItemObjId();
		final L1Pet petTemplate = PetReading.get().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		final L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetReading.get().isNameExists(name)) {
			// 327 同樣的名稱已經存在。
			pc.sendPackets(new S_ServerMessage(327));
			return;
		}

		final L1Npc l1npc = NpcTable.get().getTemplate(pet.getNpcId());
		if (!(pet.getName().equalsIgnoreCase(l1npc.get_name()))) {
			// 326 一旦你已決定就不能再變更。
			pc.sendPackets(new S_ServerMessage(326));
			return;
		}

		pet.setName(name);
		petTemplate.set_name(name);
		PetReading.get().storePet(petTemplate);

		final L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
		pc.getInventory().updateItem(item);
		pc.sendPacketsAll(new S_ChangeName(pet.getId(), name));
	}

	public static void callClan(final L1PcInstance pc) {
		final L1PcInstance callClanPc = (L1PcInstance) World.get().findObject(pc.getTempID());
		pc.setTempID(0);

		// 無法攻擊/使用道具/技能/回城的狀態 XXX
		if (pc.isParalyzedX()) {
			return;
		} // */
		if (callClanPc == null) {
			return;
		}
		if (!pc.getMap().isEscapable() && !pc.isGm()) {
			// 這附近的能量影響到瞬間移動。在此地無法使用瞬間移動。
			pc.sendPackets(new S_ServerMessage(647));
			return;
		}
		if (pc.getId() != callClanPc.getCallClanId()) {
			return;
		}

		boolean isInWarArea = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
		if (castleId != 0) {
			isInWarArea = true;
			if (ServerWarExecutor.get().isNowWar(castleId)) {
				isInWarArea = false; // 戦争時間中は旗内でも使用可能
			}
		}
		final short mapId = callClanPc.getMapId();
		if (((mapId != 0) && (mapId != 4) && (mapId != 304)) || isInWarArea) {
			// 626 因太遠以致於無法傳送到你要去的地方。
			pc.sendPackets(new S_ServerMessage(629));
			return;
		}

		// 副本地圖中判斷
		if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
			// 因太遠以致於無法傳送到你要去的地方。
			pc.sendPackets(new S_ServerMessage(629));
			return;
		}

		final int HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

		final int HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

		final L1Map map = callClanPc.getMap();
		int locX = callClanPc.getX();
		int locY = callClanPc.getY();

		int heading = callClanPc.getCallClanHeading();
		locX += HEADING_TABLE_X[heading];
		locY += HEADING_TABLE_Y[heading];
		heading = (heading + 4) % 4;

		/*
		 * final Random random = new Random(); locX += (random.nextInt(6) - 3);
		 * locY += (random.nextInt(6) - 3);
		 */

		boolean isExsistCharacter = false;
		for (final L1Object object : World.get().getVisibleObjects(callClanPc, 1)) {
			if (object instanceof L1Character) {
				final L1Character cha = (L1Character) object;
				if ((cha.getX() == locX) && (cha.getY() == locY) && (cha.getMapId() == mapId)) {
					isExsistCharacter = true;
					break;
				}
			}
		}

		if (((locX == 0) && (locY == 0)) || !map.isPassable(locX, locY, null) || isExsistCharacter) {
			// 因你要去的地方有障礙物以致於無法直接傳送到該處。
			pc.sendPackets(new S_ServerMessage(627));
			return;
		}
		L1Teleport.teleport(pc, locX, locY, mapId, heading, true, L1Teleport.CALL_CLAN);
	}

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}

	private class KickPc implements Runnable {

		private final ClientExecutor _client;

		private KickPc(final L1PcInstance pc) {
			_client = pc.getNetConnection();
		}

		private void start_cmd() {
			GeneralThreadPool.get().execute(this);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(5000);
				_client.kick();

			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private void callClan_W(final L1PcInstance pc) {
		final L1PcInstance callClanPc = (L1PcInstance) World.get().findObject(
				pc.getTempID());
		//額外條件(轉生次數)確認地圖
		boolean tel = false;
		int[] CheckMap = new int[]{2087,2086};
		int[] HeroMap = new int[]{2089}; //英雄地圖傳送判斷
		int[] Heroitems = new int[]{85203,85204}; 
		//對應地圖轉生次數
		int[] CheckMapMeleLv = new int[]{1,1};		
		pc.setTempID(0);
		// 無法攻擊/使用道具/技能/回城的狀態 XXX
		if (pc.isParalyzedX()) {
			return;
		}// */
		if (callClanPc == null) {
			return;
		}

		if (!callClanPc.getMap().isArrows()) {
			pc.sendPackets(new S_ServerMessage("無法前往限制地圖."));
			return;
		}
		
		boolean isInWarArea = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
		if (castleId != 0) {
			isInWarArea = ServerWarExecutor.get().isNowWar(castleId);
		}
		final short mapId = callClanPc.getMapId();
		if (isInWarArea) {
			// 626 因太遠以致於無法傳送到你要去的地方。
			pc.sendPackets(new S_ServerMessage(629));
			return;
		}

		// 副本地圖中判斷
		if (QuestMapTable.get().isQuestMap(pc.getMapId())) {
			// 626 因太遠以致於無法傳送到你要去的地方。
			pc.sendPackets(new S_ServerMessage(629));
			return;
		}
		int j = 0 ;
		for(int i : CheckMap){
			if(mapId==i){
				if(pc.getMeteLevel() < CheckMapMeleLv[j]){
					pc.sendPackets(new S_ServerMessage("\\fN前往當前地圖需要["+CheckMapMeleLv[j]+"]轉，你的轉生次數不足。"));
					return;
				}
				break;
			}
			j++;
		}
		j = 0 ;
		for(int i : HeroMap){
			if(mapId==i){
				 for (final int obj : Heroitems) {		
						if (pc.getInventory().findItemId(obj) != null) {
							tel=true;						
						}											
				 }
				 if(!tel){
					pc.sendPackets(new S_SystemMessage("前往當前地圖需要英雄服務票卷。"));
					return;	
				 }
			break;
			}
			j++;
		}
		

		int locX = callClanPc.getX();
		int locY = callClanPc.getY();

		int heading = callClanPc.getHeading();

		L1Teleport.teleport(pc, locX, locY, mapId, heading, true, 3);
	}
	
	
}