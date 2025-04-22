package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static com.lineage.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static com.lineage.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static com.lineage.server.model.skill.L1SkillId.SHOCK_STUN;
import static com.lineage.server.model.skill.L1SkillId.SOLID_CARRIAGE;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>技術書(騎士技能)</font><BR>
 * Technical Document
 * 
 * @author dexc
 */
public class Skill_TechnicalDocument extends ItemExecutor {

	/**
	 *
	 */
	private Skill_TechnicalDocument() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_TechnicalDocument();
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
		// 不是騎士
		if (!pc.isKnight()) {
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
			int magicLv = 0;

			if (nameId.equalsIgnoreCase("$3259")) {// 技術書(衝擊之暈)
				// 技能編號
				skillid = SHOCK_STUN;
				// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
				// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
				attribute = 5;
				// 分組
				magicLv = 31;

			} else if (nameId.equalsIgnoreCase("$4007")) {// 技術書(增幅防禦)
				// 技能編號
				skillid = REDUCTION_ARMOR;
				// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
				// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
				attribute = 5;
				// 分組
				magicLv = 31;

			} else if (nameId.equalsIgnoreCase("$4008")) {// 技術書(尖刺盔甲)
				// 技能編號
				skillid = BOUNCE_ATTACK;
				// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
				// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
				attribute = 5;
				// 分組
				magicLv = 32;

			} else if (nameId.equalsIgnoreCase("$4712")) {// 技術書(堅固防護)
				// 技能編號
				skillid = SOLID_CARRIAGE;
				// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
				// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
				attribute = 5;
				// 分組
				magicLv = 31;

			} else if (nameId.equalsIgnoreCase("$4713")) {// 技術書(反擊屏障)
				// 技能編號
				skillid = COUNTER_BARRIER;
				// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
				// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
				attribute = 5;
				// 分組
				magicLv = 31;
			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
