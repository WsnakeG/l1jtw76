package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔法書(等級8)</font><BR>
 * Spell Book Lv8
 * 
 * @author dexc
 */
public class Skill_SpellbookLv8 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv8() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv8();
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
			final int magicLv = 8;

			if (nameId.equalsIgnoreCase("$552")) {// 魔法書(全部治癒術)
				// 技能編號
				skillid = 57;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$553")) {// 魔法書(火牢)
				// 技能編號
				skillid = 58;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$554")) {// 魔法書(冰雪暴)
				// 技能編號
				skillid = 59;
				// 技能屬性
				attribute = 2;

			} else if (nameId.equalsIgnoreCase("$555")) {// 魔法書(隱身術)
				// 技能編號
				skillid = 60;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$556")) {// 魔法書(返生術)
				// 技能編號
				skillid = 61;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$1589")) {// 魔法書(震裂術)
				// 技能編號
				skillid = 62;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1868")) {// 魔法書(治癒能量風暴)
				// 技能編號
				skillid = 63;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1869")) {// 魔法書(魔法封印)
				// 技能編號
				skillid = 64;
				// 技能屬性
				attribute = 0;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
