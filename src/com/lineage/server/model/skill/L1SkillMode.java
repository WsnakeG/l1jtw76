package com.lineage.server.model.skill;

import static com.lineage.server.model.skill.L1SkillId.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lineage.server.model.skill.skillmode.*;

public class L1SkillMode {

	private static final Log _log = LogFactory.getLog(L1SkillMode.class);

	// Map<K,V>
	private static final Map<Integer, SkillMode> _skillMode = new HashMap<Integer, SkillMode>();

	private static L1SkillMode _instance;

	public static L1SkillMode get() {
		if (_instance == null) {
			_instance = new L1SkillMode();
		}
		return _instance;
	}

	/**
	 * 不會被相消的技能 repaired by terry0412
	 */
	public boolean isNotCancelable(final int skillNum) {
		return (skillNum == ENCHANT_WEAPON) || (skillNum == BLESSED_ARMOR) || (skillNum == ABSOLUTE_BARRIER)
				|| (skillNum == ADVANCE_SPIRIT) || (skillNum == SHOCK_STUN) || (skillNum == SHADOW_FANG)
				|| (skillNum == REDUCTION_ARMOR) || (skillNum == SOLID_CARRIAGE)
				|| (skillNum == COUNTER_BARRIER) || (skillNum == AWAKEN_ANTHARAS)
				|| (skillNum == AWAKEN_FAFURION) || (skillNum == AWAKEN_VALAKAS)
				|| (skillNum == STATUS_CHAT_PROHIBITED) || (skillNum == STATUS_CURSE_BARLOG)
				|| (skillNum == STATUS_CURSE_YAHEE) || (skillNum == CKEW_LV50) || (skillNum == DE_LV30)
				// added by terry0412
				|| (skillNum == SHADOW_ARMOR) // 影之防護
				|| (skillNum == UNCANNY_DODGE) // 暗影閃避
				|| (skillNum == DRESS_EVASION) // 迴避提升
				|| (skillNum == ARMOR_BREAK); // 破壞盔甲
	}

