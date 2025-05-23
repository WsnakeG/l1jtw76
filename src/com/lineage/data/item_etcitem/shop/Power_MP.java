package com.lineage.data.item_etcitem.shop;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 超級魔力藥劑<BR>
 * 44168<BR>
 * 每次恢復的魔量為30~50 使用後消失<BR>
 */
public class Power_MP extends ItemExecutor {

	/**
	 *
	 */
	private Power_MP() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_MP();
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
		if (L1BuffUtil.stopPotion(pc)) {
			if (pc.getInventory().removeItem(item, 1) != 1) {
				return;
			}

			// 解除魔法技能绝对屏障
			L1BuffUtil.cancelAbsoluteBarrier(pc);

			final Random random = new Random();

			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));

			int healMp = random.nextInt(30) + 20;
			if (pc.hasSkillEffect(POLLUTE_WATER)) {
				healMp = (healMp >> 1);
			}
			if (pc.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
				healMp = (healMp >> 1);
			}
			if (pc.hasSkillEffect(ADLV80_2_1)) {
				healMp *= -1;
			}
			pc.setCurrentMp(pc.getCurrentMp() + healMp);
		}
	}
}
