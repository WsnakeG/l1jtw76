package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級1)</font><BR>
 * Spell Book Lv1
 * 
 * @author dexc
 */
public class Skill_SpellbookLv1 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv1() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv1();
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
		final int magicLv = 1;

		if (nameId.equalsIgnoreCase("$517")) {// 魔法書(初級治癒術)
			// 技能編號
			skillid = 1;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$518")) {// 魔法書(日光術)
			// 技能編號
			skillid = 2;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$519")) {// 魔法書(保護罩)
			// 技能編號
			skillid = 3;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$520")) {// 魔法書(光箭)
			// 技能編號
			skillid = 4;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$521")) {// 魔法書(指定傳送)
			// 技能編號
			skillid = 5;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1581")) {// 魔法書(冰箭)
			// 技能編號
			skillid = 6;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1582")) {// 魔法書(風刃)
			// 技能編號
			skillid = 7;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1857")) {// 魔法書(神聖武器)
			// 技能編號
			skillid = 8;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
