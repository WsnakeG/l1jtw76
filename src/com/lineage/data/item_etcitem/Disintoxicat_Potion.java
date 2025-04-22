package com.lineage.data.item_etcitem;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 翡翠藥水(解毒藥水)40017 安特之樹枝40507
 */
public class Disintoxicat_Potion extends ItemExecutor {

	/**
	 *
	 */
	private Disintoxicat_Potion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Disintoxicat_Potion();
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
		if (pc.hasSkillEffect(71) == true) { // 若藥霜成立
			pc.sendPackets(new S_ServerMessage(698)); // 喉嚨灼熱,無法喝東西。
		} else {
			// 移除道具
			pc.getInventory().removeItem(item, 1);
			// 解除魔法技能绝对屏障
			L1BuffUtil.cancelAbsoluteBarrier(pc);
			S_SkillSound sound = new S_SkillSound(pc.getId(), 192); // 送出解毒特效
			pc.sendPacketsX8(sound);
			pc.curePoison();
		}
	}
}
