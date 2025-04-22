package com.lineage.server.model.Instance;

import static com.lineage.server.model.item.L1ItemId.B_POTION_OF_GREATER_HASTE_SELF;
import static com.lineage.server.model.item.L1ItemId.B_POTION_OF_HASTE_SELF;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_EXTRA_HEALING;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_GREATER_HASTE_SELF;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_GREATER_HEALING;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_HASTE_SELF;
import static com.lineage.server.model.item.L1ItemId.POTION_OF_HEALING;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.BLIND_HIDING;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.INVISIBILITY;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HASTE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.data.executor.NpcExecutor;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.DeShopChatTable;
import com.lineage.server.datatables.MobSkillGroupTable;
import com.lineage.server.datatables.NpcChatTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.SprTable;
import com.lineage.server.datatables.lock.SpawnBossReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackNpc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1GroundInventory;
import com.lineage.server.model.L1HateList;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Magic;
import com.lineage.server.model.L1MobGroupInfo;
import com.lineage.server.model.L1MobSkillGroupUse;
import com.lineage.server.model.L1MobSkillUse;
import com.lineage.server.model.L1NpcChatTimer;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Spawn;
// import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ChangeShape;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_NewMaster;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_Resurrection;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillHaste;
import com.lineage.server.serverpackets.S_SkillSound;
// import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.templates.L1MobSkillGroup;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1NpcChat;
import com.lineage.server.timecontroller.npc.NpcWorkTimer;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldNpc;
import com.lineage.server.world.WorldQuest;

/**
 * 對象:NPC 控制項
 * 
 * @author dexc
 */
public class L1NpcInstance extends L1Character {

	private static final Log _log = LogFactory.getLog(L1NpcInstance.class);

	private static final long serialVersionUID = 1L;

	public static final int MOVE_SPEED = 0;
	public static final int ATTACK_SPEED = 1;
	public static final int MAGIC_SPEED = 2;

	public static final int HIDDEN_STATUS_NONE = 0;
	public static final int HIDDEN_STATUS_SINK = 1;
	public static final int HIDDEN_STATUS_FLY = 2;
	public static final int HIDDEN_STATUS_ICE = 3;
	public static final int HIDDEN_STATUS_STANDBY = 4;

	// 動作暫停時間
	public static final int REST_MILLISEC = 10;

	// 作用距離
	public static final int DISTANCE = 20;

	/** 出現 */
	public static final int CHAT_TIMING_APPEARANCE = 0;

	/** 死亡 */
	public static final int CHAT_TIMING_DEAD = 1;

	/** 取消隱藏 */
	public static final int CHAT_TIMING_HIDE = 2;

	/** 指定時間 */
	public static final int CHAT_TIMING_GAME_TIME = 3;

	private static final Random _random = new Random();

	private L1Npc _npcTemplate;

	private L1Spawn _spawn;

	private int _spawnNumber; // L1Spawn 的管理編號

	private int _petcost; // ペットになったときのコスト

	public L1Inventory _inventory = new L1Inventory();// 背包

	public L1MobSkillUse _mobSkill;// 技能

	private L1MobSkillGroupUse _mobSkillGroup;

	// 160701 ROY嘗試修正精準目標
	// public int tt_clanid = -1;
	// public int tt_partyid = -1;
	// public int tt_level = 0;

	// 対象を初めて発見したとき。（テレポート用）
	private boolean firstFound = true;

	// 隨機移動距離
	private int _randomMoveDistance = 0;

	// 隨機移動方向
	private int _randomMoveDirection = 0;

	/**
	 * NPC對話判斷
	 */
	public NpcExecutor TALK = null;

	/**
	 * NPC對話執行
	 */
	public NpcExecutor ACTION = null;

	/**
	 * NPC受到攻擊(NPC)
	 */
	public NpcExecutor ATTACK = null;

	/**
	 * NPC死亡(MOB)
	 */
	public NpcExecutor DEATH = null;

	/**
	 * NPC工作時間
	 */
	public NpcExecutor WORK = null;

	/**
	 * NPC召喚
	 */
	public NpcExecutor SPAWN = null;

	protected boolean ISASCAPE = false;// 脫逃

	// ■■■■■■■■■■■■■ ＡＩ関連 ■■■■■■■■■■■

	/**
	 * 啟用NPC AI
	 */
	protected void startAI() {
		if (isDead()) {
			return;
		}
		if (destroyed()) {
			return;
		}
		if (getCurrentHp() <= 0) {
			return;
		}
		final NpcAI npcai = new NpcAI(this);
		npcai.startAI();

		startHpRegeneration();
		startMpRegeneration();
	}

	/**
	 * npc物品搜尋設置
	 */
	public void onItemUse() {
	}

	/**
	 * npc目標搜尋設置
	 */
	public void searchTarget() {
	}

