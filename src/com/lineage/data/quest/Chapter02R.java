package com.lineage.data.quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.lock.BoardOrimReading;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Party;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1MerchantInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.ServerBasePacket;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.templates.L1Rank;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldMob;

/**
 * 影法師歐林的故事<BR>
 * <海戰副本>
 * 
 * @author terry0412
 */
public class Chapter02R extends TimerTask {

	private static final Log _log = LogFactory.getLog(Chapter02R.class);

	private ScheduledFuture<?> _timer;

	private static final Random _random = new Random();

	public static final int DELAY_SPEED = 1;

	private final L1QuestUser quest;

	public final L1Party party;

	private final L1Location loc;

	private static final L1Location[] CABIN_LOC_LIST = new L1Location[4];

	static {
		CABIN_LOC_LIST[0] = new L1Location(32671, 32802, Chapter02.MAPID);
		// CABIN_LOC_LIST[0] = new L1Location(32671, 32866, Chapter02.MAPID);
		CABIN_LOC_LIST[1] = new L1Location(32735, 32802, Chapter02.MAPID);
		CABIN_LOC_LIST[2] = new L1Location(32735, 32862, Chapter02.MAPID);
		CABIN_LOC_LIST[3] = new L1Location(32799, 32863, Chapter02.MAPID);
	}

	private final L1NpcInstance door;

	private final L1NpcInstance npc1;

	private final L1NpcInstance npc2;

	private L1NpcInstance ship;

	public L1NpcInstance portal;

	private volatile int ship_step = -1;

	private int counter1;

	private int counter2;

	private int counter3;

	private boolean fire_order;

	private boolean defense_okay;

	private int critical_hit;

	private boolean hit_okay;

	private int counter_alt;

	private int counter_alt2;

	private L1PcInstance quest_pc;

	private int mimic_quest_order;

	private int mimic_quest_count;

	private boolean squid_version;

	private int round;

	private int old_score;

	private int check_power;

	private int mob_power;

	private int count_down;

	private int fire_point;

	private L1Location next_fire_loc;

	private boolean send_gift;

	public Chapter02R(final L1QuestUser quest, final L1Party party, final L1NpcInstance door,
			final L1NpcInstance npc1, final L1NpcInstance npc2) {
		this.quest = quest;
		this.party = party;
		loc = new L1Location(32798, 32803, quest.get_mapid());
		this.door = door;
		this.npc1 = npc1;
		this.npc2 = npc2;
		next_fire_loc = loc.randomLocation(8, true);
	}

	public void startR() {
		final int timeMillis = 10000 * DELAY_SPEED; // 10秒
		_timer = GeneralThreadPool.get().schedule(this, timeMillis);
	}

	@Override
	public void run() {
		cancel();
		try {
			if (round <= 0) {
				sendPacketsToAll(new S_NpcChat(party.getLeader().getId(), "$9529"));
				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9529"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9530"));
				Thread.sleep(4000 * DELAY_SPEED);

				if (party.getLeader().get_actionId() != ActionCodes.ACTION_Salute) {
					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9531"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9532"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9533"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9534"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9535"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9536"));

