package com.lineage.server.model;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.MobGroupTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.templates.L1MobGroup;
import com.lineage.server.templates.L1NpcCount;
import com.lineage.server.world.World;

/**
 * 召喚NPC隊員
 * 
 * @author daien
 */
public class L1MobGroupSpawn {

	private static final Log _log = LogFactory.getLog(L1MobGroupSpawn.class);

	private static L1MobGroupSpawn _instance;

	private static Random _random = new Random();

	private boolean _isRespawnScreen;// 召喚位置不可與隊長重疊

	private boolean _isInitSpawn;// AI啟用

	/**
	 * 召喚NPC隊員
	 */
	private L1MobGroupSpawn() {
	}

	public static L1MobGroupSpawn getInstance() {
		if (_instance == null) {
			_instance = new L1MobGroupSpawn();
		}
		return _instance;
	}

	/**
	 * 召喚NPC隊員
	 * 
	 * @param leader 隊長
	 * @param groupId 隊伍編號
	 * @param isRespawnScreen 召喚位置重疊隊長 true:不重疊 false:重疊
	 * @param isInitSpawn AI啟用
	 */
	public void doSpawn(final L1NpcInstance leader, final int groupId, final boolean isRespawnScreen,
			final boolean isInitSpawn) {

		final L1MobGroup mobGroup = MobGroupTable.get().getTemplate(groupId);
		if (mobGroup == null) {
			return;
		}

		L1NpcInstance mob;
		_isRespawnScreen = isRespawnScreen;
		_isInitSpawn = isInitSpawn;

		final L1MobGroupInfo mobGroupInfo = new L1MobGroupInfo();

		mobGroupInfo.setRemoveGroup(mobGroup.isRemoveGroupIfLeaderDie());
		mobGroupInfo.addMember(leader);// 加入隊長

		for (final L1NpcCount minion : mobGroup.getMinions()) {
			if (minion.isZero()) {
				continue;
			}
			for (int i = 0; i < minion.getCount(); i++) {
				mob = spawn(leader, minion.getId());
				if (mob != null) {
					mobGroupInfo.addMember(mob);
				}
			}
		}
	}

	private L1NpcInstance spawn(final L1NpcInstance leader, final int npcId) {
		L1NpcInstance mob = null;
		try {
			mob = NpcTable.get().newNpcInstance(npcId);

			mob.setId(IdFactoryNpc.get().nextId());

			mob.setHeading(leader.getHeading());
			mob.setMap(leader.getMapId());
			mob.setMovementDistance(leader.getMovementDistance());
			mob.setRest(leader.isRest());

			mob.setX((leader.getX() + _random.nextInt(5)) - 2);
			mob.setY((leader.getY() + _random.nextInt(5)) - 2);
			// 判斷召喚位置
			if (!canSpawn(mob)) {
				// 該位置判斷不成立 位置設置為主人位置
				mob.setX(leader.getX());
				mob.setY(leader.getY());
			}
			mob.setHomeX(mob.getX());
			mob.setHomeY(mob.getY());

			if (mob instanceof L1MonsterInstance) {
				((L1MonsterInstance) mob).initHideForMinion(leader);
			}

			// mob.setSpawn(leader.getSpawn());
			// mob.setreSpawn(leader.isReSpawn());
			// mob.setSpawnNumber(leader.getSpawnNumber());
			mob.setreSpawn(false);// 隊員不重新召喚

			// 地獄不掉落物品
			if (mob instanceof L1MonsterInstance) {
				if (mob.getMapId() == 666) {
					((L1MonsterInstance) mob).set_storeDroped(true);
				}
			}

			// 設置副本編號 TODO
			mob.set_showId(leader.get_showId());

			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);

			// 隊長具有刪除時間 同時設置隊員一並刪除
			if (leader.is_spawnTime()) {
				mob.set_spawnTime(leader.get_spawnTime());
			}

			if (mob instanceof L1MonsterInstance) {
				if (!_isInitSpawn && (mob.getHiddenStatus() == 0)) {
					mob.onNpcAI(); // AI啟用
				}
			}
			mob.turnOnOffLight();
			mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 出現

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
		return mob;
	}

	private boolean canSpawn(final L1NpcInstance mob) {
		// 召喚位置未超出地圖可用位置
		if (mob.getMap().isInMap(mob.getLocation()) && mob.getMap().isPassable(mob.getLocation(), mob)) {
			if (_isRespawnScreen) {
				return true;
			}
			if (World.get().getVisiblePlayer(mob).size() == 0) {
				return true;
			}
		}
		return false;
	}

}