	/**
	 * 增加物件主人(寵物)
	 * 
	 * @param master
	 */
	public void addMaster(final L1PcInstance master) {
		try {
			final StringBuilder stringBuilder = new StringBuilder();
			if (master != null) {
				stringBuilder.append(master.getViewName());
			} else {
				stringBuilder.append("");
			}
			// 增加物件組人
			master.sendPackets(new S_NewMaster(stringBuilder.toString(), this));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 有效目標檢查
	 */
	public void checkTarget() {
		try {
			if (_target == null) {// 目標為空
				clearTagert();
				return;
			}
			if (_target.getMapId() != getMapId()) {// 目標地圖不相等
				clearTagert();
				return;
			}
			if (_target.getCurrentHp() <= 0) {// 目標HP小於等於0
				clearTagert();
				return;
			}
			if (_target.isDead()) {// 目標死亡
				clearTagert();
				return;
			}
			if (get_showId() != _target.get_showId()) {// 副本ID不相等
				clearTagert();
				return;
			}
			if (_target.isInvisble() && // 目標已經隱身
					!getNpcTemplate().is_agrocoi() && // 不具備探查隱身能力
					!_hateList.containsKey(_target)) {// 目標不在已有攻擊清單中
				clearTagert();
				return;
			}

			final int distance = getLocation().getTileDistance(_target.getLocation());
			if (distance > 30) {
				clearTagert();
				return;
			}

		} catch (final Exception e) {
			return;
		}
	}

	/**
	 * 清除無效目標<BR>
	 * 搜尋新目標
	 */
	private void clearTagert() {
		if (_target != null) {
			// 清除目標
			tagertClear();
		}

		if (!_hateList.isEmpty()) {
			_target = _hateList.getMaxHateCharacter();
			checkTarget();
		}
	}

	/**
	 * 該物件是否為目標
	 * 
	 * @param cha
	 * @return
	 */
	public boolean isHate(final L1Character cha) {
		return _hateList.isHate(cha);
	}

	/**
	 * 攻擊目標設置
	 * 
	 * @param cha
	 * @param hate
	 */
	public void setHate(final L1Character cha, int hate) {
		try {
			if ((cha != null) && (cha.getId() != getId())) {
				if (!isFirstAttack() && (hate != 0)) {
					hate += getMaxHp() / 10; // ＦＡヘイト
					setFirstAttack(true);
				}
				if (_npcMove != null) {
					_npcMove.clear();// XXX
				}

				_hateList.add(cha, hate);
				_dropHateList.add(cha, hate);
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}

		} catch (final Exception e) {
			return;
		}
	}

	/**
	 * 攻擊目標設置
	 * 
	 * @param cha
	 */
	public void setLink(final L1Character cha) {
	}

	/**
	 * 互相幫助的判斷
	 * 
	 * @param targetPlayer
	 * @param family
	 */
	public void serchLink(final L1PcInstance targetPlayer, final int family) {
		final List<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
		for (final Object knownObject : targetKnownObjects) {
			if (knownObject instanceof L1NpcInstance) {
				final L1NpcInstance npc = (L1NpcInstance) knownObject;
				// 副本ID不相等 不列入判斷
				if (get_showId() != npc.get_showId()) {
					continue;
				}
				if (npc.getNpcTemplate().get_agrofamily() > 0) {
					// 同族間互相幫助的設置
					if (npc.getNpcTemplate().get_agrofamily() == 1) {
						// 種族相同
						if (npc.getNpcTemplate().get_family() == family) {
							npc.setLink(targetPlayer);
						}

					} else {
						// 大於1全部NPC支援
						npc.setLink(targetPlayer);
					}
				}

				// 隊伍間互相幫助的設置
				final L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
				if (mobGroupInfo != null) {
					// 具有隊伍
					if (getMobGroupId() != 0) {
						// 相同隊伍
						if (getMobGroupId() == npc.getMobGroupId()) {
							npc.setLink(targetPlayer);
						}
					}
				}
			}
		}
	}

	/**
	 * 具有目標的處理 (攻擊的判斷)
	 */
	public void onTarget() {
		try {
			// System.out.println("具有目標的處理");
			setActived(true);
			// 具有目標 清除想要撿取的物品
			if (_targetItemList.size() > 0) {
				_targetItemList.clear();
			}
			if (_targetItem != null) {
				_targetItem = null;
			}

			// ここから先は_targetが変わると影響出るので別領域に参照確保
			final L1Character target = _target;

			if (getAtkspeed() == 0) { // 逃げるキャラ
				if (getPassispeed() > 0) { // 移動できるキャラ
					ISASCAPE = true;
				}
			}

			if (ISASCAPE) {
				ascape(target.getLocation());

			} else {
				if (target == null) {
					return;
				}
				attack(target);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	protected void attack(final L1Character target) {
		// 攻撃可能位置
		if (isAttackPosition(target.getX(), target.getY(), get_ranged())) {
			if ((_mobSkill != null) && (_mobSkillGroup != null)) {
				final int actNo = _mobSkillGroup.pickActNo();

				if ((actNo != -1) && _mobSkill.skillGroupUse(target, actNo,
						_mobSkillGroup.getSkillGroup().getLeverage(actNo))) {
					setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
					return;
				}

				if (_mobSkillGroup.skillUse(target)) {
					setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
					return;
				}
			}
			if (_mobSkill.isSkillTrigger(target)) { // トリガの条件に合うスキルがある
				if (_random.nextInt(2) >= 1) { // 一定の確率で物理攻撃
					setHeading(targetDirection(target.getX(), target.getY()));
					attackTarget(target);

				} else {
					if (_mobSkill.skillUse(target, true)) { // スキル使用(mobskill.sqlのTriRndに従う)
						setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));

					} else { // スキル使用が失敗したら物理攻撃
						setHeading(targetDirection(target.getX(), target.getY()));
						attackTarget(target);
					}
				}

			} else {
				setHeading(targetDirection(target.getX(), target.getY()));
				attackTarget(target);
			}
			if (_npcMove != null) {
				_npcMove.clear();
			}

		} else { // 攻撃不可能位置
			// 對象直線上無障礙
			if (glanceCheck(_target.getX(), _target.getY())) {
				if (_mobSkill.skillUse(target, false)) {
					// スキル使用(mobskill.sqlのTriRndに従わず、発動確率は100%。ただしサモン、強制変身は常にTriRndに従う。)
					setSleepTime(calcSleepTime(_mobSkill.getSleepTime(), MAGIC_SPEED));
					return;
				}
			}

			if (getPassispeed() > 0) {
				// 移動できるキャラ
				final int distance = getLocation().getTileDistance(target.getLocation());
				if (firstFound == true) {
					if (getNpcTemplate().is_teleport() && (distance > 3) && (distance < DISTANCE)) {
						if (nearTeleport(target.getX(), target.getY()) == true) {
							firstFound = false;
							return;
						}
					}
				}

				if (getNpcTemplate().is_teleport()) {// NPC 會使用傳送
					if ((20 > _random.nextInt(100)) && (getCurrentMp() >= 10) && (distance > 6)
							&& (distance < DISTANCE)) { // テレポート移動
						if (nearTeleport(target.getX(), target.getY()) == true) {
							return;
						}
					}
				}

				if (isGripped()) {
					return;
				}

				if (_npcMove != null) {
					final int dir = _npcMove.moveDirection(target.getX(), target.getY());
					if (dir == -1) {
						tagertClear();

					} else {
						_npcMove.setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
						setNpcDollSpeed();
					}
				}

			} else {
				switch (getGfxId()) {// XXX
				case 816:// 警衛(妖堡箭塔)
					attackTarget(target);
					break;

				case 10071:
					if (World.get().getVisiblePlayer2(this, 17).size() <= 0) {
						hideOnGround();
					}
					break;

				default:
					tagertClear();
					break;
				}
			}
		}
	}

	private void setNpcDollSpeed() {
		// 虛擬人物
		if (this instanceof L1DeInstance) {
			final L1DeInstance de = (L1DeInstance) this;
			de.setNpcSpeed();
		}
	}

	/**
	 * 脫逃
	 * 
	 * @param location
	 */
	private void ascape(final L1Location location) {
		int escapeDistance = 17;
		if (hasSkillEffect(DARKNESS) == true) {
			escapeDistance = 1;
		}

		if (getLocation().getTileLineDistance(location) > escapeDistance) { // 逃亡距離到達設置值
			tagertClear();

		} else { // 目標反方向移動
			if (_npcMove != null) {
				int dir = _npcMove.targetReverseDirection(location.getX(), location.getY());
				dir = _npcMove.checkObject(dir);
				dir = _npcMove.openDoor(dir);
				_npcMove.setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			}
		}
	}

	/**
	 * 是否可以成為目標
	 * 
	 * @param cha
	 * @return 人物 寵物 召喚獸 進入安全區 傳回無法攻擊(false)
	 */
	private boolean isTraget(final L1Character cha) {
		boolean isTraget = false;
		if (cha instanceof L1PcInstance) {
			final L1PcInstance tg = (L1PcInstance) cha;
			if (!tg.isSafetyZone()) {
				isTraget = true;
			}

		} else if (cha instanceof L1PetInstance) {
			final L1PetInstance tg = (L1PetInstance) cha;
			if (!tg.isSafetyZone()) {
				isTraget = true;
			}

		} else if (cha instanceof L1SummonInstance) {
			final L1SummonInstance tg = (L1SummonInstance) cha;
			if (!tg.isSafetyZone()) {
				isTraget = true;
			}

		} else if (cha instanceof L1MonsterInstance) {
			isTraget = true;
		}
		// System.out.println("是否可以成為目標:" + isTraget);
		return isTraget;
	}

	/**
	 * 對目標進行攻擊
	 * 
	 * @param target
	 */
	public void attackTarget(final L1Character target) {
		// 攻擊者是分身
		if (this instanceof L1IllusoryInstance) {
			if (!isTraget(target)) {
				// 目標無法攻擊 暫停攻擊
				return;
			}
		}

		if (target instanceof L1PcInstance) {
			final L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) { // テレポート処理中
				return;
			}

		} else if (target instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) target;
			final L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}

		} else if (target instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) target;
			final L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		}