					final L1NpcInstance effect1 = L1SpawnUtil.spawnT(97109, 32797, 32808,
							(short) quest.get_mapid(), 0, 100);
					effect1.set_showId(quest.get_id());
					quest.addNpc(effect1);
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9537"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9538"));
					Thread.sleep(4000 * DELAY_SPEED);

					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9539"));
					final L1NpcInstance effect2 = L1SpawnUtil.spawnT(97110, 32801, 32808,
							(short) quest.get_mapid(), 0, 100);
					effect2.set_showId(quest.get_id());
					quest.addNpc(effect2);
					Thread.sleep(5000 * DELAY_SPEED);
					effect1.set_spawnTime(1);
					effect2.set_spawnTime(1);
				}

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9540"));
				Thread.sleep(5000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9603"));
				sendPacketsToAll(new S_PacketBoxGree(0x01));

				quest.spawnQuestMob(0, 5);
				Thread.sleep(10000 * DELAY_SPEED);

				round = 1;
			}

			for (final int n = 12; round <= n; round++) {
				Thread.sleep(3000 * DELAY_SPEED);

				if (send_gift) {
					break;
				}

				if (quest.size() < 3) {
					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9547"));
					break;
				} else if (fire_point >= 10) {
					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9562"));
					break;
				} else {
					hit_okay = false;
					counter_alt = 0;
					counter_alt2 = 0;
					mimic_quest_order = -1;
					if ((round % 4) == 0) {
						critical_hit = 0;
					}
					if (fire_point >= 7) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$12097"));
					} else {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9548"));
					}
				}
				Thread.sleep(4000 * DELAY_SPEED);

				final int score_before = quest.get_score();
				final int type = randomHintSeaMonster();
				if (type == 1) {
					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9541"));
					sendPacketsToAll(new S_EffectLocation(32801, 32789, 8142));
					sendPacketsToAll(new S_EffectLocation(32798, 32820, 8142));
				} else if (type == 2) {
					if (++counter1 < 3) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9542"));
						spawnSeaMonster(97097);
						Thread.sleep(10000);
					}
				} else if (type == 3) {
					if (++counter2 < 3) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9543"));
						spawnSeaMonster(97098);
						Thread.sleep(10000);
					}
				} else if (type == 4) {
					if (++counter3 < 3) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9544"));
						sendPacketsToAll(new S_EffectLocation(32800, 32794, 8241));
						Thread.sleep(5000);
						sendPacketsToAll(new S_EffectLocation(32800, 32794, 8241));
					}
				}
				Thread.sleep(10000);

				if (round >= 12) {
					sendPacketsToAll(new S_PacketBoxGree(0x02, "$9556"));
					Thread.sleep(3000 * DELAY_SPEED);

					final int locx = fire_order ? 32796 : 32802;
					final int locy = 32824;
					ship = L1SpawnUtil.spawn(97099, new L1Location(locx, locy, quest.get_mapid()), 0,
							quest.get_id());
					ship.WORK.work(ship);
				} else {
					if ((counter1 >= 3) || (counter2 >= 3) || (counter3 >= 3)) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9549"));
						Thread.sleep(4000 * DELAY_SPEED);

						sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$" + (9608 + round)));
						sendPacketsToAll(new S_PacketBoxGree(0x01));
						Thread.sleep(3000 * DELAY_SPEED);

						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9558"));
						if (counter1 >= 3) {
							for (int i = 0, size = 3; i < size; i++) {
								final L1Location new_loc = loc.randomLocation(5, true);
								L1SpawnUtil.spawn(97095, new_loc, 0, quest.get_id()).set_quest_id(56255);
							}
							counter1 = 0;
							Thread.sleep(20000 * DELAY_SPEED);
						} else if (counter2 >= 3) {
							for (int i = 0, size = 3; i < size; i++) {
								final L1Location new_loc = loc.randomLocation(5, true);
								L1SpawnUtil.spawn(97096, new_loc, 0, quest.get_id()).set_quest_id(56255);
							}
							counter2 = 0;
							Thread.sleep(20000 * DELAY_SPEED);
						} else if (counter3 >= 3) {
							L1SpawnUtil.spawn(97103, new L1Location(32795, 32795, loc.getMapId()), 0,
									quest.get_id());
							L1SpawnUtil.spawn(97104, new L1Location(32804, 32796, loc.getMapId()), 0,
									quest.get_id());
							Thread.sleep(4000);
							final L1NpcInstance squid = L1SpawnUtil.spawn(97105,
									new L1Location(32800, 32794, loc.getMapId()), 0, quest.get_id());

							counter3 = 0;
							Thread.sleep(5000);
							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$10720"));
							Thread.sleep(55000);
							boolean checkNotDead = false;
							for (final L1NpcInstance npc : quest.npcList()) {
								if ((npc.getNpcId() == 97105) && !npc.isDead()) {
									checkNotDead = true;
									break;
								}
							}
							if (!checkNotDead) {
								L1SpawnUtil.spawn(97118, new L1Location(32796, 32803, quest.get_mapid()), 0,
										quest.get_id()).set_quest_id(49311);
							} else {
								squid.set_spawnTime(1);
							}
							Thread.sleep(10000 * DELAY_SPEED);
						}
					} else {
						if ((check_power >= 120) || (_random.nextInt(5) == 0)) {
							sendPacketsToAll(new S_PacketBoxGree(0x02, "$9588"));
							mob_power = Math.min(mob_power + 1, 2);
						} else if ((check_power < 60) || (_random.nextInt(5) == 0)) {
							sendPacketsToAll(new S_PacketBoxGree(0x02, "$9589"));
							mob_power = Math.max(mob_power - 1, 0);
						} else {
							sendPacketsToAll(new S_PacketBoxGree(0x02, "$9550"));
						}
						check_power = 0;

						randomMagicCircle();
						Thread.sleep(10000);

						defenseFailed();
						Thread.sleep(2000);

						randomMagicCircle();
						Thread.sleep(6000);

						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9551"));

						spawnEnemyShip();
						Thread.sleep(4000);

						defenseFailed();
						Thread.sleep(2000);

						randomMagicCircle();
						Thread.sleep(10000);

						defenseFailed();

						counter_alt = 0;
						counter_alt2 = 0;
					}
					for (int i = 0; i < 300; i++) {
						Thread.sleep(500);
						if (ship_step == 0) {
							ship_step = -1;
							count_down = 60 * DELAY_SPEED;
							sendPacketsToAll(new S_PacketBoxGree(0x02));
							if (door.getCurrentHp() > 377) {
								door.setCurrentHp(door.getCurrentHp() - 377);
								final int nowStatus = ActionCodes.ACTION_DoorAction5
										- (door.getCurrentHp() / 1000);
								if (door.getStatus() != nowStatus) {
									door.setStatus(Math.min(nowStatus, ActionCodes.ACTION_DoorAction5));
									door.broadcastPacketAll(
											new S_DoActionGFX(door.getId(), door.getStatus()));
								}
							}
							if (round < 12) {
								if (ship.getGfxId() == 8263) {
									sendPacketsToAll(new S_PacketBoxGree(0x02, "$9552"));
								} else if (ship.getGfxId() == 8260) {
									sendPacketsToAll(new S_PacketBoxGree(0x02, "$9553"));
								} else if (ship.getGfxId() == 8166) {
									sendPacketsToAll(new S_PacketBoxGree(0x02, "$9554"));
								} else {
									sendPacketsToAll(new S_PacketBoxGree(0x02, "$9555"));
								}
							}
						} else if (ship_step == 1) {
							ship_step = -1;
							Thread.sleep(2000 * DELAY_SPEED);
							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$" + (9608 + round)));
							sendPacketsToAll(new S_PacketBoxGree(0x01));
							Thread.sleep(4000 * DELAY_SPEED);

							if (critical_hit >= 3) {
								sendPacketsToAll(new S_PacketBoxGree(0x02, "$9559"));
								Thread.sleep(2000 * DELAY_SPEED);
								portal = L1SpawnUtil.spawn(97111,
										new L1Location(32799, 32809, quest.get_mapid()), 0, quest.get_id());
							}
							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$9563"));
							quest.add_score(_random.nextInt(30));
							if (round >= 12) {
								if (quest.get_score() < 1000) {
									quest.spawnQuestMob(252);
								} else if (quest.get_score() < 1200) {
									quest.spawnQuestMob(253);
								} else if (quest.get_score() < 2000) {
									quest.spawnQuestMob(254);
								} else {
									quest.spawnQuestMob(255);
								}
							}
							if (portal == null) {
								quest.spawnQuestMob(1 + mob_power + ((round - 1) * 10), 4);
							} else {
								final int ship_type = getCabinLocation();
								if (ship_type == 1) {
									quest.spawnQuestMob(191 + mob_power, 4);
									quest.spawnQuestMob(201 + mob_power, 4);
								} else if (ship_type == 2) {
									quest.spawnQuestMob(211 + mob_power, 4);
									quest.spawnQuestMob(221 + mob_power, 4);
								} else if (ship_type == 3) {
									quest.spawnQuestMob(231 + mob_power, 4);
									quest.spawnQuestMob(241 + mob_power, 4);
								}
							}
							Thread.sleep(15000 * DELAY_SPEED);

							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$9564"));
							if (portal == null) {
								quest.spawnQuestMob(4 + mob_power + ((round - 1) * 10), 4);
							} else {
								final int ship_type = getCabinLocation();
								if (ship_type == 1) {
									quest.spawnQuestMob(194 + mob_power, 4);
									quest.spawnQuestMob(204 + mob_power, 4);
								} else if (ship_type == 2) {
									quest.spawnQuestMob(214 + mob_power, 4);
									quest.spawnQuestMob(224 + mob_power, 4);
								} else if (ship_type == 3) {
									quest.spawnQuestMob(234 + mob_power, 4);
									quest.spawnQuestMob(244 + mob_power, 4);
								}
							}
							Thread.sleep(15000 * DELAY_SPEED);

							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=$9565"));
							if (portal == null) {
								quest.spawnQuestMob(7 + mob_power + ((round - 1) * 10), 4);
							} else {
								final int ship_type = getCabinLocation();
								if (ship_type == 1) {
									quest.spawnQuestMob(197 + mob_power, 4);
									quest.spawnQuestMob(207 + mob_power, 4);
								} else if (ship_type == 2) {
									quest.spawnQuestMob(217 + mob_power, 4);
									quest.spawnQuestMob(227 + mob_power, 4);
								} else if (ship_type == 3) {
									quest.spawnQuestMob(237 + mob_power, 4);
									quest.spawnQuestMob(247 + mob_power, 4);
								}
							}
							Thread.sleep(15000 * DELAY_SPEED);

							// this.sendPacketsToAll(new S_PacketBoxGree(0x02,
							// "\\f=$9566"));
							// Thread.sleep(15000 * DELAY_SPEED);

							final int this_round = (round - 1) * 10;
							loop: for (int j = 0, k = 60 * DELAY_SPEED; j < k; j++) {
								Thread.sleep(1000 * DELAY_SPEED);
								boolean isRemainMob = false;
								for (final L1MonsterInstance mob : WorldMob.get()
										.getVisibleMob(party.getLeader())) {
									if (!mob.isDead()) {
										isRemainMob = true;
										break;
									}
								}
								if (isRemainMob) {
									continue loop;
								}
								sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f3$9560"));
								quest.add_score(Math.max(_random.nextInt(k) - j, 5));
								int mobId1, mobId2, mobId3;
								boolean checkOkayInFirstTime = false;
								if (squid_version) {
									mobId1 = quest.spawnQuestMob(this_round, 0, 0);
									mobId2 = mobId1;
									mobId3 = mobId1;
								} else if (_random.nextInt(100) < 30) {
									if (mimic_quest_order <= -1) {
										mimic_quest_order = 2 << _random.nextInt(3);
									}
									if (mimic_quest_order == 2) {
										mobId1 = quest.spawnQuestMob(this_round, 0, 0);
										mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
										mobId3 = mobId2;
									} else if (mimic_quest_order == 4) {
										mobId2 = quest.spawnQuestMob(this_round, 0, 0);
										mobId1 = quest.spawnQuestMob(this_round, mobId2, 0);
										mobId3 = mobId1;
									} else if (mimic_quest_order == 8) {
										mobId3 = quest.spawnQuestMob(this_round, 0, 0);
										mobId1 = quest.spawnQuestMob(this_round, mobId3, 0);
										mobId2 = mobId1;
									} else {
										mobId1 = quest.spawnQuestMob(this_round, 0, 0);
										mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
										mobId3 = quest.spawnQuestMob(this_round, mobId1, mobId2);
									}
								} else {
									mobId1 = quest.spawnQuestMob(this_round, 0, 0);
									mobId2 = quest.spawnQuestMob(this_round, mobId1, 0);
									mobId3 = quest.spawnQuestMob(this_round, mobId1, mobId2);
								}
								final L1Location new_loc_1 = new L1Location(32793, 32800, quest.get_mapid())
										.randomLocation(4, true);
								final L1Location new_loc_2 = new L1Location(32802, 32800, quest.get_mapid())
										.randomLocation(4, true);
								final L1Location new_loc_3 = new L1Location(32799, 32805, quest.get_mapid())
										.randomLocation(4, true);

								sendPacketsToAll(
										new S_EffectLocation(new_loc_1.getX(), new_loc_1.getY(), 7930));
								sendPacketsToAll(
										new S_EffectLocation(new_loc_2.getX(), new_loc_2.getY(), 7930));
								sendPacketsToAll(
										new S_EffectLocation(new_loc_3.getX(), new_loc_3.getY(), 7930));

								final L1NpcInstance mob1 = L1SpawnUtil.spawn(mobId1, new_loc_1, 0,
										quest.get_id());
								final L1NpcInstance mob2 = L1SpawnUtil.spawn(mobId2, new_loc_2, 0,
										quest.get_id());
								final L1NpcInstance mob3 = L1SpawnUtil.spawn(mobId3, new_loc_3, 0,
										quest.get_id());

								// 延遲 6500毫秒，或是 直到下次被通知
								// _timer.wait(6500);
								for (int m = 0, p = 12 * DELAY_SPEED; m < p; m++) {
									Thread.sleep(500 * DELAY_SPEED);
									if (ship_step == 2) {
										break loop;
									}
									if (!checkOkayInFirstTime) {
										if (mob1.isDead() || mob2.isDead() || mob3.isDead()) {
											checkOkayInFirstTime = true;
											if (((mimic_quest_order == 2) && mob1.isDead())
													|| ((mimic_quest_order == 4) && mob2.isDead())
													|| ((mimic_quest_order == 8) && mob3.isDead())) {
												if (++mimic_quest_count >= 4) {
													mimic_quest_count = 0;
													squid_version = true;
												}
											}
										}
									}
								}
							}
						} else if (ship_step == 2) {
							ship_step = -1;
							if (portal != null) {
								portal.deleteMe();
								portal = null;
								for (final L1PcInstance pc : quest.pcList()) {
									if (!pc.getLocation().isInScreen(loc)) {
										teleport(pc, -1);
									}
								}
							}
							break;
						}
					}
				}
				if (round <= 10) {
					quest.add_score(_random.nextInt(5) + 11);
					sendPacketsToAll(new S_PacketBoxGree(0x04));
					sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f3$9608"));
					Thread.sleep(3000);

					final L1PcInstance partyMember = party.partyUser();
					if (partyMember != null) {
						quest_pc = partyMember;
						final L1Location new_loc = CABIN_LOC_LIST[0];
						final L1Location new_loc2 = new L1Location(new_loc.getX() + 6, new_loc.getY() - 6,
								new_loc.getMapId());
						final L1NpcInstance hardin = L1SpawnUtil.spawn(97119, new_loc2, 4, quest.get_id());
						hardin.set_spawnTime(40);
						L1SpawnUtil.spawn(97120, new_loc2, 4, quest.get_id()).set_spawnTime(60);
						final L1NpcInstance mimic_1 = L1SpawnUtil.spawn(97121,
								new L1Location(new_loc.getX() - 3, new_loc.getY() - 4, new_loc.getMapId()), 0,
								quest.get_id());
						mimic_1.set_quest_id(2);
						mimic_1.set_spawnTime(40);
						final L1NpcInstance mimic_2 = L1SpawnUtil.spawn(97121,
								new L1Location(new_loc.getX() + 6, new_loc.getY() - 2, new_loc.getMapId()), 0,
								quest.get_id());
						mimic_2.set_quest_id(4);
						mimic_2.set_spawnTime(40);
						final L1NpcInstance mimic_3 = L1SpawnUtil.spawn(97121,
								new L1Location(new_loc.getX() - 1, new_loc.getY() + 5, new_loc.getMapId()), 0,
								quest.get_id());
						mimic_3.set_quest_id(8);
						mimic_3.set_spawnTime(40);
						teleport(partyMember, 0);
						Thread.sleep(2000);
						hardin.broadcastPacketX10(new S_NpcChat(hardin.getId(), "$12122"));
						Thread.sleep(3000);

						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9606"));

						hardin.broadcastPacketX10(new S_NpcChat(hardin.getId(), "$12123"));
						Thread.sleep(3000);

						hardin.broadcastPacketX10(new S_NpcChat(hardin.getId(), "$12124"));
					}

					if (quest_pc != null) {
						for (int i = 0; i < 15; i++) {
							Thread.sleep(1000 * DELAY_SPEED);
							if (quest_pc.getLocation().isInScreen(loc)) {
								quest.add_score(Math.max(_random.nextInt(35) - (i * 2), 0));
								break;
							}
							if (i == 7) {
								sendPacketsToAll(new S_PacketBoxGree(0x02, "$9545"));
							} else if (i == 14) {
								sendPacketsToAll(new S_PacketBoxGree(0x02, "$9546"));
								Thread.sleep(4000 * DELAY_SPEED);
								if (!quest_pc.getLocation().isInScreen(loc)) {
									teleport(quest_pc, -1);
								}
								Thread.sleep(4000 * DELAY_SPEED);
								break;
							}
						}
					}
					if (check_power <= 0) {
						check_power = quest.get_score() - score_before;
					}
				} else if (round >= 12) {
					for (int i = 0; i < 100; i++) {
						Thread.sleep(6000 * DELAY_SPEED);
						if (fire_point >= 10) {
							sendPacketsToAll(new S_PacketBoxGree(0x02, "$9587"));
							Thread.sleep(4000 * DELAY_SPEED);

							sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f3$9586"));
							break;
						} else {
							int mob_count = 0;
							for (final L1MonsterInstance mob : WorldMob.get()
									.getVisibleMob(party.getLeader())) {
								if (!mob.isDead()) {
									mob_count++;
								}
							}
							if (mob_count <= 0) {
								final int bonus_box_id = +Math.min(quest.get_score() / 100, 16);
								final L1NpcInstance mimic = L1SpawnUtil.spawn(97122,
										new L1Location(32796, 32803, quest.get_mapid()), 0, quest.get_id());
								if (bonus_box_id > 0) {
									mimic.set_quest_id(56235 + bonus_box_id);
									send_gift = true;
								}
								break;
							} else if (mob_count <= 4) {
								sendPacketsToAll(new S_PacketBoxGree(0x02, "$9585"));
							}
						}
					}
				}
			}

			if ((round >= 12) && (fire_point < 10)) {
				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9579"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9580"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9581"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9582"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9583"));
				Thread.sleep(4000 * DELAY_SPEED);

				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9584"));
				Thread.sleep(10000 * DELAY_SPEED);

				final int score = quest.get_score();
				final int rankId = BoardOrimReading.get().writeTopic(score, party.getLeader().getName(),
						party.getPartyMembers());
				final List<L1Rank> totalList = BoardOrimReading.get().getTotalList();
				int totalSize = 0;
				for (int i = 0, r = 5, n = totalList.size(); (i < r) && (i < n); i++) {
					final L1Rank rank = totalList.get(i);
					if (rank != null) {
						totalSize += rank.getMemberSize();
					}
				}
				sendPacketsToAll(new S_PacketBoxGree(totalList, totalSize, rankId, score));
			}

			Thread.sleep(15000 * DELAY_SPEED);

			clearAllObject();

		} catch (final Exception e) {
			_log.error("歐林海戰副本發生錯誤", e);
			GeneralThreadPool.get().cancel(_timer, false);
			startR();
		}
	}

	public final void calcScore() {
		if (old_score < quest.get_score()) {
			old_score = quest.get_score();
			for (final L1PcInstance pc : quest.pcList()) {
				pc.sendPackets(new S_PacketBoxGree(0x04, String.valueOf(old_score)));
			}
		}
		if (count_down > 0) {
			count_down--;
			if (count_down == 0) {
				boolean isRemainMob = false;
				for (final L1MonsterInstance mob : WorldMob.get().getVisibleMob(party.getLeader())) {
					if (!mob.isDead()) {
						isRemainMob = true;
						break;
					}
				}
				if (isRemainMob) {
					sendPacketsToAll(new S_PacketBoxGree(0x01));
					final L1Location new_loc = loc.randomLocation(8, true);
					L1SpawnUtil.spawn(_random.nextInt(5) + 97112, new_loc, 0, quest.get_id());
					if (++fire_point < 10) {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9561"));
						count_down = 10 * DELAY_SPEED;
					} else {
						sendPacketsToAll(new S_PacketBoxGree(0x02, "$9562"));
					}
				}
			}
		}
	}

	private final int randomHintSeaMonster() {
		if (squid_version) {
			squid_version = false;
			return 4;
		}
		final int chance = _random.nextInt(100);
		if (chance < 35) {
			return 1;
		} else if (chance < 65) {
			return 2;
		} else if (chance < 95) {
			return 3;
		} else {
			return 4;
		}
	}

	private final void spawnSeaMonster(final int npcId) {
		final L1NpcInstance monster_0 = L1SpawnUtil.spawn(npcId,
				new L1Location(32778, 32800, quest.get_mapid()), 0, quest.get_id());
		monster_0.set_quest_id(0);
		monster_0.WORK.work(monster_0);
		final L1NpcInstance monster_1 = L1SpawnUtil.spawn(npcId,
				new L1Location(32801, 32784, quest.get_mapid()).randomLocation(1, true), 0, quest.get_id());
		monster_1.set_quest_id(1);
		monster_1.WORK.work(monster_1);
		final L1NpcInstance monster_2 = L1SpawnUtil.spawn(npcId,
				new L1Location(32810, 32787, quest.get_mapid()).randomLocation(1, true), 0, quest.get_id());
		monster_2.set_quest_id(2);
		monster_2.WORK.work(monster_2);
		for (int i = 0, n = 3; i < n; i++) {
			final L1NpcInstance spawn_monster = L1SpawnUtil.spawn(npcId,
					new L1Location(32793, 32803, quest.get_mapid()).randomLocation(2, true), 0,
					quest.get_id());
			spawn_monster.set_quest_id(3 + i);
			spawn_monster.WORK.work(spawn_monster);
		}
	}

	private final void randomMagicCircle() {
		defense_okay = false;
		L1Location new_loc = loc.randomLocation(7, true);
		final L1NpcInstance npc = L1SpawnUtil.spawnT(97109, new_loc.getX(), new_loc.getY(),
				(short) new_loc.getMapId(), 0, 12);
		npc.set_showId(quest.get_id());
		quest.addNpc(npc);
		for (int i = 0, n = quest.size() - 2; i < n; i++) {
			new_loc = loc.randomLocation(7, true);
			final L1NpcInstance npc2 = L1SpawnUtil.spawnT(97110, new_loc.getX(), new_loc.getY(),
					(short) new_loc.getMapId(), 0, 12);
			npc2.set_showId(quest.get_id());
			quest.addNpc(npc2);
		}
	}

	public final void attack() {
		L1NpcInstance checkNpc = null;
		for (final L1NpcInstance npc : quest.npcList()) {
			if (npc.getGfxId() == 8322) {
				for (final L1PcInstance pc : quest.pcList()) {
					if (pc.getLocation().isSamePoint(npc.getLocation())) {
						checkNpc = npc;
						break;
					}
				}
				break;
			}
		}
		if (checkNpc != null) {
			if (fire_order) {
				npc1.broadcastPacketAll(new S_DoActionGFX(npc1.getId(), ActionCodes.ACTION_Damage));
				if (_random.nextInt(100) < 75) {
					sendPacketsToAll(new S_EffectLocation(32790, _random.nextInt(5) + 32816, 8233));
				}
			} else {
				npc2.broadcastPacketAll(new S_DoActionGFX(npc2.getId(), ActionCodes.ACTION_Damage));
				if (_random.nextInt(100) < 75) {
					sendPacketsToAll(new S_EffectLocation(32801, _random.nextInt(5) + 32816, 8233));
				}
			}
			fire_order = !fire_order;
			if (round <= 0) {
				return;
			}
			counter_alt++;
		} else {
			if (round <= 0) {
				return;
			}
			counter_alt2++;
		}

		if (counter_alt > (counter_alt2 + 5)) {
			if (!hit_okay) {
				critical_hit++;
			}
			hit_okay = true;
			counter_alt = 0;
			sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f3Critical HIT!"));
			quest.add_score(3);
		} else if (counter_alt > (counter_alt2 + 4)) {
			sendPacketsToAll(new S_PacketBoxGree(0x02, "Double HIT!"));
			quest.add_score(1);
		} else if (counter_alt > (counter_alt2 + 2)) {
			sendPacketsToAll(new S_PacketBoxGree(0x02, "\\f=HIT!"));
		}
	}

	public final void defense() {
		if (round <= 0) {
			return;
		}
		final List<L1NpcInstance> checkList = new ArrayList<L1NpcInstance>();
		for (final L1NpcInstance npc : quest.npcList()) {
			if (npc.getGfxId() == 8323) {
				checkList.add(npc);
			}
		}
		if (!checkList.isEmpty()) {
			int checkCount = 0;
			for (final L1NpcInstance npc : checkList) {
				for (final L1PcInstance pc : quest.pcList()) {
					if (pc.getLocation().isSamePoint(npc.getLocation())) {
						checkCount++;
					}
				}
			}
			if (checkCount >= (quest.size() - 2)) {
				for (final L1NpcInstance npc : checkList) {
					npc.deleteMe();
				}
				if (defense_okay) {
					return;
				}
				defense_okay = true;
				// 煙火特效
				// this.sendPacketsToAll(new S_PacketBoxGree(0x03));
				// 黃金盾牌特效編號
				for (final L1PcInstance pc : quest.pcList()) {
					pc.sendPackets(new S_SkillSound(pc.getId(), 10165));
				}
				return;
			}
		}
		counter_alt2++;
	}

	private final void defenseFailed() {
		if (!defense_okay) {
			defense_okay = true;
			sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
			sendPacketsToAll(new S_PacketBoxGree(0x02));
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			sendPacketsToAll(new S_EffectLocation(next_fire_loc.getX(), next_fire_loc.getY(), 762));
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			L1SpawnUtil.spawn(_random.nextInt(5) + 97112, next_fire_loc, 0, quest.get_id());
			next_fire_loc = loc.randomLocation(8, true);

			if (++fire_point < 10) {
				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9604"));
			} else {
				sendPacketsToAll(new S_PacketBoxGree(0x02, "$9562"));
			}
		}
	}

	public final int getCabinLocation() {
		if (ship == null) {
			return -1;
		}
		int type = 0;
		switch (ship.getGfxId()) {
		case 8164:
		case 8165:
		case 8166:
			type = 1;
			break;
		case 8260:
			type = 2;
			break;
		case 8263:
			type = 3;
			break;
		}
		return type;
	}

	public final void teleport(final L1PcInstance pc, final int type) {
		if ((pc.getId() == party.getLeader().getId()) || (pc.getMapId() != loc.getMapId())
				|| !quest.pcList().contains(pc)) {
			return;
		}
		if (type <= -1) {
			final L1Location new_loc = loc.randomLocation(5, true);
			L1Teleport.teleport(pc, new_loc, pc.getHeading(), true);
		} else if (type < CABIN_LOC_LIST.length) {
			if ((type == 0) || ((portal != null)
					&& (pc.getLocation().getTileLineDistance(portal.getLocation()) <= 1))) {
				final L1Location new_loc = CABIN_LOC_LIST[type].randomLocation(5, true);
				L1Teleport.teleport(pc, new_loc, pc.getHeading(), true);
			}
		}
	}

	private final void spawnEnemyShip() {
		L1NpcInstance spawn_ship = null;
		final int locx = fire_order ? 32796 : 32802;
		final int locy = 32824;
		if (critical_hit >= 4) {
			if (round == 4) {
				spawn_ship = L1SpawnUtil.spawn(97107, new L1Location(locx, locy, quest.get_mapid()), 0,
						quest.get_id());
			} else if (round == 8) {
				spawn_ship = L1SpawnUtil.spawn(97106, new L1Location(locx, locy, quest.get_mapid()), 0,
						quest.get_id());
			}
		}
		if (spawn_ship == null) {
			spawn_ship = L1SpawnUtil.spawn(_random.nextInt(3) + 97099,
					new L1Location(locx, locy, quest.get_mapid()), 0, quest.get_id());
		}
		ship = spawn_ship;
		ship.WORK.work(ship);
	}

	public final void checkQuestOrder(final L1PcInstance pc, final int quest_id) {
		mimic_quest_order -= quest_id;
		if (mimic_quest_order <= 0) {
			if ((mimic_quest_order == 0) && (++mimic_quest_count >= 4)) {
				mimic_quest_count = 0;
				squid_version = true;
			}
			quest.get_orimR().teleport(pc, -1);
		}
	}

	public final void shipReturnStep(final int step) {
		ship_step = step;
	}

	private final void sendPacketsToAll(final ServerBasePacket packet) {
		if (quest.size() > 0) {
			for (final L1PcInstance pc : quest.pcList()) {
				pc.sendPackets(packet);
			}
		}
	}

	private final void clearAllObject() {
		for (final L1Object obj : World.get().getVisibleObjects(quest.get_mapid()).values()) {
			if (obj.get_showId() != quest.get_id()) {
				continue;
			}
			if (obj instanceof L1EffectInstance) {
				final L1EffectInstance mob = (L1EffectInstance) obj;
				mob.deleteMe();
			} else if (obj instanceof L1MerchantInstance) {
				final L1MerchantInstance mob = (L1MerchantInstance) obj;
				mob.setreSpawn(false);
				mob.deleteMe();
			} else if (obj instanceof L1MonsterInstance) {
				final L1MonsterInstance mob = (L1MonsterInstance) obj;
				if (!mob.isDead()) {
					mob.setDead(true);
					mob.setStatus(ActionCodes.ACTION_Die);
					mob.setCurrentHpDirect(0);
					mob.deleteMe();
				}
			} else if (obj instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) obj;
				// 傳送回 說話之島
				final L1Location loc = new L1Location(32580, 32931, 0).randomLocation(5, true);
				L1Teleport.teleport(pc, loc, pc.getHeading(), true);
			} else if (obj instanceof L1Inventory) {
				final L1Inventory inventory = (L1Inventory) obj;
				inventory.clearItems();
			}
		}
	}
}
