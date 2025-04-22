package com.lineage.server.model.Instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1PcQuest;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_FollowerPack;
import com.lineage.server.serverpackets.S_NPCTalkReturn;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 跟隨者
 * 
 * @author dexc
 */
public class L1FollowerInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1FollowerInstance.class);

	/**
	 * 目標檢查
	 */
	@Override
	public boolean noTarget() {
		// final L1NpcInstance targetNpc = null;
		if (ATTACK != null) {
			L1PcInstance pc = null;
			if (_master instanceof L1PcInstance) {
				pc = (L1PcInstance) _master;
			}
			ATTACK.attack(pc, this);

		} else {
			for (final L1Object object : World.get().getVisibleObjects(this)) {
				if (object instanceof L1NpcInstance) {
					final L1NpcInstance tgnpc = (L1NpcInstance) object;
					if ((tgnpc.getNpcTemplate().get_npcId() == 71061 // 卡得穆斯
					) && (getNpcTemplate().get_npcId() == 71062)) { // 卡米特
						if (getLocation().getTileLineDistance(_master.getLocation()) < 3) {
							final L1PcInstance pc = (L1PcInstance) _master;
							if (((pc.getX() >= 32448) && (pc.getX() <= 32452)) // カドモス周辺座標
									&& ((pc.getY() >= 33048) && (pc.getY() <= 33052))
									&& (pc.getMapId() == 440)) {
								setParalyzed(true);
								if (!pc.getInventory().checkItem(40711)) {
									createNewItem(pc, 40711, 1);
									pc.getQuest().set_step(L1PcQuest.QUEST_CADMUS, 3);
								}
								deleteMe();
								return true;
							}
						}

					} else if ((tgnpc.getNpcTemplate().get_npcId() == 71074 // 蜥蜴人長老
					) && (getNpcTemplate().get_npcId() == 71075)) {// 疲憊的蜥蜴人戰士
						// 疲れ果てたリザードマンファイター
						if (getLocation().getTileLineDistance(_master.getLocation()) < 3) {
							final L1PcInstance pc = (L1PcInstance) _master;
							if (((pc.getX() >= 32731) && (pc.getX() <= 32735)) // リザードマン長老周辺座標
									&& ((pc.getY() >= 32854) && (pc.getY() <= 32858))
									&& (pc.getMapId() == 480)) {
								setParalyzed(true);
								if (!pc.getInventory().checkItem(40633)) {
									createNewItem(pc, 40633, 1);
									pc.getQuest().set_step(L1PcQuest.QUEST_LIZARD, 2);
								}
								deleteMe();
								return true;
							}
						}
					}
				}
			}
		}

		// 進行刪除
		if (destroyed()) {
			return true;
		}
		// 主人為空
		if (_master == null) {
			return true;
		}
		if (_master.isDead() || (getLocation().getTileLineDistance(_master.getLocation()) > 13)) {
			setParalyzed(true);
			// 跟隨者主人遺失 重新召喚原始NPC
			spawn(getNpcTemplate().get_npcId(), getX(), getY(), getHeading(), getMapId());

			deleteMe();
			return true;

		} else if ((_master != null) && (_master.getMapId() == getMapId())) {
			// 跟隨主人移動
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				if (_npcMove != null) {
					_npcMove.setDirectionMove(_npcMove.moveDirection(_master.getX(), _master.getY()));
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
			}
		}
		return false;
	}

	/**
	 * 跟隨者
	 * 
	 * @param template 召喚NPC數據
	 * @param target 原始對話對象
	 * @param master 執行者
	 */
	public L1FollowerInstance(final L1Npc template, final L1NpcInstance target, final L1Character master) {
		super(template);

		// 對話對象是否已經成為跟隨者NPC主人
		if (is(master)) {
			return;
		}

		_master = master;
		setId(IdFactoryNpc.get().nextId());
		// 副本編號
		set_showId(master.get_showId());

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		this.setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());

		// 刪除原始對話對象(這可讓原始NPC重新召喚)
		target.setParalyzed(true);
		target.deleteMe();

		World.get().storeObject(this);
		World.get().addVisibleObject(this);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		onNpcAI();
		master.addFollower(this);
	}

	/**
	 * 對話對象是否已經成為跟隨者NPC主人
	 * 
	 * @param master
	 * @param npcId
	 * @return
	 */
	private boolean is(final L1Character master) {
		if (master.getFollowerList().size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		startAI();
	}

	@Override
	public synchronized void deleteMe() {
		// 移出跟隨者
		_master.removeFollower(this);
		setMaster(null);
		getMap().setPassable(getLocation(), true);
		// 執行L1NpcInstance刪除動作
		super.deleteMe();
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		try {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
			}
			attack.action();
			attack.commit();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		if (isDead()) {
			return;
		}
		switch (getNpcTemplate().get_npcId()) {
		case 71062:// 卡米特
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "kamit2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "kamit1"));
			}
			break;

		case 71075:// 疲憊的蜥蜴人戰士
			if (_master.equals(player)) {
				player.sendPackets(new S_NPCTalkReturn(getId(), "llizard2"));
			} else {
				player.sendPackets(new S_NPCTalkReturn(getId(), "llizard1a"));
			}
			break;
		}
	}

	/**
	 * TODO 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			// 副本ID不相等 不相護顯示
			if (perceivedFrom.get_showId() != get_showId()) {
				return;
			}
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_FollowerPack(this, perceivedFrom));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 給予任務道具
	 * 
	 * @param pc
	 * @param item_id
	 * @param count
	 */
	private void createNewItem(final L1PcInstance pc, final int item_id, final long count) {
		final L1ItemInstance item = ItemTable.get().createItem(item_id);
		item.setCount(count);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else {
				item.set_showId(pc.get_showId());
				World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
		}
	}

	/**
	 * 跟隨者主人遺失 重新召喚原始NPC
	 * 
	 * @param npcId 編號
	 * @param x X座標
	 * @param y Y座標
	 * @param h 面向
	 * @param m 地圖編號
	 */
	public void spawn(final int npcId, final int x, final int y, final int h, final short m) {
		try {
			final L1NpcInstance mob = L1SpawnUtil.spawn(npcId, x, y, m, h);
			final L1QuestInstance newnpc = (L1QuestInstance) mob;
			newnpc.onNpcAI();
			// newnpc.turnOnOffLight();
			newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // チャット開始

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 受攻擊hp減少計算 XXX
	 */
	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		if (getMaxHp() > 100) {
			if ((getCurrentHp() > 0) && !isDead()) {
				final int newHp = getCurrentHp() - damage;
				if ((newHp <= 0) && !isDead()) {
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					final Death death = new Death();
					GeneralThreadPool.get().execute(death);
				}
				if (newHp > 0) {
					setCurrentHp(newHp);
				}

			} else if (!isDead()) {
				setDead(true);
				setStatus(ActionCodes.ACTION_Die);
				final Death death = new Death();
				GeneralThreadPool.get().execute(death);
			}
		}
	}

	/**
	 * 死亡
	 * 
	 * @author daien
	 */
	private class Death implements Runnable {

		@Override
		public void run() {
			try {
				// 解除舊座標障礙宣告
				getMap().setPassable(getLocation(), true);

				setDeathProcessing(true);

				broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

				setDeathProcessing(false);
				// 加入死亡處理清單
				startDeleteTimer(10);

			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
