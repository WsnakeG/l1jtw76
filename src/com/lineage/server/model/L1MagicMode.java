package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.DRAGON4;
import static com.lineage.server.model.skill.L1SkillId.DRAGON5;
import static com.lineage.server.model.skill.L1SkillId.DRAGON7;

import java.util.ConcurrentModificationException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.sql.ClanStepTable;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.templates.L1Skills;

/**
 * 魔法攻擊判定
 * 
 * @author dexc
 */
public abstract class L1MagicMode {

	private static final Log _log = LogFactory.getLog(L1MagicMode.class);

	protected int _calcType;

	protected static final int PC_PC = 1;

	protected static final int PC_NPC = 2;

	protected static final int NPC_PC = 3;

	protected static final int NPC_NPC = 4;

	protected L1PcInstance _pc = null;

	protected L1PcInstance _targetPc = null;

	protected L1NpcInstance _npc = null;

	protected L1NpcInstance _targetNpc = null;

	protected int _leverage = 10; // 1/10倍で表現する。

	protected static final Random _random = new Random();

	/**
	 * 傷害為0
	 * 
	 * @param pc
	 * @return true 傷害為0
	 */
	protected static boolean dmg0(final L1Character character) {
		try {
			if (character == null) {
				return false;
			}

			if (character.getSkillisEmpty()) {
				return false;
			}

			if (character.getSkillEffect().size() <= 0) {
				return false;
			}

			for (final Integer key : character.getSkillEffect()) {
				final Integer integer = L1AttackList.SKM0.get(key);
				if (integer != null) {
					return true;
				}
			}

		} catch (final ConcurrentModificationException e) {
			// 技能取回發生其他線程進行修改
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return false;
	}

	/**
	 * 血盟技能魔法傷害增加
	 * 
	 * @return
	 */
	protected static double getDamageUpByClan(final L1PcInstance pc) {
		double dmg = 0.0;
		try {
			if (pc == null) {
				return 0.0;
			}
			// 套裝增加魔法傷害
			dmg += pc.get_magic_modifier_dmg();

			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return dmg;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				// 4:魔擊(增加魔法攻擊力)
				if (pc.get_other().get_clanskill() == 4) {
					final int clanMan = clan.getOnlineClanMemberSize();
					final int clanstep = ClanStepTable.skill3.get(clan.getClanStep());
					dmg += (0.25 * clanMan) * (clanstep);
				}

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return 0.0;
		}
		return dmg;
	}

	/**
	 * 血盟技能魔法傷害減免
	 * 
	 * @return
	 */
	protected static double getDamageReductionByClan(final L1PcInstance targetPc) {
		double dmg = 0.0;
		try {
			if (targetPc == null) {
				return 0.0;
			}
			// 套裝減免魔法傷害
			dmg += targetPc.get_magic_reduction_dmg();

			final L1Clan clan = targetPc.getClan();
			if (clan == null) {
				return 0.0;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				// 8:消魔(增加魔法傷害減免)
				if (targetPc.get_other().get_clanskill() == 8) {
					final int clanMan = clan.getOnlineClanMemberSize();
					final int clanstep = ClanStepTable.skill4.get(clan.getClanStep());
					dmg += (0.25 * clanMan) * (clanstep);
				}

			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return 0.0;
		}
		return dmg;
	}

	/**
	 * 設置該技能魔法等級-攻擊倍率(1/10)
	 * 
	 * @param i
	 */
	public void setLeverage(final int i) {
		_leverage = i;
	}

	/**
	 * 該技能魔法等級-攻擊倍率(1/10)
	 * 
	 * @return
	 */
	protected int getLeverage() {
		return _leverage;
	}

	/**
	 * 施展者魔攻
	 * 
	 * @return
	 */
	protected int getTargetSp() {
		int sp = 0;
		switch (_calcType) {
		case PC_PC:
		case PC_NPC:
			sp = _pc.getSp() - _pc.getTrueSp();

			switch (_pc.guardianEncounter()) {
			case 3:// 邪惡的守護 Lv.1
				sp += 1;
				break;

			case 4:// 邪惡的守護 Lv.2
				sp += 2;
				break;

			case 5:// 邪惡的守護 Lv.3
				sp += 3;
				break;
			}

			// 風 魔法重擊增加 睡眠耐性+3，持續1200秒
			if (_pc.hasSkillEffect(DRAGON4)) {
				sp += 1;
			}

			// 生命-物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 額外攻擊點數+2 防護中毒狀態
			if (_pc.hasSkillEffect(DRAGON5)) {
				sp += 1;
			}

			// 形象-物理攻擊迴避率+10% 魔法傷害減免+50 魔法暴擊率+1 支撐耐性+3
			if (_pc.hasSkillEffect(DRAGON7)) {
				sp += 1;
			}

			break;

		case NPC_NPC:
		case NPC_PC:
			sp = _npc.getSp() - _npc.getTrueSp();
			break;
		}
		return sp;
	}

	/**
	 * 目標魔防
	 * 
	 * @return
	 */
	protected int getTargetMr() {
		int mr = 0;
		switch (_calcType) {
		case NPC_PC:
		case PC_PC:
			if (_targetPc == null) {
				return 0;
			}
			mr = _targetPc.getMr();

			switch (_targetPc.guardianEncounter()) {
			case 0:// 正義的守護 Lv.1
				mr += 3;
				break;

			case 1:// 正義的守護 Lv.2
				mr += 6;
				break;

			case 2:// 正義的守護 Lv.3
				mr += 9;
				break;
			}
			break;

		case NPC_NPC:
		case PC_NPC:
			if (_targetNpc == null) {
				return 0;
			}
			mr = _targetNpc.getMr();
			break;
		}
		return mr;
	}

	/**
	 * 迴避
	 * 
	 * @return true:迴避成功 false:迴避未成功
	 */
	protected boolean calcEvasion() {
		if (_targetPc == null) {
			return false;
		}
		final int ev = _targetPc.get_evasion();
		if (ev == 0) {
			return false;
		}
		final int rnd = _random.nextInt(1000) + 1;
		return ev >= rnd;
	}

	/**
	 * 屬性傷害 減低 與 提昇 attr:0.無属性魔法,1.地魔法,2.火魔法,4.水魔法,8.風魔法(,16.光魔法)
	 */
	protected double calcAttrResistance(final int attr) {
		int resist = 0;
		switch (_calcType) {
		case PC_PC:
		case NPC_PC:
			if (_targetPc == null) {
				return 0;
			}
			switch (attr) {
			case L1Skills.ATTR_EARTH:
				resist = _targetPc.getEarth();
				break;

			case L1Skills.ATTR_FIRE:
				resist = _targetPc.getFire();
				break;

			case L1Skills.ATTR_WATER:
				resist = _targetPc.getWater();
				break;

			case L1Skills.ATTR_WIND:
				resist = _targetPc.getWind();
				break;
			}
			break;

		case PC_NPC:
		case NPC_NPC:
			if (_targetNpc == null) {
				return 0;
			}
			switch (attr) {
			case L1Skills.ATTR_EARTH:
				resist = _targetNpc.getEarth();
				break;

			case L1Skills.ATTR_FIRE:
				resist = _targetNpc.getFire();
				break;

			case L1Skills.ATTR_WATER:
				resist = _targetNpc.getWater();
				break;

			case L1Skills.ATTR_WIND:
				resist = _targetNpc.getWind();
				break;
			}
			break;
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;

		} else {
			resistFloor *= -1;
		}

		final double attrDeffence = resistFloor / 32.0;

		return attrDeffence;
	}

	/**
	 * ■■■■■■■■■■■■■■ 成功判定 ■■■■■■■■■■■■■ ●●●● 確率系魔法の成功判定 ●●●● 計算方法 攻撃側ポイント：LV +
	 * ((MagicBonus * 3) * 魔法固有係数) 防御側ポイント：((LV / 2) + (MR * 3)) / 2
	 * 攻撃成功率：攻撃側ポイント - 防御側ポイント
	 */
	public abstract boolean calcProbabilityMagic(final int skillId);

	/**
	 * 魔法傷害值計算
	 * 
	 * @param skillId
	 * @return
	 */
	public abstract int calcMagicDamage(final int skillId);

	/**
	 * ヒール回復量（対アンデッドにはダメージ）を算出
	 * 
	 * @param skillId
	 * @return
	 */
	public abstract int calcHealing(final int skillId);

	/**
	 * 計算結果反映
	 * 
	 * @param damage
	 * @param drainMana
	 */
	public abstract void commit(final int damage, final int drainMana);

	public void commitTitan(final int dmg) {
		if (dmg == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			if ((_pc != null) && (_targetPc != null)) {
				_pc.receiveDamage(_targetPc, dmg, false, false);
			}
		} else if (_calcType == NPC_PC) {
			if ((_npc != null) && (_targetPc != null)) {
				_npc.receiveDamage(_targetPc, dmg);
			}
		}
	}

	public void actionTitan() {
		final int targetId = _targetPc.getId();
		final int target_x = _targetPc.getX();
		final int target_y = _targetPc.getY();

		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(target_x, target_y));
			_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), ActionCodes.ACTION_Damage));
			_pc.sendPacketsAll(new S_SkillSound(targetId, 12559));

		} else if (_calcType == NPC_PC) {
			_npc.setHeading(_npc.targetDirection(target_x, target_y));
			_npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(), ActionCodes.ACTION_Damage));
			_npc.broadcastPacketAll(new S_SkillSound(targetId, 12559));
		}
	}

}
