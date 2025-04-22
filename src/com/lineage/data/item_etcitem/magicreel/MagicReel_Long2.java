package com.lineage.data.item_etcitem.magicreel;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.model.skill.L1SkillUse;
import com.lineage.server.serverpackets.S_ServerMessage;

/**
 * 40862 魔法卷軸 (光箭) 40864 魔法卷軸 (冰箭) 40865 魔法卷軸 (風刃) 40869 魔法卷軸 (毒咒) 40873 魔法卷軸
 * (火箭) 40874 魔法卷軸 (地獄之牙) 40875 魔法卷軸 (極光雷電) 40876 魔法卷軸 (起死回生術) 40878 魔法卷軸 (闇盲咒術)
 * 40880 魔法卷軸 (寒冰氣息) 40881 魔法卷軸 (能量感測) 40883 魔法卷軸 (燃燒的火球) 40885 魔法卷軸 (壞物術) 40887
 * 魔法卷軸 (緩速術) 40888 魔法卷軸 (巖牢) 40891 魔法卷軸 (木乃伊的詛咒) 40892 魔法卷軸 (極道落雷) 40894 魔法卷軸
 * (迷魅術) 40896 魔法卷軸 (冰錐) 40898 魔法卷軸 (黑闇之影)
 */
public class MagicReel_Long2 extends ItemExecutor {

	/**
	 *
	 */
	private MagicReel_Long2() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new MagicReel_Long2();
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

		// 隱身狀態可用魔法限制
		if (pc.isInvisble() || pc.isInvisDelay()) {
			// \f1施咒取消。
			pc.sendPackets(new S_ServerMessage(281));
			return;
		}

		final int targetID = data[0];
		final int spellsc_x = data[1];
		final int spellsc_y = data[2];

		if ((targetID == pc.getId()) || (targetID == 0)) {
			// \f1施咒取消。
			pc.sendPackets(new S_ServerMessage(281));
			return;
		}

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
