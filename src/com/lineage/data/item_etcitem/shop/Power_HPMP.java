package com.lineage.data.item_etcitem.shop;

import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_1;
import static com.lineage.server.model.skill.L1SkillId.ADLV80_2_2;
import static com.lineage.server.model.skill.L1SkillId.POLLUTE_WATER;

import java.util.Random;

import com.lineage.data.executor.ItemExecutor;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.skill.L1BuffUtil;
import com.lineage.server.serverpackets.S_PacketBoxHpMsg;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 超級治療藥劑<BR>
 * 44169<BR>
 * 每次恢復的血量為50~75<BR>
 * 每次恢復的魔量為30~40<BR>
 * 使用後消失
 */
public class Power_HPMP extends ItemExecutor {

	/**
	 *
	 */
	private Power_HPMP() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_HPMP();
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

			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 197));
			pc.sendPacketsX8(new S_SkillSound(pc.getId(), 190));

			int healMp = random.nextInt(50) + 25;
			int healHp = random.nextInt(30) + 10;
			if (pc.get_up_hp_potion() > 0) {
				healHp += (healHp * pc.get_up_hp_potion()) / 100;
			}
			if (pc.hasSkillEffect(POLLUTE_WATER)) {
				healHp = (healHp >> 1);
				healMp = (healMp >> 1);
			}
			if (pc.hasSkillEffect(ADLV80_2_2)) {// 污濁的水流(水龍副本 回復量1/2倍)
				healHp = (healHp >> 1);
				healMp = (healMp >> 1);
			}
			if (pc.hasSkillEffect(ADLV80_2_1)) {
				healHp *= -1;
				healMp *= -1;
			}
			if (healHp > 0) {
				// 你覺得舒服多了訊息
				pc.sendPackets(new S_PacketBoxHpMsg());
			}
			pc.setCurrentMp(pc.getCurrentMp() + healMp);
			pc.setCurrentHp(pc.getCurrentHp() + healHp);
		}
	}
}
