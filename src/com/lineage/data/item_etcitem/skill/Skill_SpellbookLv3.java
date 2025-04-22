package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級3)</font><BR>
 * Spell Book Lv3
 * 
 * @author dexc
 */
public class Skill_SpellbookLv3 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv3() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv3();
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
		final int magicLv = 3;

		if (nameId.equalsIgnoreCase("$527")) {// 魔法書(極光雷電)
			// 技能編號
			skillid = 17;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$528")) {// 魔法書(起死回生術)
			// 技能編號
			skillid = 18;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$529")) {// 魔法書(中級治癒術)
			// 技能編號
			skillid = 19;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$530")) {// 魔法書(闇盲咒術)
			// 技能編號
			skillid = 20;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$531")) {// 魔法書(鎧甲護持)
			// 技能編號
			skillid = 21;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1585")) {// 魔法書(寒冰氣息)
			// 技能編號
			skillid = 22;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1859")) {// 魔法書(能量感測)
			// 技能編號
			skillid = 23;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
