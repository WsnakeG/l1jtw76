package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;

/**
 * 40860 魔法卷軸 (日光術) 40861 魔法卷軸 (保護罩) 40871 魔法卷軸 (無所遁形術) 40872 魔法卷軸 (負重強化) 40889
 * 魔法卷軸 (魔法屏障) 40890 魔法卷軸 (冥想術)
 */
public class MagicReel_Spell extends ItemExecutor {

	/**
	 *
	 */
	private MagicReel_Spell() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicReel_Spell();
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
		if (pc == null) {
			return;
		}
		if (item == null) {
			return;
		}

		final int useCount = 1;
		if (pc.getInventory().removeItem(item, useCount) >= useCount) {
			L1BuffUtil.cancelAbsoluteBarrier(pc);

			final int itemId = item.getItemId();
			final int skillid = itemId - 40858;

			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillid, pc.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
		}
	}
}
