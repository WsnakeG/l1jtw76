package com.lineage.server.model;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.world.World;

/**
 * NPC 召喚技能
 * 
 * @author dexc
 */
public class L1MobSkillUseSpawn {

	private static final Log _log = LogFactory.getLog(L1MobSkillUseSpawn.class);

	private final Random _rnd = new Random();

	/**
	 * NPC召喚技能
	 * 
	 * @param attacker 執行的NPC
	 * @param target 目標物件
	 * @param summonId 召喚的NPC編號
	 * @param count 數量
	 */
	public void mobspawn(final L1Character attacker, final L1Character target, final int summonId,
			final int count) {
		int i;
		for (i = 0; i < count; i++) {
			this.mobspawn(attacker, target, summonId);
		}
	}

	public L1MonsterInstance mobspawnX(final L1Character attacker, final L1Character target,
			final int summonId) {
		return this.mobspawn(attacker, target, summonId);
	}

	/**
	 * NPC召喚技能
	 * 
	 * @param attacker 執行的NPC
	 * @param target 目標物件
	 * @param summonId 召喚的NPC編號
	 * @return
	 */
	private L1MonsterInstance mobspawn(final L1Character attacker, final L1Character target,
			final int summonId) {
		try {
			final L1NpcInstance mob = NpcTable.get().newNpcInstance(summonId);
			if (mob == null) {
				_log.error("NPC召喚技能 目標NPCID設置異常 異常編號: " + summonId);
				return null;
			}
			mob.setId(IdFactoryNpc.get().nextId());
			final L1Location loc = attacker.getLocation().randomLocation(6, false);
			final int heading = _rnd.nextInt(8);
			mob.setX(loc.getX());
			mob.setY(loc.getY());
			mob.setHomeX(loc.getX());
			mob.setHomeY(loc.getY());
			final short mapid = attacker.getMapId();
			mob.setMap(mapid);
			mob.setHeading(heading);

			// 設置副本編號 TODO
			mob.set_showId(attacker.get_showId());

			World.get().storeObject(mob);
			World.get().addVisibleObject(mob);
			final L1Object object = World.get().findObject(mob.getId());

			final L1MonsterInstance newnpc = (L1MonsterInstance) object;
			switch (mob.getNpcId()) {
			case 45061:// 弱化史巴托
			case 45161:// 史巴托
			case 45181:// 史巴托
			case 45455:// 殘暴的史巴托
				newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), ActionCodes.ACTION_Hide));
				newnpc.setStatus(13);
				newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
				newnpc.broadcastPacketAll(new S_DoActionGFX(newnpc.getId(), ActionCodes.ACTION_Appear));
				newnpc.setStatus(0);
				newnpc.broadcastPacketAll(new S_NPCPack(newnpc));
				// 不掉落道具
				newnpc.set_storeDroped(true);
				break;

			case 97350:// 沙蟲-召喚被餵食的毒蠍 by terry0412
				break;

			default:
				// 不掉落道具
				newnpc.set_storeDroped(true);
				break;
			}
			newnpc.onNpcAI();
			// newnpc.turnOnOffLight();
			newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始

			if (target != null) {
				newnpc.setLink(target);
			}

			if (newnpc != null) {
				return newnpc;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	/**
	 * NPC召喚技能
	 * 
	 * @param attacker
	 * @param target
	 * @param summonId
	 */
	public void mobspawnSrc(final L1Character attacker, final L1Character target, final int summonId) {
		try {
			final L1NpcInstance npc = NpcTable.get().newNpcInstance(summonId);
			npc.setId(IdFactoryNpc.get().nextId());
			npc.setMap(attacker.getMapId());
			npc.setX(attacker.getX());
			npc.setY(attacker.getY());
			npc.setHomeX(npc.getX());
			npc.setHomeY(npc.getY());
			npc.setHeading(attacker.getHeading());

			// 設置副本編號 TODO
			npc.set_showId(attacker.get_showId());

			World.get().storeObject(npc);
			World.get().addVisibleObject(npc);

			npc.turnOnOffLight();
			npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始

			npc.setLink(target);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
