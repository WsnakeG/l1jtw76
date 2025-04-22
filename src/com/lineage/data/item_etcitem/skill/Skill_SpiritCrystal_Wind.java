package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.STORM_EYE;
import static com.lineage.server.model.skill.L1SkillId.STORM_SHOT;
import static com.lineage.server.model.skill.L1SkillId.STRIKER_GALE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHACKLE;
import static com.lineage.server.model.skill.L1SkillId.WIND_SHOT;
import static com.lineage.server.model.skill.L1SkillId.WIND_WALK;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>精靈水晶(精靈魔法-風屬性)</font><BR>
 * Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_SpiritCrystal_Wind extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpiritCrystal_Wind() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpiritCrystal_Wind();
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

			// 屬性係不同
		} else if (pc.getElfAttr() != 8) {
			// 684 屬性系列不同無法學習。
			final S_ServerMessage msg = new S_ServerMessage(684);
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

			if (nameId.equalsIgnoreCase("$1838")) {// 精靈水晶(風之神射)
				// 技能編號
				skillid = WIND_SHOT;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1839")) {// 精靈水晶(風之疾走)
				// 技能編號
				skillid = WIND_WALK;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1845")) {// 精靈水晶(暴風之眼)
				// 技能編號
				skillid = STORM_EYE;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$1854")) {// 精靈水晶(暴風神射)
				// 技能編號
				skillid = STORM_SHOT;
				// 分組
				magicLv = 15;

			} else if (nameId.equalsIgnoreCase("$1855")) {// 精靈水晶(風之枷鎖)
				// 技能編號
				skillid = WIND_SHACKLE;
				// 分組
				magicLv = 15;

			} else if (nameId.equalsIgnoreCase("$4718")) {// 精靈水晶(精准射擊)
				// 技能編號
				skillid = STRIKER_GALE;
				// 分組
				magicLv = 15;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
