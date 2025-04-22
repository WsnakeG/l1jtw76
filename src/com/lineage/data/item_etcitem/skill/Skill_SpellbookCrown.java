package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.BRAVE_MENTAL;
import static com.lineage.server.model.skill.L1SkillId.CALL_CLAN;
import static com.lineage.server.model.skill.L1SkillId.GLOWING_WEAPON;
import static com.lineage.server.model.skill.L1SkillId.RUN_CLAN;
import static com.lineage.server.model.skill.L1SkillId.SHINING_AURA;
import static com.lineage.server.model.skill.L1SkillId.TRUE_TARGET;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>魔法書(王族專屬魔法)</font><BR>
 * Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_SpellbookCrown extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpellbookCrown() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpellbookCrown();
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
		// 不是王族
		if (!pc.isCrown()) {
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
			final int attribute = 4;
			// 分組
			int magicLv = 0;

			if (nameId.equalsIgnoreCase("$1959")) {// 魔法書 (精準目標)
				// 技能編號
				skillid = TRUE_TARGET;
				// 分組
				magicLv = 21;

			} else if (nameId.equalsIgnoreCase("$2089")) {// 魔法書 (呼喚盟友)
				// 技能編號
				skillid = CALL_CLAN;
				// 分組
				magicLv = 22;

			} else if (nameId.equalsIgnoreCase("$1960")) {// 魔法書(激勵士氣)
				// 技能編號
				skillid = GLOWING_WEAPON;
				// 分組
				magicLv = 23;

			} else if (nameId.equalsIgnoreCase("$3260")) {// 魔法書(援護盟友)
				// 技能編號
				skillid = RUN_CLAN;
				// 分組
				magicLv = 24;

			} else if (nameId.equalsIgnoreCase("$3176")) {// 魔法書(衝擊士氣)
				// 技能編號
				skillid = BRAVE_MENTAL;
				// 分組
				magicLv = 25;

			} else if (nameId.equalsIgnoreCase("$3175")) {// 魔法書(鋼鐵士氣)
				// 技能編號
				skillid = SHINING_AURA;
				// 分組
				magicLv = 26;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
