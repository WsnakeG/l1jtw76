package com.lineage.data.item_etcitem.skill;

import static com.lineage.server.model.skill.L1SkillId.EARTH_BIND;
import static com.lineage.server.model.skill.L1SkillId.EARTH_GUARDIAN;
import static com.lineage.server.model.skill.L1SkillId.EARTH_SKIN;
import static com.lineage.server.model.skill.L1SkillId.ENTANGLE;
import static com.lineage.server.model.skill.L1SkillId.EXOTIC_VITALIZE;
import static com.lineage.server.model.skill.L1SkillId.IRON_SKIN;

import com.lineage.data.cmd.Skill_Check;
import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * <font color=#00800>精靈水晶(精靈魔法-地屬性)</font><BR>
 * Spirit Crystal
 * 
 * @author dexc
 */
public class Skill_SpiritCrystal_Earth extends ItemExecutor {

	/**
	 *
	 */
	private Skill_SpiritCrystal_Earth() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Skill_SpiritCrystal_Earth();
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
		// 不是精靈
		if (!pc.isElf()) {
			// 79 沒有任何事情發生
			final S_ServerMessage msg = new S_ServerMessage(79);
			pc.sendPackets(msg);

			// 屬性係不同
		} else if (pc.getElfAttr() != 1) {
			// 684 屬性系列不同無法學習。
			final S_ServerMessage msg = new S_ServerMessage(684);
			pc.sendPackets(msg);

		} else {
			// 取得名稱
			final String nameId = item.getItem().getNameId();
			// 技能編號
			int skillid = 0;
			// 技能屬性 0:中立屬性魔法 1:正義屬性魔法 2:邪惡屬性魔法
			// 技能屬性 3:精靈專屬魔法 4:王族專屬魔法 5:騎士專屬技能 6:黑暗精靈專屬魔法
			final int attribute = 3;
			// 分組
			int magicLv = 0;

			if (nameId.equalsIgnoreCase("$1840")) {// 精靈水晶(大地防護)
				// 技能編號
				skillid = EARTH_SKIN;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1841")) {// 精靈水晶(地面障礙)
				// 技能編號
				skillid = ENTANGLE;
				// 分組
				magicLv = 13;

			} else if (nameId.equalsIgnoreCase("$1846")) {// 精靈水晶(大地屏障)
				// 技能編號
				skillid = EARTH_BIND;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$1848")) {// 精靈水晶(大地的祝福)
				// 技能編號
				skillid = EARTH_GUARDIAN;
				// 分組
				magicLv = 14;

			} else if (nameId.equalsIgnoreCase("$1856")) {// 精靈水晶(鋼鐵防護)
				// 技能編號
				skillid = IRON_SKIN;
				// 分組
				magicLv = 15;

			} else if (nameId.equalsIgnoreCase("$3265")) {// 精靈水晶(體能激發)
				// 技能編號
				skillid = EXOTIC_VITALIZE;
				// 分組
				magicLv = 15;

			}

			// 檢查學習該法術是否成立
			Skill_Check.check(pc, item, skillid, magicLv, attribute);
		}
	}
}
