package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_PacketBoxCooking;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 經驗書
 */
public class ExpBook extends ItemExecutor {

	/**
	 *
	 */
	private ExpBook() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ExpBook();
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
		pc.getInventory().removeItem(item, 1);
		// 判斷經驗加倍技能
		if (L1BuffUtil.cancelExpSkill(pc)) {
			final int time = 600;
			pc.setSkillEffect(L1SkillId.EXP13, time * 1000);
			pc.getInventory().removeItem(item, 1);
			pc.sendPackets(new S_ServerMessage("經驗質提升1.3倍(600秒)"));
			// 狩獵的經驗職將會增加
			pc.sendPackets(new S_PacketBoxCooking(pc, 32, time));
		}
	}
}