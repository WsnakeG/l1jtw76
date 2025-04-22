package com.lineage.server.utils;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.MapsTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.RandomMobTable;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.lock.FurnitureSpawnReading;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.RandomMobDeleteTimer;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1DoorInstance;
import com.lineage.server.model.Instance.L1EffectInstance;
import com.lineage.server.model.Instance.L1FieldObjectInstance;
import com.lineage.server.model.Instance.L1FurnitureInstance;
import com.lineage.server.model.Instance.L1IllusoryInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.map.L1Map;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.DeName;
import com.lineage.server.templates.L1Furniture;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldEffect;
import com.lineage.server.world.WorldQuest;

/**
 * 召喚控制項
 * 
 * @author dexc
 */
public class L1SpawnUtil {

	private static final Log _log = LogFactory.getLog(L1SpawnUtil.class);

	// 正向
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	/**
	 * 依PC位置召喚指定NPC
	 * 
	 * @param pc 召喚者
	 * @param npcId NPC編號
	 * @param r 召喚距離(不為0 NPC召喚與PC將有距離 否則重疊)
	 * @param t 存在時間(秒) 小於等於0不限制
	 */
	public static void spawn(final L1PcInstance pc, final int npcId, final int r, final int t) {
		final L1SpawnUtil util = new L1SpawnUtil();
		util.spawnR(pc.getLocation(), npcId, pc.get_showId(), r, t);
	}

