package com.lineage.server.model;

import static com.lineage.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.BERSERKERS;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.ICE_LANCE;
import static com.lineage.server.model.skill.L1SkillId.MOVE_STOP;
import static com.lineage.server.model.skill.L1SkillId.STATUS_FREEZE;

import java.util.Random;

import com.lineage.server.ActionCodes;
import com.lineage.server.datatables.SkillsTable;
import com.lineage.server.datatables.WeaponSkillTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1MonsterInstance;
import com.lineage.server.model.Instance.L1NpcInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.Instance.L1PetInstance;
import com.lineage.server.model.Instance.L1SummonInstance;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_DoActionGFX;
import com.lineage.server.serverpackets.S_EffectLocation;
import com.lineage.server.serverpackets.S_Paralysis;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;
import com.lineage.server.serverpackets.S_UseAttackSkill;
import com.lineage.server.templates.L1Skills;
import com.lineage.server.utils.L1SpawnUtil;
import com.lineage.server.world.World;

/**
 * 武器技能
 * 
 * @author daien
 */
public class L1WeaponSkill {

	private static Random _random = new Random();

	private final int _weaponId;

	private final int _probability;

	private final int _fixDamage;

	private final int _randomDamage;

	private final int _area;

	private final int _skillId;

	private final int _skillTime;

	private final int _effectId;

	private final int _effectTarget; // エフェクトの対象 0:相手 1:自分

	private final boolean _isArrowType;

	private final int _attr;

	public L1WeaponSkill(final int weaponId, final int probability, final int fixDamage,
			final int randomDamage, final int area, final int skillId, final int skillTime,
			final int effectId, final int effectTarget, final boolean isArrowType, final int attr) {
		_weaponId = weaponId;
		_probability = probability;
		_fixDamage = fixDamage;
		_randomDamage = randomDamage;
		_area = area;
		_skillId = skillId;
		_skillTime = skillTime;
		_effectId = effectId;
		_effectTarget = effectTarget;
		_isArrowType = isArrowType;
		_attr = attr;
	}

	public int getWeaponId() {
		return _weaponId;
	}

	public int getProbability() {
		return _probability;
	}

	public int getFixDamage() {
		return _fixDamage;
	}

	public int getRandomDamage() {
		return _randomDamage;
	}

	public int getArea() {
		return _area;
	}

	public int getSkillId() {
		return _skillId;
	}

	public int getSkillTime() {
		return _skillTime;
	}

	public int getEffectId() {
		return _effectId;
	}

	public int getEffectTarget() {
		return _effectTarget;
	}

	public boolean isArrowType() {
		return _isArrowType;
	}

	public int getAttr() {
		return _attr;
	}

	public static double getWeaponSkillDamage(final L1PcInstance pc, final L1Character cha,
			final int weaponId) {
		final L1WeaponSkill weaponSkill = WeaponSkillTable.get().getTemplate(weaponId);
		if ((pc == null) || (cha == null) || (weaponSkill == null)) {
			return 0;
		}

		final int chance = _random.nextInt(100) + 1;
		if (weaponSkill.getProbability() < chance) {
			return 0;
		}

		final int skillId = weaponSkill.getSkillId();
		if (skillId != 0) {
			final L1Skills skill = SkillsTable.get().getTemplate(skillId);
			if ((skill != null) && skill.getTarget().equals("buff")) {
				if (!isFreeze(cha)) { // 凍結状態orカウンターマジック中
					cha.setSkillEffect(skillId, weaponSkill.getSkillTime() * 1000);
				}
			}
		}

		final int effectId = weaponSkill.getEffectId();
		// 具有動畫
		if (effectId > 0) {
			int chaId = 0;
			if (weaponSkill.getEffectTarget() == 0) {
				chaId = cha.getId();

			} else {
				chaId = pc.getId();
			}
			final boolean isArrowType = weaponSkill.isArrowType();
			if (!isArrowType) {
				if (pc.is_send_weapon_gfxid()) {
					pc.sendPackets(new S_SkillSound(chaId, effectId));
				}
				pc.broadcastPacketForWeapon(new S_SkillSound(chaId, effectId));

			} else {
				final S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(), effectId, cha.getX(),
						cha.getY(), ActionCodes.ACTION_Attack, false);
				if (pc.is_send_weapon_gfxid()) {
					pc.sendPackets(packet);
				}
				pc.broadcastPacketForWeapon(packet);
			}
		}
		double damage = 0;
		final int randomDamage = weaponSkill.getRandomDamage();
		if (randomDamage != 0) {
			damage = _random.nextInt(randomDamage);
		}
		damage += weaponSkill.getFixDamage();

