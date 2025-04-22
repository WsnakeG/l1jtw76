package com.lineage.data.item_etcitem.skill;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔法書(等級9)</font><BR>
 * Spell Book Lv9
 * 
 * @author dexc
 */
public class Skill_SpellbookLv9 extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookLv9() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookLv9();
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
			final int magicLv = 9;

			if (nameId.equalsIgnoreCase("$557")) {// 魔法書(雷霆風暴)
				// 技能編號
				skillid = 65;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$558")) {// 魔法書(沉睡之霧)
				// 技能編號
				skillid = 66;
				// 技能屬性
				attribute = 2;

			} else if (nameId.equalsIgnoreCase("$559")) {// 魔法書(變形術)
				// 技能編號
				skillid = 67;
				// 技能屬性
				attribute = 2;

			} else if (nameId.equalsIgnoreCase("$560")) {// 魔法書(聖結界)
				// 技能編號
				skillid = 68;
				// 技能屬性
				attribute = 1;

			} else if (nameId.equalsIgnoreCase("$561")) {// 魔法書(集體傳送術)
				// 技能編號
				skillid = 69;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1590")) {// 魔法書(火風暴)
				// 技能編號
				skillid = 70;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1870")) {// 魔法書(藥水霜化術)
				// 技能編號
				skillid = 71;
				// 技能屬性
				attribute = 0;

			} else if (nameId.equalsIgnoreCase("$1871")) {// 魔法書(強力無所遁形術)
				// 技能編號
				skillid = 72;
				// 技能屬性
				attribute = 0;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
