package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static com.lineage.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static com.lineage.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static com.lineage.server.model.skill.L1SkillId.BLOODLUST;
import static com.lineage.server.model.skill.L1SkillId.BURNING_SLASH;
import static com.lineage.server.model.skill.L1SkillId.DRAGON_SKIN;
import static com.lineage.server.model.skill.L1SkillId.FOE_SLAYER;
import static com.lineage.server.model.skill.L1SkillId.FREEZING_BREATH;
import static com.lineage.server.model.skill.L1SkillId.GUARD_BRAKE;
import static com.lineage.server.model.skill.L1SkillId.HORROR_OF_DEATH;
import static com.lineage.server.model.skill.L1SkillId.MAGMA_BREATH;
import static com.lineage.server.model.skill.L1SkillId.MORTAL_BODY;
import static com.lineage.server.model.skill.L1SkillId.RESIST_FEAR;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_SKIN;
import static com.lineage.server.model.skill.L1SkillId.THUNDER_GRAB;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>龍騎士書板(龍騎士魔法)</font><BR>
 * Dark Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_DragonKnigh extends ItemExecutor {

	/**
	 *
	 */
	private Skill_DragonKnigh() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_DragonKnigh();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc, final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		// 不是龍騎士
		if (!pc.isDragonKnight()) {
			// 79 沒有任何事情發生
			final S_ServerMessage msg = new S_ServerMessage(79);
			pc.sendPackets(msg);

		} else {
			// 取得名稱
			final String nameId = item.getItem().getNameId();
			// 技能編號
			int skillid = 0;
			// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
			// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
			// 技能屬性 7:龍騎士專屬魔法 8:幻術師專屬魔法
			final int attribute = 6;
			// 分組
			int magicLv = 0;
			/*
			 * 龍騎士書板(龍之護鎧) $5778 龍騎士書板(燃燒擊砍) $5701 龍騎士書板(護衛毀滅) $5702 龍騎士書板(岩漿噴吐)
			 * $5703 龍騎士書板(覺醒：安塔瑞斯) $5704 龍騎士書板(血之渴望) $5705 龍騎士書板(屠宰者) $5706
			 * 龍騎士書板(恐懼無助) $5707 龍騎士書板(衝擊之膚) $5708 龍騎士書板(覺醒：法利昂) $5709
			 * 龍騎士書板(致命身軀) $5710 龍騎士書板(奪命之雷) $5711 龍騎士書板(驚悚死神) $5712 龍騎士書板(寒冰噴吐)
			 * $5713 龍騎士書板(覺醒：巴拉卡斯) $5714
			 */
			// TODO 1
			if (nameId.equalsIgnoreCase("$5778")) {// 龍騎士書板(龍之護鎧)
				// 技能編號
				skillid = DRAGON_SKIN;
				// 分組
				magicLv = 51;

			} else if (nameId.equalsIgnoreCase("$5701")) {// 龍騎士書板(燃燒擊砍)
				// 技能編號
				skillid = BURNING_SLASH;
				// 分組
				magicLv = 51;

			} else if (nameId.equalsIgnoreCase("$5702")) {// 龍騎士書板(護衛毀滅)
				// 技能編號
				skillid = GUARD_BRAKE;
				// 分組
				magicLv = 51;

			} else if (nameId.equalsIgnoreCase("$5703")) {// 龍騎士書板(岩漿噴吐)
				// 技能編號
				skillid = MAGMA_BREATH;
				// 分組
				magicLv = 51;

				// TODO 2
			} else if (nameId.equalsIgnoreCase("$5704")) {// 龍騎士書板(覺醒：安塔瑞斯)
				// 技能編號
				skillid = AWAKEN_ANTHARAS;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5705")) {// 龍騎士書板(血之渴望)
				// 技能編號
				skillid = BLOODLUST;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5706")) {// 龍騎士書板(屠宰者)
				// 技能編號
				skillid = FOE_SLAYER;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5707")) {// 龍騎士書板(恐懼無助)
				// 技能編號
				skillid = RESIST_FEAR;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5708")) {// 龍騎士書板(衝擊之膚)
				// 技能編號
				skillid = SHOCK_SKIN;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5709")) {// 龍騎士書板(覺醒：法利昂)
				// 技能編號
				skillid = AWAKEN_FAFURION;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5710")) {// 龍騎士書板(致命身軀)
				// 技能編號
				skillid = MORTAL_BODY;
				// 分組
				magicLv = 52;

			} else if (nameId.equalsIgnoreCase("$5711")) {// 龍騎士書板(奪命之雷)
				// 技能編號
				skillid = THUNDER_GRAB;
				// 分組
				magicLv = 52;

				// TODO 3
			} else if (nameId.equalsIgnoreCase("$5712")) {// 龍騎士書板(驚悚死神)
				// 技能編號
				skillid = HORROR_OF_DEATH;
				// 分組
				magicLv = 53;

			} else if (nameId.equalsIgnoreCase("$5713")) {// 龍騎士書板(寒冰噴吐)
				// 技能編號
				skillid = FREEZING_BREATH;
				// 分組
				magicLv = 53;

			} else if (nameId.equalsIgnoreCase("$5714")) {// 龍騎士書板(覺醒：巴拉卡斯)
				// 技能編號
				skillid = AWAKEN_VALAKAS;
				// 分組
				magicLv = 53;
			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
