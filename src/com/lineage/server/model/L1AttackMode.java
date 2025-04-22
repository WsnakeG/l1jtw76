package com.lineage.server.model;

import java.util.ConcurrentModificationException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.config.ConfigRate;
import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.sql.ClanStepTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 攻擊判定
 * 
 * @author dexc
 */
public abstract class L1AttackMode {

	private static final Log _log = LogFactory.getLog(L1AttackMode.class);

	// 目標物件
	protected L1Character _target;

	// 執行PC
	protected L1PcInstance _pc;

	// 目標PC
	protected L1PcInstance _targetPc;

	// 執行NPC
	protected L1NpcInstance _npc;

	// 目標NPC
	protected L1NpcInstance _targetNpc;

	protected int _targetId;

	protected int _targetX;

	protected int _targetY;

	protected int _statusDamage;

	protected int _hitRate;

	protected int _calcType;

	protected static final int PC_PC = 1;

	protected static final int PC_NPC = 2;

	protected static final int NPC_PC = 3;

	protected static final int NPC_NPC = 4;

	protected boolean _isHit;

	protected int _damage;

	protected int _drainMana;

	protected int _drainHp;

	protected int _attckGrfxId;

	protected int _attckActId;

	// 攻撃者がプレイヤーの場合の武器情報
	protected L1ItemInstance _weapon;

	protected int _weaponId;

	protected int _weaponType;

	protected int _weaponType2;

	protected int _weaponAddHit;// 命中追加

	protected int _weaponAddDmg;// 傷害追加

	protected int _weaponSmall;// 對小型

	protected int _weaponLarge;// 對大型

	protected int _weaponRange = 1;// 武器攻擊距離

	protected int _weaponBless = 1;// 祝福類型

	protected int _weaponEnchant;// 強化質

	protected int _weaponMaterial;// 材質

	protected int _weaponDoubleDmgChance;

	protected int _weaponAttrEnchantKind;

	protected int _weaponAttrEnchantLevel;

	protected L1ItemInstance _arrow;

	protected L1ItemInstance _sting;

	protected int _leverage = 10; // 攻擊倍率(1/10)

	protected static final Random _random = new Random();

