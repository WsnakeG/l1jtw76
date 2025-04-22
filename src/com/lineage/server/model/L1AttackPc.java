package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.ARMOR_BREAK;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.BRAVE_MENTAL;
import static com.lineage.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static com.lineage.server.model.skill.L1SkillId.BURNING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_A;
import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_C;
import static com.lineage.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static com.lineage.server.model.skill.L1SkillId.ENCHANT_VENOM;
import static com.lineage.server.model.skill.L1SkillId.FIRE_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.MAGIC_ITEM_POWER_A;
import static com.lineage.server.model.skill.L1SkillId.MAGIC_ITEM_POWER_C;
import static com.lineage.server.model.skill.L1SkillId.MAZU_STATUS;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static com.lineage.server.model.skill.L1SkillId.STATUS_POISON_PARALYZED;

import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigOther;
import com.lineage.config.ConfigRate;
import com.lineage.data.event.FeatureItemSet;
import com.lineage.data.event.RedBlueSet;
import com.lineage.server.ActionCodes;
import com.lineage.server.model.Instance.L1DollInstance;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.poison.L1DamagePoison;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.weaponskill.WeaponSkillStart;
import com.lineage.server.serverpackets.S_Attack;
import com.lineage.server.serverpackets.S_AttackPacketPc;
import com.lineage.server.serverpackets.S_ChangeHeading;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_PacketBoxDk;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseArrowSkill;
import com.lineage.server.templates.L1MagicWeapon;
import com.lineage.server.timecontroller.server.ServerWarExecutor;
import com.lineage.server.utils.L1SpawnUtil;

/**
 * 物理攻擊判斷項(PC)
 * 
 * @author dexc
 */
public class L1AttackPc extends L1AttackMode {

	private static final Log _log = LogFactory.getLog(L1AttackPc.class);

	// 攻擊模式 0x00:none 0x02:暴擊 0x04:雙擊 0x08:鏡反射
	private byte _attackType = 0x00;

