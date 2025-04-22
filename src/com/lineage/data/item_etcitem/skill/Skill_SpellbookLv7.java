package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔法書(等級7)</font><BR>
 * Spell Book Lv7
 * 
 * @author dexc
 */
public class Skill_SpellbookLv7 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv7() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv7();
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
		// 不是法師
		if (!pc.isWizard()) {
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
			int attribute = 0;
			// 分組
			final int magicLv = 7;

			if (nameId.equalsIgnoreCase("$547")) {// 魔法書(體力回復術)
				// 技能編號
				skillid = 49;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$548")) {// 魔法書(冰矛圍籬)
				// 技能編號
				skillid = 50;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$549")) {// 魔法書(召喚術)
				// 技能編號
				skillid = 51;
				// 技能屬性
				attribute = 2;

			} else if (nameId.equalsIgnoreCase("$550")) {// 魔法書(神聖疾走)
				// 技能編號
				skillid = 52;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$551")) {// 魔法書(龍捲風)
				// 技能編號
				skillid = 53;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1651")) {// 魔法書(強力加速術)
				// 技能編號
				skillid = 54;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1866")) {// 魔法書(狂暴術)
				// 技能編號
				skillid = 55;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1867")) {// 魔法書(疾病術)
				// 技能編號
				skillid = 56;
				// 技能屬性
				attribute = 2;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
