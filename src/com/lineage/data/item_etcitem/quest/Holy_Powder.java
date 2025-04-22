package com.lineage.data.item_etcitem.quest;

import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_MITHRIL_POWDER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_WATER;
import static com.lineage.server.model.skill.L1SkillId.STATUS_HOLY_WATER_OF_EVA;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 神聖的米索莉粉41316
 */
public class Holy_Powder extends ItemExecutor {

	/**
	 *
	 */
	private Holy_Powder() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Holy_Powder();
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
		if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (pc.hasSkillEffect(STATUS_HOLY_WATER)) {
			pc.removeSkillEffect(STATUS_HOLY_WATER);
		}
		pc.setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));
		// 你得到可以攻擊哈蒙將軍的力量。
		pc.sendPackets(new S_ServerMessage(1142));
		pc.getInventory().removeItem(item, 1);
	}
}
