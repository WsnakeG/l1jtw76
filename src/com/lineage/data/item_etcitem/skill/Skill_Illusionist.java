package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.ARM_BREAKER;
import static com.lineage.server.model.skill.L1SkillId.BONE_BREAK;
import static com.lineage.server.model.skill.L1SkillId.CONCENTRATION;
import static com.lineage.server.model.skill.L1SkillId.CONFUSION;
import static com.lineage.server.model.skill.L1SkillId.CUBE_BALANCE;
import static com.lineage.server.model.skill.L1SkillId.CUBE_IGNITION;
import static com.lineage.server.model.skill.L1SkillId.CUBE_QUAKE;
import static com.lineage.server.model.skill.L1SkillId.CUBE_SHOCK;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_AVATAR;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_DIA_GOLEM;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_LICH;
import static com.lineage.server.model.skill.L1SkillId.ILLUSION_OGRE;
import static com.lineage.server.model.skill.L1SkillId.INSIGHT;
import static com.lineage.server.model.skill.L1SkillId.MIND_BREAK;
import static com.lineage.server.model.skill.L1SkillId.MIRROR_IMAGE;
import static com.lineage.server.model.skill.L1SkillId.PANIC;
import static com.lineage.server.model.skill.L1SkillId.PATIENCE;
import static com.lineage.server.model.skill.L1SkillId.PHANTASM;
import static com.lineage.server.model.skill.L1SkillId.REDUCE_WEIGHT;
import static com.lineage.server.model.skill.L1SkillId.SMASH;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>記憶水晶(幻術師魔法)</font><BR>
 * Dark Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_Illusionist extends ItemExecutor {

	/**
	 *
	 */
	private Skill_Illusionist() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_Illusionist();
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
		// 不是幻術師
		if (!pc.isIllusionist()) {
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
			 * 記憶水晶(鏡像) $5681 記憶水晶(混亂) $5682 記憶水晶(暴擊) $5683 記憶水晶(幻覺：歐吉) $5684
			 * 記憶水晶(立方：燃燒) $5685 記憶水晶(專注) $5686 記憶水晶(心靈破壞 ) $5687 記憶水晶(骷髏毀壞)
			 * $5688 記憶水晶(幻覺：巫妖) $5689 記憶水晶(立方：地裂) $5690 記憶水晶(耐力) $5691 記憶水晶(幻想)
			 * $5692 記憶水晶(武器破壞者) $5693 記憶水晶(幻覺：鑽石高崙) $5694 記憶水晶(立方：衝擊) $5695
			 * 記憶水晶(洞察) $5696 記憶水晶(恐慌) $5697 記憶水晶(疼痛的歡愉) $5698 記憶水晶(幻覺：化身) $5699
			 * 記憶水晶(立方：和諧) $5700
			 */
			// TODO 1
			if (nameId.equalsIgnoreCase("$5681")) {// 記憶水晶(鏡像)
				// 技能編號
				skillid = MIRROR_IMAGE;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5682")) {// 記憶水晶(混亂)
				// 技能編號
				skillid = CONFUSION;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5683")) {// 記憶水晶(暴擊)
				// 技能編號
				skillid = SMASH;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5684")) {// 記憶水晶(幻覺：歐吉)
				// 技能編號
				skillid = ILLUSION_OGRE;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5685")) {// 記憶水晶(立方：燃燒)
				// 技能編號
				skillid = CUBE_IGNITION;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5686")) {// 記憶水晶(專注)
				// 技能編號
				skillid = CONCENTRATION;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5687")) {// 記憶水晶(心靈破壞 )
				// 技能編號
				skillid = MIND_BREAK;
				// 分組
				magicLv = 61;

			} else if (nameId.equalsIgnoreCase("$5688")) {// 記憶水晶(骷髏毀壞)
				// 技能編號
				skillid = BONE_BREAK;
				// 分組
				magicLv = 61;

				// TODO 2
			} else if (nameId.equalsIgnoreCase("$5689")) {// 記憶水晶(幻覺：巫妖)
				// 技能編號
				skillid = ILLUSION_LICH;
				// 分組
				magicLv = 62;

			} else if (nameId.equalsIgnoreCase("$5690")) {// 記憶水晶(立方：地裂)
				// 技能編號
				skillid = CUBE_QUAKE;
				// 分組
				magicLv = 62;

			} else if (nameId.equalsIgnoreCase("$5691")) {// 記憶水晶(耐力)
				// 技能編號
				skillid = PATIENCE;
				// 分組
				magicLv = 62;

			} else if (nameId.equalsIgnoreCase("$5692")) {// 記憶水晶(幻想)
				// 技能編號
				skillid = PHANTASM;
				// 分組
				magicLv = 62;

				// TODO 3
			} else if (nameId.equalsIgnoreCase("$5693")) {// 記憶水晶(武器破壞者)
				// 技能編號
				skillid = ARM_BREAKER;
				// 分組
				magicLv = 63;

			} else if (nameId.equalsIgnoreCase("$5694")) {// 記憶水晶(幻覺：鑽石高崙)
				// 技能編號
				skillid = ILLUSION_DIA_GOLEM;
				// 分組
				magicLv = 63;

			} else if (nameId.equalsIgnoreCase("$5695")) {// 記憶水晶(立方：衝擊)
				// 技能編號
				skillid = CUBE_SHOCK;
				// 分組
				magicLv = 63;

			} else if (nameId.equalsIgnoreCase("$5696")) {// 記憶水晶(洞察)
				// 技能編號
				skillid = INSIGHT;
				// 分組
				magicLv = 63;

				// TODO 4
			} else if (nameId.equalsIgnoreCase("$5697")) {// 記憶水晶(恐慌)
				// 技能編號
				skillid = PANIC;
				// 分組
				magicLv = 64;

			} else if (nameId.equalsIgnoreCase("$5698")) {// 記憶水晶(疼痛的歡愉)
				// 技能編號
				skillid = REDUCE_WEIGHT;
				// 分組
				magicLv = 64;

			} else if (nameId.equalsIgnoreCase("$5699")) {// 記憶水晶(幻覺：化身)
				// 技能編號
				skillid = ILLUSION_AVATAR;
				// 分組
				magicLv = 64;

			} else if (nameId.equalsIgnoreCase("$5700")) {// 記憶水晶(立方：和諧)
				// 技能編號
				skillid = CUBE_BALANCE;
				// 分組
				magicLv = 64;
			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