	public L1AttackPc(final L1PcInstance attacker, final L1Character target) {
		if (target == null) {
			return;
		}

		if (target.isDead()) {
			return;
		}

		_pc = attacker;

		// 161106 加入沉睡之霧攻擊判定
		if (_pc.hasSkillEffect(FOG_OF_SLEEPING)) {
			return;
		}

		// 170105 加入麻痺效果攻擊判定
		if (_pc.hasSkillEffect(STATUS_POISON_PARALYZED)) {
			// System.out.println("木乃伊麻痺開始");
			return;
		}

		// 陣營戰同隊之間無法攻擊判定
		if (target instanceof L1PcInstance) {
			_targetPc = (L1PcInstance) target;
			_calcType = PC_PC;
			if (_targetPc.get_redbluejoin() != 0 && _pc.get_redbluejoin() != 0) {
				if (_targetPc.get_redbluejoin() == _pc.get_redbluejoin() || (_pc.get_redblueroom() == 1 && RedBlueSet.step1 == 3) || (_pc.get_redblueroom() == 2 && RedBlueSet.step2 == 3)) {
					return;
				}
			}

		} else if (target instanceof L1NpcInstance) {
			_targetNpc = (L1NpcInstance) target;
			_calcType = PC_NPC;
		}

		// 武器情報の取得
		_weapon = _pc.getWeapon();

		if (_pc.isWarrior() && (_pc.getWeaponWarrior() != null)) {
			if (_pc.is_change_weapon()) {
				_weapon = _pc.getWeaponWarrior();
				_pc.set_change_weapon(false);
			} else {
				_pc.set_change_weapon(true);
			}
		}

		if (_weapon != null) {
			_weaponId = _weapon.getItem().getItemId();
			_weaponType = _weapon.getItem().getType1();
			_weaponType2 = _weapon.getItem().getType();
			_weaponAddHit = _weapon.getItem().getHitModifier() + _weapon.getHitByMagic();
			_weaponAddDmg = _weapon.getItem().getDmgModifier() + _weapon.getDmgByMagic();

			_weaponSmall = _weapon.getItem().getDmgSmall();
			_weaponLarge = _weapon.getItem().getDmgLarge();
			_weaponRange = _weapon.getItem().getRange();
			_weaponBless = _weapon.getItem().getBless();

			if ((_weaponType != 20) && (_weaponType != 62)) {
				_weaponEnchant = _weapon.getEnchantLevel() - _weapon.get_durability(); // 損傷分マイナス

			} else {
				_weaponEnchant = _weapon.getEnchantLevel();
			}

			_weaponMaterial = _weapon.getItem().getMaterial();
			if (_weaponType == 20) {// 弓 武器類型:箭取回
				_arrow = _pc.getInventory().getArrow();
				if (_arrow != null) {
					_weaponBless = _arrow.getItem().getBless();
					_weaponMaterial = _arrow.getItem().getMaterial();
				}
			}

			if (_weaponType == 62) {// 鐵手甲 武器類型:飛刀取回
				_sting = _pc.getInventory().getSting();
				if (_sting != null) {
					_weaponBless = _sting.getItem().getBless();
					_weaponMaterial = _sting.getItem().getMaterial();
				}
			}

			_weaponDoubleDmgChance = _weapon.getItem().getDoubleDmgChance();
			_weaponAttrEnchantKind = _weapon.getAttrEnchantKind();
			_weaponAttrEnchantLevel = _weapon.getAttrEnchantLevel();
		}

		// ステータスによる追加ダメージ補正
		if (_weaponType == 20) {// 弓 增加敏捷傷害
			final Integer dmg = L1AttackList.DEXD.get((int) _pc.getDex());
			if (dmg != null) {
				_statusDamage = dmg;
			}

		} else { // それ以外はＳＴＲ値参照abstract
			final Integer dmg = L1AttackList.STRD.get((int) _pc.getStr());
			if (dmg != null) {
				_statusDamage = dmg;
			}
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

		if (_weaponRange != -1) {

			// 近距離武器攻擊距離判斷
			final int location = _pc.getLocation().getTileLineDistance(_target.getLocation());

			if (location > (_weaponRange + 1)) {
				_isHit = false; // 射程範囲外
				return _isHit;
			}

		} else {
			// 遠距離武器攻擊距離判斷
			if (!_pc.getLocation().isInScreen(_target.getLocation())) {
				_isHit = false; // 射程範囲外
				return _isHit;
			}
		}

		if ((_weaponType == 20) && (_weaponId != 190) && (_arrow == null)) {
			_isHit = false; // 持弓 無箭

		} else if ((_weaponType == 62) && (_sting == null)) {
			_isHit = false; // 持鐵手甲 無飛刀

		} else if (!_pc.glanceCheck(_targetX, _targetY)) {
			_isHit = false; // 攻擊方向中途具有障礙

		} else if ((_weaponId == 247) || (_weaponId == 248) || (_weaponId == 249)) {
			_isHit = false; // 試煉武器

		} else if (_calcType == PC_PC) {
			_isHit = calcPcHit();// PC TO PC

		} else if (_calcType == PC_NPC) {
			_isHit = calcNpcHit();// PC TO NPC
		}

		return _isHit;
	}

	private int str_dex_Hit() {
		int hitRate = 0;
		// 力量命中補正
		final Integer hitStr = L1AttackList.STRH.get(_pc.getStr() - 1);
		if (hitStr != null) {
			hitRate += hitStr;

		} else {
			hitRate += 19;
		}

		// 敏捷命中補正
		final Integer hitDex = L1AttackList.DEXH.get(_pc.getDex() - 1);
		if (hitDex != null) {
			hitRate += hitDex;

		} else {
			hitRate += 29;
		}
		return hitRate;
	}

	/**
	 * PC對PC的命中
	 * 
	 * @return
	 */
	private boolean calcPcHit() {
		if (_targetPc == null) {
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

		if (_weaponType2 == 17) {// 奇古獸
			return true;
		}

		_hitRate = _pc.getLevel();
		_hitRate += _pc.getClassFeature().getHitLevel(_pc.getLevel());// 職業物理命中補正
		// 力量命中補正 / 敏捷命中補正
		_hitRate += str_dex_Hit();

		if ((_weaponType != 20) && (_weaponType != 62)) {
			_hitRate += (_weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup() + (_weaponEnchant * 0.6));

		} else {
			_hitRate += (_weaponAddHit + _pc.getBowHitup() + _pc.getOriginalBowHitup() + (_weaponEnchant * 0.6));
		}

		if ((_weaponType != 20) && (_weaponType != 62)) { // 防具による追加命中
			_hitRate += _pc.getHitModifierByArmor();

		} else {
			_hitRate += _pc.getBowHitModifierByArmor();
		}

		final int weight240 = _pc.getInventory().getWeight240();
		if (weight240 > 80) { // 重量による命中補正
			if ((80 < weight240) && (120 >= weight240)) {
				_hitRate -= 1;

			} else if ((121 <= weight240) && (160 >= weight240)) {
				_hitRate -= 3;

			} else if ((161 <= weight240) && (200 >= weight240)) {
				_hitRate -= 5;
			}
		}

		_hitRate += hitUp();

		if (_pc.hasSkillEffect(MAZU_STATUS)) {// 媽祖祝福
			_hitRate += 1;
		}

		int attackerDice = (_random.nextInt(20) + 2 + _hitRate) - 10;

		// 技能增加閃避
		attackerDice += attackerDice(_targetPc);

		int defenderDice = 0;

		final int defenderValue = (int) (_targetPc.getAc() * 1.5) * -1;

		if (_targetPc.getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAc();

		} else if (_targetPc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}

		final int fumble = _hitRate - 9;
		final int critical = _hitRate + 10;
		if (_pc.isDragonKnight()) {
			attackerDice *= 1.01;
		} else if (_pc.isElf()) {
			if (_pc.getElfAttr() == 2) {
				attackerDice *= 1.02;
			}
		} else if (_pc.isWizard()) { // 法師額外增加命中 by terry0412
			attackerDice += 5;
		}

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
		if (_weaponType == 20) {// 弓 附加ER計算
			if (_hitRate > rnd) {
				return calcErEvasion();
			}
		}
		return _hitRate >= rnd;
	}

	/**
	 * PC對NPC的命中
	 * 
	 * @return
	 */
	private boolean calcNpcHit() {
		// 對不可見的怪物額外判斷
		final int gfxid = _targetNpc.getNpcTemplate().get_gfxid();
		switch (gfxid) {
		case 2412:// 南瓜的影子
			if (!_pc.getInventory().checkEquipped(20046)) {// 南瓜帽
				return false;
			}
			break;
		}

		// 傷害為0
		if (dmg0(_targetNpc)) {
			return false;
		}

		// NPC商店模式
		if (_targetNpc.isShop()) {
			return false;
		}

		if (_weaponType2 == 17) {// 奇古獸 命中100%
			return true;
		}

		// ＮＰＣへの命中率
		// ＝（PCのLv＋クラス補正＋STR補正＋DEX補正＋武器補正＋DAIの枚数/2＋魔法補正）×5−{NPCのAC×（-5）}
		_hitRate = _pc.getLevel();

		// 力量命中補正 / 敏捷命中補正
		_hitRate += str_dex_Hit();

		if ((_weaponType != 20) && (_weaponType != 62)) {
			_hitRate += (_weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup() + (_weaponEnchant * 0.6));

		} else {
			_hitRate += (_weaponAddHit + _pc.getBowHitup() + _pc.getOriginalBowHitup() + (_weaponEnchant * 0.6));
		}

		if ((_weaponType != 20) && (_weaponType != 62)) { // 防具による追加命中
			_hitRate += _pc.getHitModifierByArmor();

		} else {
			_hitRate += _pc.getBowHitModifierByArmor();
		}

		final int weight240 = _pc.getInventory().getWeight240();
		if (weight240 > 80) { // 重量による命中補正
			if ((80 < weight240) && (120 >= weight240)) {
				_hitRate -= 1;

			} else if ((121 <= weight240) && (160 >= weight240)) {
				_hitRate -= 3;

			} else if ((161 <= weight240) && (200 >= weight240)) {
				_hitRate -= 5;
			}
		}

		_hitRate += hitUp();

		if (_pc.hasSkillEffect(MAZU_STATUS)) {// 媽祖祝福
			_hitRate += 1;
		}

		int attackerDice = (_random.nextInt(20) + 2 + _hitRate) - 10;

		// 技能增加閃避
		attackerDice += attackerDice(_targetNpc);

		final int defenderDice = 10 - _targetNpc.getAc();

		final int fumble = _hitRate - 9;
		final int critical = _hitRate + 10;
		if (_pc.isDragonKnight()) {
			attackerDice *= 1.01;
		}
		if (_pc.isElf()) {
			if (_pc.getElfAttr() == 2) {
				attackerDice *= 1.02;
			}
		}

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

		final int npcId = _targetNpc.getNpcTemplate().get_npcId();

		final Integer tgskill = L1AttackList.SKNPC.get(npcId);
		if (tgskill != null) {
			if (!_pc.hasSkillEffect(tgskill)) {
				_hitRate = 0;
			}
		}

		final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
		if (tgpoly != null) {
			if (tgpoly.equals(_pc.getTempCharGfx())) {
				_hitRate = 0;
			}
		}

		final int rnd = _random.nextInt(100) + 1;

		return _hitRate >= rnd;
	}

	/**
	 * 追加命中
	 * 
	 * @return
	 */
	private int hitUp() {
		int hitUp = 0;
		if (_pc.getSkillEffect().size() <= 0) {
			return hitUp;
		}

		if (!_pc.getSkillisEmpty()) {
			try {
				// 追加命中(近距離武器)
				if ((_weaponType != 20) && (_weaponType != 62)) {
					for (final Integer key : _pc.getSkillEffect()) {
						final Integer integer = L1AttackList.SKU1.get(key);
						if (integer != null) {
							hitUp += integer;
						}
					}

					// 追加命中(遠距離武器)
				} else {
					for (final Integer key : _pc.getSkillEffect()) {
						final Integer integer = L1AttackList.SKU2.get(key);
						if (integer != null) {
							hitUp += integer;
						}
					}
				}

			} catch (final ConcurrentModificationException e) {
				// 技能取回發生其他線程進行修改
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		return hitUp;
	}

	/**
	 * 傷害計算
	 */
	@Override
	public int calcDamage() {
		switch (_calcType) {
		case PC_PC:
			_damage = calcPcDamage();
			break;

		case PC_NPC:
			_damage = calcNpcDamage();
			break;
		}
		if (_damage > 0) {
			// 霸王色霸氣 (固定增加傷害) by terry0412
			if (_pc.hasSkillEffect(DOMINATE_POWER_A)) {
				_damage += _pc.getValue();
			}
			int outcount = 0;
			// 特殊魔法武器
			if (_weapon != null) {
				if (FeatureItemSet.POWER_START) {
					final L1AttackPower attackPower = new L1AttackPower(_pc, _target, _weaponAttrEnchantKind, _weaponAttrEnchantLevel);
					final int add = attackPower.set_item_power(_damage);
					if (add > 0) {
						_damage += add;
						outcount++;
					}
				}

				// 武器魔法DIY系統 by terry0412
				if (_weapon.get_magic_weapon() != null) {
					boolean isLongRange;
					// 弓 or 鐵手甲
					if ((_weaponType == 20) || (_weaponType == 62)) {
						isLongRange = true;
					} else {
						isLongRange = false;
					}
					_damage += L1MagicWeapon.getWeaponSkillDamage(_pc, _target, _damage, _weapon.get_magic_weapon(), isLongRange);
				}
				// 近距離武器使用破壞盔甲傷害

				if ((_weaponType != 20) && (_weaponType != 62)) {
					if (_target.hasSkillEffect(ARMOR_BREAK)) {
						_damage *= 1.58;
					}
				}
				// 武器+9(含)以上附加額外增加傷害值 by terry0412
				if (ConfigOther.WEAPON_POWER && (_weaponEnchant >= 9)) { // 強化值大於等於9
					_damage += ConfigOther.WEAPON_POWER_LIST[Math.min(_weaponEnchant - 9, ConfigOther.WEAPON_POWER_LIST.length - 1)];
				}
			}
			if (outcount > 1) {
				_damage /= outcount;
			}
		}
		return _damage;
	}

	/**
	 * 傷害質初始化
	 * 
	 * @param weaponMaxDamage
	 *            可發出的最大攻擊質
	 * @return
	 */
	private int weaponDamage1(final int weaponMaxDamage) {
		int weaponDamage = 0;
		// 武器類型核心分類
		switch (_weaponType2) {
		case 0:// 空手
		case 4:// 弓
		case 10:// 鐵手甲
		case 13:// 弓(單手)
			break;

		case 11:// 鋼爪
			if ((_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) {
				weaponDamage = weaponMaxDamage;
				if ((_random.nextInt(100) + 1) < (_pc.getLevel()/5+1)) { //新增鋼爪依等級機率再次爆擊 by WS
					weaponDamage *= 1.6;
				}
				_attackType = 0x02;
			}
			break;

		case 15:// 雙手斧
		case 1:// 劍
		case 2:// 匕首
		case 3:// 雙手劍
		case 5:// 矛(雙手)
		case 6:// 斧(單手)
		case 7:// 魔杖
		case 8:// 飛刀
		case 9:// 箭
		case 12:// 雙刀
		case 14:// 矛(單手)
		case 16:// 魔杖(雙手)
		case 17:// 奇古獸
		case 18:// 鎖鏈劍
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
			break;
		}

		// 烈焰之魂
		if ((weaponDamage != 0) && _pc.hasSkillEffect(SOUL_OF_FLAME)) {
			// 在時效內，近距離武器的攻擊力最大化。並且隨機增加(0-50的傷害值)
			// changed by terry0412 (原_random.nextInt(51);
			// 在時效內，近距離武器的攻擊力最大化。並且隨機增加(0-101的傷害值)
			// 07/01 changed by erics4179 (更動數值101)
			weaponDamage = weaponMaxDamage + _random.nextInt(101);

		}

		if (_pc.getClanid() != 0) {
			weaponDamage += getDamageUpByClan(_pc);// 血盟技能傷害提升
		}
		switch (_pc.guardianEncounter()) {
		case 3:// 邪惡的守護 Lv.1
			weaponDamage += 1;
			break;

		case 4:// 邪惡的守護 Lv.2
			weaponDamage += 3;
			break;

		case 5:// 邪惡的守護 Lv.3
			weaponDamage += 5;
			break;
		}
		return weaponDamage;
	}

	/**
	 * 傷害質最終計算
	 * 
	 * @param weaponTotalDamage
	 * @return
	 */
	private double weaponDamage2(final double weaponTotalDamage) {
		double dmg = 0.0;
		boolean doubleBrake = false;// 技能(雙重破壞)
		switch (_weaponType2) {
		case 1:// 劍
		case 2:// 匕首
		case 3:// 雙手劍
		case 5:// 矛(雙手)
		case 6:// 斧(單手)
		case 7:// 魔杖
		case 8:// 飛刀
		case 9:// 箭
		case 14:// 矛(單手)
		case 15:// 雙手斧
		case 16:// 魔杖(雙手)
		case 18:// 鎖鏈劍
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup() + _pc.getOriginalDmgup();
			break;

		case 11:// 鋼爪
			// 技能(雙重破壞)
			if (_pc.hasSkillEffect(DOUBLE_BRAKE)) {
				doubleBrake = true;
			}
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup() + _pc.getOriginalDmgup();
			dmg += 5.0;
			break;

		case 12:// 雙刀
			// 技能(雙重破壞)
			if (_pc.hasSkillEffect(DOUBLE_BRAKE)) {
				doubleBrake = true;
			}
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup() + _pc.getOriginalDmgup();
			// 雙刀重擊
			if ((_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) {
				_attackType = 0x04;
				dmg *= 1.60;// 2012-01-30(1.80) 2014/07/01(1.60)
				if ((_random.nextInt(100) + 1) < (_pc.getLevel()/5+1)) { //新增雙刀依等級機率再次爆擊 By WS
					dmg *= 2;
				}
				int actid = 1;// 預設攻擊動作
				int[] data = null; // 攻擊封包的參數
				int OutGfxId = 13417; // 輸出的圖示代碼
				data = new int[] { actid, _damage, OutGfxId, 0 }; // 參數
				S_Attack atk = new S_Attack(_target, _target, data);
				_pc.sendPacketsAll(atk); // 對自身非自身送出
			}
			break;

		case 0:// 空手
			dmg = (_random.nextInt(5) + 4) >> 2;// / 4;
			break;

		case 4:// 弓
		case 13:// 弓(單手)
			double add = _statusDamage;
			switch (_calcType) {
			case PC_PC:
				add *= 1.8D;// 2016-12-29(2.5)
				break;

			case PC_NPC:
				add *= 1.2D;// 2016-12-29(1.5)
				break;
			}
			dmg = weaponTotalDamage + add + _pc.getBowDmgup() + _pc.getOriginalBowDmgup();
			if (_arrow != null) {
				final int add_dmg = Math.max(_arrow.getItem().getDmgSmall(), 1);
				dmg += _random.nextInt(add_dmg) + 1;

			} else if (_weaponId == 190) { // 沙哈之弓
				dmg += _random.nextInt(15) + 1;
			}
			break;

		case 10:// 鐵手甲
			dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup() + _pc.getOriginalBowDmgup();
			final int add_dmg = Math.max(_arrow.getItem().getDmgSmall(), 1);
			dmg += _random.nextInt(add_dmg) + 1;
			break;

		case 17:// 奇古獸
//			dmg += weaponTotalDamage;
//			dmg += (L1WeaponSkill.getKiringkuDamage(_pc, _target) * 1.2D);// 2016-12-29(0.9)
			dmg = L1WeaponSkill.getKiringkuDamage(_pc, _target);
//			dmg += weaponTotalDamage + calcAttrEnchantDmg();
			break;
		}

		// 技能(雙重破壞)
		if (doubleBrake) {
			if ((_random.nextInt(100) + 1) <= 23) {
				dmg *= 1.80;// 2010-11-26(1.89)
			}
		}

		if (_weaponType2 != 0) {
			final int add_dmg = _weapon.getItem().get_add_dmg();
			if (add_dmg != 0) {
				dmg += add_dmg;
			}
		}

		// 受到近距離傷害將以兩倍反擊傷害回去 by terry0412
		if ((_calcType == PC_PC) && (dmg > 0) && (_weaponRange != -1) && _targetPc.hasSkillEffect(MAGIC_ITEM_POWER_C)) {
			// _pc.sendPacketsX10(new S_DoActionGFX(
			// _pc.getId(), ActionCodes.ACTION_Damage));
			_pc.receiveDamage(_targetPc, dmg * 2, false, true);
		}
		if (_weaponType2 != 12) {
			if (_weaponDoubleDmgChance != 0) {
				if ((_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) {
					int actid = 1;// 預設攻擊動作
					int[] data = null; // 攻擊封包的參數
					int OutGfxId = -1; // 輸出的圖示代碼
					if (_weaponType2 == 1) {// 한손검 單手劍
						OutGfxId = 13411;
					} else if (_weaponType2 == 2) {// 단검 匕首
						OutGfxId = 13412;
					} else if (_weaponType2 == 3) {// 양손검 雙手劍
						OutGfxId = 13410;
					} else if (_weaponType2 == 4 || _weaponType2 == 13) {// 弓(雙/單)
						OutGfxId = 13412;
					} else if (_weaponType2 == 5 || _weaponType2 == 14) {// 창
																			// 矛(雙/單)
						OutGfxId = 13403;
					} else if (_weaponType2 == 6 || _weaponType2 == 15) {// 도끼
																			// 斧(單/雙)
						if (_pc.getWeapon() != null && _pc.getWeaponWarrior() != null) {
							OutGfxId = 13415;
						} else {
							OutGfxId = 13414;
						}
					} else if (_weaponType2 == 7 || _weaponType2 == 16) {// 둔기
																			// 魔仗(單/雙)
						OutGfxId = 13413;
					} else if (_weaponType2 == 11) {// 크로우 鋼爪
						OutGfxId = 13416;
					} else if (_weaponType2 == 17) {// 키링크 奇古獸
						OutGfxId = 13397;
					} else if (_weaponType2 == 18) {// 체인소드 鎖鏈劍
						OutGfxId = 13409;
					}
					dmg *= 1.60;
					data = new int[] { actid, _damage, OutGfxId, 0 }; // 參數
					S_Attack atk = new S_Attack(_target, _target, data);
					_pc.sendPacketsAll(atk); // 對自身非自身送出
				}
			}
		}

		return dmg;
	}

	/**
	 * PC基礎傷害提昇計算
	 * 
	 * @param dmg
	 * @param weaponTotalDamage
	 * @return
	 */
	private double pcDmgMode(double dmg, final double weaponTotalDamage) {

		dmg = calcBuffDamage(dmg);

		dmg += weaponSkill(_pc, _target, weaponTotalDamage);// 武器附加魔法
//		dmg += _pc.getClassFeature().getAttackLevel(_pc.getLevel());// 職業傷害補正

		addPcPoisonAttack(_target);

		if ((_weaponType != 20) && (_weaponType != 62)) { // 防具追加傷害
			dmg += _pc.getDmgModifierByArmor();

		} else {
			dmg += _pc.getBowDmgModifierByArmor();
		}

		// 物理傷害增加+% by terry0412
		if (_pc.getPhysicsDmgUp() != 0) {
			dmg += (double) (dmg * _pc.getPhysicsDmgUp() / 100);
		}

		dmg += dmgUp();
		dmg += dk_dmgUp();
		dmg += calcCrash(_target);

		// 物理暴擊發動機率+% (發動後普攻傷害*1.5倍) by terry0412
		if (_pc.getPhysicsDoubleHit() != 0 && _random.nextInt(100) < _pc.getPhysicsDoubleHit()) {
			dmg *= 1.5;
			int actid = 1;// 預設攻擊動作
			int[] data = null; // 攻擊封包的參數
			int OutGfxId = 16157; // 輸出的圖示代碼
			data = new int[] { actid, _damage, OutGfxId, 0 }; // 參數
			S_Attack atk = new S_Attack(_target, _target, data);
			_pc.sendPacketsAll(atk); // 對自身非自身送出
			// _pc.sendPackets(new S_SkillSound(_pc.getId(), 16157));
		}

		return dmg;
	}

	private int calcCrash(final L1Character target) {
		int dmg = 0;
		if (!_pc.isWarrior()) {
			return dmg;
		}
		if (_pc.isCRASH() && ((_random.nextInt(100) + 1) <= ConfigRate.WARRIOR_CRASH)) {
			int crashdmg = (2 + _pc.getLevel()) - 45;
			int gfxid = 12487;
			if (_pc.isFURY() && ((_random.nextInt(100) + 1) <= ConfigRate.WARRIOR_FURY)) {
				crashdmg *= 2;
				gfxid = 12489;
			}
			dmg += crashdmg;
			_pc.sendPacketsAll(new S_SkillSound(target.getId(), gfxid));
		}
		return dmg;
	}

	/**
	 * PC對PC傷害計算
	 */
	public int calcPcDamage() {
		if (_targetPc == null) {
			return 0;
		}

		// 傷害為0
		if (dmg0(_targetPc)) {
			_isHit = false;
			_drainHp = 0;
			return 0;
		}

		// 受到物理傷害無效化 by terry0412
		if (_targetPc.hasSkillEffect(MAGIC_ITEM_POWER_A)) {
			return 0;
		}

		if (!_isHit) {
			return 0;
		}

		final int c3_power_type = c3_power();
		if (c3_power() != 0) {
			return c3_power_to_pc(c3_power_type);
		}

		final int weaponMaxDamage = _weaponSmall;

		// 傷害直初始化
		int weaponDamage = weaponDamage1(weaponMaxDamage);

		if (_pc.hasSkillEffect(MAZU_STATUS)) {// 媽祖祝福
			weaponDamage += 1;
		}

		double weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

		weaponTotalDamage += calcAttrEnchantDmg(); // 屬性強化

		// 傷害直最終計算
		double dmg = weaponDamage2(weaponTotalDamage);

		// PC基礎傷害提昇計算
		dmg = pcDmgMode(dmg, weaponTotalDamage);

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

		// 魔法娃娃特殊技能
		if (!_pc.getDolls().isEmpty()) {
			for (final Iterator<L1DollInstance> iter = _pc.getDolls().values().iterator(); iter.hasNext();) {
				final L1DollInstance doll = iter.next();
				doll.startDollSkill(_targetPc, dmg);
			}
		}

		dmg *= coatArms();

		if (_targetPc.isWarrior() && (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100) * 40// 血量低於40%
				)) && _targetPc.isCrystal()) {// 足夠魔法結晶體
			if ((_weaponType != 20) && (_weaponType != 62)) {
				if (_targetPc.isTITANROCK() && (_random.nextInt(100) < ConfigRate.WARRIOR_TITANROCK)) {
					dmg = 0;
					actionTitan(false);
					commitTitan(_targetPc.colcTitanDmg());
				}
			} else {
				if (_targetPc.isTITANBULLET() && (_random.nextInt(100) < ConfigRate.WARRIOR_TITANBULLET)) {
					dmg = 0;
					actionTitan(true);
					commitTitan(_targetPc.colcTitanDmg());
				}
			}
		}

		// 未命中傷害歸0
		if (!_isHit) {
			dmg = 0.0;
		}

		if (dmg <= 0) {
			_isHit = false;
			_drainHp = 0;

		} else {
			// 武裝色霸氣 (機率受到物理攻擊會被反震暈隨機1~2秒) by terry0412
			// 武裝色霸氣 (機率受到物理攻擊會被反震暈隨機2~3秒) by erics4179 07/01修改數值
			if (_targetPc.hasSkillEffect(DOMINATE_POWER_C) && (_targetPc.getValue() > _random.nextInt(100))) {
				final int time = _random.nextInt(3) + 2;
				_pc.setSkillEffect(L1SkillId.SHOCK_STUN, time * 1000);
				// 騎士技能(衝擊之暈)
				L1SpawnUtil.spawnEffect(81162, time, _pc.getX(), _pc.getY(), _pc.getMapId(), _targetPc, 0);

				_pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
			}
		}
		return (int) dmg;
	}

	private int c3_power() {
		if (_pc.hasSkillEffect(L1SkillId.C3_FIRE)) {// 火轉術
			return 1;
		}
		if (_pc.hasSkillEffect(L1SkillId.C3_WATER)) {// 水攻術
			return 2;
		}
		return 0;
	}

	private int c3_power_to_pc(final int type) {
		int damage = c3_power_dmg(type);
		int resist = 0;
		switch (type) {
		case 1:// 火轉術
			resist = _targetPc.getFire();
			// 傷害增減
			if (resist > 0) {// 抵抗
				damage = c3_power_dmg_down(damage, Math.min(100, resist));

			} else if (resist < 0) {// 懼怕
				damage = c3_power_dmg_up(damage, Math.min(0, resist));
			}
			break;

		case 2:// 水攻術
			resist = _targetPc.getWater();
			// 傷害增減
			if (resist > 0) {// 抵抗
				damage = c3_power_dmg_down(damage, Math.min(100, resist));

			} else if (resist < 0) {// 懼怕
				damage = c3_power_dmg_up(damage, Math.min(0, resist));
			}
			break;
		}
		return damage;
	}

	private int c3_power_to_npc(final int type) {
		final int damage = c3_power_dmg(type);
		switch (type) {
		case 1:// 火轉術
			if (_targetNpc instanceof L1MonsterInstance) {
				final L1MonsterInstance tgmob = (L1MonsterInstance) _targetNpc;
				if (!tgmob.isDead()) {
					tgmob.receiveDamage(_pc, damage, 2);// 火傷害
					// 受傷動作
					tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), ActionCodes.ACTION_Damage));
				}
			}
			break;

		case 2:// 水攻術
			if (_targetNpc instanceof L1MonsterInstance) {
				final L1MonsterInstance tgmob = (L1MonsterInstance) _targetNpc;
				if (!tgmob.isDead()) {
					tgmob.receiveDamage(_pc, damage, 4);// 水傷害
					// 受傷動作
					tgmob.broadcastPacketX8(new S_DoActionGFX(tgmob.getId(), ActionCodes.ACTION_Damage));
				}
			}
			break;
		}
		return 0;
	}

	/**
	 * 抵抗
	 * 
	 * @param resist
	 * @return
	 */
	private int c3_power_dmg_down(final int damage, final int resist) {
		final int r = 100 - resist;
		final int dmg = (damage * r) / 100;
		return Math.max(10, dmg);
	}

	/**
	 * 懼怕
	 * 
	 * @param resist
	 * @return
	 */
	private int c3_power_dmg_up(final int damage, final int resist) {
		final int dmg = damage - ((damage * resist) / 100);
		return Math.abs(dmg);
	}

	private int c3_power_dmg(final int type) {
		int damage = 0;
		final int level = _pc.getLevel();
		switch (type) {
		case 1:// 火轉術
			if ((level >= 50) && (level < 54)) {
				damage = random_dmg(40, 120);

			} else if ((level >= 55) && (level < 59)) {
				damage = random_dmg(60, 180);

			} else if ((level >= 60) && (level < 64)) {
				damage = random_dmg(80, 240);

			} else if ((level >= 65) && (level < 69)) {
				damage = random_dmg(100, 300);

			} else if ((level >= 70) && (level < 74)) {
				damage = random_dmg(120, 360);

			} else if ((level >= 75) && (level < 79)) {
				damage = random_dmg(140, 420);

			} else if ((level >= 80) && (level < 89)) {
				damage = random_dmg(160, 480);

			} else if ((level >= 90) && (level < 99)) {
				damage = random_dmg(180, 540);

			} else {
				damage = random_dmg(200, 600);
			}
			break;

		case 2:// 水攻術
			if ((level >= 50) && (level < 54)) {
				damage = random_dmg(40, 120);

			} else if ((level >= 55) && (level < 59)) {
				damage = random_dmg(60, 180);

			} else if ((level >= 60) && (level < 64)) {
				damage = random_dmg(80, 240);

			} else if ((level >= 65) && (level < 69)) {
				damage = random_dmg(100, 300);

			} else if ((level >= 70) && (level < 74)) {
				damage = random_dmg(120, 360);

			} else if ((level >= 75) && (level < 79)) {
				damage = random_dmg(140, 420);

			} else if ((level >= 80) && (level < 89)) {
				damage = random_dmg(160, 480);

			} else if ((level >= 90) && (level < 99)) {
				damage = random_dmg(180, 540);

			} else {
				damage = random_dmg(200, 600);
			}
			break;
		}
		return damage;
	}

	private int random_dmg(final int i, final int j) {
		return _random.nextInt(j - i) + i;
	}

	/**
	 * PC對NPC傷害
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
			_drainHp = 0;
			return 0;
		}

		if (!_isHit) {
			return 0;
		}

		// 需使用淨化藥水才能攻擊此NPC by terry0412
		if (_targetNpc.getNpcTemplate().is_attack_request()) {
			// 檢查PC是否有允許攻擊清單
			if (!_pc.check_allow_list(_targetNpc.getNpcId())) {
				return 0;
			}
		}

		final int c3_power_type = c3_power();
		if (c3_power() != 0) {
			return c3_power_to_npc(c3_power_type);
		}

		int weaponMaxDamage = 0;
		if (_targetNpc.getNpcTemplate().isSmall() && (_weaponSmall > 0)) {
			if (_weaponSmall > 0) {
				weaponMaxDamage = _weaponSmall;
			}

		} else if (_targetNpc.getNpcTemplate().isLarge() && (_weaponLarge > 0)) {
			if (_weaponLarge > 0) {
				weaponMaxDamage = _weaponLarge;
			}

		} else {
			if (_weaponSmall > 0) {
				weaponMaxDamage = _weaponSmall;
			}
		}
		// 傷害直初始化
		int weaponDamage = weaponDamage1(weaponMaxDamage);

		if (_pc.hasSkillEffect(MAZU_STATUS)) {// 媽祖祝福
			weaponDamage += 1;
		}

		double weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

		weaponTotalDamage += calcMaterialBlessDmg(); // 祝福武器 銀/米索莉/奧里哈魯根材質武器

		weaponTotalDamage += calcAttrEnchantDmg(); // 属性強化

		// 傷害直最終計算
		double dmg = weaponDamage2(weaponTotalDamage);

		// PC基礎傷害提昇計算
		dmg = pcDmgMode(dmg, weaponTotalDamage);

		dmg -= calcNpcDamageReduction();// NPC防禦力額外傷害減低

		// プレイヤーからペット、サモンに攻撃
		boolean isNowWar = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}

		if (!isNowWar) {
			if (_targetNpc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_targetNpc instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) _targetNpc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}

		// 聖結界
		if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}

		// 魔法娃娃特殊技能
		if (!_pc.getDolls().isEmpty()) {
			for (final Iterator<L1DollInstance> iter = _pc.getDolls().values().iterator(); iter.hasNext();) {
				final L1DollInstance doll = iter.next();
				doll.startDollSkill(_targetNpc, dmg);
			}
		}

		// 未命中傷害歸0
		if (!_isHit) {
			dmg = 0D;
		}

		if (dmg <= 0D) {
			_isHit = false;
			_drainHp = 0; // ダメージ無しの場合は吸収による回復はしない
		}

		// System.out.println("PC對NPC傷害 武器系統:" + dmg);
		return (int) dmg;
	}

	/**
	 * 龍騎士 弱點曝光
	 * 
	 * @return
	 */
	private int dk_dmgUp() {
		int dmg = 0;
		if (_pc.isDragonKnight() && (_weaponType2 == 18)) {// 鎖鍊劍
			if (_pc.get_weaknss() != 0) {
				final long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 換算為秒
				if (h_time - _pc.get_weaknss_t() > 16) {
					_pc.set_weaknss(0, 0);
				}
			}
			if (_pc.isFoeSlayer()) {
				// 依照階段調整傷害
				switch (_pc.get_weaknss()) {
				case 1:
					dmg += 20;
					break;
				case 2:
					dmg += 40;
					break;
				case 3:
					dmg += 60;
					break;
				}
			} else if (_random.nextInt(100) + 1 <= 20) {
				final long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 換算為秒
				switch (_pc.get_weaknss()) {
				case 1:
					_pc.set_weaknss(2, h_time);
					_pc.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV2));
					break;
				case 2:
					_pc.set_weaknss(3, h_time);
					_pc.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV3));
					break;
				case 3:
					break;
				default:
					_pc.set_weaknss(1, h_time);
					_pc.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV1));
					break;
				}
			}
		}
		return dmg;
	}

	/**
	 * 技能對武器追加傷害
	 * 
	 * @return
	 */
	private double dmgUp() {
		double dmg = 0.0;

		if (_pc.getSkillEffect().size() <= 0) {
			return dmg;
		}

		if (!_pc.getSkillisEmpty()) {
			try {
				HashMap<Integer, Integer> skills = null;
				switch (_weaponType) {
				case 20:// 弓
				case 62:// 鐵手甲
					skills = L1AttackList.SKD2;
					break;

				case 24:// 鎖鍊劍
				default:
					skills = L1AttackList.SKD1;
					break;
				}

				if (skills != null) {
					for (final Integer key : _pc.getSkillEffect()) {
						final Integer integer = L1AttackList.SKD2.get(key);
						if (integer != null) {
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

		dmg += _pc.dmgAdd();
		return dmg;
	}

	/**
	 * 武器附加魔法
	 * 
	 * @param pcInstance
	 * @param character
	 * @param weaponTotalDamage
	 * @return
	 */
	private double weaponSkill(final L1PcInstance pcInstance, final L1Character character, final double weaponTotalDamage) {
		double dmg = 0D;
		dmg = WeaponSkillStart.start_weapon_skill(pcInstance, character, _weapon, weaponTotalDamage);
		if (dmg != 0) {
			return dmg;
		}
		switch (_weaponId) {
		case 124:// 巴風特魔杖
			dmg = L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
			break;

		case 204:// 深紅之弩
		case 100204:// 深紅之弩
			L1WeaponSkill.giveFettersEffect(_pc, _target);
			break;

		case 261:// 大法師魔仗
			L1WeaponSkill.giveArkMageDiseaseEffect(_pc, _target);
			break;

		case 260:// 狂風之斧
		case 263:// 酷寒之矛
			dmg = L1WeaponSkill.getAreaSkillWeaponDamage(_pc, _target, _weaponId);
			break;

		case 264:// 雷雨之劍
			dmg = L1WeaponSkill.getLightningEdgeDamage(_pc, _target);
			break;

		default:
			dmg = L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
			break;
		}

		return dmg;
	}

	/**
	 * 武器強化魔法
	 * 
	 * @param dmg
	 * @return
	 */
	private double calcBuffDamage(double dmg) {
		if (_weaponType == 20) {// 弓
			return dmg;
		}
		if (_weaponType == 62) {// 鐵手甲
			return dmg;
		}
		if (_weaponType2 == 17) {// 奇古獸
			return dmg;
		}

		final int random = _random.nextInt(100) + 1;

		// 屬性之火 勇猛意志
		if (_pc.hasSkillEffect(ELEMENTAL_FIRE) || _pc.hasSkillEffect(BRAVE_MENTAL)) {
			if (random <= 40) {
				dmg *= 1.6;// 2011-10-13(0.5)
			}
		}

		// 燃燒鬥志
		if (_pc.hasSkillEffect(BURNING_SPIRIT)) {
			if (random <= 33) {
				dmg *= 1.40;// 2010-11-26(0.45)
			}
		}

		// 火焰武器
		if (_pc.hasSkillEffect(FIRE_WEAPON)) {
			dmg += 4;
		}

		// 烈炎武器
		if (_pc.hasSkillEffect(BURNING_WEAPON)) {
			dmg += 6;
		}

		// 狂暴術
		if (_pc.hasSkillEffect(BERSERKERS)) {
			dmg += 5;
		}
		return dmg;
	}

	/**
	 * 祝福武器 銀/米索莉/奧里哈魯根材質武器<BR>
	 * 其他屬性定義
	 * 
	 * @return
	 */
	private int calcMaterialBlessDmg() {
		int damage = 0;
		if (_pc.getWeapon() != null) {
			final int undead = _targetNpc.getNpcTemplate().get_undead();
			switch (undead) {
			case 1:// 不死系
				if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧里哈魯根
					damage += _random.nextInt(20) + 1;
				}
				if (_weaponBless == 0) { // 祝福武器
					damage += _random.nextInt(4) + 1;
				}
				switch (_weaponType) {
				case 20:
				case 62:
					break;
				default:
					if (_weapon.getHolyDmgByMagic() != 0) {
						damage += _weapon.getHolyDmgByMagic();// 武器強化魔法
					}
					break;
				}
				break;
			case 2:// 惡魔系
				if ((_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 米索莉/奧里哈魯根
					damage += _random.nextInt(3) + 1;
				}
				if (_weaponBless == 0) { // 祝福武器
					damage += _random.nextInt(4) + 1;
				}
				break;
			case 3:// 殭屍系
				if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧里哈魯根
					damage += _random.nextInt(20) + 1;
				}
				if (_weaponBless == 0) { // 祝福武器
					damage += _random.nextInt(4) + 1;
				}
				switch (_weaponType) {
				case 20:
				case 62:
					break;
				default:
					if (_weapon.getHolyDmgByMagic() != 0) {
						damage += _weapon.getHolyDmgByMagic();// 武器強化魔法
					}
					break;
				}
				break;
			case 5:// 狼人系
				if ((_weaponMaterial == 14) || (_weaponMaterial == 17) || (_weaponMaterial == 22)) {// 銀/米索莉/奧里哈魯根
					damage += _random.nextInt(20) + 1;
				}
				break;
			}
		}
		return damage;
	}

	/**
	 * 武器の属性強化による追加ダメージ算出
	 * 
	 * @return
	 */
	private int calcAttrEnchantDmg() {
		int damage = 0;
		switch (_weaponAttrEnchantLevel) {
		case 1:
			damage = 1;
			break;

		case 2:
			damage = 3;
			break;

		case 3:
			damage = 5;
			break;
		}

		// 對地火火風抗性的處理
		int resist = 0;
		switch (_calcType) {
		case PC_PC:
			switch (_weaponAttrEnchantKind) {
			case 1: // 地
				resist = _targetPc.getEarth();
				break;

			case 2: // 火
				resist = _targetPc.getFire();
				break;

			case 4: // 水
				resist = _targetPc.getWater();
				break;

			case 8: // 風
				resist = _targetPc.getWind();
				break;

			case 16: // 光
				resist = _targetPc.getEarth();
				break;

			case 32: // 暗
				resist = _targetPc.getFire();
				break;

			case 64: // 聖
				resist = _targetPc.getWater();
				break;

			case 128: // 邪
				resist = _targetPc.getWind();
				break;
			}
			break;

		case PC_NPC:
			switch (_weaponAttrEnchantKind) {
			case 1: // 地
				resist = _targetNpc.getEarth();
				break;

			case 2: // 火
				resist = _targetNpc.getFire();
				break;

			case 4: // 水
				resist = _targetNpc.getWater();
				break;

			case 8: // 風
				resist = _targetNpc.getWind();
				break;

			case 16: // 光
				resist = _targetNpc.getEarth();
				break;

			case 32: // 暗
				resist = _targetNpc.getFire();
				break;

			case 64: // 聖
				resist = _targetNpc.getWater();
				break;

			case 128: // 邪
				resist = _targetNpc.getWind();
				break;
			}
			break;
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));

		if (resist < 0) {
			resistFloor *= -1;
		}

		final double attrDeffence = resistFloor / 32.0;
		final double attrCoefficient = 1 - attrDeffence;

		damage *= attrCoefficient;

		return damage;
	}

	/**
	 * PC附加毒性攻擊
	 * 
	 * @param attacker
	 * @param target
	 */
	private void addPcPoisonAttack(final L1Character target) {
		if (_weaponId != 0) {// 非空手
			if (_pc.hasSkillEffect(ENCHANT_VENOM)) {
				final int chance = _random.nextInt(100) + 1;
				if (chance <= 10) {
					// 通常毒、3秒周期、ダメージHP-5
					L1DamagePoison.doInfection(_pc, target, 3000, 5);
				}
			}
		}
	}

	/**
	 * 攻擊資訊送出
	 */
	@Override
	public void action() {
		try {
			if (_pc == null) {
				return;
			}
			if (_target == null) {
				return;
			}
			// 改變面向
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY));

			if (_weaponRange == -1) {// 遠距離武器
				actionX1();

			} else {// 近距離武器
				actionX2();
			}
//			// 傷害顯示
//			if (_pc.is_attack_view()) { // 傷害顯示開關
//				int units = _damage % 10;
//				int tens = (_damage / 10) % 10;
//				int hundreads = (_damage / 100) % 10;
//				int thousands = (_damage / 1000) % 10;
//				int tenthousands = (_damage / 10000) % 10;
//				if ((units > 0) || (tens > 0) || (hundreads > 0) || (thousands > 0) || (tenthousands > 0)) {
//					units += 16000;
//					final S_SkillSound units_s = new S_SkillSound(_target.getId(), units);
//					_pc.sendPackets(units_s);
//				}
//				if ((tens > 0) || (hundreads > 0) || (thousands > 0) || (tenthousands > 0)) {
//					tens += 16010;
//					final S_SkillSound tens_s = new S_SkillSound(_target.getId(), tens);
//					_pc.sendPackets(tens_s);
//				}
//				if ((hundreads > 0) || (thousands > 0) || (tenthousands > 0)) {
//					hundreads += 16020;
//					final S_SkillSound hundreads_s = new S_SkillSound(_target.getId(), hundreads);
//					_pc.sendPackets(hundreads_s);
//				}
//				if ((thousands > 0) || (tenthousands > 0)) {
//					thousands += 16030;
//					final S_SkillSound thousands_s = new S_SkillSound(_target.getId(), thousands);
//					_pc.sendPackets(thousands_s);
//				}
//				if (tenthousands > 0) {
//					tenthousands += 16040;
//					final S_SkillSound tenthousands_s = new S_SkillSound(_target.getId(), tenthousands);
//					_pc.sendPackets(tenthousands_s);
//				}
//				if (_damage == 0) {
//					final S_SkillSound miss = new S_SkillSound(_target.getId(), 16050);
//					_pc.sendPackets(miss);
//				}
//			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 近距離武器/空手
	 */
	private void actionX2() {
		try {
			if (_isHit) {// 命中
				// System.out.println("命中");
				_pc.sendPacketsX10(new S_AttackPacketPc(_pc, _target, _attackType, _damage));

			} else {// 未命中
				if (_targetId > 0) {
					_pc.sendPacketsX10(new S_AttackPacketPc(_pc, _target));

				} else {
					_pc.sendPacketsAll(new S_AttackPacketPc(_pc));
				}
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
			switch (_weaponType) {
			case 20:// 弓
				switch (_weaponId) {
				case 190: // 沙哈之弓 不具有箭
					if (_arrow != null) { // 具有箭
						_pc.getInventory().removeItem(_arrow, 1);
					}
					_pc.sendPacketsX10(new S_UseArrowSkill(_pc, _targetId, 2349, _targetX, _targetY, _damage));
					break;

				default:// 其他武器 沒有箭
					if (_arrow != null) { // 具有箭
						int arrowGfxid = 66;
						switch (_pc.getTempCharGfx()) {
						case 8842:
						case 8900:// 海露拜
							arrowGfxid = 8904;// 黑
							break;

						case 8913:
						case 8845:// 朱里安
							arrowGfxid = 8916;// 白
							break;

						// 151228 測試外觀變更箭矢
						case 12314:// 新絲莉安
						case 13631:// 新黑暗精靈
						case 13635:// 新黑暗精靈
							arrowGfxid = 13658;// 綠X1
							break;

						case 364:// 赤燄死騎 特化遠戰變身
						case 367:// 真。赤燄死騎 特化遠戰變身
							arrowGfxid = 13656;// 紫X1
							break;

						case 395:// 真。絲莉安 氣焰變身(綠)
						case 396:// 真。絲莉安 氣焰變身(紫)
							arrowGfxid = 13657;// 紫X3
							break;

						case 7959:
						case 7967:
						case 7968:
						case 7969:
						case 7970:// 天上騎士
							arrowGfxid = 7972;// 火
							break;
						}
						_pc.sendPacketsX10(new S_UseArrowSkill(_pc, _targetId, arrowGfxid, _targetX, _targetY, _damage));
						_pc.getInventory().removeItem(_arrow, 1);

					} else {
						int aid = 1;
						// 外型編號改變動作
						if (_pc.getTempCharGfx() == 3860) {
							aid = 21;
						}
						_pc.sendPacketsAll(new S_ChangeHeading(_pc));
						// 送出封包(動作)
						_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), aid));
					}
				}
				break;

			case 62: // 鐵手甲
				if (_sting != null) {// 具有飛刀
					int stingGfxid = 2989;
					switch (_pc.getTempCharGfx()) {
					case 8842:
					case 8900:// 海露拜
						stingGfxid = 8904;// 黑
						break;

					case 8913:
					case 8845:// 朱里安
						stingGfxid = 8916;// 白
						break;

					case 7959:
					case 7967:
					case 7968:
					case 7969:
					case 7970:// 天上騎士
						stingGfxid = 7972;// 火
						break;
					}
					_pc.sendPacketsX10(new S_UseArrowSkill(_pc, _targetId, stingGfxid, _targetX, _targetY, _damage));
					_pc.getInventory().removeItem(_sting, 1);

				} else {// 沒有飛刀
					int aid = 1;
					// 外型編號改變動作
					if (_pc.getTempCharGfx() == 3860) {
						aid = 21;
					}

					_pc.sendPacketsAll(new S_ChangeHeading(_pc));
					// 送出封包(動作)
					_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), aid));
				}
				break;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 計算結果
	 */
	@Override
	public void commit() {
		if (_isHit) {
			if (_pc.dice_hp() != 0) {
				if ((_random.nextInt(1000) + 1) <= _pc.dice_hp()) {
					_drainHp = _pc.sucking_hp();
				}
			}
			if (_pc.dice_mp() != 0) {
				if ((_random.nextInt(1000) + 1) <= _pc.dice_mp()) {
					_drainMana = _pc.sucking_mp();
				}
			}
			switch (_calcType) {
			case PC_PC:
				if (_pc.lift() != 0) {
					if ((_random.nextInt(1000) + 1) <= _pc.lift()) {
						final L1ItemInstance armor = _targetPc.getInventory().getItemEquipped(2, 1);// 頭盔
						if (armor != null) {
							_targetPc.getInventory().setEquipped(armor, false);
							// 1,356：盔甲的連接部分被破壞了。
							_targetPc.sendPackets(new S_ServerMessage(1356));
						}
					}
				}
				commitPc();
				break;

			case PC_NPC:
				commitNpc();
				break;
			}
		}

		// gm攻擊資訊
		if (!ConfigAlt.ALT_ATKMSG) {
			return;

		} else {
			switch (_calcType) {
			case PC_PC:
				if (!_pc.isGm()) {
					if (!_targetPc.isGm()) {
						return;
					}
				}
				break;

			case PC_NPC:
				if (!_pc.isGm()) {
					return;
				}
				break;
			}
		}

		final String srcatk = _pc.getName();// 攻擊者
		String tgatk = "";// 被攻擊者
		String hitinfo = "";// 資訊
		String dmginfo = "";// 傷害
		String x = "";// 最終資訊

		switch (_calcType) {
		case PC_PC:
			tgatk = _targetPc.getName();
			hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetPc.getCurrentHp();
			dmginfo = _isHit ? "傷害:" + _damage : "失敗";
			x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
			if (_pc.isGm()) {
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, "對PC送出攻擊: " + x));
			}

			if (_targetPc.isGm()) {
				// 166 \f1%0%s %4%1%3 %2。
				_targetPc.sendPackets(new S_ServerMessage(166, "受到PC攻擊: " + x));
			}
			break;

		case PC_NPC:
			tgatk = _targetNpc.getName();
			hitinfo = " 命中:" + _hitRate + "% 剩餘hp:" + _targetNpc.getCurrentHp();
			dmginfo = _isHit ? "傷害:" + _damage : "失敗";
			x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
			if (_pc.isGm()) {
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, "對NPC送出攻擊: " + x));
			}
			break;
		}
	}

	/**
	 * 對PC攻擊傷害結果
	 */
	private void commitPc() {
		if ((_drainMana > 0) && (_targetPc.getCurrentMp() > 0)) {
			if (_drainMana > _targetPc.getCurrentMp()) {
				_drainMana = _targetPc.getCurrentMp();
			}
			short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
			_targetPc.setCurrentMp(newMp);
			newMp = (short) (_pc.getCurrentMp() + _drainMana);
			_pc.setCurrentMp(newMp);
		}

		if (_drainHp > 0) { // HP吸收回復
			int newHp = _pc.getCurrentHp() + _drainHp;
			if (newHp >= _pc.getMaxHp()) {
				newHp = _pc.getMaxHp();
			}
			_pc.setCurrentHp(newHp);
		}

		// damagePcWeaponDurability(); // 武器受到傷害
		_targetPc.receiveDamage(_pc, _damage, false, false);
	}

	/**
	 * 對NPC攻擊傷害結果
	 */
	private void commitNpc() {
		if (_drainMana > 0) {
			final int drainValue = _targetNpc.drainMana(_drainMana);
			final int newMp = _pc.getCurrentMp() + drainValue;
			_pc.setCurrentMp(newMp);
			if (drainValue > 0) {
				final int newMp2 = _targetNpc.getCurrentMp() - drainValue;
				_targetNpc.setCurrentMpDirect(newMp2);
			}
		}

		if (_drainHp > 0) { // HP吸收回復
			int newHp = _pc.getCurrentHp() + _drainHp;
			if (newHp >= _pc.getMaxHp()) {
				newHp = _pc.getMaxHp();
			}
			_pc.setCurrentHp(newHp);
		}

		damageNpcWeaponDurability(); // 武器受到傷害
		_targetNpc.receiveDamage(_pc, _damage);
	}

	/**
	 * 相手の攻撃に対してカウンターバリアが有効かを判別
	 */
	@Override
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if ((_weaponType == 20) || (_weaponType == 62)) { // 弓かガントレット
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

		if (_targetPc != null) {
			_targetPc.sendPacketsAll(new S_SkillSound(_targetPc.getId(), 10710));
		}

		// 受傷動作
		_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
		_pc.receiveDamage(_target, damage, false, true);
	}

	/**
	 * 武器受到傷害 対NPCの場合、損傷確率は10%とする。祝福武器は3%とする。
	 */
	private void damageNpcWeaponDurability() {
		/*
		 * 損傷しないNPC、素手、損傷しない武器使用、SOF中の場合何もしない。
		 */
		if (_calcType != PC_NPC) {
			return;
		}

		if (_targetNpc.getNpcTemplate().is_hard() == false) {
			return;
		}

		if (_weaponType == 0) {
			return;
		}

		if (_weapon.getItem().get_canbedmg() == 0) {
			return;
		}

		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}

		final int random = _random.nextInt(100) + 1;
		switch (_weaponBless) {
		case 0:// 祝福
			if (random < 3) {
				// \f1你的%0%s壞了。
				_pc.sendPackets(new S_ServerMessage(268, _weapon.getLogName()));
				_pc.sendPackets(new S_SkillSound(_pc.getId(), 10712)); // 因應
				// 3.80C
				// 新增受損特效
				_pc.getInventory().receiveDamage(_weapon);
			}
			break;

		case 1:// 一般
		case 2:// 詛咒
			if (random < 10) {
				// \f1你的%0%s壞了。
				_pc.sendPackets(new S_ServerMessage(268, _weapon.getLogName()));
				_pc.sendPackets(new S_SkillSound(_pc.getId(), 10712)); // 因應
				// 3.80C
				// 新增受損特效
				_pc.getInventory().receiveDamage(_weapon);
			}
			break;
		}
	}

	/**
	 * バウンスアタックにより武器受到傷害 バウンスアタックの損傷確率は10%
	 */
	/*
	 * private void damagePcWeaponDurability() { if (this._calcType != PC_PC) { return; } if (this._weaponType == 0) { return; } if (this._weaponType == 20) { return; } if
	 * (this._weaponType == 62) { return; } if (this._targetPc.hasSkillEffect(BOUNCE_ATTACK) == false) { return; } if (this._pc.hasSkillEffect(SOUL_OF_FLAME)) { return; } if
	 * (_random.nextInt(100) + 1 <= 10) { // \f1你的%0%s壞了。 this._pc.sendPackets(new S_ServerMessage(268, this._weapon.getLogName())); this._pc.getInventory().receiveDamage(this._weapon); }
	 * }
	 */
}