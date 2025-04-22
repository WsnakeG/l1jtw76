package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.ARMOR_BREAK;
import static com.lineage.server.model.skill.L1SkillId.BONE_BREAK;
import static com.lineage.server.model.skill.L1SkillId.CANCELLATION;
import static com.lineage.server.model.skill.L1SkillId.CONFUSION;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static com.lineage.server.model.skill.L1SkillId.CURSE_BLIND;
import static com.lineage.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static com.lineage.server.model.skill.L1SkillId.DARKNESS;
import static com.lineage.server.model.skill.L1SkillId.DARK_BLIND;
import static com.lineage.server.model.skill.L1SkillId.DESPERADO;
import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_B;
import static com.lineage.server.model.skill.L1SkillId.DOMINATE_POWER_D;
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
import static com.lineage.server.model.skill.L1SkillId.PANIC;
import static com.lineage.server.model.skill.L1SkillId.PHANTASM;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;
import static com.lineage.server.model.skill.L1SkillId.POWERGRIP;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.RESIST_FEAR;
import static com.lineage.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.SILENCE;
import static com.lineage.server.model.skill.L1SkillId.SLOW;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.TAMING_MONSTER;
import static com.lineage.server.model.skill.L1SkillId.WEAPON_BREAK;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.ConcurrentModificationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigAlt;
import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.model.Instance.L1DeInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.serverpackets.S_Attack;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.timecontroller.server.ServerWarExecutor;

/**
 * 魔法攻擊判定(PC)
 * 
 * @author daien
 */
public class L1MagicPc extends L1MagicMode {

	private static final Log _log = LogFactory.getLog(L1MagicPc.class);

	/**
	 * 魔法攻擊判定(PC)
	 * 
	 * @param attacker
	 * @param target
	 */
	public L1MagicPc(final L1PcInstance attacker, final L1Character target) {
		if (attacker == null) {
			return;
		}

		_pc = attacker;

		if (target instanceof L1PcInstance) {
			_calcType = PC_PC;
			_targetPc = (L1PcInstance) target;

		} else {
			_calcType = PC_NPC;
			_targetNpc = (L1NpcInstance) target;
		}

	}

	/**
	 * 職業魔法等級
	 * 
	 * @return
	 */
	private int getMagicLevel() {
		return _pc.getMagicLevel();
	}

	/**
	 * 智力命中魔法追加
	 * 
	 * @return
	 */
	private int getMagicBonus() {
		return _pc.getMagicBonus();
	}

	/**
	 * 傳回正義質
	 * 
	 * @return
	 */
	private int getLawful() {
		return _pc.getLawful();
	}

