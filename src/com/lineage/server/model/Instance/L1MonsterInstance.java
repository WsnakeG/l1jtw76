package com.lineage.server.model.Instance;

import static com.lineage.server.model.skill.L1SkillId.DRAGON_BLOOD_1;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_BLOOD_2;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_BLOOD_3;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.SCORE02;
import static com.lineage.server.model.skill.L1SkillId.SCORE03;
import static com.lineage.server.model.skill.L1SkillId.SCORE04;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.Config;
import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.data.cmd.CreateNewItem;
import com.lineage.data.event.CampSet;
import com.lineage.data.event.ProtectorSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.C1_Name_Type_Table;
import com.lineage.server.datatables.ItemTable;
import com.lineage.server.datatables.MonsterEnhanceTable;
import com.lineage.server.datatables.NpcScoreTable;
import com.lineage.server.datatables.NpcSpawnTable;
import com.lineage.server.datatables.NpcTable;
import com.lineage.server.datatables.UBTable;
import com.lineage.server.datatables.William_killnpc_quest;
import com.lineage.server.datatables.lock.CharItemsReading;
import com.lineage.server.model.L1AttackMode;
import com.lineage.server.model.L1AttackPc;
import com.lineage.server.model.L1CastleLocation;
import com.lineage.server.model.L1Character;
import com.lineage.server.model.L1Location;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.L1Teleport;
import com.lineage.server.model.L1UltimateBattle;
import com.lineage.server.model.drop.DropShare;
import com.lineage.server.model.drop.DropShareExecutor;
import com.lineage.server.model.drop.SetDrop;
import com.lineage.server.model.drop.SetDropExecutor;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_ChangeName;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_HPMeter;
import com.lineage.server.serverpackets.S_NPCPack;
import com.lineage.server.serverpackets.S_OwnCharAttrDef;
import com.lineage.server.serverpackets.S_OwnCharStatus;
import com.lineage.server.serverpackets.S_PacketBox;
import com.lineage.server.serverpackets.S_PacketBoxGree;
import com.lineage.server.serverpackets.S_RemoveObject;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillBrave;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.serverpackets.S_TrueTarget;
import com.lineage.server.templates.L1Npc;
import com.lineage.server.templates.L1QuestUser;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.thread.GeneralThreadPool;
import com.lineage.server.utils.CalcExp;
import com.lineage.server.utils.CheckUtil;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.utils.RandomArrayList;
import com.lineage.server.world.World;
import com.lineage.server.world.WorldQuest;

/**
 * 對象:mob 控制項
 * 
 * @author daien
 */
