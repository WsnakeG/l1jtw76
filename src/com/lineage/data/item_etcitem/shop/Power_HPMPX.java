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
import com.lineage.server.serverpackets.S_ServerMessage;
import com.lineage.server.serverpackets.S_SkillSound;

/**
 * 魔幻治療徽章<BR>
 * 44172<BR>
 * 每次恢復的血量為50~75<BR>
 * 每次恢復的魔量為30~40<BR>
 * 每次使用扣除人物身上7000金幣,金幣不足無法使用
 */
public class Power_HPMPX extends ItemExecutor {

	/**
	 *
	 */
	private Power_HPMPX() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Power_HPMPX();
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
		boolean isError = false;
		// 檢查金幣數量
		final L1ItemInstance adena = pc.getInventory().checkItemX(40308, 7000);
		// 金幣不為空
		if (adena != null) {
			pc.getInventory().removeItem(adena, 7000);// 刪除道具

		} else {
			isError = true;
		}

		// 異常發生
		if (isError) {
			// 189 \f1金幣不足。
			pc.sendPackets(new S_ServerMessage(189));
			return;
		}

		if (L1BuffUtil.stopPotion(pc)) {
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
