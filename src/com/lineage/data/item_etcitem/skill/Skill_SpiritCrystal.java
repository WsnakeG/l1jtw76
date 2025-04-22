package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static com.lineage.server.model.skill.L1SkillId.BLOODY_SOUL;
import static com.lineage.server.model.skill.L1SkillId.BODY_TO_MIND;
import static com.lineage.server.model.skill.L1SkillId.CLEAR_MIND;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_MIRROR;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static com.lineage.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static com.lineage.server.model.skill.L1SkillId.ERASE_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.GREATER_ELEMENTAL;
import static com.lineage.server.model.skill.L1SkillId.LESSER_ELEMENTAL;
import static com.lineage.server.model.skill.L1SkillId.RESIST_ELEMENTAL;
import static com.lineage.server.model.skill.L1SkillId.RESIST_MAGIC;
import static com.lineage.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static com.lineage.server.model.skill.L1SkillId.TELEPORT_TO_MATHER;
import static com.lineage.server.model.skill.L1SkillId.TRIPLE_ARROW;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>精靈水晶(精靈魔法-無屬性)</font><BR>
 * Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_SpiritCrystal extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpiritCrystal() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpiritCrystal();
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
		// 不是精靈
		if (!pc.isElf()) {
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
			final int attribute = 3;
			// 分組
			int magicLv = 0;

			if (nameId.equalsIgnoreCase("$1829")) {// 精靈水晶(魔法防禦)
				// 技能編號
				skillid = RESIST_MAGIC;
				// 分組
				magicLv = 11;

			} else if (nameId.equalsIgnoreCase("$1830")) {// 精靈水晶(心靈轉換)
				// 技能編號
				skillid = BODY_TO_MIND;
				// 分組
				magicLv = 11;

			} else if (nameId.equalsIgnoreCase("$1831")) {// 精靈水晶(世界樹的呼喚)
				// 技能編號
				skillid = TELEPORT_TO_MATHER;
				// 分組
				magicLv = 11;

			} else if (nameId.equalsIgnoreCase("$1832")) {// 精靈水晶(淨化精神)
				// 技能編號
				skillid = CLEAR_MIND;
				// 分組
				magicLv = 12;

			} else if (nameId.equalsIgnoreCase("$1833")) {// 精靈水晶(屬性防禦)
				// 技能編號
				skillid = RESIST_ELEMENTAL;
				// 分組
				magicLv = 12;

			} else if (nameId.equalsIgnoreCase("$3261")) {// 精靈水晶(三重矢)
				// 技能編號
				skillid = TRIPLE_ARROW;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1834")) {// 精靈水晶(釋放元素)
				// 技能編號
				skillid = RETURN_TO_NATURE;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1835")) {// 精靈水晶(魂體轉換)
				// 技能編號
				skillid = BLOODY_SOUL;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1836")) {// 精靈水晶(單屬性防禦)
				// 技能編號
				skillid = ELEMENTAL_PROTECTION;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$3262")) {// 精靈水晶(弱化屬性)
				// 技能編號
				skillid = ELEMENTAL_FALL_DOWN;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$1842")) {// 精靈水晶(魔法消除)
				// 技能編號
				skillid = ERASE_MAGIC;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$1843")) {// 精靈水晶(召喚屬性精靈)
				// 技能編號
				skillid = LESSER_ELEMENTAL;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$3263")) {// 精靈水晶(鏡反射)
				// 技能編號
				skillid = COUNTER_MIRROR;
				// 分組
				magicLv = 15;

			} else if (nameId.equalsIgnoreCase("$1849")) {// 精靈水晶(封印禁地)
				// 技能編號
				skillid = AREA_OF_SILENCE;
				// 分組
				magicLv = 15;

			} else if (nameId.equalsIgnoreCase("$1850")) {// 精靈水晶(召喚強力屬性精靈)
				// 技能編號
				skillid = GREATER_ELEMENTAL;
				// 分組
				magicLv = 15;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