		if (this instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) this;
			final L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}

		} else if (this instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) this;
			final L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート処理中
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // 地中に潜っているか、飛んでいる
				allTargetClear();
				return;
			}
		}

		boolean isCounterBarrier = false;
		final L1AttackMode attack = new L1AttackNpc(this, target);
		if (attack.calcHit()) {
			if (target.hasSkillEffect(COUNTER_BARRIER)) {
				final L1Magic magic = new L1Magic(target, this);
				final boolean isProbability = magic.calcProbabilityMagic(COUNTER_BARRIER);
				final boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isCounterBarrier = true;
				}
			}
			if (!isCounterBarrier) {
				attack.calcDamage();
			}
		}
		if (isCounterBarrier) {
			// attack.actionCounterBarrier();
			attack.commitCounterBarrier();

		} else {
			attack.action();
			attack.commit();
		}
		setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
	}

	/**
	 * ターゲットアイテムを探す
	 */
	public void searchTargetItem() {
		final ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (final L1Object obj : World.get().getVisibleObjects(this)) {
			if ((obj != null) && (obj instanceof L1GroundInventory)) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		// 拾うアイテム(のインベントリ)をランダムで選定
		final int pickupIndex = (int) (Math.random() * gInventorys.size());
		final L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (final L1ItemInstance item : inventory.getItems()) {
			if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) { // 持てるならターゲットアイテムに加える
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}
		}
	}

	/**
	 * 飛んでいる状態からアイテムを探し、あれば降りて拾う
	 */
	public void searchItemFromAir() {
		final ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (final L1Object obj : World.get().getVisibleObjects(this)) {
			if ((obj != null) && (obj instanceof L1GroundInventory)) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}

		// 拾うアイテム(のインベントリ)をランダムで選定
		final int pickupIndex = (int) (Math.random() * gInventorys.size());
		final L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (final L1ItemInstance item : inventory.getItems()) {
			if ((item.getItem().getType() == 6 // potion
			) || (item.getItem().getType() == 7)) { // food
				if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
					if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
						setHiddenStatus(HIDDEN_STATUS_NONE);
						broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
						setStatus(0);
						broadcastPacketAll(new S_NPCPack(this));
						onNpcAI();
						startChat(CHAT_TIMING_HIDE);
						_targetItem = item;
						_targetItemList.add(_targetItem);
					}
				}
			}
		}
	}

	public static void shuffle(final L1Object[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			final int t = (int) (Math.random() * i);

			// 選ばれた値と交換する
			final L1Object tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}

	/**
	 * 檢取目標有效判斷
	 */
	public void checkTargetItem() {
		if ((_targetItem == null) || (_targetItem.getMapId() != getMapId())
				|| (getLocation().getTileDistance(_targetItem.getLocation()) > 15)) {
			// 具有目標元素
			if (!_targetItemList.isEmpty()) {
				_targetItem = _targetItemList.get(0);
				_targetItemList.remove(0);
				checkTargetItem();// 重新檢查目標物品

			} else {
				_targetItem = null;
			}
		}
	}

	/**
	 * 物品檢取的判斷
	 */
	public void onTargetItem() {
		if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) { // 抵達可撿取物品位置
			pickupTargetItem(_targetItem);

		} else { // 尚未抵達可撿取物品位置
			if (_npcMove != null) {
				final int dir = _npcMove.moveDirection(_targetItem.getX(), _targetItem.getY());
				if (dir == -1) { // 拾うの諦め
					_targetItemList.remove(_targetItem);
					_targetItem = null;

				} else { // ターゲットアイテムへ移動
					_npcMove.setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}
			}
		}
	}

	/**
	 * 檢取物品
	 * 
	 * @param targetItem
	 */
	public void pickupTargetItem(final L1ItemInstance targetItem) {
		try {
			final int x = targetItem.getX();
			final int y = targetItem.getY();
			final short m = targetItem.getMapId();
			final L1Inventory groundInv = World.get().getInventory(x, y, m);
			final L1ItemInstance item = groundInv.tradeItem(targetItem, targetItem.getCount(),
					getInventory());
			// turnOnOffLight();
			onGetItem(item);
			_targetItemList.remove(_targetItem);
			_targetItem = null;
			setSleepTime(1000);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 沒有目標的處理 (傳回本次AI是否執行完成)<BR>
	 * 具有主人 跟隨主人移動
	 * 
	 * @return true:本次AI執行完成 <BR>
	 *         false:本次AI執行未完成
	 */
	public boolean noTarget() {
		if ((_master != null) && (_master.getMapId() == getMapId())
				&& (getLocation().getTileLineDistance(_master.getLocation()) > 2)) {
			// 具備移動速度
			if (getPassispeed() > 0) {
				if (_npcMove != null) {
					// 跟隨主人移動
					final int dir = _npcMove.moveDirection(_master.getX(), _master.getY());
					if (dir != -1) {
						_npcMove.setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));

					} else {
						return true;
					}
				} else {
					return true;
				}
			}

		} else {
			// 週邊無PC物件 AI處理終止
			if (World.get().getVisiblePlayer(this, DISTANCE).size() <= 0) {
				teleport(getHomeX(), getHomeY(), getHeading());
				return true;
			}
			// 沒有隊長 具有移動速度 並未動作暫停
			if ((_master == null) && (getPassispeed() > 0) && !isRest()) {
				// NPC不具有隊伍狀態
				final L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
				if ((mobGroupInfo == null) || ((mobGroupInfo != null) && mobGroupInfo.isLeader(this))) {

					// 如果移動距離已經為0 重新定義隨機移動
					if (_randomMoveDistance == 0) {
						// 產生移動距離
						_randomMoveDistance = _random.nextInt(3) + 1;

						// 產生移動方向(隨機數值超出7物件會暫停移動)
						_randomMoveDirection = _random.nextInt(40);

						// 返回出生點的判斷
						if ((getHomeX() != 0) && (getHomeY() != 0)// 具有出生點設置
								&& (_randomMoveDirection < 8)// 具有移動方向
						/* && (_random.nextInt(3) == 0) */) {// 隨機產生0
							if (_npcMove != null) {
								_randomMoveDirection = _npcMove.moveDirection(getHomeX(), getHomeY());
							}
						}

					} else {
						_randomMoveDistance--;
					}

					if (_npcMove != null) {
						if (_randomMoveDirection < 8) {
							int dir = _npcMove.checkObject(_randomMoveDirection);
							dir = _npcMove.openDoor(dir);

							if (dir != -1) {
								_npcMove.setDirectionMove(dir);
								setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
							}
						}
					}

				} else {
					// 領隊追尋
					final L1NpcInstance leader = mobGroupInfo.getLeader();
					if (getPassispeed() > 0) {
						if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
							if (_npcMove != null) {
								final int dir = _npcMove.moveDirection(leader.getX(), leader.getY());
								if (dir == -1) {
									return true;

								} else {
									_npcMove.setDirectionMove(dir);
									setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * NPC對話結果的處理
	 * 
	 * @param pc
	 * @param s
	 */
	public void onFinalAction(final L1PcInstance pc, final String s) {
	}

	/**
	 * 現在目標消除(hateList)
	 */
	protected void tagertClear() {
		if (_target == null) {
			return;
		}
		_hateList.remove(_target);
		_target = null;
	}

	/**
	 * 指定目標消除(hateList)
	 * 
	 * @param target
	 */
	public void targetRemove(final L1Character target) {
		_hateList.remove(target);
		if ((_target != null) && _target.equals(target)) {
			_target = null;
		}
	}

	/**
	 * 全部目標消除<BR>
	 * (hateList)<BR>
	 * (dropHateList)<BR>
	 * (targetItemList)
	 */
	public void allTargetClear() {
		if (_npcMove != null) {
			_npcMove.clear();
		}
		_hateList.clear();
		_dropHateList.clear();
		_target = null;
		_targetItemList.clear();
		_targetItem = null;
		if (_mobSkillGroup != null) {
			_mobSkillGroup.deleteAllTemplates();
			_mobSkillGroup = null;
		}
	}

	/**
	 * 設置主人
	 * 
	 * @param cha
	 */
	public void setMaster(final L1Character cha) {
		_master = cha;
	}

	/**
	 * 傳回主人
	 * 
	 * @return
	 */
	public L1Character getMaster() {
		return _master;
	}

	// ＡＩトリガ
	public void onNpcAI() {
	}

	/**
	 * 道具交換製作
	 */
	public void refineItem() {

		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		if (_npcTemplate.get_npcId() == 45032) { // ブロッブ
			// オリハルコンソードの刀身
			if ((getExp() != 0) && !_inventory.checkItem(20)) {
				materials = new int[] { 40508, 40521, 40045 };
				counts = new int[] { 150, 3, 3 };
				createitem = new int[] { 20 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ロングソードの刀身
			if ((getExp() != 0) && !_inventory.checkItem(19)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 150, 3 };
				createitem = new int[] { 19 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ショートソードの刀身
			if ((getExp() != 0) && !_inventory.checkItem(3)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 50, 1 };
				createitem = new int[] { 3 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// オリハルコンホーン
			if ((getExp() != 0) && !_inventory.checkItem(100)) {
				materials = new int[] { 88, 40508, 40045 };
				counts = new int[] { 4, 80, 3 };
				createitem = new int[] { 100 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// ミスリルホーン
			if ((getExp() != 0) && !_inventory.checkItem(89)) {
				materials = new int[] { 88, 40494 };
				counts = new int[] { 2, 80 };
				createitem = new int[] { 89 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						final L1ItemInstance item = _inventory.storeItem(createitem[j], createcount[j]);
						if (getNpcTemplate().get_digestitem() > 0) {
							setDigestItem(item);
						}
					}
				}
			}

		} else if ((_npcTemplate.get_npcId() == 45166 // ジャックオーランタン
		) || (_npcTemplate.get_npcId() == 45167)) {
			// パンプキンの種
			if ((getExp() != 0) && !_inventory.checkItem(40726)) {
				materials = new int[] { 40725 };
				counts = new int[] { 1 };
				createitem = new int[] { 40726 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		}
	}

	private boolean _aiRunning = false; // NPC AI時間軸 正在運行

	// ※ＡＩをスタートさせる時にすでに実行されてないか確認する時に使用
	private boolean _actived = false; // NPC已經激活

	// ※この値がfalseで_targetがいる場合、アクティブになって初行動とみなしヘイストポーション等を使わせる判定で使用
	private boolean _firstAttack = false; // ファーストアッタクされたか

	private int _sleep_time; // ＡＩを停止する時間(ms) ※行動を起こした場合に所要する時間をセット

	protected final L1HateList _hateList = new L1HateList();// 目標清單

	protected final L1HateList _dropHateList = new L1HateList();// 掉落物品目標清單

	// ※攻撃するターゲットの判定とＰＴ時のドロップ判定で使用
	protected final List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>(); // ダーゲットアイテム一覧

	// NPC消化道具時間清單
	private final HashMap<L1ItemInstance, DelItemTime> _del_map = new HashMap<L1ItemInstance, DelItemTime>();

	protected L1Character _target = null; // 現在のターゲット

	protected L1ItemInstance _targetItem = null; // 現在のターゲットアイテム

	protected L1Character _master = null; // 主人orグループリーダー

	private boolean _deathProcessing = false; // 死亡處理中

	/**
	 * 目標清單
	 * 
	 * @return
	 */
	public L1HateList getHateList() {
		return _hateList;
	}

	/**
	 * HP是否具備回復條件
	 * 
	 * @return
	 */
	public final boolean isHpR() {
		if (destroyed()) {// 進行刪除
			return false;
		}

		if (isDead()) {// 死亡
			return false;
		}

		if (getMaxHp() <= 0) {// 沒有HP設置
			return false;
		}

		if (getCurrentHp() <= 0) {// 目前HP小於0
			return false;
		}

		if (getCurrentHp() >= getMaxHp()) {// HP已滿
			return false;
		}
		return true;
	}

	protected boolean _hprRunning = false;// HP自然回復

	/**
	 * HP自然回復
	 */
	public final void startHpRegeneration() {
		if (destroyed() && isDead()) {
			return;
		}
		if (getCurrentHp() <= 0) {
			return;
		}
		if (!_hprRunning) {
			_hprRunning = true;
		}
	}

	/**
	 * HP是否允許回復
	 * 
	 * @return
	 */
	public final boolean isHpRegenerationX() {
		return _hprRunning;
	}

	/**
	 * 暫停HP回復
	 */
	public final void stopHpRegeneration() {
		if (_hprRunning) {
			_hprRunning = false;
		}
	}

	/**
	 * MP是否具備回復條件
	 * 
	 * @return
	 */
	public final boolean isMpR() {
		if (destroyed()) {// 進行刪除
			return false;
		}

		if (isDead()) {// 死亡
			return false;
		}

		if (getMaxHp() <= 0) {// 沒有HP設置
			return false;
		}

		if (getMaxMp() <= 0) {// 沒有MP設置
			return false;
		}

		if (getCurrentHp() <= 0) {// 目前HP小於0
			return false;
		}

		if (getCurrentMp() >= getMaxMp()) {// MP已滿
			return false;
		}
		return true;
	}

	private boolean _mprRunning = false;// MP自然回復

	/**
	 * MP自然回復
	 */
	public final void startMpRegeneration() {
		if (destroyed() && isDead()) {
			return;
		}
		if (getCurrentMp() <= 0) {
			return;
		}
		if (!_mprRunning) {
			_mprRunning = true;
		}
	}

	/**
	 * MP是否允許回復
	 * 
	 * @return
	 */
	public final boolean isMpRegenerationX() {
		return _mprRunning;
	}

	/**
	 * 暫停MP回復
	 */
	public final void stopMpRegeneration() {
		if (_mprRunning) {
			_mprRunning = false;
		}
	}

	protected NpcMoveExecutor _npcMove = null;// XXX

	public L1NpcInstance(final L1Npc template) {
		setStatus(0);
		setMoveSpeed(0);
		setDead(false);
		setStatus(0);
		setreSpawn(false);
		_npcMove = new NpcMove(this);

		if (template != null) {
			setting_template(template);
		}
	}

	/**
	 * TODO NPC召喚後 各項數值初始化
	 * 
	 * @param template
	 */
	public void setting_template(final L1Npc template) {
		_npcTemplate = template;
		int randomlevel = 0;
		double rate = 0;
		double diff = 0;

		setName(template.get_name());

		final String name = newName(template.get_npcId()) + template.get_nameid();
		setNameId(name);

		if (template.get_randomlevel() == 0) { // ランダムLv指定なし
			setLevel(template.get_level());

		} else { // ランダムLv指定あり（最小値:get_level(),最大値:get_randomlevel()）
			randomlevel = _random.nextInt((template.get_randomlevel() - template.get_level()) + 1);
			diff = template.get_randomlevel() - template.get_level();
			rate = randomlevel / diff;
			randomlevel += template.get_level();
			setLevel(randomlevel);
		}

		if (template.get_randomhp() == 0) {
			setMaxHp(template.get_hp());
			setCurrentHpDirect(template.get_hp());

		} else {
			final double randomhp = rate * (template.get_randomhp() - template.get_hp());
			final int hp = (int) (template.get_hp() + randomhp);
			setMaxHp(hp);
			setCurrentHpDirect(hp);
		}

		if (template.get_randommp() == 0) {
			setMaxMp(template.get_mp());
			setCurrentMpDirect(template.get_mp());

		} else {
			final double randommp = rate * (template.get_randommp() - template.get_mp());
			final int mp = (int) (template.get_mp() + randommp);
			setMaxMp(mp);
			setCurrentMpDirect(mp);
		}

		if (template.get_randomac() == 0) {
			setAc(template.get_ac());

		} else {
			final double randomac = rate * (template.get_randomac() - template.get_ac());
			final int ac = (int) (template.get_ac() + randomac);
			setAc(ac);
		}

		if (template.get_randomlevel() == 0) {
			setStr(template.get_str());
			setCon(template.get_con());
			setDex(template.get_dex());
			setInt(template.get_int());
			setWis(template.get_wis());
			setMr(template.get_mr());

		} else {
			setStr((byte) Math.min(template.get_str() + diff, 127));
			setCon((byte) Math.min(template.get_con() + diff, 127));
			setDex((byte) Math.min(template.get_dex() + diff, 127));
			setInt((byte) Math.min(template.get_int() + diff, 127));
			setWis((byte) Math.min(template.get_wis() + diff, 127));
			setMr((byte) Math.min(template.get_mr() + diff, 127));

			addHitup((int) diff * 2);
			addDmgup((int) diff * 2);
		}
		setPassispeed(template.get_passispeed());
		setAtkspeed(template.get_atkspeed());

		setAgro(template.is_agro());

		setAgrocoi(template.is_agrocoi());
		setAgrososc(template.is_agrososc());

		if (getNpcTemplate().get_weakAttr() != 0) {
			switch (getNpcTemplate().get_weakAttr()) {
			case 1:// 地
				addEarth(-50);
				break;

			case 2:// 火
				addFire(-50);
				break;

			case 4:// 水
				addWater(-50);
				break;

			case 8:// 風
				addWind(-50);
				break;

			case -1:// 抗地
				addEarth(50);
				break;

			case -2:// 抗火
				addFire(50);
				break;

			case -4:// 抗水
				addWater(50);
				break;

			case -8:// 抗風
				addWind(50);
				break;
			}
		}

		// 設置多外型
		final int gfxid = newGfx(template.get_gfxid());
		setTempCharGfx(gfxid);
		setGfxId(gfxid);

		// 特定外型改變初始型態
		setGfxidInStatus(gfxid);

		if (template.get_randomexp() == 0) {
			setExp(template.get_exp());

		} else {
			// final int level = this.getLevel();
			final int exp = template.get_exp() + _random.nextInt(template.get_randomexp());
			// exp += 1;
			setExp(exp);
		}

		if (template.get_randomlawful() == 0) {
			setLawful(template.get_lawful());
			setTempLawful(template.get_lawful());

		} else {
			final double randomlawful = rate * (template.get_randomlawful() - template.get_lawful());
			final int lawful = (int) (template.get_lawful() + randomlawful);
			setLawful(lawful);
			setTempLawful(lawful);
		}

		setPickupItem(template.is_picupitem());
		if (template.is_bravespeed()) {
			setBraveSpeed(1);
		} else {
			setBraveSpeed(0);
		}

		setKarma(template.getKarma());
		setLightSize(template.getLightSize());

		if (template.talk()) {// NPC對話判斷
			TALK = template.getNpcExecutor();
		}
		if (template.action()) {// NPC對話執行
			ACTION = template.getNpcExecutor();
		}
		if (template.attack()) {// NPC受到攻擊
			ATTACK = template.getNpcExecutor();
		}
		if (template.death()) {// NPC死亡
			DEATH = template.getNpcExecutor();
		}
		if (template.work()) {// NPC工作時間
			WORK = template.getNpcExecutor();
			if (WORK.workTime() != 0) {
				// 加入NPC工作列隊
				NpcWorkTimer.put(this, WORK.workTime());

			} else {// 工作時間設置為0
				// 執行一次
				WORK.work(this);
			}
		}
		if (template.spawn()) {// NPC召喚
			SPAWN = template.getNpcExecutor();
			SPAWN.spawn(this);
		}
		_mobSkill = new L1MobSkillUse(this);

		final L1MobSkillGroup mobSkillGroup = MobSkillGroupTable.get().getTemplate(getNpcId());
		if (mobSkillGroup != null) {
			_mobSkillGroup = new L1MobSkillGroupUse(this, mobSkillGroup);
			return;
		}
	}

	/**
	 * 特定外型改變初始型態
	 * 
	 * @param gfxid
	 */
	public void setGfxidInStatus(final int gfxid) {
		switch (gfxid) {
		case 51:// 持槍警衛
		case 147:// 持槍警衛
		case 110:// 黑騎士
			setStatus(ActionCodes.ACTION_SpearWalk);
			break;

		/*
		 * case 892:// 潘 case 893:// 潘 setStatus(ActionCodes.ACTION_StaffWalk);
		 * break;
		 */

		case 57:// 妖魔弓箭手
		case 816:// 妖魔弓箭手(塔)
		case 3137:// 警衛
		case 3140:// 警衛
		case 3145:// 弓
		case 3148:// 弓
		case 3151:// 弓
		case 7621:// 阿利歐克 的 守門人
			setStatus(ActionCodes.ACTION_BowWalk);
			break;

		case 111:// 克特
			setStatus(ActionCodes.ACTION_SwordWalk);
			break;
		}
	}

	/**
	 * 名稱顏色改變
	 * 
	 * @param npcid
	 * @return
	 */
	private String newName(final int npcid) {
		final String color = "";
		return color;
	}

	/**
	 * 取回多外型
	 * 
	 * @param get_gfxid
	 * @return
	 */
	private int newGfx(int get_gfxid) {
		int[] r = null;
		int newgfx;
		switch (get_gfxid) {
		// 兔子
		case 998:
		case 999:
		case 1002:
		case 1003:
			r = new int[] { 998, 999, 1002, 1003 };
			break;

		// 蛇女
		case 1597:
		case 1600:
			r = new int[] { 1597, 1600 };
			break;

		case 5942:// 象牙塔 助手
			r = new int[] { 5942, 5135, 5137, 5139, 5141, 5143, 5145, 5156, 5158, 5160, 5162 };
			break;

		case 1318:// 鬼魂(藍)
		case 1321:// 鬼魂(紅)
			r = new int[] { 1318, 1321 };
			break;
		}
		if (r != null) {
			newgfx = _random.nextInt(r.length);
			get_gfxid = r[newgfx];
		}
		return get_gfxid;
	}

	private int _passispeed;// 移動速度

	/**
	 * 移動速度
	 * 
	 * @return
	 */
	public int getPassispeed() {
		return _passispeed;
	}

	/**
	 * 移動速度
	 * 
	 * @param i
	 */
	public void setPassispeed(final int i) {
		_passispeed = i;
	}

	private int _atkspeed;// 攻擊速度

	/**
	 * 攻擊速度
	 * 
	 * @return
	 */
	public int getAtkspeed() {
		return _atkspeed;
	}

	/**
	 * 攻擊速度
	 * 
	 * @param i
	 */
	public void setAtkspeed(final int i) {
		_atkspeed = i;
	}

	private boolean _pickupItem;

	public boolean isPickupItem() {
		return _pickupItem;
	}

	public void setPickupItem(final boolean flag) {
		_pickupItem = flag;
	}

	@Override
	public L1Inventory getInventory() {
		return _inventory;
	}

	public void setInventory(final L1Inventory inventory) {
		_inventory = inventory;
	}

	public L1Npc getNpcTemplate() {
		return _npcTemplate;
	}

	public int getNpcId() {
		return _npcTemplate.get_npcId();
	}

	public void setPetcost(final int i) {
		_petcost = i;
	}

	public int getPetcost() {
		return _petcost;
	}

	public void setSpawn(final L1Spawn spawn) {
		_spawn = spawn;
	}

	public L1Spawn getSpawn() {
		return _spawn;
	}

	public void setSpawnNumber(final int number) {
		_spawnNumber = number;
	}

	public int getSpawnNumber() {
		return _spawnNumber;
	}

	/**
	 * 世界物件編號是否再利用
	 * 
	 * @param isReuseId
	 */
	public void onDecay(final boolean isReuseId) {
		int id = 0;
		if (isReuseId) {
			id = getId();

		} else {
			id = 0;
		}
		_spawn.executeSpawnTask(_spawnNumber, id);
	}

	/**
	 * 接觸資訊
	 */
	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		try {
			perceivedFrom.addKnownObject(this);
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * NPC物件刪除的處理
	 */
	public void deleteMe() {
		if (_destroyed) {
			return;
		}

		int tgid = 0;
		switch (getNpcId()) {
		case 92000:// 杰弗雷庫(雌)
			tgid = 92001;
			break;

		case 92001:// 杰弗雷庫(雄)
			tgid = 92000;
			break;
		}

		// 移出分身數據
		if (this instanceof L1IllusoryInstance) {
			if ((getMaster() != null) && (getMaster() instanceof L1PcInstance)) {
				final L1PcInstance masterPc = (L1PcInstance) getMaster();
				if (masterPc != null) {
					masterPc.get_otherList().removeIllusoryList(getId());
				}
			}
		}

		L1NpcInstance tgMob = null;
		if (tgid != 0) {
			// 可見範圍物件
			final ArrayList<L1Object> list = World.get().getVisibleObjects(this);
			for (final L1Object objects : list) {
				if (objects instanceof L1NpcInstance) {
					final L1NpcInstance tgMobR = (L1NpcInstance) objects;

					if (tgMobR.getNpcId() != tgid) {
						continue;
					}

					// 物件為指定物件 並且未死亡
					if (!tgMobR.isDead()) {
						// 復活
						resurrect(getMaxHp());
						return;

					} else {
						if (!tgMobR._destroyed) {// 未進行刪除
							tgMob = tgMobR;
							break;
						}
					}
				}
			}
		}

		_destroyed = true;// 進行刪除

		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;

		final int showid = get_showId();

		// 副本編號 是執行中副本
		if (WorldQuest.get().isQuest(showid)) {
			// 移出副本
			WorldQuest.get().remove(showid, this);
		}

		// 取回娃娃
		if (!getDolls().isEmpty()) {
			for (final Object obj : getDolls().values().toArray()) {
				final L1DollInstance doll = (L1DollInstance) obj;
				if (doll != null) {
					doll.deleteDoll();
				}
			}
		}

		if (!getPetList().isEmpty()) {
			for (final Object obj : getPetList().values().toArray()) {
				final L1NpcInstance summon = (L1NpcInstance) obj;
				if (summon != null) {
					if (summon instanceof L1SummonInstance) {
						final L1SummonInstance su = (L1SummonInstance) summon;
						su.deleteMe();
					}
				}
			}
		}

		// 清空清單
		_hateList.clear();
		_dropHateList.clear();
		_targetItemList.clear();
		_del_map.clear();

		// 移出世界
		World.get().removeVisibleObject(this);
		World.get().removeObject(this);

		// 畫面內可見範圍物件取回
		final List<L1PcInstance> players = World.get().getRecognizePlayer(this);
		if (players.size() > 0) {
			for (final L1PcInstance tgpc : players) {
				if (tgpc != null) {
					tgpc.removeKnownObject(this);
					// 移除顯示
					tgpc.sendPackets(new S_RemoveObject(this));
				}
			}
		}

		// 認識消除
		removeAllKnownObjects();

		if (tgMob != null) {
			tgMob.deleteMe();
		}

		try {
			// BOSS召喚列表中物件(NPCID)
			if (getNpcTemplate().is_boss()) {
				if (_spawn.get_nextSpawnTime() != null) {
					// 取得下次召喚時間差異
					final long newTime = (_spawn.get_spawnInterval() * 60 * 1000);
					final Calendar cals = Calendar.getInstance();
					cals.setTimeInMillis(System.currentTimeMillis() + newTime);

					// 設定下次出現時間
					_spawn.get_nextSpawnTime().setTimeInMillis(cals.getTimeInMillis());
					// 更新資料庫 下次召喚時間紀錄
					SpawnBossReading.get().upDateNextSpawnTime(_spawn.getId(), cals);

					// 公告狀態
					switch (_spawn.getHeading()) {
					case 0:// 面相基礎設置為0公告死亡及下次出現時間
						final Date spawnTime = _spawn.get_nextSpawnTime().getTime();
						final String nextTime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(spawnTime);
						// 9:%s 已經死亡，下次出現時間：%s。
						World.get().broadcastPacketToAll(
								new S_ServerMessage(getNameId() + "已經死亡，下次出現時間" + nextTime));
						break;

					case 1:// 面相基礎設置為1公告死亡
							// 8:%s 已經死亡!
						World.get().broadcastPacketToAll(new S_ServerMessage(getNameId() + "已經死亡!"));
						break;

					default:
						break;
					}
				}
			}

		} catch (final Exception e) {
			// 手動召喚BOSS 因為不具備召喚時間數據
			// 因此忽略此錯誤訊息
			// _log.error(e.getLocalizedMessage(), e);
		}

		// 是否具有隊伍
		final L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
		if (mobGroupInfo == null) {
			/*
			 * if (this.isReSpawn()) { this.onDecay(true); }
			 */

		} else {
			// 剩餘隊員數量為0
			if (mobGroupInfo.removeMember(this) == 0) {
				setMobGroupInfo(null);
				/*
				 * if (this.isReSpawn()) { this.onDecay(false); }
				 */
			}
		}

		if (isReSpawn()) {
			_spawn.executeSpawnTask(_spawnNumber, 0);
		}
	}

	/**
	 * 刪除隊員
	 * 
	 * @param mob
	 */
	public void outParty(final L1NpcInstance mob) {
		// 刪除隊員
		final Collection<L1NpcInstance> list = WorldNpc.get().all();
		for (final L1NpcInstance tgNpc : list) {
			if (tgNpc.isDead()) {
				continue;
			}

			if (tgNpc.getMaster() == null) {
				continue;
			}

			if (tgNpc.getMaster().equals(mob)) {
				// 產生動畫
				tgNpc.broadcastPacketAll(new S_SkillSound(tgNpc.getId(), 2236));
				tgNpc.deleteMe();
			}
		}
	}

	/**
	 * 受攻擊mp減少計算
	 * 
	 * @param attacker
	 * @param damageMp
	 */
	public void ReceiveManaDamage(final L1Character attacker, final int damageMp) {
	}

	/**
	 * 受攻擊hp減少計算
	 * 
	 * @param attacker
	 * @param damage
	 */
	public void receiveDamage(final L1Character attacker, final int damage) {
	}

	// 刪除物件計時器
	public class DelItemTime {
		public int _del_item_time = 0;// 單位:秒
	}

	/**
	 * 加入NPC消化道具時間清單
	 * 
	 * @param item
	 */
	public void setDigestItem(final L1ItemInstance item) {
		final DelItemTime delItemTime = new DelItemTime();
		delItemTime._del_item_time = getNpcTemplate().get_digestitem();
		_del_map.put(item, delItemTime);
		// NpcDigestItemTimer.put(this, item, new
		// Integer(this.getNpcTemplate().get_digestitem()));
	}

	/**
	 * NPC消化道具時間清單
	 * 
	 * @return
	 */
	public HashMap<L1ItemInstance, DelItemTime> getDigestItem() {
		return _del_map;
	}

	/**
	 * 清空NPC消化道具時間清單
	 */
	public void getDigestItemClear() {
		_del_map.clear();
	}

	/**
	 * NPC消化道具時間清單內無物件
	 * 
	 * @return true:無物件 false:有物件
	 */
	public boolean getDigestItemEmpty() {
		return _del_map.isEmpty();
	}

	public void onGetItem(final L1ItemInstance item) {
		refineItem();
		getInventory().shuffle();
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
	}

	public void approachPlayer(final L1PcInstance pc) {
		if (pc.hasSkillEffect(INVISIBILITY) || pc.hasSkillEffect(BLIND_HIDING)) { // インビジビリティ、ブラインドハイディング中
			return;
		}
		switch (getHiddenStatus()) {
		case HIDDEN_STATUS_SINK:
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(getLocation()) <= 2) {
					appearOnGround(pc);
				}
			}
			break;

		case HIDDEN_STATUS_FLY:
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(getLocation()) <= 1) {
					appearOnGround(pc);
				}
			} else {
				searchItemFromAir();
			}
			break;

		case HIDDEN_STATUS_ICE:
			if (getCurrentHp() < getMaxHp()) {
				appearOnGround(pc);
			}
			break;

		case HIDDEN_STATUS_STANDBY:
			if (getCurrentHp() >= getMaxHp()) {
				if (pc.getLocation().getTileDistance(getLocation()) < 17) {
					appearOnGround(pc);
				}
			}
			break;
		}
	}

	public void appearOnGround(final L1PcInstance pc) {
		switch (getHiddenStatus()) {
		case HIDDEN_STATUS_SINK:
		case HIDDEN_STATUS_ICE:
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
			setStatus(0);
			broadcastPacketAll(new S_NPCPack(this));
			if (!pc.hasSkillEffect(INVISIBILITY) && !pc.hasSkillEffect(BLIND_HIDING) && !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // モンスターのＡＩを開始
			break;

		case HIDDEN_STATUS_FLY:
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
			setStatus(0);
			broadcastPacketAll(new S_NPCPack(this));
			if (!pc.hasSkillEffect(INVISIBILITY) && !pc.hasSkillEffect(BLIND_HIDING) && !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // モンスターのＡＩを開始
			startChat(CHAT_TIMING_HIDE);
			break;

		case HIDDEN_STATUS_STANDBY:
			if (getPassispeed() != 0) {
				setPassispeed(0);

				setHiddenStatus(HIDDEN_STATUS_NONE);
				broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
				setStatus(4);
				broadcastPacketAll(new S_NPCPack(this));
				if (!pc.isGm() && (!pc.isInvisble() || getNpcTemplate().is_agrocoi())) {
					_hateList.add(pc, 0);
					_target = pc;
				}
				// onNpcAI(); // モンスターのＡＩを開始
			}
			break;
		}
	}

	public void hideOnGround() {
		switch (getGfxId()) {
		case 10071:
			if (getPassispeed() == 0) {
				setPassispeed(getNpcTemplate().get_passispeed());
				allTargetClear();
				setHiddenStatus(HIDDEN_STATUS_STANDBY);
				broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
				setStatus(0);
				broadcastPacketAll(new S_NPCPack(this));
			}
			break;
		}
	}

	/**
	 * 現在目標
	 */
	public L1Character is_now_target() {
		return _target;
	}

	/**
	 * 現在目標(物品)
	 */
	public L1ItemInstance is_now_targetItem() {
		return _targetItem;
	}

	/**
	 * 現在目標(物品)
	 */
	public void set_now_targetItem(final L1ItemInstance item) {
		_targetItem = item;
	}

	// 移動關聯

	/**
	 * 移動數據
	 */
	public NpcMoveExecutor getMove() {
		return _npcMove;
	}

	// ■■■■■■■■■■■■ アイテム関連 ■■■■■■■■■■

	private void useHealPotion(int healHp, final int effectId) {
		broadcastPacketAll(new S_SkillSound(getId(), effectId));
		if (hasSkillEffect(POLLUTE_WATER)) { // ポルートウォーター中は回復量1/2倍
			healHp = (healHp >> 1);
		}
		if (hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
			healHp = (healHp >> 1);
		}
		if (hasSkillEffect(ADLV80_2_1)) {
			healHp *= -1;
		}
		if (this instanceof L1PetInstance) {
			((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);

		} else if (this instanceof L1SummonInstance) {
			((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);

		} else {
			setCurrentHpDirect(getCurrentHp() + healHp);
		}
	}

	private void useHastePotion(final int time) {
		broadcastPacketAll(new S_SkillHaste(getId(), 1, time));
		broadcastPacketX8(new S_SkillSound(getId(), 191));
		setMoveSpeed(1);
		setSkillEffect(STATUS_HASTE, time * 1000);
	}

	// アイテムの使用判定及び使用
	public static final int USEITEM_HEAL = 0;
	public static final int USEITEM_HASTE = 1;
	public static int[] healPotions = { POTION_OF_GREATER_HEALING, POTION_OF_EXTRA_HEALING,
			POTION_OF_HEALING };
	public static int[] haestPotions = { B_POTION_OF_GREATER_HASTE_SELF, POTION_OF_GREATER_HASTE_SELF,
			B_POTION_OF_HASTE_SELF, POTION_OF_HASTE_SELF };

	public void useItem(final int type, final int chance) { // 使用的物品種類
															// 以及使用機率(1/100)
		if (is_decay_potion()) {
			return; // 藥水霜化狀態
		}
		if (_random.nextInt(100) > chance) {
			return;
		}
		if (getInventory() == null) {
			return;
		}
		switch (type) {
		case USEITEM_HEAL:// 回復系ポーション
			// 回復量の大きい順
			if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
				useHealPotion(75, 197);

			} else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
				useHealPotion(45, 194);

			} else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
				useHealPotion(15, 189);
			}
			break;

		case USEITEM_HASTE:// ヘイスト系ポーション
			if (hasSkillEffect(STATUS_HASTE)) {
				return; // ヘイスト状態チェック
			}

			// 効果の長い順
			if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
				useHastePotion(2100);

			} else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF, 1)) {
				useHastePotion(1800);

			} else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(350);

			} else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(300);
			}
			break;
		}
	}

	/**
	 * 往指定面向設定座標
	 * 
	 * @param dir
	 */
	public void setDirectionMoveSrc(final int dir) {
		if ((dir >= 0) && (dir <= 7)) {
			final int locx = getX() + HEADING_TABLE_X[dir];
			final int locy = getY() + HEADING_TABLE_Y[dir];

			setHeading(dir);

			setX(locx);
			setY(locy);
		}
	}

	/**
	 * 目標鄰近位置指定傳送
	 * 
	 * @param nx
	 * @param ny
	 * @return
	 */
	public boolean nearTeleport(int nx, int ny) {
		final int rdir = _random.nextInt(8);
		int dir;
		for (int i = 0; i < 8; i++) {
			dir = rdir + i;
			if (dir > 7) {
				dir -= 8;
			}
			nx += HEADING_TABLE_X[dir];
			;
			ny += HEADING_TABLE_Y[dir];

			if (getMap().isPassable(nx, ny, this)) {
				dir += 4;
				if (dir > 7) {
					dir -= 8;
				}
				teleport(nx, ny, dir);
				setCurrentMp(getCurrentMp() - 10);
				return true;
			}
		}
		return false;
	}

	/**
	 * 目標位置指定標傳送
	 * 
	 * @param nx
	 * @param ny
	 * @param dir
	 */
	public void teleport(final int nx, final int ny, final int dir) {
		try {
			for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
				pc.sendPackets(new S_SkillSound(getId(), 169));
				pc.sendPackets(new S_RemoveObject(this));
				pc.removeKnownObject(this);
			}
			setX(nx);
			setY(ny);
			setHeading(dir);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// ----------From L1Character-------------
	private String _nameId; // ● ネームＩＤ

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(final String s) {
		_nameId = s;
	}

	private boolean _Agro; // ● アクティブか

	public boolean isAgro() {
		return _Agro;
	}

	public void setAgro(final boolean flag) {
		_Agro = flag;
	}

	private boolean _Agrocoi; // ● インビジアクティブか

	public boolean isAgrocoi() {
		return _Agrocoi;
	}

	public void setAgrocoi(final boolean flag) {
		_Agrocoi = flag;
	}

	private boolean _Agrososc; // ● 変身アクティブか

	public boolean isAgrososc() {
		return _Agrososc;
	}

	public void setAgrososc(final boolean flag) {
		_Agrososc = flag;
	}

	private int _homeX; // ● ホームポイントＸ（モンスターの戻る位置とかペットの警戒位置）

	public int getHomeX() {
		return _homeX;
	}

	public void setHomeX(final int i) {
		_homeX = i;
	}

	private int _homeY; // ● ホームポイントＹ（モンスターの戻る位置とかペットの警戒位置）

	public int getHomeY() {
		return _homeY;
	}

	public void setHomeY(final int i) {
		_homeY = i;
	}

	private boolean _reSpawn; // 死亡後是否再召喚

	/**
	 * 死亡後是否再召喚
	 * 
	 * @return
	 */
	public boolean isReSpawn() {
		return _reSpawn;
	}

	/**
	 * 死亡後是否再召喚
	 * 
	 * @param flag
	 */
	public void setreSpawn(final boolean flag) {
		_reSpawn = flag;
	}

	private int _lightSize; // ● ライト ０．なし １〜１４．大きさ

	public int getLightSize() {
		return _lightSize;
	}

	public void setLightSize(final int i) {
		_lightSize = i;
	}

	private boolean _weaponBreaked; // 受到壞物類型魔法(降低攻擊力)

	/**
	 * 受到壞物類型魔法(降低攻擊力)
	 * 
	 * @return
	 */
	public boolean isWeaponBreaked() {
		return _weaponBreaked;
	}

	/**
	 * 受到壞物類型魔法(降低攻擊力)
	 * 
	 * @param flag
	 */
	public void setWeaponBreaked(final boolean flag) {
		_weaponBreaked = flag;
	}

	private int _hiddenStatus; // ● 地中に潜ったり、空を飛んでいる状態

	public int getHiddenStatus() {
		return _hiddenStatus;
	}

	public void setHiddenStatus(final int i) {
		_hiddenStatus = i;
	}

	// 行動距離
	private int _movementDistance = 0;

	/**
	 * 行動距離
	 * 
	 * @return
	 */
	public int getMovementDistance() {
		return _movementDistance;
	}

	/**
	 * 行動距離
	 * 
	 * @param i
	 */
	public void setMovementDistance(final int i) {
		_movementDistance = i;
	}

	// 表示用ロウフル
	private int _tempLawful = 0;

	public int getTempLawful() {
		return _tempLawful;
	}

	public void setTempLawful(final int i) {
		_tempLawful = i;
	}

	/**
	 * NPC速計算
	 * 
	 * @param sleepTime 原始數據
	 * @param type 0:通常 1:加速 2:減速
	 * @return 應該延遲的毫秒
	 */
	public int calcSleepTime(int sleepTime, final int type) {
		if (sleepTime <= 0) {
			sleepTime = 960;
		}
		switch (getMoveSpeed()) {
		case 0: // 通常
			break;

		case 1: // 加速
			sleepTime -= (sleepTime * 0.25);
			break;

		case 2: // 減速
			sleepTime *= 2;
			break;
		}
		if (getBraveSpeed() == 1) {// 具有勇水狀態
			sleepTime -= (sleepTime * 0.25);
		}
		if (hasSkillEffect(WIND_SHACKLE)) {
			if ((type == ATTACK_SPEED) || (type == MAGIC_SPEED)) {
				sleepTime += (sleepTime * 0.25);
			}
		}
		return sleepTime;
	}

	/**
	 * NPC AI時間軸 正在運行
	 * 
	 * @param aiRunning
	 */
	protected void setAiRunning(final boolean aiRunning) {
		_aiRunning = aiRunning;
	}

	/**
	 * NPC AI時間軸 正在運行
	 * 
	 * @return
	 */
	protected boolean isAiRunning() {
		return _aiRunning;
	}

	/**
	 * 進行刪除
	 * 
	 * @return true:進行刪除 false:無
	 */
	public boolean destroyed() {
		return _destroyed;
	}

	protected L1MobSkillUse mobSkill() {
		return _mobSkill;
	}

	/**
	 * NPC已經激活
	 * 
	 * @param actived true:激活 false:無
	 */
	protected void setActived(final boolean actived) {
		_actived = actived;
	}

	/**
	 * NPC已經激活
	 * 
	 * @return true:激活 false:無
	 */
	protected boolean isActived() {
		return _actived;
	}

	protected void setFirstAttack(final boolean firstAttack) {
		_firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return _firstAttack;
	}

	public void setSleepTime(final int sleep_time) {
		_sleep_time = sleep_time;
	}

	protected int getSleepTime() {
		return _sleep_time;
	}

	/**
	 * 死亡處理中
	 * 
	 * @param deathProcessing
	 */
	protected void setDeathProcessing(final boolean deathProcessing) {
		_deathProcessing = deathProcessing;
	}

	/**
	 * 死亡處理中
	 * 
	 * @return
	 */
	protected boolean isDeathProcessing() {
		return _deathProcessing;
	}

	// 已經被吸取的MP數量
	private int _drainedMana = 0;

	/**
	 * 被吸取MP
	 * 
	 * @param drain
	 * @return
	 */
	public int drainMana(final int drain) {
		if (_drainedMana >= 40) {
			return 0;
		}
		int result = Math.min(drain, getCurrentMp());
		if ((_drainedMana + result) > 40) {
			result = 40 - _drainedMana;
		}
		_drainedMana += result;
		return result;
	}

	public final void npcSleepTime(final int paramInt1, final int paramInt2) {
		setSleepTime(calcSleepTime(SprTable.get().getMoveSpeed(getTempCharGfx(), paramInt1), paramInt2));
	}

	public boolean _destroyed = false; // 進行刪除

	// ※破棄後に動かないよう強制的にＡＩ等のスレッド処理中止（念のため）

	/**
	 * NPC死亡變身的處理
	 */
	protected void transform(final int transformId) {
		stopHpRegeneration();
		stopMpRegeneration();
		final int transformGfxId = getNpcTemplate().getTransformGfxId();
		if (transformGfxId != 0) {
			broadcastPacketAll(new S_SkillSound(getId(), transformGfxId));
		}
		final L1Npc npcTemplate = NpcTable.get().getTemplate(transformId);
		setting_template(npcTemplate);

		broadcastPacketAll(new S_ChangeShape(this, getTempCharGfx()));
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			// 副本ID不相等 不相護顯示
			if (pc.get_showId() != get_showId()) {
				continue;
			}
			onPerceive(pc);
		}

	}

	// 設置 NPC 動作暫停
	private boolean _rest = false;

	/**
	 * 設置 NPC 動作暫停
	 * 
	 * @param rest
	 */
	public void setRest(final boolean rest) {
		_rest = rest;
	}

	/**
	 * NPC 動作暫停
	 * 
	 * @return
	 */
	public boolean isRest() {
		return _rest;
	}

	private boolean _isResurrect = false;// 是復活過的

	/**
	 * 是復活過的
	 * 
	 * @return
	 */
	public boolean isResurrect() {
		return _isResurrect;
	}

	/**
	 * 是復活過的
	 * 
	 * @param flag
	 */
	public void setResurrect(final boolean flag) {
		_isResurrect = flag;
	}

	/**
	 * 復活處理
	 */
	@Override
	public synchronized void resurrect(final int hp) {
		if (_destroyed) {
			return;
		}
		super.resurrect(hp);

		startHpRegeneration();
		startMpRegeneration();

		broadcastPacketAll(new S_Resurrection(this, this, 0));

		// キャンセレーションをエフェクトなしでかける
		// 本来は死亡時に行うべきだが、負荷が大きくなるため復活時に行う
		final L1SkillUse skill = new L1SkillUse();
		skill.handleCommands(null, CANCELLATION, getId(), getX(), getY(), 0, L1SkillUse.TYPE_LOGIN, this);

		// 移出死亡清單
		if (_deadTimerTemp != -1) {
			_deadTimerTemp = -1;
		}
	}

	/**
	 * 加入NPC死亡清單
	 * 
	 * @param deltime 刪除時間(秒)
	 */
	protected synchronized void startDeleteTimer(final int deltime) {
		if (_deadTimerTemp != -1) {
			return;
		}
		_deadTimerTemp = deltime;
	}

	private L1MobGroupInfo _mobGroupInfo = null;// NPC隊伍資訊

	/**
	 * 是否在隊伍中
	 * 
	 * @return
	 */
	public boolean isInMobGroup() {
		return getMobGroupInfo() != null;
	}

	/**
	 * NPC隊伍資訊
	 * 
	 * @return
	 */
	public L1MobGroupInfo getMobGroupInfo() {
		return _mobGroupInfo;
	}

	/**
	 * NPC隊伍資訊
	 * 
	 * @param m
	 */
	public void setMobGroupInfo(final L1MobGroupInfo m) {
		_mobGroupInfo = m;
	}

	private int _mobGroupId = 0;// NPC隊伍 隊長NPCID

	/**
	 * NPC隊伍 隊長NPCID
	 * 
	 * @return
	 */
	public int getMobGroupId() {
		return _mobGroupId;
	}

	/**
	 * NPC隊伍 隊長NPCID
	 * 
	 * @param i
	 */
	public void setMobGroupId(final int i) {
		_mobGroupId = i;
	}

	/**
	 * NPC出現的對話
	 * 
	 * @param chatTiming
	 */
	public void startChat(final int chatTiming) {
		// 出現時のチャットにも関わらず死亡中、死亡時のチャットにも関わらず生存中
		if ((chatTiming == CHAT_TIMING_APPEARANCE) && isDead()) {
			return;
		}
		if ((chatTiming == CHAT_TIMING_DEAD) && !isDead()) {
			return;
		}
		if ((chatTiming == CHAT_TIMING_HIDE) && isDead()) {
			return;
		}
		if ((chatTiming == CHAT_TIMING_GAME_TIME) && isDead()) {
			return;
		}

		final int npcId = getNpcTemplate().get_npcId();
		L1NpcChat npcChat = null;
		switch (chatTiming) {
		case CHAT_TIMING_APPEARANCE:// 出現
			npcChat = NpcChatTable.get().getTemplateAppearance(npcId);
			break;

		case CHAT_TIMING_DEAD:// 死亡
			npcChat = NpcChatTable.get().getTemplateDead(npcId);
			break;

		case CHAT_TIMING_HIDE:// 取消隱藏
			npcChat = NpcChatTable.get().getTemplateHide(npcId);
			break;

		case CHAT_TIMING_GAME_TIME:// 指定時間
			npcChat = NpcChatTable.get().getTemplateGameTime(npcId);
			break;
		}
		if (npcChat == null) {
			return;
		}

		final Timer timer = new Timer(true);
		final L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
		if (!npcChat.isRepeat()) {
			timer.schedule(npcChatTimer, npcChat.getStartDelayTime());

		} else {
			timer.scheduleAtFixedRate(npcChatTimer, npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
		}
	}

	private int _bowActId = -1;// 攻擊動畫

	/**
	 * 攻擊動畫
	 * 
	 * @return
	 */
	public int getBowActId() {
		if (_bowActId != -1) {
			return _bowActId;
		}
		return getNpcTemplate().getBowActId();
	}

	/**
	 * 攻擊動畫
	 * 
	 * @param bowActId
	 */
	public void setBowActId(final int bowActId) {
		_bowActId = bowActId;
	}

	private int _ranged = -1;// 攻擊距離

	/**
	 * 攻擊距離
	 * 
	 * @return
	 */
	public int get_ranged() {
		if (_ranged != -1) {
			return _ranged;
		}
		return getNpcTemplate().get_ranged();
	}

	/**
	 * 攻擊距離
	 * 
	 * @param ranged
	 */
	public void set_ranged(final int ranged) {
		_ranged = ranged;
	}

	private int _skyTime;

	public final int getSkyTime() {
		return _skyTime;
	}

	public final void setSkyTime(final int i) {
		_skyTime = i;
	}

	private int _quest_id;

	public int get_quest_id() {
		return _quest_id;
	}

	public void set_quest_id(final int quest_id) {
		_quest_id = quest_id;
	}

	private boolean _isShop = false;// 販賣狀態

	/**
	 * 是否在販賣狀態
	 * 
	 * @return
	 */
	public boolean isShop() {
		return _isShop;
	}

	/**
	 * 設置販賣狀態
	 * 
	 * @param isShop
	 */
	public void set_isShop(final boolean isShop) {
		if (this instanceof L1DeInstance) {
			final L1DeInstance de = (L1DeInstance) this;
			de.setShopChat(DeShopChatTable.get().getChat(), DeShopChatTable.get().getChat());
		}
		_isShop = isShop;
	}

	private int _spawnTime = 0;// 存在時間(秒)

	private boolean _isspawnTime = false;// 具有存在時間

	/**
	 * 設定存在時間(秒)
	 * 
	 * @param spawnTime
	 */
	public void set_spawnTime(final int spawnTime) {
		_spawnTime = spawnTime;
		_isspawnTime = true;
	}

	/**
	 * 傳回存在時間(秒)
	 * 
	 * @return
	 */
	public int get_spawnTime() {
		return _spawnTime;
	}

	/**
	 * 具有存在時間
	 * 
	 * @return
	 */
	public boolean is_spawnTime() {
		return _isspawnTime;
	}

	private boolean _isremovearmor = false;// 施展過移除裝備

	/**
	 * 施展過移除裝備
	 * 
	 * @return
	 */
	public boolean isremovearmor() {
		return _isremovearmor;
	}

	/**
	 * 施展過移除裝備
	 * 
	 * @param _isremovearmor
	 */
	public void set_removearmor(final boolean isremovearmor) {
		_isremovearmor = isremovearmor;
	}

	private int _deadTimerTemp = -1;// 死亡時間

	/**
	 * 死亡時間(秒)
	 * 
	 * @param time
	 */
	public void set_deadTimerTemp(final int time) {
		_deadTimerTemp = time;
	}

	/**
	 * 死亡時間(秒)
	 * 
	 * @return
	 */
	public int get_deadTimerTemp() {
		return _deadTimerTemp;
	}

	private int _stop_time = -1;// 動作暫停時間

	/**
	 * 動作暫停時間(秒)
	 * 
	 * @param time
	 */
	public void set_stop_time(final int time) {
		_stop_time = time;
	}

	/**
	 * 動作暫停時間(秒)
	 * 
	 * @return
	 */
	public int get_stop_time() {
		return _stop_time;
	}

	private int _work_time = -1;// 工作時間

	public void set_work_time(final int time) {
		_work_time = time;
	}

	public int get_work_time() {
		return _work_time;
	}

	private boolean _gripped;

	/**
	 * 是否為束縛狀態
	 * 
	 * @param gripped
	 */
	public void setGripped(final boolean gripped) {
		_gripped = gripped;
	}

	public boolean isGripped() {
		return _gripped;
	}
	
	// 暫存製造命令
		private String _craftkey = null;

		public void set_craftkey(final String craftkey) {
			_craftkey = craftkey;
		}

		public String get_craftkey() {
			return _craftkey;
		}
	
}
