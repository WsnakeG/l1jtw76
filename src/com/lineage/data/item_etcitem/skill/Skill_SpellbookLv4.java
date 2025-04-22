package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;

/**
 * <font color=#00800>魔法書(等級4)</font><BR>
 * Spell Book Lv4
 * 
 * @author dexc
 */
public class Skill_SpellbookLv4 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv4() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv4();
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
		final int magicLv = 4;

		if (nameId.equalsIgnoreCase("$532")) {// 魔法書(燃燒的火球)
			// 技能編號
			skillid = 25;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$533")) {// 魔法書(通暢氣脈術)
			// 技能編號
			skillid = 26;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$534")) {// 魔法書(壞物術)
			// 技能編號
			skillid = 27;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$535")) {// 魔法書(吸血鬼之吻)
			// 技能編號
			skillid = 28;
			// 技能屬性
			attribute = 2;

		} else if (nameId.equalsIgnoreCase("$536")) {// 魔法書(緩速術)
			// 技能編號
			skillid = 29;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1586")) {// 魔法書(岩牢)
			// 技能編號
			skillid = 30;
			// 技能屬性
			attribute = 0;

		} else if (nameId.equalsIgnoreCase("$1860")) {// 魔法書(魔法屏障)
			// 技能編號
			skillid = 31;
			// 技能屬性
			attribute = 1;

		} else if (nameId.equalsIgnoreCase("$1861")) {// 魔法書(冥想術)
			// 技能編號
			skillid = 32;
			// 技能屬性
			attribute = 0;

		}

		// 檢查學習該法術是否成立
		Skill_Check.check(pc, item, skillid, magicLv, attribute);
	}
}
