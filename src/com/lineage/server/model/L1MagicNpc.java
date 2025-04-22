package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.BONE_BREAK;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DARK_BLIND;
import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_B;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.FINAL_BURN;
import static com.lineage.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.GUARD_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.HORROR_OF_DEATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static com.lineage.server.model.skill.L1SkillId.MAGIC_ITEM_POWER_A;
import static com.lineage.server.model.skill.L1SkillId.PHANTASM;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.WEAPON_BREAK;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.ConcurrentModificationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;

/**
 * 魔法攻擊判定(NPC)
 * 
 * @author daien
 */
public class L1MagicNpc extends L1MagicMode {

	private static final Log _log = LogFactory.getLog(L1MagicNpc.class);

	/**
	 * 魔法攻擊判定(NPC)
	 * 
	 * @param attacker
	 * @param target
	 */
	public L1MagicNpc(final L1NpcInstance attacker, final L1Character target) {
		if (attacker == null) {
			return;
		}

		if (target instanceof L1PcInstance) {
			_calcType = NPC_PC;
			_npc = attacker;
			_targetPc = (L1PcInstance) target;

		} else {
			_calcType = NPC_NPC;
			_npc = attacker;
			_targetNpc = (L1NpcInstance) target;
		}
	}

	/**
	 * 魔法等級
	 * 
	 * @return
	 */
	private int getMagicLevel() {
		final int magicLevel = _npc.getMagicLevel();
		return magicLevel;
	}

	/**
	 * 智力命中魔法追加
	 * 
	 * @return
	 */
	private int getMagicBonus() {
		final int magicBonus = _npc.getMagicBonus();
		return magicBonus;
	}

	/**
	 * 傳回正義質
	 * 
	 * @return
	 */
	private int getLawful() {
		final int lawful = _npc.getLawful();
		return lawful;
	}