	/**
	 * 攻擊成功的判斷 ●●●● 確率系魔法の成功判定 ●●●● 計算方法 攻撃側ポイント：LV + ((MagicBonus * 3) *
	 * 魔法固有係数) 防御側ポイント：((LV / 2) + (MR * 3)) / 2 攻撃成功率：攻撃側ポイント - 防御側ポイント
	 */
	@Override
	public boolean calcProbabilityMagic(final int skillId) {
		int probability = 0;// 魔法成功機率
		boolean isSuccess = false;

		switch (_calcType) {
		case PC_PC:
			// 魔法相消術
			if (skillId == CANCELLATION) {
				if ((_pc != null) && (_targetPc != null)) {
					// 對象為自己100%成功
					if (_pc.getId() == _targetPc.getId()) {
						return true;
					}

					// 相同血盟100%成功
					if (_pc.getClanid() > 0) {
						if (_pc.getClanid() == _targetPc.getClanid()) {
							if(_pc.get_redbluejoin()==0){//判斷陣營戰同盟之間
							return true;
							}
						}
					}

					// 相同隊伍100%成功
					if (_pc.isInParty()) {
						if (_pc.getParty().isMember(_targetPc)) {
							if(_pc.get_redbluejoin()==0){//判斷陣營戰同隊之間
							return true;
							}
						}
					}
				}
			}

			// 攻擊者 或是 被攻擊者 在安全區內
			if (!checkZone(skillId)) {
				return false;
			}

			// 被攻擊者受到大地屏障
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				// 施展法術不是壞物術或魔法相消術
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}

			// 聞見色霸氣 (機率受到負面魔法無效化) by terry0412
			if (_targetPc.hasSkillEffect(DOMINATE_POWER_B)
					&& (_targetPc.getValue() > _random.nextInt(100))) {
				_targetPc
						.sendPackets(new S_SystemMessage("[聞見色霸氣]幫你阻擋負面魔法成功。"));
				return false;
			}

			// 迴避
			if (calcEvasion()) {
				return false;
			}
			break;

		case PC_NPC:
			if (_targetNpc != null) {
				// 虛擬人物 判定失敗
				if (_targetNpc instanceof L1DeInstance) {
					// 攻擊者 或是 被攻擊者 在安全區內
					if (!checkZoneDE(skillId)) {
						return false;
					}
				}
				// 對不可見的怪物額外判斷
				final int gfxid = _targetNpc.getNpcTemplate().get_gfxid();
				switch (gfxid) {
				case 2412:// 南瓜的影子
					if (!_pc.getInventory().checkEquipped(20046)) {// 南瓜帽
						return false;
					}
					break;
				}

				// NPC需附加技能可攻擊
				final int npcId = _targetNpc.getNpcTemplate().get_npcId();
				final Integer tgskill = L1AttackList.SKNPC.get(npcId);
				if (tgskill != null) {
					if (!_pc.hasSkillEffect(tgskill)) {
						return false;
					}
				}

				// NPC指定外型不可攻擊
				final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
				if (tgpoly != null) {
					if (tgpoly.equals(_pc.getTempCharGfx())) {
						return false;
					}
				}

				// NPC抵抗技能(NPCID / 技能編號) 列表中該技能對該NPC施展失敗
				final boolean dgskill = L1AttackList.DNNPC.containsKey(npcId);
				if (dgskill) {
					final Integer[] dgskillids = L1AttackList.DNNPC.get(npcId);
					for (final Integer dgskillid : dgskillids) {
						if (dgskillid.equals(skillId)) {
							return false;
						}
					}
				}
			}

			// 魔法相消術
			if (skillId == CANCELLATION) {
				return true;
			}

			// 被攻擊者受到大地屏障
			if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
				// 施展法術不是壞物術或魔法相消術
				if ((skillId != WEAPON_BREAK) && (skillId != CANCELLATION)) {
					return false;
				}
			}
			break;
		}

		// 計算魔法成功機率
		probability = calcProbability(skillId);

		// 法師提高機率 +智力/4 (>> 1: 除) (<< 1: 乘)
		if (_pc.isWizard()) {
			probability += _pc.getInt() / 4;// 2012-05-12(9)
		}

		final int rnd = _random.nextInt(100) + 1;

		// 最大成功率90%
		probability = Math.min(probability, 90);
		// 最小成功率1%
		probability = Math.max(probability, 1);

		if (probability >= rnd) {
			isSuccess = true;

		} else {
			isSuccess = false;
		}

		// gm攻擊資訊
		if (!ConfigAlt.ALT_ATKMSG) {
			return isSuccess;

		} else {
			switch (_calcType) {
			case PC_PC:
				if (!_pc.isGm()) {
					if (!_targetPc.isGm()) {
						return isSuccess;
					}
				}
				break;

			case PC_NPC:
				if (!_pc.isGm()) {
					return isSuccess;
				}
				break;
			}
		}

		switch (_calcType) {
		case PC_PC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對PC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			if (_targetPc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("受到PC技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_targetPc.sendPackets(new S_ServerMessage(166, atkMsg
						.toString()));
			}
			break;

		case PC_NPC:
			if (_pc.isGm()) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對NPC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetNpc.getName() + " ");// 被攻擊者
				atkMsg.append(isSuccess ? "成功" : "失敗");// 資訊
				atkMsg.append(" 成功機率:" + probability + "%");// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			break;
		}
		return isSuccess;
	}

	/**
	 * 攻擊者 或是 被攻擊者 在安全區內
	 * 
	 * @param skillId
	 * @return
	 */
	private boolean checkZone(final int skillId) {
		if ((_pc != null) && (_targetPc != null)) {
			// 攻擊者 或是 被攻擊者 在安全區內
			if (_pc.isSafetyZone() || _targetPc.isSafetyZone()) {
				// 施展技能限制安全區域無法使用
				final Boolean isBoolean = L1AttackList.NZONE.get(skillId);
				if (isBoolean != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 攻擊者 或是 被攻擊者 在安全區內(對DE)
	 * 
	 * @param skillId
	 * @return
	 */
	private boolean checkZoneDE(final int skillId) {
		if ((_pc != null) && (_targetNpc != null)) {
			// 攻擊者 或是 被攻擊者 在安全區內
			if (_pc.isSafetyZone() || _targetNpc.isSafetyZone()) {
				// 施展技能限制安全區域無法使用
				final Boolean isBoolean = L1AttackList.NZONE.get(skillId);
				if (isBoolean != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 魔法命中的判斷
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcProbability(final int skillId) {
		final L1Skills l1skills = SkillsTable.get().getTemplate(skillId);
		final int attackLevel = _pc.getLevel();// 攻擊者等級
		int defenseLevel = 0;// 對手等級
		int probability = 0;// 輸出機率

		switch (_calcType) {
		case PC_PC:
			if (_targetPc.isGm()) {
				return -1;
			}
			defenseLevel = _targetPc.getLevel();
			break;

		case PC_NPC:
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
		case POLLUTE_WATER:// 污濁之水
			// 成功確率は 魔法固有係数 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			probability -= getTargetMr() / 80;
			break;

		case AREA_OF_SILENCE:// 封印禁地
		case STRIKER_GALE:// 精準射擊
		case ARMOR_BREAK: // 破壞盔甲 (3.63C 新技能 by terry0412)
			// 成功確率は 魔法固有係数 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			break;

		case ERASE_MAGIC:// 魔法消除
			// 成功確率 魔法固有係数 × LV差 + 基本確率
			probability = (int) (((l1skills.getProbabilityDice()) / 10D) * (attackLevel - defenseLevel))
					+ l1skills.getProbabilityValue();
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			probability -= getTargetMr() / 80;
			break;

		case EARTH_BIND:// 大地屏障
			// 追加智力影響(>> 1: 除) (<< 1: 乘)
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 3 + (_pc.getInt() >> 3);// /8

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 4 + (_pc.getInt() >> 2);// /4

			} else {// 攻擊者等級大於被攻擊者
				probability = 5 + (_pc.getInt() >> 1);// /2
			}
			// probability -= getTargetMr() / 40;
			break;

		case SHOCK_STUN:// 衝擊之暈
		case POWERGRIP:// 拘束移動
		case DESPERADO:// 亡命之徒
			if (attackLevel < defenseLevel) {// 攻擊者等級小於被攻擊者
				probability = 22;// SRC 20

			} else if (attackLevel == defenseLevel) { // 攻擊者等級 等於 被攻擊者
				probability = 40;// SRC NO

			} else {// 攻擊者等級大於等於被攻擊者
				probability = 60;// SRC 80
			}
			break;

		case COUNTER_BARRIER:// 反擊屏障
			// 成功機率 基本確率 + LV差1毎 +-1%
			probability = (l1skills.getProbabilityValue() + attackLevel)
					- defenseLevel;
			// 追加2倍智力影響(>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			break;

		case PANIC:// 恐慌
			// 成功機率 基本確率 + LV差1毎 +-1%
			probability = (l1skills.getProbabilityValue() + attackLevel)
					- defenseLevel;
			// 追加2倍智力影響 (>> 1: 除) (<< 1: 乘)
			probability += _pc.getOriginalMagicHit();
			break;

		case PHANTASM:// 幻想
			final int rnd = _random.nextInt(100) + 1;
			if (rnd <= 30) {
				probability = 100;

			} else {
				probability = 0;
			}
			break;

		case CONFUSION:// 混亂
			final int rad = _random.nextInt(100) + 1;
			final int trad = _random.nextInt(10) + 20;
			if (rad <= trad) {
				probability = 100;

			} else {
				probability = 0;
			}
			break;

		case RESIST_FEAR:// 恐懼無助
			final int dice4 = l1skills.getProbabilityDice();
			final int diceCount4 = Math.max(getMagicBonus() + getMagicLevel()
					+ 1, 1);

			for (int i = 0; i < diceCount4; i++) {
				probability += (_random.nextInt(dice4) + 1);
			}

			probability = (int) (probability * (getLeverage() / 10D));
			// System.out.println("probability:" +probability +
			// " getTargetMr:"+getTargetMr());
			// 智力(依職業)附加魔法命中
			probability += 2 * _pc.getOriginalMagicHit();
			// System.out.println("智力(依職業)附加魔法命中:" +probability +
			// " getTargetMr:"+getTargetMr());

			// 扣除抗魔減免
			probability -= getTargetMr();
			// System.out.println("扣除抗魔減免:" +probability +
			// " getTargetMr:"+getTargetMr());

			// 等級差(被攻擊者 - 攻擊者) / 24
			final int levelR1 = Math.max(defenseLevel / 20, 1);// 2011-11-26 24

			probability /= levelR1;
			// System.out.println("等級差(被攻擊者 - 攻擊者) / 24:" +probability +
			// " getTargetMr:"+getTargetMr());
			break;

		case GUARD_BRAKE:// 護衛毀滅
			// case HORROR_OF_DEATH:// 驚悚死神
		{
			final int dice1 = l1skills.getProbabilityDice();
			final int value = l1skills.getProbabilityValue();
			final int diceCount1 = Math.max(getMagicBonus() + getMagicLevel(),
					1);

			for (int i = 0; i < diceCount1; i++) {
				probability += (_random.nextInt(dice1) + 1 + value);
			}

			probability = (int) (probability * (getLeverage() / 10D));
			// System.out.println("probability:" +probability +
			// " getTargetMr:"+getTargetMr());

			// 追加2倍智力影響 (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			// System.out.println("追加2倍智力影響:" +probability +
			// " getTargetMr:"+getTargetMr());

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
			final int diceCount1 = Math.max(getMagicBonus() + getMagicLevel(),
					1);

			for (int i = 0; i < diceCount1; i++) {
				probability += (_random.nextInt(dice1) + 1 + value);
			}

			probability = (int) (probability * (getLeverage() / 10D));
			// System.out.println("probability:" +probability +
			// " getTargetMr:"+getTargetMr());

			// 追加2倍智力影響 (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);
			// System.out.println("追加2倍智力影響:" +probability +
			// " getTargetMr:"+getTargetMr());
			break;
		}

		// case RESIST_FEAR:// 恐懼無助*/
		case SILENCE:// 魔法封印
		case WEAPON_BREAK:// 壞物術
		case SLOW:// 緩速術
			final int dice3 = l1skills.getProbabilityDice();
			int diceCount3 = 0;

			if (_pc.isWizard()) {
				diceCount3 = getMagicBonus() + getMagicLevel() + 1;

			} else {
				diceCount3 = (getMagicBonus() + getMagicLevel()) - 1;
			}

			diceCount3 = Math.max(diceCount3, 1);

			for (int i = 0; i < diceCount3; i++) {
				probability += (_random.nextInt(dice3) + 1);
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// 智力(依職業)附加魔法命中
			probability += 2 * _pc.getOriginalMagicHit();

			// 扣除抗魔減免
			probability -= getTargetMr();

			// 等級差(被攻擊者 - 攻擊者) / 24
			final int levelR = Math.max(defenseLevel / 24, 1);

			probability /= levelR;
			break;

		case ICE_LANCE:// 冰矛圍籬 - 寒冰耐性
			// 取回技能計算機率
			final int diceICE = l1skills.getProbabilityDice();
			final int diceCountICE = Math.max(getMagicBonus() + getMagicLevel()
					+ 1, 1);

			for (int i = 0; i < diceCountICE; i++) {
				if (diceICE > 0) {
					probability += (_random.nextInt(diceICE) + 1);
				}
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);

			probability -= getTargetMr();
			break;

		default:
			// 取回技能計算機率
			final int dice2 = l1skills.getProbabilityDice();
			int diceCount2 = 0;

			if (_pc.isWizard()) {
				diceCount2 = getMagicBonus() + getMagicLevel() + 1;

			} else if (_pc.isElf()) {
				diceCount2 = (getMagicBonus() + getMagicLevel()) - 1;

			} else {
				diceCount2 = (getMagicBonus() + getMagicLevel()) - 1;
			}

			diceCount2 = Math.max(diceCount2, 1);

			for (int i = 0; i < diceCount2; i++) {
				if (dice2 > 0) {
					probability += (_random.nextInt(dice2) + 1);
				}
			}

			probability = (int) (probability * (getLeverage() / 10D));

			// 智力(依職業)附加魔法命中 *2 (>> 1: 除) (<< 1: 乘)
			probability += (_pc.getOriginalMagicHit() << 1);

			probability -= getTargetMr();

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if (((_targetNpc.getMaxHp()) >> 2) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;

				} else if (((_targetNpc.getMaxHp() << 2) >> 2) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.2;

				} else if (((_targetNpc.getMaxHp() * 3) >> 2) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
			break;
		}

		// 有害魔法成功率+% by terry0412
		if (_pc.getMagicHitUp() != 0) {
			probability += (double) (probability * _pc.getMagicHitUp() / 100);
		}

		// 抵抗有害魔法成功率+% by terry0412
		if (_calcType == PC_PC && _targetPc.getMagicHitUp() != 0) {
			probability -= (double) (probability * _targetPc.getMagicHitUp() / 100);
		}

		// 耐性 (>> 1: 除) (<< 1: 乘)
		switch (_calcType) {
		case PC_PC:
			switch (skillId) {
			case EARTH_BIND:// 大地屏障 - 支撐耐性
				probability -= (_targetPc.getRegistSustain() >> 1);
				break;

			case SHOCK_STUN:// 衝擊之暈 - 昏迷耐性
			case BONE_BREAK:// 骷髏毀壞
				probability -= (_targetPc.getRegistStun() >> 1);
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
			break;
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
		case PC_PC:
			damage = calcPcMagicDamage(skillId);
			break;

		case PC_NPC:
			damage = calcNpcMagicDamage(skillId);
			break;
		}

		if (skillId == FINAL_BURN) {
			return damage;
		}
		return calcMrDefense(damage);
	}

	/**
	 * PC對PC魔法傷害計算
	 * 
	 * @param skillId
	 * @return
	 */
	private int calcPcMagicDamage(final int skillId) {
		if (_targetPc == null) {
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
			return dmg = _pc.getCurrentHp() / 3; // 技能-會心一擊改成(當前體力/3) by
													// terry0412

		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (int) (dmg * (getLeverage() / 10D));
		}

		// 魔法傷害減免+% by terry0412
		if (_targetPc.getMagicDmgDown() != 0) {
			dmg -= (double) (dmg * _targetPc.getMagicDmgDown() / 100);
		}

		dmg -= _targetPc.getDamageReductionByArmor()
				+ _targetPc.getMagicDmgReduction();

		dmg -= _targetPc.dmgDowe(); // 機率傷害減免

		if (_targetPc.getClanid() != 0) {
			dmg -= getDamageReductionByClan(_targetPc);// 血盟技能魔法傷害減免
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

		// 魔化色霸氣 (機率使用魔法攻擊傷害X2倍) by terry0412
		if (_pc.hasSkillEffect(DOMINATE_POWER_D)
				&& (_pc.getValue() > _random.nextInt(100))) {
			dmg *= 2;
		}

		boolean dmgX2 = false;// 傷害除2
		// 取回技能
		if (!_targetPc.getSkillisEmpty()
				&& (_targetPc.getSkillEffect().size() > 0)) {
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

		if (dmgX2) {
			dmg = (dmg >> 1);// dmg /= 2;
		}

		// 技能鏡反射
		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getWis() >= _random.nextInt(100)) {
					_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_targetPc.sendPacketsX8(new S_SkillSound(_targetPc.getId(),
							4395));
					_pc.receiveDamage(_targetPc, dmg, false, false);
					dmg = 0;
					_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
				}
			}
		}

		if (_targetPc.isWarrior()
				&& (_targetPc.getCurrentHp() < ((_targetPc.getMaxHp() / 100) * 40// 血量低於40%
				))
				&& _targetPc.isCrystal()// 足夠魔法結晶體
				&& _targetPc.isTITANMAGIC()
				&& (_random.nextInt(100) < ConfigRate.WARRIOR_TITANMAGIC)) {
			dmg = 0;
			actionTitan();
			commitTitan(_targetPc.colcTitanDmg());
		}

		return Math.max(dmg, 0);
	}

	/**
	 * PC對NPC魔法傷害計算
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

		final int npcId = _targetNpc.getNpcTemplate().get_npcId();
		final Integer tgskill = L1AttackList.SKNPC.get(npcId);
		if (tgskill != null) {
			if (!_pc.hasSkillEffect(tgskill)) {
				return 0;
			}
		}

		final Integer tgpoly = L1AttackList.PLNPC.get(npcId);
		if (tgpoly != null) {
			if (tgpoly.equals(_pc.getTempCharGfx())) {
				return 0;
			}
		}

		// 需使用淨化藥水才能攻擊此NPC by terry0412
		if (_targetNpc.getNpcTemplate().is_attack_request()) {
			// 檢查PC是否有允許攻擊清單
			if (!_pc.check_allow_list(npcId)) {
				return 0;
			}
		}

		int dmg = 0;
		// if (skillId == FINAL_BURN) {
		// dmg = _pc.getCurrentMp();

		// } else {
		dmg = calcMagicDiceDamage(skillId);
		dmg = (int) (dmg * (getLeverage() / 10D));
		// }

		boolean isNowWar = false;// 戰爭中
		final int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = ServerWarExecutor.get().isNowWar(castleId);
		}

		boolean isPet = false;// 是寵物
		if (_targetNpc instanceof L1PetInstance) {
			isPet = true;
			if (_targetNpc.getMaster().equals(_pc)) {
				dmg = 0;
			}
		}
		if (_targetNpc instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) _targetNpc;
			if (summon.isExsistMaster()) {
				isPet = true;
			}
			if (_targetNpc.getMaster().equals(_pc)) {
				dmg = 0;
			}
		}

		// 魔化色霸氣 (機率使用魔法攻擊傷害X2倍) by terry0412
		if ((dmg != 0) && _pc.hasSkillEffect(DOMINATE_POWER_D)
				&& (_pc.getValue() > _random.nextInt(100))) {
			dmg *= 2;
		}

		if (!isNowWar && isPet) {// 非戰爭中 對象是寵物
			if (dmg != 0) {
				dmg = (dmg >> 3);// dmg /= 8;
			}
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

		if (_pc.getClanid() != 0) {
			// 血盟技能魔法傷害增加
			magicDamage += getDamageUpByClan(_pc);
		}

		switch (_pc.guardianEncounter()) {
		case 3:// 邪惡的守護 Lv.1
			magicDamage += 1;
			break;

		case 4:// 邪惡的守護 Lv.2
			magicDamage += 2;
			break;

		case 5:// 邪惡的守護 Lv.3
			magicDamage += 3;
			break;
		}

		final int spByItem = getTargetSp();// this._pc.getSp() -
											// this._pc.getTrueSp(); //
											// アイテムによるSP変動
		charaIntelligence = Math.max((_pc.getInt() + spByItem) - 12, 1);

		/*
		 * if (charaIntelligence < 1) { charaIntelligence = 1; }
		 */

		final double attrDeffence = calcAttrResistance(l1skills.getAttr());

		final double coefficient = Math.max(
				((1.0 - attrDeffence) + ((charaIntelligence * 3.0) / 32.0)),
				0.0);
		/*
		 * if (coefficient < 0) { coefficient = 0; }
		 */

		magicDamage *= coefficient;

		final int rnd = _random.nextInt(100) + 1;
		if (l1skills.getSkillLevel() <= 6) {
			if (rnd <= (10 + _pc.getOriginalMagicCritical())) {
				final double criticalCoefficient = 1.5; // 魔法クリティカル
				magicDamage *= criticalCoefficient;
			}
		}

		// 魔法暴擊發動機率+% (發動後技能傷害*1.5倍) by terry0412
		if (_pc.getMagicDoubleHit() != 0
				&& _random.nextInt(100) < _pc.getMagicDoubleHit()) {
			magicDamage *= 1.5;
			int actid = 1;// 預設攻擊動作
			int[] data = null; // 攻擊封包的參數
			int OutGfxId = 16157; // 輸出的圖示代碼
			data = new int[] { actid, magicDamage, OutGfxId, 0 }; // 參數
			S_Attack atk = new S_Attack(_targetNpc, _targetNpc, data);
			_pc.sendPacketsAll(atk); // 對自身非自身送出
		}

		// 魔法傷害增加+% by terry0412
		if (_pc.getMagicDmgUp() != 0) {
			magicDamage += (double) (magicDamage * _pc.getMagicDmgUp() / 100);
		}

		magicDamage += _pc.getOriginalMagicDamage() + _pc.getMagicDmgModifier();

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

		// 傷害顯示
//		if (_pc.is_attack_view()) { // 傷害顯示開關
//			int units = magicDamage % 10;
//			int tens = (magicDamage / 10) % 10;
//			int hundreads = (magicDamage / 100) % 10;
//			int thousands = (magicDamage / 1000) % 10;
//			int tenthousands = (magicDamage / 10000) % 10;
//			if ((units > 0) || (tens > 0) || (hundreads > 0) || (thousands > 0)
//					|| (tenthousands > 0)) {
//				units += 16100;
//				final S_SkillSound units_s = new S_SkillSound(
//						_targetPc.getId(), units);
//				_pc.sendPackets(units_s);
//			}
//			if ((tens > 0) || (hundreads > 0) || (thousands > 0)
//					|| (tenthousands > 0)) {
//				tens += 16110;
//				final S_SkillSound tens_s = new S_SkillSound(_targetPc.getId(),
//						tens);
//				_pc.sendPackets(tens_s);
//			}
//			if ((hundreads > 0) || (thousands > 0) || (tenthousands > 0)) {
//				hundreads += 16120;
//				final S_SkillSound hundreads_s = new S_SkillSound(
//						_targetPc.getId(), hundreads);
//				_pc.sendPackets(hundreads_s);
//			}
//			if ((thousands > 0) || (tenthousands > 0)) {
//				thousands += 16130;
//				final S_SkillSound thousands_s = new S_SkillSound(
//						_targetPc.getId(), thousands);
//				_pc.sendPackets(thousands_s);
//			}
//			if (tenthousands > 0) {
//				tenthousands += 16140;
//				final S_SkillSound tenthousands_s = new S_SkillSound(
//						_targetPc.getId(), tenthousands);
//				_pc.sendPackets(tenthousands_s);
//			}
//			if (magicDamage == 0) {
//				final S_SkillSound miss = new S_SkillSound(_targetPc.getId(),
//						16150);
//				_pc.sendPackets(miss);
//			}
//			if (magicDamage > 900) {
//				final S_SkillSound hundreads_s1 = new S_SkillSound(
//						_targetPc.getId(), 16157);
//				_pc.sendPackets(hundreads_s1);
//			}
//		}

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
		final Double[] mrF = L1AttackList.MRDMG.get(new Integer(mr));
		if (mrF != null) {
			mrFloor = mrF[0].doubleValue();
			mrCoefficient = mrF[1].doubleValue();

		} else {
			mrFloor = 11;
			mrCoefficient = 0.5;
		}

		// 計算減低的傷害
		dmg *= (mrCoefficient - (0.01 * Math.floor((mr - _pc
				.getOriginalMagicHit()) / mrFloor)));

		return dmg;
	}

	/**
	 * 計算結果反映
	 * 
	 * @param damage
	 * @param drainMana
	 */
	@Override
	public void commit(final int damage, final int drainMana) {
		L1Character _target = null;
		switch (_calcType) {
		case PC_PC:
			_target = _targetPc;
			commitPc(damage, drainMana);
			break;

		case PC_NPC:
			_target = _targetNpc;
			commitNpc(damage, drainMana);
			break;
		}
		// Smile 2015/04/25 加入魔法傷害顯示優化寫法
//		int i = -1;
//		int k = -1;
//		int h = -1;
//		int s = -1;
//		int p = -1;
//		int m = -1;
//		// 當傷害輸出只引用了各階段的公式
//		if (_pc.is_attack_view()) {
//			if (damage > 0) {
//				i = (int) ((damage / Math.pow(10, 0)) % 10) + 16100;// 個位
//				if (damage >= 10) {
//					k = (int) ((damage / Math.pow(10, 1)) % 10) + 16110;// 十位
//					if (damage >= 100) {
//						h = (int) ((damage / Math.pow(10, 2)) % 10) + 16120;// 百位
//						if (damage >= 1000) {
//							s = (int) ((damage / Math.pow(10, 3)) % 10) + 16130;// 千位
//							if (damage >= 10000) {
//								m = (int) ((damage / Math.pow(10, 4)) % 10) + 16140;// 萬位
//								if (damage >= 2000) {
//									p = (int) ((damage / Math.pow(10, 5)) % 10) + 16157;// 爆擊
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		// Smile 2015/04/25 加入魔法傷害顯示優化寫法
//		// 引用了各階段的公式之後開始下列的圖檔調用
//		if (_pc.is_attack_view()) {
//			if (i != -1) {
//				this._pc.sendPackets(new S_SkillSound(_target.getId(), i));// 個位數
//				if (k != -1) {
//					this._pc.sendPackets(new S_SkillSound(_target.getId(), k));// 十位數
//					if (h != -1) {
//						this._pc.sendPackets(new S_SkillSound(_target.getId(),
//								h));// 百位數
//						if (s != -1) {
//							this._pc.sendPackets(new S_SkillSound(_target
//									.getId(), s));// 千位數
//							if (m != -1) {
//								this._pc.sendPackets(new S_SkillSound(_target
//										.getId(), m));// 萬位數
//								if (p != -1) {
//									this._pc.sendPackets(new S_SkillSound(
//											_target.getId(), p));// 爆擊
//								}
//							}
//						}
//					}
//				}
//			} else {
//				this._pc.sendPackets(new S_SkillSound(_target.getId(), 16150));// Miss
//			}
//		}
		// ダメージ値及び命中率確認用メッセージ
		if (!ConfigAlt.ALT_ATKMSG) {
			return;
		} else {
			switch (_calcType) {
			case PC_PC:
				if (_pc.getAccessLevel() == 0) {
					if (_targetPc.getAccessLevel() == 0) {
						return;
					}
				}
				break;

			case PC_NPC:
				if (_pc.getAccessLevel() == 0) {
					return;
				}
				break;
			}
		}

		switch (_calcType) {
		case PC_PC:
			if (_pc.getAccessLevel() > 0) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對PC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetPc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			if (_targetPc.getAccessLevel() > 0) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("受到PC技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetPc.getName() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetPc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_targetPc.sendPackets(new S_ServerMessage(166, atkMsg
						.toString()));
			}
			break;

		case PC_NPC:
			if (_pc.getAccessLevel() > 0) {
				final StringBuilder atkMsg = new StringBuilder();
				atkMsg.append("對NPC送出技能: ");
				atkMsg.append(_pc.getName() + ">");// 攻擊者
				atkMsg.append(_targetNpc.getNameId() + " ");// 被攻擊者
				atkMsg.append("傷害: " + damage);// 資訊
				atkMsg.append(" 目標HP:" + _targetNpc.getCurrentHp());// 最終資訊
				// 166 \f1%0%s %4%1%3 %2。
				_pc.sendPackets(new S_ServerMessage(166, atkMsg.toString()));
			}
			break;
		}
	}

	/**
	 * 對pc傷害的輸出
	 * 
	 * @param damage
	 * @param drainMana
	 */
	private void commitPc(final int damage, int drainMana) {
		try {
			if (drainMana > 0) {
				if (_targetPc.getCurrentMp() > 0) {
					drainMana = Math.min(drainMana, _targetPc.getCurrentMp());
					final int newMp = _pc.getCurrentMp() + drainMana;
					_pc.setCurrentMp(newMp);

				} else {
					drainMana = 0;
				}
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage, true, false);
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
	private void commitNpc(final int damage, int drainMana) {
		try {
			if (drainMana > 0) {
				if (_targetNpc.getCurrentMp() > 0) {
					final int drainValue = _targetNpc.drainMana(drainMana);
					final int newMp = _pc.getCurrentMp() + drainValue;
					_pc.setCurrentMp(newMp);

				} else {
					drainMana = 0;
				}
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}