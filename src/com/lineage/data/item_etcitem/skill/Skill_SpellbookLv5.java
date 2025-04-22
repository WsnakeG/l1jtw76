package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級5)</font><BR>
 * Spell Book Lv5
 * 
 * @author dexc
 */
public class Skill_SpellbookLv5 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv5() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv5();
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
		// 取得名稱
		final String nameId = item.getItem().getNameId();
		// 技能編號
		int skillid = 0;
		// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
		// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
		int attribute = 0;
		// 分組
		final int magicLv = 5;

		if (nameId.equalsIgnoreCase("$537")) {// 魔法書(木乃伊的詛咒)
			// 技能編號
			skillid = 33;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$538")) {// 魔法書(極道落雷)
			// 技能編號
			skillid = 34;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$539")) {// 魔法書(高級治癒術)
			// 技能編號
			skillid = 35;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$540")) {// 魔法書(迷魅術)
			// 技能編號
			skillid = 36;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$541")) {// 魔法書(聖潔之光)
			// 技能編號
			skillid = 37;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$1587")) {// 魔法書(冰錐)
			// 技能編號
			skillid = 38;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1862")) {// 魔法書(魔力奪取)
			// 技能編號
			skillid = 39;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1863")) {// 魔法書(黑闇之影)
			// 技能編號
			skillid = 40;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