	/**
	 * 血盟技能傷害增加
	 * 
	 * @return
	 */
	protected static double getDamageUpByClan(final L1PcInstance pc) {
		double dmg = 0.0;
		try {
			if (pc == null) {
				return 0.0;
			}
			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return 0.0;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				// 1:狂暴(增加物理攻擊力)
				if (pc.get_other().get_clanskill() == 1) {
					final int clanMan = clan.getOnlineClanMemberSize();
					final int clanstep = ClanStepTable.skill1.get(clan
							.getClanStep());
					dmg += (0.25 * clanMan) * (clanstep);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return dmg;
	}

	/**
	 * 血盟技能傷害減免
	 * 
	 * @param targetPc
	 * @return
	 */
	protected static double getDamageReductionByClan(final L1PcInstance targetPc) {
		double dmg = 0.0;
		try {
			if (targetPc == null) {
				return 0.0;
			}
			final L1Clan clan = targetPc.getClan();
			if (clan == null) {
				return 0.0;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				// 2:寂靜(增加物理傷害減免)
				if (targetPc.get_other().get_clanskill() == 2) {
					final int clanMan = clan.getOnlineClanMemberSize();
					final int clanstep = ClanStepTable.skill2.get(clan
							.getClanStep());
					dmg += (0.25 * clanMan) * (clanstep);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return dmg;
	}

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
		}
		return false;
	}

	/**
	 * 技能增加閃避
	 * 
	 * @param character
	 * @return
	 */
	protected static int attackerDice(final L1Character character) {
		try {
			int attackerDice = 0;
			if (character.get_dodge() > 0) {
				attackerDice -= character.get_dodge();
			}
			if (character.get_dodge_down() > 0) {
				attackerDice += character.get_dodge_down();
			}
			return attackerDice;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * 攻擊倍率(1/10)
	 * 
	 * @param i
	 */
	public void setLeverage(final int i) {
		_leverage = i;
	}

	/**
	 * 攻擊倍率(1/10)
	 * 
	 * @return
	 */
	protected int getLeverage() {
		return _leverage;
	}

	public void setActId(final int actId) {
		_attckActId = actId;
	}

	public void setGfxId(final int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}

	/**
	 * ER迴避率
	 * 
	 * @return true:命中 false:未命中
	 */
	protected boolean calcErEvasion() {
		final int er = _targetPc.getEr();
		final int rnd = _random.nextInt(100) + 1;
		return er < rnd;
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
	 * PC防禦力傷害減低
	 * 
	 * @return
	 */
	protected int calcPcDefense() {
		try {
			if (_targetPc != null) {
				final int ac = Math.max(0, 10 - _targetPc.getAc());

				final int acDefMax = _targetPc.getClassFeature()
						.getAcDefenseMax(ac);
				if (acDefMax != 0) {
					// (>> 1: 除) (<< 1: 乘) XXX
					final int srcacd = Math.max(1, (acDefMax >> 3));
					final int acdown = _random.nextInt(acDefMax) + srcacd;
					// System.out.println("acdown:"+acdown+" srcacd:"+srcacd);
					return acdown;
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return 0;
	}

	/**
	 * (NPC防禦力 + 額外傷害減低) 傷害減低
	 * 
	 * @return
	 */
	protected int calcNpcDamageReduction() {
		// TEST
		final int damagereduction = _targetNpc.getNpcTemplate()
				.get_damagereduction();// 額外傷害減低
		try {
			final int srcac = _targetNpc.getAc();
			final int ac = Math.max(0, 10 - srcac);

			final int acDefMax = ac / 7;// 防禦力傷害減免降低1/7 XXX
			if (acDefMax != 0) {
				final int srcacd = Math.max(1, acDefMax);// XXX
				return _random.nextInt(acDefMax) + srcacd + damagereduction;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return damagereduction;
	}

	/**
	 * 反擊屏障的傷害反擊計算
	 * 
	 * @return
	 */
	protected int calcCounterBarrierDamage() {
		int damage = 0;
		try {
			// 反擊對象是PC
			if (_targetPc != null) {
				final L1ItemInstance weapon = _targetPc.getWeapon();
				if (weapon != null) {
					if (weapon.getItem().getType() == 3) {
						// 雙手劍
						// (BIG最大ダメージ+強化数+追加ダメージ)*2
						// (>> 1: 除) (<< 1: 乘)
						// 161226 傷害計算方式為PC自身力量+大怪攻擊+武器加值+額外攻擊數值 = XX * 設定倍率
						damage = (_targetPc.getStr()
								+ weapon.getItem().getDmgLarge()
								+ weapon.getEnchantLevel() + weapon.getItem()
								.getDmgModifier()) * ConfigRate.COUNTER_BARRIER;
					}
				}

				// 反擊對象是NPC
			} else if (_targetNpc != null) {
				// (>> 1: 除) (<< 1: 乘) 原 << 1
				// 161226 修改傷害為PC自身力量+怪物的力敏體+等級 = 傷害 * 設定倍率
				damage = (_targetPc.getStr() + _targetNpc.getStr()
						+ _targetNpc.getDex() + _targetNpc.getLevel()) << 2;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return damage;
	}

	/**
	 * 紋章傷害減免
	 * 
	 * @return
	 */
	protected double coatArms() {
		int damage = 100;
		try {
			if (_targetPc != null) {
				damage -= _targetPc.get_dmgDown();
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return (double) damage / 100;
	}

	public abstract boolean calcHit();

	/**
	 * 攻擊資訊送出
	 */
	public abstract void action();

	/**
	 * 傷害計算
	 * 
	 * @return
	 */
	public abstract int calcDamage();

	/**
	 * 計算結果反映
	 */
	public abstract void commit();

	/**
	 * 攻擊使用武器是否為近距離武器判斷
	 * 
	 * @return
	 */
	public abstract boolean isShortDistance();

	/**
	 * 反擊屏障的傷害反擊
	 */
	public abstract void commitCounterBarrier();

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

	public void actionTitan(final boolean check) {
		int gfxid = 12555;
		if (check) {
			gfxid = 12557;
		}
		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY));
			_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.sendPacketsAll(new S_SkillSound(_targetId, gfxid));

		} else if (_calcType == NPC_PC) {
			_npc.setHeading(_npc.targetDirection(_targetX, _targetY));
			_npc.broadcastPacketAll(new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
			_npc.broadcastPacketAll(new S_SkillSound(_targetId, gfxid));
		}
	}

}