		final int area = weaponSkill.getArea();
		if ((area > 0) || (area == -1)) { // 範圍技能
			for (final L1Object object : World.get().getVisibleObjects(cha, area)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()) {
					continue;
				}
				if (object.getId() == cha.getId()) { // 攻撃対象はL1Attackで処理するため除外
					continue;
				}

				// 攻撃対象がMOBの場合は、範囲内のMOBにのみ当たる
				// 攻撃対象がPC,Summon,Petの場合は、範囲内のPC,Summon,Pet,MOBに当たる
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if ((cha instanceof L1PcInstance) || (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					if (!((object instanceof L1PcInstance) || (object instanceof L1SummonInstance)
							|| (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
						continue;
					}
				}

				damage = calcDamageReduction(pc, (L1Character) object, damage, weaponSkill.getAttr());
				if (damage <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) object;
					// 受傷動作
					targetPc.sendPacketsX8(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));
					targetPc.receiveDamage(pc, (int) damage, false, false);

				} else if ((object instanceof L1SummonInstance) || (object instanceof L1PetInstance)
						|| (object instanceof L1MonsterInstance)) {
					final L1NpcInstance targetNpc = (L1NpcInstance) object;
					// 受傷動作
					targetNpc.broadcastPacketX8(
							new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
					targetNpc.receiveDamage(pc, (int) damage);
				}
			}
		}

		return calcDamageReduction(pc, cha, damage, weaponSkill.getAttr());
	}

