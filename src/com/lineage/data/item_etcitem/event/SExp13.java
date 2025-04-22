package com.lineage.data.item_etcitem.event;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 第二段1.3倍經驗加倍 41238
 */
public class SExp13 extends ItemExecutor {

	/**
	 *
	 */
	private SExp13() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SExp13();
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
		if (L1BuffUtil.cancelExpSkill_2(pc)) {
			final int time = 600;
			// 刪除物品
			if (pc.getInventory().removeItem(item, 1) == 1) {
				pc.setSkillEffect(L1SkillId.SEXP13, time * 1000);
				// 3076 第二段經驗13%計時開始，效果時間600秒。
				pc.sendPackets(new S_ServerMessage("第二段 EXP提升130%(600秒)"));
				pc.sendPacketsX8(new S_SkillSound(pc.getId(), 198));
			}
		}
	}
}