	/**
	 * 依LOC位置召喚指定NPC傳回NPC訊息)
	 * 
	 * @param loc LOC
	 * @param npcId NPC編號
	 * @param showid 副本編號
	 * @param r 召喚距離(不為0 NPC召喚與PC將有距離 否則重疊)
	 * @param time 存在時間(秒) 小於等於0不限制
	 */
	public static L1NpcInstance spawnRx(final L1Location loc, final int npcId, final int showid, final int r,
			final int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}

			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());

			if (r == 0) {
				npc.getLocation().set(loc);
				// npc.getLocation().forward(_pc.getHeading());

			} else {
				int tryCount = 0;
				do {
					tryCount++;
					npc.setX((loc.getX() + (int) (Math.random() * r)) - (int) (Math.random() * r));
					npc.setY((loc.getY() + (int) (Math.random() * r)) - (int) (Math.random() * r));
					if (npc.getMap().isInMap(npc.getLocation())
							&& npc.getMap().isPassable(npc.getLocation(), npc)) {
						break;
					}
					Thread.sleep(2);
				} while (tryCount < 50);

				if (tryCount >= 50) {
					npc.getLocation().set(loc);
					// npc.getLocation().forward(_pc.getHeading());
				}
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(5);

			npc.set_showId(showid);

			final L1QuestUser q = WorldQuest.get().get(showid);
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();

			// 設置NPC現身
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < time) {
				npc.set_spawnTime(time);
			}
			return npc;

		} catch (final Exception e) {
			_log.error("依LOC位置召喚指定NPC(傳回NPC訊息)發生異常: " + npcId, e);
		}
		return null;
	}

	/**
	 * 依LOC位置召喚指定NPC(未傳回NPC訊息)
	 * 
	 * @param loc LOC
	 * @param npcId NPC編號
	 * @param showid 副本編號
	 * @param randomRange 範圍
	 */
	public static void spawnR(final L1Location loc, final int npcId, final int showid,
			final int randomRange) {
		final L1SpawnUtil util = new L1SpawnUtil();
		util.spawnR(loc, npcId, showid, randomRange, 0);
	}

	private void spawnR(final L1Location loc, final int npcId, final int showid, final int randomRange,
			final int timeMillisToDelete) {
		final SpawnR1 spawn = new SpawnR1(loc, npcId, showid, randomRange, timeMillisToDelete);
		GeneralThreadPool.get().schedule(spawn, 0);
	}

	private class SpawnR1 implements Runnable {
		// final L1PcInstance _pc;
		final L1Location _location;
		final int _npcId;
		final int _showid;
		final int _randomRange;
		final int _timeMillisToDelete;

		private SpawnR1(final L1Location location, final int npcId, final int showid, final int randomRange,
				final int timeMillisToDelete) {
			_location = location;
			_npcId = npcId;
			_showid = showid;
			_randomRange = randomRange;
			_timeMillisToDelete = timeMillisToDelete;
		}

		@Override
		public void run() {
			try {
				final L1NpcInstance npc = NpcTable.get().newNpcInstance(_npcId);
				if (npc == null) {
					return;
				}

				// 修復假人問題 by terry0412
				if (npc instanceof L1DeInstance) {
					((L1DeInstance) npc).startNpc(null);
				}

				npc.setId(IdFactoryNpc.get().nextId());
				npc.setMap(_location.getMap());

				if (_randomRange == 0) {
					npc.getLocation().set(_location);
					// npc.getLocation().forward(_pc.getHeading());

				} else {
					int tryCount = 0;
					do {
						tryCount++;
						npc.setX((_location.getX() + (int) (Math.random() * _randomRange))
								- (int) (Math.random() * _randomRange));
						npc.setY((_location.getY() + (int) (Math.random() * _randomRange))
								- (int) (Math.random() * _randomRange));
						if (npc.getMap().isInMap(npc.getLocation())
								&& npc.getMap().isPassable(npc.getLocation(), npc)) {
							break;
						}
						Thread.sleep(2);
					} while (tryCount < 50);

					if (tryCount >= 50) {
						npc.getLocation().set(_location);
						// npc.getLocation().forward(_pc.getHeading());
					}
				}

				npc.setHomeX(npc.getX());
				npc.setHomeY(npc.getY());
				npc.setHeading(5);

				// 設置副本編號 TODO
				npc.set_showId(_showid);

				final L1QuestUser q = WorldQuest.get().get(_showid);
				if (q != null) {
					q.addNpc(npc);
				}

				// added by terry0412
				if (npc instanceof L1MonsterInstance) {
					// 召喚後隱藏
					((L1MonsterInstance) npc).initHide();
				}

				World.get().storeObject(npc);
				World.get().addVisibleObject(npc);

				npc.turnOnOffLight();

				// 設置NPC現身
				npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				if (0 < _timeMillisToDelete) {
					// 存在時間(秒)
					npc.set_spawnTime(_timeMillisToDelete);
				}

			} catch (final Exception e) {
				_log.error("執行NPC召喚發生異常: " + _npcId, e);
			}
		}
	}

	/**
	 * 召喚已有家具資料
	 * 
	 * @param temp
	 */
	public static void spawn(final L1Furniture temp) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(temp.get_npcid());

			if (npc == null) {
				return;
			}

			final L1FurnitureInstance furniture = (L1FurnitureInstance) npc;

			furniture.setId(IdFactoryNpc.get().nextId());
			furniture.setMap(temp.get_mapid());

			furniture.setX(temp.get_locx());
			furniture.setY(temp.get_locy());

			furniture.setHomeX(furniture.getX());
			furniture.setHomeY(furniture.getY());
			furniture.setHeading(0);
			furniture.setItemObjId(temp.get_item_obj_id());

			World.get().storeObject(furniture);
			World.get().addVisibleObject(furniture);

		} catch (final Exception e) {
			_log.error("執行家具召喚發生異常!", e);
		}
	}

	/**
	 * 執行家具召喚
	 * 
	 * @param pc
	 * @return
	 */
	public static L1FurnitureInstance spawn(final L1PcInstance pc, final int npcid, final int itemObjectId) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			final L1FurnitureInstance furniture = (L1FurnitureInstance) npc;

			furniture.setId(IdFactoryNpc.get().nextId());
			furniture.setMap(pc.getMapId());

			if (pc.getHeading() == 0) {
				furniture.setX(pc.getX());
				furniture.setY(pc.getY() - 1);

			} else if (pc.getHeading() == 2) {
				furniture.setX(pc.getX() + 1);
				furniture.setY(pc.getY());
			}

			furniture.setHomeX(furniture.getX());
			furniture.setHomeY(furniture.getY());
			furniture.setHeading(0);
			furniture.setItemObjId(itemObjectId);

			World.get().storeObject(furniture);
			World.get().addVisibleObject(furniture);
			FurnitureSpawnReading.get().insertFurniture(furniture);
			return furniture;

		} catch (final Exception e) {
			_log.error("執行家具召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚指定編號NPC
	 * 
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final int x, final int y, final short m, final int h) {
		return spawnT(npcid, x, y, m, h, 0);
	}

	/**
	 * 召喚指定編號NPC
	 * 
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @param time 存在時間(秒)
	 * @return
	 */
	public static L1NpcInstance spawnT(final int npcid, final int x, final int y, final short m, final int h,
			final int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(m);

			npc.setX(x);
			npc.setY(y);

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(h);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();

			// 設置NPC現身
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < time) {
				// 存在時間(秒)
				npc.set_spawnTime(time);
			}
			return npc;

		} catch (final Exception e) {
			_log.error("執行NPC召喚發生異常: " + npcid, e);
		}
		return null;
	}

	/**
	 * 召喚分身
	 * 
	 * @param pc
	 * @param loc
	 * @param h
	 * @return
	 */
	public static L1IllusoryInstance spawn(final L1PcInstance pc, final L1Location loc, final int h,
			final int time) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(81003);

			if (npc == null) {
				return null;
			}

			final L1IllusoryInstance ill = (L1IllusoryInstance) npc;
			ill.setId(IdFactoryNpc.get().nextId());
			ill.setMap(loc.getMap());

			ill.setX(loc.getX());
			ill.setY(loc.getY());

			ill.setHomeX(ill.getX());
			ill.setHomeY(ill.getY());
			ill.setHeading(h);

			ill.setNameId("分身");
			ill.setTitle(pc.getName() + " 的");// 567：的
			ill.setMaster(pc);

			// 設置副本編號 TODO
			ill.set_showId(pc.get_showId());

			final L1QuestUser q = WorldQuest.get().get(pc.get_showId());
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(ill);
			World.get().addVisibleObject(ill);

			if (time > 0) {
				// 存在時間(秒)
				ill.set_spawnTime(time);
			}

			if (pc.getWeapon() != null) {
				ill.setStatus(pc.getWeapon().getItem().getType1());
				if (pc.getWeapon().getItem().getRange() != -1) {
					ill.set_ranged(2);

				} else {
					ill.set_ranged(10);
					ill.setBowActId(66);
				}
			}
			ill.setLevel((int) (pc.getLevel() * 0.7));

			ill.setStr((int) (pc.getStr() * 0.7));
			ill.setCon((int) (pc.getCon() * 0.7));
			ill.setDex((int) (pc.getDex() * 0.7));
			ill.setInt((int) (pc.getInt() * 0.7));
			ill.setWis((int) (pc.getWis() * 0.7));

			ill.setMaxMp(10);
			ill.setCurrentMpDirect(10);

			ill.setTempCharGfx(pc.getTempCharGfx());
			ill.setGfxId(pc.getGfxId());

			final int attack = SprTable.get().getAttackSpeed(pc.getGfxId(), 1);
			final int move = SprTable.get().getMoveSpeed(pc.getGfxId(), ill.getStatus());

			ill.setPassispeed(move);
			ill.setAtkspeed(attack);

			ill.setBraveSpeed(pc.getBraveSpeed());
			ill.setMoveSpeed(pc.getMoveSpeed());

			return ill;

		} catch (final Exception e) {
			_log.error("執行分身召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚救援
	 * 
	 * @param npcid
	 * @param loc
	 * @param show_id 副本編號
	 * @return
	 */
	public static L1MonsterInstance spawnX(final int npcid, final L1Location loc, final int show_id) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			final L1MonsterInstance mob = (L1MonsterInstance) npc;
			mob.setId(IdFactoryNpc.get().nextId());
			mob.setMap(loc.getMap());

			mob.setX(loc.getX());
			mob.setY(loc.getY());

			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());

			// 設置副本編號 TODO
			mob.set_showId(show_id);

			final L1QuestUser q = WorldQuest.get().get(show_id);
			if (q != null) {
				q.addNpc(npc);
			}

			mob.setMovementDistance(20);

			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);

			return mob;

		} catch (final Exception e) {
			_log.error("執行召喚救援發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚指定對員
	 * 
	 * @param master
	 * @param npcid
	 * @param loc
	 * @return
	 */
	public static L1MonsterInstance spawnParty(final L1NpcInstance master, final int npcid,
			final L1Location loc) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			final L1MonsterInstance mob = (L1MonsterInstance) npc;
			mob.setId(IdFactoryNpc.get().nextId());
			mob.setMap(loc.getMap());

			mob.setX(loc.getX());
			mob.setY(loc.getY());

			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());

			mob.setHeading(master.getHeading());

			mob.setMaster(master);

			// 設置副本編號 TODO
			mob.set_showId(master.get_showId());

			final L1QuestUser q = WorldQuest.get().get(master.get_showId());
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);

			return mob;

		} catch (final Exception e) {
			_log.error("執行召喚指定對員發生異常!", e);
		}
		return null;
	}

	/**
	 * 依召L1Location喚指定NPC
	 * 
	 * @param npcid
	 * @param loc
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final L1Location loc) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());

			npc.setX(loc.getX());
			npc.setY(loc.getY());

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();

			return npc;

		} catch (final Exception e) {
			_log.error("執行分身召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 依召L1Location喚指定NPC<BR>
	 * 附加 副本編號
	 * 
	 * @param npcid
	 * @param loc
	 * @param heading
	 * @param showId
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final L1Location loc, final int heading,
			final int showId) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(loc.getMap());

			npc.setX(loc.getX());
			npc.setY(loc.getY());

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());

			npc.setHeading(heading);

			// 設置副本編號 TODO
			npc.set_showId(showId);

			final L1QuestUser q = WorldQuest.get().get(showId);
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();

			return npc;

		} catch (final Exception e) {
			_log.error("執行副本NPC召喚發生異常!", e);
		}
		return null;
	}

	/**
	 * 召喚賭場參賽者
	 * 
	 * @param npcid
	 * @param x
	 * @param y
	 * @param m
	 * @param h
	 * @param gfxid
	 * @return
	 */
	public static L1NpcInstance spawn(final int npcid, final int x, final int y, final short m, final int h,
			final int gfxid) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcid);

			if (npc == null) {
				return null;
			}

			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(m);

			npc.setX(x);
			npc.setY(y);

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(h);

			npc.setTempCharGfx(gfxid);
			npc.setGfxId(gfxid);

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			return npc;

		} catch (final Exception e) {
			_log.error("執行NPC召喚發生異常: " + npcid, e);
		}
		return null;
	}

	/**
	 * 法師技能(火牢)
	 * 
	 * @param cha 主人
	 * @param targetX 目標X位置
	 * @param targetY 目標Y位置
	 */
	public static void doSpawnFireWall(final L1Character cha, final int targetX, final int targetY) {
		final L1SpawnUtil util = new L1SpawnUtil();
		util.doSpawnFireWallR(cha, targetX, targetY);
	}

	public void doSpawnFireWallR(final L1Character cha, final int targetX, final int targetY) {
		final SpawnR2 spawn = new SpawnR2(cha, targetX, targetY);
		GeneralThreadPool.get().schedule(spawn, 0);
	}

	private class SpawnR2 implements Runnable {

		private final L1Character _cha;
		private final int _targetX;
		private final int _targetY;

		private SpawnR2(final L1Character cha, final int targetX, final int targetY) {
			_cha = cha;
			_targetX = targetX;
			_targetY = targetY;
		}

		@Override
		public void run() {
			try {
				if (_cha == null) {
					_log.error("火牢施放者出現空值錯誤");
					return;
				}
				final int npcid = 81157; // 法師技能(火牢)
				final L1Npc firewall = NpcTable.get().getTemplate(npcid);
				if (firewall == null) {
					return;
				}
				final int duration = SkillsTable.get().getTemplate(L1SkillId.FIRE_WALL).getBuffDuration();

				// 判斷位置用物件
				L1Character base = _cha;
				for (int i = 0, n = 8; i < n; i++) {
					Thread.sleep(2);
					// System.out.println("法師技能(火牢)i:"+i);
					int tmp = 0;

					for (final L1Object objects : World.get().getVisibleObjects(_cha)) {
						if (objects == null) { // 對象為空
							continue;
						}
						// 同地點相同主人 tmp + 1
						if (objects instanceof L1EffectInstance) {
							final L1EffectInstance effect = (L1EffectInstance) objects;
							if (_cha.equals(effect.getMaster())) {
								tmp++;
							}
						}
						// System.out.println("同地點相同物件 tmp + 1:"+tmp);
					}
					if (tmp >= 24) { // 畫面內 同使用者 最多召喚24個火牢物件
						return;
					}

					final int a = base.targetDirection(_targetX, _targetY);

					int x = base.getX() + HEADING_TABLE_X[a];
					int y = base.getY() + HEADING_TABLE_Y[a];

					// System.out.println("XY位置增加:"+x+"/"+y);
					if (!base.isAttackPosition(x, y, 1)) {
						x = base.getX();
						y = base.getY();
					}

					// 判斷座標上 是否已有相同NPCID物件
					final L1Location loc = new L1Location(x, y, _cha.getMapId());
					if (WorldEffect.get().isEffect(loc, npcid)) {
						continue;
					}

					// 安全區域或是攻擊無法穿透的區域則跳開 by terry0412
					final L1Map map = _cha.getMap();
					if (map.isSafetyZone(x, y) || !map.isArrowPassable(x, y, _cha.getHeading())) {
						break;
					}

					// 施展者 是PC
					final L1EffectInstance temp;
					if (_cha instanceof L1PcInstance) {
						final L1PcInstance user = (L1PcInstance) _cha;
						temp = spawnEffect(npcid, duration, x, y, user.getMapId(), user, L1SkillId.FIRE_WALL);

					} else {
						temp = spawnEffect(npcid, duration, x, y, _cha.getMapId(), null, L1SkillId.FIRE_WALL);
					}
					// 原點跳開 by terry0412
					if ((_targetX == x) && (_targetY == y)) {
						break;
					}
					base = temp;
				}

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * 技能效果動畫召喚
	 * 
	 * @param npcId 對應的NPC編號
	 * @param time 存在時間(秒)
	 * @param locX 設置的座標X
	 * @param locY 設置的座標Y
	 * @param mapId 設置的地圖編號
	 * @param user 施展的PC
	 * @param skiiId 技能編號
	 * @return
	 */
	public static L1EffectInstance spawnEffect(final int npcId, final int time, final int locX,
			final int locY, final short mapId, final L1Character user, final int skiiId) {
		L1EffectInstance effect = null;

		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}

			effect = (L1EffectInstance) npc;

			effect.setId(IdFactoryNpc.get().nextId());
			effect.setGfxId(effect.getGfxId());

			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);

			if (user != null) {
				effect.setMaster(user);// 施展者
				// 設置副本編號 TODO
				effect.set_showId(user.get_showId());

				final L1QuestUser q = WorldQuest.get().get(user.get_showId());
				if (q != null) {
					q.addNpc(npc);
				}
			}

			effect.setSkillId(skiiId);// 引用技能編號

			World.get().storeObject(effect);
			World.get().addVisibleObject(effect);

			effect.broadcastPacketAll(new S_NPCPack(effect));
			// 畫面認識
			for (final L1PcInstance pc : World.get().getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
			}

			if (time > 0) {
				// 存在時間(秒)
				effect.set_spawnTime(time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return effect;
	}

	/**
	 * 技能效果動畫召喚
	 * 
	 * @param npcId 對應的NPC編號
	 * @param time 存在時間(秒)
	 * @param locX 設置的座標X
	 * @param locY 設置的座標Y
	 * @param mapId 設置的地圖編號
	 * @param user 施展的PC
	 * @param skiiId 技能編號
	 * @param gfxid 外型編號
	 * @return
	 */
	public static L1EffectInstance spawnEffect(final int npcId, final int time, final int locX,
			final int locY, final short mapId, final L1Character user, final int skiiId, final int gfxid) {
		L1EffectInstance effect = null;

		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(npcId);

			if (npc == null) {
				return null;
			}

			effect = (L1EffectInstance) npc;

			effect.setId(IdFactoryNpc.get().nextId());
			effect.setGfxId(gfxid);
			effect.setTempCharGfx(gfxid);

			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);

			if (user != null) {
				effect.setMaster(user);// 施展者
				// 設置副本編號 TODO
				effect.set_showId(user.get_showId());

				final L1QuestUser q = WorldQuest.get().get(user.get_showId());
				if (q != null) {
					q.addNpc(npc);
				}
			}

			effect.setSkillId(skiiId);// 引用技能編號

			World.get().storeObject(effect);
			World.get().addVisibleObject(effect);

			effect.broadcastPacketAll(new S_NPCPack(effect));
			// 畫面認識
			for (final L1PcInstance pc : World.get().getRecognizePlayer(effect)) {
				effect.addKnownObject(pc);
				pc.addKnownObject(effect);
			}

			if (time > 0) {
				// 存在時間(秒)
				effect.set_spawnTime(time);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return effect;
	}

	/**
	 * 召喚門(副本專用)<BR>
	 * 門編號 51~55 由賭場使用<BR>
	 * 門編號 10000~10003 由任務不死族的叛徒 (法師30級以上官方任務)使用<BR>
	 * 門編號 10004 由任務拯救被幽禁的吉姆 (騎士30級以上官方任務)使用<BR>
	 * 門編號 10005~10007 由安塔瑞斯棲息地使用<BR>
	 * 門編號 10008~10010 由法利昂棲息地使用<BR>
	 * 門編號 10011~10013 由法利昂棲息地使用<BR>
	 * 門編號 10014~10036 哈汀副本<BR>
	 * 
	 * @param quest 任務
	 * @param doorId 門編號
	 * @param gfxid 外型
	 * @param x X
	 * @param y Y
	 * @param mapid 地圖編號
	 * @param direction
	 * @return
	 */
	public static L1DoorInstance spawnDoor(final L1QuestUser quest, final int doorId, final int gfxid,
			final int x, final int y, final short mapid, final int direction) {
		final L1Npc l1npc = NpcTable.get().getTemplate(81158);
		if (l1npc != null) {
			final L1DoorInstance door = (L1DoorInstance) NpcTable.get().newNpcInstance(l1npc);

			final int objid = IdFactoryNpc.get().nextId();
			door.setId(objid);

			door.setDoorId(doorId);
			door.setGfxId(gfxid);
			door.setX(x);
			door.setY(y);
			door.setMap(mapid);
			door.setHomeX(x);
			door.setHomeY(y);
			door.setDirection(direction);
			switch (gfxid) {
			case 89:// \
				door.setLeftEdgeLocation(y);
				door.setRightEdgeLocation(y);
				break;

			case 88:// /
				if (mapid == 9000) {
					door.setLeftEdgeLocation(x - 1);
					door.setRightEdgeLocation(x + 1);
				} else {
					door.setLeftEdgeLocation(x - 1);
					door.setRightEdgeLocation(x + 1);
				}
				break;

			case 90:// 地面/
				door.setLeftEdgeLocation(x);
				door.setRightEdgeLocation(x + 1);
				break;

			case 7556:// 安塔瑞斯洞穴 \
				door.setLeftEdgeLocation(y - 1);
				door.setRightEdgeLocation(y + 3);
				break;

			case 7858:// 法利昂洞穴 /
			case 8011: // 林德拜爾洞穴 /
				door.setLeftEdgeLocation(x - 2);
				door.setRightEdgeLocation(x + 3);
				break;

			case 7859:// 法利昂洞穴 \
			case 8015: // 林德拜爾洞穴 \
			case 8019: // 林德拜爾洞穴 \
				door.setLeftEdgeLocation(y - 2);
				door.setRightEdgeLocation(y + 3);
				break;

			default:
				door.setLeftEdgeLocation(y);
				door.setRightEdgeLocation(y);
				break;
			}

			door.setMaxHp(0);
			door.setCurrentHp(0);
			door.setKeeperId(0);
			if (quest != null) {
				// 副本編號
				door.set_showId(quest.get_id());
				quest.addNpc(door);
			}
			door.close();

			World.get().storeObject(door);
			World.get().addVisibleObject(door);

			return door;
		}
		return null;
	}

	public static L1DeInstance spawn(final L1PcInstance pc, final DeName de, final int timeMillisToDelete) {
		try {
			final L1Npc l1npc = NpcTable.get().getTemplate(81002);
			if (l1npc == null) {
				pc.sendPackets(new S_SystemMessage("找不到該npc。"));
				return null;
			}
			final L1DeInstance npc = new L1DeInstance(l1npc);
			npc.startNpc(de); // 修復假人問題 by terry0412

			final int randomRange = 5;
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(pc.getMapId());
			// npc.set_dename(de);

			int tryCount = 0;
			do {
				tryCount++;
				npc.setX((pc.getX() + (int) (Math.random() * randomRange))
						- (int) (Math.random() * randomRange));
				npc.setY((pc.getY() + (int) (Math.random() * randomRange))
						- (int) (Math.random() * randomRange));
				if (npc.getMap().isInMap(npc.getLocation())
						&& npc.getMap().isPassable(npc.getLocation(), npc)) {
					break;
				}
				Thread.sleep(2);
			} while (tryCount < 50);

			if (tryCount >= 50) {
				npc.getLocation().set(pc.getLocation());
				npc.getLocation().forward(pc.getHeading());
			}

			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(pc.getHeading());

			// 設置副本編號 TODO
			npc.set_showId(pc.get_showId());

			final L1QuestUser q = WorldQuest.get().get(pc.get_showId());
			if (q != null) {
				q.addNpc(npc);
			}

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();

			// 設置NPC現身
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < timeMillisToDelete) {
				// 存在時間(秒)
				npc.set_spawnTime(timeMillisToDelete);
			}
			return npc;

		} catch (final Exception e) {
			_log.error("執行DE召喚發生異常", e);
		}
		return null;
	}

	/**
	 * 召喚景觀(副本專用)
	 * 
	 * @param showid 副本編號
	 * @param gfxid 圖型編號
	 * @param x X
	 * @param y Y
	 * @param map MAP
	 * @param timeMillisToDelete
	 * @return
	 */
	public static L1FieldObjectInstance spawn(final int showid, final int gfxid, final int x, final int y,
			final int map, final int timeMillisToDelete) {
		try {
			final L1FieldObjectInstance field = (L1FieldObjectInstance) NpcTable.get().newNpcInstance(71081);

			if (field != null) {
				field.setId(IdFactoryNpc.get().nextId());
				field.setGfxId(gfxid);
				field.setTempCharGfx(gfxid);
				field.setMap((short) map);
				field.setX(x);
				field.setY(y);
				field.setHomeX(x);
				field.setHomeY(y);
				field.setHeading(5);
				field.set_showId(showid);

				final L1QuestUser q = WorldQuest.get().get(showid);
				if (q != null) {
					q.addNpc(field);
				}

				World.get().storeObject(field);
				World.get().addVisibleObject(field);

				if (0 < timeMillisToDelete) {
					// 存在時間(秒)
					field.set_spawnTime(timeMillisToDelete);
				}
				return field;
			}

		} catch (final Exception e) {
			_log.error("執行景觀(副本專用)召喚發生異常", e);
		}
		return null;
	}
	
	//新增怪物隨機地圖產生  by eric1300460
	public static void spawn(int randomMobId) {
		try {
			RandomMobTable mobTable = RandomMobTable.getInstance();
			int mobCont=mobTable.getCont(randomMobId);
			int mobId = mobTable.getMobId(randomMobId);
			L1NpcInstance [] npc =new L1NpcInstance[mobCont];
			for(int i=0;i<mobCont;i++){
				short mapId=mobTable.getRandomMapId(randomMobId);
				int x=mobTable.getRandomMapX(mapId);
				int y=mobTable.getRandomMapY(mapId);
				npc[i] = NpcTable.get().newNpcInstance(mobId);
				//npc[i].setId(IdFactory.getInstance().nextId());
				npc[i].setId(IdFactoryNpc.get().nextId());
				npc[i].setMap(mapId);
				
				int tryCount = 0;
				do {
					tryCount++;
					//Random rand= new Random();
					npc[i].setX(x + RandomArrayList.getInt(200) - RandomArrayList.getInt(200));
					npc[i].setY(y + RandomArrayList.getInt(200) - RandomArrayList.getInt(200));
					
					L1Map map = npc[i].getMap();
					if (map.isInMap(npc[i].getLocation())
							&& map.isPassable(npc[i].getLocation(), npc[i])) {
						break;
					}
					
					Thread.sleep(500);
				} while (tryCount < 50);
				if (tryCount >= 50) {
					boolean find=false;
					L1PcInstance findPc;
					for (Object obj : World.get().getVisibleObjects(mapId)
							.values()) {
						if (obj instanceof L1PcInstance)
						{
							findPc=(L1PcInstance)obj;
							npc[i].getLocation().set(findPc.getLocation());
							npc[i].getLocation().forward(findPc.getHeading());
							find=true;
							break;
						}
					}
					if(!find){//隨機怪物創造失敗
						continue;
					}
				}
				
				
				npc[i].setHomeX(npc[i].getX());
				npc[i].setHomeY(npc[i].getY());
				npc[i].setHeading(0);
				World.get().storeObject(npc[i]);
				World.get().addVisibleObject(npc[i]);
				//全白天用不到npc[i].turnOnOffLight();
				npc[i].startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
				// 隨機生怪是否公告
				if (mobTable.isBroad(randomMobId)) {
					World.get().broadcastServerMessage("【"+npc[i].getName()+"】出現在" + MapsTable.get().getLoaction(mapId));
				}
				
				// 告知GM
				final Collection<L1PcInstance> allPc = World.get().getAllPlayers();
				for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
					final L1PcInstance tgpc = iter.next();
					if (tgpc.isGm()) {
						tgpc.sendPackets(new S_SystemMessage("隨機生怪 NPCID: " + mobTable.getMobId(randomMobId) + " NPCNAME :" + npc[i].getName() + " 位置: " + MapsTable.get().getLoaction(mapId)));
					}
				}
				
				//_log.info("隨機生怪 NPCID: " + mobTable.getMobId(randomMobId) + " NPCNAME :" + npc[i].getName() + " 位置: " + MapsTable.get().getLoaction(mapId));
			}
			final int timeSecondToDelete = mobTable.getTimeSecondToDelete(randomMobId);
			
			if (timeSecondToDelete > 0) {
				final RandomMobDeleteTimer timer = new RandomMobDeleteTimer(randomMobId,npc);
				timer.begin();
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