	public static double getBaphometStaffDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (14 >= chance) {
			final int locx = cha.getX();
			final int locy = cha.getY();
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = ((intel + sp) * (1.8 + bsk)) + (_random.nextInt(intel + sp) * 1.8);
			pc.sendPacketsAll(new S_EffectLocation(locx, locy, 129));
		}
		return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_EARTH);
	}

	public static double getDiceDaggerDamage(final L1PcInstance pc, final L1PcInstance targetPc,
			final L1ItemInstance weapon) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (3 >= chance) {
			dmg = (targetPc.getCurrentHp() * 2) / 3;
			if ((targetPc.getCurrentHp() - dmg) < 0) {
				dmg = 0;
			}
			final String msg = weapon.getLogName();
			pc.sendPackets(new S_ServerMessage(158, msg));
			// \f1%0が蒸発してなくなりました。
			pc.getInventory().removeItem(weapon, 1);
		}
		return dmg;
	}

	/**
	 * 底比斯武器魔法的效果
	 * 
	 * @param pc
	 * @param targetPc
	 * @param weapon
	 * @return
	 */
	public static double getChaserDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (4 >= chance) {
			dmg = 4.0;
			pc.sendPacketsAll(new S_EffectLocation(cha.getX(), cha.getY(), 7025));
		}
		return dmg;
	}

	/**
	 * 奇古獸傷害計算
	 * 
	 * @param pc
	 * @param cha
	 * @return
	 */
	public static double getKiringkuDamage(final L1PcInstance pc, final L1Character cha) {
		int dmg = 0;
		final int dice = 5;
		final int diceCount = 2;
		final int value = 14;
		int kiringkuDamage = 0;
		int charaIntelligence = 0;
		// final int getTargetMr = 0;
		// XXX ダイスと値は本来、キーリンク毎に違うが不明な為、判明しているDSKのものに固定。

		for (int i = 0; i < diceCount; i++) {
			kiringkuDamage += (_random.nextInt(dice) + 1);
		}
//		kiringkuDamage += value;

		final int spByItem = pc.getSp() - pc.getTrueSp();
		charaIntelligence = (pc.getInt() + spByItem) - 12;
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}
//		final double kiringkuCoefficientA = (1.0 + ((charaIntelligence * 3.0) / 32.0));
		final double kiringkuCoefficientA = (1.0 + ((charaIntelligence * 0.5) / 32.0));

		kiringkuDamage *= kiringkuCoefficientA;

		final double kiringkuFloor = Math.floor(kiringkuDamage);

		dmg += kiringkuFloor + pc.getWeapon().getEnchantLevel();

		switch (pc.getWeapon().getItem().getGfxId()) {
		case 3018:// 藍寶石奇古獸
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 6983));
			break;

		default:
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 7049));
			break;
		}

		return calcDamageReduction(pc, cha, dmg, 0);
	}

	public static double getAreaSkillWeaponDamage(final L1PcInstance pc, final L1Character cha,
			final int weaponId) {
		double dmg = 0;
		int probability = 0;
		int attr = 0;
		final int chance = _random.nextInt(100) + 1;
		if (weaponId == 263) { // フリージングランサー
			probability = 5;
			attr = L1Skills.ATTR_WATER;
		} else if (weaponId == 260) { // レイジングウィンド
			probability = 4;
			attr = L1Skills.ATTR_WIND;
		}
		if (probability >= chance) {
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			int area = 0;
			int effectTargetId = 0;
			int effectId = 0;
			L1Character areaBase = cha;
			double damageRate = 0;

			if (weaponId == 263) { // フリージングランサー
				area = 3;
				damageRate = 1.4D;
				effectTargetId = cha.getId();
				effectId = 1804;
				areaBase = cha;

			} else if (weaponId == 260) { // レイジングウィンド
				area = 4;
				damageRate = 1.5D;
				effectTargetId = pc.getId();
				effectId = 758;
				areaBase = pc;
			}
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = ((intel + sp) * (damageRate + bsk)) + (_random.nextInt(intel + sp) * damageRate);
			pc.sendPacketsX8(new S_SkillSound(effectTargetId, effectId));

			for (final L1Object object : World.get().getVisibleObjects(areaBase, area)) {
				if (object == null) {
					continue;
				}
				if (!(object instanceof L1Character)) {
					continue;
				}
				if (object.getId() == pc.getId()) {
					continue;
				}
				if (object.getId() == cha.getId()) { // 攻撃対象は除外
					continue;
				}

				// 攻撃対象がMOBの場合は、範囲内のMOBにのみ当たる
				// 攻撃対象がPC,Summon,Petの場合は、範囲内のPC,Summon,Pet,MOBに当たる
				if (cha instanceof L1MonsterInstance) {
					if (!(object instanceof L1MonsterInstance)) {
						continue;
					}
				}
				if ((cha instanceof L1PcInstance) || (cha instanceof L1SummonInstance)
						|| (cha instanceof L1PetInstance)) {
					if (!((object instanceof L1PcInstance) || (object instanceof L1SummonInstance)
							|| (object instanceof L1PetInstance) || (object instanceof L1MonsterInstance))) {
						continue;
					}
				}

				dmg = calcDamageReduction(pc, (L1Character) object, dmg, attr);
				if (dmg <= 0) {
					continue;
				}
				if (object instanceof L1PcInstance) {
					final L1PcInstance targetPc = (L1PcInstance) object;
					// 受傷動作
					targetPc.sendPacketsX8(new S_DoActionGFX(targetPc.getId(), ActionCodes.ACTION_Damage));

					targetPc.receiveDamage(pc, (int) dmg, false, false);

				} else if ((object instanceof L1SummonInstance) || (object instanceof L1PetInstance)
						|| (object instanceof L1MonsterInstance)) {
					final L1NpcInstance targetNpc = (L1NpcInstance) object;
					// 受傷動作
					targetNpc.broadcastPacketX8(
							new S_DoActionGFX(targetNpc.getId(), ActionCodes.ACTION_Damage));
					targetNpc.receiveDamage(pc, (int) dmg);
				}
			}
		}
		return calcDamageReduction(pc, cha, dmg, attr);
	}

	public static double getLightningEdgeDamage(final L1PcInstance pc, final L1Character cha) {
		double dmg = 0;
		final int chance = _random.nextInt(100) + 1;
		if (4 >= chance) {
			final int sp = pc.getSp();
			final int intel = pc.getInt();
			double bsk = 0;
			if (pc.hasSkillEffect(BERSERKERS)) {
				bsk = 0.2;
			}
			dmg = ((intel + sp) * (2 + bsk)) + (_random.nextInt(intel + sp) * 2);

			pc.sendPacketsX8(new S_SkillSound(cha.getId(), 10));
		}
		return calcDamageReduction(pc, cha, dmg, L1Skills.ATTR_WIND);
	}

	public static void giveArkMageDiseaseEffect(final L1PcInstance pc, final L1Character cha) {
		final int chance = _random.nextInt(1000) + 1;
		int probability = (5 - ((cha.getMr() / 10) * 5)) * 10;
		if (probability == 0) {
			probability = 10;
		}
		if (probability >= chance) {
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, L1SkillId.DISEASE, cha.getId(), cha.getX(), cha.getY(), 0,
					L1SkillUse.TYPE_GMBUFF);
		}
	}

	/**
	 * 深紅之弩凍結
	 * 
	 * @param pc
	 * @param cha
	 */
	public static void giveFettersEffect(final L1PcInstance pc, final L1Character cha) {
		final int fettersTime = 8;
		if (isFreeze(cha)) { // 凍結状態orカウンターマジック中
			return;
		}
		if ((_random.nextInt(100) + 1) <= 2) {
			L1SpawnUtil.spawnEffect(81182, fettersTime, cha.getX(), cha.getY(), cha.getMapId(), cha, 0);
			if (cha instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) cha;
				targetPc.setSkillEffect(MOVE_STOP, fettersTime * 1000);
				targetPc.sendPacketsX8(new S_SkillSound(targetPc.getId(), 4184));

				targetPc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));

			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance)
					|| (cha instanceof L1PetInstance)) {
				final L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setSkillEffect(MOVE_STOP, fettersTime * 1000);
				npc.broadcastPacketX8(new S_SkillSound(npc.getId(), 4184));
				npc.setParalyzed(true);
			}
		}
	}

	public static double calcDamageReduction(final L1PcInstance pc, final L1Character cha, double dmg,
			final int attr) {
		// 凍結状態orカウンターマジック中
		if (isFreeze(cha)) {
			return 0;
		}

		// MRによるダメージ軽減
		final int mr = cha.getMr();
		double mrFloor = 0;
		if (mr <= 100) {
			mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 2);
		} else if (mr >= 100) {
			mrFloor = Math.floor((mr - pc.getOriginalMagicHit()) / 10);
		}
		double mrCoefficient = 0;
		if (mr <= 100) {
			mrCoefficient = 1 - (0.01 * mrFloor);
		} else if (mr >= 100) {
			mrCoefficient = 0.6 - (0.01 * mrFloor);
		}
		dmg *= mrCoefficient;

		// 属性によるダメージ軽減
		int resist = 0;
		if (attr == L1Skills.ATTR_EARTH) {
			resist = cha.getEarth();
		} else if (attr == L1Skills.ATTR_FIRE) {
			resist = cha.getFire();
		} else if (attr == L1Skills.ATTR_WATER) {
			resist = cha.getWater();
		} else if (attr == L1Skills.ATTR_WIND) {
			resist = cha.getWind();
		}
		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}
		final double attrDeffence = resistFloor / 32.0;
		dmg = (1.0 - attrDeffence) * dmg;

		return dmg;
	}

	/**
	 * 凍結中
	 * 
	 * @param cha
	 * @return
	 */
	public static boolean isFreeze(final L1Character cha) {
		if (cha.hasSkillEffect(STATUS_FREEZE)) {
			return true;
		}
		if (cha.hasSkillEffect(ABSOLUTE_BARRIER)) {
			return true;
		}
		if (cha.hasSkillEffect(ICE_LANCE)) {
			return true;
		}
		if (cha.hasSkillEffect(FREEZING_BREATH)) {
			return true;
		}
		if (cha.hasSkillEffect(EARTH_BIND)) {
			return true;
		}
		if (cha.hasSkillEffect(MOVE_STOP)) {
			return true;
		}

		// カウンターマジック判定
		if (cha.hasSkillEffect(COUNTER_MAGIC)) {
			cha.removeSkillEffect(COUNTER_MAGIC);
			final int castgfx = SkillsTable.get().getTemplate(COUNTER_MAGIC).getCastGfx2();
			cha.broadcastPacketX8(new S_SkillSound(cha.getId(), castgfx));
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		}
		return false;
	}
}
