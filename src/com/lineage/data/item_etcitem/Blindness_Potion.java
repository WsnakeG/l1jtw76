package com.lineage.data.item_etcitem;

import static com.lineage.server.model.skill.L1SkillId.DARKNESS;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillMode;
import com.lineage.server.model.skill.skillmode.SkillMode;

/**
 * 失明药水（黑暗药水）40025
 */
public class Blindness_Potion extends ItemExecutor {

	/**
	 *
	 */
	private Blindness_Potion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Blindness_Potion();
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
		pc.getInventory().removeItem(item, 1);
		useBlindPotion(pc);
	}

	private void useBlindPotion(final L1PcInstance pc) {
		// 解除魔法技能绝对屏障
		L1BuffUtil.cancelAbsoluteBarrier(pc);

		final int time = 16;
		/*
		 * if (pc.hasSkillEffect(CURSE_BLIND)) {
		 * pc.killSkillEffectTimer(CURSE_BLIND); } else
		 */if (pc.hasSkillEffect(DARKNESS)) {
			pc.killSkillEffectTimer(DARKNESS);
		}

		// SKILL移轉 (替換成`DARKNESS` by terry0412)
		final SkillMode mode = L1SkillMode.get().getSkill(DARKNESS);
		if (mode != null) {
			try {
				mode.start(pc, pc, null, time);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
