package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.MAGIC_ITEM_POWER_A;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;

import java.util.ConcurrentModificationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.gametime.L1GameTimeClock;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.poison.L1ParalysisPoison;
import com.lineage.server.model.poison.L1SilencePoison;
import com.lineage.server.serverpackets.S_AttackPacketNpc;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillIconPoison;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.types.Point;

/**
 * 攻擊判定
 * 
 * @author dexc
 */
public class L1AttackNpc extends L1AttackMode {

	private static final Log _log = LogFactory.getLog(L1AttackNpc.class);

	public L1AttackNpc(final L1NpcInstance attacker, final L1Character target) {
		if (attacker == null) {
			return;
		}
		if (target == null) {
			return;
		}
		if (target.isDead()) {
			return;
		}
		if (target.getCurrentHp() <= 0) {
			return;
		}
		_npc = attacker;
		if (target instanceof L1PcInstance) {
			_targetPc = (L1PcInstance) target;
			_calcType = NPC_PC;

		} else if (target instanceof L1NpcInstance) {
			_targetNpc = (L1NpcInstance) target;
			_calcType = NPC_NPC;
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/**
	 * 命中判定
	 */
	@Override
	public boolean calcHit() {
		if (_target == null) {// 物件遺失
			_isHit = false;
			return _isHit;
		}
		switch (_calcType) {
		case NPC_PC:
			_isHit = calcPcHit();
			break;

		case NPC_NPC:
			_isHit = calcNpcHit();
			break;
		}
		return _isHit;
	}

	/**
	 * NPC對PC命中
	 * 
	 * @return
	 */
	private boolean calcPcHit() {
		if (((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance))
				&& (_targetPc.getZoneType() == 1)) {
			return false;
		}
		// 傷害為0
		if (dmg0(_targetPc)) {
			return false;
		}

		// 迴避攻擊
		if (calcEvasion()) {
			return false;
		}

		// this._hitRate += this._npc.getLevel();// XXX
		_hitRate += _npc.getLevel() + 5;

		if (_npc instanceof L1PetInstance) { // 寵物武器命中追加
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		// int attackerDice = _random.nextInt(20) + 1 + this._hitRate - 1;// XXX
		int attackerDice = (_random.nextInt(20) + 1 + _hitRate) - 3;

		// 技能增加閃避
		attackerDice += attackerDice(_targetPc);

		// 防禦力抵銷
		int defenderDice = 0;

		final int defenderValue = (_targetPc.getAc()) * -1;

		if (_targetPc.getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAc();

		} else if (_targetPc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		// 基礎命中
		final int fumble = _hitRate;
		// 基礎命中 + 19
		final int critical = _hitRate + 19;

		if (attackerDice <= fumble) {
			_hitRate = 15;

		} else if (attackerDice >= critical) {
			_hitRate = 100;

		} else {
			// 防禦力抵銷
			if (attackerDice > defenderDice) {
				_hitRate = 100;

			} else if (attackerDice <= defenderDice) {
				_hitRate = 15;
			}
		}

		// BOSS XXX
		if (_npc.getNpcTemplate().is_boss()) {
			attackerDice += 30;// 2011-12-10(15)
		}

		// 比較用機率
		final int rnd = _random.nextInt(100) + 1;

		// NPC攻擊距離2格以上附加ER計算
		if ((_npc.get_ranged() >= 10) && (_hitRate > rnd)
				&& (_npc.getLocation().getTileLineDistance(new Point(_targetX, _targetY)) >= 2)) {
			return calcErEvasion();
		}

		return _hitRate >= rnd;
	}

	/**
	 * NPC對NPC命中
	 * 
	 * @return
	 */
	private boolean calcNpcHit() {
		// 傷害為0
		if (dmg0(_targetNpc)) {
			return false;
		}

		// this._hitRate += this._npc.getLevel();// XXX
		_hitRate += _npc.getLevel() + 3;

		if (_npc instanceof L1PetInstance) { // 寵物武器命中追加
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		// int attackerDice = _random.nextInt(20) + 1 + this._hitRate - 1;// XXX
		int attackerDice = (_random.nextInt(20) + 1 + _hitRate) - 3;

		// 技能增加閃避
		attackerDice += attackerDice(_targetNpc);

		// BOSS XXX
		if (_npc.getNpcTemplate().is_boss()) {
			attackerDice += 30;// 2011-12-10(10)
		}

		int defenderDice = 0;

		final int defenderValue = (_targetNpc.getAc()) * -1;

		if (_targetNpc.getAc() >= 0) {
			defenderDice = 10 - _targetNpc.getAc();

		} else if (_targetNpc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		final int fumble = _hitRate;
		final int critical = _hitRate + 19;

		if (attackerDice <= fumble) {
			_hitRate = 15;

		} else if (attackerDice >= critical) {
			_hitRate = 100;

		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;

			} else if (attackerDice <= defenderDice) {
				_hitRate = 15;
			}
		}

		final int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	/**
	 * 傷害計算
	 */
	@Override
	public int calcDamage() {
		switch (_calcType) {
		case NPC_PC:
			_damage = calcPcDamage();
			break;

		case NPC_NPC:
			_damage = calcNpcDamage();
			break;
		}
		return _damage;
	}

	/**
	 * NPC基礎傷害提昇計算
	 * 
	 * @return
	 */
	private double npcDmgMode(double dmg) {
		// 暴擊
		if (_random.nextInt(100) < 15) {
			dmg *= 1.80;
		}

		dmg += _npc.getDmgup();

		if (isUndeadDamage()) {
			dmg *= 1.20;
		}

		dmg = (int) (dmg * (getLeverage() / 10D));

		// BOSS XXX
		if (_npc.getNpcTemplate().is_boss()) {
			dmg *= 1.80;// 2011-12-10(1.45)
		}

		if (_npc.isWeaponBreaked()) { // ＮＰＣがウェポンブレイク中。
			dmg /= 2;
		}

		return dmg;
	}

	/**
	 * NPC對PC傷害
	 * 
	 * @return
	 */
	private int calcPcDamage() {
		if (_targetPc == null) {
			return 0;
		}
		// 傷害為0
		if (dmg0(_targetPc)) {
			_isHit = false;
			return 0;
		}

		// 受到物理傷害無效化 by terry0412
		if (_targetPc.hasSkillEffect(MAGIC_ITEM_POWER_A)) {
			return 0;
		}

		final int lvl = _npc.getLevel();
		double dmg = 0D;

		final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
		dmg = _random.nextInt(lvl) + (_npc.getStr() * 0.8) + dmgStr;

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 7); // 每7級追加1點攻擊力 XXX
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

		// NPC基礎傷害提昇計算
		dmg = npcDmgMode(dmg);

		// 物理傷害減免+% by terry0412
		if (_targetPc.getPhysicsDmgDown() != 0) {
			dmg -= (double) (dmg * _targetPc.getPhysicsDmgDown() / 100);
		}

		dmg -= calcPcDefense();// 被攻擊者防禦力傷害減低

		dmg -= _targetPc.getDamageReductionByArmor(); // 被攻擊者防具額外傷害減免

		dmg -= _targetPc.dmgDowe(); // 機率傷害減免

		if (_targetPc.getClanid() != 0) {
			dmg -= getDamageReductionByClan(_targetPc);// 被攻擊者血盟技能傷害減免
		}

		// 護甲身軀
		if (_targetPc.isWarrior() && _targetPc.isARMORGARDE()) {
			dmg += _targetPc.getAc() / 10;
		}

		// 增幅防禦 repaired by terry0412
		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			if (_targetPc.getLevel() >= 50) {
				dmg -= Math.min(((_targetPc.getLevel() - 50) / 5) + 1, 7);
			}
		}

		boolean dmgX2 = false;// 傷害除2
		// 取回技能
		if (!_targetPc.getSkillisEmpty() && (_targetPc.getSkillEffect().size() > 0)) {
			try {
				for (final Integer key : _targetPc.getSkillEffect()) {
					final Integer integer = L1AttackList.SKD3.get(key);
					if (integer != null) {
						if (integer.equals(key)) {
							dmgX2 = true;

						} else {
							dmg += integer;
						}
					}
				}

			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		if (dmgX2) {
			dmg /= 2;
		}

		// ペット、サモンからプレイヤーに攻撃
		boolean isNowWar = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
		if (castleId > 0) {
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_npc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_npc instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) _npc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}

		if (_targetPc.isWarrior() && (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100) * 40// 血量低於40%
		)) && _targetPc.isCrystal()) {// 足夠魔法結晶體
			if (!isShortDistance()) {
				if (_targetPc.isTITANBULLET() && (_random.nextInt(100) < ConfigRate.WARRIOR_TITANBULLET)) {
					dmg = 0;
					actionTitan(true);
					commitTitan(_targetPc.colcTitanDmg());
				}
			} else {
				if (_targetPc.isTITANROCK() && (_random.nextInt(100) < ConfigRate.WARRIOR_TITANROCK)) {
					dmg = 0;
					actionTitan(false);
					commitTitan(_targetPc.colcTitanDmg());
				}
			}
		}

		dmg *= coatArms();

		if (dmg <= 0) {
			_isHit = false;

		} else {
			// 武裝色霸氣 (機率受到物理攻擊會被反震暈隨機1~2秒) by terry0412
			/*
			 * if (_targetPc.hasSkillEffect(DOMINATE_POWER_C) &&
			 * _targetPc.getValue() > _random.nextInt(100)) { final int time =
			 * _random.nextInt(2) + 1; _npc.setSkillEffect(L1SkillId.SHOCK_STUN,
			 * time * 1000); // 騎士技能(衝擊之暈) L1SpawnUtil.spawnEffect(81162, time,
			 * _npc.getX(), _npc.getY(), _npc.getMapId(), _targetPc, 0);
			 * _npc.setParalyzed(true); }
			 */
		}

		addNpcPoisonAttack(_targetPc);

		// 未命中 傷害歸0
		if (!_isHit) {
			dmg = 0.0;
		}

		return (int) dmg;
	}

	/**
	 * NPC對NPC傷害
	 * 
	 * @return
	 */
	private int calcNpcDamage() {
		if (_targetNpc == null) {
			return 0;
		}

		// 傷害為0
		if (dmg0(_targetNpc)) {
			_isHit = false;
			return 0;
		}

		final int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			dmg = _random.nextInt(_npc.getNpcTemplate().get_level()) + (_npc.getStr() / 2) + 1;
			// dmg += (lvl / 16); // 每16級追加1點攻擊力// TEST
			dmg += (lvl / 14); // 每14級追加1點攻擊力 XXX
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();

		} else {
			final Integer dmgStr = L1AttackList.STRD.get((int) _npc.getStr());
			dmg = _random.nextInt(lvl) + (_npc.getStr() / 2) + dmgStr;
		}

		// NPC基礎傷害提昇計算
		dmg = npcDmgMode(dmg);

		dmg -= calcNpcDamageReduction();// NPC防禦力傷害減低

		addNpcPoisonAttack(_targetNpc);

		// 聖結界
		if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}

		if (dmg <= 0) {
			_isHit = false;
		}

		// 未命中 傷害歸0
		if (!_isHit) {
			dmg = 0.0;
		}

		return (int) dmg;
	}

	/**
	 * ＮＰＣのアンデッドの夜間攻撃力の変化
	 * 
	 * @return
	 */
	private boolean isUndeadDamage() {
		boolean flag = false;
		final int undead = _npc.getNpcTemplate().get_undead();
		final boolean isNight = L1GameTimeClock.getInstance().currentTime().isNight();
		if (isNight) {
			switch (undead) {
			case 1:
			case 3:
			case 4:
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * ＮＰＣの毒攻撃を付加
	 * 
	 * @param attacker
	 * @param target
	 */
	private void addNpcPoisonAttack(final L1Character target) {
		switch (_npc.getNpcTemplate().get_poisonatk()) {

		case 1:// 通常毒
			if (15 >= _random.nextInt(100) + 1) {
				// 3秒周期でダメージ5
				final boolean isDamagePoison = L1DamagePoison.doInfection(_npc, target, 3000, 5);
				if (isDamagePoison) {
					if (target instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) target;
						S_SkillIconPoison type = new S_SkillIconPoison(1, 30);
						pc.sendPackets(type);
					}
				}
			}
			break;

		case 2:// 沈黙毒 (毒素：禁言,時效120秒 2013年資料)
			if (15 >= _random.nextInt(100) + 1) {
				final boolean isSilencePoison = L1SilencePoison.doInfection(target);
				if (isSilencePoison) {
					if (target instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) target;
						S_SkillIconPoison type = new S_SkillIconPoison(6, 120);
						pc.sendPackets(type);
					}
				}
			}
			break;

		case 4:// 麻痺毒
			if (15 >= _random.nextInt(100) + 1) {
				L1ParalysisPoison.doInfection(target, 3000, 6000);
			}
			break;
		}
		if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // 麻痺攻撃あり
		}
	}

	/**
	 * 攻擊資訊送出
	 */
	@Override
	public void action() {
		try {
			if (_npc == null) {
				return;
			}
			if (_npc.isDead()) {
				return;
			}

			_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 設置新面向

			// 距離2格以上攻擊
			final boolean isLongRange = (_npc.getLocation()
					.getTileLineDistance(new Point(_targetX, _targetY)) > 1);
			final int bowActId = _npc.getBowActId();

			// 遠距離武器
			if (isLongRange && (bowActId > 0)) {
				actionX1();

				// 近距離武器
			} else {
				actionX2();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 遠距離武器
	 */
	private void actionX1() {
		try {
			final int bowActId = _npc.getBowActId();
			// 攻擊資訊封包
			_npc.broadcastPacketX10(
					new S_UseArrowSkill(_npc, _targetId, bowActId, _targetX, _targetY, _damage));

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 近距離武器
	 */
	private void actionX2() {
		try {
			int actId = 0;
			if (getActId() > 0) {
				actId = getActId();

			} else {
				actId = ActionCodes.ACTION_Attack;
			}

			if (_isHit) {// 命中
				if (getGfxId() > 0) {
					// 攻擊資訊封包
					_npc.broadcastPacketX10(new S_UseAttackSkill(_target, _npc.getId(), getGfxId(), _targetX,
							_targetY, actId, _damage));
				} else {
					gfx7049();
					_npc.broadcastPacketX10(new S_AttackPacketNpc(_npc, _target, actId, _damage));
				}

			} else {// 未命中
				if (getGfxId() > 0) {
					// 攻擊資訊封包
					_npc.broadcastPacketX10(new S_UseAttackSkill(_target, _npc.getId(), getGfxId(), _targetX,
							_targetY, actId, 0));
				} else {
					_npc.broadcastPacketX10(new S_AttackPacketNpc(_npc, _target, actId));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 幻術師外型 使用古奇獸
	 */
	private void gfx7049() {
		if (_npc.getStatus() != 58) {
			return;
		}
		boolean is = false;
		if ((_npc.getTempCharGfx() == 6671) && (_npc.getGfxId() == 6671)) {
			is = true;
		}
		if ((_npc.getTempCharGfx() == 6650) && (_npc.getGfxId() == 6650)) {
			is = true;
		}

		if (is) {// 幻術師外型 使用古奇獸
			_npc.broadcastPacketAll(new S_SkillSound(_npc.getId(), 7049));
		}
	}

	/**
	 * 計算結果反映
	 */
	@Override
	public void commit() {
		if (_isHit) {
			switch (_calcType) {
			case NPC_PC:
				commitPc();
				break;

			case NPC_NPC:
				commitNpc();
				break;
			}
		}

		// gm攻擊資訊
		if (!ConfigAlt.ALT_ATKMSG) {
			return;

		} else {
			if (_calcType == NPC_NPC) {
				return;
			}
			if (!_targetPc.isGm()) {
				return;
			}
		}

		final String srcatk = _npc.getName();
		;// 攻擊者
		final String tgatk = _targetPc.getName();
		;// 被攻擊者
		final String dmginfo = _isHit ? "傷害:" + _damage : "失敗";// 傷害
		final String hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetPc.getCurrentHp();// 資訊
		final String x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;

		_targetPc.sendPackets(new S_ServerMessage(166, "受到NPC攻擊: " + x));

	}

	/**
	 * 對PC攻擊傷害結果
	 */
	private void commitPc() {
		_targetPc.receiveDamage(_npc, _damage, false, false);
	}

	/**
	 * 對NPC攻擊傷害結果
	 */
	private void commitNpc() {
		_targetNpc.receiveDamage(_npc, _damage);
	}

	/**
	 * 受到反擊屏障傷害表示
	 */
	/*
	 * @Override public void actionCounterBarrier() { // 受傷動作
	 * _npc.broadcastPacketAll( new S_DoActionGFX(_npc.getId(),
	 * ActionCodes.ACTION_Damage )); }
	 */

	/**
	 * 相手の攻撃に対してカウンターバリアが有効かを判別
	 */
	@Override
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		final boolean isLongRange = (_npc.getLocation()
				.getTileLineDistance(new Point(_targetX, _targetY)) > 1);
		final int bowActId = _npc.getBowActId();
		// 距離が2以上、攻撃者の弓のアクションIDがある場合は遠攻撃
		if (isLongRange && (bowActId > 0)) {
			isShortDistance = false;
		}
		return isShortDistance;
	}

	/**
	 * 反擊屏障的傷害反擊
	 */
	@Override
	public void commitCounterBarrier() {
		final int damage = calcCounterBarrierDamage();
		if (damage == 0) {
			return;
		}
		_npc.receiveDamage(_target, damage);
		// NPC受傷動作特效 161226 補上送出特效
		_npc.broadcastPacketAll(new S_SkillSound(_targetId, 10710));
		// 受傷動作
		_npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
	}
}