	public void load() {
		try {
			// 法師
			_skillMode.put(TELEPORT, new TELEPORT());// 指定傳送
			_skillMode.put(MASS_TELEPORT, new MASS_TELEPORT());// 集體傳送術
			_skillMode.put(HASTE, new HASTE());// 加速術
			_skillMode.put(CANCELLATION, new CANCELLATION());// 魔法相消術
			_skillMode.put(CURE_POISON, new CURE_POISON());// 解毒術
			_skillMode.put(REMOVE_CURSE, new REMOVE_CURSE());// 聖潔之光
			_skillMode.put(SUMMON_MONSTER, new SUMMON_MONSTER());// 召喚術
			_skillMode.put(RESURRECTION, new RESURRECTION());// 返生術
			_skillMode.put(GREATER_RESURRECTION, new GREATER_RESURRECTION());// 終極返生術
			_skillMode.put(ADVANCE_SPIRIT, new ADVANCE_SPIRIT());// 靈魂昇華
			_skillMode.put(CURSE_PARALYZE, new CURSE_PARALYZE());// 木乃伊的詛咒
			_skillMode.put(CURSE_PARALYZE2, new CURSE_PARALYZE());// 魔法效果:麻痺
			_skillMode.put(CURSE_BLIND, new CURSE_BLIND());// 闇盲咒術20
			_skillMode.put(DARKNESS, new CURSE_BLIND());// 黑闇之影40
			_skillMode.put(DECAY_POTION, new DECAY_POTION());// 藥水霜化術71
			_skillMode.put(ENCHANT_WEAPON, new ENCHANT_WEAPON());
			_skillMode.put(HOLY_WEAPON, new HOLY_WEAPON());
			_skillMode.put(BLESS_WEAPON, new BLESS_WEAPON());
			_skillMode.put(BLESSED_ARMOR, new BLESSED_ARMOR());

			// 王族
			_skillMode.put(CALL_CLAN, new CALL_CLAN());// 呼喚盟友
			_skillMode.put(RUN_CLAN, new RUN_CLAN());// 援護盟友

			// 騎士
			_skillMode.put(SHOCK_STUN, new SHOCK_STUN());// 衝擊之暈
			_skillMode.put(SOLID_CARRIAGE, new SOLID_CARRIAGE());// 堅固防護
			_skillMode.put(BOUNCE_ATTACK, new BOUNCE_ATTACK());// 尖刺盔甲

			// 精靈
			_skillMode.put(CALL_OF_NATURE, new CALL_OF_NATURE());// 生命呼喚
			_skillMode.put(ELEMENTAL_FALL_DOWN, new ELEMENTAL_FALL_DOWN());// 弱化屬性
			_skillMode.put(BODY_TO_MIND, new BODY_TO_MIND());// 心靈轉換
			_skillMode.put(BLOODY_SOUL, new BLOODY_SOUL());// 魂體轉換
			_skillMode.put(TRIPLE_ARROW, new TRIPLE_ARROW());// 三重矢
			_skillMode.put(TELEPORT_TO_MATHER, new TELEPORT_TO_MATHER());// 世界樹的呼喚
			_skillMode.put(AQUA_PROTECTER, new AQUA_PROTECTER());// 水之防護
			_skillMode.put(GREATER_ELEMENTAL, new GREATER_ELEMENTAL());// 召喚強力屬性精靈
			_skillMode.put(LESSER_ELEMENTAL, new LESSER_ELEMENTAL());// 召喚屬性精靈
			_skillMode.put(WIND_SHACKLE, new WIND_SHACKLE());// 風之枷鎖

			// 黑暗精靈
			_skillMode.put(UNCANNY_DODGE, new UNCANNY_DODGE());// 暗影閃避106
			_skillMode.put(DARK_BLIND, new PHANTASM());// 暗黑盲咒103
			_skillMode.put(SHADOW_FANG, new SHADOW_FANG());

			// 龍騎士
			_skillMode.put(AWAKEN_ANTHARAS, new AWAKEN_ANTHARAS());// 覺醒：安塔瑞斯
			_skillMode.put(AWAKEN_FAFURION, new AWAKEN_FAFURION());// 覺醒：法利昂
			_skillMode.put(AWAKEN_VALAKAS, new AWAKEN_VALAKAS());// 覺醒：巴拉卡斯
			_skillMode.put(FOE_SLAYER, new FOE_SLAYER());// 屠宰者
			_skillMode.put(BLOODLUST, new BLOODLUST());// 血之渴望
			_skillMode.put(RESIST_FEAR, new RESIST_FEAR());// 恐懼無助188

			// 幻術師
			_skillMode.put(CONFUSION, new CONFUSION());// 混亂
			_skillMode.put(PHANTASM, new PHANTASM());// 幻想
			// _skillMode.put(ARM_BREAKER, new ARM_BREAKER());// 武器破壞者
			_skillMode.put(PANIC, new PANIC());// 恐慌
			_skillMode.put(INSIGHT, new INSIGHT());// 洞察
			_skillMode.put(BONE_BREAK, new BONE_BREAK());// 骷髏毀壞
			_skillMode.put(MIND_BREAK, new MIND_BREAK());// 心靈破壞
			_skillMode.put(ILLUSION_AVATAR, new ILLUSION_AVATAR());// 幻覺：化身
			_skillMode.put(ILLUSION_LICH, new ILLUSION_LICH());// 幻覺：巫妖
			_skillMode.put(MIRROR_IMAGE, new MIRROR_IMAGE());// 镜像

			// 魔法效果
			_skillMode.put(STATUS_FREEZE, new STATUS_FREEZE());// 魔法效果:凍結
			_skillMode.put(MOVE_STOP, new MOVE_STOP());// 魔法效果:無法移動

			// 道具效果
			_skillMode.put(DRAGON1, new DRAGON1());// 火 額外攻擊點+2，持續1200秒
			_skillMode.put(DRAGON2, new DRAGON2());// 地 攻擊迴避提升 石化耐性+3，持續1200秒
			_skillMode.put(DRAGON3, new DRAGON3());// 水 魔法傷害減免 寒冰耐心+3，持續1200秒
			_skillMode.put(DRAGON4, new DRAGON4());// 風 魔法重擊增加 睡眠耐性+3，持續1200秒
			_skillMode.put(DRAGON5, new DRAGON5());// 生命-物理攻擊迴避率+10% 魔法傷害減免+50
													// 魔法暴擊率+1 額外攻擊點數+2 防護中毒狀態
			_skillMode.put(DRAGON6, new DRAGON6());// 誕生-物理攻擊迴避率+10% 魔法傷害減免+50
													// 暗黑耐性+3
			_skillMode.put(DRAGON7, new DRAGON7());// 形象-物理攻擊迴避率+10% 魔法傷害減免+50
													// 魔法暴擊率+1 支撐耐性+3

			// NPC 特殊效果
			_skillMode.put(ADLV80_1, new ADLV80_1());// 卡瑞的祝福(地龍副本)
			_skillMode.put(ADLV80_2, new ADLV80_2());// 莎爾的祝福(水龍副本)
			_skillMode.put(AGLV85_1X, new AGLV85_1X());// 莎爾的祝福(水龍副本-強化版)

			// 新林德拜爾-空中尖刺 by terry0412
			_skillMode.put(LINDVIOR_SKY_SPIKED, new LINDVIOR_SKY_SPIKED());

			// 龍印魔石(鬥士)
			_skillMode.put(DS_GX00, new DS_GX00());
			_skillMode.put(DS_GX01, new DS_GX01());
			_skillMode.put(DS_GX02, new DS_GX02());
			_skillMode.put(DS_GX03, new DS_GX03());
			_skillMode.put(DS_GX04, new DS_GX04());
			_skillMode.put(DS_GX05, new DS_GX05());
			_skillMode.put(DS_GX06, new DS_GX06());
			_skillMode.put(DS_GX07, new DS_GX07());
			_skillMode.put(DS_GX08, new DS_GX08());
			_skillMode.put(DS_GX09, new DS_GX09());

			// 龍印魔石(弓手)
			_skillMode.put(DS_AX00, new DS_AX00());
			_skillMode.put(DS_AX01, new DS_AX01());
			_skillMode.put(DS_AX02, new DS_AX02());
			_skillMode.put(DS_AX03, new DS_AX03());
			_skillMode.put(DS_AX04, new DS_AX04());
			_skillMode.put(DS_AX05, new DS_AX05());
			_skillMode.put(DS_AX06, new DS_AX06());
			_skillMode.put(DS_AX07, new DS_AX07());
			_skillMode.put(DS_AX08, new DS_AX08());
			_skillMode.put(DS_AX09, new DS_AX09());

			// 龍印魔石(賢者)
			_skillMode.put(DS_WX00, new DS_WX00());
			_skillMode.put(DS_WX01, new DS_WX01());
			_skillMode.put(DS_WX02, new DS_WX02());
			_skillMode.put(DS_WX03, new DS_WX03());
			_skillMode.put(DS_WX04, new DS_WX04());
			_skillMode.put(DS_WX05, new DS_WX05());
			_skillMode.put(DS_WX06, new DS_WX06());
			_skillMode.put(DS_WX07, new DS_WX07());
			_skillMode.put(DS_WX08, new DS_WX08());
			_skillMode.put(DS_WX09, new DS_WX09());

			// 龍印魔石(衝鋒)
			_skillMode.put(DS_ASX00, new DS_ASX00());
			_skillMode.put(DS_ASX01, new DS_ASX01());
			_skillMode.put(DS_ASX02, new DS_ASX02());
			_skillMode.put(DS_ASX03, new DS_ASX03());
			_skillMode.put(DS_ASX04, new DS_ASX04());
			_skillMode.put(DS_ASX05, new DS_ASX05());
			_skillMode.put(DS_ASX06, new DS_ASX06());
			_skillMode.put(DS_ASX07, new DS_ASX07());
			_skillMode.put(DS_ASX08, new DS_ASX08());
			_skillMode.put(DS_ASX09, new DS_ASX09());

			// 附魔石(近戰)
			_skillMode.put(BS_GX01, new BS_GX01());
			_skillMode.put(BS_GX02, new BS_GX02());
			_skillMode.put(BS_GX03, new BS_GX03());
			_skillMode.put(BS_GX04, new BS_GX04());
			_skillMode.put(BS_GX05, new BS_GX05());
			_skillMode.put(BS_GX06, new BS_GX06());
			_skillMode.put(BS_GX07, new BS_GX07());
			_skillMode.put(BS_GX08, new BS_GX08());
			_skillMode.put(BS_GX09, new BS_GX09());

			// 附魔石(遠攻)
			_skillMode.put(BS_AX01, new BS_AX01());
			_skillMode.put(BS_AX02, new BS_AX02());
			_skillMode.put(BS_AX03, new BS_AX03());
			_skillMode.put(BS_AX04, new BS_AX04());
			_skillMode.put(BS_AX05, new BS_AX05());
			_skillMode.put(BS_AX06, new BS_AX06());
			_skillMode.put(BS_AX07, new BS_AX07());
			_skillMode.put(BS_AX08, new BS_AX08());
			_skillMode.put(BS_AX09, new BS_AX09());

			// 附魔石(恢復)
			_skillMode.put(BS_WX01, new BS_WX01());
			_skillMode.put(BS_WX02, new BS_WX02());
			_skillMode.put(BS_WX03, new BS_WX03());
			_skillMode.put(BS_WX04, new BS_WX04());
			_skillMode.put(BS_WX05, new BS_WX05());
			_skillMode.put(BS_WX06, new BS_WX06());
			_skillMode.put(BS_WX07, new BS_WX07());
			_skillMode.put(BS_WX08, new BS_WX08());
			_skillMode.put(BS_WX09, new BS_WX09());

			// 附魔石(防禦)
			_skillMode.put(BS_ASX01, new BS_ASX01());
			_skillMode.put(BS_ASX02, new BS_ASX02());
			_skillMode.put(BS_ASX03, new BS_ASX03());
			_skillMode.put(BS_ASX04, new BS_ASX04());
			_skillMode.put(BS_ASX05, new BS_ASX05());
			_skillMode.put(BS_ASX06, new BS_ASX06());
			_skillMode.put(BS_ASX07, new BS_ASX07());
			_skillMode.put(BS_ASX08, new BS_ASX08());
			_skillMode.put(BS_ASX09, new BS_ASX09());

			// 戰士
			_skillMode.put(POWERGRIP, new POWERGRIP());
			_skillMode.put(DESPERADO, new DESPERADO());
			_skillMode.put(TOMAHAWK, new TOMAHAWK());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		}
	}

	public SkillMode getSkill(final int skillid) {
		return _skillMode.get(skillid);
	}
}