public class L1MonsterInstance extends L1NpcInstance {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1MonsterInstance.class);

	private static final Random _random = new Random();

	// private static final boolean _tkpc = false; // 追殺邪惡玩家

	private boolean _storeDroped; // 背包是否禁止加入掉落物品

	// アイテム使用処理
	@Override
	public void onItemUse() {
		if (!isActived() && (_target != null)) {
			useItem(USEITEM_HASTE, 40); // ４０％の確率でヘイストポーション使用

			// 變形怪 變身數據處理
			if (getNpcTemplate().is_doppel() && (_target instanceof L1PcInstance)) {
				final L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900); // 正確な値がわからん
				for (final L1PcInstance pc : World.get().getRecognizePlayer(this)) {
					pc.sendPackets(new S_RemoveObject(this));
					pc.removeKnownObject(this);
					pc.updateObject();
				}
			}
		}
		if (((getCurrentHp() * 100) / getMaxHp()) < 40) { // ＨＰが４０％きったら
			useItem(USEITEM_HEAL, 50); // ５０％の確率で回復ポーション使用
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
			if (0 < getCurrentHp()) {
				if ((getHiddenStatus() == HIDDEN_STATUS_SINK) || (getHiddenStatus() == HIDDEN_STATUS_ICE)) {
					perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));

				} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
					perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
				}
				perceivedFrom.sendPackets(new S_NPCPack(this,perceivedFrom));
				onNpcAI(); // 啟動AI
				if (getBraveSpeed() == 1) {// 具有勇水狀態
					perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
				}

			} else {
				perceivedFrom.sendPackets(new S_NPCPack(this));
			}

			// 天刀要得顯示特效
			/*if (this.getNpcTemplate().get_npcId() == 45555) {
				perceivedFrom.sendPackets(new S_TrueTarget(getId(), 12299, 1));
			}*/
			William_killnpc_quest.sendGfx(perceivedFrom, this);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void searchTarget() {
		// 攻擊目標搜尋
		final L1PcInstance targetPlayer = searchTarget(this);
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;

		} else {
			ISASCAPE = false;
		}
	}

	public void searchAtkNpc() {
		boolean hasnpc = false;
		for (final L1Object tg : World.get().getVisibleObjects(this)) {
			hasnpc = false;
			if (tg instanceof L1MonsterInstance) {
				L1MonsterInstance tgNpc = (L1MonsterInstance) tg;
				if (tgNpc.isDead()) {
					continue;
				}
				if (tgNpc.getCurrentHp() <= 0) {
					continue;
				}
				if (this.getNpcTemplate().is_agrogfxid1() >= 0) {
					if (tgNpc.getGfxId() == this.getNpcTemplate().is_agrogfxid1()) {
					this._hateList.add(tgNpc, 0);
					this._target = tgNpc;
					hasnpc = true;
					break;
					}
				}
				if (this.getNpcTemplate().is_agrogfxid2() >= 0) {
					if (tgNpc.getGfxId() == this.getNpcTemplate().is_agrogfxid2()) {
					this._hateList.add(tgNpc, 0);
					this._target = tgNpc;
					hasnpc = true;
					break;
					}
				}
			}
			if(!hasnpc){
				continue;
			}
		}
	}
	
	private L1PcInstance searchTarget(final L1MonsterInstance npc) {
		// 攻擊目標搜尋
		L1PcInstance targetPlayer = null;
		for (final L1PcInstance pc : World.get().getVisiblePlayer(npc)) {
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				_log.error(e.getLocalizedMessage(), e);
			}
			if (pc.getCurrentHp() <= 0) {
				continue;
			}
			if (pc.isDead()) {
				continue;
			}
			if (pc.isGhost()) {
				continue;
			}
			if (pc.isGm()) {
				continue;
			}

			// 副本ID不相等
			if (npc.get_showId() != pc.get_showId()) {
				continue;
			}

			if (npc.getMapId() == 410) {// 魔族神殿的MOB
				// 忽略收到調職命令的小惡魔
				if (pc.getTempCharGfx() == 4261) {
					continue;
				}
			}

			if (npc.getNpcTemplate().get_family() == NpcTable.ORC) {
				if (pc.getClan() != null) {
					if (pc.getClan().getCastleId() == L1CastleLocation.OT_CASTLE_ID) {
						continue;
					}
				}
			}

			final L1PcInstance tgpc1 = npc.attackPc1(pc);
			if (tgpc1 != null) {
				targetPlayer = tgpc1;
				return targetPlayer;
			}

			final L1PcInstance tgpc2 = npc.attackPc2(pc);
			if (tgpc2 != null) {
				targetPlayer = tgpc2;
				return targetPlayer;
			}

			// どちらかの条件を満たす場合、友好と見なされ先制攻撃されない。
			// ・モンスターのカルマがマイナス値（バルログ側モンスター）でPCのカルマレベルが1以上（バルログ友好）
			// ・モンスターのカルマがプラス値（ヤヒ側モンスター）でPCのカルマレベルが-1以下（ヤヒ友好）
			if (npc.getNpcTemplate().getKarma() < 0) {
				if (pc.getKarmaLevel() >= 1) {
					continue;
				}
			}
			if (npc.getNpcTemplate().getKarma() > 0) {
				if (pc.getKarmaLevel() <= -1) {
					continue;
				}
			}

			// 見棄てられた者たちの地 カルマクエストの変身中は、各陣営のモンスターから先制攻撃されない
			if (pc.getTempCharGfx() == 6034) {
				if (npc.getNpcTemplate().getKarma() < 0) {
					continue;
				}
			}
			if (pc.getTempCharGfx() == 6035) {
				if (npc.getNpcTemplate().getKarma() > 0) {
					continue;
				}
				if (npc.getNpcTemplate().get_npcId() == 46070) {// 被拋棄的魔族
					continue;
				}
				if (npc.getNpcTemplate().get_npcId() == 46072) {// 被拋棄的魔族
					continue;
				}

			}

			// 邪惡玩家追殺
			final L1PcInstance tgpc = npc.targetPlayer1000(pc);
			if (tgpc != null) {
				targetPlayer = tgpc;
				return targetPlayer;
			}

			boolean isCheck = false;
			if (!pc.isInvisble()) {
				isCheck = true;
			}

			if (npc.getNpcTemplate().is_agrocoi()) {
				isCheck = true;
			}
			if (isCheck) { // インビジチェック
				// 變形探知
				if (pc.hasSkillEffect(67)) { // 變形術
					if (npc.getNpcTemplate().is_agrososc()) {
						targetPlayer = pc;
						return targetPlayer;
					}
				}

				// 主動攻擊
				if (npc.getNpcTemplate().is_agro()) {
					targetPlayer = pc;
					return targetPlayer;
				}

				// 特定外型搜尋
				if (npc.getNpcTemplate().is_agrogfxid1() >= 0) {
					if (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid1()) {
						targetPlayer = pc;
						return targetPlayer;
					}
				}
				if (npc.getNpcTemplate().is_agrogfxid2() >= 0) {
					if (pc.getGfxId() == npc.getNpcTemplate().is_agrogfxid2()) {
						targetPlayer = pc;
						return targetPlayer;
					}
				}
			}
		}
		return targetPlayer;
	}

	/**
	 * 攻擊虛擬玩家
	 */
	/*
	 * private void tkDe() { for (final L1Object tg :
	 * World.get().getVisibleObjects(this)) { try { Thread.sleep(2); } catch
	 * (InterruptedException e) { _log.error(e.getLocalizedMessage(), e); } if
	 * (tg instanceof L1DeInstance) { L1DeInstance tgDe = (L1DeInstance) tg; if
	 * (tgDe.isDead()) { continue; } if (tgDe.getCurrentHp() <= 0) { continue; }
	 * if (_random.nextBoolean()) { this._hateList.add(tgDe, 0); this._target =
	 * tgDe; } } } }
	 */

	/**
	 * 克特
	 * 
	 * @param pc
	 * @return
	 */
	private L1PcInstance attackPc2(final L1PcInstance pc) {
		if (getNpcId() == 45600) { // 克特
			if (pc.isCrown()) {// 王族
				if (pc.getTempCharGfx() == pc.getClassId()) {
					return pc;
				}
			}
			if (pc.isDarkelf()) {// 黑妖
				return pc;
			}
		}
		return null;
	}

	/**
	 * 競技場
	 * 
	 * @param pc
	 * @return
	 */
	private L1PcInstance attackPc1(final L1PcInstance pc) {
		final int mapId = getMapId();
		boolean isCheck = false;
		if (mapId == 88) {
			isCheck = true;
		}
		if (mapId == 98) {
			isCheck = true;
		}
		if (mapId == 92) {
			isCheck = true;
		}
		if (mapId == 91) {
			isCheck = true;
		}
		if (mapId == 95) {
			isCheck = true;
		}
		if (isCheck) {
			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // インビジチェック
				return pc;
			}
		}
		return null;
	}

	/**
	 * 邪惡玩家追殺
	 * 
	 * @param pc
	 * @return
	 */
	private L1PcInstance targetPlayer1000(final L1PcInstance pc) {
		if (ConfigOther.KILLRED) {
			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& (getNpcTemplate().is_agrogfxid1() < 0)
					&& (getNpcTemplate().is_agrogfxid2() < 0)) { // 完全なノンアクティブモンスター

				if (pc.getLawful() < -1000) { // プレイヤーがカオティック
					return pc;
				}
			}
		}
		return null;
	}

	/**
	 * 攻擊目標設置
	 */
	@Override
	public void setLink(final L1Character cha) {
		// 副本ID不相等
		if (get_showId() != cha.get_showId()) {
			return;
		}
		if ((cha != null) && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	public L1MonsterInstance(final L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}

		// 怪物誕生前特效 by terry0412
		if (((getNpcId() >= 71014) && (getNpcId() <= 71016))
				|| ((getNpcId() >= 71026) && (getNpcId() <= 71028))
				|| ((getNpcId() >= 97204) && (getNpcId() <= 97209))) {
			broadcastPacketAll(new S_DoActionGFX(getId(), 11));
			npcSleepTime(11, 0);
		}

		if (!_storeDroped) {// 背包是否加入掉落物品
			final SetDropExecutor setdrop = new SetDrop();
			setdrop.setDrop(this, getInventory());

			getInventory().shuffle();
			_storeDroped = true;
		}

		setActived(false);
		startAI();
	}

	/**
	 * 對話
	 */
	@Override
	public void onTalkAction(final L1PcInstance pc) {
		// 改變面向
		setHeading(targetDirection(pc.getX(), pc.getY()));
		broadcastPacketAll(new S_ChangeHeading(this));

		// 動作暫停
		set_stop_time(REST_MILLISEC);
		setRest(true);
	}

	@Override
	public void onAction(final L1PcInstance pc) {
		if (ATTACK != null) {
			ATTACK.attack(pc, this);
		}
		if ((getCurrentHp() > 0) && !isDead()) {
			final L1AttackMode attack = new L1AttackPc(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
			}
			attack.action();
			attack.commit();
		}
	}

	/**
	 * 受攻擊mp減少計算
	 */
	@Override
	public void ReceiveManaDamage(final L1Character attacker, final int mpDamage) {
		if ((mpDamage > 0) && !isDead()) {
			setHate(attacker, mpDamage);

			onNpcAI();

			// NPC互相幫助的判斷
			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	/**
	 * 魔法具有属性傷害使用 (魔法抗性處理) attr:0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法 (武器技能使用)
	 * 
	 * @param attacker
	 * @param damage
	 * @param attr
	 */
	public void receiveDamage(final L1Character attacker, double damage, final int attr) {
		final int player_mr = getMr();
		final int rnd = _random.nextInt(300) + 1;
		if (player_mr >= rnd) {
			damage /= 2.01;
		}

		int resist = 0;
		switch (attr) {
		case L1Skills.ATTR_EARTH:
			resist = getEarth();
			break;

		case L1Skills.ATTR_FIRE:
			resist = getFire();
			break;

		case L1Skills.ATTR_WATER:
			resist = getWater();
			break;

		case L1Skills.ATTR_WIND:
			resist = getWind();
			break;
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;

		} else {
			resistFloor *= -1;
		}

		final double attrDeffence = resistFloor / 32.0;

		final double coefficient = ((1.0 - attrDeffence) + (3.0 / 32.0));

		if (coefficient > 0) {
			damage *= coefficient;
		}
		this.receiveDamage(attacker, (int) damage);
	}

	/**
	 * 受攻擊hp減少計算
	 */
	@Override
	public void receiveDamage(L1Character attacker, int damage) {
		ISASCAPE = false;
		if ((getCurrentHp() > 0) && !isDead()) {
			if ((getHiddenStatus() == HIDDEN_STATUS_SINK) || (getHiddenStatus() == HIDDEN_STATUS_FLY)) {
				return;
			}
			if (damage >= 0) {
				if (attacker instanceof L1EffectInstance) { // 效果不列入目標(設置主人為目標)
					final L1EffectInstance effect = (L1EffectInstance) attacker;
					attacker = effect.getMaster();
					if (attacker != null) {
						setHate(attacker, damage);
					}

				} else if (attacker instanceof L1IllusoryInstance) { // 攻擊者是分身不列入目標(設置主人為目標)
					final L1IllusoryInstance ill = (L1IllusoryInstance) attacker;
					attacker = ill.getMaster();
					if (attacker != null) {
						setHate(attacker, damage);
					}
				} else if (attacker instanceof L1MonsterInstance) {// 魔法師．哈汀(故事)
					switch (getNpcTemplate().get_npcId()) {
					case 91290: // 鐮刀死神的使者
					case 91294: // 巴風特
					case 91295: // 黑翼賽尼斯
					case 91296: // 賽尼斯
						setHate(attacker, damage);
						damage = 0;
						break;
					}

				} else {
					setHate(attacker, damage);
				}
			}

			if (damage > 0) {
				removeSkillEffect(FOG_OF_SLEEPING);
			}

			onNpcAI();

			L1PcInstance atkpc = null;
			// 攻擊者昰PC
			if (attacker instanceof L1PcInstance) {
				atkpc = (L1PcInstance) attacker;
				if (damage > 0) {
					atkpc.setPetTarget(this);
					switch (getNpcTemplate().get_npcId()) {
					case 45681: // 林德拜爾
					case 45682: // 安塔瑞斯
					case 45683: // 法利昂
					case 45684: // 巴拉卡斯
						recall(atkpc);
						break;
					}
				}
				// NPC互相幫助的判斷
				serchLink(atkpc, getNpcTemplate().get_family());
			}

			final int newHp = getCurrentHp() - damage;
			if ((newHp <= 0) && !isDead()) {
				final int transformId = getNpcTemplate().getTransformId();
				// 変身しないモンスター
				if (transformId == -1) {
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					openDoorWhenNpcDied(this);
					final Death death = new Death(attacker);
					GeneralThreadPool.get().execute(death);

					// 有攻擊者存在
					if (atkpc != null) {
						// 未復活過
						if (!isResurrect()) {
							// 守護者系統 by terry0412
							if ((ProtectorSet.CHANCE > 0 // 隨機數 1/10000
							) && (_random.nextInt(10000) < ProtectorSet.CHANCE)) {
								// 檢查指定道具編號的總世界數量
								if (CharItemsReading.get()
										.checkItemId(ProtectorSet.ITEM_ID) < ProtectorSet.DROP_LIMIT) {
									CreateNewItem.createNewItemW(atkpc, ProtectorSet.ITEM_ID, 1);
								}
							}
						}
					}

					// 三階段屠龍 by terry0412
					if ((getNpcId() >= 71014) && (getNpcId() <= 71016)) { // 新安塔瑞斯
						GeneralThreadPool.get().execute(new deathDragonTimer1(this, getMapId()));
					} else if ((getNpcId() >= 71026) && (getNpcId() <= 71028)) { // 新法利昂
						GeneralThreadPool.get().execute(new deathDragonTimer2(this, getMapId()));
					} else if ((getNpcId() >= 97204) && (getNpcId() <= 97209)) { // 新林德拜爾
						GeneralThreadPool.get().execute(new deathDragonTimer3(this));
					}

				} else { // 変身するモンスター
					// distributeExpDropKarma(attacker);
					transform(transformId);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
			// HP 顯示設置
			if (ConfigOther.HPBAR) {
				if ((attacker instanceof L1PcInstance))
			    {
			      L1PcInstance player = (L1PcInstance)attacker;
			      player.sendPackets(new S_HPMeter(this));
			    }
				// 讓寵物或召喚怪攻擊時也看得到怪物血條 by terry0412
				if (atkpc == null) {
					if (attacker instanceof L1PetInstance) {
						atkpc = (L1PcInstance) ((L1PetInstance) attacker).getMaster();

					} else if (attacker instanceof L1SummonInstance) {
						atkpc = (L1PcInstance) ((L1SummonInstance) attacker).getMaster();
					}

					// 存在PC主人
					if (atkpc != null) {
						broadcastPacketHP(atkpc);
					}

				} else {
					broadcastPacketHP(atkpc);
				}
			}

		} else if (!isDead()) { // 念のため
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			final Death death = new Death(attacker);
			GeneralThreadPool.get().execute(death);
			// Death(attacker);
		}
	}

	/**
	 * NPC死亡開門的處理
	 * 
	 * @param npc
	 */
	private static void openDoorWhenNpcDied(final L1NpcInstance npc) {
		final int[] npcId = { 46143, 46144, 46145, 46146, 46147, 46148, 46149, 46150, 46151, 46152 };
		final int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006, 5007, 5008, 5009, 5010 };

		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
			}
		}
	}

	/**
	 * 開門的處理
	 * 
	 * @param doorId
	 */
	private static void openDoorInCrystalCave(final int doorId) {
		for (final L1Object object : World.get().getObject()) {
			if (object instanceof L1DoorInstance) {
				final L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * 召回PC的處理(PC距離自身過遠)
	 * 
	 * @param pc
	 */
	private void recall(final L1PcInstance pc) {
		if (getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				final L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(), getMapId(), 5, true);
					break;
				}
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

	@Override
	public void setCurrentMp(final int i) {
		final int currentMp = Math.min(i, getMaxMp());

		if (getCurrentMp() == currentMp) {
			return;
		}

		setCurrentMpDirect(currentMp);
	}

	/**
	 * 死亡判斷
	 * 
	 * @author daien
	 */
	class Death implements Runnable {

		L1Character _lastAttacker;// 攻擊者

		/**
		 * 死亡判斷
		 * 
		 * @param lastAttacker 攻擊者
		 */
		public Death(final L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			final L1MonsterInstance mob = L1MonsterInstance.this;

			if (_lastAttacker instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) _lastAttacker;
				pc.setKillCount(pc.getKillCount() + 1);
				pc.sendPackets(new S_OwnCharStatus(pc));
				if (Config.AICHECK) {
					if (pc.hasSkillEffect(L1SkillId.AICHECK)) {
						final int a = _random.nextInt(100);
						final int b = _random.nextInt(100);
						final int sum = a + b;
						pc.setAIsum(sum);
						pc.sendPackets(new S_PacketBoxGree(0x01));
						final int sec = pc.getSkillEffectTimeSec(L1SkillId.AICHECK);
						final String msg1 = "\\aG請您繼續完成之前沒有回答完的問題，請您在" + sec + "秒之內回答問題。";
						final String msg2 = "" + a + "+" + "" + b + "等於多少？";
						// pc.sendPackets(new S_TrueTarget(pc.getId(),
						// pc.getId(), msg2));
						pc.sendPackets(new S_SystemMessage(msg1));
						pc.sendPackets(new S_SystemMessage("\\aG" + msg2));
						pc.setAImsg("\\f=" + msg2);
						/*
						 * if
						 * (pc.hasSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED))
						 * { pc.sendPackets(new
						 * S_PacketBox(S_PacketBox.ICON_CHATBAN, -1)); }
						 */
					} else {
						if (!pc.isCheckAI().isEmpty()) {
							final int a = _random.nextInt(100);
							final int b = _random.nextInt(100);
							final int sum = a + b;
							pc.setAIsum(sum);
							pc.sendPackets(new S_PacketBoxGree(0x01));
							final String msg1 = "\\aG系統收到玩家 " + pc.isCheckAI() + " 舉報，請您在3分鐘之內回答問題。";
							final String msg2 = "" + a + "+" + "" + b + "等於多少？";
							// pc.sendPackets(new S_TrueTarget(pc.getId(),
							// pc.getId(), msg2));
							pc.sendPackets(new S_SystemMessage(msg1));
							pc.sendPackets(new S_SystemMessage("\\aG" + msg2));
							pc.setAImsg("\\f=" + msg2);
							if (!pc.hasSkillEffect(L1SkillId.AICHECK)) {
								pc.setSkillEffect(L1SkillId.AICHECK, 180 * 1000);
							}
							/*
							 * if (pc.hasSkillEffect(L1SkillId.
							 * STATUS_CHAT_PROHIBITED )) { pc.sendPackets(new
							 * S_PacketBox(S_PacketBox.ICON_CHATBAN, -1)); }
							 */
						} else {
							final int sec = pc.getSec();
							if (sec > 0) {
								final int a = _random.nextInt(100);
								final int b = _random.nextInt(100);
								final int sum = a + b;
								pc.setAIsum(sum);
								pc.sendPackets(new S_PacketBoxGree(0x01));
								final String msg1 = "\\aG請您繼續完成之前沒有回答完的問題，請您在" + sec + "秒之內回答問題。";
								final String msg2 = "" + a + "+" + "" + b + "等於多少？";
								// pc.sendPackets(new S_TrueTarget(pc.getId(),
								// pc.getId(), msg2));
								pc.sendPackets(new S_SystemMessage(msg1));
								pc.sendPackets(new S_SystemMessage("\\aG" + msg2));
								pc.setAImsg("\\f=" + msg2);
								pc.setSkillEffect(L1SkillId.AICHECK, sec * 1000);
								/*
								 * if (pc.hasSkillEffect(L1SkillId.
								 * STATUS_CHAT_PROHIBITED )) {
								 * pc.sendPackets(new
								 * S_PacketBox(S_PacketBox.ICON_CHATBAN, -1)); }
								 */
							}
						}
					}
				}
			}

			// 指定NPC死亡對話
			tark(mob);

			// 召喚幫手
			spawn(mob);

			mob.setDeathProcessing(true);
			mob.setCurrentHpDirect(0);
			mob.setDead(true);
			mob.setStatus(ActionCodes.ACTION_Die);

			mob.broadcastPacketAll(new S_DoActionGFX(mob.getId(), ActionCodes.ACTION_Die));

			// 解除舊座標障礙宣告
			mob.getMap().setPassable(mob.getLocation(), true);

			mob.startChat(CHAT_TIMING_DEAD);

			mob.distributeExpDropKarma(_lastAttacker);
			mob.giveUbSeal();

			mob.setDeathProcessing(false);

			mob.setExp(0);
			mob.setKarma(0);
			mob.allTargetClear();

			int deltime = 0;
			// 特定NPC死亡時間設置
			switch (mob.getNpcId()) {
			case 92000:// 杰弗雷庫(雌)
			case 92001:// 杰弗雷庫(雄)
			case 71014: // 新安塔瑞斯 (第一階段)
			case 71015: // 新安塔瑞斯 (第二階段)
			case 71016: // 新安塔瑞斯 (第三階段)
			case 71026: // 新法利昂 (第一階段)
			case 71027: // 新法利昂 (第二階段)
			case 71028: // 新法利昂 (第三階段)
			case 97204: // 新林德拜爾 (第一階段)
			case 97205: // 新林德拜爾 (第二階段)
			case 97206: // 新林德拜爾 (第三階段)
			case 97207: // 新林德拜爾 (空中戰)
			case 97208: // 新林德拜爾 (空中戰)
			case 97209: // 新林德拜爾 (空中戰)
				deltime = 60;
				break;

			default:
				deltime = ConfigAlt.NPC_DELETION_TIME;
				break;
			}
			mob.startDeleteTimer(deltime);
		}

		/**
		 * 死亡呼救
		 * 
		 * @param mob
		 */
		private void spawn(final L1MonsterInstance mob) {
			// 以NPCID定義
			switch (mob.getNpcId()) {

			}
			// 以地圖編號定義
			switch (mob.getMapId()) {

			}
		}

		/**
		 * NPC 死亡 新手教學/特定死亡說話
		 * 
		 * @param mob
		 */
		private void tark(final L1MonsterInstance mob) {
			// 取回NPC所在地圖編號
			/*
			 * short mapid = mob.getMapId(); switch (mapid) { case 68:// 歌唱之島
			 * case 69:// 隐藏之谷 //case 630:// 英雄領地 final int rnd1 = 8000 +
			 * _random.nextInt(12); mob.broadcastPacketX8(new S_NpcChat(mob, "$"
			 * + rnd1)); break; }
			 */

			// 取回NPC編號
			final int npcid = mob.getNpcId();
			switch (npcid) {

			}
		}
	}

	/**
	 * 判斷主要攻擊者(最後殺死NPC的人)
	 * 
	 * @param lastAttacker
	 */
	private void distributeExpDropKarma(final L1Character lastAttacker) {
		if (lastAttacker == null) {
			return;
		}

		// 判斷主要攻擊者
		L1PcInstance pc = null;

		// NPC具有死亡判斷設置
		if (DEATH != null) {
			pc = DEATH.death(lastAttacker, this);

		} else {
			// 判斷主要攻擊者
			pc = CheckUtil.checkAtkPc(lastAttacker);
		}

		if (pc != null) {
			final ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			final ArrayList<Integer> hateList = _hateList.toHateArrayList();
			// 取回經驗值
			final long exp = getExp();

			// 加入經驗值與積分
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			int score = NpcScoreTable.get().get_score(getNpcId());

			if (CampSet.CAMPSTART) {
				// 陣營系統啟用 XXX
				if ((pc.get_c_power() != null) && (pc.get_c_power().get_c1_type() != 0)) {
					if ((score > 0) && !isResurrect()) {
						if (pc.hasSkillEffect(SCORE02)) {
							score *= 2;
						}
						if (pc.hasSkillEffect(SCORE03)) {
							score *= 3;
						}
						if (pc.hasSkillEffect(SCORE04)) {
							score *= 4;
						}
						// 你得到了 %0 積分。
						pc.sendPackets(new S_ServerMessage("\\aI你狩獵獲得了(" + score + ")貢獻積分。"));
						pc.get_other().add_score(score);
						final int lv = C1_Name_Type_Table.get().getLv(pc.get_c_power().get_c1_type(),
								pc.get_other().get_score());
						if (lv != pc.get_c_power().get_power().get_c1_id()) {
							pc.get_c_power().set_power(pc, false);
							pc.sendPackets(new S_ServerMessage(
									"\\aF恭喜您，陣營位階提升為:" + pc.get_c_power().get_power().get_c1_name_type()));
							pc.sendPacketsAll(new S_ChangeName(pc, true));
						}
					}
				}
			}

			// 死亡後續處理
			if (isDead()) {
				// TODO 怪物強化系統 by erics4179
				if (MonsterEnhanceTable.getInstance().getTemplate(getNpcId()) != null) {
					L1MonsterEnhanceInstance mei = MonsterEnhanceTable.getInstance().getTemplate(getNpcId());
					if (mei.getDcEnhance() != 0) {
						mei.setCurrentDc(mei.getCurrentDc() + 1);
						MonsterEnhanceTable.getInstance().save(mei);
						if ((mei.getCurrentDc() % mei.getDcEnhance()) == 0) {
							World.get().broadcastPacketToAll(
									new S_SystemMessage(getName() + "含著淚說：當下次你在見到我，必定讓你吃驚！"));
						}
					}
				}
				// 掉落物品分配
				distributeDrop();
				// 陣營
				giveKarma(pc);
			}
			// 殺死怪物有機率掉落所設定物品
			if (ConfigOther.AllNpcDropItem && ConfigOther.Rnd >= RandomArrayList.getInt(100) + 1
					&& getNpcTemplate().getImpl().equalsIgnoreCase("L1Monster")) {
				L1ItemInstance item3 = ItemTable.get().createItem(ConfigOther.ItemId);
				item3.setCount(ConfigOther.ItemCount);
				if (item3 != null) {
					if (pc.getInventory().checkAddItem(item3, 1) == 0)
						pc.getInventory().storeItem(item3);
					else
						World.get().getInventory(pc.getX(), pc.getY(), pc.getMapId()).storeItem(item3);
					pc.sendPackets(new S_ServerMessage(403, item3.getName()));
				}
			}
			
			// 天刀要得顯示特效
			William_killnpc_quest.add_kill_npcId(pc, this);
		}
	}

	/**
	 * 掉落物品分配
	 */
	private void distributeDrop() {
		final ArrayList<L1Character> dropTargetList = _dropHateList.toTargetArrayList();
		final ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			// 設置掉落物品
			final DropShareExecutor dropShareExecutor = new DropShare();
			dropShareExecutor.dropShare(L1MonsterInstance.this, dropTargetList, dropHateList);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 陣營
	 * 
	 * @param pc
	 */
	private void giveKarma(final L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			final int karmaSign = Integer.signum(karma);
			final int pcKarmaLevel = pc.getKarmaLevel();
			final int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			// カルマ背信行為は5倍
			if ((pcKarmaLevelSign != 0) && (karmaSign != pcKarmaLevelSign)) {
				karma *= 5;
			}
			// カルマは止めを刺したプレイヤーに設定。ペットorサモンで倒した場合も入る。
			pc.addKarma((int) (karma * ConfigRate.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) { // UBの勇者の証
			final L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
			if (ub != null) {
				for (final L1PcInstance pc : ub.getMembersArray()) {
					if ((pc != null) && !pc.isDead() && !pc.isGhost()) {
						final L1ItemInstance item =
								// 勇者的勳章(41402)
								pc.getInventory().storeItem(41402, getUbSealCount());
						// 403 獲得%0%o 。
						pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
					}
				}
			}
		}
	}

	/**
	 * 背包是否禁止加入掉落物品
	 * 
	 * @return true:不加入 false:加入
	 */
	public boolean is_storeDroped() {
		return _storeDroped;
	}

	/**
	 * 設置背包是否禁止加入掉落物品
	 * 
	 * @param flag true:不加入 false:加入
	 */
	public void set_storeDroped(final boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0; // 無限大賽可獲得的勇氣之證數量

	/**
	 * 給予勇氣之證數量
	 * 
	 * @return
	 */
	public int getUbSealCount() {
		return _ubSealCount;
	}

	/**
	 * 設置給予勇氣之證數量
	 * 
	 * @param i
	 */
	public void setUbSealCount(final int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	/**
	 * UBID
	 * 
	 * @return
	 */
	public int getUbId() {
		return _ubId;
	}

	/**
	 * UBID
	 * 
	 * @param i
	 */
	public void setUbId(final int i) {
		_ubId = i;
	}

	/**
	 * 一定機率躲藏
	 */
	private void hide() {
		final int npcid = getNpcTemplate().get_npcId();
		switch (npcid) {
		case 45061: // 弱化史巴托
		case 45161: // 史巴托
		case 45181: // 史巴托
		case 45455: // 殘暴的史巴托
			if ((getMaxHp() / 3) > getCurrentHp()) {
				final int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;

		case 45682: // 安塔瑞斯
			if ((getMaxHp() / 3) > getCurrentHp()) {
				final int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;

		case 45067: // 弱化哈維
		case 45264: // 哈維
		case 45452: // 哈維
		case 45090: // 弱化格利芬
		case 45321: // 格利芬
		case 45445: // 格利芬
			if ((getMaxHp() / 3) > getCurrentHp()) {
				final int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;
		/*
		 * case 45681: // 林德拜爾 XXX 暫時移除躲藏 if (this.getMaxHp() / 3 >
		 * this.getCurrentHp()) { final int rnd = _random.nextInt(50); if (1 >
		 * rnd) { this.allTargetClear();
		 * this.setHiddenStatus(HIDDEN_STATUS_FLY); this.broadcastPacket(new
		 * S_DoActionGFX(this.getId(), ActionCodes.ACTION_Moveup));
		 * this.setStatus(11); this.broadcastPacket(new S_NPCPack(this)); } }
		 */

		case 46107: // 底比斯 曼陀羅草(白)
		case 46108: // 底比斯 曼陀羅草(黒)
			if ((getMaxHp() / 4) > getCurrentHp()) {
				final int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacketAll(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacketAll(new S_NPCPack(this));
				}
			}
			break;
		}
	}

	/**
	 * 召喚後隱藏
	 */
	public void initHide() {
		// 出現直後の隠れる動作
		// 潜るMOBは一定の確率で地中に潜った状態に、
		// 飛ぶMOBは飛んだ状態にしておく
		final int npcid = getNpcTemplate().get_npcId();
		final int rnd = _random.nextInt(3);
		switch (npcid) {
		case 45061: // 弱化史巴托
		case 45161: // 史巴托
		case 45181: // 史巴托
		case 45455: // 殘暴的史巴托
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
			break;

		case 45045: // 弱化高侖石頭怪
		case 45126: // 高侖石頭怪
		case 45134: // 高侖石頭怪
		case 45281: // 奇巖 高侖石頭怪
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
			}
			break;

		case 45067: // 弱化哈維
		case 45264: // 哈維
		case 45452: // 哈維
		case 45090: // 弱化格利芬
		case 45321: // 格利芬
		case 45445: // 格利芬
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(4);
			break;

		case 45681: // 林德拜爾
			setHiddenStatus(HIDDEN_STATUS_FLY);
			setStatus(11);
			break;

		case 46107: // 底比斯 曼陀羅草(白)
		case 46108: // 底比斯 曼陀羅草(黒)
			if (1 > rnd) {
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
			}
			break;

		case 46125:// 高侖鋼鐵怪
		case 46126:// 萊肯
		case 46127:// 歐熊
		case 46128:// 冰原老虎
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
			break;

		case 97349:
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_STANDBY);
			// this.setStatus(0);
			break;
		}
	}

	public void initHideForMinion(final L1NpcInstance leader) {
		// グループに属するモンスターの出現直後の隠れる動作（リーダーと同じ動作にする）
		final int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == HIDDEN_STATUS_SINK) {
			switch (npcid) {
			case 45061: // カーズドスパルトイ
			case 45161: // スパルトイ
			case 45181: // スパルトイ
			case 45455: // デッドリースパルトイ
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
				break;
			case 45045: // クレイゴーレム
			case 45126: // ストーンゴーレム
			case 45134: // ストーンゴーレム
			case 45281: // ギランストーンゴーレム
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(4);
				break;
			case 46107: // テーベ マンドラゴラ(白)
			case 46108: // テーベ マンドラゴラ(黒)
				setHiddenStatus(HIDDEN_STATUS_SINK);
				setStatus(13);
				break;
			}
		} else if (leader.getHiddenStatus() == HIDDEN_STATUS_FLY) {
			switch (npcid) {
			case 45067: // バレーハーピー
			case 45264: // ハーピー
			case 45452: // ハーピー
			case 45090: // バレーグリフォン
			case 45321: // グリフォン
			case 45445: // グリフォン
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(4);
				break;
			case 45681: // 林德拜爾
				setHiddenStatus(HIDDEN_STATUS_FLY);
				setStatus(11);
				break;
			case 46125:
			case 46126:
			case 46127:
			case 46128:
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
				setStatus(4);
				break;
			}
		}
	}

	@Override
	protected void transform(final int transformId) {
		super.transform(transformId);
		// DROPの再設定
		getInventory().clearItems();
		// XXX
		final SetDropExecutor setDropExecutor = new SetDrop();
		setDropExecutor.setDrop(this, getInventory());
		// DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}

	private class deathDragonTimer1 extends TimerTask {

		private final L1MonsterInstance npc;
		private final short mapId;

		public deathDragonTimer1(final L1MonsterInstance paramShort, final short arg3) {
			npc = paramShort;
			mapId = arg3;
		}

		@Override
		public void run() {
			try {
				if (npc.getNpcId() == 71014) {
					// GeneralThreadPool.get().execute(new
					// spawnEffectTrap(this.npc.getX(), this.npc.getY(),
					// this.mapId, 7331, 1085));
					Thread.sleep(5000L);

					sendServerMessage(1573);
					Thread.sleep(5000L);

					sendServerMessage(1574);
					Thread.sleep(10000L);

					sendServerMessage(1575);
					Thread.sleep(10000L);

					sendServerMessage(1576);
					Thread.sleep(10000L);

					final int i = 32776 + _random.nextInt(20);
					final int k = 32679 + _random.nextInt(20);
					// GeneralThreadPool.get().execute(new spawnEffectTrap(i, k,
					// this.mapId, 7331, 1085));
					Thread.sleep(5000L);

					// 新安塔瑞斯(2階段)
					final L1Location loc = new L1Location(i, k, mapId);
					L1SpawnUtil.spawn(71015, loc, new Random().nextInt(8), get_showId());
				} else {
					if (npc.getNpcId() == 71015) {
						// GeneralThreadPool.get().execute(new
						// spawnEffectTrap(this.npc.getX(), this.npc.getY(),
						// this.mapId, 7331, 1085));
						Thread.sleep(5000L);

						sendServerMessage(1577);
						Thread.sleep(5000L);

						sendServerMessage(1578);
						Thread.sleep(10000L);

						sendServerMessage(1579);
						Thread.sleep(10000L);

						final int j = 32776 + _random.nextInt(20);
						final int m = 32679 + _random.nextInt(20);
						// GeneralThreadPool.get().execute(new
						// spawnEffectTrap(j, m, this.mapId, 7331, 1085));
						Thread.sleep(5000L);

						// 新安塔瑞斯(3階段)
						final L1Location loc = new L1Location(j, m, mapId);
						L1SpawnUtil.spawn(71016, loc, new Random().nextInt(8), get_showId());
					} else if (npc.getNpcId() == 71016) {
						final int time = 86400;
						final ArrayList<L1Character> target = npc.getHateList().toTargetArrayList();
						for (final L1Character cha : target) {
							if (cha instanceof L1PcInstance) {
								final L1PcInstance pc = (L1PcInstance) cha;
								pc.broadcastPacketX10(new S_SkillSound(pc.getId(), 7854));
								pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_BLOOD_ICON, 82, time / 60));
								if (!pc.hasSkillEffect(DRAGON_BLOOD_1)) {
									pc.addAc(-2);
									pc.addWater(50);
									pc.sendPackets(new S_OwnCharAttrDef(pc));
								}
								pc.setSkillEffect(DRAGON_BLOOD_1, time * 1000);
							}
						}

						Thread.sleep(5000L);

						sendServerMessage(1580);
						Thread.sleep(5000L);

						sendServerMessage(1581);
						Thread.sleep(10000L);

						// ItemCreateByBoss.giveGiftFromDragon1(this.mapId,
						// localArrayList);

						GeneralThreadPool.get().execute(new CountDownTimer(33718, 32506, (short) 4,
								WorldQuest.get().get(get_showId()), 0));
						boolean hasDoor = false;
						// 是否已經存在「隱匿的巨龍谷入口」
						for (final L1Object obj : World.get().getObject()) {
							if (obj instanceof L1NpcInstance) {
								final L1NpcInstance find_npc = (L1NpcInstance) obj;
								if (find_npc.getNpcId() == 70936) {
									hasDoor = true;
									return;
								}
							}
						}
						if (!hasDoor) {
							Thread.sleep(7000L);

							World.get().broadcastPacketToAll(new S_ServerMessage(1582));
							Thread.sleep(10000L);

							World.get().broadcastPacketToAll(new S_ServerMessage(1583));

							// 「隱匿的巨龍谷入口」將會存在現實6小時
							final L1Location loc = new L1Location(33725, 32506, 4);
							NpcSpawnTable.get().storeSpawn(70936, loc.getX(), loc.getY(), loc.getMapId(),
									21600000);
							L1SpawnUtil.spawn(70936, loc, 0, -1);
						}
					}
				}
			} catch (final Exception localException) {
			}
		}
	}

	private class deathDragonTimer2 extends TimerTask {
		private final L1MonsterInstance npc;
		private final short mapId;

		public deathDragonTimer2(final L1MonsterInstance paramShort, final short arg3) {
			npc = paramShort;
			mapId = arg3;
		}

		@Override
		public void run() {
			try {
				if (npc.getNpcId() == 71026) {
					Thread.sleep(5000L);

					sendServerMessage(1661);
					Thread.sleep(5000L);

					sendServerMessage(1662);
					Thread.sleep(10000L);

					sendServerMessage(1663);
					Thread.sleep(10000L);

					sendServerMessage(1664);
					Thread.sleep(10000L);

					final int j = 32948 + _random.nextInt(20);
					final int m = 32825 + _random.nextInt(20);

					// 新法利昂(2階段)
					final L1Location loc = new L1Location(j, m, mapId);
					L1SpawnUtil.spawn(71027, loc, new Random().nextInt(8), get_showId());
					// Thread.sleep(2000L);

					/*
					 * for (int i1 = 0; i1 < 5; i1++) { localObject3 =
					 * ((L1NpcInstance
					 * )localObject1).getLocation().randomLocation(12, false);
					 * L1SpawnUtil.spawn(103349,
					 * ((L1Location)localObject3).getX(),
					 * ((L1Location)localObject3).getY(), this.mapId, 120000); }
					 */
					// Thread.sleep(2000L);
					// localObject2 =
					// ((L1NpcInstance)localObject1).getLocation().randomLocation(5,
					// false);
					// L1SpawnUtil.spawn(103347,
					// ((L1Location)localObject2).getX(),
					// ((L1Location)localObject2).getY(), this.mapId, 0);
					// Thread.sleep(2000L);
					// localObject3 =
					// ((L1NpcInstance)localObject1).getLocation().randomLocation(5,
					// false);
					// L1SpawnUtil.spawn(103348,
					// ((L1Location)localObject3).getX(),
					// ((L1Location)localObject3).getY(), this.mapId, 0);
					// Thread.sleep(3000L);

					// GeneralThreadPool.get().execute(new
					// spawnRepeatNpc((L1NpcInstance)localObject1, 103336, 2));
				} else if (npc.getNpcId() == 71027) {
					Thread.sleep(5000L);

					sendServerMessage(1665);
					Thread.sleep(5000L);

					sendServerMessage(1666);
					Thread.sleep(10000L);

					sendServerMessage(1667);
					Thread.sleep(10000L);

					final int k = 32948 + _random.nextInt(20);
					final int n = 32825 + _random.nextInt(20);

					// 新法利昂(3階段)
					final L1Location loc = new L1Location(k, n, mapId);
					L1SpawnUtil.spawn(71028, loc, new Random().nextInt(8), get_showId());
					// Thread.sleep(2000L);

					/*
					 * for (int i2 = 0; i2 < 5; i2++) { localObject3 =
					 * ((L1NpcInstance
					 * )localObject1).getLocation().randomLocation(12, false);
					 * L1SpawnUtil.spawn(103349,
					 * ((L1Location)localObject3).getX(),
					 * ((L1Location)localObject3).getY(), this.mapId, 120000); }
					 */
					// Thread.sleep(2000L);
					// L1Location localL1Location =
					// ((L1NpcInstance)localObject1).getLocation().randomLocation(5,
					// false);
					// L1SpawnUtil.spawn(103347, localL1Location.getX(),
					// localL1Location.getY(), this.mapId, 0);
					// Thread.sleep(2000L);
					// localObject3 =
					// ((L1NpcInstance)localObject1).getLocation().randomLocation(5,
					// false);
					// L1SpawnUtil.spawn(103348,
					// ((L1Location)localObject3).getX(),
					// ((L1Location)localObject3).getY(), this.mapId, 0);
					// Thread.sleep(3000L);

					// GeneralThreadPool.get().execute(new
					// spawnRepeatNpc((L1NpcInstance)localObject1, 103336, 3));
				} else if (npc.getNpcId() == 71028) {
					final int time = 86400;
					final ArrayList<L1Character> target = npc.getHateList().toTargetArrayList();
					for (final L1Character cha : target) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.broadcastPacketX10(new S_SkillSound(pc.getId(), 7854));
							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_BLOOD_ICON, 85, time / 60));
							if (!pc.hasSkillEffect(DRAGON_BLOOD_2)) {
								pc.addHpr(3);
								pc.addMpr(1);
								pc.addWind(50);
								pc.sendPackets(new S_OwnCharAttrDef(pc));
							}
							pc.setSkillEffect(DRAGON_BLOOD_2, time * 1000);
						}
					}

					Thread.sleep(5000L);

					sendServerMessage(1668);
					Thread.sleep(5000L);

					sendServerMessage(1669);
					Thread.sleep(10000L);

					// ItemCreateByBoss.giveGiftFromDragon2(this.mapId,
					// localArrayList2);

					GeneralThreadPool.get().execute(new CountDownTimer(33718, 32506, (short) 4,
							WorldQuest.get().get(get_showId()), 0));
					boolean hasDoor = false;
					// 是否已經存在「隱匿的巨龍谷入口」
					for (final L1Object obj : World.get().getObject()) {
						if (obj instanceof L1NpcInstance) {
							final L1NpcInstance find_npc = (L1NpcInstance) obj;
							if (find_npc.getNpcId() == 70936) {
								hasDoor = true;
								return;
							}
						}
					}
					if (!hasDoor) {
						Thread.sleep(7000L);

						World.get().broadcastPacketToAll(new S_ServerMessage(1582));
						Thread.sleep(10000L);

						World.get().broadcastPacketToAll(new S_ServerMessage(1583));

						// 「隱匿的巨龍谷入口」將會存在現實6小時
						final L1Location loc = new L1Location(33725, 32506, 4);
						NpcSpawnTable.get().storeSpawn(70936, loc.getX(), loc.getY(), loc.getMapId(),
								21600000);
						L1SpawnUtil.spawn(70936, loc, 0, -1);
					}
				}
			} catch (final Exception localException) {
			}
		}
	}

	private class deathDragonTimer3 extends TimerTask {
		private final L1MonsterInstance npc;

		public deathDragonTimer3(final L1MonsterInstance npc) {
			this.npc = npc;
		}

		@Override
		public void run() {
			try {
				if (npc.getNpcId() == 97204) { // 第一階段地面戰
					Thread.sleep(5000L);

					sendServerMessage(1759);
					Thread.sleep(5000L);

					sendServerMessage(1760);
					Thread.sleep(10000L);

					sendServerMessage(1761);
					Thread.sleep(10000L);

					sendServerMessage(1762);
					Thread.sleep(10000L);

					Thread.sleep(5000L);

					final L1NpcInstance[] npc_list = {
							L1SpawnUtil.spawn(97207, new L1Location(32850, 32856, npc.getMapId()), 4,
									npc.get_showId()),
							L1SpawnUtil.spawn(97208, new L1Location(32864, 32862, npc.getMapId()), 5,
									npc.get_showId()),
							L1SpawnUtil.spawn(97209, new L1Location(32869, 32876, npc.getMapId()), 6,
									npc.get_showId()) };
					npc_list[_random.nextInt(npc_list.length)].set_quest_id(1);
				} else if ((npc.getNpcId() >= 97207) && (npc.getNpcId() <= 97209)) { // 第二階段空中戰
					if (npc.get_quest_id() <= 0) {
						return;
					}
					final L1QuestUser quest = WorldQuest.get().get(npc.get_showId());
					for (final L1NpcInstance npc : quest.npcList()) {
						if (!npc.isDead() && ((npc.getNpcId() >= 97207) && (npc.getNpcId() <= 97209))) {
							npc.set_spawnTime(3);
						}
					}
					Thread.sleep(5000L);

					sendServerMessage(1763);
					Thread.sleep(5000L);

					sendServerMessage(1764);
					Thread.sleep(10000L);

					sendServerMessage(1765);
					Thread.sleep(10000L);

					sendServerMessage(1766);
					Thread.sleep(10000L);

					Thread.sleep(5000L);

					final L1Location loc = new L1Location(32846, 32877, npc.getMapId()).randomLocation(10,
							true);
					L1SpawnUtil.spawn(97205, loc, 0, npc.get_showId());
				} else if (npc.getNpcId() == 97205) { // 第三階段地面戰
					Thread.sleep(5000L);

					sendServerMessage(1767);
					Thread.sleep(5000L);

					sendServerMessage(1768);
					Thread.sleep(10000L);

					sendServerMessage(1769);
					Thread.sleep(10000L);

					sendServerMessage(1770);
					Thread.sleep(10000L);

					sendServerMessage(1771);
					Thread.sleep(10000L);

					Thread.sleep(5000L);

					final L1Location loc = new L1Location(32846, 32877, npc.getMapId()).randomLocation(10,
							true);
					L1SpawnUtil.spawn(97206, loc, 0, npc.get_showId());
				} else if (npc.getNpcId() == 97206) { // 第四階段地面戰
					final int i = 7200;
					final ArrayList<L1Character> target = npc.getHateList().toTargetArrayList();
					for (final L1Character cha : target) {
						if (cha instanceof L1PcInstance) {
							final L1PcInstance pc = (L1PcInstance) cha;
							pc.broadcastPacketX10(new S_SkillSound(pc.getId(), 7854));
							pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGON_BLOOD_ICON, 88, i / 60));
							pc.addFire(50);
							pc.sendPackets(new S_OwnCharAttrDef(pc));
							pc.setSkillEffect(DRAGON_BLOOD_3, i * 1000);
						}
					}

					Thread.sleep(5000L);

					sendServerMessage(1772);
					Thread.sleep(5000L);

					sendServerMessage(1773);
					Thread.sleep(10000L);

					GeneralThreadPool.get().execute(new CountDownTimer(33718, 32506, (short) 4,
							WorldQuest.get().get(get_showId()), 0));
					boolean hasDoor = false;
					// 是否已經存在「隱匿的巨龍谷入口」
					for (final L1Object obj : World.get().getObject()) {
						if (obj instanceof L1NpcInstance) {
							final L1NpcInstance find_npc = (L1NpcInstance) obj;
							if (find_npc.getNpcId() == 70936) {
								hasDoor = true;
								return;
							}
						}
					}
					if (!hasDoor) {
						Thread.sleep(7000L);

						World.get().broadcastPacketToAll(new S_ServerMessage(1582));
						Thread.sleep(10000L);

						World.get().broadcastPacketToAll(new S_ServerMessage(1583));

						// 「隱匿的巨龍谷入口」將會存在現實6小時
						final L1Location loc = new L1Location(33725, 32506, 4);
						NpcSpawnTable.get().storeSpawn(70936, loc.getX(), loc.getY(), loc.getMapId(),
								21600000);
						L1SpawnUtil.spawn(70936, loc, 0, -1);
					}
				}
			} catch (final Exception e) {
			}
		}
	}

	private final void sendServerMessage(final int msgid) {
		final L1QuestUser quest = WorldQuest.get().get(get_showId());
		if (quest != null) {
			if (!quest.pcList().isEmpty()) {
				for (final L1PcInstance pc : quest.pcList()) {
					pc.sendPackets(new S_ServerMessage(msgid));
				}
			}
		}
	}

	private final class CountDownTimer extends TimerTask {
		private final int _loc_x;
		private final int _loc_y;
		private final short _loc_mapId;

		private final L1QuestUser _quest;
		private final int _firstMsgId;

		public CountDownTimer(final int loc_x, final int loc_y, final short loc_mapId,
				final L1QuestUser quest, final int firstMsgId) {
			_loc_x = loc_x;
			_loc_y = loc_y;
			_loc_mapId = loc_mapId;
			_quest = quest;
			_firstMsgId = firstMsgId;
		}

		@Override
		public void run() {
			try {
				if (_firstMsgId != 0) {
					sendServerMessage(_firstMsgId);
				}
				Thread.sleep(10000L);

				sendServerMessage(1476);
				Thread.sleep(10000L);

				sendServerMessage(1477);
				Thread.sleep(10000L);

				sendServerMessage(1478);
				Thread.sleep(5000L);

				sendServerMessage(1480);
				Thread.sleep(1000L);

				sendServerMessage(1481);
				Thread.sleep(1000L);

				sendServerMessage(1482);
				Thread.sleep(1000L);

				sendServerMessage(1483);
				Thread.sleep(1000L);

				sendServerMessage(1484);
				Thread.sleep(1000L);

				for (int i = 10; i > 0; i--) {
					if (_quest != null) {
						if (!_quest.pcList().isEmpty()) {
							for (final L1PcInstance pc : _quest.pcList()) {
								L1Teleport.teleport(pc, _loc_x, _loc_y, _loc_mapId, pc.getHeading(), true);
							}
						}
					}
					Thread.sleep(500L);
				}
			} catch (final Exception e) {
			}
		}
	}
}
