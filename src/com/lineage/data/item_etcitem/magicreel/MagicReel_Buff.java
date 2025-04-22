package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.world.World;

/**
 * 40859 魔法卷軸 (初級治癒術) 40866 魔法卷軸 (神聖武器) 40867 魔法卷軸 (解毒術) 40877 魔法卷軸 (中級治癒術)
 * 40884 魔法卷軸 (通暢氣脈術) 40893 魔法卷軸 (高級治癒術) 40895 魔法卷軸 (聖潔之光)
 */
public class MagicReel_Buff extends ItemExecutor {

	/**
	 *
	 */
	private MagicReel_Buff() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicReel_Buff();
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
		final int targetID = data[0];

		if (targetID == 0) {
			// 281 \f1施咒取消。
			pc.sendPackets(new S_ServerMessage(281));
			return;
		}

		final L1Object target = World.get().findObject(targetID);

		final int spellsc_x = target.getX();
		final int spellsc_y = target.getY();

		final int useCount = 1;
		if (pc.getInventory().removeItem(item, useCount) >= useCount) {
			L1BuffUtil.cancelAbsoluteBarrier(pc);

			final int itemId = item.getItemId();
			final int skillid = itemId - 40858;

			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillid, targetID, spellsc_x, spellsc_y, 0,
					L1SkillUse.TYPE_SPELLSC);
		}
	}
}
