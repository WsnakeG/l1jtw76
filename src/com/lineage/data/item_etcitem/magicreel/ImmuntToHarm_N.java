package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillId;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 魔法卷軸 (聖結界) 禁止轉生後使用
 */
public class ImmuntToHarm_N extends ItemExecutor {

	/**
	 *
	 */
	private ImmuntToHarm_N() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ImmuntToHarm_N();
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

		// 等級限制
		if (pc.getLevel() > 60) {
			// 285 \f1在此狀態下無法使用魔法。
			pc.sendPackets(new S_ServerMessage(285));
			return;
		}

		final int useCount = 1;
		if (pc.getInventory().removeItem(item, useCount) >= useCount) {
			L1BuffUtil.cancelAbsoluteBarrier(pc);

			final int skillid = L1SkillId.IMMUNE_TO_HARM;

			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillid, pc.getId(), 0, 0, 0, L1SkillUse.TYPE_SPELLSC);
		}
	}
}
