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
 * 伊娃的聖水41354
 */
public class Holy_WaterEva extends ItemExecutor {

	/**
	 *
	 */
	private Holy_WaterEva() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Holy_WaterEva();
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
		if (pc.hasSkillEffect(STATUS_HOLY_WATER) || pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
			// 沒有任何事情發生
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
			pc.removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
		}
		pc.setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
		pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));
		// 你得到可以攻擊受詛咒的巫女莎爾的力量。
		pc.sendPackets(new S_ServerMessage(1140));
		pc.getInventory().removeItem(item, 1);
	}
}
