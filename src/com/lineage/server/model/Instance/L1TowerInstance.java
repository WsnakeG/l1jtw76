package com.lineage.server.model.Instance;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Clan;
import com.lineage.server.model.L1War;
import com.lineage.server.model.L1WarSpawn;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldClan;
import com.lineage.server.world.WorldNpc;
import com.lineage.server.world.WorldWar;

/**
 * 守護者之塔控制項
 * 
 * @author daien
 */
public class L1TowerInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1TowerInstance.class);

	/**
	 * 守護者之塔
	 * 
	 * @param template
	 */
	public L1TowerInstance(final L1Npc template) {
		super(template);
	}

	private int _castle_id;// 所屬城堡編號

	public int get_castle_id() {// 所屬城堡編號
		return _castle_id;
	}

	private int _crackStatus;// 損壞狀態

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack(this));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onAction(final L1PcInstance player) {
		try {
			if ((getCurrentHp() > 0) && !isDead()) {
				final L1AttackMode attack = new L1AttackPc(player, this);
				if (attack.calcHit()) {
					attack.calcDamage();
				}
				attack.action();
				attack.commit();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		boolean isDamage = false;
		if (_castle_id == 0) { // 所屬城堡編號未設定
			if (isSubTower()) {
				_castle_id = L1CastleLocation.ADEN_CASTLE_ID;

			} else {
				_castle_id = L1CastleLocation.getCastleId(getX(), getY(), getMapId());
			}
		}
		// 戰爭期間內
		if ((_castle_id > 0) && ServerWarExecutor.get().isNowWar(_castle_id)) {
			// 亞丁城 攻擊主塔 必須先破壞四座子塔
			if ((_castle_id == L1CastleLocation.ADEN_CASTLE_ID) && !isSubTower()) {
				int subTowerDeadCount = 0;// 子塔被破壞數量
				for (final L1NpcInstance npc : WorldNpc.get().all()) {
					if (npc instanceof L1TowerInstance) {
						final L1TowerInstance tower = (L1TowerInstance) npc;
						if (tower.isSubTower() && tower.isDead()) {
							subTowerDeadCount++;
						}
					}
				}
				if (subTowerDeadCount >= 4) {
					// 亞丁城主塔 四座子塔已破壞-設定塔可以攻擊
					isDamage = true;

				} else {
					// 四座子塔未被破壞
					return;
				}
			}
			// 戰爭期間內-設定塔可以攻擊
			isDamage = true;
		}

		// 塔可以攻擊
		if (isDamage) {
			// 判斷主要攻擊者
			L1PcInstance pc = null;
			if (attacker instanceof L1PcInstance) {// 攻擊者是玩家
				pc = (L1PcInstance) attacker;

			} else if (attacker instanceof L1PetInstance) {// 攻擊者是寵物
				pc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();

			} else if (attacker instanceof L1SummonInstance) {// 攻擊者是 召換獸
				pc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();

			} else if (attacker instanceof L1IllusoryInstance) {// 攻擊者是 分身
				pc = (L1PcInstance) ((L1IllusoryInstance) attacker).getMaster();

			} else if (attacker instanceof L1EffectInstance) {// 攻擊者是 技能物件(火牢)
				// 火牢無法傷害守護塔
				// pc = (L1PcInstance) ((L1EffectInstance)
				// attacker).getMaster();
			}

			if (pc == null) {
				return;
			}

			boolean existDefenseClan = false;// 該城堡是否具有城盟
			final Collection<L1Clan> allClans = WorldClan.get().getAllClans();
			for (final Iterator<L1Clan> iter = allClans.iterator(); iter.hasNext();) {
				final L1Clan clan = iter.next();
				final int clanCastleId = clan.getCastleId();
				if (clanCastleId == _castle_id) {
					existDefenseClan = true;
					break;
				}
			}

			boolean isProclamation = false;// 是否為戰爭中血盟
			// 全部戰爭資料
			for (final L1War war : WorldWar.get().getWarList()) {
				if (_castle_id == war.getCastleId()) { // 攻擊的城堡在戰爭狀態
					isProclamation = war.checkClanInWar(pc.getClanname());
					break;
				}
			}

			// 有城盟 並且 攻擊者非戰爭中血盟
			if ((existDefenseClan == true) && (isProclamation == false)) {
				return;
			}

			if ((getCurrentHp() > 0) && !isDead()) {
				final int newHp = getCurrentHp() - damage;
				if ((newHp <= 0) && !isDead()) {
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_TowerDie);
					_crackStatus = 0;
					final Death death = new Death(this, attacker);
					GeneralThreadPool.get().execute(death);
				}
				if (newHp > 0) {
					setCurrentHp(newHp);
					if (((getMaxHp() * 1) / 5) > getCurrentHp()) {
						if (_crackStatus != 4) {
							broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack4));
							setStatus(ActionCodes.ACTION_TowerCrack4);
							_crackStatus = 4;
						}
					} else if (((getMaxHp() * 2) / 5) > getCurrentHp()) {
						if (_crackStatus != 3) {
							broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack3));
							setStatus(ActionCodes.ACTION_TowerCrack3);
							_crackStatus = 3;
						}
					} else if (((getMaxHp() * 3) / 5) > getCurrentHp()) {
						if (_crackStatus != 2) {
							broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack2));
							setStatus(ActionCodes.ACTION_TowerCrack2);
							_crackStatus = 2;
						}
					} else if (((getMaxHp() * 4) / 5) > getCurrentHp()) {
						if (_crackStatus != 1) {
							broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_TowerCrack1));
							setStatus(ActionCodes.ACTION_TowerCrack1);
							_crackStatus = 1;
						}
					}
				}

			} else if (!isDead()) { // 念のため
				setDead(true);
				setStatus(ActionCodes.ACTION_TowerDie);
				final Death death = new Death(this, attacker);
				GeneralThreadPool.get().execute(death);
			}
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);
	}

	/**
	 * 塔死亡
	 * 
	 * @author daien
	 */
	private class Death implements Runnable {

		// private L1Character _lastattacker;// 攻擊者

		private L1TowerInstance _tower = null;// 塔

		public Death(final L1TowerInstance tower, final L1Character attacker) {
			// _lastattacker = attacker;
			_tower = tower;
		}

		@Override
		public void run() {
			_tower.setCurrentHpDirect(0);
			_tower.setDead(true);
			_tower.setStatus(ActionCodes.ACTION_TowerDie);

			_tower.getMap().setPassable(_tower.getLocation(), true);

			_tower.broadcastPacketAll(new S_DoActionGFX(_tower.getId(), ActionCodes.ACTION_TowerDie));
			// 不是亞丁守護子塔
			if (!_tower.isSubTower()) {
				final L1WarSpawn warspawn = new L1WarSpawn();
				warspawn.SpawnCrown(_tower._castle_id);
			}
		}
	}

	@Override
	public void deleteMe() {
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		World.get().removeVisibleObject(this);
		World.get().removeObject(this);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			pc.removeKnownObject(this);
			pc.sendPackets(new S_RemoveObject(this));
		}
		removeAllKnownObjects();
	}

	/**
	 * 是亞丁守護子塔
	 * 
	 * @return
	 */
	public boolean isSubTower() {
		return ((getNpcTemplate().get_npcId() == 81190)// 守護塔:伊娃
				|| (getNpcTemplate().get_npcId() == 81191)// 守護塔:帕格裡奧
				|| (getNpcTemplate().get_npcId() == 81192)// 守護塔:馬普勒
				|| (getNpcTemplate().get_npcId() == 81193));// 守護塔:沙哈
	}
}