	/**
	 * ■■■■■■■■■■■■■■ 成功判定 ■■■■■■■■■■■■■ ●●●● 確率系魔法の成功判定 ●●●● 計算方法 攻撃側ポイント：LV +
	 * ((MagicBonus * 3) * 魔法固有係数) 防御側ポイント：((LV / 2) + (MR * 3)) / 2
	 * 攻撃成功率：攻撃側ポイント - 防御側ポイント
	 */
	@Override
	public boolean calcProbabilityMagic(final int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		if (skillId == CANCELLATION) {
			// 対象がNPC、使用者がNPCの場合は100%成功
			return true;
		}
		switch (_calcType) {
		case NPC_PC:
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}

			// 聞見色霸氣 (機率受到負面魔法無效化) by terry0412
			if (_targetPc.hasSkillEffect(DOMINATE_POWER_B) && (_targetPc.getValue() > _random.nextInt(100))) {
				_targetPc.sendPackets(new S_SystemMessage("[聞見色霸氣]幫你阻擋負面魔法成功。"));
				return false;
			}

			break;

		case NPC_NPC:
			if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}
			break;
		}

		probability = calcProbability(skillId);

		final int rnd = _random.nextInt(100) + 1;

		// 最大成功率90%
		probability = Math.min(probability, 90);
		if (probability >= rnd) {
			isSuccess = true;

		} else {
			isSuccess = false;
		}

		if (calcEvasion()) {
			return false;
		}
		return isSuccess;
	}

	/**
	 * 魔法命中的判斷
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcProbability(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int attackLevel = _npc.getLevel();
		;
		int defenseLevel = 0;
		int probability = 0;

		switch (_calcType) {
		case NPC_PC:
			defenseLevel = _targetPc.getLevel();
			break;

		case NPC_NPC:
			defenseLevel = _targetNpc.getLevel();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					final L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
			break;
		}

		switch (skillId) {
		case ENTANGLE:// 地面障礙
		case WIND_SHACKLE:// 風之枷鎖
		case ELEMENTAL_FALL_DOWN:// 弱化屬性
		case RETURN_TO_NATURE:// 釋放元素
		case ERASE_MAGIC:// 魔法消除
		case AREA_OF_SILENCE:// 封印禁地
		case STRIKER_GALE:// 精準射擊
		case POLLUTE_WATER:// 污濁之水
			// 成功確率は 魔法固有係数 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			break;

		case EARTH_BIND:// 大地屏障
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 3 + (_pc.getInt() / 3);// SRC 20

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 20 + (_pc.getInt() / 3);// SRC 80

			} else {// 攻擊者等級大於被攻擊者
				probability = 40 + (_pc.getInt() / 3);// SRC 80
			}
			break;

		case SHOCK_STUN:// 衝擊之暈
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 22;// SRC 20

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 40;// SRC NO

			} else {// 攻擊者等級大於等於被攻擊者
				probability = 60;// SRC 80
			}
			break;

		// case SHOCK_STUN:
		case COUNTER_BARRIER:// 反擊屏障
			// 成功確率は 基本確率 + LV差1毎に+-1%
			probability = (l1skills.getProbabilityValue() + attackLevel) - defenseLevel;
			break;

		case GUARD_BRAKE:// 護衛毀滅
		// case RESIST_FEAR:// 恐懼無助
		// case HORROR_OF_DEATH:// 驚悚死神
		{
			final int dice1 = l1skills.getProbabilityDice();
			final int value = l1skills.getProbabilityValue();

			final int diceCount1 = Math.max(getMagicBonus() + getMagicLevel(), 1);
			/*
			 * int diceCount1 = 0; diceCount1 = this.getMagicBonus() +
			 * this.getMagicLevel(); if (diceCount1 < 1) { diceCount1 = 1; }
			 */

			for (int i = 0; i < diceCount1; i++) {
				probability += (_random.nextInt(dice1) + 1 + value);
			}

			probability = (int) (probability * (getLeverage() / 10D));

			if (probability >= getTargetMr()) {
				probability = 100;
			} else {
				probability = 0;
			}
			break;
		}

		case HORROR_OF_DEATH:// 驚悚死神 (無視目標的魔防)
		{
			final int dice1 = l1skills.getProbabilityDice();
			final int value = l1skills.getProbabilityValue();

			final int diceCount1 = Math.max(getMagicBonus() + getMagicLevel(), 1);
			/*
			 * int diceCount1 = 0; diceCount1 = this.getMagicBonus() +
			 * this.getMagicLevel(); if (diceCount1 < 1) { diceCount1 = 1; }
			 */

			for (int i = 0; i < diceCount1; i++) {
				probability += (_random.nextInt(dice1) + 1 + value);
			}

			probability = (int) (probability * (getLeverage() / 10D));

			break;
		}

		/*
		 * case SILENCE:// 魔法封印 case WEAPON_BREAK:// 壞物術 case SLOW:// 緩速術 final
		 * int dice3 = l1skills.getProbabilityDice(); int diceCount3 = 0; if
		 * (this._pc.isWizard()) { diceCount3 = this.getMagicBonus() +
		 * this.getMagicLevel() + 1; } else if (this._pc.isElf()) { diceCount3 =
		 * this.getMagicBonus() + this.getMagicLevel() - 1; } else { diceCount3
		 * = this.getMagicBonus() + this.getMagicLevel() - 1; } if (diceCount3 <
		 * 1) { diceCount3 = 1; } for (int i = 0; i < diceCount3; i++) {
		 * probability += (_random.nextInt(dice3) + 1); } probability =
		 * probability * this.getLeverage() / 10; // 智力(依職業)附加魔法命中 probability
		 * += 2 * this._pc.getOriginalMagicHit(); // 扣除抗魔減免 probability -=
		 * this.getTargetMr(); // 等級差(被攻擊者 - 攻擊者) / 16 int levelR = defenseLevel
		 * / 24; if (levelR <= 0) { levelR = 1; } probability /= levelR;
		 * //System.out.println("probability:" + probability); break;
		 */

		default:
			final int dice2 = l1skills.getProbabilityDice();

			final int diceCount2 = Math.max(getMagicBonus() + getMagicLevel(), 1);
			/*
			 * int diceCount2 = this.getMagicBonus() + this.getMagicLevel(); if
			 * (diceCount2 < 1) { diceCount2 = 1; }
			 */

			for (int i = 0; i < diceCount2; i++) {
				probability += (_random.nextInt(dice2) + 1);
			}

			probability = (int) (probability * (getLeverage() / 10D));

			probability -= getTargetMr();
			break;
		}

		// 抵抗有害魔法成功率+% by terry0412
		if (_calcType == NPC_PC && _targetPc.getMagicHitUp() != 0) {
			probability -= (double) (probability * _targetPc.getMagicHitUp() / 100);
		}

		// 耐性
		if (_calcType == NPC_PC) {
			switch (skillId) {
			case EARTH_BIND:// 大地屏障 - 支撐耐性
				probability -= (_targetPc.getRegistSustain() >> 1);
				break;

			case SHOCK_STUN:// 衝擊之暈 - 昏迷耐性
			case BONE_BREAK:// 骷髏毀壞
				// (>> 1: 除) (<< 1: 乘)
				probability -= (_targetPc.getRegistStun() >> 1);
				// probability -= 2 * this._targetPc.getRegistStun();
				break;

			case CURSE_PARALYZE:// 木乃伊的詛咒 - 石化耐性
			case PHANTASM:// 幻想
				probability -= (_targetPc.getRegistStone() >> 1);
				break;

			case FOG_OF_SLEEPING:// 沉睡之霧 - 睡眠耐性
				probability -= (_targetPc.getRegistSleep() >> 1);
				break;

			case ICE_LANCE:// 冰矛圍籬 - 寒冰耐性
			case FREEZING_BREATH:// 寒冰噴吐
				probability -= (_targetPc.getRegistFreeze() >> 1);
				break;

			case CURSE_BLIND:// 闇盲咒術 - 暗黑耐性
			case DARKNESS:// 黑闇之影
			case DARK_BLIND:// 暗黑盲咒
				probability -= (_targetPc.getRegistBlind() >> 1);
				break;
			}

			// 被攻擊者轉生次數免除命中
			/*
			 * int levelup = this._targetPc.get_other().get_levelup(); if
			 * (levelup > 0) { probability -= (levelup << 1); }//
			 */
		}

		return probability;
	}

	/**
	 * 魔法傷害值計算
	 * 
	 * @param skillId
	 * @return
	 */
	@Override
	public int calcMagicDamage(final int skillId) {
		int damage = 0;
		switch (_calcType) {
		case NPC_PC:
			damage = calcPcMagicDamage(skillId);
			break;

		case NPC_NPC:
			damage = calcNpcMagicDamage(skillId);
			break;
		}

		damage = calcMrDefense(damage);
		return damage;
	}

	/**
	 * NPC對PC傷害計算
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcPcMagicDamage(final int skillId) {
		if (_targetPc == null) {
			return 0;
		}
		if (((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance))
				&& (_targetPc.getZoneType() == 1)) {
			return 0;
		}

		// 傷害為0
		if (dmg0(_targetPc)) {
			return 0;
		}

		// 160620補正 - 受到魔法傷害無效化 by erics4179
		if (_targetPc.hasSkillEffect(MAGIC_ITEM_POWER_A)) {
			return 0;
		}

		int dmg = 0;
		if (skillId == FINAL_BURN) {
			dmg = _npc.getCurrentMp();

		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (int) (dmg * (getLeverage() / 10D));
		}

		// 魔法傷害減免+% by terry0412
		if (_targetPc.getMagicDmgDown() != 0) {
			dmg -= (double) (dmg * _targetPc.getMagicDmgDown() / 100);
		}

		dmg -= _targetPc.getDamageReductionByArmor() + _targetPc.getMagicDmgReduction();

		dmg -= _targetPc.dmgDowe(); // 機率傷害減免

		if (_targetPc.getClanid() != 0) {
			dmg -= getDamageReductionByClan(_targetPc);// 血盟技能魔法傷害減免
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
					// 傷害減免
					if (integer != null) {
						if (integer.equals(key)) {
							// 技能編號與返回值相等
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

		boolean isNowWar = false;
		final int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
		if (castleId > 0) {
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_npc instanceof L1PetInstance) {
				// (>> 1: 除) (<< 1: 乘)
				dmg = (dmg >> 3);// dmg /= 8;
			}
			if (_npc instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) _npc;
				if (summon.isExsistMaster()) {
					dmg = (dmg >> 3);// dmg /= 8;
				}
			}
		}
		if (dmgX2) {
			dmg = dmg >> 1;// dmg /= 2;
		}

		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			final int npcId = _npc.getNpcTemplate().get_npcId();
			switch (npcId) {
			case 45681:// 林德拜爾
			case 45682:// 安塔瑞斯
			case 45683:// 法利昂
			case 45684:// 巴拉卡斯
			case 91161:// 紀元水龍的影像
			case 91159:// 紀元地龍的影像
			case 91160:// 紀元火龍的影像
			case 91162:// 紀元風龍的影像
			case 71014:// 新安塔瑞斯(1階段)
			case 71015:// 新安塔瑞斯(2階段)
			case 71016:// 新安塔瑞斯(3階段)
			case 71026:// 新法利昂(1階段)
			case 71027:// 新法利昂(2階段)
			case 71028:// 新法利昂(3階段)
				// 不接受鏡反射攻擊
				break;

			default:
				if (!_npc.getNpcTemplate().get_IsErase()) {

				} else {
					if (_targetPc.getWis() >= _random.nextInt(100)) {
						_npc.broadcastPacketX10(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
						_targetPc.sendPacketsX8(new S_SkillSound(_targetPc.getId(), 4395));
						_npc.receiveDamage(_targetPc, dmg);
						dmg = 0;
						_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
					}
				}
				break;
			}
		}
		final int dmgOut = Math.max(dmg, 0);
		// System.out.println("NPC對PC傷害計算 010:"+dmgOut);

		return dmgOut;
	}

	/**
	 * プレイヤー・ＮＰＣ から ＮＰＣ へのダメージ算出
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcNpcMagicDamage(final int skillId) {
		if (_targetNpc == null) {
			return 0;
		}
		// 傷害為0
		if (dmg0(_targetNpc)) {
			return 0;
		}

		int dmg = 0;
		if (skillId == FINAL_BURN) {
			dmg = _npc.getCurrentMp();

		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (int) (dmg * (getLeverage() / 10D));
		}
		// 聖結界
		if (_targetNpc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}

		return dmg;
	}

	/**
	 * damage_dice、damage_dice_count、damage_value、SPから魔法ダメージを算出
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcMagicDiceDamage(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int dice = l1skills.getDamageDice();
		final int diceCount = l1skills.getDamageDiceCount();
		final int value = l1skills.getDamageValue();
		int magicDamage = 0;
		int charaIntelligence = 0;

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}
		magicDamage += value;

		final int spByItem = getTargetSp();// this._npc.getSp() -
											// this._npc.getTrueSp(); //
											// アイテムによるSP変動
		charaIntelligence = Math.max((_npc.getInt() + spByItem) - 12, 1);
		/*
		 * charaIntelligence = this._npc.getInt() + spByItem - 12; if
		 * (charaIntelligence < 1) { charaIntelligence = 1; }
		 */

		final double attrDeffence = calcAttrResistance(l1skills.getAttr());

		final double coefficient = Math.max(((1.0 - attrDeffence) + ((charaIntelligence * 3.0) / 32.0)), 0.0);
		/*
		 * double coefficient = (1.0 - attrDeffence + charaIntelligence * 3.0 /
		 * 32.0); if (coefficient < 0) { coefficient = 0; }
		 */

		magicDamage *= coefficient;

		return magicDamage;
	}

	/**
	 * ヒール回復量（対アンデッドにはダメージ）を算出
	 * 
	 * @param skillId
	 * @return
	 */
	@Override
	public int calcHealing(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int dice = l1skills.getDamageDice();
		final int value = l1skills.getDamageValue();
		int magicDamage = 0;

		final int magicBonus = Math.min(getMagicBonus(), 10);

		/*
		 * int magicBonus = this.getMagicBonus(); if (magicBonus > 10) {
		 * magicBonus = 10; }
		 */

		final int diceCount = value + magicBonus;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (_random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (int) (magicDamage * (getLeverage() / 10D));

		return magicDamage;
	}

	/**
	 * ＭＲ魔法傷害減輕
	 * 
	 * @param dmg
	 * @return
	 */
	private int calcMrDefense(int dmg) {
		// 取回目標抗魔
		final int mr = getTargetMr();

		double mrFloor = 0;
		double mrCoefficient = 0;

		if (mr == 0) {
			mrFloor = 1;
			mrCoefficient = 1;

		} else if ((mr > 0) && (mr <= 50)) {
			mrFloor = 2;
			mrCoefficient = 1;

		} else if ((mr > 50) && (mr <= 100)) {
			mrFloor = 3;
			mrCoefficient = 0.9;

		} else if ((mr > 100) && (mr <= 120)) {
			mrFloor = 4;
			mrCoefficient = 0.9;

		} else if ((mr > 120) && (mr <= 140)) {
			mrFloor = 5;
			mrCoefficient = 0.8;

		} else if ((mr > 140) && (mr <= 160)) {
			mrFloor = 6;
			mrCoefficient = 0.8;

		} else if ((mr > 160) && (mr <= 180)) {
			mrFloor = 7;
			mrCoefficient = 0.7;

		} else if ((mr > 180) && (mr <= 200)) {
			mrFloor = 8;
			mrCoefficient = 0.7;

		} else if ((mr > 200) && (mr <= 220)) {
			mrFloor = 9;
			mrCoefficient = 0.6;

		} else if ((mr > 220) && (mr <= 240)) {
			mrFloor = 10;
			mrCoefficient = 0.6;

		} else if (mr > 240) {
			mrFloor = 11;
			mrCoefficient = 0.5;
		}

		// 取回NPC智力
		final int originalInt = _npc.getInt();
		int originalMagicHit = 1;

		if (originalInt < 18) {
			originalMagicHit = 1;

		} else if ((originalInt >= 18) && (originalInt < 36)) {
			originalMagicHit = 2;

		} else if ((originalInt >= 36) && (originalInt < 72)) {
			originalMagicHit = 3;

		} else if (originalInt >= 72) {
			originalMagicHit = 4;
		}

		// 計算減低
		dmg *= (mrCoefficient - (0.01 * Math.floor((mr - originalMagicHit) / mrFloor)));

		/*
		 * final int mr = this.getTargetMr(); final int rnd =
		 * _random.nextInt(100) + 1; if (mr >= rnd) { // (>> 1: 除) (<< 1: 乘) dmg
		 * = dmg >> 1;//dmg /= 2; }
		 */

		return dmg;
	}

	/**
	 * 計算結果反映
	 * 
	 * @param damage
	 * @param drainMana
	 */
	@Override
	public void commit(int damage, final int drainMana) {

		// BOSS XXX
		if (_npc.getNpcTemplate().is_boss()) {
			damage *= 1.25;
		}
		switch (_calcType) {
		case NPC_PC:
			commitPc(damage, drainMana);
			break;

		case NPC_NPC:
			commitNpc(damage, drainMana);
			break;
		}
		// GM攻擊訊息
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

		final StringBuilder atkMsg = new StringBuilder();
		atkMsg.append("受到NPC技能: ");
		atkMsg.append(_npc.getNameId() + ">");// 攻擊者
		atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
		atkMsg.append("傷害: " + damage);// 資訊
		// 166 \f1%0%s %4%1%3 %2。
		_targetPc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));

		// final String srcatk = this._targetPc.getName();
		// final String hitinfo = "目標HP:" + this._targetPc.getCurrentHp();
		// final String dmginfo = "傷害: " + damage;
		// final String tgatk = this._targetPc.getName();
		// final String x = srcatk + ">" + tgatk + " " + dmginfo + hitinfo;
		// this._targetPc.sendPackets(new S_ServerMessage(166, "受到NPC技能: " +
		// x));
	}

	/**
	 * 對pc傷害的輸出
	 * 
	 * @param damage
	 * @param drainMana
	 */
	private void commitPc(final int damage, final int drainMana) {
		try {
			_targetPc.receiveDamage(_npc, damage, true, false);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 對npc傷害的輸出
	 * 
	 * @param damage
	 * @param drainMana
	 */
	private void commitNpc(final int damage, final int drainMana) {
		try {
			_targetNpc.receiveDamage(_npc, damage);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}