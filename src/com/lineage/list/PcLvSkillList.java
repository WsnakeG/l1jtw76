package com.lineage.list;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.ArrayList;

import com.lineage.server.model.Instance.L1PcInstance;

public class PcLvSkillList {

	/**
	 * 一般魔法老師(限制教學到3級魔法)
	 * 
	 * @param pc
	 * @return
	 */
	public static ArrayList<Integer> scount(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		switch (pc.getType()) {
		case 0: // 王族
			if (pc.getLevel() >= 10) {
				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}

			}
			if (pc.getLevel() >= 20) {
				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;

		case 1: // 騎士
			if (pc.getLevel() >= 50) {
				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;

		case 2: // 精靈
			if (pc.getLevel() >= 8) {
				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			if (pc.getLevel() >= 16) {
				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			if (pc.getLevel() >= 24) {
				for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;

		case 3: // 法師
			if (pc.getLevel() >= 4) {
				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			if (pc.getLevel() >= 8) {
				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			if (pc.getLevel() >= 12) {
				for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;

		case 4: // 黑妖
			if (pc.getLevel() >= 12) {
				for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			if (pc.getLevel() >= 24) {
				for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;

		case 7: // 戰士
			if (pc.getLevel() >= 50) {
				for (int skillid = HEAL - 1; skillid < HOLY_WEAPON; skillid++) {
					skillList.add(new Integer(skillid));
				}
			}
			break;
		}
		return skillList;
	}

	/**
	 * 幻術
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isIllusionist(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 10) {
			for (int skillid = (MIRROR_IMAGE - 1); skillid < BONE_BREAK; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 20) {
			for (int skillid = (ILLUSION_LICH - 1); skillid < PHANTASM; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 30) {
			for (int skillid = (ARM_BREAKER - 1); skillid < INSIGHT; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 40) {
			for (int skillid = (PANIC - 1); skillid < CUBE_BALANCE; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		return skillList;
	}

	/**
	 * 龍騎
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isDragonKnight(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 15) {
			for (int skillid = (DRAGON_SKIN - 1); skillid < MAGMA_BREATH; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 30) {
			for (int skillid = (AWAKEN_ANTHARAS - 1); skillid < THUNDER_GRAB; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 45) {
			for (int skillid = (HORROR_OF_DEATH - 1); skillid < AWAKEN_VALAKAS; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		return skillList;
	}

	/**
	 * 黑妖
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isDarkelf(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 12) {
			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 24) {
			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 15) {
			for (int skillid = (BLIND_HIDING - 1); skillid < BRING_STONE; skillid++) {
				skillList.add(new Integer(skillid));
			}
			skillList.add(new Integer(DRESS_MIGHTY - 1));
		}
		if (pc.getLevel() >= 30) {
			for (int skillid = (MOVING_ACCELERATION - 1); skillid < VENOM_RESIST; skillid++) {
				skillList.add(new Integer(skillid));
			}
			skillList.add(new Integer(DRESS_DEXTERITY - 1));
		}
		if (pc.getLevel() >= 45) {
			for (int skillid = (DOUBLE_BRAKE - 1); skillid < FINAL_BURN; skillid++) {
				skillList.add(new Integer(skillid));
			}
			skillList.add(new Integer(DRESS_EVASION - 1));
		}
		return skillList;
	}

	/**
	 * 法師
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isWizard(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 4) {
			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 8) {
			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 12) {
			for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 16) {
			for (int skillid = (FIREBALL - 1); skillid < MEDITATION; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 20) {
			for (int skillid = (CURSE_PARALYZE - 1); skillid < DARKNESS; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 24) {
			for (int skillid = (CREATE_ZOMBIE - 1); skillid < BLESS_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 28) {
			for (int skillid = (HEAL_ALL - 1); skillid < DISEASE; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 32) {
			for (int skillid = (FULL_HEAL - 1); skillid < SILENCE; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 36) {
			for (int skillid = (LIGHTNING_STORM - 1); skillid < COUNTER_DETECTION; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 40) {
			for (int skillid = (CREATE_MAGICAL_WEAPON - 1); skillid < ICE_SPIKE; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		return skillList;
	}

	/**
	 * 精靈
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isElf(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 8) {
			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 16) {
			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 24) {
			for (int skillid = (LIGHTNING - 1); skillid < WEAK_ELEMENTAL; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 32) {
			for (int skillid = (FIREBALL - 1); skillid < MEDITATION; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 40) {
			for (int skillid = (CURSE_PARALYZE - 1); skillid < DARKNESS; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 48) {
			for (int skillid = (CREATE_ZOMBIE - 1); skillid < BLESS_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}

		if (pc.getLevel() >= 10) {
			skillList.add(new Integer(RESIST_MAGIC - 1));
			skillList.add(new Integer(BODY_TO_MIND - 1));
			skillList.add(new Integer(TELEPORT_TO_MATHER - 1));
		}
		if (pc.getLevel() >= 20) {
			skillList.add(new Integer(CLEAR_MIND - 1));
			skillList.add(new Integer(RESIST_ELEMENTAL - 1));
		}
		if (pc.getLevel() >= 30) {
			skillList.add(new Integer(TRIPLE_ARROW - 1));
			skillList.add(new Integer(RETURN_TO_NATURE - 1));
			skillList.add(new Integer(BLOODY_SOUL - 1));
			skillList.add(new Integer(ELEMENTAL_PROTECTION - 1));

			switch (pc.getElfAttr()) {
			case 1:// 地属性
				skillList.add(new Integer(EARTH_SKIN - 1));
				skillList.add(new Integer(ENTANGLE - 1));
				break;

			case 2:// 火属性
				skillList.add(new Integer(FIRE_WEAPON - 1));
				break;

			case 4:// 水属性
				skillList.add(new Integer(WATER_LIFE - 1));
				break;

			case 8:// 風属性
				skillList.add(new Integer(WIND_SHOT - 1));
				skillList.add(new Integer(WIND_WALK - 1));
				break;
			}
		}
		if (pc.getLevel() >= 40) {
			skillList.add(new Integer(ELEMENTAL_FALL_DOWN - 1));
			skillList.add(new Integer(ERASE_MAGIC - 1));
			skillList.add(new Integer(LESSER_ELEMENTAL - 1));

			switch (pc.getElfAttr()) {
			case 1:// 地属性
				skillList.add(new Integer(EARTH_BIND - 1));
				skillList.add(new Integer(EARTH_GUARDIAN - 1));
				break;

			case 2:// 火属性
				skillList.add(new Integer(DANCING_BLAZE - 1));
				break;

			case 4:// 水属性
				skillList.add(new Integer(NATURES_TOUCH - 1));
				skillList.add(new Integer(AQUA_PROTECTER - 1));
				break;

			case 8:// 風属性
				skillList.add(new Integer(STORM_EYE - 1));
				break;
			}
		}
		if (pc.getLevel() >= 50) {
			skillList.add(new Integer(COUNTER_MIRROR - 1));
			skillList.add(new Integer(AREA_OF_SILENCE - 1));
			skillList.add(new Integer(GREATER_ELEMENTAL - 1));

			switch (pc.getElfAttr()) {
			case 1:// 地属性
				skillList.add(new Integer(IRON_SKIN - 1));
				skillList.add(new Integer(EXOTIC_VITALIZE - 1));
				break;

			case 2:// 火属性
				skillList.add(new Integer(BURNING_WEAPON - 1));
				skillList.add(new Integer(ELEMENTAL_FIRE - 1));
				skillList.add(new Integer(SOUL_OF_FLAME - 1));
				skillList.add(new Integer(ADDITIONAL_FIRE - 1));
				break;

			case 4:// 水属性
				skillList.add(new Integer(NATURES_BLESSING - 1));
				skillList.add(new Integer(CALL_OF_NATURE - 1));
				skillList.add(new Integer(POLLUTE_WATER - 1));
				break;

			case 8:// 風属性
				skillList.add(new Integer(STORM_SHOT - 1));
				skillList.add(new Integer(WIND_SHACKLE - 1));
				skillList.add(new Integer(STRIKER_GALE - 1));
				break;
			}
		}
		return skillList;
	}

	/**
	 * 騎士
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isKnight(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 50) {// magic lv1
			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 50) {// Knight magic lv1
			skillList.add(new Integer(SHOCK_STUN - 1));
			skillList.add(new Integer(REDUCTION_ARMOR - 1));
			skillList.add(new Integer(SOLID_CARRIAGE - 1));
			skillList.add(new Integer(COUNTER_BARRIER - 1));
		}
		if (pc.getLevel() >= 60) {// Knight magic lv2
			skillList.add(new Integer(SOLID_CARRIAGE - 1));

		}
		return skillList;
	}

	/**
	 * 王族
	 * 
	 * @param pc
	 */
	public static ArrayList<Integer> isCrown(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 10) {// magic lv1
			for (int skillid = (HEAL - 1); skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}

		}
		if (pc.getLevel() >= 20) {// magic lv2
			for (int skillid = (CURE_POISON - 1); skillid < STALAC; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 15) {// Crown magic lv1
			skillList.add(new Integer(TRUE_TARGET - 1));
		}
		if (pc.getLevel() >= 30) {// Crown magic lv2
			skillList.add(new Integer(CALL_CLAN - 1));
		}
		if (pc.getLevel() >= 40) {// Crown magic lv3
			skillList.add(new Integer(GLOWING_WEAPON - 1));
		}
		if (pc.getLevel() >= 45) {// Crown magic lv4
			skillList.add(new Integer(RUN_CLAN - 1));
		}
		if (pc.getLevel() >= 50) {// Crown magic lv5
			skillList.add(new Integer(BRAVE_MENTAL - 1));
		}
		if (pc.getLevel() >= 55) {// Crown magic lv6
			skillList.add(new Integer(SHINING_AURA - 1));
		}
		return skillList;
	}

	/**
	 * 戰士
	 * 
	 * @param pc
	 * @return
	 */
	public static ArrayList<Integer> isWarrior(final L1PcInstance pc) {
		final ArrayList<Integer> skillList = new ArrayList<Integer>();
		if (pc.getLevel() >= 50) {// magic lv1
			for (int skillid = HEAL - 1; skillid < HOLY_WEAPON; skillid++) {
				skillList.add(new Integer(skillid));
			}
		}
		if (pc.getLevel() >= 15) {
			skillList.add(new Integer(PASSIVE_SLAYER - 1));
		}
		if (pc.getLevel() >= 30) {
			skillList.add(new Integer(HOWL - 1));
		}
		if (pc.getLevel() >= 45) {
			skillList.add(new Integer(PASSIVE_CRASH - 1));
			skillList.add(new Integer(TOMAHAWK - 1));
		}
		if (pc.getLevel() >= 50) {
			skillList.add(new Integer(PASSIVE_ARMORGARDE - 1));
		}
		if (pc.getLevel() >= 55) {
			skillList.add(new Integer(GIGANTIC - 1));
		}
		if (pc.getLevel() >= 60) {
			skillList.add(new Integer(DESPERADO - 1));
			skillList.add(new Integer(PASSIVE_FURY - 1));
			skillList.add(new Integer(PASSIVE_TITANROCK - 1));
		}
		if (pc.getLevel() >= 70) {
			skillList.add(new Integer(POWERGRIP - 1));
		}
		if (pc.getLevel() >= 75) {
			skillList.add(new Integer(PASSIVE_TITANMAGIC - 1));
		}
		if (pc.getLevel() >= 82) {
			skillList.add(new Integer(PASSIVE_TITANBULLET - 1));
		}
		return skillList;
	}

}
