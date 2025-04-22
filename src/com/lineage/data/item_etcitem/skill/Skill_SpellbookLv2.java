package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級2)</font><BR>
 * Spell Book Lv2
 * 
 * @author dexc
 */
public class Skill_SpellbookLv2 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv2();
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
		final int magicLv = 2;

		if (nameId.equalsIgnoreCase("$522")) {// 魔法書(解毒術)
			// 技能編號
			skillid = 9;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$523")) {// 魔法書(寒冷戰慄)
			// 技能編號
			skillid = 10;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$524")) {// 魔法書(毒咒)
			// 技能編號
			skillid = 11;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$525")) {// 魔法書(擬似魔法武器)
			// 技能編號
			skillid = 12;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$526")) {// 魔法書(無所遁形術)
			// 技能編號
			skillid = 13;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1858")) {// 魔法書(負重強化)
			// 技能編號
			skillid = 14;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1583")) {// 魔法書 (火箭)
			// 技能編號
			skillid = 15;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1584")) {// 魔法書 (地獄之牙)
			// 技能編號
			skillid = 16;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
