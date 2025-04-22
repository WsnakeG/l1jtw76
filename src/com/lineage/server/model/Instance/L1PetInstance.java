package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.IdFactory;
import com.lineage.server.IdFactoryNpc;
import com.lineage.server.datatables.ExpTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.PetItemTable;
import com.lineage.server.datatables.PetTypeTable;
import com.lineage.server.datatables.lock.PetReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.model.L1Location;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_NPCPack_Pet;
import com.lineage.server.serverpackets.S_NpcChat;
import com.lineage.server.serverpackets.S_PetMenuPacket;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1Pet;
import com.lineage.server.templates.L1PetItem;
import com.lineage.server.templates.L1PetType;
import com.lineage.server.world.World;

/**
 * 寵物控制項
 * 
 * @author DaiEn
 */
public class L1PetInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1PetInstance.class);

	private static Random _random = new Random();

	/**
	 * 0:無<BR>
	 * 1:攻擊<BR>
	 * 2:防禦<BR>
	 * 3:休息<BR>
	 * 4:配置<BR>
	 * 5:警戒<BR>
	 * 6:解散<BR>
	 * 7:召回
	 */
	private int _currentPetStatus;

	private int _checkMove = 0;

	private final L1PcInstance _petMaster;

	private int _itemObjId;

	private L1PetType _type;

	private int _expPercent;

	// ターゲットがいない場合の処理
	@Override
	public boolean noTarget() {
		switch (_currentPetStatus) {
		case 3:// 休息
			return true;

		case 4:// 散開
			if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(_petMaster.getLocation()) < 5)) {
				if (_npcMove != null) {
					int dir = _npcMove.targetReverseDirection(_petMaster.getX(), _petMaster.getY());
					dir = _npcMove.checkObject(dir);
					_npcMove.setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
				}

			} else { // 主人を見失うか５マス以上はなれたら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			break;

		case 5:// 警戒
			if ((Math.abs(getHomeX() - getX()) > 1) || (Math.abs(getHomeY() - getY()) > 1)) {
				if (_npcMove != null) {
					final int dir = _npcMove.moveDirection(getHomeX(), getHomeY());
					if (dir == -1) { // ホームが離れすぎてたら現在地がホーム
						setHomeX(getX());
						setHomeY(getY());
					} else {
						_npcMove.setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
					}
				}
			}
			break;

		case 7:// 召回
			if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())
					&& (getLocation().getTileLineDistance(_petMaster.getLocation()) <= 1)) {
				_currentPetStatus = 3;
				return true;
			}
			if (_npcMove != null) {
				final int locx = _petMaster.getX() + _random.nextInt(1);
				final int locy = _petMaster.getY() + _random.nextInt(1);
				final int dirx = _npcMove.moveDirection(locx, locy);
				if (dirx == -1) { // 主人を見失うかはなれたらその場で休憩状態に
					_currentPetStatus = 3;
					return true;
				}
				_npcMove.setDirectionMove(dirx);
				setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
			}
			break;

		default:
			if ((_petMaster != null) && (_petMaster.getMapId() == getMapId())) { // ●
				// 主人を追尾
				if (getLocation().getTileLineDistance(_petMaster.getLocation()) > 2) {
					if (_npcMove != null) {
						final int dir = _npcMove.moveDirection(_petMaster.getX(), _petMaster.getY());
						if (dir == -1) { // 主人が離れすぎたら休憩状態に
							_checkMove++;
							if (_checkMove >= 10) {
								// 控制者遺失
								// System.out.println("控制者遺失次數: "+_checkMove);
								_checkMove = 0;
								_currentPetStatus = 3;
								return true;
							}

						} else {
							_checkMove = 0;
							_npcMove.setDirectionMove(dir);
							setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
						}
					}
				}

			} else { // ● 主人を見失ったら休憩状態に
				_currentPetStatus = 3;
				return true;
			}
			break;
		}
		return false;
	}

	/**
	 * GM用寵物
	 * 
	 * @param npc
	 * @param pc
	 */
	public L1PetInstance(final L1Npc template, final L1PcInstance master) {
		super(template);

		_petMaster = master;
		_itemObjId = -1;
		_type = null;

		setId(IdFactoryNpc.get().nextId());
		// 副本編號
		set_showId(master.get_showId());
		setName(template.get_name());
		setLevel(template.get_level());
		// HPMP最大質設置
		setMaxHp(template.get_hp());
		setCurrentHpDirect(template.get_hp());
		setMaxMp(template.get_mp());
		setCurrentMpDirect(template.get_mp());
		setExp(template.get_exp());
		setExpPercent(ExpTable.getExpPercentage(template.get_level(), template.get_exp()));
		setLawful(template.get_lawful());
		setTempLawful(template.get_lawful());

		setMaster(master);
		setX((master.getX() + _random.nextInt(5)) - 2);
		setY((master.getY() + _random.nextInt(5)) - 2);
		this.setMap(master.getMapId());
		setHeading(master.getHeading());
		setLightSize(template.getLightSize());

		_currentPetStatus = 3;

		World.get().storeObject(this);
		World.get().addVisibleObject(this);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
		// 增加物件組人
		addMaster(master);
		// master.sendPackets(new S_NewMaster(master.getName(), this));
	}

	/**
	 * 寵物領取
	 * 
	 * @param template NPC資料
	 * @param master 主人
	 * @param pet 寵物資料
	 */
	public L1PetInstance(final L1Npc template, final L1PcInstance master, final L1Pet pet) {
		super(template);

		_petMaster = master;
		_itemObjId = pet.get_itemobjid();
		_type = PetTypeTable.getInstance().get(template.get_npcId());

		// ステータスを上書き
		setId(pet.get_objid());
		// 副本編號
		set_showId(master.get_showId());
		setName(pet.get_name());
		setLevel(pet.get_level());
		// HPMPはMAXとする
		setMaxHp(pet.get_hp());
		setCurrentHpDirect(pet.get_hp());
		setMaxMp(pet.get_mp());
		setCurrentMpDirect(pet.get_mp());
		setExp(pet.get_exp());
		setExpPercent(ExpTable.getExpPercentage(pet.get_level(), pet.get_exp()));
		setLawful(pet.get_lawful());
		setTempLawful(pet.get_lawful());

		setMaster(master);
		setX((master.getX() + _random.nextInt(5)) - 2);
		setY((master.getY() + _random.nextInt(5)) - 2);
		this.setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());

		_currentPetStatus = 3;

		World.get().storeObject(this);
		World.get().addVisibleObject(this);

		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}
		master.addPet(this);
		// 增加物件組人
		addMaster(master);
		// master.sendPackets(new S_NewMaster(master.getName(), this));
	}

	/**
	 * 寵物抓取
	 * 
	 * @param target
	 * @param master 主人
	 * @param itemid 項圈OBJID
	 */
	public L1PetInstance(final L1NpcInstance target, final L1PcInstance master, final int itemid) {
		super(null);

		_petMaster = master;
		_itemObjId = itemid;
		_type = PetTypeTable.getInstance().get(target.getNpcTemplate().get_npcId());

		// ステータスを上書き
		setId(IdFactory.get().nextId());
		// 副本編號
		set_showId(master.get_showId());
		setting_template(target.getNpcTemplate());
		setCurrentHpDirect(target.getCurrentHp());
		setCurrentMpDirect(target.getCurrentMp());
		setExp(750); // Lv.5のEXP
		setExpPercent(0);
		setLawful(0);
		setTempLawful(0);

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		this.setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());
		setPetcost(6);
		setInventory(target.getInventory());
		target.setInventory(null);

		_currentPetStatus = 3;

		target.deleteMe();
		World.get().storeObject(this);
		World.get().addVisibleObject(this);

		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		master.addPet(this);
		PetReading.get().storeNewPet(target, getId(), itemid);
		// 增加物件組人
		addMaster(master);
		// master.sendPackets(new S_NewMaster(master.getName(), this));
	}

	/**
	 * 寵物取得
	 * 
	 * @param npcid 寵物npcid
	 * @param master 主人
	 * @param itemid 項圈OBJID
	 */
	public L1PetInstance(final int npcid, final L1PcInstance master, final int itemid) {
		super(null);

		_petMaster = master;
		_itemObjId = itemid;
		_type = PetTypeTable.getInstance().get(npcid);
		final L1Npc npc = NpcTable.get().getTemplate(npcid);

		// ステータスを上書き
		setId(IdFactory.get().nextId());
		// 副本編號
		set_showId(master.get_showId());

		setting_template(npc);
		setCurrentHpDirect(npc.get_hp());
		setCurrentMpDirect(npc.get_mp());

		long exp = 750;// 5
		if (npcid == 71020) {// 頑皮龍
			exp = 55810962;// 50
		}
		if (npcid == 71019) {// 淘氣龍
			exp = 560722250;// 64
		}
		setExp(exp); // EXP
		setLevel(ExpTable.getLevelByExp(exp));
		setExpPercent(0);
		setLawful(0);
		setTempLawful(0);

		setMaster(master);

		// 隨機周邊座標
		final L1Location loc = master.getLocation().randomLocation(5, false);
		setX(loc.getX());
		setY(loc.getY());
		this.setMap((short) loc.getMapId());
		setHeading(5);
		setLightSize(npc.getLightSize());
		setPetcost(6);

		_currentPetStatus = 3;

		World.get().storeObject(this);
		World.get().addVisibleObject(this);

		for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		master.addPet(this);

		// final L1NpcInstance target = NpcTable.get().newNpcInstance(npcid);
		PetReading.get().storeNewPet(this, getId(), itemid);
		// 增加物件組人
		addMaster(master);
		// master.sendPackets(new S_NewMaster(master.getName(), this));
	}

	/**
	 * 受到攻擊HP減少
	 */
	@Override
	public void receiveDamage(final L1Character attacker, final int damage) {
		ISASCAPE = false;
		// System.out.println("攻擊目標設置:"+attacker.getName() + " h:" + damage);
		if (getCurrentHp() > 0) {
			if (damage > 0) { // 回復の場合は攻撃しない。
				setHate(attacker, 0); // ペットはヘイト無し
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			if ((attacker instanceof L1PcInstance) && (damage > 0)) {
				final L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
			}

			final int newHp = getCurrentHp() - damage;
			if (newHp <= 0) {
				death(attacker);
			} else {
				setCurrentHp(newHp);
			}

		} else if (!isDead()) { // 念のため
			death(attacker);
		}
	}

	public synchronized void death(final L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			setCurrentHp(0);

			getMap().setPassable(getLocation(), true);
			broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
		}
	}

	/**
	 * 進化寵物
	 * 
	 * @param new_itemobjid
	 */
	public void evolvePet(final int new_itemobjid) {
		final L1Pet pet = PetReading.get().getTemplate(_itemObjId);
		if (pet == null) {
			return;
		}

		final int newNpcId = _type.getNpcIdForEvolving();
		// 進化前のmaxHp,maxMpを退避
		final int tmpMaxHp = getMaxHp();
		final int tmpMaxMp = getMaxMp();

		transform(newNpcId);
		_type = PetTypeTable.getInstance().get(newNpcId);

		setLevel(1);
		// HPMPを元の半分にする
		setMaxHp(tmpMaxHp / 2);
		setMaxMp(tmpMaxMp / 2);
		setCurrentHpDirect(getMaxHp());
		setCurrentMpDirect(getMaxMp());
		setExp(0);
		setExpPercent(0);

		// インベントリを空にする
		getInventory().clearItems();

		// 古いペットをDBから消す
		PetReading.get().deletePet(_itemObjId);

		// 新しいペットをDBに書き込む
		pet.set_itemobjid(new_itemobjid);
		pet.set_npcid(newNpcId);
		pet.set_name(getName());
		pet.set_level(getLevel());
		pet.set_hp(getMaxHp());
		pet.set_mp(getMaxMp());
		pet.set_exp((int) getExp());
		PetReading.get().storeNewPet(this, getId(), new_itemobjid);

		_itemObjId = new_itemobjid;
	}

	/**
	 * 解散寵物
	 */
	public void liberate() {
		final L1MonsterInstance monster = new L1MonsterInstance(getNpcTemplate());
		monster.setId(IdFactoryNpc.get().nextId());

		monster.setX(getX());
		monster.setY(getY());
		monster.setMap(getMapId());
		monster.setHeading(getHeading());
		monster.set_storeDroped(true);
		monster.setInventory(getInventory());
		setInventory(null);
		monster.setLevel(getLevel());
		monster.setMaxHp(getMaxHp());
		monster.setCurrentHpDirect(getCurrentHp());
		monster.setMaxMp(getMaxMp());
		monster.setCurrentMpDirect(getCurrentMp());

		_petMaster.getPetList().remove(getId());
		deleteMe();

		_petMaster.getInventory().removeItem(_itemObjId, 1);
		PetReading.get().deletePet(_itemObjId);

		World.get().storeObject(monster);
		World.get().addVisibleObject(monster);
		for (final L1PcInstance pc : World.get().getRecognizePlayer(monster)) {
			onPerceive(pc);
		}
	}

	/**
	 * 收集
	 * 
	 * @param isDepositnpc 寵物是否進入管理所
	 */
	public void collect(final boolean isDepositnpc) {
		L1Inventory masterInv = _petMaster.getInventory();
		final List<L1ItemInstance> items = _inventory.getItems();

		for (final L1ItemInstance item : items) {
			// _log.info(item.getItem().getName());
			if (item.isEquipped()) { // 使用中
				if (isDepositnpc) {// 寵物進入管理所
					final int itemId = item.getItem().getItemId();
					final L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
					// 解除使用狀態
					if (petItem != null) {
						setHitByWeapon(0);
						setDamageByWeapon(0);
						addStr(-petItem.getAddStr());
						addCon(-petItem.getAddCon());
						addDex(-petItem.getAddDex());
						addInt(-petItem.getAddInt());
						addWis(-petItem.getAddWis());
						addMaxHp(-petItem.getAddHp());
						addMaxMp(-petItem.getAddMp());
						addSp(-petItem.getAddSp());
						addMr(-petItem.getAddMr());

						setWeapon(null);
						setArmor(null);
						item.setEquipped(false);
					}
					// item.setEquipped(false);

				} else {
					continue;
				}
			}
			// 容量重量確認
			if (_petMaster.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
				_inventory.tradeItem(item, item.getCount(), masterInv);
				// 143 \f1%0%s 給你 %1%o 。
				_petMaster.sendPackets(new S_ServerMessage(143, getName(), item.getLogName()));

			} else {
				item.set_showId(get_showId());
				// 過重 變更主人背包為地面
				masterInv = World.get().getInventory(getX(), getY(), getMapId());
				_inventory.tradeItem(item, item.getCount(), masterInv);
			}
		}
		savePet();
	}

	/**
	 * 背包內物品的掉落
	 */
	public void dropItem() {
		final L1Inventory worldInv = World.get().getInventory(getX(), getY(), getMapId());
		// 取回背包物件
		final List<L1ItemInstance> items = _inventory.getItems();

		for (final L1ItemInstance item : items) {
			item.set_showId(get_showId());
			if (item.isEquipped()) { // 使用中
				final int itemId = item.getItem().getItemId();
				final L1PetItem petItem = PetItemTable.get().getTemplate(itemId);
				// 解除使用狀態
				if (petItem != null) {
					setHitByWeapon(0);
					setDamageByWeapon(0);
					addStr(-petItem.getAddStr());
					addCon(-petItem.getAddCon());
					addDex(-petItem.getAddDex());
					addInt(-petItem.getAddInt());
					addWis(-petItem.getAddWis());
					addMaxHp(-petItem.getAddHp());
					addMaxMp(-petItem.getAddMp());
					addSp(-petItem.getAddSp());
					addMr(-petItem.getAddMr());

					setWeapon(null);
					setArmor(null);
					item.setEquipped(false);
				}
			}
			// item.setEquipped(false);
			_inventory.tradeItem(item, item.getCount(), worldInv);
		}
		// 存檔
		savePet();
	}

	/**
	 * 寵物資料存檔
	 */
	private void savePet() {
		try {
			final L1Pet pet = PetReading.get().getTemplate(_itemObjId);

			if (pet != null) {
				pet.set_exp((int) getExp());
				pet.set_level(getLevel());
				pet.set_hp(getMaxHp());
				pet.set_mp(getMaxMp());
				PetReading.get().storePet(pet);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 主人笛子使用
	 */
	public void call() {
		if (_type != null) {
			final int id = _type.getMessageId(L1PetType.getMessageNumber(getLevel()));
			if (id != 0) {
				broadcastPacketX8(new S_NpcChat(this, "$" + id));
			}
		}

		// 移動至主人身邊休息
		setCurrentPetStatus(7);
	}

	/**
	 * 設置目標
	 * 
	 * @param target
	 */
	public void setTarget(final L1Character target) {
		if ((target != null) && ((_currentPetStatus == 1) || // 攻擊
				(_currentPetStatus == 2) || // 防禦
				(_currentPetStatus == 5)// 警戒
		)) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
		}
	}

	/**
	 * 設置主人目標
	 * 
	 * @param target
	 */
	public void setMasterTarget(final L1Character target) {
		// System.out.println("設置主人目標");
		if ((target != null) && ((_currentPetStatus == 1) || // 攻擊
				(_currentPetStatus == 5)// 警戒
		)) {
			setHate(target, 10);
			if (!isAiRunning()) {
				// System.out.println("設置主人目標 startAI");
				startAI();
			}
		}
	}

	/**
	 * 設置主人指定目標
	 * 
	 * @param target
	 */
	public void setMasterSelectTarget(final L1Character target) {
		// System.out.println("設置主人指定目標:" + target);
		// 目標不為空
		if (target != null) {
			setHate(target, 0);
			if (!isAiRunning()) {
				startAI();
			}
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
			perceivedFrom.sendPackets(new S_NPCPack_Pet(this, perceivedFrom)); // ペット系オブジェクト認識
			if (getMaster() != null) {
				if (perceivedFrom.getId() == getMaster().getId()) {
					perceivedFrom.sendPackets(new S_HPMeter(getId(), (100 * getCurrentHp()) / getMaxHp(), 0xff));
				}
			}
			if (isDead()) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 對該物件攻擊的調用
	 */
	@Override
	public void onAction(final L1PcInstance player) {
		if (player == null) {
			return;
		}
		final L1Character cha = getMaster();
		if (cha == null) {
			return;
		}
		final L1PcInstance master = (L1PcInstance) cha;
		if (master.isTeleport()) { // 傳送處理中
			return;
		}
		if (master.equals(player)) {// 攻擊者是主人
			final L1AttackMode attack_mortion = new L1AttackPc(player, this); // 攻擊判斷
			attack_mortion.action();
			return;
		}
		if (isSafetyZone() || player.isSafetyZone()) {// 安全區域中
			final L1AttackMode attack_mortion = new L1AttackPc(player, this); // 攻擊判斷
			attack_mortion.action();
			return;
		}

		if (player.checkNonPvP(player, this)) {
			return;
		}

		final L1AttackMode attack = new L1AttackPc(player, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(final L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (_petMaster.equals(player)) {
			player.sendPackets(new S_PetMenuPacket(this, getExpPercent()));
			savePet();
		}
	}

	@Override
	public void onFinalAction(final L1PcInstance player, final String action) {
		final int status = Integer.parseInt(action);
		// int status = actionType(action);
		switch (status) {
		case 0:
			return;

		case 6:
			liberate(); // ペットの解放
			break;

		default:
			// 同じ主人のペットの状態をすべて更新
			final Object[] petList = _petMaster.getPetList().values().toArray();
			for (final Object petObject : petList) {
				if (petObject instanceof L1PetInstance) { // ペット
					final L1PetInstance pet = (L1PetInstance) petObject;
					if (_petMaster != null) {
						if (_petMaster.isGm()) {
							pet.setCurrentPetStatus(status);
							continue;
						}

						// 等級高於寵物
						if (_petMaster.getLevel() >= pet.getLevel()) {
							pet.setCurrentPetStatus(status);

						} else {
							// 取回寵物分類
							// final L1PetType type =
							// PetTypeTable.getInstance().get(pet.getNpcTemplate().get_npcId());
							if (_type != null) {
								final int id = _type.getDefyMessageId();
								if (id != 0) {
									broadcastPacketX8(new S_NpcChat(pet, "$" + id));
								}
							}
						}

					}
				}
			}
			break;
		}
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			useItem(USEITEM_HASTE, 100); // １００％の確率でヘイストポーション使用
		}
		if (((getCurrentHp() * 100) / getMaxHp()) < 40) { // ＨＰが４０％きったら
			useItem(USEITEM_HEAL, 100); // １００％の確率で回復ポーション使用
		}
	}

	@Override
	public void onGetItem(final L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		Arrays.sort(healPotions);
		Arrays.sort(haestPotions);
		if (Arrays.binarySearch(healPotions, item.getItem().getItemId()) >= 0) {
			if (getCurrentHp() != getMaxHp()) {
				useItem(USEITEM_HEAL, 100);
			}

		} else if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void setCurrentHp(final int i) {
		final int currentHp = Math.min(i, getMaxHp());

		if (getCurrentHp() == currentHp) {
			return;
		}

		setCurrentHpDirect(currentHp);

		// 寵物血條更新
		if (_petMaster != null) {
			final int hpRatio = (100 * currentHp) / getMaxHp();
			final L1PcInstance master = _petMaster;
			master.sendPackets(new S_HPMeter(getId(), hpRatio, 0xff));
		}
	}

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, getMaxMp());

		if (getCurrentMp() == currentMp) {
			return;
		}

		setCurrentMpDirect(currentMp);
	}

	/**
	 * 寵物狀態
	 * 
	 * @param i
	 */
	public void setCurrentPetStatus(final int i) {
		// System.out.println("寵物狀態:" + i);
		_currentPetStatus = i;
		set_tempModel();
		switch (_currentPetStatus) {
		case 5:// 警戒
			setHomeX(getX());
			setHomeY(getY());
			break;

		case 3:// 休息
			allTargetClear();
			break;

		default:
			if (!isAiRunning()) {
				startAI();
			}
			break;
		}
	}

	/**
	 * 寵物狀態<BR>
	 * <BR>
	 * 0:無<BR>
	 * 1:攻擊<BR>
	 * 2:防禦<BR>
	 * 3:休息<BR>
	 * 4:配置<BR>
	 * 5:警戒<BR>
	 * 6:解散<BR>
	 * 7:召回
	 * 
	 * @return
	 */
	public int getCurrentPetStatus() {
		return _currentPetStatus;
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setExpPercent(final int expPercent) {
		_expPercent = expPercent;
	}

	public int getExpPercent() {
		return _expPercent;
	}

	private L1ItemInstance _weapon;

	public void setWeapon(final L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	private L1ItemInstance _armor;

	public void setArmor(final L1ItemInstance armor) {
		_armor = armor;
	}

	public L1ItemInstance getArmor() {
		return _armor;
	}

	private int _hitByWeapon;

	public void setHitByWeapon(final int i) {
		_hitByWeapon = i;
	}

	public int getHitByWeapon() {
		return _hitByWeapon;
	}

	private int _damageByWeapon;

	public void setDamageByWeapon(final int i) {
		_damageByWeapon = i;
	}

	public int getDamageByWeapon() {
		return _damageByWeapon;
	}

	public L1PetType getPetType() {
		return _type;
	}

	private int _tempModel = 3;

	public void set_tempModel() {
		_tempModel = _currentPetStatus;
	}

	public void get_tempModel() {
		_currentPetStatus = _tempModel;
	}
}
