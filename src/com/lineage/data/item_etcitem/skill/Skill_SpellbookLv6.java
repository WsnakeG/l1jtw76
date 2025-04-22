package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級6)</font><BR>
 * Spell Book Lv6
 * 
 * @author dexc
 */
public class Skill_SpellbookLv6 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv6() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv6();
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
		final int magicLv = 6;

		if (nameId.equalsIgnoreCase("$542")) {// 魔法書(造屍術)
			// 技能編號
			skillid = 41;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$543")) {// 魔法書(體魄強健術)
			// 技能編號
			skillid = 42;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$544")) {// 魔法書(加速術)
			// 技能編號
			skillid = 43;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$545")) {// 魔法書(魔法相消術)
			// 技能編號
			skillid = 44;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$546")) {// 魔法書(地裂術)
			// 技能編號
			skillid = 45;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1588")) {// 魔法書(烈炎術)
			// 技能編號
			skillid = 46;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1864")) {// 魔法書(弱化術)
			// 技能編號
			skillid = 47;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$1865")) {// 魔法書(祝福魔法武器)
			// 技能編號
			skillid = 48;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
