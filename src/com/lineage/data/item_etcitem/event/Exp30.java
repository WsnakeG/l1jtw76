package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_ServerMessage;

public class Exp30 extends ItemExecutor {

	/**
	 *
	 */
	private Exp30() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Exp30();
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
		// 判斷經驗加倍技能
		if (L1BuffUtil.cancelExpSkill(pc)) {
			final int time = 600;
			pc.setSkillEffect(L1SkillId.EXP30, time * 1000);
			pc.getInventory().removeItem(item, 1);// 删除1个药水
			pc.sendPackets(new S_ServerMessage("EXP提升300%(600秒)"));
			// 狩獵的EXP將會增加
			pc.sendPackets(new S_PacketBoxCooking(pc, 32, time));
		}
	}
}