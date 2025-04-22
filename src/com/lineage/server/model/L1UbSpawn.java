package com.lineage.server.model;

import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.world.World;

/**
 * 無線大賽
 * 
 * @author daien
 */
public class L1UbSpawn implements Comparable<L1UbSpawn> {
	private int _id;
	private int _ubId;
	private int _pattern;
	private int _group;
	private int _npcTemplateId;
	private int _amount;
	private int _spawnDelay;
	private int _sealCount;
	private String _name;

	// --------------------start getter/setter--------------------
	public int getId() {
		return _id;
	}

	public void setId(final int id) {
		_id = id;
	}

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(final int ubId) {
		_ubId = ubId;
	}

	public int getPattern() {
		return _pattern;
	}

	public void setPattern(final int pattern) {
		_pattern = pattern;
	}

	public int getGroup() {
		return _group;
	}

	public void setGroup(final int group) {
		_group = group;
	}

	public int getNpcTemplateId() {
		return _npcTemplateId;
	}

	public void setNpcTemplateId(final int npcTemplateId) {
		_npcTemplateId = npcTemplateId;
	}

	public int getAmount() {
		return _amount;
	}

	public void setAmount(final int amount) {
		_amount = amount;
	}

	public int getSpawnDelay() {
		return _spawnDelay;
	}

	public void setSpawnDelay(final int spawnDelay) {
		_spawnDelay = spawnDelay;
	}

	public int getSealCount() {
		return _sealCount;
	}

	public void setSealCount(final int i) {
		_sealCount = i;
	}

	public String getName() {
		return _name;
	}

	public void setName(final String name) {
		_name = name;
	}

	// --------------------end getter/setter--------------------

	public void spawnOne() {
		final L1UltimateBattle ub = UBTable.getInstance().getUb(_ubId);
		final L1Location loc = ub.getLocation().randomLocation((ub.getLocX2() - ub.getLocX1()) / 2, false);
		final L1MonsterInstance mob = new L1MonsterInstance(NpcTable.get().getTemplate(getNpcTemplateId()));

		mob.setId(IdFactoryNpc.get().nextId());
		mob.setHeading(5);
		mob.setX(loc.getX());
		mob.setHomeX(loc.getX());
		mob.setY(loc.getY());
		mob.setHomeY(loc.getY());
		mob.setMap((short) loc.getMapId());
		mob.set_storeDroped(!(3 < getGroup()));
		mob.setUbSealCount(getSealCount());
		mob.setUbId(getUbId());

		World.get().storeObject(mob);
		World.get().addVisibleObject(mob);

		final S_NPCPack s_npcPack = new S_NPCPack(mob);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(mob)) {
			pc.addKnownObject(mob);
			mob.addKnownObject(pc);
			pc.sendPackets(s_npcPack);
		}
		// モンスターのＡＩを開始
		mob.onNpcAI();
		mob.turnOnOffLight();
		// mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始
	}

	public void spawnAll() {
		for (int i = 0; i < getAmount(); i++) {
			spawnOne();
		}
	}

	@Override
	public int compareTo(final L1UbSpawn rhs) {
		// XXX - 本当はもっと厳密な順序付けがあるはずだが、必要なさそうなので後回し
		if (getId() < rhs.getId()) {
			return -1;
		}
		if (getId() > rhs.getId()) {
			return 1;
		}
		return 0;
	}

	/*
	 * private static final Log _log = LogFactory.getLog(L1UbSpawn.class
	 * .getName());
	 */
}
