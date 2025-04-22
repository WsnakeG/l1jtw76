package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔法書(等級10)</font><BR>
 * Spell Book Lv10
 * 
 * @author dexc
 */
public class Skill_SpellbookLv10 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv10() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv10();
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
			final int magicLv = 10;

			if (nameId.equalsIgnoreCase("$562")) {// 魔法書(創造魔法武器)
				// 技能編號
				skillid = 73;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$563")) {// 魔法書(流星雨)
				// 技能編號
				skillid = 74;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$564")) {// 魔法書(終極返生術)
				// 技能編號
				skillid = 75;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$565")) {// 魔法書(集體緩速術)
				// 技能編號
				skillid = 76;
				// 技能屬性
				attribute = 2;

			} else if (nameId.equalsIgnoreCase("$566")) {// 魔法書(究極光裂術)
				// 技能編號
				skillid = 77;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$1872")) {// 魔法書(絕對屏障)
				// 技能編號
				skillid = 78;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1873")) {// 魔法書(靈魂昇華)
				// 技能編號
				skillid = 79;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1874")) {// 魔法書(冰雪颶風)
				// 技能編號
				skillid = 80;
				// 技能屬性
				attribute = 2;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
